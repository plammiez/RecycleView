package com.augmentis.ayp.crimin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.augmentis.ayp.crimin.model.CrimeCursorWrapper;
import com.augmentis.ayp.crimin.model.CrimesBaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.augmentis.ayp.crimin.model.CrimeDbSchema.CrimeTable;

public class CrimeLab {

    private static final String TAG = "CrimeLab";
    /////////////////////////STATIC ZONE/////////////////////////////////
    private static CrimeLab instance;


    public static CrimeLab getInstance(Context context){

        if(instance == null){
            instance = new CrimeLab(context);
        }
        return instance;
    }

    public static ContentValues getContentValues(Crime crime){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeTable.Cols.DATE, crime.getCrimeDate().getTime());
        contentValues.put(CrimeTable.Cols.SOLVED, (crime.isSolved()) ? 1 : 0);
        contentValues.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        return contentValues;
    }
////////////////////////////////////////////////////////////////////

    private Context context;
    private SQLiteDatabase database;

    private CrimeLab(Context context){
        this.context = context;
        CrimesBaseHelper crimesBaseHelper = new CrimesBaseHelper(context);
        database = crimesBaseHelper.getWritableDatabase();

    }

    public  Crime getCrimeByID(UUID uuid){
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID
                + " = ? ", new String[] { uuid.toString()});

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }

    public CrimeCursorWrapper queryCrimes(String whereCause, String[] whereArgs){
        Cursor cursor = database.query(CrimeTable.NAME,
                null,
                whereCause,
                whereArgs,
                null,
                null,
                null);

        return new CrimeCursorWrapper(cursor);
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursorWrapper = queryCrimes(null, null);
        try {
            cursorWrapper.moveToFirst();
            while ( !cursorWrapper.isAfterLast()){
                crimes.add(cursorWrapper.getCrime());

                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }

        return crimes;
    }

    public static void main(String [] args){
        CrimeLab crimeLab = CrimeLab.getInstance(null);
        List<Crime> crimeList = crimeLab.getCrimes();
        int size = crimeList.size();
        for(int i = 0; i < size; i++){
            System.out.println(crimeList.get(i));

        }

    }

    public void addCrime(Crime crime) {
        Log.d(TAG, "Add crime " + crime.toString());
        ContentValues contentValues = getContentValues(crime);

        database.insert(CrimeTable.NAME, null, contentValues);
    }

    public void deleteCrime(UUID uuid) {
        database.delete(CrimeTable.NAME, CrimeTable.Cols.UUID
                + " = ? ", new String[] {uuid.toString() });
    }

    public void updateCrime(Crime crime){
        String uuidStr = crime.getId().toString();
        ContentValues contentValues = getContentValues(crime);

        database.update(CrimeTable.NAME, contentValues, CrimeTable.Cols.UUID
                + " = ?", new String[] { uuidStr}); // uuidStr will manage n put in ? position (sql injection)

    }
}
