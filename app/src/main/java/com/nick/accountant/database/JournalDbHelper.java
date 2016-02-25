package com.nick.accountant.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.nick.accountant.Bin;
import com.nick.accountant.JournalEntry;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Nick on 2015-12-02.
 */
public class JournalDbHelper extends SQLiteOpenHelper{

    public static int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "Journal.db";

    private static final String INTEGER_TYPE = "INTEGER";
    private static final String DOUBLE_TYPE = "REAL";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    public JournalDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public void createJournalEntry(JournalEntry entry){
        SQLiteDatabase db = this.getWritableDatabase();
        entry.id = db.insert(Entry.TABLE_NAME,null,getContentValues(entry));
    }

    public JournalEntry getJournalEntry(long id){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = Entry._ID + "= ?";
        String[] selectionArgs = {id +""};
        Cursor cursor = db.query(Entry.TABLE_NAME,null,selection,selectionArgs,null,null,null);
        if(!cursor.moveToFirst()) return null;
        return createFromCursor(cursor);
    }

    public ArrayList<JournalEntry> getJournalEntry(int month, int year){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = Entry.COLUMN_NAME_JOURNAL_MONTH + " = ? AND " +
                Entry.COLUMN_NAME_JOURNAL_YEAR + " = ?";
        String[] selectionArgs = {month + "", year + ""};
        Cursor cursor = db.query(Entry.TABLE_NAME, null, selection,selectionArgs,null,null,null);
        ArrayList<JournalEntry> re = new ArrayList<>();
        if(!cursor.moveToFirst()) return re;
        do{
            re.add(createFromCursor(cursor));
        }while(cursor.moveToNext());
        return re;
    }

    JournalEntry createFromCursor(Cursor cursor){
        JournalEntry re = new JournalEntry();
        String[] acntIds = cursor.getString(
                cursor.getColumnIndex(Entry.COLUMN_NAME_JOURNAL_TRANSACTIONS)).split("%");
        for(String str : acntIds){
            re.addTransaction(Bin.transactionDbHelper.getTransaction(Long.parseLong(str)));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_NAME_JOURNAL_MILLIS)));
        cal.set(Calendar.DAY_OF_MONTH,cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_NAME_JOURNAL_DAY)));
        cal.set(Calendar.MONTH,cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_NAME_JOURNAL_MONTH)));
        cal.set(Calendar.YEAR,cursor.getInt(cursor.getColumnIndex(Entry.COLUMN_NAME_JOURNAL_YEAR)));
        re.setDate(cal);
        re.setDescription(cursor.getString(
                cursor.getColumnIndex(Entry.COLUMN_NAME_JOURNAL_DESCRIPTION)));
        re.id = cursor.getLong(cursor.getColumnIndex(Entry._ID));
        return re;
    }

    ContentValues getContentValues(JournalEntry entry){
        ContentValues values = new ContentValues();
        String temp = "";
        for (int i = 0; i < entry.getTransactionCount();i++){
            temp += entry.getTransaction(i).id + "%";
        }
        temp = temp.substring(0,temp.length()-1);
        values.put(Entry.COLUMN_NAME_JOURNAL_TRANSACTIONS,temp);
        Calendar cal = entry.getDate();
        values.put(Entry.COLUMN_NAME_JOURNAL_MILLIS, cal.getTimeInMillis());
        values.put(Entry.COLUMN_NAME_JOURNAL_DAY, cal.get(Calendar.DAY_OF_MONTH));
        values.put(Entry.COLUMN_NAME_JOURNAL_MONTH, cal.get(Calendar.MONTH));
        values.put(Entry.COLUMN_NAME_JOURNAL_YEAR,cal.get(Calendar.YEAR));
        values.put(Entry.COLUMN_NAME_JOURNAL_DESCRIPTION,entry.getDescription());
        return values;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String create_table = "CREATE TABLE " + Entry.TABLE_NAME + " (" +
                Entry._ID + " INTEGER PRIMARY KEY" + COMMA_SEP +
                Entry.COLUMN_NAME_JOURNAL_TRANSACTIONS + " " + TEXT_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_JOURNAL_MILLIS + " " + INTEGER_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_JOURNAL_DAY + " " + INTEGER_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_JOURNAL_MONTH + " " + INTEGER_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_JOURNAL_YEAR + " " + INTEGER_TYPE + COMMA_SEP +
                Entry.COLUMN_NAME_JOURNAL_DESCRIPTION + " " + TEXT_TYPE + ");";
        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    abstract class Entry implements BaseColumns {
        public static final String TABLE_NAME = "Journal_Entries",
                                    COLUMN_NAME_JOURNAL_TRANSACTIONS = "transactions",
                                    COLUMN_NAME_JOURNAL_MILLIS = "millis",
                                    COLUMN_NAME_JOURNAL_DAY = "day",
                                    COLUMN_NAME_JOURNAL_MONTH = "month",
                                    COLUMN_NAME_JOURNAL_YEAR = "year",
                                    COLUMN_NAME_JOURNAL_DESCRIPTION = "description";
    }
}
