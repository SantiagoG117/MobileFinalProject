package algonquin.cst2335.mobilefinalproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import algonquin.cst2335.mobilefinalproject.databinding.AlbumBinding;
import algonquin.cst2335.mobilefinalproject.databinding.DeezerBinding;

public class Deezer extends AppCompatActivity {
    //?Attributes

    private RecyclerView.Adapter myAdapter;

    /**
     *
     */
    RequestQueue queue = null;

    /**
     * Stores the songs from the API
     */
    ArrayList<Songs> songsList = new ArrayList<>();

    /**
     * Stores the albums from the API
     */
    ArrayList<DeezerAlbum> albumsList = new ArrayList<>();


    AlbumsViewModel albumModel;

    DeezerBinding binding;
    AlbumBinding ab;

    /*
    *
     */
    SharedPreferences sp;

    Songs song;

    protected Bitmap albumCover;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DeezerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //?Create the Volley
        queue = Volley.newRequestQueue(this);

        //?Set the toolbar
        setTitle("Welcome to Deezer");
        androidx.appcompat.widget.Toolbar toolBar = (binding.toolbar);
        setSupportActionBar(toolBar);

        //?Set the buttons:
        binding.searchButton.setOnClickListener(click -> {
            Intent intent = new Intent(this, Deezer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        binding.playlistPageButton.setOnClickListener(click ->{
            startActivity(new Intent(this,playlist.class));
        });

        //?
        binding.deezerAlbums.setLayoutManager(new LinearLayoutManager(this));
        albumModel = new ViewModelProvider(this).get(AlbumsViewModel.class);

        //?Create the API logic:
        binding.searchButton.setOnClickListener(click -> {
            String searchedText = binding.searchText.getText().toString().trim();

            String stringURL = null;
            try {
                stringURL = "https://api.deezer.com/search/album/?q=" +
                        URLEncoder.encode(searchedText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            //* Make a GET request to the API to get the albums of the selected artists:
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                    (response) -> {
                        try {
                            //* Get the albums array
                            JSONArray albumArray = response.getJSONArray("data");

                            //* Clear the existing albumsList before adding new data
                            albumsList.clear();

                            for (int i = 0; i < albumArray.length(); i++) {
                                //* Get the album's information
                                JSONObject album0 = albumArray.getJSONObject(i);
                                long albumId = album0.getLong("id");
                                String albumName = album0.getString("title");
                                String albumCoverUrl = album0.getString("cover_xl");
                                //* Get the artist information
                                JSONObject artist = album0.getJSONObject("artist");
                                String artistName = artist.getString("name");

                                //*Create a new album and add it to the albums arraylist
                                DeezerAlbum album = new DeezerAlbum(albumId, albumName, artistName, albumCoverUrl);
                                albumsList.add(album);
                            }
                            //* Notify the adapter that the data set has changed
                            myAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ,
                    error -> {
                        //* Error handler
                        error.printStackTrace();
                    });
            queue.add(request);
        });

        albumModel.selectedAlbums.observe(this, album -> {
            if (album != null){
                try {
                    //* Construct the URL for the Deezer API to get the songs (tracks) of the selected album
                    String tracksURL = "https://api.deezer.com/album/" + album.getAlbumId() + "/tracks";

                    //* Make a GET request to the API to get the songs of the selected album:
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, tracksURL, null,
                            (response) -> {
                                try {
                                    //*Get the array of albums from the response
                                    JSONArray songsArray = response.getJSONArray("data");
                                    //*Clear the existing songsList before adding new data
                                    songsList.clear();

                                    for (int i = 0; i < songsArray.length(); i++) {
                                        JSONObject trackObject = songsArray.getJSONObject(i);
                                        long trackId = trackObject.getLong("id");
                                        String trackTitle = trackObject.getString("title");
                                        String artistName = trackObject.getJSONObject("artist").getString("name");
                                        int duration = trackObject.getInt("duration");

                                        Songs track = new Songs(trackId, trackTitle, duration, album.getTitle(), album.getCoverUrl(), artistName);
                                        songsList.add(track);
                                    }
                                    // Notify the adapter that the data set has changed
                                    myAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                AlbumDetailFragment albumDetailFragment = new AlbumDetailFragment(songsList, album,queue);
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();

                                transaction.addToBackStack("");
                                transaction.replace(R.id.albumFragment, albumDetailFragment);
                                transaction.commit();
                            },
                            error -> {
                                //* Error handler
                                error.printStackTrace();
                            });
                    //* Add the tracks request
                    queue.add(request);

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        binding.deezerAlbums.setAdapter(myAdapter = new RecyclerView.Adapter<MyAlbumHolder>() {
            @NonNull
            @Override
            public MyAlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                AlbumBinding ab = AlbumBinding.inflate(getLayoutInflater(), parent, false);
                return new MyAlbumHolder(ab.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull MyAlbumHolder holder, int position) {
                DeezerAlbum deezerAlbum = albumsList.get(position);
                holder.bind(deezerAlbum);
            }

            @Override
            public int getItemCount() {
                return albumsList.size();
            }
        });
    }

    //?Set Row holder
    class MyAlbumHolder extends RecyclerView.ViewHolder {
        TextView albumName;
        TextView artistName;
        ImageView imageView;
        Toolbar albumMenu;


        public MyAlbumHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(c -> {
                int position = getAbsoluteAdapterPosition();
                DeezerAlbum selected = albumsList.get(position);
                albumModel.selectedAlbums.postValue(selected);
            });

            albumName = itemView.findViewById(R.id.albumName);
            artistName = itemView.findViewById(R.id.artistName);
            imageView = itemView.findViewById(R.id.albumCover);
        }

        public void bind(DeezerAlbum deezerAlbum) {
            albumName.setText(deezerAlbum.getTitle());
            artistName.setText(deezerAlbum.getArtistName());

            String pathname = getFilesDir() + "/" + deezerAlbum.getCoverUrl();
            File file = new File(pathname);

            if (file.exists()) {
                albumCover = BitmapFactory.decodeFile(pathname);
                imageView.setImageBitmap(albumCover);
                imageView.setVisibility(View.VISIBLE);
            } else {
                ImageRequest imgReq = new ImageRequest(deezerAlbum.getCoverUrl(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                                    Deezer.this.openFileOutput(deezerAlbum.getArtistName() + ".png", Activity.MODE_PRIVATE));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1024, 1024, ImageView.ScaleType.CENTER, null, error -> {
                });
                queue.add(imgReq);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.deezer_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.sunrise:
//                startActivity(new Intent(this, Sunrise.class));
                break;
            case R.id.dictionary:
//                startActivity(new Intent(this, dictionary.class));
                break;
            case R.id.recipe:
//                startActivity(new Intent(this, dictionary.class));
                break;
        }

        return true;
    }

}
