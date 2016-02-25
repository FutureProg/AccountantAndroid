package com.nick.accountant;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nick.accountant.database.AccountsDbHelper;
import com.nick.accountant.database.JournalDbHelper;
import com.nick.accountant.database.TransactionDbHelper;

import java.util.ArrayList;

/**
 * Created by Nick on 2015-10-28.
 */
public class Bin {

    final static int DATA_VERSION = 15;
    //final static String ACCOUNTS = "accounts.dat";
    //final static String JOURNAL = "journal.dat";

    /*public static ArrayList<String> accountNames;
    public static HashSet<Account> accounts;
    public static HashSet<JournalEntry> journalEntries;*/

    public static AccountsDbHelper accountsDbHelper;
    public static TransactionDbHelper transactionDbHelper;
    public static JournalDbHelper journalDbHelper;
    //public static HashMap<String,ArrayList<JournalEntry>> monthlyJournals;

    static String blockCharacterSet = "%|_+";
    public static InputFilter characterFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if(source != null && blockCharacterSet.contains(("" + source))){
                return "";
            }
            return null;
        }
    };

    public static void init(Context context){
        accountsDbHelper = new AccountsDbHelper(context);
        transactionDbHelper = new TransactionDbHelper(context);
        journalDbHelper = new JournalDbHelper(context);
    }

    public static Dialog messageDialog(Context context, String title, String message, View.OnClickListener listener){
        final Dialog re = new Dialog(context);
        re.setContentView(R.layout.message_dialog);
        re.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if(title != null){
            ((TextView)re.findViewById(R.id.message_dialog_title)).setText(title);
        }
        if(message != null){
            ((TextView)re.findViewById(R.id.message_dialog_content)).setText(message);
        }
        if (listener != null){
            ((Button)re.findViewById(R.id.message_dialog_button)).setOnClickListener(listener);
        }else{
            ((Button)re.findViewById(R.id.message_dialog_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    re.dismiss();
                }
            });
        }
        return re;
    }

    /*public static void saveData(Context context) throws IOException {
        //--save the accounts--//
        FileOutputStream fos = context.openFileOutput(ACCOUNTS,Context.MODE_PRIVATE);
        OutputStreamWriter out = new OutputStreamWriter(fos);
        String line = DATA_VERSION + "\n";
        out.write(line); //write the format version
        out.write(accounts.size() + "\n"); //write the number of accounts
        for (Account account : accounts){
            line = account.getName() + "%" + account.getType() +  "%" + account.getDebit() + "%" + account.getCredit();
            for(String key : account.valueByDate.keySet()){
                line += "%" + key + "%" + account.valueByDate.get(key)[0] + "%" +
                        account.valueByDate.get(key)[1];
            }
            Log.v("ACCOUNTANT_WRITE",line);
            line += "\n";
            out.write(line);
        }
        out.flush();
        out.close();

        //--save the journal entries--//
        fos = context.openFileOutput(JOURNAL,Context.MODE_PRIVATE);
        out = new OutputStreamWriter(fos);
        line = DATA_VERSION + "\n";
        out.write(line); //write the format version
        out.write(journalEntries.size() + "\n"); //write the number of entries
        for (JournalEntry entry : journalEntries){
            line = entry.getDate().get(Calendar.MILLISECOND) + "";
            out.write(line + "\n");//save the time in milliseconds
            line = entry.getDate().get(Calendar.YEAR) + "%" +
                    entry.getDate().get(Calendar.MONTH) + "%" +
                    entry.getDate().get(Calendar.DAY_OF_MONTH);
            out.write(line + "\n");//save the date in format (y|m|d)
            out.write(entry.getDescription() + "\n"); //write the description
            out.write(entry.transactions.size() + "\n"); //write the number of transactions
            for(Transaction transaction: entry.transactions){
                out.write(transaction.getAccount().getName() + "%" +
                            transaction.getType().toString() + "%" +
                            transaction.getValue() + "\n");
            }
        }
        out.flush();
        out.close();
    }*/
    /*
    public static void loadData(Context context) throws IOException {
        try{
            //--accounts--//
            accounts = new HashSet<>();
            FileInputStream fis = context.openFileInput(ACCOUNTS);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            String line = in.readLine(); //format version (do stuff later)
            if(line == null||!line.equals(DATA_VERSION+"") ) {
                context.openFileOutput(ACCOUNTS,Context.MODE_PRIVATE).close();
                context.openFileOutput(JOURNAL,Context.MODE_PRIVATE).close();
                return;
            }
            line = in.readLine(); //number of accounts
            int size = Integer.parseInt(line);
            for (int i = 0; i < size;i++){
                line = in.readLine();
                if(line == null)break;
                String[] data = line.split("%");
                Log.v("ACCOUNTANT_LOAD_ACCOUNT", line);
                if(hasAccount(data[0])) continue;
                Account account = new Account(data[0].trim(), Double.parseDouble(data[2]),
                        Double.parseDouble(data[3]));
                account.setType(Integer.parseInt(data[1]));
                for(int j = 4; j < data.length;j++){
                    long time_millis = Long.parseLong(data[j++]);
                    int y = Integer.parseInt(data[j++]);
                    int m = Integer.parseInt(data[j++]);
                    int d = Integer.parseInt(data[j++]);
                    double debit = Double.parseDouble(data[j++]);
                    double credit = Double.parseDouble(data[j]);
                    String key = time_millis + "%" + y + "%" + m + "%" + d;
                    Log.v("ACCOUNTANT_LOAD_DATE",key + ": " +debit + "," + credit);
                    account.valueByDate.put(key, new double[]{debit, credit});
                }
                addAccount(account);
            }
            in.close();

            //--journal--//
            fis = context.openFileInput(JOURNAL);
            in = new BufferedReader(new InputStreamReader(fis));
            line = in.readLine(); //format version
            line = in.readLine(); //number of entries
            size = Integer.parseInt(line);
            journalEntries = new HashSet<>();
            for (int i = 0; i < size;i++){
                String debugOut = "";
                int millis = Integer.parseInt(in.readLine()); //time
                debugOut += millis;
                String[] tempsa = in.readLine().split("%");
                debugOut += tempsa[0] + "%" + tempsa[1] + "%" + tempsa[2] + "%";
                int[] tempia = new int[3];
                for(int j = 0; j < 3; j++) tempia[j] = Integer.parseInt(tempsa[j]);
                Calendar date = Calendar.getInstance();
                date.setTimeInMillis(millis);
                date.set(tempia[0],tempia[1],tempia[2]); //set the date
                String desc = in.readLine(); //get the description
                debugOut += desc;
                Log.v("ACCOUNTANT_LOAD_JRNL",debugOut);
                int sizet = Integer.parseInt(in.readLine()); //trasaction size
                ArrayList<Transaction> transactions = new ArrayList<>();
                for (int j = 0; j < sizet; j++){
                    String[] data = in.readLine().split("%");
                    Account acct = getAccount(data[0].trim());
                    Transaction.TransactionType type = Transaction.TransactionType.TYPE_DEBIT;
                    if(!data[1].equals(type.toString())){
                        type = Transaction.TransactionType.TYPE_CREDIT;
                    }
                    double value = Double.parseDouble(data[2]);
                    Transaction t = new Transaction(acct,type,value);
                    Log.v("ACCOUNTANT_LOAD_JRNL_T",t.toString());
                    transactions.add(t);
                }
                JournalEntry entry = new JournalEntry();
                entry.setDate(date);
                entry.transactions = transactions;
                entry.description = desc;
                addJournalEntry(entry);
            }
            Log.v("ACCOUNTANT_LOAD_JRNL_S",journalEntries.size()+"");
            in.close();
        }catch (FileNotFoundException exc){
            return;
        }
    }*/

    public static void addJournalEntry(JournalEntry journalEntry){
        for(int i = 0; i < journalEntry.getTransactionCount();i++){
            transactionDbHelper.createTransaction(journalEntry.getTransaction(i));
        }
        journalDbHelper.createJournalEntry(journalEntry);
        /*if (journalEntries == null) journalEntries = new HashSet<>();
        //if(monthlyJournals == null) monthlyJournals = new HashMap<>();
        journalEntries.add(journalEntry);
        int month = journalEntry.getDate().get(Calendar.MONTH);
        int year = journalEntry.getDate().get(Calendar.YEAR);
        /*if (monthlyJournals.containsKey(month + ":" + year)) {
              ArrayList<JournalEntry> monthlies = monthlyJournals.get(month + ":" + year);
            monthlies.add(journalEntry);
        }else{
            ArrayList<JournalEntry> monthlies = new ArrayList<>();
            monthlies.add(journalEntry);
            monthlyJournals.put(month + ":" + year,monthlies);
        }*/
        /*Log.v("ACCOUNTANT", "Added journal entry - " + journalEntry.getDescription()
                + " - " + journalEntry.getDate().toString());
        if(journalEntries.size() < 2) return;
        /*int big = 0;
        for(int i = journalEntries.size()-1; i > 0; i--){
            for(int j = 1; j < i; j++){
                if(journalEntries.get(big).getDate().compareTo(journalEntries.get(j).getDate()) < 0){
                    big = j;
                }
            }
            JournalEntry temp = journalEntries.get(journalEntries.size()-1);
            journalEntries.set(journalEntries.size()-1,journalEntries.get(big));
            journalEntries.set(big,temp);
        }*/
    }

    public static ArrayList<JournalEntry> getJournalEntries(int month, int year){
        return journalDbHelper.getJournalEntry(month,year);
        /*if(journalEntries == null) journalEntries = new HashSet<>();
        //if(monthlyJournals == null) monthlyJournals = new HashMap<>();
        /*if (monthlyJournals.containsKey(month + ":" + year)) {
            Log.v("ACCOUNTANT_GET_JRNL_S",monthlyJournals.get(month + ":" + year).size()+"");
            return  monthlyJournals.get(month + ":" + year);
        }else{
            ArrayList<JournalEntry> re = new ArrayList<>();
            Log.v("ACCOUNTANT_GET_JRNL_S","0");
            monthlyJournals.put(month + ":" + year,re);
            return re;
        }*/
        /*ArrayList<JournalEntry> re = new ArrayList<>();
        for (JournalEntry entry : journalEntries){
            if(entry.getDate().get(Calendar.MONTH) == month
                    && entry.getDate().get(Calendar.YEAR) == year){
                re.add(entry);
            }
        }
        //monthlyJournals.put(month + ":" + year,re);
        return re;*/
    }

    public static void addAccount(Account account){
        accountsDbHelper.createAccount(account);
        /*accountsDbHelper.createAccount(account);
        if(accountNames == null) accountNames = new ArrayList<>();
        accountNames.add(account.getName());
        Collections.sort(accountNames);*/
        /*int big = 0;
        for(int i = accounts.size()-1; i > 0; i--){
            for(int j = 1; j < i; j++){
                if(accounts.get(big).getName().compareTo(accounts.get(j).getName()) < 0){
                    big = j;
                }
            }
            Account temp = accounts.get(accounts.size()-1);
            accounts.set(accounts.size()-1,accounts.get(big));
            accounts.set(big,temp);
        }*/
    }

    public static void saveAccountChanges(Account account){
        accountsDbHelper.saveChanges(account);
    }

    public static Account getAccount(int index){
        return accountsDbHelper.getAccount(index);
    }

    public static Account getAccount(String name){
        return accountsDbHelper.getAccount(name);
    }

    private static boolean hasAccount(String name){
        if(AccountsDbHelper.accountNames == null) {
            AccountsDbHelper.accountNames = new ArrayList<>();
            return false;
        }
        return AccountsDbHelper.accountNames.contains(name);
    }

}
