package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.util.List;

public class CrimeListFragment extends Fragment {

    private static final int REQUEST_UPDATED_CRIME = 401;
    private static final java.lang.String SUBTITLE_VISIBLE_STATE = "SUBTITLE_VISIBLE";
    private RecyclerView _crimeRecyclerView;
    private TextView empty_view;

    private CrimeAdapter _adapter;

    protected static final String TAG = "CRIME_LIST";
    private Integer[] crimePos;

    private boolean _subtitleVisible;

    //for activity call Callbacks only
    private Callbacks callbacks;

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
        void onOpenSelectFirst();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context; //if activity not impl callbacks fn return error;
        callbacks.onOpenSelectFirst();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        _crimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycle_view); //put recyclerview into view then view find this id
        _crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity())); // set layoutManager into this no matter what they doing
        //but we know it will draw follow LManager what class like LinearLManager

        empty_view = (TextView) v.findViewById(R.id.emtpy_views);

        if(savedInstanceState != null){
            _subtitleVisible = savedInstanceState.getBoolean(SUBTITLE_VISIBLE_STATE);
        }
        updateUI();//call method update UI to drawing fragment
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.crime_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_show_subtitle);

        if(_subtitleVisible){
            menuItem.setTitle(R.string.hide_subtitle);
        }else {
            menuItem.setTitle(R.string.show_subtitle);
        }
    }

    /**
     * Update UI
     */
    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (_adapter == null) {
            _adapter = new CrimeAdapter(this, crimes);
            _crimeRecyclerView.setAdapter(_adapter);
        }else {
            _adapter.setCrimes(crimeLab.getCrimes());
            _adapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

//        setFirstCrimeFragment();
    }

    private void setFirstCrimeFragment() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        if (crimeCount != 0){
            callbacks.onCrimeSelected(crimeLab.getCrimes().get(0));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);//TODO: Add addCrime() to Crime
//                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
//                startActivity(intent);

                //support tablet
                updateUI();
                callbacks.onCrimeSelected(crime);
                return true;// return true is nothing to do after this Law Na

            case R.id.menu_item_show_subtitle:
                _subtitleVisible = !_subtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateSubtitle(){

        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        //String subtitle = getString(R.string.subtitle_format, crimeCount); //%d is digit

        //plurals
        String subtitle = getResources().getQuantityString(R.plurals.Subtitle_format, crimeCount, crimeCount);

        if(!_subtitleVisible){
            subtitle = null;
        }

        AppCompatActivity  appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);

        if(crimeCount != 0){
            empty_view.setVisibility(View.INVISIBLE);
        }else {
            empty_view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SUBTITLE_VISIBLE_STATE, _subtitleVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resume list");
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_UPDATED_CRIME){
            if(resultCode == Activity.RESULT_OK) {
                crimePos = (Integer[]) data.getExtras().get("position");
                Log.d(TAG, "get crimePos = " + crimePos);
            }
            //blah blah
            Log.d(TAG, "Return from CrimeFragment");
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView _titleTextView;
        public TextView _dateTextView;
        public CheckBox _solvedCheckBox;
        public ImageView _photoView;

        Crime _crime;
        int _position;

        public CrimeHolder(View itemView) {
            super(itemView);

            _titleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);
            _solvedCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
            _dateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            _photoView = (ImageView) itemView.findViewById(R.id.list_item_photo);

            itemView.setOnClickListener(this);
        }

        public void bind(final Crime crime , int position) {
            _crime = crime;
            _position = position;
            _titleTextView.setText(_crime.getTitle());
            _dateTextView.setText(_crime.getCrimeDate().toString());
            _solvedCheckBox.setChecked(_crime.isSolved());
            _solvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    _crime.setSolved(isChecked);

                    CrimeLab.getInstance(getActivity()).updateCrime(_crime);
                    callbacks.onCrimeSelected(_crime);

//                    if (buttonView.isPressed()) {
//                        crime.setSolved(isChecked);
//                        CrimeLab.getInstance(getActivity()).updateCrime(_crime);
//                        callbacks.onCrimeSelected(_crime);
//                    }
                }
            });

            File photoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(_crime);
            Bitmap bitmap = PictureUtils.getScaleBitmap(photoFile.getPath(), getActivity());
            _photoView.setImageBitmap(bitmap);

        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "send position : " + _position);
//            Intent intent = CrimePagerActivity.newIntent(getActivity(), _crime.getId());
//            startActivityForResult(intent, REQUEST_UPDATED_CRIME);

            //support tablet
            callbacks.onCrimeSelected(_crime);
        }
    }



    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder>{
        private List<Crime> _crimes;
        private Fragment _f;
        private int _viewCreatingCount;

        public CrimeAdapter(Fragment f, List<Crime> crimes){
            _crimes = crimes;
            _f = f;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "Create view holder for CrimeList: creating view time = " + _viewCreatingCount);

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);

            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Log.d(TAG, "Bind view holder for CrimeList : position = " + position);

            Crime crime = _crimes.get(position);
            holder.bind(crime, position);

        }

        @Override
        public int getItemCount() {
            return _crimes.size();
        }

        public void setCrimes(List<Crime> crimes) {
            _crimes = crimes;
        }
    }
}
