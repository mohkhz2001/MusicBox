package com.mohammadkz.musicbox.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mohammadkz.musicbox.Adapter.AllMusicListAdapter;
import com.mohammadkz.musicbox.Adapter.PlayListAdapter;
import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.Model.PlayList;
import com.mohammadkz.musicbox.Model.PlayListDA;
import com.mohammadkz.musicbox.R;

import java.util.List;

public class PlayListFragment extends Fragment {

    View view;
    RecyclerView list;
    FloatingActionButton newPlayList;
    List<PlayList> playLists;

    PlayListDA playListDA;
    List<Music> musicList;

    public PlayListFragment(List<Music> musicList) {
        this.musicList = musicList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_play_list, container, false);

        playListDA = new PlayListDA(getActivity());
        playListDA.openDB();

        initViews();
        controllerViews();

        getPlayList();

        return view;
    }

    private void initViews() {
        list = view.findViewById(R.id.list);
        newPlayList = view.findViewById(R.id.newPlayList);
    }

    private void controllerViews() {
        newPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog();
            }
        });
    }

    private void addPlayList(String name) {
        boolean result = playListDA.newPlayList(name);

        if (result) {
            getPlayList();
        } else {

        }

    }

    // get play list name from user
    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("play list name");

// Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), input.getText().toString(), Toast.LENGTH_LONG).show();
                if (!input.getText().toString().equals("playList")) {
                    addPlayList(input.getText().toString());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void getPlayList() {
        playLists = null;
        playLists = playListDA.getPlayList();
        if (playLists != null) {
            setAdapter();
        }
    }

    private void setAdapter() {
        PlayListAdapter playListAdapter = new PlayListAdapter(getContext(), playLists);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(playListAdapter);

        playListAdapter.setOnItemClickListener(new PlayListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                itemClick(playLists.get(pos));
            }
        });
    }

    private void itemClick(PlayList playList) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        PlayListMusicFragment playListMusicFragment = new PlayListMusicFragment(playList, musicList);
        fragmentTransaction.replace(R.id.frameLayout, playListMusicFragment).commit();
    }

    public void refresh(){
        Log.e("test" , "refreshed");
    }

}