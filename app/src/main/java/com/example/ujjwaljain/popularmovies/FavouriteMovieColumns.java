package com.example.ujjwaljain.popularmovies;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by Ujjwal Jain on 19-02-2016.
 */
public interface FavouriteMovieColumns {

    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.INTEGER) @NotNull
    public static final String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.BLOB)
    public static final String BACKDROP = "backdrop";

    @DataType(DataType.Type.BLOB)
    public static final String POSTER = "poster";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String DATE = "date";

    @DataType(DataType.Type.REAL) @NotNull
    public static final String RATING = "rating";
}
