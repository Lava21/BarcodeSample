package com.mas.samplescanbarcode.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mas.samplescanbarcode.model.Code;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class PracticeDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "scanqrbarcode.db";
    private static final int DATABASE_VERSION = 1;

    public PracticeDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static {
        cupboard().register(Code.class);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
