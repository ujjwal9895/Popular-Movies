package com.example.ujjwaljain.popularmovies;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by Ujjwal Jain on 19-02-2016.
 */

@ContentProvider(authority = FavouriteMovieProvider.AUTHORITY, database = FavouriteMovieDatabase.class, packageName = "com.example.ujjwaljain.popularmovies.provider")
public class FavouriteMovieProvider {

    public static final String AUTHORITY =
            "com.example.ujjwaljain.popularmovies.favouritemovieprovider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String FAVOURITE_MOVIES = "favourite_movies";
    }

    private static Uri buildUri(String ... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();

        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = FavouriteMovieDatabase.FAVOURITE_MOVIES) public static class FavouriteMovies{

        @ContentUri(
                path = Path.FAVOURITE_MOVIES,
                type = "vnd.android.cursor.dir/favourite_movies",
                defaultSort = FavouriteMovieColumns._ID + " ASC")

        public static final Uri CONTENT_URI = buildUri(Path.FAVOURITE_MOVIES);

        @InexactContentUri(
                name = "FavouriteMovies_ID",
                path = Path.FAVOURITE_MOVIES + "/#",
                type = "vnd.android.cursor.item/favourite_movies",
                whereColumn = FavouriteMovieColumns.MOVIE_ID,
                pathSegment = 1)

        public static Uri withId(String id){
            return buildUri(Path.FAVOURITE_MOVIES, String.valueOf(id));
        }

    }
}
