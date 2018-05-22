package com.example.mina.movieapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class displayMovies extends AppCompatActivity {


    ListView listView;

    ArrayList<String> arrayList;
    ArrayList<MovieObject> arrayobject;

    /*
    function to check network connection
     */
    public static boolean isNwConnected(Context context) {

        if (context == null) {
            return true;
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
        if (nwInfo != null && nwInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_movies);
        arrayList = new ArrayList<String>();
        arrayobject = new ArrayList<MovieObject>();
        //if network is connected
        if (isNwConnected(displayMovies.this)) {
            SqlliteOpenHelper sqlliteOpenHelper = new SqlliteOpenHelper(displayMovies.this);
            sqlliteOpenHelper.deleteAllData();//delete all old data
            FetchMovies fetchMovies = new FetchMovies();
            fetchMovies.execute();//call do in background
            //if network not found
        } else if (isNwConnected(displayMovies.this) == false) {
            //gret last data from database
            SqlliteOpenHelper sqlliteOpenHelper = new SqlliteOpenHelper(displayMovies.this);
            arrayList = sqlliteOpenHelper.getAllMovies();
            listView = (ListView) findViewById(R.id.movieList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(displayMovies.this, R.layout.movie_list_design, R.id.Movie_text, sqlliteOpenHelper.getAllMovies());
            listView.setAdapter(adapter);
            /////////////////
            //if not not found display alert dialog to tell user
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(displayMovies.this);
                    builder.setTitle("Network Status").setMessage("Network is not found").setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();

                }
            });

            ////////////////////////
        }
    }

    // required total pages and orginal title
    // method to parse json
    private String getMovieDataFromJson(String movieString) {
        SqlliteOpenHelper sqlliteOpenHelper = new SqlliteOpenHelper(displayMovies.this);
        String resultStrs = "";
        try {
            JSONObject jsonObject = new JSONObject(movieString);
            JSONArray MovieTitlesArray = jsonObject.getJSONArray("results");


            for (int i = 0; i < MovieTitlesArray.length(); i++) {
                JSONObject Movie = MovieTitlesArray.getJSONObject(i);
                String MovieName = Movie.getString("title");
                String MovieId = Movie.getString("id");
                String rating = Movie.getString("vote_average");
                String overView = Movie.getString("overview");

                arrayList.add(MovieName);
                MovieObject ob = new MovieObject(MovieName, MovieId, rating, overView);
                arrayobject.add(ob);

                sqlliteOpenHelper.addContact(MovieName);

            }

        } catch (Exception e) {
            Log.e("jsonException", e.getMessage());
        }
        return resultStrs;
    }

    //1) create class extend of AsyncTask
    class FetchMovies extends AsyncTask<String, Void, String> {
        //private final String LOG_TAG=FetchMovies.class.getSimpleName();
        @Override
        protected String doInBackground(String... params) {
            // data come from url
            String movieString = "";

            try {

                URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=b1f5db0adfa30b9e8714dba42e9edb44");

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();

                String line;
                while ((line = reader.readLine()) != null) {       // if reader buffer find data set it in String Buffer
                    buffer.append(line);
                }

                movieString = buffer.toString();   // reading data from String Buffer

            } catch (IOException e) {
                Log.e("connetion error", e.getMessage());


            }
            return getMovieDataFromJson(movieString);  // return data from background

        }


        @Override
        protected void onPostExecute(String result) {

            listView = (ListView) findViewById(R.id.movieList);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(displayMovies.this, R.layout.movie_list_design, R.id.Movie_text, arrayList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent intent = new Intent(displayMovies.this, MoviePage.class);
                    // intent.putExtra("name",arrayList.get(i));
                    MovieObject object = arrayobject.get(i);
                    Bundle bundle = new Bundle();

                    bundle.putSerializable("value", (Serializable) object);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

        }

    }
}
