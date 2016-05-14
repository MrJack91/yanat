package ch.zhaw.moba.yanat.paint;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;

import ch.zhaw.moba.yanat.R;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

/**
 * Created by Milijana on 24.04.2016.
 */
public class MarkerPaint {

    private Resources res;
    private Context context;
    private ImageViewTouch pdfView;

    public MarkerPaint(Resources res, Context context, ImageViewTouch pdfView) {
        this.res = res;
        this.context = context;
        this.pdfView = pdfView;
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
        Canvas canvas = new Canvas(pdfBitmap);

        // 1. Variante: gefüllter Halbkreis mit Dreieck
        // pin Halbkreis

                       /* canvas.drawArc(curX - 10, curY - 30, curX + 10, curY, 0, (float) -180, true, paint);
                        canvas.drawLine(curX, curY, curX - 10, curY - 20, paint);
                        canvas.drawLine(curX, curY, curX+10, curY-20, paint);
                        canvas.drawPicture(new Picture());
                        pdfView.setImageBitmap(pdfBitmap);*/

        // ----------------------------------------------------------------------------------------

        // 2. Variante: gefüllter Halbkreis mit gefüllter Dreieck
        // ACHTUNG: Dreieck wird nicht ausgefüllt, Lösung nicht gefunden
        // pin Halbkreis
        // canvas.drawArc(curX - 10, curY - 30, curX + 10, curY, 0, (float) -180, true, paint);

        // pin Dreieck, Funktioniert nicht
                       /* Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                        canvas.drawPaint(paint2);

                        paint2.setStrokeWidth(1);
                        paint2.setStyle(Paint.Style.STROKE);
                        paint2.setAntiAlias(true);

                        Path path = new Path();
                        //path.setFillType(Path.FillType.EVEN_ODD);
                        path.moveTo(curX, curY);
                        path.lineTo(curX - 10, curY - 20);
                        path.lineTo(curX+10, curY- 20);
                        path.lineTo(curX, curY);
                        path.close();

                        canvas.drawPath(path, paint2);
                        pdfView.setImageBitmap(pdfBitmap);*/


        // ----------------------------------------------------------------------------------------

        // 3 Variante: Bild hinzufügen
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.marker_small);
        canvas.drawBitmap(bitmap, curX-10, curY-35, null);

        paintLetter(curX, curY, name, canvas);
        pdfView.setImageBitmap(pdfBitmap);
    }
}
