package algonquin.cst2335.mobilefinalproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import algonquin.cst2335.mobilefinalproject.databinding.ActivityPlaylistBinding;

public class playlist extends AppCompatActivity {
    private RecyclerView.Adapter myAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Songs> songsList = new ArrayList<>();
    private SongsViewModel songsViewModel;
    private ActivityPlaylistBinding binding;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaylistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle(R.string.app_name);

        setSupportActionBar(binding.toolbar);

        queue = Volley.newRequestQueue(this);

        SongsAdapter songsAdapter = new SongsAdapter(songsList);

        SongsDatabase songsDatabase = Room.databaseBuilder(getApplicationContext(), SongsDatabase.class, "deezerDB").build();
        DeezerDAO dDao = songsDatabase.deezerDao();


        songsViewModel = new ViewModelProvider(this).get(SongsViewModel.class);

        if (songsList.isEmpty()) {
            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
                List<Songs> allSongs = dDao.getAllSongs();
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


        binding.searchButton.setOnClickListener(click -> {
            Intent intent = new Intent(this, Deezer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        binding.playlistPageButton.setOnClickListener(click -> {
            startActivity(new Intent(this, playlist.class));
        });

        binding.searchButton.setOnClickListener(c -> {
            String searchedText = binding.searchTextPlaylist.getText().toString().trim();

            Executor thread = Executors.newSingleThreadExecutor();
            thread.execute(() -> {
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

        recyclerView = binding.favSongs;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(songsAdapter);

    }


    public class SongsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        TextView durationTextView;
        ImageView albumCoverSP;

        SongsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.songName);
            artistTextView = itemView.findViewById(R.id.artistName);
            durationTextView = itemView.findViewById(R.id.duration);
            albumCoverSP = itemView.findViewById(R.id.albumCoverSP);
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
            ActivityPlaylistBinding playlistBinding = ActivityPlaylistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SongsViewHolder(playlistBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull SongsViewHolder holder, int position) {
            Songs song = songsList.get(position);
            holder.bind(song);
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
        }
        return true;
    }

}
