package algonquin.cst2335.mobilefinalproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

/**
 * @Author Santiago Garcia
 * Launch class in charge of triggering the Deezer application
 */
public class Deezer extends AppCompatActivity {
    //?Attributes

    /**
     * Initialize the Adapter for the Recycle view
     */
    private RecyclerView.Adapter myAdapter;

    /**
     * Creates the RequestQueue necessary for the Volley library
     */
    RequestQueue queue = null;

    /**
     * Stores the songs from the API
     */
    ArrayList<Songs> songsList = new ArrayList<>();

    /**
     * Stores the albums from the API
     */
    ArrayList<DeezerAlbumDTO> albumsList = new ArrayList<>();

    /**
     * View Model for the album
     */
    AlbumsViewModel albumModel;
    /**
     * View Model for the song
     */
    SongsViewModel songModel;

    DeezerBinding binding;

    protected Bitmap albumCover;

    /**
     * Launching code for the Deezer application
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DeezerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /**
         * Create the Volley
         */
        queue = Volley.newRequestQueue(this);

        /**
         * Set the toolbar
         */
        setTitle("Deezer");

        /**
         * User instructions for how to use the application
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(Deezer.this);
        builder.setMessage(("Info: \n Create your very own deezer playlists here \n 1. click on the Search Icon to look up your favourite artists and you will receive a list of their albums \n 2. Click on any album and all of their tracks within the album will be displayed for you to save \n 3. click on the 3 dotted icon to preview or save your song \n 4. Go ahead ahead and click the playlist icon and all of your favourite music will be displayed. \n 5. You are able to delete any song from your playlist with a click of a button. \n"))
                .setTitle("Welcome to Deezer")
                .setPositiveButton("Okay", (dialog, which) -> {
                    dialog.dismiss();
                }).show();

        androidx.appcompat.widget.Toolbar toolBar = (binding.toolbar);
        setSupportActionBar(toolBar);

        /**
         * Calls the Deezer class so we can switch between Playlist and Search functions
         */
        binding.searchButton.setOnClickListener(click -> {
            Intent intent = new Intent(this, Deezer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        /**
         * Sends the user to the Playlist page
         */
        binding.playlistPageButton.setOnClickListener(click ->{
            startActivity(new Intent(this,playlist.class));
        });

        /**
         * Set the Recycle View the deezer layout to linear
         */
        binding.deezerAlbums.setLayoutManager(new LinearLayoutManager(this));
        albumModel = new ViewModelProvider(this).get(AlbumsViewModel.class);

        //?Create the API logic:
        /**
         * Event listener for the searchButton.
         */
        binding.searchButton.setOnClickListener(click -> {
            /**
             * Store the user's input in a variable
             */
            String searchedText = binding.searchText.getText().toString().trim();

            /**
             * Build the URL to access the Deezer API using the value of the user's input
             */
            String stringURL = null;
            try {
                stringURL = "https://api.deezer.com/search/album/?q=" +
                        URLEncoder.encode(searchedText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            /**
             * Make a GET request to the API to get the albums of the selected artists:
             */
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, stringURL, null,
                    (response) -> {
                        try {
                            /**
                             * Get the albums array
                             */
                            JSONArray albumArray = response.getJSONArray("data");

                            /**
                             * Clear the existing albumsList before adding new data
                             */
                            albumsList.clear();

                            /**
                             * Iterate over the JSON array holding the albums
                             */
                            for (int i = 0; i < albumArray.length(); i++) {
                                /**
                                 * Get the JSON object (album) information
                                 */
                                JSONObject album = albumArray.getJSONObject(i);
                                long albumId = album.getLong("id");
                                String albumName = album.getString("title");
                                String albumCoverUrl = album.getString("cover_xl");

                                /**
                                 * Create a new JSON object holding the artist information
                                 */
                                JSONObject artist = album.getJSONObject("artist");
                                String artistName = artist.getString("name");

                                /**
                                 * Create a new album and add it to the albums arraylist
                                 */
                                DeezerAlbumDTO deezerAlbumDTO = new DeezerAlbumDTO(albumId, albumName, artistName, albumCoverUrl);
                                albumsList.add(deezerAlbumDTO);
                                //Prevent the application from crashing when the phone rotates its view
                                albumModel.deezerAlbum.postValue(albumsList);
                            }

                            /**
                             *  Notify the adapter that the data set has changed
                             */
                            myAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ,
                    error -> {
                        /**
                         * Error handler
                         */
                        error.printStackTrace();
                    });
            queue.add(request);
        });

        /**
         * Observer to be triggered when an album is selected
         */
        albumModel.selectedAlbums.observe(this, album -> {
            if (album != null){
                try {
                    /**
                     * Construct the URL for the Deezer API to get the songs (tracks) of the selected album
                     */
                    String tracksURL = "https://api.deezer.com/album/" + album.getAlbumId() + "/tracks";

                    /**
                     * Make a GET request to the API to get the songs of the selected album:
                      */
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, tracksURL, null,
                            (response) -> {
                                try {
                                    /**
                                     * Get the array of songs from the selected album
                                     */
                                    JSONArray songsArray = response.getJSONArray("data");
                                    //*Clear the existing songsList before adding new data
                                    songsList.clear();

                                    /**
                                     * Iterate over the album's songs and extract their information
                                     */
                                    for (int i = 0; i < songsArray.length(); i++) {
                                        JSONObject trackObject = songsArray.getJSONObject(i);
                                        long songID = trackObject.getLong("id");
                                        String songTitle = trackObject.getString("title");
                                        String artistName = trackObject.getJSONObject("artist").getString("name");
                                        int duration = trackObject.getInt("duration");

                                        //Create a new song:
                                        Songs song = new Songs(songID, songTitle, duration, album.getTitle(), album.getCoverUrl(), artistName);
                                        //Add the songs to the songsList
                                        songsList.add(song);

                                        //Prevent the application from crashing when the phone rotates its view
                                        songModel.songs.postValue(songsList);
                                    }



                                    /**
                                     * Notify the adapter that the data set has changed
                                     */
                                    myAdapter.notifyDataSetChanged();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                /**
                                 * Create the fragment containing the songs.
                                 * Displays album cover, album name and artist name
                                 * Displays all the songs within the album
                                 * Songs are stored in the recycle view
                                 */
                                AlbumDetailFragment albumDetailFragment = new AlbumDetailFragment(songsList, album, queue);
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                //TODO
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

        /**
         * Adapter for album's recycle view
         */
        binding.deezerAlbums.setAdapter(myAdapter = new RecyclerView.Adapter<MyAlbumHolder>() {
            @NonNull
            @Override
            public MyAlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                /**
                 *  Inflates the album layout
                 */
                AlbumBinding albumBinding = AlbumBinding.inflate(getLayoutInflater(), parent, false);
                return new MyAlbumHolder(albumBinding.getRoot());
            }

            @Override
            public void onBindViewHolder(@NonNull MyAlbumHolder holder, int position) {
                /**
                 * Sets the data for each row in the recyclerview according to the position and size within the albums list
                 */
                DeezerAlbumDTO deezerAlbumDTO = albumsList.get(position);
                holder.bind(deezerAlbumDTO);
            }

            @Override
            public int getItemCount() {
                return albumsList.size();
            }
        });
    }



    /**
     * Represents the data that will be displayed on the Recycle view in the Album list layout
     */
    class MyAlbumHolder extends RecyclerView.ViewHolder {

        TextView albumName;
        TextView artistName;
        ImageView imageView;

        public MyAlbumHolder(@NonNull View itemView) {
            super(itemView);
            /**
             * Determines the album that was selected and its position. Then it initialize the given album.
             */
            itemView.setOnClickListener(c -> {
                int position = getAbsoluteAdapterPosition();
                DeezerAlbumDTO selected = albumsList.get(position);
                albumModel.selectedAlbums.postValue(selected);
            });


            albumName = itemView.findViewById(R.id.albumName);
            artistName = itemView.findViewById(R.id.artistName);
            imageView = itemView.findViewById(R.id.albumCover);
        }


        /**
         * Fills the data for the album passed as parameter
         * @param deezerAlbumDTO
         */
        public void bind(DeezerAlbumDTO deezerAlbumDTO) {
            albumName.setText(deezerAlbumDTO.getTitle());
            artistName.setText(deezerAlbumDTO.getArtistName());

            String pathname = getFilesDir() + "/" + deezerAlbumDTO.getCoverUrl();
            File file = new File(pathname);

            if (file.exists()) {
                albumCover = BitmapFactory.decodeFile(pathname);
                imageView.setImageBitmap(albumCover);
                imageView.setVisibility(View.VISIBLE);
            } else {
                ImageRequest imgReq = new ImageRequest(deezerAlbumDTO.getCoverUrl(), new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                        imageView.setVisibility(View.VISIBLE);
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                                    Deezer.this.openFileOutput(deezerAlbumDTO.getArtistName() + ".png", Activity.MODE_PRIVATE));
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

    /** Creates the manu to add or get the preview of a song
     *
     * @param menu The options menu in which you place your items.
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.deezer_menu, menu);
        return true;
    }


    /**
     * Navigate through the different applications
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
/*            case R.id.sunrise:
//                startActivity(new Intent(this, Sunrise.class));
                break;
            case R.id.dictionary:
//                startActivity(new Intent(this, dictionary.class));
                break;
            case R.id.recipe:
//                startActivity(new Intent(this, dictionary.
class));
                break;*/
            case R.id.main:
                Intent exit = new Intent(Deezer.this, MainActivity.class);
                startActivity(exit);
                break;
            case R.id.info:
                AlertDialog.Builder builder = new AlertDialog.Builder(Deezer.this);
                builder.setMessage(("Info: \n Create your very own deezer playlists here \n 1. click on the Search Icon to look up your favourite artists and you will receive a list of their albums \n 2. Click on any album and all of their tracks within the album will be displayed for you to save \n 3. click on the 3 dotted icon to preview or save your song \n 4. Go ahead ahead and click the playlist icon and all of your favourite music will be displayed. \n 5. You are able to delete any song from your playlist with a click of a button."))
                        .setTitle("Welcome to Deezer")
                        .setPositiveButton("Okay", (dialog, which) -> {
                            dialog.dismiss();
                        }).show();
        }
        return true;
    }

}