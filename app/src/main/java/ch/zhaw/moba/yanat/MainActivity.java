package ch.zhaw.moba.yanat;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // ProjectDbHelper mDbHelper = new ProjectDbHelper(MainActivity.this);
    ProjectRepository projectRepository = new ProjectRepository(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // print all current projects
        String projectList = "";
        ArrayList<Project> projects = projectRepository.findAll();
        for(Project project : projects){
            projectList += project.getTitle() + " (" + project.getId() + ")\n";
        }

        TextView content = (TextView) findViewById(R.id.contentList);
        content.setText(projectList);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(MainActivity.this, DetailActivity.class));

                // prefill
                /*
                EditText mProjectName = (EditText)findViewById(R.id.input_project_name);
                mProjectName.setText(projectTitle);
                */

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                builder.setView(inflater.inflate(R.layout.dialog_create_project, null))
                    .setTitle("Projektdetails")
                    // Add action buttons
                    .setPositiveButton("Erstellen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                        /*
                        // todo: set real project name
                        EditText mProjectName = (EditText)findViewById(R.id.input_project_name);
                        String projectName = mProjectName.getText().toString();
                        */
                            String projectName = "Projekt 1! :D";

                            // build project object
                            // Project project = new Project(projectName);

                            // open project
                            // startActivity(new Intent(MainActivity.this, DetailActivity.class));
                        }
                    })
                    .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // MainActivity.this.getDialog().cancel();
                            // LoginDialogFragment.this.getDialog().cancel();
                        }
                    })
                ;

                /*
                builder.setMessage("hallo liebe welt")
                        .setTitle("hallo")
                */

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
