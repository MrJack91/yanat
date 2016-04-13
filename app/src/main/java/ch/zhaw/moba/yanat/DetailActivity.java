package ch.zhaw.moba.yanat;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
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
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;


import java.io.File;
import java.util.List;

import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;
import ch.zhaw.moba.yanat.view.PointAdapter;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

public class DetailActivity extends AppCompatActivity {

    public ProjectRepository projectRepository = new ProjectRepository(DetailActivity.this);
    public PointRepository pointRepository = null;

    private PointAdapter pointAddapter = null;
    private RecyclerView recyclerView;

    protected Project project = null;
    protected View viewList;
    private List<Point> points;
    private ImageViewTouch  pdfView;

    private android.widget.RelativeLayout.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int projectId = getIntent().getIntExtra("projectId", 0);
        // load all points

        project = projectRepository.findById(projectId);
        Log.v("YANAT", project.toString());
        pointRepository = project.getPointRepository(this);

        showPDfAsImagee();

        final ImageView img = (ImageView) findViewById(R.id.image_view_pin);

        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(),mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(img);

                v.startDrag(dragData,myShadow,null,0);
                return true;
            }
        });

        img.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");

                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(img);
                    img.startDrag(data, shadowBuilder, img, 0);
                    img.setVisibility(View.INVISIBLE);

                    return true;
                } else {
                    return false;
                }
            }
        });


        pdfView.setOnDragListener(new View.OnDragListener() {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        Log.i("YANAT", "Action is DragEvent.ACTION_DRAG_STARTED");

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.i("YANAT", "Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.i("YANAT", "Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        v.setLayoutParams(layoutParams);
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.i("YANAT", "Action is DragEvent.ACTION_DRAG_LOCATION");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.d("YANAT", "Action is DragEvent.ACTION_DRAG_ENDED");

                        // Do nothing
                        break;

                    case DragEvent.ACTION_DROP:
                        Log.i("YANAT", "ACTION_DROP event");
                        Log.i("YANAT", "event.getX(): " + event.getX());
                        Log.i("YANAT", "event.gety(): " + event.getY());

                        int x = (int) event.getX();
                        int y = (int)  event.getY();

                        openPointDialog();
                        // Do nothing
                        break;
                    default:

                        Log.i("YANAT", "Unknown action type received by OnDragListener.");

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
                // Log.v("YANAT", getApplicationInfo().dataDir + "/files/");
                File pdfFile = project.buildPdf(DetailActivity.this);
                Log.v("YANAT", "Build PDF: " + pdfFile.getAbsolutePath());

                Uri fileUri = null;
                // answer with the create file provider
                try {
                    fileUri = FileProvider.getUriForFile(
                            DetailActivity.this,
                            "ch.zhaw.moba.yanat.fileprovider",
                            pdfFile);
                } catch (IllegalArgumentException e) {
                    Log.e("File Selector",
                            "The selected file can't be shared: ");
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

    public void listPoints() {
         points = getPoints();

        recyclerView = (RecyclerView) viewList.findViewById(R.id.point_list);
        recyclerView.setHasFixedSize(true);

        // and a layout manager (needed!)
        LinearLayoutManager llm = new LinearLayoutManager(DetailActivity.this);
        recyclerView.setLayoutManager(llm);

        // add the adapter
        PointAdapter adapter = new PointAdapter(points, pointRepository);
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }

    public void updatePointList(){
        //recyclerView.getAdapter().notifyDataSetChanged();
        listPoints();
    }

    private void fillFieldsWithPoint(View view, Point point){
        ((TextView)view.findViewById(R.id.input_measure_point_comment)).setText(point.getComment());
        ((TextView)view.findViewById(R.id.input_measure_point_height)).setText("" + point.getHeight());
        ((CheckBox)view.findViewById(R.id.ground_floor)).setChecked(point.isGroundFloor());
    }


    private void openPointDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetailActivity.this);
        LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
        viewList = inflater.inflate(R.layout.dialog_point_list, null);
        listPoints();

        FloatingActionButton fab = (FloatingActionButton) viewList.findViewById(R.id.fb_add_measure_point);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Point newPoint = new Point();
                pointRepository.add(newPoint);
                listPoints();
            }
        });

        dialogBuilder.setView(viewList);
        dialogBuilder.setTitle("Messpunkt");
        Log.v("YANAT", "Points size: " + points.size());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    public void showPDfAsImagee(){

        pdfView = (ImageViewTouch) findViewById(R.id.pdf_view);

        try {
            File file = new File(project.getPdf());
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

            PdfRenderer.Page page = renderer.openPage(0);
            Log.v("YANAT", "PDF: " + project.getPdf()+"-"+page.getWidth()+"-"+page.getHeight());
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_4444);

            page.render(bitmap, new Rect(0, 0, page.getWidth(), page.getHeight()), new Matrix(), PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            pdfView.setImageBitmap(bitmap);
            pdfView.invalidate();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private List<Point> getPoints(){

        final List<Point> points = pointRepository.findAll();

        Log.v("YANAT", "Points size: " + points.size());
        for (Point point : points) {
            Log.v("YANAT", point.toString());
        }

        return points;
    }

}
