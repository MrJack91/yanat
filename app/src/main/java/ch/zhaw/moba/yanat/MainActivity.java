package ch.zhaw.moba.yanat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.EditText;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;
import ch.zhaw.moba.yanat.view.ProjectAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Intent mRequestFileIntent;
    private AnalyticsTrackers analyticsTrackers = null;
    private ProjectAdapter adapter = null;
    final int PICKFILE_RESULT_CODE = 1712;

    public ProjectRepository projectRepository = new ProjectRepository(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // analyticsTrackers.initialize(MainActivity.this);
        // -> throw error if screen rotated (reinit of analytics)

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // open file dialog
                mRequestFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mRequestFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                // mRequestFileIntent.setType(Intent.normalizeMimeType("application/pdf")); // application/pdf | */*
                mRequestFileIntent.setType(Intent.normalizeMimeType("*/*")); // application/pdf | */*
                startActivityForResult(mRequestFileIntent, PICKFILE_RESULT_CODE);
            }
        });

        this.listProjects();

    }

    public void listProjects() {
        // print all current projects
        List<Project> projects = projectRepository.findAll();

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
            if (requestCode == PICKFILE_RESULT_CODE) {
                // Get the file's content URI from the incoming Intent
                Uri returnUri = data.getData();

                // read meta data
                Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                // int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                returnCursor.moveToFirst();
                String filename = returnCursor.getString(nameIndex);
                // Long filesize = returnCursor.getLong(sizeIndex);

                // handle paths
                String src = returnUri.getPath();
                File source = new File(src);

                Log.d("src is ", source.toString());
                Log.d("FileName is ", filename);

                File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/yanat/temp/" + filename);
                // check if dictionary exists
                directoryExist(destination.getParentFile());

                try {
                    destination.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("Destination is ", destination.toString());
                Log.d("Destination Parent is ", destination.getParent().toString());


                try {
                    InputStream inputStream = getContentResolver().openInputStream(returnUri);
                    FileOutputStream outputStream = new FileOutputStream(destination);
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

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
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText mProjectName = (EditText) view.findViewById(R.id.input_project_title);
                        String projectTitle = mProjectName.getText().toString();

                        // build project object
                        Project project = new Project();
                        project.setTitle(projectTitle);
                        projectRepository.add(project);

                        // reload project list
                        listProjects();
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
        }
    }

    private FileDescriptor getMediaFileDescriptor(Uri outputUri) {
        try {
            return MainActivity.this.getContentResolver().openFileDescriptor(outputUri, "w").getFileDescriptor();
        } catch (FileNotFoundException e) {
            Log.e("YANAT", ".getMediaFileDescriptor() - Error opening media output stream", e);
        }
        return null;
    }


    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            Log.e("YANAT", "Source file not exists");
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }



    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /*
    private void copy(File source, File destination) throws IOException {

        Log.d("YANAT", "inside copy()");

        FileChannel in = new FileInputStream(source).getChannel();
        Log.d("YANAT", "inside 2 copy()");
        FileChannel out = new FileOutputStream(destination).getChannel();

        Log.d("YANAT", "Size: " + Long.toString(in.size()));

        try {
            in.transferTo(0, in.size(), out);
        } catch(Exception e){
            Log.d("Exception", e.toString());
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }

        Log.d("YANAT", "at ent copy()");

    }
    */

    private void directoryExist(File destination) {
        if (!destination.isDirectory()) {
            if (destination.mkdirs()) {
                Log.d("Carpeta creada", "....");
            } else {
                Log.d("Carpeta no creada", "....");
            }
        }
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
}
