package mk.jdex.paniniworldcup;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import mk.jdex.paniniworldcup.content.StickersProvider;
import mk.jdex.paniniworldcup.content.StickersTable;

/**
 * Created by Dejan on 4/6/2014.
 */
public class DbInsert extends Activity {

    EditText mGroupId;
    EditText mFromIndex;
    EditText mToIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbinsert);

        mGroupId = (EditText) findViewById(R.id.et_r);
        mFromIndex = (EditText) findViewById(R.id.et_g);
        mToIndex = (EditText) findViewById(R.id.et_b);

        getActionBar().setDisplayShowTitleEnabled(false);
    }

    public void onClick(View v) {
        int group = Integer.valueOf(mGroupId.getText().toString());
        int from = Integer.valueOf(mFromIndex.getText().toString());
        int to = Integer.valueOf(mToIndex.getText().toString());

        ContentValues values = new ContentValues();
        values.put(StickersTable.COLUMN_COUNTRY_ID, group);
        values.put(StickersTable.COLUMN_COUNT, 0);

        SQLiteDatabase db = null;//StickersProvider.sDatabase;
        for (int i = from; i <= to; i++) {
            values.put(StickersTable.COLUMN_DISPLAY_NAME, String.valueOf(i));
            values.put(StickersTable.COLUMN_SIGN, i);

            db.insert(StickersTable.TABLE_NAME, null, values);
        }

        Toast.makeText(this, "Insert successful", Toast.LENGTH_SHORT).show();
    }
}
