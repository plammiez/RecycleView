package com.augmentis.ayp.crimin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String CRIME_ID = "CrimeFragment.CRIME_ID";
    private static final String CRIME_POSITION = "CrimeFragment.CRIME_POS";
    private static final String DIALOG_DATE = "CrimeFragment.DIALOG_DATE";
    private static final int REQUEST_DATE = 221;
    private static final int REQUEST_TIME = 1;
    private static final String DIALOG_TIME = "CrimeFragment.DIALOG_TIME";

    private static final String TAG = "CrimeFragment";
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 28197;
    private static final int REQUEST_CONTACT_SUSPECT = 29900;
    private static final int REQUEST_CAPTURE_PHOTO = 29800;

    private Crime crime;
    private File photoFile;

    private EditText editText;
    private Button crimeDateButton;
    private Button crimeTimeButton;
    private CheckBox crimeSolveCheckbox;
    private Button crimeReportButton;
    private Button crimeSuspectButton;
    private Button crimeCallSuspect;
    private ImageView photoView;
    private ImageButton photoButton;

    public CrimeFragment(){}

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    private Callbacks callbacks;

    //Callback
    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
        void onCrimeDelete();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());

        if(getArguments().get(CRIME_ID) != null) {
            UUID crimeId = (UUID) getArguments().getSerializable(CRIME_ID);
            crime = CrimeLab.getInstance(getActivity()).getCrimeByID(crimeId);
            Log.d(CrimeListFragment.TAG, " crime.getId()=" + crime.getId());
        }else {//TODO delete later
            //== null
            Crime crime = new Crime();
            crimeLab.addCrime(crime);
            this.crime = crime;
        }
        photoFile = CrimeLab.getInstance(getActivity()).getPhotoFile(crime);
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
                updateCrime();
//                addThisPositionToResult(position);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        crimeDateButton = (Button) v.findViewById(R.id.crime_date);
        crimeDateButton.setText(getFormattedDate(crime.getCrimeDate()));
        crimeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ///
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialogFragment = DatePickerFragment.newInstance(crime.getCrimeDate());
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
//                FragmentManager fm = getFragmentManager();
//                DatePickerFragment dialogFragment = new DatePickerFragment();
//                Bundle args = new Bundle();
//                args.putSerializable("ARG_DATE", crime.getCrimeDate());
//                dialogFragment.setArguments(args);
                dialogFragment.show(fm, DIALOG_DATE);

            }
        });//date button

        crimeTimeButton = (Button) v.findViewById(R.id.crime_time);
        crimeTimeButton.setText(getFormattedTime(crime.getCrimeDate()));
        crimeTimeButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                TimePickerFragment dialogFragment = TimePickerFragment.newInstance(crime.getCrimeDate());
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialogFragment.show(fm, DIALOG_TIME);
            }
        });//time button

        crimeSolveCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        crimeSolveCheckbox.setChecked(crime.isSolved());
        crimeSolveCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
                updateCrime();

