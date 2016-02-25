package com.nick.accountant.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.nick.accountant.Account;

import java.util.ArrayList;

/**
 * Created by Nick on 2015-11-30.
 */
public class AccountsDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Accounts.db";

    private static final String INTEGER_TYPE = "INTEGER";
    private static final String DOUBLE_TYPE = "REAL";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    public static ArrayList<String> accountNames;

    public AccountsDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        accountNames = new ArrayList<>();
        updateAccountNames();
    }

    public void createAccount(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.v("ACCOUNT_CREATED", account.getName());
        account.id = db.insert(Entry.TABLE_NAME,null,getContentValues(account));
        updateAccountNames();
    }

    public void saveChanges(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = Entry._ID + " = ?";
        String[] selectionArgs = {account.id+""};
        ContentValues values = getContentValues(account);
        db.update(Entry.TABLE_NAME,values,selection,selectionArgs);
        updateAccountNames();
    }

    public Account getAccount(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = Entry.COLUMN_NAME_ACCOUNT_NAME + " = ?";
        String[] selectionArgs = {name};
        Cursor cursor = db.query(Entry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if(!cursor.moveToFirst()) return null;
        return getFromCursor(cursor);
    }

    public Account getAccount(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = Entry._ID + " = ?";
        String[] selectionArgs = {id+""};
        Cursor cursor = db.query(Entry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if(!cursor.moveToFirst()) return null;
        return getFromCursor(cursor);
    }

    public Account getFromCursor(Cursor cursor){
        String name = cursor.getString(cursor.getColumnIndex(Entry.COLUMN_NAME_ACCOUNT_NAME));
        double debit = cursor.getDouble(
                cursor.getColumnIndex(Entry.COLUMN_NAME_ACCOUNT_DEBIT));
        double credit = cursor.getDouble(
                cursor.getColumnIndex(Entry.COLUMN_NAME_ACCOUNT_CREDIT));
        Account re = new Account(name,debit,credit);
        int type = cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_NAME_ACCOUNT_TYPE));
        re.setType(type);
        re.id = cursor.getLong(cursor.getColumnIndex(Entry._ID));

        return re;
    }

    public void updateAccountNames(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = {Entry.COLUMN_NAME_ACCOUNT_NAME};
        Cursor cursor = db.query(Entry.TABLE_NAME,cols,null,null,null,null,null);
        if(accountNames == null) accountNames = new ArrayList<>();
        accountNames.clear();
        if(cursor.moveToFirst()){
            accountNames.add(cursor.getString(0));
            while(cursor.moveToNext()) {
                accountNames.add(cursor.getString(0));
            }
        }
    }

    private ContentValues getContentValues(Account account){
        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_NAME_ACCOUNT_NAME,account.getName());
        values.put(Entry.COLUMN_NAME_ACCOUNT_TYPE, account.getType());
        values.put(Entry.COLUMN_NAME_ACCOUNT_DEBIT, account.getDebit());
        values.put(Entry.COLUMN_NAME_ACCOUNT_CREDIT,account.getCredit());
        return values;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        accountNames = new ArrayList<>();
        final String createEntries = "CREATE TABLE " + Entry.TABLE_NAME + " ("+
                Entry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                Entry.COLUMN_NAME_ACCOUNT_NAME + " " + TEXT_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_ACCOUNT_TYPE + " " + TEXT_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_ACCOUNT_DEBIT + " " + DOUBLE_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_ACCOUNT_CREDIT + " " + DOUBLE_TYPE + ");";
        db.execSQL(createEntries);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static abstract  class Entry implements BaseColumns {
        public static final String TABLE_NAME = "Accounts",
                COLUMN_NAME_ACCOUNT_NAME = "name",
                COLUMN_NAME_ACCOUNT_TYPE = "type",
                COLUMN_NAME_ACCOUNT_DEBIT = "debit",
                COLUMN_NAME_ACCOUNT_CREDIT = "credit";
    }
}
