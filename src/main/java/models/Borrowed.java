package models;

import java.util.Date;

public class Borrowed {

    private Integer idEmployee;
    private Integer idEquipment;
    private Integer quantity;
    private String equipmentName;
    private Date date;

    public Borrowed(String equipmentName, Integer idEquipment, Date date) {
        this.equipmentName = equipmentName;
        this.idEquipment = idEquipment;
        this.date = date;
    }

    public Borrowed(Integer idEmployee, Integer idEquipment, Date date) {
        this.idEmployee = idEmployee;
        this.idEquipment = idEquipment;
        this.date = date;
    }

    public Integer getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(Integer idEmployee) {
        this.idEmployee = idEmployee;
    }

    public Integer getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(Integer idEquipment) {
        this.idEquipment = idEquipment;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public java.sql.Date getDate() {
        return (java.sql.Date) date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Borrowed{" +
                "idEquipment=" + idEquipment +
                ", equipmentName='" + equipmentName + '\'' +
                ", date=" + date +
                '}';
    }

}
