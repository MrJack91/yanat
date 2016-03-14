package ch.zhaw.moba.yanat.domain.model;

/**
 * Created by michael on 04.03.16.
 */
public class Point extends AbstractModel {

    protected int projectId = 0;
    protected int referenceId = 0;
    protected int groupId = 0;
    protected boolean isAbsolute = false;
    protected boolean isGroundFloor = false;
    protected float posX = 0;
    protected float posY = 0;
    protected float height = 0;
    protected String comment = "";

    protected String title = "";

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

    public double getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public double getPosY() {
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
}
