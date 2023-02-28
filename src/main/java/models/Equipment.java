package models;

import java.util.Date;

public class Equipment {
    private Integer idEquipment;
    private String nameEquip;
    private String status;
    private String nameEmployee;
    private Date date;

    public Equipment(Integer idEquipment, String nameEquip) {
        this.idEquipment = idEquipment;
        this.nameEquip = nameEquip;
    }

    public Equipment(Integer idEquipment, String nameEquip, String status, String nameEmployee, Date date) {
        this.idEquipment = idEquipment;
        this.nameEquip = nameEquip;
        this.status = status;
        this.nameEmployee = nameEmployee;
        this.date = date;
    }

    public Integer getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(Integer idEquipment) {
        this.idEquipment = idEquipment;
    }

    public String getName() {
        return nameEquip;
    }

    public void setName(String name) {
        this.nameEquip = name;
    }

    public String getNameEquip() {return nameEquip;}

    public void setNameEquip(String nameEquip) {this.nameEquip = nameEquip;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public String getNameEmployee() {return nameEmployee;}

    public void setNameEmployee(String nameEmployee) {this.nameEmployee = nameEmployee;}

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return idEquipment + ", " + nameEquip;
    }
}
