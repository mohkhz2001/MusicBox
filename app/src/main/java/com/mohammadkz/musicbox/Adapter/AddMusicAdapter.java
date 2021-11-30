package com.mohammadkz.musicbox.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddMusicAdapter extends RecyclerView.Adapter<AddMusicAdapter.ViewHolder> {

    Context context;
    List<Music> musicList;
    public OnItemClickListener onItemClickListener;

    public AddMusicAdapter(Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_add_to_playlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.musicName.setText(music.getName());
        holder.artistName.setText(music.getArtist());
        holder.checkBox.setChecked(false);

        // set img of the pic
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(music.getPath());
            Bitmap bitmap;
            byte[] data = mmr.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);

            loadImage(holder.artistImage, bitmap);

        } catch (Exception e) {
            holder.artistImage.setImageResource(R.drawable.audio_img_white);
        }

    }

    private void loadImage(ImageView iv, Bitmap url) {
        Glide.with(iv.getContext()).load(url).thumbnail(0.3f).into(iv);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView artistImage;
        TextView musicName, artistName;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.artistImage);
            musicName = itemView.findViewById(R.id.musicName);
            artistName = itemView.findViewById(R.id.artistName);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setChecked(false);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v, checkBox.isChecked());
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v, Boolean c);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
