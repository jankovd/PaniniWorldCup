package mk.jdex.paniniworldcup.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import mk.jdex.paniniworldcup.R;
import mk.jdex.paniniworldcup.content.CountriesInfoTable;

/**
 * Created by Dejan on 4/5/14.
 */
public class CountriesCursorAdapter extends CursorAdapter {

    private LayoutInflater mInflater;

    private int mColId;
    private int mColAbbr;
    private int mColTheOne;
    private int mColAllCount;
    private int mColUniqueCount;
    private int mColWithDoubles;

    public CountriesCursorAdapter(Context context, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mInflater = LayoutInflater.from(context);
        readColumnsFromCursor(c);
    }

    @Override
    public Cursor swapCursor(Cursor newCursor) {
        readColumnsFromCursor(newCursor);
        return super.swapCursor(newCursor);
    }

    @Override
    public void changeCursor(Cursor cursor) {
        super.changeCursor(cursor);
        readColumnsFromCursor(cursor);
    }

    private void readColumnsFromCursor(Cursor c) {
        if (c != null) {
            mColId = c.getColumnIndex(CountriesInfoTable._ID);
            mColAbbr = c.getColumnIndex(CountriesInfoTable.COLUMN_ABBR);
            mColTheOne = c.getColumnIndex(CountriesInfoTable.COLUMN_HAS_STICKERS);
            mColAllCount = c.getColumnIndex(CountriesInfoTable.COLUMN_ALL_STICKERS);
            mColUniqueCount = c.getColumnIndex(CountriesInfoTable.COLUMN_UNIQUE_STICKERS);
            mColWithDoubles = c.getColumnIndex(CountriesInfoTable.COLUMN_WITH_DOUBLES_COUNT);
        }
    }

    public int getColCountryId() {
        return mColId;
    }

    public int getColHasStickers() {
        return mColTheOne;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View convertView = mInflater.inflate(R.layout.item_spinner_country, viewGroup, false);

        ViewHolder holder = new ViewHolder();
        holder.displayName = (TextView) convertView.findViewById(R.id.tv_country_name);
        holder.count = (TextView) convertView.findViewById(R.id.tv_country_stickers_count);
        convertView.setTag(holder);

        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.displayName.setText(cursor.getString(mColAbbr));
        int allCount = cursor.getInt(mColAllCount);
        int uniqueCount = cursor.getInt(mColUniqueCount);
        boolean isTheOne = cursor.getInt(mColTheOne) == 1;

        if (isTheOne) {
            holder.count.setVisibility(View.INVISIBLE);
        } else {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(uniqueCount + " / " + allCount);
        }
    }

    public boolean isAllItem(int position) {
        return ((Cursor) getItem(position)).getInt(mColTheOne) == 1;
    }

    private static class ViewHolder {
        TextView displayName;
        TextView count;
    }

}
