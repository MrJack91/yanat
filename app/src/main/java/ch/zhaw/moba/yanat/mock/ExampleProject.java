package ch.zhaw.moba.yanat.mock;

import android.content.Context;

import java.util.List;

import ch.zhaw.moba.yanat.domain.model.Point;
import ch.zhaw.moba.yanat.domain.model.Project;
import ch.zhaw.moba.yanat.domain.repository.PointRepository;
import ch.zhaw.moba.yanat.domain.repository.ProjectRepository;

/**
 * Created by michael on 21.03.16.
 */
public class ExampleProject {

    protected Context context;

    protected ProjectRepository projectRepository;

    public ExampleProject(Context context) {
        this.context = context;
        this.projectRepository = new ProjectRepository(this.context);
    }

    public void create() {

        /*
        Project project = new Project();
        project.setTitle("0.0 Example Project");
        projectRepository.add(project);
        // todo: add pdf handling
        project.setPdf("/data/data/ch.zhaw.moba.yanat/files/" + project.getId() + "/01.2.1 Grundriss UG.pdf");
        project.setPdfHeight(0);
        project.setPdfWidth(0);
        projectRepository.update(project);
        */


        List<Project> projects = projectRepository.findById(1);
        Project project = projects.get(0);


        PointRepository pointRepository = project.getPointRepository(this.context);

        // fix point
        Point pointFix = new Point();
        pointFix.setIsAbsolute(true);
        pointFix.setHeight(450);
        pointFix.setComment("Absolute point");
        pointFix.setPosX(10);
        pointFix.setPosY(10);
        pointRepository.add(pointFix);

        // groundFloor point
        Point pointGroundFloor = new Point();
        pointGroundFloor.setIsGroundFloor(true);
        pointGroundFloor.setHeight(-10);
        pointGroundFloor.setComment("Ground Floor (-10m)");
        pointGroundFloor.setPosX(15);
        pointGroundFloor.setPosY(15);
        pointRepository.add(pointGroundFloor);

        // reference points
        for (int i = 0; i < 5; i++) {
            Point point = new Point();
            float height = (i * 10) - 20;
            point.setHeight(height);
            point.setComment("Reference Point " + i + " (" + height + "m)");
            point.setPosX(15 + (i*5));
            point.setPosY(15 + (i*5));

            pointRepository.add(point);
        }



    }
}
