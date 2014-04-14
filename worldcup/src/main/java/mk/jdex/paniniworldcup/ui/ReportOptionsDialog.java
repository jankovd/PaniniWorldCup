package mk.jdex.paniniworldcup.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import mk.jdex.paniniworldcup.model.ReportOptions;

public class ReportOptionsDialog extends DialogFragment {

    private static final String STATE_OPTION = "state_report_options";

    public interface OnOptionsSelected {
        void onOptionsSelected(ReportOptions opt);
    }

    private CharSequence[] mItemTitles = new CharSequence[]{
            "Group stickers", "Show collected",
            "Show duplicates", "Show missing"
    };
    private boolean[] mItemsCheked;
    private OnOptionsSelected mListener;
    private ReportOptions mOptions;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof OnOptionsSelected)) {
            throw new IllegalArgumentException("The calling activity must implement " +
                    "OnOptionsSelected interface");
        }

        mListener = (OnOptionsSelected) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mOptions = (ReportOptions) savedInstanceState.getSerializable(STATE_OPTION);
        } else {
            mOptions = new ReportOptions();
            mOptions.showGroup = true;
            mOptions.showAll = true;
            mOptions.showMissing = true;
            mOptions.showDoubles = true;
        }
        mItemsCheked = new boolean[]{
                mOptions.showGroup, mOptions.showAll,
                mOptions.showDoubles, mOptions.showMissing
        };
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Report Options:");
        builder.setMultiChoiceItems(mItemTitles, mItemsCheked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                switch (i) {
                    case 0:
                        mOptions.showGroup = b;
                        break;
                    case 1:
                        mOptions.showAll = b;
                        break;
                    case 2:
                        mOptions.showDoubles = b;
                        break;
                    case 3:
                        mOptions.showMissing = b;
                        break;
                }
            }
        });
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!mOptions.showAll && !mOptions.showMissing && !mOptions.showDoubles) {
                    Toast.makeText(getActivity(), "At least one stickers group should be selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                mListener.onOptionsSelected(mOptions);
            }
        });
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_OPTION, mOptions);
    }

    @Override
    public void onDestroy() {
        mListener = null;
        super.onDestroy();
    }
}
