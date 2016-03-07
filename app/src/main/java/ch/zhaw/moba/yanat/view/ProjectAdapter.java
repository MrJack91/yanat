package ch.zhaw.moba.yanat.view;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.moba.yanat.R;
import ch.zhaw.moba.yanat.domain.model.Project;

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
        CardView cv;
        TextView title;
        TextView create_date;
        TextView tstamp;

        ProjectViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.project_card_view);
            title = (TextView)itemView.findViewById(R.id.project_list_item_title);
            create_date = (TextView)itemView.findViewById(R.id.project_list_item_create_date);
            tstamp = (TextView)itemView.findViewById(R.id.project_list_item_tstamp);
            // personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    @Override
    public ProjectViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.project_list_item, viewGroup, false);
        ProjectViewHolder pvh = new ProjectViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ProjectViewHolder personViewHolder, int i) {
        personViewHolder.title.setText(projects.get(i).getTitle());

        // personViewHolder.create_date.setText(projects.get(i).getCreateDateString());
        // personViewHolder.tstamp.setText(projects.get(i).getTstampString());

        // personViewHolder.personPhoto.setImageResource(projects.get(i).photoId);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
