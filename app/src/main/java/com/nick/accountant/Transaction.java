package com.nick.accountant;

import java.io.Serializable;

/**
 * Created by Nick on 2015-10-28.
 */
public class Transaction implements Serializable, Comparable<Transaction>{

    public enum TransactionType{
        TYPE_DEBIT,
        TYPE_CREDIT
    };

    Account account;
    double value;
    public double[] accountValue;
    TransactionType type;
    public long id;

    public Transaction(Account account, TransactionType type, double value){
        this.account = account;
        this.type = type;
        this.value = value;
        this.accountValue = new double[2];
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }



    @Override
    public String toString() {
        return account.getName() + "%" + ((type == TransactionType.TYPE_DEBIT)? "Debit" : "Credit")
                + "%" + value;
    }

    @Override
    public int compareTo(Transaction other) {
        if(this.type == TransactionType.TYPE_DEBIT && other.type == TransactionType.TYPE_CREDIT)
            return -1;
        if(this.type == TransactionType.TYPE_CREDIT && other.type == TransactionType.TYPE_DEBIT)
            return 1;
        return 0;
    }
}
