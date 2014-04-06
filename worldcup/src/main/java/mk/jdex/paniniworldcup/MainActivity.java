package mk.jdex.paniniworldcup;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import mk.jdex.paniniworldcup.content.CountriesInfoTable;
import mk.jdex.paniniworldcup.ui.StickersFragment;
import mk.jdex.paniniworldcup.ui.adapter.CountriesCursorAdapter;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private CountriesCursorAdapter mAdapter;
    private StickersFragment mStickersFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStickersFragment =
                (StickersFragment) getSupportFragmentManager()
                        .findFragmentByTag(getString(R.string.tag_fragment_stickers_grid));

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        mAdapter = new CountriesCursorAdapter(actionBar.getThemedContext(), null);
        actionBar.setListNavigationCallbacks(mAdapter, this);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, ColorCalc.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        int colCountryId = mAdapter.getColCountryId();
        int colTheOne = mAdapter.getColTheOne();
        Cursor c = ((Cursor) mAdapter.getItem(position));
        int countryId = c.getInt(colCountryId);
        boolean isTheOne = c.getInt(colTheOne) == 1;
        mStickersFragment.setSelectedCountryId(isTheOne ? -1 : countryId);
        return true;
    }

    public static final String[] sProjection = new String[]{
            CountriesInfoTable._ID,
            CountriesInfoTable.COLUMN_ABBR,
            CountriesInfoTable.COLUMN_PRIMARY_COLOR,
            CountriesInfoTable.COLUMN_SECONDARY_COLOR,
            CountriesInfoTable.COLUMN_UNIQUE_STICKERS,
            CountriesInfoTable.COLUMN_WITH_DOUBLES_COUNT,
            CountriesInfoTable.COLUMN_ALL_STICKERS,
            CountriesInfoTable.COLUMN_THE_ONE
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cr = new CursorLoader(this, CountriesInfoTable.CONTENT_URI, sProjection, null, null, null);
        cr.setUpdateThrottle(500);
        return cr;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

}
