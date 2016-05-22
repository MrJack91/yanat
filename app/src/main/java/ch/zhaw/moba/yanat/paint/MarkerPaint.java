package ch.zhaw.moba.yanat.paint;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfRenderer;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.NoSuchElementException;

import ch.zhaw.moba.yanat.DetailActivity;
import ch.zhaw.moba.yanat.R;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * Created by Milijana on 24.04.2016.
 */
public class MarkerPaint {

    private Resources res;
    private Context context;
    private ImageViewTouch pdfView;
    private PdfRenderer renderer;
    private int x;
    private int y;
    private DetailActivity detailActivity;

    public MarkerPaint( Resources res, Context context, ImageViewTouch pdfView, DetailActivity detailActivity) {
        this.res = res;
        this.context = context;
        this.pdfView = pdfView;
        this.detailActivity = detailActivity;
    }

    private void paintLetter(float curX, float curY, String name, Canvas canvas) {
        Paint letterPaint = new Paint();
        Paint circlePaint = new Paint();

        letterPaint.setColor(Color.WHITE);
        letterPaint.setTextSize(24);
        letterPaint.setAntiAlias(true);
        letterPaint.setTextAlign(Paint.Align.CENTER);

        Rect bounds = new Rect();
        letterPaint.getTextBounds(name, 0, name.length(), bounds);

        circlePaint.setColor(ContextCompat.getColor(context, R.color.turquoise));
        circlePaint.setAntiAlias(true);

        canvas.drawCircle(curX + 40, curY - 10 - (bounds.height() / 2), bounds.width() + 5, circlePaint);
        canvas.drawText(name, curX + 40, curY - 10, letterPaint);
    }

    public void drawMarker(float curX, float curY, String name, Bitmap pdfBitmap) {
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.marker_small);
        Canvas canvas = new Canvas(pdfBitmap);
        canvas.drawBitmap(bitmap, curX - 10, curY - 35, new Paint());

        paintLetter(curX, curY, name, canvas);
    }

    public void scaleImage(Bitmap bitmap) throws NoSuchElementException {


        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(250);
        Log.i("YANAT", "original width = " + Integer.toString(width));
        Log.i("YANAT", "original height = " + Integer.toString(height));
        Log.i("YANAT", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("YANAT", "xScale = " + Float.toString(xScale));
        Log.i("YANAT", "yScale = " + Float.toString(yScale));
        Log.i("YANAT", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("YANAT", "scaled width = " + Integer.toString(width));
        Log.i("YANAT", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        //pdfView.setImageDrawable(result);
        pdfView.setImageBitmap(scaledBitmap);

        // Now change ImageView's dimensions to match the scaled image
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) pdfView.getLayoutParams();
        params.width = width;
        params.height = height;
        pdfView.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private int dpToPx(int dp) {
        float density = detailActivity.getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }
}
