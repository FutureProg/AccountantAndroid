package com.nick.accountant;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Nick on 2015-10-29.
 */
public class JournalTransactionManager {

    public static Transaction onDebitAccount(JournalEntry journalEntry, View v){
        Transaction transaction;
        View view = (View)v.getParent().getParent();
        Editable txt = ((EditText)view.findViewById(R.id.add_dialog_account_name)).getText();
        if(txt == null || txt.toString().isEmpty()) return null;
        String accountName = txt.toString();
        txt = ((EditText)view.findViewById(R.id.add_dialog_amount_edittext)).getText();
        if (txt == null) return null;
        double value;
        try {
            value = Double.parseDouble(txt.toString());
        }catch(NumberFormatException exc){
            return null;
        }
        Account account = Bin.getAccount(accountName);
        if(account == null){
            account = new Account(accountName,0,0);
            Bin.accountsDbHelper.createAccount(account);
        }
        transaction = new Transaction(account, Transaction.TransactionType.TYPE_DEBIT,value);
        journalEntry.addTransaction(transaction);
        return transaction;
    }

    public static Transaction modifyDebitAccount(JournalEntry journalEntry, View v, int index){
        Transaction transaction;
        View view = (View)v.getParent().getParent();
        Editable txt = ((EditText)view.findViewById(R.id.add_dialog_account_name)).getText();
        if(txt == null || txt.toString().isEmpty()) return null;
        String accountName = txt.toString();
        txt = ((EditText)view.findViewById(R.id.add_dialog_amount_edittext)).getText();
        if (txt == null) return null;
        double value;
        try {
            value = Double.parseDouble(txt.toString());
        }catch(NumberFormatException exc){
            return null;
        }
        transaction = journalEntry.getTransaction(index);
        if(!transaction.getAccount().getName().equals(accountName)){
            Account account = Bin.getAccount(accountName);
            if(account == null){
                account = new Account(accountName,0,0);
                Bin.accountsDbHelper.createAccount(account);
            }
            transaction.setAccount(account);
        }
        transaction.setType(Transaction.TransactionType.TYPE_DEBIT);
        transaction.setValue(value);
        return transaction;
    }

    public static Transaction modifyCreditAccount(JournalEntry journalEntry, View v, int index){
        Transaction transaction;
        View view = (View)v.getParent().getParent();
        Editable txt = ((EditText)view.findViewById(R.id.add_dialog_account_name)).getText();
        if(txt == null || txt.toString().isEmpty()) return null;
        String accountName = txt.toString();
        txt = ((EditText)view.findViewById(R.id.add_dialog_amount_edittext)).getText();
        if (txt == null) return null;
        double value;
        try {
            value = Double.parseDouble(txt.toString());
        }catch(NumberFormatException exc){
            return null;
        }
        transaction = journalEntry.getTransaction(index);
        if(!transaction.getAccount().getName().equals(accountName)){
            Account account = Bin.getAccount(accountName);
            if(account == null){
                account = new Account(accountName,0,0);
                Bin.accountsDbHelper.createAccount(account);
            }
            transaction.setAccount(account);
        }
        transaction.setType(Transaction.TransactionType.TYPE_CREDIT);
        transaction.setValue(value);
        return transaction;
    }

    public static Transaction onCreditAccount(JournalEntry journalEntry, View v){
        Transaction transaction;
        View view = (View)v.getParent().getParent();
        Editable txt = ((EditText)view.findViewById(R.id.add_dialog_account_name)).getText();
        if(txt == null || txt.toString().isEmpty()) return null;
        String accountName = txt.toString();
        txt = ((EditText)view.findViewById(R.id.add_dialog_amount_edittext)).getText();
        if (txt == null) return null;
        double value;
        try {
            value = Double.parseDouble(txt.toString());
        }catch(NumberFormatException exc){
            return null;
        }
        Account account = Bin.getAccount(accountName);
        if(account == null){
            account = new Account(accountName,0,0);
            Bin.accountsDbHelper.createAccount(account);
        }
        transaction = new Transaction(account, Transaction.TransactionType.TYPE_CREDIT,value);
        journalEntry.addTransaction(transaction);
        return transaction;
    }

}
