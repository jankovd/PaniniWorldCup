package mk.jdex.paniniworldcup;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Dejan on 4/6/2014.
 */
public class ColorCalc extends Activity {

    EditText mColorR;
    EditText mColorG;
    EditText mColorB;
    TextView mColorRGB;
    ColorDrawable mABBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        mColorR = (EditText) findViewById(R.id.et_r);
        mColorG = (EditText) findViewById(R.id.et_g);
        mColorB = (EditText) findViewById(R.id.et_b);
        mColorRGB = (TextView) findViewById(R.id.tv_rgb);

        mABBackground = new ColorDrawable(Color.WHITE);
        mABBackground.setCallback(new Drawable.Callback() {
            @Override
            public void invalidateDrawable(Drawable drawable) {
                getActionBar().setBackgroundDrawable(drawable);
            }

            @Override
            public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {

            }

            @Override
            public void unscheduleDrawable(Drawable drawable, Runnable runnable) {

            }
        });

        getActionBar().setBackgroundDrawable(mABBackground);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayShowHomeEnabled(false);
    }

    public void onClick(View v) {
        int r = Integer.valueOf(mColorR.getText().toString());
        int g = Integer.valueOf(mColorG.getText().toString());
        int b = Integer.valueOf(mColorB.getText().toString());

        int rgb = (255 << 24) | (r << 16) | (g << 8) | b;
        mColorRGB.setText("" + rgb);
        mABBackground.setColor(rgb);
    }
}
