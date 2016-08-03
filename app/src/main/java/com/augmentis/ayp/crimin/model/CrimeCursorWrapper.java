package com.augmentis.ayp.crimin.model;

/**
 * Created by Waraporn on 8/2/2016.
 */
import android.database.Cursor;
import android.database.CursorWrapper;

import com.augmentis.ayp.crimin.Crime;

import com.augmentis.ayp.crimin.model.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){

        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setCrimeDate(new Date(date));
        crime.setSolved( isSolved != 0 );
        crime.setSuspect(suspect);

        return crime;
    }
}
