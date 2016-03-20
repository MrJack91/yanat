package ch.zhaw.moba.yanat;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.moba.yanat.db.ProjectContract;
import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;
import ch.zhaw.moba.yanat.view.PointAdapter;
import ch.zhaw.moba.yanat.view.ProjectAdapter;

public class DetailActivity extends AppCompatActivity {

    public ProjectRepository projectRepository = new ProjectRepository(DetailActivity.this);
    public PointRepository pointRepository = null;
    private PointAdapter pointAdapter = null;

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
        pointRepository = projects.get(0).getPointRepository(this);



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
                Point lastPoint = points.get(0);
                builder.setView(view);
                builder.setTitle("Messpunkt");


                final int pointsCount = points.size();
                Log.v("YANAT", "Points size: " + pointsCount);


                ((TextView)view.findViewById(R.id.input_measure_point_comment)).setText(lastPoint.getComment());
                ((TextView)view.findViewById(R.id.input_measure_point_height)).setText("" + lastPoint.getHeight());

                // TOOO anschauen
                ((CheckBox)view.findViewById(R.id.ground_floor)).setChecked(lastPoint.isGroundFloor());
                ((CheckBox)view.findViewById(R.id.meter_above_sea)).setChecked(lastPoint.isAbsolute());


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

                        point.setTitle("A");
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
