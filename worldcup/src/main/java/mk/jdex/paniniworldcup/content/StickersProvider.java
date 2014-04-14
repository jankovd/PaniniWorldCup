package mk.jdex.paniniworldcup.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;


public class StickersProvider extends ContentProvider {

    public static final String AUTHORITY = "mk.jankovd.provider.paniniworldcup";
    private static final String TAG = "StickersProvider";
    private static final int CODE_STICKERS = 1;
    private static final int CODE_STICKERS_ID = 2;
    private static final int CODE_COUNTRIES = 3;
    private static final SimpleDateFormat sDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private final UriMatcher mUriMatcher;
    private DatabaseHelper mDbHelper;

    /**
     * debug
     * public static SQLiteDatabase sDatabase;
     */

    public StickersProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, StickersTable.TABLE_NAME, CODE_STICKERS);
        mUriMatcher.addURI(AUTHORITY, StickersTable.TABLE_NAME + "/#", CODE_STICKERS_ID);
        mUriMatcher.addURI(AUTHORITY, CountriesInfoTable.TABLE_NAME, CODE_COUNTRIES);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String order) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (mUriMatcher.match(uri)) {
            case CODE_COUNTRIES:
                qb.setTables(CountriesInfoTable.TABLE_NAME);
                break;
            case CODE_STICKERS:
                qb.setTables(StickersTable.VIEW_NAME);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        if (!mDbHelper.open()) {
            Log.wtf(TAG, "An error occurred, db cannot be opened");
            return null;
        }

        Cursor c = qb.query(mDbHelper.mDatabase, projection, selection, selectionArgs, null, null, order);
        // everyone should be notified when the stickers table is modified
        c.setNotificationUri(getContext().getContentResolver(), StickersTable.CONTENT_URI);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (mUriMatcher.match(uri) != CODE_STICKERS_ID) {
            throw new IllegalArgumentException("Unsupported URI " + uri);
        }

        if (!mDbHelper.open()) {
            Log.wtf(TAG, "An error occurred, db cannot be opened");
            return 0;
        }

        String finalWhere = DatabaseUtils.concatenateWhere(
                StickersTable._ID + " = " + ContentUris.parseId(uri), selection);
        int count = mDbHelper.mDatabase.update(StickersTable.TABLE_NAME, values, finalWhere, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case CODE_STICKERS:
                return StickersTable.CONTENT_TYPE;
            case CODE_COUNTRIES:
                return CountriesInfoTable.CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unsupported URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("Unsupported operation: insert");
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException("Unsupported operation: delete");
    }

    public class DatabaseHelper extends SQLiteOpenHelper {

        protected static final String TAG = "DatabaseHelper";

        private static final String DATABASE_NAME = "worldcup_stickers.sqlite";

        private static final int DATABASE_VERSION = 1;

        protected SQLiteDatabase mDatabase;

        public DatabaseHelper() {
            super(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        private String getDatabasePath(String dbName) {
            return getContext().getDatabasePath(dbName).getAbsolutePath();
        }

        /**
         * Creates a empty database on the system and rewrites it with your own
         * database.
         *
         * @return true database is created successfully, false if it doesn't
         * @throws IOException
         */
        private void createDatabase() throws IOException {

            boolean dbExist = checkDatabase();

            if (!dbExist) {
                // By calling this method and empty database will be created into
                // the default system path
                // of your application so we are going to be able to overwrite that
                // database with our database.
                this.getReadableDatabase();

                try {
                    Log.i(TAG, "Copying database");
                    copyDatabase();
                } catch (IOException e) {
                    Log.i(TAG, "Error copying database");
                    throw e;
                }
            }
        }

        /**
         * Check if the database already exist to avoid re-copying the file each
         * time you open the application.
         *
         * @return true if it exists, false if it doesn't
         */
        private boolean checkDatabase() {

            SQLiteDatabase checkDB = null;

            try {
                String dbPath = getDatabasePath(DATABASE_NAME);
                checkDB = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);

            } catch (SQLiteException e) {
                // database does't exist yet.
            }

            if (checkDB != null) {
                checkDB.close();
            }

            return checkDB != null ? true : false;
        }

        /**
         * Copies your database from your local assets-folder to the just created
         * empty database in the system folder, from where it can be accessed and
         * handled. This is done by transferring bytestream.
         */
        private void copyDatabase() throws IOException {

            // Open your local db as the input stream
            InputStream myInput = getContext().getAssets().open(DATABASE_NAME);

            // Path to the just created empty db
            String outFileName = getDatabasePath(DATABASE_NAME);

            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }

        public boolean open() {
            try {
                createDatabase();
                // Open the database
                String dbPath = getDatabasePath(DATABASE_NAME);
                mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
                return true;
            } catch (Exception e) {
                Log.i(TAG, "" + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }

        public void close() {
            if (mDatabase != null) {
                mDatabase.close();
            }
        }
    }
}
