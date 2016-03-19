package ch.zhaw.moba.yanat;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.moba.yanat.db.ProjectContract;
import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;

public class DetailActivity extends AppCompatActivity {

    public ProjectRepository projectRepository = new ProjectRepository(DetailActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        int projectId = getIntent().getIntExtra("projectId", 0);
        // load all points
        List<Project> projects = projectRepository.findById(projectId);
        PointRepository pointRepository = projects.get(0).getPointRepository(this);

        List<Point> points = pointRepository.findAll();
        int i;
        for (i = 0; i < points.size(); i++) {
            Log.v("YANAT", Float.toString(points.get(i).getHeight()));
        }

        Point point = new Point();
        point.setHeight(i);
        pointRepository.add(point);




        Button button = (Button) findViewById(R.id.create_measure_point);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                LayoutInflater inflater = DetailActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_measure_point, null);


                builder.setView(view);
                builder.setTitle("Messpunkt");
                // Add action buttons
                builder.setPositiveButton("Erstellen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

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

}
