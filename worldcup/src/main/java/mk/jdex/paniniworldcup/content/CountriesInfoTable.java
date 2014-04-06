package mk.jdex.paniniworldcup.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dejan on 4/6/2014.
 */
public class CountriesInfoTable implements BaseColumns {
    public static final String TABLE_NAME = "country_info";
    public static final String COLUMN_ABBR = CountriesTable.COLUMN_ABBREVIATION;
    public static final String COLUMN_PRIMARY_COLOR = CountriesTable.COLUMN_PRIMARY_COLOR;
    public static final String COLUMN_SECONDARY_COLOR = CountriesTable.COLUMN_SECONDARY_COLOR;
    public static final String COLUMN_THE_ONE = CountriesTable.COLUMN_THE_ONE;
    public static final String COLUMN_ALL_STICKERS = "all_stickers";
    public static final String COLUMN_UNIQUE_STICKERS = "unique_stickers";
    public static final String COLUMN_WITH_DOUBLES_COUNT = "with_doubles";
    public static final Uri CONTENT_URI = Uri.parse("content://" + StickersProvider.AUTHORITY + "/" + TABLE_NAME);
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jdex.paniniworldcup.country_info";
}
