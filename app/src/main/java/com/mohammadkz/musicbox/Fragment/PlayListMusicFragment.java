package com.mohammadkz.musicbox.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mohammadkz.musicbox.Adapter.AllMusicListAdapter;
import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.Model.PlayList;
import com.mohammadkz.musicbox.Model.PlayListDA;
import com.mohammadkz.musicbox.R;

import java.util.List;

public class PlayListMusicFragment extends Fragment {
    View view;
    PlayList playList;
    FloatingActionButton addMusic;
    List<Music> musicList;
    RecyclerView list;
    ImageView img, delete;
    TextView playListName, numbers;
    BottomSheetAddMusic_playList bottomSheetAddMusic_playList;

    public PlayListMusicFragment(PlayList playList, List<Music> musicList) {
        // Required empty public constructor
        this.playList = playList;
        this.musicList = musicList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_play_list_music, container, false);

        initViews();
        controllerViews();
        setValue();

        return view;
    }

    private void initViews() {
        addMusic = view.findViewById(R.id.newMusic);
        list = view.findViewById(R.id.list);
        img = view.findViewById(R.id.img);
        playListName = view.findViewById(R.id.playListName);
        numbers = view.findViewById(R.id.numbers);
        delete = view.findViewById(R.id.delete);
    }

    private void controllerViews() {
        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMusic();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                    builder.setTitle("remove play list");
                    builder.setMessage("are you sure you want to remove the play list?");

                    // have one btn ==> close
                    builder.setPositiveButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deletePlayList();
                        }
                    });

                    builder.show();
                } catch (Exception e) {
                    e.getMessage();
                }

            }
        });
    }

    private void addMusic() {
        bottomSheetAddMusic_playList = new BottomSheetAddMusic_playList(musicList, playList);
        bottomSheetAddMusic_playList.show(getFragmentManager(), "tag");
    }

    private void setValue() {

        if (playList.getMusicList().size() > 0) {
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(playList.getMusicList().get(0).getPath().getPath());
                byte[] data = mmr.getEmbeddedPicture();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);
                img.setImageBitmap(bitmap);
            }catch (Exception e){
                e.getMessage();
                img.setImageResource(R.drawable.audio_img_white);
            }

        }

        playListName.setText(playList.getName());
        numbers.setText(playList.getMusicList().size() + " songs");

        setAdapter();
    }

    private void setAdapter() {

        AllMusicListAdapter allMusicListAdapter = new AllMusicListAdapter(getContext(), playList.getMusicList(), getActivity());
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(allMusicListAdapter);

        allMusicListAdapter.setOnItemClickListener(new AllMusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {

                ((MainActivity) getActivity()).setPlayList_toPlay(playList.getMusicList());
                ((MainActivity) getActivity()).playAudio(pos, getContext());

            }
        });
    }

    private void deletePlayList() {
        PlayListDA playListDA = new PlayListDA(getActivity());
        playListDA.openDB();
        boolean result = playListDA.deletePlayList(playList);
        if (result) {
            Toast.makeText(getActivity(), "removed", Toast.LENGTH_SHORT).show();

            PlayListFragment playListFragment = new PlayListFragment(musicList);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, playListFragment).commit();

        } else
            Toast.makeText(getActivity(), "cant remoed", Toast.LENGTH_SHORT).show();

    }

}