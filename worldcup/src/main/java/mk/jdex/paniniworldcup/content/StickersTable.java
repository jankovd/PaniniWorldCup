package mk.jdex.paniniworldcup.content;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Dejan on 4/6/2014.
 */
public class StickersTable extends CountriesTable {
    public static final String TABLE_NAME = "stickers";
    public static final String VIEW_NAME = "stickers_with_country";
    public static final String COLUMN_SIGN = "sign";
    public static final String COLUMN_DISPLAY_NAME = "display_name";
    public static final String COLUMN_COUNTRY_ID = "country_id";
    public static final String COLUMN_COUNT = "count";
    public static final Uri CONTENT_URI = Uri.parse("content://" + StickersProvider.AUTHORITY + "/" + TABLE_NAME);
    //public static final Uri QUERY_CONTENT_URI = Uri.parse("content://" + StickersProvider.AUTHORITY + "/" + VIEW_NAME);
    //public static final Uri INSERT_CONTENT_URI = Uri.parse("content://" + StickersProvider.AUTHORITY + "/" + TABLE_NAME);
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jdex.paniniworldcup.sticker";
}
