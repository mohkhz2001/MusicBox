package com.mohammadkz.musicbox.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.mohammadkz.musicbox.Adapter.AllMusicListAdapter;
import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class SearchTabFragment extends Fragment {
    View view;

    EditText search;
    RecyclerView list;
    List<Music> musicList;
    ArrayList<Music> searched;


    public SearchTabFragment(List<Music> musicList) {
        // Required empty public constructor
        this.musicList = musicList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_tab, container, false);

        initViews();
        controllerViews();

        return view;
    }

    private void initViews() {
        search = view.findViewById(R.id.search);
        list = view.findViewById(R.id.list);
    }

    private void controllerViews() {
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searched = new ArrayList<>();
                for (int i = 0; i < musicList.size(); i++) {
                    if (musicList.get(i).getName().toLowerCase().contains(search.getText().toString().toLowerCase())) {
                        searched.add(musicList.get(i));
                    }
                }
                setAdapter();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setAdapter() {
        AllMusicListAdapter allMusicListAdapter = new AllMusicListAdapter(getContext(), searched, getActivity());
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(allMusicListAdapter);


        allMusicListAdapter.setOnItemClickListener(new AllMusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {

                pos = musicList.indexOf(searched.get(pos));

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

}