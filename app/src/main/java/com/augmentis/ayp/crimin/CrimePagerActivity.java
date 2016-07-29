package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private ViewPager _viewPager;
    private List<Crime> _crimes;
//    private int _position;
    private UUID _crimeId;

    private List<Integer> positionChanged = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        _crimeId = (UUID) getIntent().getSerializableExtra(CRIME_ID);
//        _position = (int) getIntent().getExtras().get(CRIME_POSITION);

        _viewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view);

        _crimes = CrimeLab.getInstance(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();

        _viewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = _crimes.get(position);
                Fragment f = CrimeFragment.newInstance(crime.getId(), position);
                return f;
            }

            @Override
            public int getCount() {
                return _crimes.size();
            }
        });

        //setPosition
        int position = CrimeLab.getInstance(this).getCrimePositionById(_crimeId);
        _viewPager.setCurrentItem(position);
    }

//    protected void addPageUpdate(int position){
//
//        if (positionChanged.contains(position)){
//            return;
//        }
//        positionChanged.add(position);
//
//        Intent intent = new Intent();
//        Integer[] positions = positionChanged.toArray(new Integer[0]);
//        intent.putExtra("position", positions);
//        Log.d(CrimeListFragment.TAG, "send position back: " + position);
//        setResult(Activity.RESULT_OK, intent);
//    }

    protected static final String CRIME_ID = "CrimePagerActivity.crimeId";
//    protected static final String CRIME_POSITION = "CrimePagerActivity.crimePos";

    public static Intent newIntent(Context activity, UUID id){
        Intent intent = new Intent(activity, CrimePagerActivity.class);
        intent.putExtra(CRIME_ID, id);
        return intent;
    }
}
