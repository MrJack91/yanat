package ch.zhaw.moba.yanat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;
import ch.zhaw.moba.yanat.paint.MarkerPaint;
import ch.zhaw.moba.yanat.view.PointAdapter;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class DetailActivity extends AppCompatActivity {

    public ProjectRepository projectRepository = new ProjectRepository(DetailActivity.this);
    public PointRepository pointRepository = null;

    private RecyclerView recyclerView;

    protected Project project = null;
    protected View viewList;
    private List<Point> points;
    private ImageViewTouch pdfView;

    private AlertDialog dialog = null;

    private Bitmap pdfBitmap;
    private Bitmap originalEmptyPdfBitmap; // original pdf Bitmap without markers. To use after deleting markers

    private android.widget.RelativeLayout.LayoutParams layoutParams;
    private MarkerPaint markerPaint;

    public static final float POINT_TO_MM = (float) 0.352778;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int projectId = getIntent().getIntExtra("projectId", 0);
        pdfView = (ImageViewTouch) findViewById(R.id.pdf_view);

        project = projectRepository.findById(projectId);
        Log.v("YANAT", project.toString());
        pointRepository = project.getPointRepository(this);

        showPdfAsImage();
        initListener();
    }

    private void initListener() {

        final ImageView marker = (ImageView) findViewById(R.id.image_view_pin);

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

        /*
        // Calc coordinates of touch events
        pdfView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    calcAbsoluteCoord(event.getX(), event.getY());

                    return true;
                } else {
                    return false;
                }
            }
        });
        */

        // set on drag event actions
        pdfView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        float[] pos = calcAbsoluteCoord(event.getX(), event.getY());

                        int posX = (int)pos[0];
                        int posY = (int)pos[1];
                        // only positive values are alowed
                        if (Math.min(posX, posY) > 0) {
                            openPointDialog((int)pos[0], (int)pos[1], pos[2], pos[3]);
                        }

                        break;
                }
                return true;
            }
        });

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
                // generate pdf & open
                File pdfFile = project.buildPdf(DetailActivity.this);

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
                // File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ filename);
                // File file = new File(pdfPath);
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
        });
    }

    public void listPoints(Point topPoint) {
        points = getPoints();

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
    }

    private void drawPoints() {

        pdfBitmap = originalEmptyPdfBitmap.copy(originalEmptyPdfBitmap.getConfig(), true);

        for (Point point : getPoints()) {
            markerPaint.drawMarker(point.getPosX() / POINT_TO_MM, point.getPosY() / POINT_TO_MM, point.getTitle(), pdfBitmap);
        }

        pdfView.setImageBitmap(pdfBitmap);
    }

    public void updatePointList(Point topPoint) {
        //recyclerView.getAdapter().notifyDataSetChanged();
        listPoints(topPoint);
    }

    private void fillFieldsWithPoint(View view, Point point) {
        ((TextView) view.findViewById(R.id.input_measure_point_comment)).setText(point.getComment());
        ((TextView) view.findViewById(R.id.input_measure_point_height)).setText("" + point.getHeight());
        ((CheckBox) view.findViewById(R.id.ground_floor)).setChecked(point.isGroundFloor());
    }

    private void openPointDialog(final int x, final int y, final float scaleX, final float scaleY) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetailActivity.this);
        LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
        viewList = inflater.inflate(R.layout.dialog_point_list, null);

        Log.v("YANAT MH", "x,y = " + Integer.toString(x) + ", " + Integer.toString(y) + "; mm: " + Float.toString(x * POINT_TO_MM) + ", " + Float.toString(y * POINT_TO_MM));
        Point newPoint = createNewPoint((int) (x * POINT_TO_MM), (int) (y * POINT_TO_MM));

        // todo: load only points of this coordinates
        updatePointList(newPoint);
        // listPoints(newPoint);

        FloatingActionButton fab = (FloatingActionButton) viewList.findViewById(R.id.fb_add_measure_point);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Point newPoint = createNewPoint(x, y);
                updatePointList(newPoint);
                // listPoints(newPoint);
            }
        });

        dialogBuilder.setView(viewList);
        dialogBuilder.setTitle("Messpunkt");
        Log.v("YANAT", "Points size: " + points.size());

        markerPaint.drawMarker(newPoint.getPosX(), newPoint.getPosY(), newPoint.getTitle(), pdfBitmap);

        this.dialog = dialogBuilder.create();
        this.dialog.show();

        // fix to show keyboard on input fields on dialogs
        this.dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        this.dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        this.dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                /*
                pdfView.setScaleX(scaleX);
                pdfView.setScaleY(scaleY);
                */
                drawPoints();
                Log.v("YANAT", "setOnCancelListener");
            }
        });
    }

    public void closeDialog() {
        if (this.dialog != null) {
            this.dialog.dismiss();
            this.dialog = null;
        }
    }

    public void showPdfAsImage() {
        pdfView.clear();
        try {

            File file = new File(project.getPdf());

            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

            PdfRenderer.Page page = renderer.openPage(0);

            pdfBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);

            pdfBitmap.setHasAlpha(false);
            // convert alpha channel to white
            Canvas canvas = new Canvas(pdfBitmap);
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(pdfBitmap, 0, 0, null);

            page.render(pdfBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            originalEmptyPdfBitmap = pdfBitmap.copy(pdfBitmap.getConfig(), true);

            markerPaint = new MarkerPaint(getResources(), getApplicationContext(), pdfView);

            // set the image to view
            drawPoints();

            // pdfView.invalidate();
            // renderer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Point createNewPoint(final int x, final int y) {
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

    protected float[] calcAbsoluteCoord(float posX, float posY) {
        Matrix m = pdfView.getImageMatrix();
        float []mf = new float[9];
        m.getValues(mf);

        float transX = mf[Matrix.MTRANS_X] * -1;
        float transY = mf[Matrix.MTRANS_Y] * -1;
        float scaleX = mf[Matrix.MSCALE_X];
        float scaleY = mf[Matrix.MSCALE_Y];
        int lastTouchX = (int) ((posX + transX) / scaleX);
        // add + 60 for pin offset
        int lastTouchY = (int) ((posY + transY + 90) / scaleY);

        Log.i("YANAT", "event.getX(): " + posX + ", event.getY(): " + posY + ", transX: " + transX + ", transY: " + transY + ", scaleX: " + scaleX + ", scaleY: " + scaleY + " lastTouchX:" + lastTouchX + " lastTouchY:" + lastTouchY);

        float[] returnValues = {lastTouchX, lastTouchY, scaleX, scaleY};
        return returnValues;
    }

}
