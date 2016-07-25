package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Rawin on 18-Jul-16.
 */
public class CrimeListFragment extends Fragment {

    private static final int REQUEST_UPDATED_CRIME = 1;

    RecyclerView _crimeRecycleView;

    private crimeAdapter _adapter;

    protected static final String TAG = "CRIME_LIST";

    private int crimePos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);
        _crimeRecycleView = (RecyclerView) v.findViewById(R.id.crime_recycle_view);
        _crimeRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    /**
     * Update UI
     */
    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.getInstance();
        List<Crime> crimes = crimeLab.getCrimes();

        if (_adapter == null){
            _adapter = new crimeAdapter(crimes);
            _crimeRecycleView.setAdapter(_adapter);
        } else {
           //_adapter.notifyDataSetChanged();
            _adapter.notifyItemChanged(crimePos);
        }

    }

     @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resume List");
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_UPDATED_CRIME){
            if (requestCode == Activity.RESULT_OK) {
                crimePos = (int) data.getExtras().get("position");
                Log.d(TAG, "get CrimePos " + crimePos);
            }
            Log.d(TAG, "Return from crimeFragment");
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _titleTextView;
        public TextView _dateTextView;
        public CheckBox _solvedCheckbox;

        Crime _crime;
        int _position;

        public CrimeHolder(View itemView){
            super(itemView);

            //titleTextView = (TextView) itemView;
            _dateTextView = (TextView) itemView.findViewById(R.id.list_item_crime_date_text_view);
            _solvedCheckbox = (CheckBox) itemView.findViewById(R.id.list_item_crime_solved_check_box);
            _titleTextView = (TextView) itemView.findViewById(R.id.list_item_crime_title_text_view);

            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime, int position) {
            _crime = crime;
            _position = position;
            _titleTextView.setText(_crime.getTitle());
            _dateTextView.setText(_crime.getCrimeDate().toString());
            _solvedCheckbox.setChecked(_crime.isSolved());
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "Send position : " + _position);
            //Toast.makeText(getActivity(), "Press! " + _titleTextView.getText(), Toast.LENGTH_SHORT).show();
            Intent intent = CrimeActivity.newIntent(getActivity(), _crime.getId(), _position);
            //startActivity(intent);
            startActivityForResult(intent, REQUEST_UPDATED_CRIME);
        }
    }

    private class crimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> _crimes;
        private int _viewCreatingCount;

        private crimeAdapter(List<Crime> crimes) {
            this._crimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            _viewCreatingCount++;
            Log.d(TAG, "Create view holder for CrimeList times = " + _viewCreatingCount);

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Log.d(TAG, "Bind view holder for CrimeList : position = " + position);

            Crime crime = _crimes.get(position);
            //holder.titleTextView.setText(crime.getTitle());
            holder.bind(crime, position);
        }

        @Override
        public int getItemCount() {
            return _crimes.size();
        }
    }

}
