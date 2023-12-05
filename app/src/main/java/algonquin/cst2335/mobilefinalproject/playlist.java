package algonquin.cst2335.mobilefinalproject;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.mobilefinalproject.databinding.ActivityPlaylistBinding;
import algonquin.cst2335.mobilefinalproject.databinding.SongPlaylistBinding;

public class playlist extends AppCompatActivity {

    /**
     * Initialize the Adapter for the Recycle view
     */
    private RecyclerView.Adapter myAdapter;
    private RecyclerView recyclerView;
    private SongsViewModel songsViewModel;
    private ActivityPlaylistBinding binding;
    private SongsAdapter songsAdapter;
    /**
     * Creates the RequestQueue necessary for the Volley library
     */
    private RequestQueue queue;

    /**
     * Stores the songs from the API
     */
    private List<Songs> songsList = new ArrayList<>();
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setTitle("Your Playlist");

        setSupportActionBar(binding.toolbar);



        /**
         * Create the Volley
         */
        queue = Volley.newRequestQueue(this);
        songsAdapter = new SongsAdapter(songsList);
        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);

        //?Sets the logic for travel between the playlist layout and the main deezer layout
        binding.searchPageButton.setOnClickListener(click -> {
            Intent intent = new Intent(this, Deezer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        binding.playlistPageButton.setOnClickListener(click -> {
            startActivity(new Intent(this, playlist.class));
        });


        /**
         * Build the database for the Playlist
         */
        SongsDatabase songsDatabase = Room.databaseBuilder(getApplicationContext(), SongsDatabase.class, "deezerDB").build();
        DeezerDAO dDao = songsDatabase.deezerDao();


        binding.searchButton.setOnClickListener(c -> {

            String searchedText = binding.searchTextPlaylist.getText().toString().trim();

            Executor thread1 = Executors.newSingleThreadExecutor();
            thread1.execute(() -> {
                List<Songs> searchResults = dDao.searchSong(searchedText);
                runOnUiThread(() -> {
                    if (searchResults != null && !searchResults.isEmpty()) {
                        songsList.clear();
                        songsList.addAll(searchResults);
                        myAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Song not found: " + searchedText, Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });


        if (songsList.isEmpty()) {
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                List<Songs> allSongs = dDao.getAllSongs();
                Log.d("Database", "Number of songs: " + allSongs.size());
                runOnUiThread(() -> {
                    songsList.addAll(allSongs);
                    if (myAdapter == null) {
                        myAdapter = new SongsAdapter(songsList);
                        recyclerView.setAdapter(myAdapter);
                    } else {
                        myAdapter.notifyDataSetChanged();
                    }
                });
            });
        }

        recyclerView = binding.favSongs;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songsAdapter);

    }



    public class SongsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        TextView durationTextView;
        ImageView albumCoverSP;

        androidx.appcompat.widget.Toolbar songsPlaylist;

        SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.songName);
            artistTextView = itemView.findViewById(R.id.artistName);
            durationTextView = itemView.findViewById(R.id.duration);
            albumCoverSP = itemView.findViewById(R.id.albumCoverSP);
            songsPlaylist = itemView.findViewById(R.id.songToolsP);

        }

        public void bind(Songs songs) {
            titleTextView.setText(songs.getTitle());
            artistTextView.setText(songs.getArtistName());
            durationTextView.setText(formatDuration(songs.getDuration()));


            String pathname = getFilesDir() + "/" + songs.getAlbumCover();
            File file = new File(pathname);

            if (file.exists()) {
                Bitmap albumCover = BitmapFactory.decodeFile(pathname);
                albumCoverSP.setImageBitmap(albumCover);
                albumCoverSP.setVisibility(View.VISIBLE);
            } else {
                ImageRequest imgReq = new ImageRequest(songs.getAlbumCover(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        albumCoverSP.setImageBitmap(bitmap);
                        albumCoverSP.setVisibility(View.VISIBLE);
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                                    playlist.this.openFileOutput(songs.getArtistName() + ".png", Activity.MODE_PRIVATE));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1024, 1024, ImageView.ScaleType.CENTER, null, error -> {
                    // Handle error in fetching the image
                    error.printStackTrace();
                });
                queue.add(imgReq);
            }
        }

        private String formatDuration(int duration) {
            int minutes = duration / 60;
            int seconds = duration % 60;
            return String.format("%02d:%02d", minutes, seconds);
        }
    }

    // Inner class for SongsAdapter
    class SongsAdapter extends RecyclerView.Adapter<SongsViewHolder> {

        private List<Songs> songsList;

        SongsAdapter(List<Songs> songsList) {
            this.songsList = songsList;
        }


        @NonNull
        @Override
        public SongsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            SongPlaylistBinding playlistBinding = SongPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SongsViewHolder(playlistBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
            SongsDatabase songsDatabase = Room.databaseBuilder(getApplicationContext(), SongsDatabase.class, "deezerDB").build();
            DeezerDAO dDao = songsDatabase.deezerDao();
            Songs song = songsList.get(position);
            holder.bind(song);

            androidx.appcompat.widget.Toolbar toolbar = holder.songsPlaylist;
            toolbar.inflateMenu(R.menu.songsplaylist);

            toolbar.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(playlist.this);
                        builder.setMessage("Do you want to delete this song from your playlist?")
                                .setTitle("Delete")
                                .setNegativeButton("No", (dialog, which) -> {
                                })
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    Songs songToDelete = songsList.get(position);
                                    songsList.remove(songToDelete);

                                    if (songToDelete != null) {
                                        songsAdapter.notifyItemChanged(position);
                                        Executor thread1 = Executors.newSingleThreadExecutor();
                                        thread1.execute(() -> {
                                            dDao.deleteSongFromPlayList(songToDelete);
                                        });
                                        Log.d("SongToDelete", "Song to Delete: " + songToDelete.toString());

                                        Snackbar.make(findViewById(android.R.id.content), "Song Deleted from playlist", Snackbar.LENGTH_LONG)
                                                .setAction("Undo", (btn) -> {
                                                    Executor thread2 = Executors.newSingleThreadExecutor();
                                                    thread2.execute(() -> {
                                                        // undo the addition from the database
                                                        dDao.insertSong(songToDelete);
                                                    });

                                                    songsList.remove(songToDelete);
                                                    songsAdapter.notifyItemChanged(position);

                                                })
                                                .show();
                                    }
                                });
                        builder.create().show();
                        break;
                    case R.id.preview:
                        try {
                            // Construct the URL for the Deezer API to get tracks of the selected album
                            String tracksURL = "https://api.deezer.com/track/" + song.getId();

                            // Make a GET request to the Deezer API to get tracks of the selected album
                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, tracksURL, null,
                                    response -> {
                                        try {
                                            // Get the array of songs from the response
                                            JSONArray songsArray = response.getJSONArray("data");

                                            // Iterate through the songsArray to find the matching song ID
                                            for (int i = 0; i < songsArray.length(); i++) {
                                                JSONObject currentSong = songsArray.getJSONObject(i);

                                                // Check if the current song has the same ID as the selected song
                                                int currentSongId = currentSong.getInt("id");
                                                if (currentSongId == song.getId()) {
                                                    // Get the preview URL of the matched song
                                                    String previewUrl = currentSong.getString("preview");

                                                    // Play the preview
                                                    playPreview(previewUrl);

                                                    // Exit the loop after finding the match
                                                    return;
                                                }
                                            }

                                            // If no match is found, show a Snackbar
                                            Snackbar.make(findViewById(android.R.id.content), "No preview available for this song", Snackbar.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    },
                                    error -> {
                                        // Handle error in fetching tracks
                                        error.printStackTrace();
                                    });
                            queue.add(request); // Add the tracks request
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                }

                return false;
            });

        }

        private void playPreview(String previewUrl) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(previewUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(findViewById(android.R.id.content), "Error playing preview", Snackbar.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getItemCount() {
            return songsList.size();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.deezer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sunrise:
                // startActivity(new Intent(this, Sunrise.class));
                break;
            case R.id.dictionary:
                // startActivity(new Intent(this, dictionary.class));
                break;
            case R.id.recipe:
                // startActivity(new Intent(this, dictionary.class));
                break;
            case R.id.info:
                AlertDialog.Builder builder = new AlertDialog.Builder(playlist.this);
                builder.setMessage(("Info: \n Create your very own deezer playlists here \n 1. click on the Search Icon to look up your favourite artists and youll receive a list of their albums \n 2. Click on any album and all of their tracks within the album will be displayed for you to save \n 3. click on the 3 dotted icon to preview or save your song \n 4. Go ahead ahead and click the playlist icon and all of your favourite music will be displayed. \n 5. You are able to delete any song from your playlist with a click of a button. \n 6. Most important step Enjoy Deezer"))
                        .setTitle("Welcome To Deezer")
                        .setPositiveButton("Okay", (dialog, which) -> {
                            dialog.dismiss();
                        }).show();}
                return true;
        }



}