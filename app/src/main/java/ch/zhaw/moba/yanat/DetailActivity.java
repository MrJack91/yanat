package ch.zhaw.moba.yanat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;
import ch.zhaw.moba.yanat.paint.MarkerPaint;
import ch.zhaw.moba.yanat.utility.FileUtility;
import ch.zhaw.moba.yanat.view.PointAdapter;
//import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class DetailActivity extends AppCompatActivity {

    public ProjectRepository projectRepository = new ProjectRepository(DetailActivity.this);
    public PointRepository pointRepository = null;

    private RecyclerView recyclerView;

    public static final String PDF_EXPORT = "PDF_EXPORT";
    private final static String CACHE_PATH = "/data/data/ch.zhaw.moba.yanat/cache/files/";

    protected Project project = null;
    protected View viewList;
    private ImageViewTouch pdfView;

    private AlertDialog dialog = null;

    private Bitmap pdfBitmap;
    private Bitmap originalEmptyPdfBitmap; // original pdf Bitmap without markers. To use after deleting markers

    private android.widget.RelativeLayout.LayoutParams layoutParams;
    private MarkerPaint markerPaint;

    public static final float POINT_TO_MM = (float) 0.352778;

    protected float lastPosX = 0f;
    protected float lastPosY = 0f;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int projectId = getIntent().getIntExtra("projectId", 0);
        settings = getSharedPreferences(PDF_EXPORT, 0);

        pdfView = (ImageViewTouch) findViewById(R.id.pdf_view);

        project = projectRepository.findById(projectId);
        Log.v("YANAT", project.toString());
        pointRepository = project.getPointRepository(this);
        File file = new File(CACHE_PATH);

        if(!file.exists()){
            file.mkdirs();
        }

        showPdfAsImage();
        initListener();
    }

    private void initListener() {

        final ImageView marker = (ImageView) findViewById(R.id.image_view_pin);

        /*
        marker.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(marker);

                v.startDrag(dragData, myShadow, null, 0);
                return true;
            }
        });
        */

        marker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");

                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(marker);
                    marker.startDrag(data, shadowBuilder, marker, 0);
                    marker.setVisibility(View.VISIBLE);

                    return true;
                } else {
                    return false;
                }
            }
        });


        // set on drag event actions
        pdfView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        float[] pos = calcAbsoluteCoord(event.getX(), event.getY(), 90);
                        openPointDialog(pos, true);

                        break;
                }
                return true;
            }
        });


        // Save event details for later
        pdfView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // motionEvent = event;
                    lastPosX = event.getX();
                    lastPosY = event.getY();
                }
                return false;
            }
        });

        // Calc coordinates of touch events
        pdfView.setSingleTapListener(
                new ImageViewTouch.OnImageViewTouchSingleTapListener() {
                    @Override
                    public void onSingleTapConfirmed() {
                        float[] pos = calcAbsoluteCoord(lastPosX, lastPosY, 90);
                        openPointDialog(pos, false);

                    }
                }
        );


        ImageButton backButton = (ImageButton) findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.finish();
            }
        });

        ImageButton exportButton = (ImageButton) findViewById(R.id.export);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                //show dialog for choosing textsize
                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                LayoutInflater inflater = DetailActivity.this.getLayoutInflater();

                final View view = inflater.inflate(R.layout.dialog_pdfexport_textsize, null);

                final NumberPicker np = (NumberPicker) view.findViewById(R.id.textsize_input);
                np.setMinValue(8);
                np.setMaxValue(30);

                // get last used text size
                final int textsize = settings.getInt("textsize", 12);
                np.setValue(textsize);


                np.setWrapSelectorWheel(true);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                createPdf(np.getValue());
                            }
                        }
                );

                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putInt("textsize", textsize);
                                // Commit the edits
                                editor.commit();
                            }
                        }
                );

                builder.setView(view);
                builder.setTitle("Schriftgröse auswählen");

                AlertDialog dialog = builder.create();
                dialog.show();

            }});
    }

    private void createPdf(int textsize){

        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("textsize", textsize);
        // Commit the edits
        editor.commit();

        // generate pdf & open
        File pdfFile = project.buildPdf(DetailActivity.this, textsize);

        Uri fileUri = null;
        // answer with the create file provider
        try {
            fileUri = FileProvider.getUriForFile(
                    DetailActivity.this,
                    "ch.zhaw.moba.yanat.fileprovider",
                    pdfFile);
        } catch (IllegalArgumentException e) {
            Log.e("File Selector", "The selected file can't be shared: ");
        }

        // open generated pdf
        Intent target = new Intent(Intent.ACTION_VIEW);
        // target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Grant temporary read permission to the content URI
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (fileUri != null) {
            // Put the Uri and MIME type in the result Intent
            // target.setDataAndType(fileUri, "application/pdf");
            target.setDataAndType(fileUri, getContentResolver().getType(fileUri));

            // Set the result
            DetailActivity.this.setResult(Activity.RESULT_OK, target);
        } else {
            target.setDataAndType(null, "");
            DetailActivity.this.setResult(RESULT_CANCELED, target);
        }

        // for explicit request which app should open it
        // Intent intent = Intent.createChooser(target, "Open File");
        try {
            startActivity(target);
        } catch (ActivityNotFoundException e) {
            // Instruct the user to install a PDF reader here, or something
        }
    }

    public List<Point> listPointsInAdapter(List<Point> points, Point topPoint) {
        recyclerView = (RecyclerView) viewList.findViewById(R.id.point_list);
        recyclerView.setHasFixedSize(true);

        // and a layout manager (needed!)
        LinearLayoutManager llm = new LinearLayoutManager(DetailActivity.this);
        recyclerView.setLayoutManager(llm);

        List<Point> allPoints = pointRepository.findAll();
        if (topPoint != null) {
            // add new point at first position
            points.add(0, topPoint);
            try {
                Point topPointTemplate = topPoint.clone();
                topPointTemplate.setTitle("");
                allPoints.add(0, topPointTemplate);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        // add the adapter
        PointAdapter adapter = new PointAdapter(points, allPoints, pointRepository);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        return points;
    }

    private void drawPoints() {

        pdfBitmap = originalEmptyPdfBitmap.copy(originalEmptyPdfBitmap.getConfig(), true);
        Log.v("YANAT", "originalEmptyPdfBitmap " +pdfBitmap);

        for (Point point : getPoints()) {
            markerPaint.drawMarker(point.getPosX() / POINT_TO_MM, point.getPosY() / POINT_TO_MM, point.getTitle(), pdfBitmap);
        }

        pdfView.setImageBitmap(pdfBitmap);

        //pdfView.setScaleType(ImageView.ScaleType.MATRIX);
        pdfView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_IF_BIGGER);
    }

    private void openPointDialog(float[] pos, boolean isNew) {
        final int posX = (int)pos[0];
        final int posY = (int)pos[1];
        float scaleX = (float)pos[2];
        float scaleY = (float)pos[3];
        // only positive values are allowed
        if (Math.min(posX, posY) < 0) {
            return;
        }
        Log.v("YANAT Coords", "x,y = " + Integer.toString(posX) + ", " + Integer.toString(posY) + "; mm: " + Float.toString(posX * POINT_TO_MM) + ", " + Float.toString(posY * POINT_TO_MM));

        // init emtpy point list
        List<Point> points = new ArrayList();
        if (!isNew) {
            // load nearest points by coordinates
            int posXmm = (int)(posX * POINT_TO_MM);
            int posYmm = (int)(posY * POINT_TO_MM);
            Point nearestPoint = pointRepository.findNearestPoint(posXmm, posYmm);
            // check if this point is close enough
            Log.v("YANAT nearestPoint", nearestPoint.toString());
            // nearest point must be maximum x mm away (take account of scale factor)
            Log.v("YANAT", "nearest point: distance (x,y): (" + Math.abs(nearestPoint.getPosX() - posXmm) + ", " + Math.abs(nearestPoint.getPosY() - posYmm) + "); in near: (" + String.valueOf(30f) + " / " + String.valueOf((float)scaleX) + ") = " +  String.valueOf((int)(30f / scaleX)));
            if (nearestPoint != null && Math.max(Math.abs(nearestPoint.getPosX() - posXmm), Math.abs(nearestPoint.getPosY() - posYmm)) < (int)(30f / scaleX)) {
                // search group of nearest point, and add this all
                List<Point> allPoints = getPoints();
                List<List> groupedPoints = pointRepository.groupPointsByCoordinates(allPoints);
                for (List<Point> group : groupedPoints) {
                    for (Point point : group) {
                        if (point.getPosX() == nearestPoint.getPosX() && point.getPosY() == nearestPoint.getPosY()) {
                            points.add(point);
                        }
                    }
                    // break if group was found
                    if (points.size() > 0) {
                        break;
                    }
                }
            } else {
                Log.v("YANAT", "nearest point is not close enough. distance (x,y): (" + Math.abs(nearestPoint.getPosX() - posXmm) + ", " + Math.abs(nearestPoint.getPosY() - posYmm) + ")");
                return;
            }


        }

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetailActivity.this);
        LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
        viewList = inflater.inflate(R.layout.dialog_point_list, null);

        FloatingActionButton fab = (FloatingActionButton) viewList.findViewById(R.id.fb_add_measure_point);

        Point newPoint = null;
        if (isNew) {
            newPoint = createNewPoint((int) (posX * POINT_TO_MM), (int) (posY * POINT_TO_MM));
            fab.hide();
        }

        listPointsInAdapter(points, newPoint);

        // additional new point
        fab.setOnClickListener(new View.OnClickListener() {
            protected List<Point> points;

            @Override
            public void onClick(View v) {
                Point newPoint = createNewPoint(points.get(0).getPosX(), points.get(0).getPosY());
                listPointsInAdapter(points, newPoint);
                FloatingActionButton fab = (FloatingActionButton) viewList.findViewById(R.id.fb_add_measure_point);
                fab.hide();
            }

            // example to bind vars to function
            public View.OnClickListener init(List<Point> points) {
                this.points = points;
                return this;
            }

        }.init(points));


        // });

        dialogBuilder.setView(viewList);
        dialogBuilder.setTitle("Messpunkt");
        Log.v("YANAT", "Points size: " + points.size());

        this.dialog = dialogBuilder.create();
        this.dialog.show();

        // fix to show keyboard on input fields on dialogs
        this.dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);


        // maybe there was a delete -> draw all current points again
        // called by back button (cancel action)
        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                drawPoints();
            }
        });
    }

    public void closeDialog() {
        // after save, draw all points new
        // called manually after save (no cancel)
        drawPoints();
        if (this.dialog != null) {
            this.dialog.dismiss();
            this.dialog = null;
        }
    }

    private String getCacheImagePath(){
        return  CACHE_PATH+"/" + project.getId() + "/" + project.getFileTitle() + ".png";
    }


    public void showPdfAsImage() {
        pdfView.clear();

        if(new File (getCacheImagePath()).exists()){
            originalEmptyPdfBitmap = BitmapFactory.decodeFile(getCacheImagePath());
            Log.v("YANAT", "originalEmptyPdfBitmap " +originalEmptyPdfBitmap);
            markerPaint = new MarkerPaint(getResources(), getApplicationContext(), pdfView, DetailActivity.this);
            drawPoints();
            return;
        }


        try {
            File file = new File(project.getPdf());


            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
            PdfRenderer.Page page = renderer.openPage(0);

            pdfBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            pdfBitmap.setHasAlpha(false);

            Matrix m = pdfView.getImageMatrix();
            Rect rect = new Rect(0, 0, pdfView.getWidth(), pdfView.getHeight());

            page.render(pdfBitmap, rect, m, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            originalEmptyPdfBitmap = pdfBitmap.copy(pdfBitmap.getConfig(), true);
            FileUtility.saveBitmapToFile(originalEmptyPdfBitmap, getCacheImagePath());

            markerPaint = new MarkerPaint(getResources(), getApplicationContext(), pdfView, DetailActivity.this);

            // set the image to view
            drawPoints();

            // pdfView.invalidate();
            // renderer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Point createNewPoint(int x, int y) {
        Point newPoint = new Point();

        newPoint.setTitle("[neuer Punkt]");
        newPoint.setPosX(x);
        newPoint.setPosY(y);

        return newPoint;
    }

    private List<Point> getPoints() {
        final List<Point> points = pointRepository.findAll();

        Log.v("YANAT", "Points size: " + points.size());
        for (Point point : points) {
            Log.v("YANAT", point.toString());
        }

        return points;
    }

    protected float[] calcAbsoluteCoord(float posX, float posY, float pinOffsetY) {
        Matrix m = pdfView.getImageMatrix();
        float []mf = new float[9];
        m.getValues(mf);

        float transX = mf[Matrix.MTRANS_X] * -1;
        float transY = mf[Matrix.MTRANS_Y] * -1;
        float scaleX = mf[Matrix.MSCALE_X];
        float scaleY = mf[Matrix.MSCALE_Y];
        int lastTouchX = (int) ((posX + transX) / scaleX);
        // add + 60 for pin offset
        int lastTouchY = (int) ((posY + transY + pinOffsetY) / scaleY);

        Log.i("YANAT", "event.getX(): " + posX + ", event.getY(): " + posY + ", transX: " + transX + ", transY: " + transY + ", scaleX: " + scaleX + ", scaleY: " + scaleY + " lastTouchX:" + lastTouchX + " lastTouchY:" + lastTouchY);

        float[] returnValues = {lastTouchX, lastTouchY, scaleX, scaleY};
        return returnValues;
    }

}
