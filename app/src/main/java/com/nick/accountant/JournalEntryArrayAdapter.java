package com.nick.accountant;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nick on 2015-10-29.
 */
public class JournalEntryArrayAdapter extends ArrayAdapter<Transaction> implements
        ListView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    public JournalEntry journalEntry;
    boolean longPress;

    public JournalEntryArrayAdapter(Context context, int resource, List<Transaction> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        JournalEntryHolder holder;
        if(row == null || row.getTag() == null){
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.new_journal_entry_list_item,parent,false);
            holder = new JournalEntryHolder();
            holder.accountName = (TextView)row.findViewById(R.id.journal_entry_list_item_account_textview);
            holder.debitAmount = (TextView)row.findViewById(R.id.journal_entry_list_item_debit_textview);
            holder.creditAmount = (TextView)row.findViewById(R.id.journal_entry_list_item_credit_textview);
            row.setTag(holder);
        }else{
            holder = (JournalEntryHolder)row.getTag();
        }

        Transaction transaction = getItem(position);
        holder.accountName.setText(transaction.getAccount().getName());
        String txt = String.format("$%.02f",transaction.getValue());
        if(transaction.getType() == Transaction.TransactionType.TYPE_DEBIT){
            holder.debitAmount.setText(txt);
            holder.creditAmount.setText("");
        }else{
            holder.creditAmount.setText(txt);
            holder.debitAmount.setText("");
        }
        Log.v("ACCOUNTANT_DEBUG", "Show " + transaction.getAccount() + " on journal entry list");
        return row;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if(longPress){
            longPress = false;
            return;
        }
        Transaction transaction = journalEntry.getTransaction(position);
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.add_transaction_dialog);
        ((EditText)dialog.findViewById(R.id.add_dialog_account_name)).setText(transaction.
                getAccount().getName());
        ((EditText)dialog.findViewById(R.id.add_dialog_amount_edittext)).setText(
                transaction.getValue()+"");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((Button)dialog.findViewById(R.id.add_dialog_debit_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction t = JournalTransactionManager.modifyDebitAccount(journalEntry, v, position);
                MainActivity.entryAdapter.notifyDataSetChanged();
                Log.v("ACCOUNTANT_DEBUG", "modify transaction: " + journalEntry.transactions.size());
                dialog.dismiss();
            }
        });
        ((Button)dialog.findViewById(R.id.add_dialog_credit_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction t = JournalTransactionManager.modifyCreditAccount(journalEntry, v, position);
                MainActivity.entryAdapter.notifyDataSetChanged();
                Log.v("ACCOUNTANT_DEBUG", "modify transaction: " + journalEntry.transactions.size());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        remove(getItem(position));
        notifyDataSetChanged();
        longPress = true;
        /*Animation anim = AnimationUtils.loadAnimation(this.getContext(),R.anim.list_item_slide_up_out);
        anim.setDuration(450);
        final JournalEntryArrayAdapter ref = this;
        final int pos = position;
        Animation anim2 = AnimationUtils.loadAnimation(this.getContext(),R.anim.list_item_slide_up);
        anim2.setDuration(450);
        anim2.setFillEnabled(true);
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ref.notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        for(int i = position + 1; i < getCount();i++){
            parent.getChildAt(i).startAnimation(anim2);
        }
        parent.getChildAt(position).startAnimation(anim);*/
        return true;
    }


    class JournalEntryHolder{
        public TextView accountName;
        public TextView debitAmount;
        public TextView creditAmount;
    }
}
