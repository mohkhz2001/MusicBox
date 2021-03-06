package com.mohammadkz.musicbox.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.LikeDA;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllMusicListAdapter extends RecyclerView.Adapter<AllMusicListAdapter.viewHolder> {

    private Context context;
    List<Music> musicList;
    public OnItemClickListener onItemClickListener;
    public OnClickListener onPopupMenuClickListener;
    Activity activity;
    ImageLoaderConfiguration config;
    boolean showOther;

    public AllMusicListAdapter(Context context, List<Music> musicList, Activity activity, boolean showOther) {
        this.context = context;
        this.musicList = musicList;
        this.activity = activity;
        this.showOther = showOther;
        config = new ImageLoaderConfiguration.Builder(context).build();
    }

    @NonNull
    @Override
    public AllMusicListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.all_music_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllMusicListAdapter.viewHolder holder, int position) {

        Music music = musicList.get(position);

        holder.musicName.setText(music.getName());
        holder.artistName.setText(music.getArtist());

        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(musicList.get(position).getPath());
            byte[] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);

            loadImage(holder.artistImage, bitmap);

        } catch (Exception e) {
            e.getMessage();
            holder.artistImage.setImageResource(R.drawable.audio_img_white);
        }

        if (musicList.get(position).isLiked()) {
            holder.like.setImageResource(R.drawable.heart);
        } else {
            holder.like.setImageResource(R.drawable.like);
        }


    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void loadImage(ImageView iv, Bitmap url) {
        Glide.with(iv.getContext()).load(url).thumbnail(0.3f).into(iv);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        CircleImageView artistImage; //
        TextView musicName, artistName;
        Button popupMenu;
        ImageView like;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            artistImage = itemView.findViewById(R.id.artistImage);
            musicName = itemView.findViewById(R.id.musicName);
            artistName = itemView.findViewById(R.id.artistName);
            popupMenu = itemView.findViewById(R.id.popupMenu);
            like = itemView.findViewById(R.id.like);

            if (!showOther) {
                like.setVisibility(View.GONE);
                popupMenu.setVisibility(View.GONE);
            } else {
                like.setVisibility(View.VISIBLE);
                popupMenu.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition(), v);/////
                }
            });

            popupMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPopupMenuClickListener.onClick(getAdapterPosition(), v);
                }
            });

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Music music = musicList.get(getAdapterPosition());
                    music.setLiked(!music.isLiked());
                    LikeDA likeDA = new LikeDA(activity);
                    likeDA.openDB();
                    if (music.isLiked()) {
                        boolean result = likeDA.newMusicLiked(music);
                        Log.e("add liekd", "" + result);
                        ((MainActivity) activity).LikeChanged(getAdapterPosition(), true);
                    } else {
                        boolean result = likeDA.removeMusicLiked(music.getPath().toString());
                        Log.e("remove liekd", "" + result);
                        ((MainActivity) activity).LikeChanged(getAdapterPosition(), false);
                    }

                    notifyItemChanged(getAdapterPosition());
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

    public void setOnPopupMenuClickListener(OnClickListener onPopupMenuClickListener) {
        this.onPopupMenuClickListener = onPopupMenuClickListener;
    }

    public interface OnClickListener {
        void onClick(int pos, View v);
    }
}
