package mk.jdex.paniniworldcup.ui;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import mk.jdex.paniniworldcup.R;
import mk.jdex.paniniworldcup.content.StickersTable;
import mk.jdex.paniniworldcup.ui.adapter.StickersCursorAdapter;

import static mk.jdex.paniniworldcup.util.Util.hasJellyBean;

/**
 * Created by Dejan on 4/5/14.
 */
public class StickersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final int STICKERS_NO_FILTER = -1;

    private static final String STATE_COUNTRY_ID = "state_country_id";

    private StickyGridHeadersGridView mGridView;
    private TextView mEmptyTextView;
    private ProgressBar mEmptyProgressView;

    private StickersCursorAdapter mAdapter;
    private int mCountryId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCountryId = STICKERS_NO_FILTER;
        if (savedInstanceState != null) {
            mCountryId = savedInstanceState.getInt(STATE_COUNTRY_ID, STICKERS_NO_FILTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_stickers, null);

        mGridView = (StickyGridHeadersGridView) contentView.findViewById(R.id.gv_stickers);
        mGridView.setAreHeadersSticky(false);
        mGridView.setOnItemClickListener(this);
        mGridView.setOnItemLongClickListener(this);

        View emptyView = contentView.findViewById(R.id.empty_grid_layout);
        mEmptyProgressView = (ProgressBar) emptyView.findViewById(R.id.pb_empty);
        mEmptyTextView = (TextView) emptyView.findViewById(R.id.tv_empty);
        mGridView.setEmptyView(emptyView);

        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new StickersCursorAdapter(getActivity(), null);
        mGridView.setAdapter(mAdapter);

        final int gridColumnWidth = getResources().getDimensionPixelSize(R.dimen.grid_item_width);
        final int gridItemSpacing = getResources().getDimensionPixelSize(R.dimen.grid_item_spacing);
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {
                if (mGridView.getWidth() != 0) {
                    int numColumns = (int) Math.floor(mGridView.getWidth() / (gridColumnWidth + gridItemSpacing));
                    if (numColumns <= 1) {
                        numColumns = 2;
                    }
                    int columnWidth = (mGridView.getWidth() / numColumns) - gridItemSpacing;
                    mAdapter.setItemHeight(columnWidth);
                    mGridView.setNumColumns(numColumns);

                    if (hasJellyBean()) {
                        mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            }
        });


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_COUNTRY_ID, mCountryId);
    }

    /**
     * @param countryId the id of the country for which stickers should be displayed or {@link #STICKERS_NO_FILTER} to display all
     */
    public void setSelectedCountryId(int countryId) {
        if (mCountryId == countryId) {
            return;
        }
        mCountryId = countryId;
        getLoaderManager().restartLoader(0, null, this);
    }

    private void setEmptyViewVisibility(boolean isDatasetEmpty) {
        mEmptyProgressView.setVisibility(isDatasetEmpty ? View.GONE : View.VISIBLE);
        mEmptyTextView.setVisibility(isDatasetEmpty ? View.VISIBLE : View.GONE);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = null;
        String[] selectionArgs = null;
        if (mCountryId != -1) {
            selection = StickersTable.COLUMN_COUNTRY_ID + "=?";
            selectionArgs = new String[]{String.valueOf(mCountryId)};
        }

        CursorLoader cl = new CursorLoader(getActivity(), StickersTable.CONTENT_URI,
                new String[]{
                        StickersTable._ID,
                        StickersTable.COLUMN_DISPLAY_NAME,
                        StickersTable.COLUMN_COUNT,
                        StickersTable.COLUMN_COUNTRY_ID,
                        StickersTable.COLUMN_PRIMARY_COLOR,
                        StickersTable.COLUMN_SECONDARY_COLOR,
                        StickersTable.COLUMN_ABBREVIATION
                },
                selection, selectionArgs, null
        );
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);

        setEmptyViewVisibility(data == null);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);

        setEmptyViewVisibility(false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        updateCount(i, false);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        updateCount(i, true);
        return true;
    }

    private void updateCount(int position, boolean reset) {
        Cursor c = (Cursor) mAdapter.getItem(position);

        int _id = c.getInt(mAdapter.getColId());
        Uri updateUri = StickersTable.CONTENT_URI;
        updateUri = ContentUris.withAppendedId(updateUri, _id);
        ContentValues values = new ContentValues();
        int _count = reset ? -1 : c.getInt(mAdapter.getColCount());
        values.put(StickersTable.COLUMN_COUNT, _count >= 5 ? 0 : ++_count);
        getActivity().getContentResolver().update(updateUri, values, null, null);
    }
}
