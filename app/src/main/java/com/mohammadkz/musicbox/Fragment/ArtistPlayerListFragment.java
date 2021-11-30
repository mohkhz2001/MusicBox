package com.mohammadkz.musicbox.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkz.musicbox.Adapter.AllMusicListAdapter;
import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.Artist;
import com.mohammadkz.musicbox.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ArtistPlayerListFragment extends Fragment {

    View view;
    CircleImageView artistImage;
    TextView numbers, artistName;
    RecyclerView list;
    Artist artist;
    Button back;
    List<Artist> artistList;
    boolean firstTime = true;

    public ArtistPlayerListFragment(Artist artist, List<Artist> artistList) {
        // Required empty public constructor
        this.artist = artist;
        this.artistList = artistList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_artist_player_list, container, false);

        initViews();
        controllerViews();
        setValue();

        return view;
    }

    private void initViews() {
        artistImage = view.findViewById(R.id.artistImage);
        artistName = view.findViewById(R.id.artistName);
        numbers = view.findViewById(R.id.numbers);
        list = view.findViewById(R.id.list);
        back = view.findViewById(R.id.backBtn);
    }

    private void setValue() {
        artistName.setText(artist.getArtistName());

        numbers.setText(artist.getArtistMusic().size() + " songs");

        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(artist.getArtistMusic().get(0).getPath());
            byte[] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);
            artistImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.getMessage();
            artistImage.setImageResource(R.drawable.audio_img_white);
        }

        setAdapter();
    }

    private void controllerViews() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistTabFragment artistTabFragment = new ArtistTabFragment(artistList);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frameLayout, artistTabFragment).commit();
            }
        });
    }

    private void setAdapter() {

        AllMusicListAdapter allMusicListAdapter = new AllMusicListAdapter(getContext(), artist.getArtistMusic(), getActivity(), false);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.setAdapter(allMusicListAdapter);

        allMusicListAdapter.setOnItemClickListener(new AllMusicListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos, View v) {
                if (firstTime) {
                    ((MainActivity) getActivity()).setPlayList_toPlay(artist.getArtistMusic());
                    firstTime = false;
                }
                ((MainActivity) getActivity()).playAudio(pos, getContext());

            }
        });

    }

}