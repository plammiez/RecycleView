package com.augmentis.ayp.crimin;

import android.support.v4.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment onCreateFragment() {
        return new CrimeListFragment();
    }
}
