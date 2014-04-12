package mk.jdex.paniniworldcup.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;

import mk.jdex.paniniworldcup.content.StickersTable;
import mk.jdex.paniniworldcup.model.ReportOptions;
import mk.jdex.paniniworldcup.util.App;

public class GenerateReportFragment extends DialogFragment {

    public static GenerateReportFragment newInstance(ReportOptions opt) {
        GenerateReportFragment fr = new GenerateReportFragment();
        fr.mOptions = opt;
        return fr;
    }

    private StringBuilder mReportBuilder = new StringBuilder();
    private Handler mHandler;
    private ReportOptions mOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mHandler = new Handler();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Generating report");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();

        new Thread(mGenerateReportRunnable).start();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mReportBuilder.length() > 0) {
            sendReport();
        }
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setDismissMessage(null);
            dialog.setOnDismissListener(null);
        }

        super.onDestroyView();
    }

    private void sendReport() {
        if (isDetached() || getActivity() == null) {
            return;
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mReportBuilder.toString());
        sendIntent.setType("text/html");
        getActivity().startActivity(sendIntent);

        dismiss();
    }

    private Runnable mGenerateReportRunnable = new Runnable() {
        @Override
        public void run() {
            ContentResolver cr = App.getInstance().getContentResolver();
            /*only equal*/ /*base count*/ /*include count in report*/ /*section name*/
            if (mOptions.showAll)
                appendStickersWithCount(false, 0, false, "All", mReportBuilder);
            if (mOptions.showDoubles)
                appendStickersWithCount(false, 1, true, "Available for trading", mReportBuilder);
            if (mOptions.showMissing)
                appendStickersWithCount(true, 0, false, "Missing", mReportBuilder);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    sendReport();
                }
            });
        }
    };

    /**
     * @param onlyEqual    whether to include only stickers with count equal to {@code baseCount}
     * @param baseCount    the count with which the stickers will be compared
     * @param includeCount whether the count should be part of the report
     * @param sectionName  the name of the section for the report
     * @param builder      the StringBuilder in which the results should be appended
     */
    private void appendStickersWithCount(boolean onlyEqual, int baseCount,
                                         boolean includeCount, String sectionName,
                                         StringBuilder builder) {

        ContentResolver cr = App.getInstance().getContentResolver();
        Uri stickersUri = StickersTable.CONTENT_URI;

        String selection = StickersTable.COLUMN_COUNT + (onlyEqual ? "=?" : ">?");
        String[] selectionArgs = new String[]{String.valueOf(baseCount)};
        String order = "";
        if (mOptions.showGroup) {
            order = StickersTable.COLUMN_ABBREVIATION + " ASC, ";
        }
        order += StickersTable.COLUMN_SIGN + " ASC";

        Cursor c = cr.query(stickersUri, null, selection, selectionArgs, order);

        if (c.moveToFirst()) {
            builder.append("\n");
            builder.append(sectionName);
            builder.append(":\n");

            int abbrCol = c.getColumnIndex(StickersTable.COLUMN_ABBREVIATION);
            int signCol = c.getColumnIndex(StickersTable.COLUMN_SIGN);
            int countCol = c.getColumnIndex(StickersTable.COLUMN_COUNT);

            String lastAbbr = null;

            do {
                String abbr = c.getString(abbrCol);
                int sign = c.getInt(signCol);
                int count = c.getInt(countCol);

                if (mOptions.showGroup && !abbr.equals(lastAbbr)) {
                    if (lastAbbr == null) {
                        builder.append(abbr).append(" (");
                    } else {
                        builder.append(")\n");
                        builder.append(abbr).append(" (");
                    }
                } else if (mOptions.showGroup) {
                    builder.append(", ");
                }

                builder.append(sign);
                if (includeCount) {
                    builder.append("x").append(count - 1);
                }
                if (!mOptions.showGroup) {
                    builder.append(" ");
                }

                lastAbbr = abbr;
            } while (c.moveToNext());
            c.close();

            if (mOptions.showGroup) {
                builder.append(")");
            }
            builder.append("\n");
        }
    }
}
