package ch.zhaw.moba.yanat.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;

/**
 * Created by michael on 07.03.16.
 * src: http://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class PointAdapter extends RecyclerView.Adapter<PointAdapter.PointViewHolder> {

    private final List<Point> points;

    public PointAdapter(List<Point> points){
        this.points = points;
    }

    public static class PointViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        protected Point currentPoint;

        protected PointRepository pointRepository;

        TextView title;
        TextView height;
        TextView comment;
        TextView create_date;
        TextView tstamp;

        PointViewHolder(final View itemView) {
            super(itemView);
            this.view = itemView;

            //TODO 0 -> projectid anpassen
            this.pointRepository = new PointRepository(itemView.getContext(), 0);

            height = (TextView)itemView.findViewById(R.id.input_measure_point_height);
            comment = (TextView)itemView.findViewById(R.id.input_measure_point_comment);
        }

        protected DetailActivity getDetailActivity() {
            return ((DetailActivity)this.view.getContext());
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public PointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v("YANAT", "on create view helper");
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.point_item, parent, false);
        v.setClickable(true);
        PointViewHolder vh = new PointViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(PointViewHolder pointViewHolder, int i) {
        pointViewHolder.currentPoint = points.get(i);

        pointViewHolder.title.setText(points.get(i).getTitle());
        pointViewHolder.comment.setText(points.get(i).getComment());
        pointViewHolder.height.setText(""+points.get(i).getHeight());
        pointViewHolder.create_date.setText("Erstellt: " + points.get(i).getCreateDateString());
        pointViewHolder.tstamp.setText("Bearbeitet: " + points.get(i).getTstampString());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return points.size();
    }

}
