package com.augmentis.ayp.crimin.model;

/**
 * Created by Waraporn on 8/2/2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.augmentis.ayp.crimin.model.CrimeDbSchema.CrimeTable;

public class CrimesBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 5;
    private static final String DATABASE_NAME = "crimeBase.db";
    private static final String TAG = "CrimesBaseHelper";

    public CrimesBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create Database");

        db.execSQL("create table " + CrimeTable.NAME
                + "("
                + "_id integer primary key autoincrement, "
                + CrimeTable.Cols.UUID + ","
                + CrimeTable.Cols.TITLE + ","
                + CrimeTable.Cols.DATE + ","
                + CrimeTable.Cols.SOLVED + ","
                + CrimeTable.Cols.SUSPECT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d(TAG, "Running upgrade db...");

        //1. rename table to _oldVersion
        db.execSQL("alter table " + CrimeTable.NAME + " rename to " + CrimeTable.NAME + "_" + oldVersion);
        //2. drop table
        db.execSQL("drop table if exists " + CrimeTable.NAME);
        //3. create new table
        onCreate(db);
//        db.execSQL("create table " + CrimeTable.NAME
//                + "("
//                + "_id integer primary key autoincrement, "
//                + CrimeTable.Cols.UUID + ","
//                + CrimeTable.Cols.TITLE + ","
//                + CrimeTable.Cols.DATE + ","
//                + CrimeTable.Cols.SOLVED + ","
//                + CrimeTable.Cols.SUSPECT + ")"
//        );
        //4.Insert data from temp table
        db.execSQL("insert into " + CrimeTable.NAME
                + " ("
                + CrimeTable.Cols.UUID + ","
                + CrimeTable.Cols.TITLE + ","
                + CrimeTable.Cols.DATE + ","
                + CrimeTable.Cols.SOLVED
                + ") "
                + " select "
                + CrimeTable.Cols.UUID + ","
                + CrimeTable.Cols.TITLE + ","
                + CrimeTable.Cols.DATE + ","
                + CrimeTable.Cols.SOLVED
                + " from " + CrimeTable.NAME + "_" + oldVersion
        );

        Log.d(TAG, "Insert data from temp table already");

        //5.drop temp table
        db.execSQL("drop table if exists " + CrimeTable.NAME + "_" + oldVersion);

    }
}

