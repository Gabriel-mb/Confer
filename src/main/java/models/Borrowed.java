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

    public Borrowed(Integer idEmployee, Integer idEquipment, Integer quantity) {
        this.idEmployee = idEmployee;
        this.idEquipment = idEquipment;
        this.quantity = quantity;
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

    public Date getDate() {
        return date;
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
