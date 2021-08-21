package com.mohammadkz.musicbox.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkz.musicbox.Model.Artist;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArtistTabAdapter extends RecyclerView.Adapter<ArtistTabAdapter.ViewHolder> {

    private Context context;
    private List<Artist> artistsListLeft, artistsListRight;
    public OnItemClickListener onItemClickListener;

    public ArtistTabAdapter(Context context, List<Artist> artistsListLeft, List<Artist> artistsListRight) {
        this.context = context;
        this.artistsListLeft = artistsListLeft;
        this.artistsListRight = artistsListRight;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (artistsListLeft.get(position) != null) {
            holder.artistName1.setText(artistsListLeft.get(position).getArtistName());
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(artistsListLeft.get(position).getArtistMusic().get(0).getPath());
                byte[] data = mmr.getEmbeddedPicture();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);
                holder.artistImage1.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.artistImage1.setImageResource(R.drawable.audio_img_white);
            }
        } else {
            holder.artistName1.setVisibility(View.INVISIBLE);
            holder.artistImage1.setVisibility(View.INVISIBLE);
        }

        if (artistsListRight.get(position) != null) {
            holder.artistName2.setText(artistsListRight.get(position).getArtistName());
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(artistsListRight.get(position).getArtistMusic().get(0).getPath());
                byte[] data = mmr.getEmbeddedPicture();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);
                holder.artistImage2.setImageBitmap(bitmap);
            } catch (Exception e) {
                holder.artistImage2.setImageResource(R.drawable.audio_img_white);
            }
        } else {
            holder.artistName2.setVisibility(View.INVISIBLE);
            holder.artistImage2.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return artistsListLeft.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView artistImage1, artistImage2;
        TextView artistName1, artistName2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage1 = itemView.findViewById(R.id.artistImage1);
            artistName1 = itemView.findViewById(R.id.artistName1);

            artistImage2 = itemView.findViewById(R.id.artistImage2);
            artistName2 = itemView.findViewById(R.id.artistName2);

            artistImage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });

            artistImage2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });


        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }
}
