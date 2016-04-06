package ch.zhaw.moba.yanat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;
import ch.zhaw.moba.yanat.view.PointAdapter;
import ch.zhaw.moba.yanat.view.ProjectAdapter;

public class DetailActivity extends AppCompatActivity {

    public ProjectRepository projectRepository = new ProjectRepository(DetailActivity.this);
    public PointRepository pointRepository = null;

    private PointAdapter pointAddapter = null;
    private RecyclerView recyclerView;

    protected Project project = null;
    protected View viewList;
    private List<Point> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        int projectId = getIntent().getIntExtra("projectId", 0);
        // load all points

        List<Project> projects = projectRepository.findById(projectId);
        project = projects.get(0);
        pointRepository = project.getPointRepository(this);


        String pdfPath = project.buildPdf();
        Log.v("YANAT", "Build PDF: " + pdfPath);

        //  final Point point = new Point();
        //  point.setHeight(i);
        //  pointRepository.add(point);

        Button createMeasure = (Button) findViewById(R.id.create_measure_point);


        createMeasure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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

        Log.v("YANAT", "viewList: " + viewList);
        Log.v("YANAT", "recyclerView: " + recyclerView);
        Log.v("YANAT", "llm: " + llm);
    }


    public void updatePointList(){
        recyclerView.getAdapter().notifyDataSetChanged();
        listPoints();
    }


    private void fillFieldsWithPoint(View view, Point point){
        ((TextView)view.findViewById(R.id.input_measure_point_comment)).setText(point.getComment());
        ((TextView)view.findViewById(R.id.input_measure_point_height)).setText("" + point.getHeight());

        //TODO Dropdownliste auffüllen

        ArrayAdapter<Point> adapter = new ArrayAdapter<Point>(
                this, android.R.layout.simple_spinner_item, getPoints());

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((Spinner) view.findViewById(R.id.spinner_measure_point_reference_point)).setAdapter(adapter);



        ((CheckBox)view.findViewById(R.id.ground_floor)).setChecked(point.isGroundFloor());
        ((CheckBox)view.findViewById(R.id.meter_above_sea)).setChecked(point.isAbsolute());
    }

    private List<Point> getPoints(){

        final List<Point> points = pointRepository.findAll();
        int i;

        for (i = 0; i < points.size(); i++) {
            Log.v("YANAT", Float.toString(points.get(i).getHeight()));
        }

        return points;
    }

}
