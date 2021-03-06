package ch.zhaw.moba.yanat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ch.zhaw.moba.yanat.db.ProjectContract;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;
import ch.zhaw.moba.yanat.utility.FileUtility;
import ch.zhaw.moba.yanat.utility.PdfGeneratorUtility;
import ch.zhaw.moba.yanat.view.ProjectAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Intent mRequestFileIntent;
    // private AnalyticsTrackers analyticsTrackers = null;
    private ProjectAdapter adapter = null;

    private static final int PICK_FILE_RESULT_CODE = 1712;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 3;
    public static final String PDF_SORT_ORDER = "PDF_SORT_ORDER";

    private SharedPreferences settings;

    public String sortOrder = null;

    // String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
    String[] perms = {};

    public ProjectRepository projectRepository = new ProjectRepository(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(PDF_SORT_ORDER, 0);

        // create mock object for testing purposes

        /*
        ExampleProject exampleProject = new ExampleProject(this);
        exampleProject.create();
        */

        // analyticsTrackers.initialize(MainActivity.this);
        // -> throw error if screen rotated (reinit of analytics)

        // uncomment if brakes on pre marshmallows devices
        // if (this.canMakeSmores()) {
        // request all permissions on start -> for android 6
        // checkPermissions();
        // }


        // Show About Dialog
        final NavigationView navMenu = (NavigationView) findViewById(R.id.nav_view);
        MenuItem aboutItem = (MenuItem) navMenu.getMenu().findItem(R.id.nav_about);
        final   DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_layout);

        aboutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_about, null);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                item.setChecked(false);
                                dl.closeDrawers();
                            }
                        }
                );

                builder.setView(view);
                builder.setTitle("About");

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }

        });


        // Show Sort Dialog
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(
                new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getItemId() == R.id.action_sort_order) {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                            final View view = inflater.inflate(R.layout.dialog_sort_projects, null);

                            // Radiobutton vorselektieren
                            sortOrder = settings.getString("sortOrder", ProjectContract.ProjectEntry.COLUMN_NAME_TSTAMP);
                            Log.v("YANAT", " get sortOrder: " +sortOrder);

                            switch (sortOrder) {
                                case ProjectContract.ProjectEntry.COLUMN_NAME_TITLE+ " ASC":
                                    ((RadioButton) view.findViewById(R.id.sort_radio_button_name)).setChecked(true);
                                    break;
                                case ProjectContract.ProjectEntry.COLUMN_NAME_TSTAMP+ " DESC":
                                    ((RadioButton) view.findViewById(R.id.sort_radio_button_bearbeitungsdatum)).setChecked(true);
                                    break;
                                case ProjectContract.ProjectEntry.COLUMN_NAME_CREATE_DATE+ " ASC":
                                    ((RadioButton) view.findViewById(R.id.sort_radio_button_erstelldatum)).setChecked(true);
                                    break;
                            }

                            builder.setPositiveButton("Sortieren", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                            RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.sort_radio_group);

                                            int checkedId = radioGroup.getCheckedRadioButtonId();

                                            switch (checkedId) {
                                                case R.id.sort_radio_button_name:
                                                    sortOrder = ProjectContract.ProjectEntry.COLUMN_NAME_TITLE + " ASC";
                                                    break;
                                                case R.id.sort_radio_button_bearbeitungsdatum:
                                                    sortOrder = ProjectContract.ProjectEntry.COLUMN_NAME_TSTAMP+ " DESC";
                                                    break;
                                                case R.id.sort_radio_button_erstelldatum:
                                                    sortOrder = ProjectContract.ProjectEntry.COLUMN_NAME_CREATE_DATE + " ASC";
                                                    break;
                                            }

                                            Log.v("YANAT", "save sortOrder: " +sortOrder);

                                            SharedPreferences.Editor editor = settings.edit();
                                            editor.putString("sortOrder", sortOrder);
                                            // Commit the edits
                                            editor.commit();


                                            listProjects();
                                        }
                                    }
                            );

                            builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    }
                            );

                            builder.setView(view);
                            builder.setTitle("Sortieren");

                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                        return false;
                    }
                }

        );


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()

                               {
                                   @Override
                                   public void onClick(View v) {
                                       // open file dialog
                                       mRequestFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                       mRequestFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                                       // mRequestFileIntent.setType(Intent.normalizeMimeType("application/pdf")); // application/pdf | */*
                                       mRequestFileIntent.setType(Intent.normalizeMimeType("*/*")); // application/pdf | */*
                                       startActivityForResult(mRequestFileIntent, PICK_FILE_RESULT_CODE);
                                   }
                               }

        );

        this.listProjects();

    }


    public void listProjects() {
        // print all current projects
        List<Project> projects = projectRepository.find("", null, sortOrder, null);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.project_list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(llm);

        // ProjectAdapter adapter = new ProjectAdapter(projects);
        // if (adapter == null) {
        adapter = new ProjectAdapter(projects);
        mRecyclerView.setAdapter(adapter);
        /*
        } else {
            // will update, but not resort new by edit
            adapter.notifyDataSetChanged();
        }
        */
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the selection didn't work
        if (resultCode != RESULT_OK) {
            // Exit without doing anything else
            return;
        } else {
            if (requestCode == PICK_FILE_RESULT_CODE) {
                // Get the file's content URI from the incoming Intent
                Uri returnUri = data.getData();

                // read meta data
                Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                // int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String filename = returnCursor.getString(nameIndex);
                // Long filesize = returnCursor.getLong(sizeIndex);

                // open create project dialog
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                final View view = inflater.inflate(R.layout.dialog_create_project, null);

                // view.findViewById()
                EditText mProjectTitle = (EditText) view.findViewById(R.id.input_project_title);
                // cut file extension
                mProjectTitle.setText(filename.substring(0, filename.length() - 4));

                builder.setView(view);
                builder.setTitle("Projektdetails");
                // Add action buttons
                builder.setPositiveButton("Erstellen", new DialogInterface.OnClickListener() {
                    protected Uri returnUri;
                    protected String filename;

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText mProjectName = (EditText) view.findViewById(R.id.input_project_title);
                        String projectTitle = mProjectName.getText().toString();

                        // build project object
                        Project project = new Project(projectRepository);
                        project.setTitle(projectTitle);
                        projectRepository.add(project);

                        // save & add pdf path
                        FileUtility fileUtility = new FileUtility(MainActivity.this);
                        File newPdf = fileUtility.saveFile(this.returnUri, project.getId() + "/" + this.filename);
                        project.setPdf(newPdf.getAbsolutePath());

                        // get&set size of pdf -> Inspecting PDFs
                        PdfReader reader = null;
                        try {
                            reader = new PdfReader(newPdf.getAbsolutePath());
                            Rectangle mediabox = reader.getPageSize(1);

                            project.setPdfWidth((int) (mediabox.getRight() * PdfGeneratorUtility.POINT_TO_MM));
                            project.setPdfHeight((int) (mediabox.getTop() * PdfGeneratorUtility.POINT_TO_MM));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        int projectId = (int)projectRepository.update(project);

                        // reload project list, if user comes back from detail
                        listProjects();

                        // switch to detail view
                        Intent i = new Intent(MainActivity.this, DetailActivity.class);
                        i.putExtra("projectId", projectId);
                        MainActivity.this.startActivity(i);
                    }

                    // example to bind vars to function
                    public DialogInterface.OnClickListener init(Uri returnUri, String filename) {
                        this.returnUri = returnUri;
                        this.filename = filename;
                        return this;
                    }

                }.init(returnUri, filename));

                builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }
                );

                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }
    }

    /*
    protected void checkPermissions() {
        // asking for multiple permissions:
        // src: http://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
        // src: https://www.captechconsulting.com/blogs/runtime-permissions-best-practices-and-how-to-gracefully-handle-permission-removal
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                // ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions(this, this.perms, MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
            }
        }
    }

    // permissions handling
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    */

    // menu&settings handling
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

        /*
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}
