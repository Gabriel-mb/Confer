package models;

import java.sql.Date;

public class Borrowed {

    private Integer idEmployee;
    private Integer idEquipment;
    private String supplierName;
    private String equipmentName;
    private Date date;
    private Integer supplierId;
    private String employeeName;
    private Integer quantity;

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

    public Borrowed(Integer idEmployee, Integer idEquipment, String supplierName, String equipmentName, Date date, String employeeName) {
        this.idEmployee = idEmployee;
        this.idEquipment = idEquipment;
        this.supplierName = supplierName;
        this.equipmentName = equipmentName;
        this.date = date;
        this.employeeName = employeeName;
    }

    public Borrowed(Integer idEmployee, String equipmentName, Date date, String supplierName, Integer quantity) {
        this.idEmployee = idEmployee;
        this.equipmentName = equipmentName;
        this.date = date;
        this.supplierName = supplierName;
        this.quantity = quantity;
    }

    public Borrowed(String supplierName, String equipmentName, Date date, Integer quantity) {
        this.supplierName = supplierName;
        this.equipmentName = equipmentName;
        this.date = date;
        this.quantity = quantity;
    }

    public Borrowed(String equipmentName, Integer idEquipment, Date date, String supplierName, Integer supplierId) {
        this.idEquipment = idEquipment;
        this.supplierName = supplierName;
        this.equipmentName = equipmentName;
        this.date = date;
        this.supplierId = supplierId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
                ", equipmentName='" + equipmentName + '\'' +
                ", date=" + date +
                ", supplierId=" + supplierId +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }
    public String stringStockBorrowed() {
        return equipmentName + "/" + supplierName + "/" + date + "/" + quantity;
    }
}
