package com.mohammadkz.musicbox.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jackandphantom.blurimage.BlurImage;
import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class SheetBottomFragment extends BottomSheetDialogFragment {

    private Music music;
    View view;

    ImageView blurBg, play, next, previous;
    CircleImageView singer_image;
    TextView artistName, musicName , remaining , past;
    SeekBar seekbar;


    public SheetBottomFragment(Music music) {
        this.music = music;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sheet_bottom, container, false);

        initViews();
        controllerView();
        setItem();

        return view;
    }

    private void initViews() {
        blurBg = view.findViewById(R.id.blurBg);
        singer_image = view.findViewById(R.id.singer_image);
        play = view.findViewById(R.id.play);
        next = view.findViewById(R.id.next);
        previous = view.findViewById(R.id.previous);
        artistName = view.findViewById(R.id.artistName);
        musicName = view.findViewById(R.id.musicName);
        seekbar = view.findViewById(R.id.seekbar);
        past = view.findViewById(R.id.past);
        remaining = view.findViewById(R.id.remaining);
    }

    private void controllerView() {
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity) getActivity()).mediaPlayer.isPlaying()) {
                    play.setImageResource(R.drawable.play);
                    ((MainActivity) getActivity()).mediaPlayer_pause();
                } else {
                    play.setImageResource(R.drawable.pause);
                    ((MainActivity) getActivity()).mediaPlayer_start();
                }
            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int time = seekBar.getProgress();
                ((MainActivity) getActivity()).setMediaPlayerTime(time);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = ((MainActivity) getActivity()).nextMusic();
                music = ((MainActivity) getActivity()).toPlay.get(pos);
                setItem();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = ((MainActivity) getActivity()).previousMusic();
                music = ((MainActivity) getActivity()).toPlay.get(pos);
                setItem();
            }
        });

    }

    private void setItem() {
        setSinger_image(music.getPath());
        musicName.setText(music.getName());
        artistName.setText(music.getArtist());
        setAudioProgress();
        if (((MainActivity) getActivity()).mediaPlayer.isPlaying()) {
            play.setImageResource(R.drawable.pause);
        } else {
            play.setImageResource(R.drawable.play);
        }

        String time = timerConversion(Integer.parseInt(music.getDuration()));
        remaining.setText(time);


    }

    private void setSinger_image(Uri uri) {

        try {
            singer_image.setImageBitmap(null);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(uri.getPath());
            byte[] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            singer_image.setImageBitmap(bitmap);
//            makeBlur(bitmap);
        } catch (Exception e) {
            singer_image.setImageResource(R.drawable.audio_img_white);
//            blurBg.setImageResource(R.drawable.audio_img_white);
        }
    }

    private void makeBlur(Bitmap bitmap) {
        BlurImage.with(getContext()).load(bitmap).intensity(10).Async(true).into(blurBg);
    }

    //set audio progress
    public void setAudioProgress() {

        int total_duration = ((MainActivity) getActivity()).mediaPlayer.getDuration();

        //display the audio duration
//        total.setText(timerConversion((long) total_duration));
//        current.setText(timerConversion((long) current_pos));
        seekbar.setMax((int) total_duration);
        final Handler handler = new Handler();


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
//                    current.setText(timerConversion((long) current_pos));
                    if (((MainActivity) getActivity()).mediaPlayer != null) {
                        seekbar.setProgress((int) ((MainActivity) getActivity()).mediaPlayer.getCurrentPosition());
                        past.setText(timerConversion((int) ((MainActivity) getActivity()).mediaPlayer.getCurrentPosition()));
                    }
                    handler.postDelayed(this, 1000);
                } catch (Exception ed) {
                    ed.getMessage();

                }
            }
        };
        handler.postDelayed(runnable, 1000);


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return dialog;
    }

    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
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