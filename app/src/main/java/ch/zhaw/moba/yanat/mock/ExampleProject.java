package ch.zhaw.moba.yanat.mock;

import android.content.Context;

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


        Project project = projectRepository.findById(1);

        PointRepository pointRepository = project.getPointRepository(this.context);

        // fix point
        Point pointFix = new Point();
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
            point.setReferenceId(pointFix.getId());
            point.setPosX(15 + (i*5));
            point.setPosY(15 + (i*5));

            pointRepository.add(point);
        }

        /*
        for (Point point : points) {
            if (point.getId() > 1) {
                point.setReferenceId(1);
            } else {
                point.setReferenceId(0);
            }
            pointRepository.update(point);
        }
        Log.v("YANAT", "update successfull");




        // for 01.1 Situation.pdf

        // fix point
        points.get(0).setPosX(123);
        points.get(0).setPosY(project.getPdfHeight() - 215);
        pointRepository.update(points.get(0));

        // groundFloor point
        points.get(1).setPosX(208);
        points.get(1).setPosY(project.getPdfHeight() - 215);
        pointRepository.update(points.get(1));

        // refpoint 1-5
        points.get(2).setPosX(146);
        points.get(2).setPosY(project.getPdfHeight() - 308);
        pointRepository.update(points.get(2));

        points.get(3).setPosX(171);
        points.get(3).setPosY(project.getPdfHeight() - 109);
        pointRepository.update(points.get(3));

        points.get(4).setPosX(92);
        points.get(4).setPosY(project.getPdfHeight() - 97);
        pointRepository.update(points.get(4));

        points.get(5).setPosX(116);
        points.get(5).setPosY(project.getPdfHeight() - 63);
        pointRepository.update(points.get(5));

        points.get(6).setPosX(18);
        points.get(6).setPosY(project.getPdfHeight() - 195);
        pointRepository.update(points.get(6));


        BEGIN TRANSACTION;
        CREATE TABLE point (id INTEGER PRIMARY KEY,create_date INTEGER,timestamp INTEGER,deleted INTEGER,project_id INTEGER,reference_id INTEGER,group_id INTEGER,is_ground_floor INTEGER,pos_x INTEGER,pos_y INTEGER,height REAL,comment TEXT );
        INSERT INTO "point" VALUES(1,1459768229,1460209866,0,1,0,0,0,123,204,450.0,'Absolute point');
        INSERT INTO "point" VALUES(2,1459768229,1460209866,0,1,1,0,1,208,204,-10.0,'Ground Floor (-10m)');
        INSERT INTO "point" VALUES(3,1459768229,1460209863,0,1,2,0,0,146,111,-20.0,'Reference Point 0 (-20.0m)');
        INSERT INTO "point" VALUES(4,1459768229,1460209863,0,1,1,0,0,171,310,-10.0,'Reference Point 1 (-10.0m)');
        INSERT INTO "point" VALUES(5,1459768229,1460209866,0,1,1,0,0,92,322,0.0,'Reference Point 2 (0.0m)');
        INSERT INTO "point" VALUES(6,1459768229,1460209866,0,1,1,0,0,116,356,10.0,'Reference Point 3 (10.0m)');
        INSERT INTO "point" VALUES(7,1459768229,1460209866,0,1,1,0,0,18,224,20.0,'Reference Point 4 (20.0m)');
        COMMIT;

        */

    }
}
