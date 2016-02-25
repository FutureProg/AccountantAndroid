package com.nick.accountant;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Nick on 2015-11-11.
 */
public class BalanceSheetArrayAdapter extends ArrayAdapter<Transaction> {
    JournalEntry entry;

    public BalanceSheetArrayAdapter(Context context, int resource, JournalEntry entry) {
        super(context, resource, entry.transactions);
        this.entry = entry;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ItemHolder holder;
        if (row == null){
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.new_journal_entry_list_item,parent,false);
            holder = new ItemHolder();
            holder.accountName = ((TextView)row.findViewById(R.id.journal_entry_list_item_account_textview));
            holder.debitAmount = (TextView)row.findViewById(R.id.journal_entry_list_item_debit_textview);
            holder.creditAmount = (TextView)row.findViewById(R.id.journal_entry_list_item_credit_textview);
            row.setTag(holder);
        }else{
            holder = (ItemHolder)row.getTag();
        }
        Transaction transaction = entry.getTransaction(position);
        Account account= transaction.getAccount();
        holder.accountName.setText(account.getName());
        double debit = transaction.accountValue[0];
        double credit = transaction.accountValue[1];
        if(debit > credit){
            double value = debit - credit;
            String txt = String.format("$%.02f",value);
            holder.debitAmount.setText(txt);
            holder.creditAmount.setText("");
        }else{
            double value = credit - debit;
            String txt = String.format("$%.02f",value);
            holder.creditAmount.setText(txt);
            holder.debitAmount.setText("");
        }
        return row;
    }

    class ItemHolder{
        public TextView accountName;
        public TextView debitAmount;
        public TextView creditAmount;
    }
}
