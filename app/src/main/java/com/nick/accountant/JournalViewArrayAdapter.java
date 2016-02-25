package com.nick.accountant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Nick on 2015-11-04.
 */
public class JournalViewArrayAdapter extends ArrayAdapter<JournalEntry> implements AdapterView.OnItemClickListener {

    public int month, year;

    public JournalViewArrayAdapter(Context context, int resource, List<JournalEntry> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        JournalEntryHolder holder;
        if (row == null || row.getTag() == null){
            LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
            row = inflater.inflate(R.layout.journal_view_list_item,parent,false);
            holder = new JournalEntryHolder();
            holder.description = (TextView)row.findViewById(R.id.journal_view_item_description);
            holder.debit = (TextView)row.findViewById(R.id.journal_view_item_debit);
            holder.credit = (TextView)row.findViewById(R.id.journal_view_item_credit);
            holder.date = (TextView)row.findViewById(R.id.journal_view_item_date);
            row.setTag(holder);
        }else{
            holder = (JournalEntryHolder)row.getTag();
        }
        int d = getItem(position).getDate().get(Calendar.DAY_OF_MONTH);
        String day = String.format("%02d",d);
        holder.date.setText(day + "");
        holder.description.setText(getItem(position).getDescription());
        String txt = String .format("$%.02f",getItem(position).getDebits());
        holder.debit.setText(txt);
        txt = String.format("$%.02f",getItem(position).getCredits());
        holder.credit.setText(txt);
        return row;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this.getContext(),DetailViewActivity.class);
        intent.putExtra(DetailViewActivity.INTENT_JOURNAL_ENTRY,getItem(position));
        ((MainActivity)getContext()).startActivity(intent);
    }


    class JournalEntryHolder{
        public TextView date;
        public TextView description;
        public TextView debit;
        public TextView credit;
    }
}
