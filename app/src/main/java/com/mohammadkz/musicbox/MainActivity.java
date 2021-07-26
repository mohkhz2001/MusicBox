package com.mohammadkz.musicbox;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.jackandphantom.blurimage.BlurImage;
//import com.mohammadkz.musicbox.Fragment.HomeFragment;
//import com.mohammadkz.musicbox.Fragment.LikeFragment;
import com.mohammadkz.musicbox.Fragment.HomeFragment;
import com.mohammadkz.musicbox.Fragment.LikeFragment;
import com.mohammadkz.musicbox.Fragment.PlayListFragment;
import com.mohammadkz.musicbox.Fragment.SheetBottomFragment;
import com.mohammadkz.musicbox.Model.ActionPlaying;
import com.mohammadkz.musicbox.Model.Artist;
import com.mohammadkz.musicbox.Model.LikeDA;
import com.mohammadkz.musicbox.Model.Music;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;
import static android.content.Context.MODE_PRIVATE;
import static com.mohammadkz.musicbox.ApplicationClass.ACTION_CLOSE;
import static com.mohammadkz.musicbox.ApplicationClass.ACTION_NEXT;
import static com.mohammadkz.musicbox.ApplicationClass.ACTION_PLAY;
import static com.mohammadkz.musicbox.ApplicationClass.ACTION_PREV;
import static com.mohammadkz.musicbox.ApplicationClass.CHANNEL_ID_2;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity implements ActionPlaying, ServiceConnection {

    ImageView background, play_pause;
    CircleImageView singer_image;
    public List<Music> musicList, favouriteList, toPlay;
    List<Artist> artistList;
    TextView musicName, artistName;
    BottomNavigationView bottom_nav;
    public MediaPlayer mediaPlayer;
    AudioManager audioManager;
    // Audio attributes instance to set the playback
    // attributes for the media player instance
    // these attributes specify what type of media is
    // to be played and used to callback the audioFocusChangeListener
    AudioAttributes playbackAttributes;

    MediaSessionCompat mediaSession;
    MusicService musicService;
    NotificationManager notificationManager;
    Boolean played = false;
    int posToJump;

    int playNext = -1;
    int musicPos; // position of the music that play

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        mediaSession = new MediaSessionCompat(this, "playerAudio");

        Intent intent = new Intent(this, MusicService.class);
        boolean check = bindService(intent, this, BIND_AUTO_CREATE);
        Log.e("bindService", " " + check);

        initViews();
        controller();

        permission();
        if (checkPermission()) {
            getAudioFiles();
            startFragment();
            sharedPreferences_read();
        }

    }

    private void initViews() {
        background = findViewById(R.id.background);
        singer_image = findViewById(R.id.singer_image);
        musicName = findViewById(R.id.musicName);
        artistName = findViewById(R.id.artistName);
        bottom_nav = findViewById(R.id.bottom_nav);
        play_pause = findViewById(R.id.play_pause);
        mediaPlayer = new MediaPlayer();
        toPlay = new ArrayList<>();

    }

    private void controller() {

        bottom_nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.home:
                        HomeFragment homeFragment = new HomeFragment(musicList, artistList);
                        fragmentTransaction.replace(R.id.frameLayout, homeFragment);
                        fragmentTransaction.commit();
                        break;
                    case R.id.like:
                        LikeFragment likeFragment = new LikeFragment((ArrayList<Music>) musicList, setLikedMusic());
                        fragmentTransaction.replace(R.id.frameLayout, likeFragment);
                        fragmentTransaction.commit();
                        break;

                    case R.id.playList:
                        PlayListFragment playListFragment = new PlayListFragment(musicList);
                        fragmentTransaction.replace(R.id.frameLayout, playListFragment);
                        fragmentTransaction.commit();
                        break;

                }
                return true;
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer_pause();
                        } else {
                            mediaPlayer_start();
                        }
                    } else {
                        playAudio(musicPos, getApplicationContext());
                    }

                } catch (Exception e) {
                    e.getMessage();

                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                musicEnded();
                if (playNext != -1) {
                    playAudio(playNext, getApplicationContext());
                    playNext = -1;
                } else {
                    if (toPlay.size() - 1 != musicPos) {
                        musicPos++;
                    } else {
                        musicPos = 0;
                    }
                    playAudio(musicPos, getApplicationContext());
                }

            }
        });

        singer_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SheetBottomFragment sheetBottomFragment = new SheetBottomFragment(toPlay.get(musicPos));
                sheetBottomFragment.show(getSupportFragmentManager(), "tag");

            }
        });

    }

    private List<Music> setLikedMusic() {
        List<Music> liked = new ArrayList<>();
        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).isLiked()) {
                liked.add(musicList.get(i));
            }
        }

        return liked;
    }

    public boolean checkPermission() {
        boolean per1 = true, per2 = true;
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            per1 = false;
        }

        int WRITE_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if ((WRITE_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ);
            per2 = false;
        }

        if (!per1 || !per2)
            return false;
        else
            return true;
    }
