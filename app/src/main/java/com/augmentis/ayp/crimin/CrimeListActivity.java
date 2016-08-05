package com.augmentis.ayp.crimin;

import android.content.Intent;
import android.support.v4.app.Fragment;

// CrimeListActivity is Callbacks
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{

    @Override
    protected Fragment onCreateFragment() {
        return new CrimeListFragment();
    }
    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            //single pane
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            //two pane
            Fragment newDetailFragment = CrimeFragment.newInstance(crime.getId());
            //replace fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetailFragment)
                    .commit();
        }
    }
    @Override
    public void onCrimeUpdated(Crime crime) {
        //Update list
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        listFragment.updateUI();
    }

    @Override
    public void onCrimeDelete(){
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        CrimeFragment detailFragment = (CrimeFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_fragment_container);

        listFragment.updateUI();

        //clear
        getSupportFragmentManager()
                .beginTransaction()
                .detach(detailFragment)
                .commit();
    }
}
