package com.mohammadkz.musicbox.Adapter;

import android.app.Activity;
import java.io.ByteArrayOutputStream;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadkz.musicbox.MainActivity;
import com.mohammadkz.musicbox.Model.LikeDA;
import com.mohammadkz.musicbox.Model.Music;
import com.mohammadkz.musicbox.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.decode.ImageDecoder;
import com.nostra13.universalimageloader.core.decode.ImageDecodingInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllMusicListAdapter extends RecyclerView.Adapter<AllMusicListAdapter.viewHolder> {

    private Context context;
    List<Music> musicList;
    public OnItemClickListener onItemClickListener;
    public OnClickListener onPopupMenuClickListener;
    Activity activity;
    ImageLoaderConfiguration config;

    public AllMusicListAdapter(Context context, List<Music> musicList, Activity activity) {
        this.context = context;
        this.musicList = musicList;
        this.activity = activity;
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
        holder.musicName.setText(musicList.get(position).getName());
        holder.artistName.setText(musicList.get(position).getArtist());

        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(musicList.get(position).getPath().getPath());
            byte[] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);
            holder.artistImage.setImageBitmap(getResizedBitmap(bitmap, 40));
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            ImageLoader.getInstance().init(config);
//            imageLoader.displayImage(String.valueOf(getImageUri(bitmap)), holder.artistImage);
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