//                if (buttonView.isPressed()) {
//                    crime.setSolved(isChecked);
//                    updateCrime();
//                }
            }
        });

        crimeReportButton = (Button) v.findViewById(R.id.crime_report);
        crimeReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));

                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

        crimeSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        crimeSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT_SUSPECT);
            }
        });

        if (crime.getSuspect() != null){
            crimeSuspectButton.setText(crime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            crimeSuspectButton.setEnabled(false);
        }

        crimeCallSuspect = (Button) v.findViewById(R.id.crime_call_suspect);
        crimeCallSuspect.setEnabled( crime.getSuspect() != null );
        updateCrime();
        crimeCallSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasCallPermission()) {
                    callSuspect();
                }
            }
        });

        photoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        photoView = (ImageView) v.findViewById(R.id.crime_photo);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ///
                FragmentManager fm = getFragmentManager();
                PhotoFragment photoFragment = PhotoFragment.newInstance(photoFile);
                photoFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

                photoFragment.show(fm, DIALOG_DATE);

            }
        });//date button

        /*
         * .getFilesDir() --> result is folder.
         * .openFileInput(String name) --> stream data for open
         * .getExternalCacheDir()
         */

        //call camera intent
        final Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = photoFile != null
                && captureImageIntent.resolveActivity(packageManager) != null;

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(photoFile);
            Log.d(TAG, "File output at " + photoFile.getAbsolutePath());
            captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        //on click --> start activity for camera
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImageIntent, REQUEST_CAPTURE_PHOTO);
            }
        });

        updatePhotoView();
        updateCrime();

        return v;
    }

    private String getFormattedTime(Date datetime) {
        return new SimpleDateFormat("HH:mm").format(datetime);
    }

    private String getFormattedDate(Date date){
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if(result != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setCrimeDate(date);
            crimeDateButton.setText(getFormattedDate(crime.getCrimeDate()));
            updateCrime();
//            addThisPositionToResult(position);
        }
            if (requestCode == REQUEST_CONTACT_SUSPECT){
                if(data != null){
                    Uri contactUri = data.getData();
                    String[] queryFields = new String[] {
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER };

                    Cursor c = getActivity()
                            .getContentResolver()
                            .query(contactUri,
                                    queryFields,
                                    null,
                                    null,
                                    null);
                try {
                    if (c.getCount() == 0) {
                        c.moveToFirst();
                        return;
                    }
                    c.moveToFirst();
                    String suspect = c.getString(0);
                    suspect = suspect + ":" + c.getString(1);

                    crime.setSuspect(suspect);
                    updateCrime();
                    crimeSuspectButton.setText(suspect);
                    crimeCallSuspect.setEnabled(suspect != null);
                }finally {
                    c.close();
                }
            }
        }
        if (requestCode == REQUEST_CAPTURE_PHOTO) {
            updatePhotoView();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.crime_list_del, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_del_crime:

                CrimeLab.getInstance(getActivity()).deleteCrime(crime.getId());
                callbacks.onCrimeDelete();
//                getActivity().finish();
//                updateCrime();

//                CrimeLab.getInstance(getActivity()).deleteCrime(crime);
//                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
//                startActivity(intent);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        CrimeLab.getInstance(getActivity()).updateCrime(crime);// update crime in db
       // updateCrime();
    }

    private void callSuspect() {
        Intent i = new Intent(Intent.ACTION_CALL);
        StringTokenizer tokenizer = new StringTokenizer(crime.getSuspect(), ":");
        String name = tokenizer.nextToken();
        String phone = tokenizer.nextToken();
        Log.d(TAG, "calling " + name + "/" + phone);
        i.setData(Uri.parse("tel:" + phone));

        startActivity(i);
    }

    public void updateCrime(){
        CrimeLab.getInstance(getActivity()).updateCrime(crime);
        callbacks.onCrimeUpdated(crime);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Granted permission
                    callSuspect();

                } else {

                    // Denied permission
                    Toast.makeText(getActivity(),
                            R.string.denied_permission_to_call,
                            Toast.LENGTH_LONG)
                            .show();
                }
                return;
            }
        }
    }

    private boolean hasCallPermission() {

        // Check if permission is not granted
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{
                            Manifest.permission.CALL_PHONE
                    },
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

            return false; // checking -- wait for dialog
        }

        return true; // already has permission
    }
    
    private String getCrimeReport(){
        String solvedString = null;

        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        }else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString= DateFormat.format(dateFormat, crime.getCrimeDate()).toString();

        String suspect = crime.getSuspect();

        if (suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else {
            suspect = getString(R.string.crime_report_with_suspect);
        }
        String report = getString(R.string.crime_report,
                crime.getTitle(), dateString, solvedString, suspect);

        return report;
    }

    private void updatePhotoView(){
        if (photoFile == null || !photoFile.exists()) {
            photoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaleBitmap(photoFile.getPath(), getActivity());
            photoView.setImageBitmap(bitmap);
        }
    }
}
