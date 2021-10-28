package com.mohammadkz.musicbox.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkz.musicbox.Adapter.AllMusicListAdapter;
import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;

import java.io.File;
import java.util.List;

public class LikeFragment extends Fragment {
    View view;
    List<Music> musicList, likedList;
    RecyclerView list;


    public LikeFragment(List<Music> musicList, List<Music> likedList) {
        this.musicList = musicList;
        this.likedList = likedList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_like, container, false);

        initViews();
        controllerViews();
        setAdapter();

        return view;
    }

    private void initViews() {
        list = view.findViewById(R.id.favouritsList);
    }

    private void controllerViews() {

    }

    private void setAdapter() {

        AllMusicListAdapter allMusicListAdapter = new AllMusicListAdapter(getContext(), likedList, getActivity() , true);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(allMusicListAdapter);

        allMusicListAdapter.setOnItemClickListener(new AllMusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                ((MainActivity) getActivity()).setPlayList_toPlay(likedList);
                ((MainActivity) getActivity()).playAudio(pos, getContext());
            }
        });

        allMusicListAdapter.setOnPopupMenuClickListener(new AllMusicListAdapter.OnClickListener() {
            @Override
            public void onClick(int pos, View v) {
                if (v.getId() == R.id.popupMenu) {
                    //Creating the instance of PopupMenu
                    PopupMenu popup = new PopupMenu(getContext(), v.findViewById(R.id.popupMenu));
                    //Inflating the Popup using xml file
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                    //registering popup with OnMenuItemClickListener
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(getContext(), "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
//                         ((MainActivity) v.getContext()).setPlayMusic(1, context);
                            switch (item.getItemId()) {
                                case R.id.delete_media:
                                    boolean check = deleteMedia(pos);
                                    Log.i("delete log", " " + check);
                                    break;

                                case R.id.play_next:
                                    ((MainActivity) v.getContext()).playNext(pos);
                                    break;

                                case R.id.song_info:
                                    SheetBottomMusicInfo sheetBottomMusicInfo = new SheetBottomMusicInfo(musicList.get(pos));
                                    sheetBottomMusicInfo.show(getFragmentManager(), "music");
                                    break;

                                case R.id.shareMusic:
                                    shareAudio(musicList.get(pos).getPath());
                                    break;

                                default:
                                    Toast.makeText(getContext(), "Sorry try again later", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                            return true;
                        }
                    });

                    popup.show(); //showing popup menu

                }
            }
        });

    }

    private boolean deleteMedia(int pos) {

        try {
            File file = new File(musicList.get(pos).getPath().toString());
            boolean deleted = file.delete();
            deleteItem(pos);
            return deleted;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

    public void deleteItem(int pos) {
        ((MainActivity) getActivity()).deleteItem(pos);
        musicList.remove(pos);
        refreshTable();
    }

    private void refreshTable() {
        setAdapter();
    }

    public void shareAudio(String path) {
        try {
            Intent shareMedia = new Intent(Intent.ACTION_SEND);

            shareMedia.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //set application package
            shareMedia.setType("audio/*");
            //set path of media file in ExternalStorage.
            shareMedia.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
            startActivity(Intent.createChooser(shareMedia, "Share audio File"));

            Toast.makeText(getContext(), "Song Shared Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Song Shared Unsuccessfully", Toast.LENGTH_SHORT).show();

        }
    }
}