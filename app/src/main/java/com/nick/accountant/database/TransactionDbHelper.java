package com.nick.accountant.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.nick.accountant.Account;
import com.nick.accountant.Bin;
import com.nick.accountant.Transaction;

/**
 * Created by Nick on 2015-12-01.
 */
public class TransactionDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Transaction.db";

    private static final String INTEGER_TYPE = "INTEGER";
    private static final String DOUBLE_TYPE = "REAL";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    public TransactionDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void createTransaction(Transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = getContentValues(transaction);
        transaction.id = db.insertOrThrow(Entry.TABLE_NAME,null,values);
    }

    public Transaction getTransaction(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = Entry._ID + " = ?";
        String[] selectionArgs = {id + ""};
        Cursor cursor = db.query(Entry.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if(!cursor.moveToFirst())return null;

        long account_id = cursor.getLong(cursor.getColumnIndex(Entry.COLUMN_NAME_ACCOUNT_ID));
        Account account = Bin.accountsDbHelper.getAccount(account_id);

        int _type = cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_NAME_TRANSACTION_TYPE));
        Transaction.TransactionType type;
        if(_type == Transaction.TransactionType.TYPE_DEBIT.ordinal()){
            type = Transaction.TransactionType.TYPE_DEBIT;
        }else{
            type = Transaction.TransactionType.TYPE_CREDIT;
        }

        double value = cursor.getDouble(cursor.getColumnIndex(Entry.COLUMN_NAME_TRANSACTION_VALUE));
        double[] accountVals = new double[2];
        accountVals[0] = cursor.getDouble(
                cursor.getColumnIndex(Entry.COLUMN_NAME_ACCOUNT_VALUE_DEBIT));
        accountVals[1] = cursor.getDouble(cursor.getColumnIndex(
                Entry.COLUMN_NAME_ACCOUNT_VALUE_CREDIT));
        Transaction re = new Transaction(account,type,value);
        re.accountValue = accountVals;
        re.id = cursor.getLong(cursor.getColumnIndex(Entry._ID));
        return re;
    }

    public ContentValues getContentValues(Transaction transaction){
        ContentValues values = new ContentValues();
        values.put(Entry.COLUMN_NAME_ACCOUNT_ID,transaction.getAccount().id);
        values.put(Entry.COLUMN_NAME_TRANSACTION_TYPE,transaction.getType().ordinal());
        values.put(Entry.COLUMN_NAME_TRANSACTION_VALUE,transaction.getValue());
        values.put(Entry.COLUMN_NAME_ACCOUNT_VALUE_DEBIT,transaction.accountValue[0]);
        values.put(Entry.COLUMN_NAME_ACCOUNT_VALUE_CREDIT,transaction.accountValue[1]);

        return values;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createEntries = "CREATE TABLE " + Entry.TABLE_NAME + " ("+
                Entry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                Entry.COLUMN_NAME_ACCOUNT_ID + " " + INTEGER_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_TRANSACTION_TYPE + " " + INTEGER_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_TRANSACTION_VALUE + " " + DOUBLE_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_ACCOUNT_VALUE_DEBIT + " " + DOUBLE_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_ACCOUNT_VALUE_CREDIT + " " + DOUBLE_TYPE + ");";
        db.execSQL(createEntries);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static abstract  class Entry implements BaseColumns {
        public static final String TABLE_NAME = "Transactions",
                COLUMN_NAME_ACCOUNT_ID = "account_id",
                COLUMN_NAME_TRANSACTION_TYPE = "type",
                COLUMN_NAME_TRANSACTION_VALUE = "value",
                COLUMN_NAME_ACCOUNT_VALUE_DEBIT = "account_value_debit",
                COLUMN_NAME_ACCOUNT_VALUE_CREDIT = "account_value_credit";
    }
}
