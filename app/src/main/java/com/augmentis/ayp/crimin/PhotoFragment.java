package com.augmentis.ayp.crimin;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Waraporn on 8/4/2016.
 */
public class PhotoFragment extends DialogFragment implements DialogInterface.OnClickListener {

    protected static final String EXTRA_PHOTO = "PhotoFragment.EXTRA_PHOTO";


    public static PhotoFragment newInstance(File path){
        PhotoFragment pf = new PhotoFragment();
        Bundle args = new Bundle();
        args.putSerializable("ARG_PHOTO",  path);
        pf.setArguments(args);
        return pf;
    }

    ImageView _imageView;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        File imageView = (File) getArguments().getSerializable("ARG_PHOTO");

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        _imageView = (ImageView) v.findViewById(R.id.photo);

        Bitmap bitmap = PictureUtils.getScaleBitmap(imageView.getPath(), getActivity());
        _imageView.setImageBitmap(bitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //builder.setView(new DatePicker(getActivity()));
        builder.setView(v);
        builder.setTitle(R.string.date_picker_title);
        builder.setPositiveButton(android.R.string.ok, this);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
