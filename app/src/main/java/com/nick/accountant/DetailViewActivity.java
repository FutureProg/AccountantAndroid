package com.nick.accountant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DetailViewActivity extends AppCompatActivity {

    public static final String INTENT_JOURNAL_ENTRY = "DetailActivity.JOURNAL_ENTRY";
    public static final int REQUEST_CODE = 0xDE;

    JournalEntry journalEntry;
    ArrayList<String> items;
    JournalEntryArrayAdapter transactionAdapter;
    BalanceSheetArrayAdapter balanceAdapter;
    byte state = 0x0; // 0x0 => transactions, 0x1 => balance sheet

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        journalEntry = (JournalEntry)intent.getSerializableExtra(INTENT_JOURNAL_ENTRY);
        int day = journalEntry.getDate().get(Calendar.DAY_OF_MONTH);
        int year = journalEntry.getDate().get(Calendar.YEAR);
        String month = journalEntry.getDate().getDisplayName(Calendar.MONTH,Calendar.LONG, Locale.getDefault());
        ((TextView)findViewById(R.id.detail_view_description)).setText(journalEntry.getDescription());
        setTitle(month + " " + day + ", " + year);
        createListView();
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void createListView(){
        ListView view = (ListView)findViewById(R.id.detail_view_list);
        items = new ArrayList<>();
        transactionAdapter = new JournalEntryArrayAdapter(this,R.layout.new_journal_entry_list_item,
                journalEntry.transactions);
        balanceAdapter = new BalanceSheetArrayAdapter(this,R.layout.new_journal_entry_list_item,
                journalEntry);
        view.setAdapter(transactionAdapter);
    }

    public void onChangeView(View view){
        Button button = (Button)view;
        ListView listView = (ListView)findViewById(R.id.detail_view_list);
        if(state == 0x0){
            state = 0x1;
            button.setText(R.string.detail_activity_transaction_button);
            listView.setAdapter(balanceAdapter);
        }
        else if(state == 0x1){
            state = 0x0;
            button.setText(R.string.detail_activity_balance_button);
            listView.setAdapter(transactionAdapter);
        }

    }

}
