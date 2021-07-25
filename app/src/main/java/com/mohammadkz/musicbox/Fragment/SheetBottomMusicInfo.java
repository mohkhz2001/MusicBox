package com.mohammadkz.musicbox.Fragment;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.hdodenhof.circleimageview.CircleImageView;

public class SheetBottomMusicInfo extends BottomSheetDialogFragment {

    View view;
    Music music;
    CircleImageView musicImg;
    TextInputEditText musicName , musicArtist , musicDuration , musicAlbum;
    TextInputLayout musicName_layout;
    Button close;


    public SheetBottomMusicInfo(Music music) {
        this.music = music;
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sheet_bottom_music_info, container, false);

        initViews();
        controllerViews();
        setValue();

        return  view;
    }

    private void initViews(){
        musicImg =view.findViewById(R.id.musicImg);
        musicName=view.findViewById(R.id.musicName);
        musicArtist=view.findViewById(R.id.musicArtist);
        musicDuration=view.findViewById(R.id.musicDuration);
        musicAlbum=view.findViewById(R.id.musicAlbum);
        musicName_layout=view.findViewById(R.id.musicName_layout);
        close=view.findViewById(R.id.close);

    }

    private void controllerViews(){
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SheetBottomMusicInfo.super.dismiss();
            }
        });
    }

    private void setValue(){
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(music.getPath());
            byte[] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);
            musicImg.setImageBitmap(bitmap);
        }catch (Exception e){
            e.getMessage();
            musicImg.setImageResource(R.drawable.audio_img_white);
        }


        musicName.setText(music.getName());
        musicArtist.setText(music.getArtist());
        if (music.getAlbum() != null){
            musicAlbum.setText(music.getAlbum());
        }else
            musicAlbum.setVisibility(View.GONE);
        musicDuration.setText(timerConversion(Integer.parseInt(music.getDuration())));
    }

    //set the time to the txt
    private String timerConversion(int duration) {

        Long time = Long.valueOf(duration);
        int mns = (int) ((time / 60000) % 60000);
        int scs = (int) (time % 60000 / 1000);
        Log.e("TIME", " " + mns + ":" + scs);

        if (scs <10){
            return mns+":0" + scs;
        }else
            return mns+":" + scs;

    }

}
