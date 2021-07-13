package com.mohammadkz.musicbox.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mohammadkz.musicbox.Adapter.ArtistTabAdapter;
import com.mohammadkz.musicbox.Model.Artist;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;

import java.util.ArrayList;
import java.util.List;

public class ArtistTabFragment extends Fragment {

    View view;
    RecyclerView listLeft;

    List<Artist> artistList;
    ArrayList<Artist> artistsLeft, artistsRight;

    public ArtistTabFragment(List<Artist> artistList) {
        // Required empty public constructor
        this.artistList = artistList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_artist_tab, container, false);

        initViews();
        controllerViews();
        setArtistList();
        setAdapter();

        return view;
    }

    private void initViews() {
        listLeft = view.findViewById(R.id.list_left);

        artistsLeft = new ArrayList<>();
        artistsRight = new ArrayList<>();
    }

    private void controllerViews() {

    }

    private void setArtistList() {

        for (int i = 1; i <= artistList.size(); i++) {

            if (i % 2 == 0) {
                artistsLeft.add(artistList.get(i - 1));
            } else {
                artistsRight.add(artistList.get(i - 1));
            }

        }

    }

    private void setAdapter() {
        ArtistTabAdapter artistTabAdapter = new ArtistTabAdapter(getContext(), artistsLeft, artistsRight);
        listLeft.setHasFixedSize(true);
        listLeft.setLayoutManager(new LinearLayoutManager(view.getContext()));

        listLeft.setAdapter(artistTabAdapter);


    }

}