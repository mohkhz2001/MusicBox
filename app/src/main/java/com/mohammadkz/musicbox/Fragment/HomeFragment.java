package com.mohammadkz.musicbox.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkz.musicbox.Adapter.AllMusicListAdapter;
import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment {

    View view;
    List<Music> musicList;
    RecyclerView list;
    ImageView jump, playAll, sort;
    TextView number, playAllTxt;
    boolean firstTime = true;
    AllMusicListAdapter allMusicListAdapter;
    RecyclerView.SmoothScroller smoothScroller;

    enum Sorting {AZ, date}

    private Sorting sorting = Sorting.AZ;


    public HomeFragment(List<Music> musicList) {
        this.musicList = musicList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews();
        controllerViews();
        number.setText("(" + musicList.size() + ")");
        ((MainActivity) getActivity()).start();
        setAdapter();

        return view;
    }

    private void initViews() {
        list = view.findViewById(R.id.list);
        jump = view.findViewById(R.id.jump);
        sort = view.findViewById(R.id.sort);
        number = view.findViewById(R.id.number);
        playAll = view.findViewById(R.id.playAll);
        playAllTxt = view.findViewById(R.id.playAllTxt);
        smoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
    }

    private void controllerViews() {
        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.getAdapter() != null) {
                    int pos = ((MainActivity) getActivity()).getPosToJump();
//                    smoothScroller.setTargetPosition(pos);
//                    list.getLayoutManager().startSmoothScroll(smoothScroller);
                    list.scrollToPosition(pos);
                    Toast.makeText(getContext(), "Jumped to current music", Toast.LENGTH_SHORT).show();
                    blankItem(pos);
                }
            }
        });

        playAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstTime) {
                    ((MainActivity) getActivity()).setPlayList_toPlay(musicList);
                    firstTime = false;
                }
                ((MainActivity) getActivity()).playAudio(0, getContext());
            }
        });

        playAllTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstTime) {
                    ((MainActivity) getActivity()).setPlayList_toPlay(musicList);
                    firstTime = false;
                }
                ((MainActivity) getActivity()).playAudio(0, getContext());
            }
        });

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstTime = false;
                switch (sorting) {
                    case AZ:
                        sorting = Sorting.date;
                        sortingByDate();
                        break;
                    case date:
                        sorting = Sorting.AZ;
                        sortingByAZ();
                        break;
                }
            }
        });
    }

    private void setAdapter() {

        allMusicListAdapter = new AllMusicListAdapter(getContext(), musicList, getActivity(), true);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setItemViewCacheSize(20);
        list.setDrawingCacheEnabled(true);
        list.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        list.setAdapter(allMusicListAdapter);

        allMusicListAdapter.setOnItemClickListener(new AllMusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                if (firstTime) {
                    ((MainActivity) getActivity()).setPlayList_toPlay(musicList);
                    firstTime = false;
                }
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
//                    ((MainActivity) v.getContext()).setPlayMusic(1, context);
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
        allMusicListAdapter.notifyItemRemoved(pos);
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

    private void blankItem(int pos) {
        try {
            final int[] counter = {0};
            View view = list.getLayoutManager().findViewByPosition(pos);
            TextView musicName = view.findViewById(R.id.musicName);

            if (musicName != null) {
                CountDownTimer countDownTimer = new CountDownTimer(2000, 400) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        if (counter[0] % 2 == 0) {
                            musicName.setTextColor(Color.parseColor("#FF9800"));
                        } else {
                            musicName.setTextColor(Color.parseColor("#FFFFFF"));
                        }
                        counter[0]++;
                    }

                    @Override
                    public void onFinish() {
                        musicName.setTextColor(Color.parseColor("#FFFFFF"));
                    }

                }.start();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void sortingByDate() {
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music music, Music music1) {

                return (int) (music.getDateAdded() - music1.getDateAdded());
            }
        });

        allMusicListAdapter.notifyDataSetChanged();
    }

    private void sortingByAZ() {
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music music, Music music1) {

                return music.getName().compareTo(music1.getName());
            }
        });

        allMusicListAdapter.notifyDataSetChanged();
    }
}