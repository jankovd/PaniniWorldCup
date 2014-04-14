package mk.jdex.paniniworldcup.content;

import android.provider.BaseColumns;

/**
 * Created by Dejan on 4/6/2014.
 */
class CountriesTable implements BaseColumns {
    public static final String TABLE_NAME = "countries";
    public static final String COLUMN_ABBREVIATION = "abbr";
    public static final String COLUMN_FULL_NAME = "full_name";
    public static final String COLUMN_PRIMARY_COLOR = "primary_color";
    public static final String COLUMN_SECONDARY_COLOR = "secondary_color";
    public static final String COLUMN_HAS_STICKERS = "has_stickers";
}