//
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSION_READ: {
//                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
//                    } else {
//
//                    }
//                }
//
//            }
//            case PERMISSION_WRITE: {
//                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
//                    } else {
//
//                    }
//                }
//
//            }
//            break;
//
//        }
//    }

    //fetch the audio files from storage
    public void getAudioFiles() {
        permission();

        ContentResolver contentResolver = this.getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        musicList = new ArrayList<>();
        artistList = new ArrayList<>();

        //looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

                Music modelAudio = new Music(url, title, null, artist, duration);

                setArtistList(modelAudio);
                musicList.add(modelAudio);

            } while (cursor.moveToNext());
        }

        System.out.println();
        // sort the music list (A-Z)
        Collections.sort(musicList, new Comparator<Music>() {
            @Override
            public int compare(Music music, Music music1) {

                return music.getName().compareTo(music1.getName());
            }
        });

        setLike();

    }

    private void startFragment() {
        System.out.println();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment(musicList, artistList);
        fragmentTransaction.replace(R.id.frameLayout, homeFragment);
        fragmentTransaction.commit();
    }

    public void playAudio(int musicPos, Context context) {
        Log.e("check", "passed");

        this.musicPos = musicPos;
        setPlayMusic(musicPos, context);

    }

    public void setPlayMusic(int musicPos, Context context) {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        try {
            Log.e("lenght", " " + toPlay.get(musicPos).getDuration());
            mediaPlayer.reset();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setDataSource(toPlay.get(musicPos).getPath().toString());

            mediaPlayer.prepare();
            mediaPlayer.start();

            setSinger_image(Uri.parse(toPlay.get(musicPos).getPath()));
//
            musicName.setText(toPlay.get(musicPos).getName());
            artistName.setText(toPlay.get(musicPos).getArtist());
            play_pause.setImageResource(R.drawable.pause);
            posToJump = musicPos;

            sharedPreferences_edit(toPlay.get(musicPos));
            notification(R.drawable.pause); // show notification
        } catch (Exception e) {
            Log.e("ERROR", " " + e.getMessage());
            e.getMessage();
//            img_music_cover.setImageResource(R.drawable.ic_audiotrack);
            musicName.setText(null);
            artistName.setText(null);

        }


    }

    private void setSinger_image(Uri uri) {

        try {
            singer_image.setImageBitmap(null);
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(uri.getPath());
            byte[] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            singer_image.setImageBitmap(bitmap);
            makeBlur(bitmap);
        } catch (Exception e) {
            singer_image.setImageResource(R.drawable.audio_img_white);
            background.setImageResource(R.drawable.audio_img_white);
        }
    }

    private void makeBlur(Bitmap bitmap) {
        BlurImage.with(getApplicationContext()).load(bitmap).intensity(20).Async(true).into(background);
    }

    public void start() {
        audioManager();
        if (!mediaPlayer.isPlaying()) {
            if (posToJump != 0) {
                setDefaultPlayList();
                setPlayMusic(posToJump, getApplicationContext());
                mediaPlayer_pause();
            } else {
                setDefaultPlayList();
                setPlayMusic(0, getApplicationContext());
                mediaPlayer_pause();
            }
        }
    }

    public void mediaPlayer_start() {
        play_pause.setImageResource(R.drawable.pause);
        mediaPlayer.start();
        notification(R.drawable.pause);
        played = true;
    }

    public void mediaPlayer_pause() {
        mediaPlayer.pause();
        play_pause.setImageResource(R.drawable.play);
        notification(R.drawable.play);
        played = false;
    }

    public void playNext(int playNext) {
        this.playNext = playNext;
    }

    private void musicEnded() {
        play_pause.setImageResource(R.drawable.play);
    }

    private void setArtistList(Music music) {

        if (artistList.size() > 0)
            for (int i = 0; i < artistList.size(); i++) {
                if (artistList.get(i).getArtistName().equals(music.getArtist())) {
                    if (!artistList.get(i).getArtistMusic().contains(music)) {

                        artistList.get(i).getArtistMusic().add(music);
                        break;
                    }
                } else if (i == artistList.size() - 1) {
                    ArrayList<Music> Music = new ArrayList<>();
                    Music.add(music);

                    artistList.add(new Artist(music.getArtist(), Music));
                }
            }
        else {
            ArrayList<Music> Music = new ArrayList<>();
            Music.add(music);

            artistList.add(new Artist(music.getArtist(), Music));
        }

    }

    public int nextMusic() {

        if (musicPos == toPlay.size() - 1)
            musicPos = 0;
        else
            musicPos++;

        playAudio(musicPos, getApplicationContext());
        return musicPos;
    }

    public int previousMusic() {

        if (musicPos == 0)
            musicPos = musicList.size() - 1;
        else
            musicPos--;

        playAudio(musicPos, getApplicationContext());
        return musicPos;
    }

    public void deleteItem(int pos) {
        musicList.remove(pos);
    }

    private void getFavouriteMusic() {

        LikeDA likeDA = new LikeDA(MainActivity.this);

        likeDA.openDB();
        favouriteList = likeDA.getAllLiked((ArrayList<Music>) musicList);
        likeDA.closeDB();

    }

    private void setLike() {
        getFavouriteMusic();


        for (int i = 0; i < favouriteList.size(); i++) {
            for (int j = 0; j < musicList.size(); j++) {
                if (favouriteList.get(i).getPath().toString().equals(musicList.get(j).getPath().toString())) {
                    musicList.get(j).setLiked(true);
                    break;
                }
            }

        }
        System.out.println();
    }

    public void LikeChanged(int pos, boolean result) {
        musicList.get(pos).setLiked(result);
    }

    public void setPlayList_toPlay(List<Music> musicList) {
        this.toPlay = musicList;
    }

    private void setDefaultPlayList() {
        toPlay = musicList;
    }

    // runtime permission
    private void permission() {

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Log.e("per", "denied");
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (played)
                    mediaPlayer_start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                mediaPlayer_pause();
//                mediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                mediaPlayer_pause();
            }
        }
    };

    private void audioManager() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // initiate the audio playback attributes
        playbackAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        AudioFocusRequest focusRequest = null;
        // set the playback attributes for the focus requester
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            focusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener(audioFocusChangeListener)
                    .build();

        final int audioFocusRequest = audioManager.requestAudioFocus(focusRequest);


    }

    public void setMediaPlayerTime(int time) {
        mediaPlayer.seekTo(time);
    }

    // notification manager
    private void notification(int play_pause) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent prevIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent playIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent closeIntent = new Intent(this, NotificationReceiver.class).setAction(ACTION_CLOSE);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Bitmap bitmap;
        try {
            bitmap = bimapGenerate(Uri.parse(toPlay.get(musicPos).getPath()));
        } catch (Exception e) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.audio_img_white);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_2)
                .setLargeIcon(bitmap)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(toPlay.get(musicPos).getName())
                .setContentText(toPlay.get(musicPos).getArtist())
                .addAction(R.drawable.previous, "previous", prevPendingIntent)
                .addAction(play_pause, "play", playPendingIntent)
                .addAction(R.drawable.next, "next", nextPendingIntent)
                .addAction(R.drawable.ic_close, "close", closePendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()) // .setMediaSession(mediaSession.getSessionToken())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setColor(Color.BLUE)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setContentIntent(contentIntent)
                .setAutoCancel(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .setNotificationSilent()
                .setOngoing(true)
                .build();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

    }

    private Bitmap bimapGenerate(Uri uri) {

        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(uri.getPath());
            byte[] data = mmr.getEmbeddedPicture();
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, mmr.getEmbeddedPicture().length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.audio_img_white);
            return bitmap;
        }
    }

    @Override
    public void next() {
        nextMusic();
    }

    @Override
    public void prev() {
        previousMusic();
    }

    @Override
    public void play() {

        if (mediaPlayer.isPlaying())
            mediaPlayer_pause();
        else
            mediaPlayer_start();
    }

    @Override
    public void close() {
        notificationManager.cancel(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!mediaPlayer.isPlaying()) {
            unbindService(this);
            notificationManager.cancel(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, MusicService.class);
        boolean check = bindService(intent, this, BIND_AUTO_CREATE);

        if (mediaPlayer.isPlaying())
            notification(R.drawable.pause);
        else
            notification(R.drawable.play);

        Log.e("bindService", " " + check);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        MusicService.MyBinder myBinder = (MusicService.MyBinder) iBinder;
        musicService = myBinder.getService();
        musicService.setCallBack(MainActivity.this);
        Log.e("Connected", musicService + "");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.e("Disconnected", musicService + "");
        musicService = null;
    }

    public void sharedPreferences_edit(Music music) {
        SharedPreferences sh = getSharedPreferences("lastMusic", MODE_PRIVATE);
        Gson gson = new Gson();
        String str = gson.toJson(music);
        Log.e("path", str);
        SharedPreferences.Editor editor = sh.edit();
        editor.clear();
        editor.putString("music", str);
        editor.commit();

    }

    private void sharedPreferences_read() {
        try {
            SharedPreferences sh = getSharedPreferences("lastMusic", MODE_PRIVATE);
            String data = sh.getString("music", null);
            System.out.println("   ");
            if (data != null) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    Music music = new Music();
                    music.setName(jsonObject.getString("Name"));
                    music.setPath(jsonObject.getString("Path"));
                    music.setArtist(jsonObject.getString("Artist"));
                    music.setDuration(jsonObject.getString("Duration"));
//                    checkMusic(music);
                    posToJump = musicList.indexOf(music);

                    for (int i = 0; i < musicList.size(); i++) {
                        if (musicList.get(i).getPath().equals(music.getPath()) && musicList.get(i).getName().equals(music.getName())) {
                            posToJump = i;
                        }
                    }
                    System.out.println(" ");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public int getPosToJump() {
        return posToJump;
    }

}