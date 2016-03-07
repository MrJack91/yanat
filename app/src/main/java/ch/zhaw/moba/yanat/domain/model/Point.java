package ch.zhaw.moba.yanat.domain.model;

/**
 * Created by michael on 04.03.16.
 */
public class Point {

    protected int id = 0;

    protected Long createDate = null;
    protected Long tstamp = null;

    protected int referenceId = 0;
    protected int groupId = 0;
    protected boolean isAbsolute = false;
    protected boolean isGroundFloor = false;
    protected double posX = 0;
    protected double posY = 0;
    protected int height = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Long createDate) {
        this.createDate = createDate;
    }

    public Long getTstamp() {
        return tstamp;
    }

    public void setTstamp(Long tstamp) {
        this.tstamp = tstamp;
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

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
