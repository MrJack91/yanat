package ch.zhaw.moba.yanat.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.zhaw.moba.yanat.DetailActivity;
import ch.zhaw.moba.yanat.R;
import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.utility.LinkedHashMapAdapter;

/**
 * Created by michael on 07.03.16.
 * src: http://code.tutsplus.com/tutorials/getting-started-with-recyclerview-and-cardview-on-android--cms-23465
 */
public class PointAdapter extends RecyclerView.Adapter<PointAdapter.PointViewHolder> {

    /**
     * All points to display in dialog
     */
    private List<Point> points;

    /**
     * All points of this project -> to create reference dropdown
     */
    private List<Point> allPoints;

    private LinkedHashMap<Integer, String> spinnerRefData;

    private PointRepository pointRepository;
    private ViewGroup view;

    public PointAdapter(List<Point> points, List<Point> allPoints, PointRepository pointRepository) {
        this.points = points;
        this.allPoints = allPoints;
        this.pointRepository = pointRepository;
    }

    public static class PointViewHolder extends RecyclerView.ViewHolder {
        protected View view;
        protected Point currentPoint;

        protected PointRepository pointRepository;

        public TextView title;
        public TextView height;
        public TextView comment;
        public Spinner spinner;
        // public TextView create_date;
        // public TextView tstamp;

        PointViewHolder(final View itemView, PointRepository pointRepository1, LinkedHashMap<Integer, String> spinnerRefData) {
            super(itemView);
            this.view = itemView;

            final PointRepository pointRepository = pointRepository1;

            title = (TextView) itemView.findViewById(R.id.mesure_point_name_show);
            height = (TextView) itemView.findViewById(R.id.input_measure_point_height);
            comment = (TextView) itemView.findViewById(R.id.input_measure_point_comment);
            spinner = (Spinner) itemView.findViewById(R.id.spinner_measure_point_reference_point);
            spinner.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {

                        private LinkedHashMap<Integer, String> spinnerRefData;

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                            Map.Entry item = (Map.Entry) parent.getItemAtPosition(pos);
                            currentPoint.setReferenceId((int) item.getKey());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Another interface callback
                        }

                        public AdapterView.OnItemSelectedListener init(LinkedHashMap<Integer, String> spinnerRefData) {
                            this.spinnerRefData = spinnerRefData;
                            return this;
                        }

                    }.init(spinnerRefData)
            );


            ((Button) itemView.findViewById(R.id.button_delete_measure_point)).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pointRepository.delete(currentPoint);
                            getDetailActivity().updatePointList(null);
                        }
                    }
            );


            ((Button) itemView.findViewById(R.id.button_save_measure_point)).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String height = ((TextView) itemView.findViewById(R.id.input_measure_point_height)).getText().toString();
                            String comment = ((TextView) itemView.findViewById(R.id.input_measure_point_comment)).getText().toString();

                            currentPoint.setHeight(Float.parseFloat(height));
                            currentPoint.setComment(comment);

                            pointRepository.update(currentPoint);
                            getDetailActivity().closeDialog();
                        }
                    }
            );
        }

        protected DetailActivity getDetailActivity() {
            return ((DetailActivity) this.view.getContext());
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.view = parent;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.point_item, parent, false);
        v.setClickable(true);
        PointViewHolder vh = new PointViewHolder(v, this.pointRepository, this.spinnerRefData);
        return vh;
    }

    @Override
    public void onBindViewHolder(PointViewHolder pointViewHolder, int i) {
        Point pointCurrent = points.get(i);
        pointViewHolder.currentPoint = pointCurrent;

        pointViewHolder.title.setText(pointCurrent.getTitle());
        pointViewHolder.comment.setText(pointCurrent.getComment());
        pointViewHolder.height.setText("" + pointCurrent.getHeight());

        spinnerRefData = new LinkedHashMap<>();

        // add empty option
        spinnerRefData.put(0, "");

        int indexOfRef = 0;
        for (Point point : this.allPoints) {
            if (this.allPoints.get(i).getId() != point.getId()) {
                spinnerRefData.put(point.getId(), point.getTitle());
                // search index of current selected item
                if (point.getId() == pointCurrent.getReferenceId()) {
                    indexOfRef = spinnerRefData.size() - 1;
                }
            }
        }

        LinkedHashMapAdapter<Integer, String> adapter = new LinkedHashMapAdapter<>(
                this.view.getContext(),
                android.R.layout.simple_spinner_item,
                spinnerRefData
        );

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointViewHolder.spinner.setAdapter(adapter);

        if (indexOfRef > 0) {
            pointViewHolder.spinner.setSelection(indexOfRef);
        }
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
