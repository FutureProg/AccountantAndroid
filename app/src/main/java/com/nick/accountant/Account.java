package com.nick.accountant;

import java.io.Serializable;

/**
 * Created by Nick on 2015-10-28.
 */
public class Account implements Serializable, Comparable<Account> {

    public static final int TYPE_CURRENT_ASSET = 0, TYPE_CURRENT_LIABILITY = 1,
            TYPE_LONG_TERM_ASSET = 2, TYPE_LONG_TERM_LIABILITY = 3,
            TYPE_EXPENSE = 4, TYPE_REVEUE = 5;

    private String name;
    private double[] value; // [0] => debit [1] => credit
    int type;
    public long id;

    public Account(String name, double debit, double credit){
        this.name = name.trim();
        this.value = new double[]{debit,credit};
        if(name.toLowerCase().contains("expense")){
            this.type = TYPE_EXPENSE;
        }
        else if(name.toLowerCase().contains("revenue")){
            this.type = TYPE_REVEUE;
        }
        else if(name.toLowerCase().contains("cash")){
            this.type = TYPE_CURRENT_ASSET;
        }
        else if(name.toLowerCase().contains("payable")){
            this.type = TYPE_CURRENT_LIABILITY;
        }else{
            this.type = TYPE_CURRENT_ASSET;
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public double getAmount(){
        return Math.abs(value[0] - value[1]);
    }

    public void debit(double value){
        this.value[0] += value;
    }

    public void credit(double value) {
        this.value[1] += value;
    }

    public double getDebit(){
        return value[0];
    }

    public double getCredit(){
        return value[1];
    }


    @Override
    public String toString(){
        return name;
    }

    @Override
    public int compareTo(Account another) {
        return this.getName().compareTo(another.getName().trim());
    }
}
