package models;

import java.util.Date;

public class Borrowed {

    private Integer idEmployee;
    private Integer idEquipment;
    private String supplierName;
    private Integer quantity;
    private String equipmentName;
    private Date date;
    private Integer supplierId;

    public Borrowed(String equipmentName, Integer idEquipment, Date date) {
        this.equipmentName = equipmentName;
        this.idEquipment = idEquipment;
        this.date = date;
    }

    public Borrowed(Integer idEmployee, Integer idEquipment, Date date, Integer supplierId) {
        this.idEmployee = idEmployee;
        this.idEquipment = idEquipment;
        this.date = date;
        this.supplierId = supplierId;
    }

    public Borrowed(String equipmentName, Integer idEquipment, Date date, String supplierName) {
        this.equipmentName = equipmentName;
        this.idEquipment = idEquipment;
        this.date = date;
        this.supplierName = supplierName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    @Override
    public String toString() {
        return "Borrowed{" +
                "idEmployee=" + idEmployee +
                ", idEquipment=" + idEquipment +
                ", supplierName='" + supplierName + '\'' +
                ", quantity=" + quantity +
                ", equipmentName='" + equipmentName + '\'' +
                ", date=" + date +
                '}';
    }
}
