package ch.zhaw.moba.yanat.view;

import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.moba.yanat.DetailActivity;
import ch.zhaw.moba.yanat.MainActivity;
import ch.zhaw.moba.yanat.R;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;

/**
 * Created by michael on 07.03.16.
 * src: http://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private final List<Project> projects;

    public ProjectAdapter(List<Project> projects){
        this.projects = projects;
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        protected Project currentProject;

        protected ProjectRepository projectRepository;

        TextView title;
        TextView create_date;
        TextView tstamp;

        ProjectViewHolder(final View itemView) {
            super(itemView);
            this.view = itemView;

            this.projectRepository = new ProjectRepository(itemView.getContext());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Integer element = getAdapterPosition();
                    Log.v("YANAT", "clicked on " + currentProject.getTitle() + "(" + element.toString() + ")");
                    // open project
                    getMainActivity().startActivity(new Intent(getMainActivity(), DetailActivity.class));
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    LayoutInflater inflater = LayoutInflater.from(v.getContext());
                    final View view = inflater.inflate(R.layout.dialog_create_project, null);

                    EditText mProjectTitle = (EditText)view.findViewById(R.id.input_project_title);
                    mProjectTitle.setText(currentProject.getTitle());

                    builder.setView(view);
                    builder.setTitle("Projekt bearbeiten");
                    // Add action buttons
                    builder.setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText mProjectName = (EditText)view.findViewById(R.id.input_project_title);
                                String projectTitle = mProjectName.getText().toString();

                                // build project object
                                currentProject.setTitle(projectTitle);
                                projectRepository.update(currentProject);

                                // reload project list
                                getMainActivity().listProjects();
                            }
                        })
                        .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {}
                        })
                        .setNeutralButton("Projekt Löschen", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                projectRepository.delete(currentProject);
                                getMainActivity().listProjects();
                            }
                        })
                    ;

                    AlertDialog dialog = builder.create();
                    dialog.show();

                    return true;
                }
            });
            title = (TextView)itemView.findViewById(R.id.project_list_item_title);
            create_date = (TextView)itemView.findViewById(R.id.project_list_item_create_date);
            tstamp = (TextView)itemView.findViewById(R.id.project_list_item_tstamp);
        }

        protected MainActivity getMainActivity() {
            return ((MainActivity)this.view.getContext());
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_list_item, parent, false);
        v.setClickable(true);
        ProjectViewHolder vh = new ProjectViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder personViewHolder, int i) {
        personViewHolder.currentProject = projects.get(i);

        personViewHolder.title.setText(projects.get(i).getTitle());
        personViewHolder.create_date.setText("Erstellt: " + projects.get(i).getCreateDateString());
        personViewHolder.tstamp.setText("Bearbeitet: " + projects.get(i).getTstampString());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

}
