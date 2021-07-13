package com.mohammadkz.musicbox.Adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkz.musicbox.Model.PlayList;
import com.mohammadkz.musicbox.R;

import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {

    Context context;
    List<PlayList> playList;
    public OnItemClickListener onItemClickListener;

    public PlayListAdapter(Context context, List<PlayList> playList) {
        this.context = context;
        this.playList = playList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_play_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayListAdapter.ViewHolder holder, int position) {
        holder.name.setText(playList.get(position).getName());
        if (playList.get(position).getMusicList().size() > 0)
            holder.numbers.setText(playList.get(position).getMusicList().size() + " songs");
        else
            holder.numbers.setText("0 songs");

        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(playList.get(position).getMusicList().get(0).getPath().getPath());
            byte[] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);
            holder.img.setImageBitmap(bitmap);
        } catch (Exception e) {
            holder.img.setImageResource(R.drawable.audio_img_white);
        }
    }

    @Override
    public int getItemCount() {
        return playList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, numbers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            numbers = itemView.findViewById(R.id.numbers);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, View v);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
