package com.nick.accountant;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by Nick on 2015-10-28.
 */
@SuppressWarnings("serial")
public class JournalEntry implements Serializable, Comparable<JournalEntry> {

    ArrayList<Transaction> transactions;
    Calendar date;
    String description;
    public long id;

    public JournalEntry(){
        transactions = new ArrayList<Transaction>();
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void addTransaction(Transaction transaction){
        if(transaction == null)return;
        Collections.sort(transactions);
        transactions.add(transaction);
    }

    public Transaction getTransaction(int index){
        return transactions.get(index);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTransaction(Transaction transaction, int index){
        transactions.set(index, transaction);
    }

    public int getTransactionCount(){
        return transactions.size();
    }

    public boolean isBalanced(){
        double debit = 0;
        double credit = 0;
        for(Transaction t : transactions){
            if(t.getType() == Transaction.TransactionType.TYPE_DEBIT){
                debit += t.getValue();
            }else{
                credit += t.getValue();
            }
        }
        return debit == credit;
    }

    public double getDebits(){
        double sum = 0;
        for(Transaction t : transactions){
            if(t.type == Transaction.TransactionType.TYPE_DEBIT){
                sum += t.getValue();;
            }
        }
        return sum;
    }

    public double getCredits(){
        double sum = 0;
        for(Transaction t : transactions){
            if(t.type == Transaction.TransactionType.TYPE_CREDIT){
                sum += t.getValue();
            }
        }
        return  sum;
    }

    @Override
    public int compareTo(JournalEntry another) {
        return this.getDate().compareTo(another.getDate());
    }
}
