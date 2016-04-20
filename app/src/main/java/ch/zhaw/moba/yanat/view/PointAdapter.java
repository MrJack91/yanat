package ch.zhaw.moba.yanat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ch.zhaw.moba.yanat.DetailActivity;
import ch.zhaw.moba.yanat.R;
import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;

/**
 * Created by michael on 07.03.16.
 * src: http://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class PointAdapter extends RecyclerView.Adapter<PointAdapter.PointViewHolder> {

    private final List<Point> points;
    private PointRepository pointRepository;


    public PointAdapter(List<Point> points, PointRepository pointRepository){
        this.points = points;
        this.pointRepository = pointRepository;
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

        PointViewHolder(final View itemView, PointRepository pointRepository1) {
            super(itemView);
            this.view = itemView;

            final PointRepository pointRepository = pointRepository1;

            title = (TextView)itemView.findViewById(R.id.mesure_point_name_show);
            height = (TextView)itemView.findViewById(R.id.input_measure_point_height);
            comment = (TextView)itemView.findViewById(R.id.input_measure_point_comment);


            ((Button)itemView.findViewById(R.id.button_delete_measure_point)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pointRepository.delete(currentPoint);
                    getDetailActivity().updatePointList();
                }
            });


            ((Button)itemView.findViewById(R.id.button_save_measure_point)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pointRepository.update(currentPoint);

                }
            });
        }

        protected DetailActivity getDetailActivity() {
            return ((DetailActivity)this.view.getContext());
        }

    }

    // Create new views (invoked by the layout manager)
    @Override
    public PointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.point_item, parent, false);
        v.setClickable(true);
        PointViewHolder vh = new PointViewHolder(v, pointRepository);
        return vh;
    }

    @Override
    public void onBindViewHolder(PointViewHolder pointViewHolder, int i) {
        pointViewHolder.currentPoint = points.get(i);

        pointViewHolder.title.setText(points.get(i).getTitle());
        pointViewHolder.comment.setText(points.get(i).getComment());
        pointViewHolder.height.setText("" + points.get(i).getHeight());

        /*
        pointViewHolder.create_date.setText("Erstellt: " + points.get(i).getCreateDateString());
        pointViewHolder.tstamp.setText("Bearbeitet: " + points.get(i).getTstampString());
        */
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
