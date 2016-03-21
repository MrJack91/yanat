package ch.zhaw.moba.yanat;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
    private PointAdapter pointAddapter = null;
    private RecyclerView recyclerView;

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


        Button createMeasure = (Button) findViewById(R.id.create_measure_point);
        createMeasure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DetailActivity.this);

                LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
                final View viewList = inflater.inflate(R.layout.dialog_point_list, null);

                   //and a layout manager (needed!)
                LinearLayoutManager llm = new LinearLayoutManager(DetailActivity.this);
                recyclerView = (RecyclerView) viewList.findViewById(R.id.point_list);
                recyclerView.setLayoutManager(llm);

                List<Point> points = getPoints();

                //add the adapter
                PointAdapter adapter = new PointAdapter(points);
                recyclerView.setAdapter(adapter);

                dialogBuilder.setView(recyclerView);
                dialogBuilder.setTitle("Messpunkt");
                Log.v("YANAT", "Points size: " + points.size());

                AlertDialog dialog = dialogBuilder.create();
                dialog.show();
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
