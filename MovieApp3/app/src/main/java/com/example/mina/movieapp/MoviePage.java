package com.example.mina.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class MoviePage extends AppCompatActivity {

    ImageView imageView;
    RatingBar ratingBar;
    TextView txtName, overview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_page);

        txtName = (TextView) findViewById(R.id.chosenMovie);
        overview = (TextView) findViewById(R.id.overview);
        ratingBar = (RatingBar) findViewById(R.id.retingBar);
        imageView = (ImageView) findViewById(R.id.image);
        /*
        get data movie object from class display movies by intent
         */
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        MovieObject ob = (MovieObject) bundle.getSerializable("value");
        txtName.setText(ob.getName());
        overview.setText(ob.getOverview());
        float num = (Float.parseFloat(ob.getRate())) / 2;
        ratingBar.setRating(num);
        String id = ob.getId();

        ////////////////////////
        FetchMoviesLink MovieData = new FetchMoviesLink();
        MovieData.execute(id);//call do in background
    }

    //1) create class extend of AsyncTask
    class FetchMoviesLink extends AsyncTask<String, Void, String> {
        //private final String LOG_TAG=FetchMovies.class.getSimpleName();
        @Override
        protected String doInBackground(String... params) {
            // data come from url
            String movieLink = "";

            try {

                URL url = new URL("http://api.themoviedb.org/3/movie/" + params[0] + "/videos?api_key=18147b0826078c6d5e462bf97f3e032d");

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

                movieLink = buffer.toString();   // reading data from String Buffer

                Log.e("movieLink", movieLink);

            } catch (IOException e) {
                Log.e("connetion error", e.getMessage());
            }

            return getMovieLinkFromJson(movieLink);  // return data from background

        }


        @Override
        protected void onPostExecute(String result) {
            final String x = result;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("link", "https://www.youtube.com/watch?v=" + x);
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + x));
                    startActivity(i);
                }
            });


        }
    }

    // required total pages and orginal title
    // method to parse json
    //get movie key from movie string
    private String getMovieLinkFromJson(String movieString) {

        String Link = "";
        try {
            JSONObject jsonObject = new JSONObject(movieString);
            Log.e("movieString", movieString);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            Log.d("link", "https://www.youtube.com/watch?v=" + Link);
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject one = jsonArray.getJSONObject(i);
                Link = one.getString("key");

            }


        } catch (Exception e) {
            Log.e("jsonException", e.getMessage());
        }
        return Link;
    }
}

