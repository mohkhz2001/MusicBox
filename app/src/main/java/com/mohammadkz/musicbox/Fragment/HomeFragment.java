package com.mohammadkz.musicbox.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
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
    BottomSheetDialog bottomSheetDialog;

    enum Sorting {AlphaAZ, AlphaZA, Date_AZ, Date_ZA}

    private Sorting sorting;


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
        readSort();
        changeSorting(sorting);
        bottomSheetNav();
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
                bottomSheetDialog.show();
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

    // TODO: sort part
    private void sortingByDateAZ() {
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music music, Music music1) {

                return (int) (music.getDateAdded() - music1.getDateAdded());
            }
        });

        if (allMusicListAdapter != null)
            allMusicListAdapter.notifyDataSetChanged();
    }

    private void sortingByDateZA() {
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music music, Music music1) {

                return (int) (music1.getDateAdded() - music.getDateAdded());
            }
        });

        if (allMusicListAdapter != null)
            allMusicListAdapter.notifyDataSetChanged();
    }

    private void sortingByNameAZ() {
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music music, Music music1) {

                return music.getName().compareTo(music1.getName());
            }
        });

        if (allMusicListAdapter != null)
            allMusicListAdapter.notifyDataSetChanged();
    }

    private void sortingByNameZA() {
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music music, Music music1) {

                return music1.getName().compareTo(music.getName());
            }
        });

        if (allMusicListAdapter != null)
            allMusicListAdapter.notifyDataSetChanged();
    }

    //choose the sorting
    private void bottomSheetNav() {
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetTheme);
        View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_sort_bottom_sheet, (CardView) view.findViewById(R.id.bottomSheetSort));

        CheckedTextView alphaAZ = bottomSheetView.findViewById(R.id.AlphaAZ);
        CheckedTextView alphaZA = bottomSheetView.findViewById(R.id.AlphaZA);
        CheckedTextView date_AZ = bottomSheetView.findViewById(R.id.Date_AZ);
        CheckedTextView date_ZA = bottomSheetView.findViewById(R.id.Date_ZA);

        if (sorting == null)
            sorting = Sorting.AlphaAZ;

        switch (sorting) {
            case AlphaAZ:
                alphaAZ.setChecked(true);
                alphaZA.setChecked(false);
                date_AZ.setChecked(false);
                date_ZA.setChecked(false);
                break;
            case AlphaZA:
                alphaZA.setChecked(true);
                alphaAZ.setChecked(false);
                date_AZ.setChecked(false);
                date_ZA.setChecked(false);
                break;
            case Date_AZ:
                date_AZ.setChecked(true);
                alphaAZ.setChecked(false);
                alphaZA.setChecked(false);
                date_ZA.setChecked(false);
                break;
            case Date_ZA:
                date_ZA.setChecked(true);
                alphaAZ.setChecked(false);
                alphaZA.setChecked(false);
                date_AZ.setChecked(false);
                break;
            default:
                alphaAZ.setChecked(true);
                alphaZA.setChecked(false);
                date_AZ.setChecked(false);
                date_ZA.setChecked(false);
                break;
        }

        alphaAZ.setOnClickListener(v -> {
            if (sorting == Sorting.AlphaZA || sorting == Sorting.Date_AZ || sorting == Sorting.Date_ZA) {
                alphaZA.setChecked(false);
                date_AZ.setChecked(false);
                date_ZA.setChecked(false);
            }
            alphaAZ.setChecked(true);
            changeSorting(Sorting.AlphaAZ);
            bottomSheetDialog.dismiss();
        });

        alphaZA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sorting == Sorting.AlphaAZ || sorting == Sorting.Date_AZ || sorting == Sorting.Date_ZA) {
                    alphaAZ.setChecked(false);
                    date_AZ.setChecked(false);
                    date_ZA.setChecked(false);
                }
                alphaZA.setChecked(true);
                changeSorting(Sorting.AlphaZA);
                bottomSheetDialog.dismiss();
            }
        });

        date_AZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sorting == Sorting.AlphaAZ || sorting == Sorting.AlphaZA || sorting == Sorting.Date_ZA) {
                    alphaAZ.setChecked(false);
                    alphaZA.setChecked(false);
                    date_ZA.setChecked(false);
                }
                date_AZ.setChecked(true);
                changeSorting(Sorting.Date_AZ);
                bottomSheetDialog.dismiss();
            }
        });

        date_ZA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sorting == Sorting.AlphaAZ || sorting == Sorting.AlphaZA || sorting == Sorting.Date_AZ) {
                    alphaAZ.setChecked(false);
                    alphaZA.setChecked(false);
                    date_AZ.setChecked(false);
                }
                date_ZA.setChecked(true);
                changeSorting(Sorting.Date_ZA);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
    }

    private void changeSorting(Sorting sortChoosed) {
        switch (sortChoosed) {
            case AlphaAZ:
                sorting = Sorting.AlphaAZ;
                sortingByNameAZ();
                break;
            case AlphaZA:
                sorting = Sorting.AlphaZA;
                sortingByNameZA();
                break;
            case Date_AZ:
                sorting = Sorting.Date_AZ;
                sortingByDateAZ();
                break;
            case Date_ZA:
                sorting = Sorting.Date_ZA;
                sortingByDateZA();
                break;
        }
        saveSort();
    }

    // use sharedPreferences to save and read
    private void saveSort() {
        SharedPreferences sh = getContext().getSharedPreferences("sort", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        editor.clear();
        editor.putString("sorting", sorting.toString());
        editor.commit();
    }

    private void readSort() {
        try {
            SharedPreferences sh = getContext().getSharedPreferences("sort", MODE_PRIVATE);
            String data = sh.getString("sorting", Sorting.AlphaAZ.toString());
            if (data != null) {
                try {
                    if (data.equals(Sorting.AlphaAZ.toString()))
                        sorting = Sorting.AlphaAZ;
                    else if (data.equals(Sorting.AlphaZA.toString()))
                        sorting = Sorting.AlphaZA;
                    else if (data.equals(Sorting.Date_AZ.toString()))
                        sorting = Sorting.Date_AZ;
                    else if (data.equals(Sorting.Date_ZA.toString()))
                        sorting = Sorting.Date_ZA;

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                sorting = Sorting.AlphaAZ;
            }
        } catch (Exception e) {
            sorting = Sorting.AlphaAZ;
            e.getMessage();
        }
    }
}