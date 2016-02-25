package com.nick.accountant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.nick.accountant.database.AccountsDbHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemClickListener{

    ViewFlipper viewFlipper;
    JournalEntry newEntry;
    int month = 0, year = 0;
    JournalViewArrayAdapter journalAdapter;
    public static JournalEntryArrayAdapter entryAdapter;
    ArrayAdapter<String> accountsAdapter;
    ArrayList<Transaction> currentTransactions;
    ArrayList<JournalEntry> currentJournalEntries;

    static String STATE_VIEW_SWITCH = "MainActivity_STATE_VIEW_SWITCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bin.init(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        viewFlipper = (ViewFlipper)findViewById(R.id.app_layout_flipper);
        newEntry = new JournalEntry();
        currentJournalEntries = new ArrayList<>();
        currentJournalEntries = (ArrayList<JournalEntry>)Bin.getJournalEntries(month,year).clone();
        currentTransactions = new ArrayList<>();
        setupEntryView();
        setupJournalView();
        setupAccountsView();
        final String[] months = new String[]{"January","February","March","April","May","June","July",
                "August","September","October","November","December"};
        ((Button) findViewById(R.id.journal_view_month_button)).setText(months[month]);
        ((Button) findViewById(R.id.journal_view_year_button)).setText(year + "");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_VIEW_SWITCH, viewFlipper.getDisplayedChild());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewFlipper.setDisplayedChild(savedInstanceState.getInt(STATE_VIEW_SWITCH));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //------Methods for Add Entry------//
    void setupEntryView(){

        final Button editText = (Button)findViewById(R.id.journal_entry_description_button);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(v.getContext());
                dialog.getWindow().setBackgroundDrawable(getResources().
                        getDrawable(R.drawable.popup_dialog_bg, getTheme()));
                final EditText input = new EditText(MainActivity.this);
                input.setText(editText.getText().toString());
                input.setSingleLine(true);
                input.setHint("Description...");
                LinearLayout layout = new LinearLayout(v.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);
                input.setBackground(getResources().
                        getDrawable(R.drawable.rounded_white_rectangle, getTheme()));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                lp.setMargins(20, 20, 20, 20);
                LinearLayout.LayoutParams templayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                templayout.setMargins(20, 20, 20, 20);
                input.setPadding(20, 20, 20, 20);
                input.setLayoutParams(templayout);

                Button button = new Button(v.getContext());
                button.setBackgroundColor(Color.TRANSPARENT);
                button.setTextColor(Color.WHITE);
                button.setTextSize(15);
                templayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                templayout.setMargins(20, 50, 20, 20);
                button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                button.setLayoutParams(templayout);
                button.setText("Confirm");
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Editable txt = input.getText();
                        if (txt != null && !txt.toString().isEmpty()) {
                            newEntry.setDescription(txt.toString());
                            editText.setText(txt.toString());
                        }
                        dialog.dismiss();
                    }
                });

                layout.setLayoutParams(lp);
                layout.addView(input);
                layout.addView(button);
                dialog.setContentView(layout);

                dialog.getWindow().setLayout(editText.getWidth(), (int) (editText.getHeight() * 3.5));

                dialog.show();
            }
        });
        ListView listView = (ListView)findViewById(R.id.journal_entry_list);
        entryAdapter = new JournalEntryArrayAdapter(this,
                R.layout.new_journal_entry_list_item,currentTransactions);
        entryAdapter.journalEntry = newEntry;
        listView.setAdapter(entryAdapter);
        listView.setOnItemClickListener(entryAdapter);
        listView.setOnItemLongClickListener(entryAdapter);
    }

    public void onSavePressed(View view){
        newEntry.setDate(Calendar.getInstance());
        if(!newEntry.isBalanced()){
            Bin.messageDialog(this,"Unable to save journal entry",
                    "Ensure that you're debits and credits are equal before saving",
                    null).show();
            return;
        }
        if(newEntry.description == null || newEntry.description.isEmpty()){
            Bin.messageDialog(this,"Unable to save journal entry",
                    "The description cannot be blank",
                    null).show();
            return;
        }
        Calendar date = newEntry.getDate();
        for (Transaction transaction : newEntry.transactions){
            Account account = transaction.getAccount();
            if (transaction.type == Transaction.TransactionType.TYPE_DEBIT){
                account.debit(transaction.getValue());
            }else{
                account.credit(transaction.getValue());
            }
            transaction.accountValue = new double[]{account.getDebit(), account.getCredit()};
            Bin.saveAccountChanges(account);
        }
        Bin.addJournalEntry(newEntry);
        if(newEntry.getDate().get(Calendar.MONTH) == month
                && newEntry.getDate().get(Calendar.YEAR) == year){
            currentJournalEntries.add(newEntry);
            journalAdapter.notifyDataSetChanged();
        }
        newEntry = new JournalEntry();
        ((Button)findViewById(R.id.journal_entry_description_button)).setText("");
        journalAdapter.notifyDataSetChanged();
        currentTransactions.clear();
        entryAdapter.journalEntry = newEntry;
        entryAdapter.notifyDataSetChanged();
        Bin.accountsDbHelper.updateAccountNames();
        accountsAdapter.notifyDataSetChanged();
    }

    public void onAddPressed(View view){
        final Dialog dialog = new Dialog(view.getContext());
        dialog.setContentView(R.layout.add_transaction_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ((Button)dialog.findViewById(R.id.add_dialog_debit_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction t = JournalTransactionManager.onDebitAccount(newEntry, v);
                currentTransactions.add(t);
                Collections.sort(currentTransactions);
                ListView listView = (ListView) findViewById(R.id.journal_entry_list);
                ((JournalEntryArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                journalAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        ((Button)dialog.findViewById(R.id.add_dialog_credit_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Transaction t = JournalTransactionManager.onCreditAccount(newEntry, v);
                currentTransactions.add(t);
                Collections.sort(currentTransactions);
                ListView listView = (ListView) findViewById(R.id.journal_entry_list);
                ((JournalEntryArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
                journalAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        AutoCompleteTextView editText = (AutoCompleteTextView)dialog.
                findViewById(R.id.add_dialog_account_name);
        editText.setFilters(new InputFilter[]{Bin.characterFilter});
        String[] accounts = new String[AccountsDbHelper.accountNames.size()];
        for (int i = 0;i < accounts.length;i++){
            accounts[i] = AccountsDbHelper.accountNames.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,accounts);
        editText.setAdapter(adapter);
        dialog.show();
    }

    //------Methods for View Journal---//
    void setupJournalView(){
        ListView listView = (ListView)findViewById(R.id.journal_view_list);
        journalAdapter = new JournalViewArrayAdapter(this,
                R.layout.journal_view_list_item,currentJournalEntries);
        listView.setAdapter(journalAdapter);
        listView.setOnItemClickListener(journalAdapter);
    }

    public void onChangeMonth(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Month");
        final String[] months = new String[]{"January","February","March","April","May","June","July",
                                    "August","September","October","November","December"};
        builder.setItems(months, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMonth(which);
            }
        });
        builder.show();
    }

    public void setMonth(int month){
        final String[] months = new String[]{"January","February","March","April","May","June","July",
                "August","September","October","November","December"};
        ((Button) findViewById(R.id.journal_view_month_button)).setText(months[month]);
        this.month = month;
        journalAdapter.month = month;
        changeDate();
        journalAdapter.notifyDataSetChanged();

    }

    public void onChangeYear(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Year");
        final String[] years = new String[30];
        for (int i = 0; i < 30;i++) {
            years[i] = "" + (2000 + i);
        }
        builder.setItems(years, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setYear(Integer.parseInt(years[which]));
            }
        });

        builder.show();
    }

    public void setYear(int year){
        ((Button) findViewById(R.id.journal_view_year_button)).setText(year + "");
        journalAdapter.year = year;
        changeDate();
        journalAdapter.notifyDataSetChanged();
    }

    void changeDate(){
        currentJournalEntries.clear();
        for (JournalEntry entry : Bin.getJournalEntries(month,year)){
            currentJournalEntries.add(entry);
        }
        journalAdapter.notifyDataSetChanged();
    }

    //-------Methods for Accounts View---//
    void setupAccountsView(){
        ListView listView = (ListView)findViewById(R.id.content_account_view_list);
        if(AccountsDbHelper.accountNames == null) AccountsDbHelper.accountNames = new ArrayList<>();
        accountsAdapter = new ArrayAdapter<String>((Context)this,
                android.R.layout.simple_list_item_1,AccountsDbHelper.accountNames);
        listView.setAdapter(accountsAdapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_entry) {
            viewFlipper.setDisplayedChild(0);
            journalAdapter.notifyDataSetChanged();
        } else if (id == R.id.nav_journal) {
            viewFlipper.setDisplayedChild(1);
        } else if (id == R.id.nav_accounts) {
            viewFlipper.setDisplayedChild(2);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.content_account_view_list){
            Intent intent = new Intent((Context)this,AccountDetailActivity.class);
            intent.putExtra(AccountDetailActivity.INTENT_ACCOUNT_NAME,
                    AccountsDbHelper.accountNames.get(position));
            startActivity(intent);
        }
    }
}
