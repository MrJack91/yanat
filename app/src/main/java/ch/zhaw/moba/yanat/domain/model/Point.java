package ch.zhaw.moba.yanat.domain.model;

/**
 * Created by michael on 04.03.16.
 */
public class Point extends AbstractModel implements Comparable<Point> {

    protected int projectId = 0;
    protected int referenceId = 0;
    protected int groupId = 0;
    protected boolean isAbsolute = false;
    protected boolean isGroundFloor = false;
    protected int posX = 0;
    protected int posY = 0;
    protected float height = 0;
    protected String comment = "";

    // dynamic calculated data
    protected String title = "";
    protected Float heightAbsolute = 0f;
    protected Float heightRelative = 0f;
    protected Float heightToGroundFloor = null;


    public Point() {
        super();
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * unique group id per absolute point
     * @return
     */
    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public boolean isAbsolute() {
        return isAbsolute;
    }

    public void setIsAbsolute(boolean isAbsolute) {
        this.isAbsolute = isAbsolute;
    }

    public boolean isGroundFloor() {
        return isGroundFloor;
    }

    public void setIsGroundFloor(boolean isGroundFloor) {
        this.isGroundFloor = isGroundFloor;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getHeightAbsolute() {
        return heightAbsolute;
    }

    public void setHeightAbsolute(Float heightAbsolute) {
        this.heightAbsolute = heightAbsolute;
    }

    public Float getHeightToGroundFloor() {
        return heightToGroundFloor;
    }

    public void setHeightToGroundFloor(Float heightToGroundFloor) {
        this.heightToGroundFloor = heightToGroundFloor;
    }

    public Float getHeightRelative() {
        return heightRelative;
    }

    public void setHeightRelative(Float heightRelative) {
        this.heightRelative = heightRelative;
    }


    @Override
    public String toString() {
        return "Point{" +
                super.toString() +
                ", projectId=" + projectId +
                ", referenceId=" + referenceId +
                ", groupId=" + groupId +
                ", isAbsolute=" + isAbsolute +
                ", isGroundFloor=" + isGroundFloor +
                ", posX=" + posX +
                ", posY=" + posY +
                ", title='" + title + '\'' +
                ", height=" + height +
                ", heightAbsolute=" + heightAbsolute +
                ", heightRelative=" + heightRelative +
                ", heightToGroundFloor=" + heightToGroundFloor +
                ", comment='" + comment + '\'' +
                '}';
    }

    /**
     * Order by coordinate, to get points on same position
     * @param o
     * @return
     */
    @Override
    public int compareTo(Point o) {
        int xDiff = this.getPosX() - o.getPosX();
        int yDiff = this.getPosY() - o.getPosY();
        if (xDiff == 0 && yDiff == 0) {
            return 0;
        }
        // use not zero var and return it
        if (xDiff == 0) {
            xDiff = yDiff;
        }
        return xDiff;
    }
}
