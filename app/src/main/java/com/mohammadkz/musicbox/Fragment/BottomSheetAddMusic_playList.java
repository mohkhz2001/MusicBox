package com.mohammadkz.musicbox.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mohammadkz.musicbox.Adapter.AddMusicAdapter;
import com.mohammadkz.musicbox.Adapter.AllMusicListAdapter;
import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.Model.PlayList;
import com.mohammadkz.musicbox.Model.PlayListDA;
import com.mohammadkz.musicbox.R;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class BottomSheetAddMusic_playList extends BottomSheetDialogFragment {

    View view;
    RecyclerView list;
    List<Music> musicList;
    PlayList playLists;
    List<Music> wantList = new ArrayList<>();
    Button done;

    public BottomSheetAddMusic_playList(List<Music> musicList, PlayList playLists) {
        this.musicList = musicList;
        this.playLists = playLists;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.bottom_sheet_add_to_playlist, container, false);

        initViews();
        controllerView();
        setAdapter();

        return view;
    }

    private void initViews() {
        list = view.findViewById(R.id.list);
        done = view.findViewById(R.id.done);
    }

    private void controllerView() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMusic();
            }
        });
    }

    private void setAdapter() {
        AddMusicAdapter addMusicAdapter = new AddMusicAdapter(getContext(), musicList);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(addMusicAdapter);

        addMusicAdapter.setOnItemClickListener(new AddMusicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v, Boolean c) {
                if (c) {
                    wantList.add(musicList.get(pos));
                } else {

                }
            }
        });

    }

    private void addNewMusic() {
        PlayListDA playListDA = new PlayListDA(getActivity());
        playListDA.openDB();

        boolean check = playListDA.newMusic_playList(wantList, playLists.getName());


        if (check) {
            playLists.getMusicList().addAll(wantList);
            super.dismiss();
        } else {
            Toast.makeText(getContext(), "please try again. can't add music to play list ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        PlayListMusicFragment playListMusicFragment = new PlayListMusicFragment(playLists , musicList);
        fragmentTransaction.replace(R.id.frameLayout , playListMusicFragment).commit();
    }

}
