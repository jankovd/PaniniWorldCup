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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.util.ArrayList;

import mk.jdex.paniniworldcup.R;
import mk.jdex.paniniworldcup.content.StickersTable;
import mk.jdex.paniniworldcup.ui.adapter.StickersCursorAdapter;
import mk.jdex.paniniworldcup.util.App;

import static mk.jdex.paniniworldcup.util.Util.hasJellyBean;

/**
 * Created by Dejan on 4/5/14.
 */
public class StickersFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String STATE_FISRT_VISIBLE_ITEM = "state_first_visible_item";
    private static final String STATE_COLLECTED_SELECTED = "state_collected_selected";
    private static final String STATE_MISSING_SELECTED = "state_missing_selected";
    private static final String STATE_DUPLICATES_SELECTED = "state_duplicates_selected";

    private static final int MAX_STICKER_COUNT = 10;

    public static final int STICKERS_NO_FILTER = -1;
    private static final int STICKERS_WAIT_FOR_FILTER = -2;

    private static final String STATE_COUNTRY_ID = "state_country_id";

    private StickyGridHeadersGridView mGridView;
    private TextView mEmptyTextView;
    private ProgressBar mEmptyProgressView;

    private StickersCursorAdapter mAdapter;
    private int mCountryId;
    private int mFirstVisiblePosition;

    private boolean mIsCollectedSelected = false;
    private boolean mIsMissingSelected = false;
    private boolean mIsDuplicatesSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCountryId = STICKERS_WAIT_FOR_FILTER;
        mFirstVisiblePosition = 0;
        if (savedInstanceState != null) {
            mCountryId = savedInstanceState.getInt(STATE_COUNTRY_ID, STICKERS_WAIT_FOR_FILTER);
            mFirstVisiblePosition = savedInstanceState.getInt(STATE_FISRT_VISIBLE_ITEM);

            mIsCollectedSelected = savedInstanceState.getBoolean(STATE_COLLECTED_SELECTED);
            mIsMissingSelected = savedInstanceState.getBoolean(STATE_MISSING_SELECTED);
            mIsDuplicatesSelected = savedInstanceState.getBoolean(STATE_DUPLICATES_SELECTED);
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
        setHasOptionsMenu(true);

        if (mCountryId != STICKERS_WAIT_FOR_FILTER) {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        SubMenu subMenu = menu.addSubMenu("Filter");
        subMenu.getItem().setIcon(R.drawable.ic_action_filter);
        subMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        inflater.inflate(R.menu.stickers, subMenu);

        subMenu.findItem(R.id.action_collected).setChecked(mIsCollectedSelected);
        subMenu.findItem(R.id.action_missing).setChecked(mIsMissingSelected);
        subMenu.findItem(R.id.action_duplicates).setChecked(mIsDuplicatesSelected);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (R.id.action_collected == itemId) {
            mIsCollectedSelected = !mIsCollectedSelected;
            item.setChecked(mIsCollectedSelected);
        } else if (R.id.action_missing == itemId) {
            mIsMissingSelected = !mIsMissingSelected;
            item.setChecked(mIsMissingSelected);
        } else if (R.id.action_duplicates == itemId) {
            mIsDuplicatesSelected = !mIsDuplicatesSelected;
            item.setChecked(mIsDuplicatesSelected);
        } else {
            return super.onOptionsItemSelected(item);
        }

        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_COUNTRY_ID, mCountryId);
        outState.putInt(STATE_FISRT_VISIBLE_ITEM, mGridView.getFirstVisiblePosition());
        outState.putBoolean(STATE_COLLECTED_SELECTED, mIsCollectedSelected);
        outState.putBoolean(STATE_MISSING_SELECTED, mIsMissingSelected);
        outState.putBoolean(STATE_DUPLICATES_SELECTED, mIsDuplicatesSelected);
    }

    /**
     * @param countryId the id of the country for which stickers should be displayed or {@link #STICKERS_NO_FILTER} to display all
     */
    public void setSelectedCountryId(int countryId) {
        if (mCountryId == countryId && mCountryId != STICKERS_WAIT_FOR_FILTER) {
            return;
        }
        mCountryId = countryId;
        getLoaderManager().restartLoader(0, null, this);
    }

    private void setEmptyViewVisibility(boolean isLoading) {
        mEmptyProgressView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mEmptyTextView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundleArgs) {
        String selection = "";
        ArrayList<String> selectionArgs = new ArrayList<String>(4);
        if (mCountryId == STICKERS_WAIT_FOR_FILTER) {
            // if we by mistake get here when we should wait for a filter, display all items
            mCountryId = STICKERS_NO_FILTER;
        }

        if (mCountryId != STICKERS_NO_FILTER) {
            selection = StickersTable.COLUMN_COUNTRY_ID + "=?";
            selectionArgs.add(String.valueOf(mCountryId));
        }
        boolean hasArgs = !selectionArgs.isEmpty();
        String filter = "";
        if (mIsCollectedSelected) {
            filter += StickersTable.COLUMN_COUNT + ">?";
            selectionArgs.add("0");
        }
        if (mIsDuplicatesSelected) {
            filter += TextUtils.isEmpty(filter) ? "" : " OR ";
            filter += StickersTable.COLUMN_COUNT + ">?";
            selectionArgs.add("1");
        }
        if (mIsMissingSelected) {
            filter += TextUtils.isEmpty(filter) ? "" : " OR ";
            filter += StickersTable.COLUMN_COUNT + "=?";
            selectionArgs.add("0");
        }

        if (!TextUtils.isEmpty(filter)) {
            selection += hasArgs ? " AND (" : "";
            selection += filter;
            selection += hasArgs ? ")" : "";
        }

        String[] args = selectionArgs.toArray(new String[selectionArgs.size()]);
        CursorLoader cl = new CursorLoader(getActivity(), StickersTable.CONTENT_URI,
                new String[]{
                        StickersTable._ID,
                        StickersTable.COLUMN_DISPLAY_NAME,
                        StickersTable.COLUMN_COUNT,
                        StickersTable.COLUMN_COUNTRY_ID,
                        StickersTable.COLUMN_PRIMARY_COLOR,
                        StickersTable.COLUMN_SECONDARY_COLOR,
                        StickersTable.COLUMN_ABBREVIATION,
                        StickersTable.COLUMN_FULL_NAME
                },
                selection, selectionArgs.isEmpty() ? null : args, null
        );
        return cl;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean restorePostion = mAdapter.isEmpty();
        mAdapter.changeCursor(data);
        setEmptyViewVisibility(false);

        if (!restorePostion) {
            return;
        }

        mGridView.post(new Runnable() {
            @Override
            public void run() {
                mGridView.setSelection(mFirstVisiblePosition);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);

        setEmptyViewVisibility(true);
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
        final int _id = c.getInt(mAdapter.getColId());
        final int _count = reset ? -1 : c.getInt(mAdapter.getColCount());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri updateUri = StickersTable.CONTENT_URI;
                updateUri = ContentUris.withAppendedId(updateUri, _id);
                ContentValues values = new ContentValues();
                values.put(StickersTable.COLUMN_COUNT, _count >= MAX_STICKER_COUNT ? 0 : (_count + 1));
                App.getInstance().getContentResolver().update(updateUri, values, null, null);
            }
        }).start();
    }
}
