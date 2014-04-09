package mk.jdex.paniniworldcup.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import java.util.HashMap;

import mk.jdex.paniniworldcup.R;
import mk.jdex.paniniworldcup.content.StickersTable;

/**
 * Created by Dejan on 4/5/14.
 */
public class StickersCursorAdapter extends CursorAdapter implements StickyGridHeadersSimpleAdapter {

    private static final HashMap<Integer, Boolean> sColorIsDark = new HashMap<Integer, Boolean>(15);

    private LayoutInflater mInflater;
    private int mItemHeight;

    private int mColId;
    private int mColSign;
    private int mColDisplayName;
    private int mColCount;
    private int mPrimaryColor;
    private int mSecondaryColor;
    private int mCountryId;
    private int mCountryAbbr;

    private int mColorActiveItem;

    public StickersCursorAdapter(Context context, Cursor c) {
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        mInflater = LayoutInflater.from(context);
        mItemHeight = context.getResources().getDimensionPixelSize(R.dimen.grid_item_width);
        mColorActiveItem = context.getResources().getColor(R.color.item_sticker_active);

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
            mColId = c.getColumnIndex(StickersTable._ID);
            mColSign = c.getColumnIndex(StickersTable.COLUMN_SIGN);
            mColDisplayName = c.getColumnIndex(StickersTable.COLUMN_DISPLAY_NAME);
            mColCount = c.getColumnIndex(StickersTable.COLUMN_COUNT);
            mPrimaryColor = c.getColumnIndex(StickersTable.COLUMN_PRIMARY_COLOR);
            mSecondaryColor = c.getColumnIndex(StickersTable.COLUMN_SECONDARY_COLOR);
            mCountryId = c.getColumnIndex(StickersTable.COLUMN_COUNTRY_ID);
            mCountryAbbr = c.getColumnIndex(StickersTable.COLUMN_ABBREVIATION);
        }
    }

    public int getColCount() {
        return mColCount;
    }

    public int getColDisplayName() {
        return mColDisplayName;
    }

    public int getColSign() {
        return mColSign;
    }

    public int getColId() {
        return mColId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View convertView = mInflater.inflate(R.layout.item_grid_sticker, null);

        ViewHolder holder = new ViewHolder();
        holder.displayName = (TextView) convertView.findViewById(R.id.tv_item_sticker_id);
        holder.count = (TextView) convertView.findViewById(R.id.tv_item_sticker_count);
        convertView.setTag(holder);

        //bindView(convertView, context, cursor);
        return convertView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp == null) {
            lp = new AbsListView.LayoutParams(mItemHeight, mItemHeight);
        }
        lp.height = mItemHeight;
        view.setLayoutParams(lp);

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.displayName.setText(cursor.getString(mColDisplayName));
        int count = cursor.getInt(mColCount);
        holder.count.setText("" + count);

        int bkgColor;
        if (count == 0) {
            holder.count.setVisibility(View.GONE);
            bkgColor = cursor.getInt(mPrimaryColor /*mSecondaryColor*/);
        } else {
            holder.count.setVisibility(View.VISIBLE);
            bkgColor = mColorActiveItem; // cursor.getInt(mPrimaryColor);
        }
        view.setBackgroundColor(bkgColor);

        int textColor = getTextColor(bkgColor);
        holder.displayName.setTextColor(textColor);
        holder.count.setTextColor(textColor);
    }

    private int getTextColor(int bkgColor) {

        if (sColorIsDark.containsKey(bkgColor)) {
            return sColorIsDark.get(bkgColor) ? Color.WHITE : Color.BLACK;
        }

        int[] rgb = new int[]{Color.red(bkgColor), Color.green(bkgColor), Color.blue(bkgColor)};
        double luma = 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2];
        sColorIsDark.put(bkgColor, luma < 128);
        return getTextColor(bkgColor);
    }

    public void setItemHeight(int itemHeight) {
        this.mItemHeight = itemHeight;
        notifyDataSetChanged();
    }

    @Override
    public long getHeaderId(int i) {
        return ((Cursor) getItem(i)).getInt(mCountryId);
    }

    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        ViewHolderHeader holder;

        if (view == null) {
            view = mInflater.inflate(R.layout.item_grid_header, viewGroup, false);

            holder = new ViewHolderHeader();
            holder.title = (TextView) view.findViewById(R.id.tv_grid_header_title);
            view.setTag(holder);
        } else {
            holder = (ViewHolderHeader) view.getTag();
        }

        holder.title.setText(((Cursor) getItem(i)).getString(mCountryAbbr));

        return view;
    }


    private static class ViewHolder {
        TextView displayName;
        TextView count;
    }

    private static class ViewHolderHeader {
        TextView title;
    }
}
