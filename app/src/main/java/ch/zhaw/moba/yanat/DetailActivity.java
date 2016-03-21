package ch.zhaw.moba.yanat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.moba.yanat.db.ProjectContract;
import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;
import ch.zhaw.moba.yanat.view.PointAdapter;

public class DetailActivity extends AppCompatActivity {

    public ProjectRepository projectRepository = new ProjectRepository(DetailActivity.this);
    public PointRepository pointRepository = null;
    private PointAdapter pointAdapter = null;
    protected Project project = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        int projectId = getIntent().getIntExtra("projectId", 0);
        // load all points

        List<Project> projects = projectRepository.find(
                ProjectContract.ProjectEntry.COLUMN_NAME_ID + " LIKE ?",
                new String[]{String.valueOf(projectId)}
        );
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_measure_point, null);

                List<Point> points = getPoints();

                if(points.size()>0){
                    fillFieldsWithPoint(view, points.get(0));
                }

                builder.setView(view);
                builder.setTitle("Messpunkt");

                Log.v("YANAT", "Points size: " + points.size());

                builder.setPositiveButton("Erstellen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText pointComment = (EditText) view.findViewById(R.id.input_measure_point_comment);
                        EditText pointHeight = (EditText) view.findViewById(R.id.input_measure_point_height);
                        CheckBox groundFloor = (CheckBox) view.findViewById(R.id.ground_floor);
                        CheckBox meterAboveSea = (CheckBox) view.findViewById(R.id.meter_above_sea);

                        Point point = new Point();
                        point.setComment(pointComment.getText().toString());
                        // TODO testen ob man Buchstaben eingeben kann
                        point.setHeight(Float.parseFloat(pointHeight.getText().toString()));

                        // point.setTitle("A"); // not needed, will be handled by repository (dynamic name allocation)
                        point.setPosX(1);
                        point.setPosY(1);
                        point.setIsAbsolute(meterAboveSea.isChecked());
                        point.setIsGroundFloor(groundFloor.isChecked());

                        Button save = (Button) findViewById(R.id.save_measure_point_button);
                        pointRepository.add(point);

                        Log.v("YANAT", "Add a point");
                    }
                })
                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                ;

                AlertDialog dialog = builder.create();
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

    private void fillFieldsWithPoint(View view, Point point){
        ((TextView)view.findViewById(R.id.input_measure_point_comment)).setText(point.getComment());
        ((TextView)view.findViewById(R.id.input_measure_point_height)).setText("" + point.getHeight());

        //TODO Dropdownliste auffüllen
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
