package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Rawin on 18-Jul-16.
 */
public class CrimeFragment extends Fragment {

    private static final String CRIME_ID = "CrimeFragment.CRIME_ID";
    private static final String CRIME_POSITION = "CrimeFragment.CRIME_POS";
    private Crime crime;
    private int position;
    private EditText editText;
    private Button crimeDateButton;
    private CheckBox crimeSolvedCheckbox;

    public static CrimeFragment newInstance(UUID crimeID, int position){
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, crimeID);
        args.putInt(CRIME_POSITION, position);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.CRIME_ID);
        UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID);
        position = getArguments().getInt(CRIME_POSITION);
        //crime = new Crime();
        crime = CrimeLab.getInstance().getCrimeById(crimeId);
        Log.d(CrimeListFragment.TAG, "crime.getID() = " + crime.getId());
        Log.d(CrimeListFragment.TAG, "crime.getTitle() = " + crime.getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        editText = (EditText) v.findViewById(R.id.crime_title);
        editText.setText(crime.getTitle());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        crimeDateButton = (Button) v.findViewById(R.id.crime_date);
        crimeDateButton.setText(getFormattedDate(crime.getCrimeDate()));
        crimeDateButton.setEnabled(false);

        crimeSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        crimeSolvedCheckbox.setChecked(crime.isSolved());
        crimeSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
                Log.d(CrimeListFragment.TAG, "Crime:" + crime.toString());
            }
        });

        Intent intent = new Intent();
        intent.putExtra("position", position);
        Log.d(CrimeListFragment.TAG, "send position back" + position);
        getActivity().setResult(Activity.RESULT_OK, intent);
        return v;
    }

    private String getFormattedDate(Date date) {
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }
}
