package com.example.ujjwaljain.popularmovies;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by Ujjwal Jain on 19-02-2016.
 */

@Database(version = FavouriteMovieDatabase.VERSION)
public class FavouriteMovieDatabase {

    private FavouriteMovieDatabase(){}

    public static final int VERSION = 1;

    @Table(FavouriteMovieColumns.class) public static final String FAVOURITE_MOVIES = "favourite_movies";

}
