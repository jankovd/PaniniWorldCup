package mk.jdex.paniniworldcup;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import fr.nicolaspomepuy.discreetapprate.AppRate;
import fr.nicolaspomepuy.discreetapprate.AppRateTheme;
import fr.nicolaspomepuy.discreetapprate.RetryPolicy;
import mk.jdex.paniniworldcup.content.CountriesInfoTable;
import mk.jdex.paniniworldcup.model.ReportOptions;
import mk.jdex.paniniworldcup.ui.GenerateReportFragment;
import mk.jdex.paniniworldcup.ui.ReportOptionsDialog;
import mk.jdex.paniniworldcup.ui.StickersFragment;
import mk.jdex.paniniworldcup.ui.adapter.CountriesCursorAdapter;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener,
        LoaderManager.LoaderCallbacks<Cursor>, ReportOptionsDialog.OnOptionsSelected {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private CountriesCursorAdapter mAdapter;
    private StickersFragment mStickersFragment;

    private int mSelectedNavItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStickersFragment =
                (StickersFragment) getSupportFragmentManager()
                        .findFragmentByTag(getString(R.string.tag_fragment_stickers_grid));

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();

        // Set up the dropdown list navigation in the action bar.
        mAdapter = new CountriesCursorAdapter(actionBar.getThemedContext(), null);
        actionBar.setListNavigationCallbacks(mAdapter, this);
        actionBar.setDisplayShowHomeEnabled(false);
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (savedInstanceState == null) {
            AppRate.with(MainActivity.this)
                    .text("Like the app? Rate it!")
                    .fromTop(true)
                    .theme(AppRateTheme.DARK)
                    .retryPolicy(RetryPolicy.INCREMENTAL)
                    .initialLaunchCount(3)
                    .delay(1500)
                    .checkAndShow();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            mSelectedNavItem = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);

            if (getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST) {
                getSupportActionBar().setSelectedNavigationItem(mSelectedNavItem);
                mSelectedNavItem = -1;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
        if (id == R.id.action_export) {
            new ReportOptionsDialog().show(getSupportFragmentManager(), "report_options_dialog");
            return true;
        } else if (id == R.id.action_share_app) {
            shareApp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareApp() {
        String shareUrl = "https://play.google.com/store/apps/details?id=" + getPackageName();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareUrl);
        shareIntent.setType("text/plain");
        if(getPackageManager().resolveActivity(shareIntent, 0) != null) {
            startActivity(shareIntent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        int colCountryId = mAdapter.getColCountryId();
        int colHasStickers = mAdapter.getColHasStickers();
        Cursor c = ((Cursor) mAdapter.getItem(position));
        int countryId = c.getInt(colCountryId);
        boolean hasStickers = c.getInt(colHasStickers) == 1;
        mStickersFragment.setSelectedCountryId(hasStickers ? StickersFragment.STICKERS_NO_FILTER : countryId);
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
            CountriesInfoTable.COLUMN_HAS_STICKERS
    };

    private void showAbListNavigation(boolean showList) {
        final ActionBar actionBar = getSupportActionBar();
        if (showList && actionBar.getNavigationMode() != ActionBar.NAVIGATION_MODE_LIST) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            if (mSelectedNavItem != -1) {
                actionBar.setSelectedNavigationItem(mSelectedNavItem);
                mSelectedNavItem = -1;
            }
        } else if (!showList) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cr = new CursorLoader(this, CountriesInfoTable.CONTENT_URI, sProjection, null, null, null);
        cr.setUpdateThrottle(500);
        return cr;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        showAbListNavigation(data != null);
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        showAbListNavigation(false);
        mAdapter.changeCursor(null);
    }

    @Override
    public void onOptionsSelected(ReportOptions opt) {
        GenerateReportFragment.newInstance(opt).show(getSupportFragmentManager(), "generate_report_dialog");
    }
}
