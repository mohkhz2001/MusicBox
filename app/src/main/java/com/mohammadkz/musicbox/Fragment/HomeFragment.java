package com.mohammadkz.musicbox.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.mohammadkz.musicbox.Model.Artist;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;

import java.util.List;

public class HomeFragment extends Fragment {
    View view;
    TabLayout tabLayout;
    TabItem all_tab, artist_tab, search_tab;
    List<Music> musicList;
    List<Artist> artistList;

    public HomeFragment(List<Music> musicList, List<Artist> artistList) {
        this.musicList = musicList;
        this.artistList = artistList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews();
        controllerViews();
        start();

        return view;
    }

    private void initViews() {
        tabLayout = view.findViewById(R.id.tab_parent);
        all_tab = view.findViewById(R.id.all_tab);
        artist_tab = view.findViewById(R.id.artist_tab);
        search_tab = view.findViewById(R.id.search_tab);
    }

    private void controllerViews() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                switch (tab.getPosition()) {
                    case 0:
                        AllMusicTabFragment allMusicTabFragment = new AllMusicTabFragment(musicList);
                        fragmentTransaction.replace(R.id.frameLayout_tab, allMusicTabFragment).commit();
                        break;
                    case 1:
                        ArtistTabFragment artistTabFragment = new ArtistTabFragment(artistList);
                        fragmentTransaction.replace(R.id.frameLayout_tab, artistTabFragment).commit();
                        break;
                    case 2:
                        SearchTabFragment searchTabFragment = new SearchTabFragment(musicList);
                        fragmentTransaction.replace(R.id.frameLayout_tab , searchTabFragment).commit();
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void start() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        AllMusicTabFragment allMusicTabFragment = new AllMusicTabFragment(musicList);
        fragmentTransaction.replace(R.id.frameLayout_tab, allMusicTabFragment).commit();
    }
}