package com.example.mina.movieapp;

import java.io.Serializable;

/**
 * Created by NH on 5/18/2018.
 */

/*
create movie data object
 */

public class MovieObject implements Serializable {

    String name, id, rate, overview;

    public MovieObject(String name, String id, String rate, String overview) {

        this.name = name;
        this.id = id;
        this.rate = rate;
        this.overview = overview;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getRate() {
        return rate;
    }

    public String getOverview() {
        return overview;
    }


}
