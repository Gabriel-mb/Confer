package models;

import java.util.Date;

public class Stock {
    private Integer quantity;
    private String equipmentName;
    private Integer supplierId;
    private String supplierName;
    private Date date;

    public Stock(Integer quantity, String equipmentName, Integer supplierId) {
        this.quantity = quantity;
        this.equipmentName = equipmentName;
        this.supplierId = supplierId;
    }

    public Stock() {
    }

    public Stock(Integer quantity, String equipmentName, String supplierName, Date date) {
        this.quantity = quantity;
        this.equipmentName = equipmentName;
        this.supplierName = supplierName;
        this.date = date;
    }

    public Stock(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public String toString() {
        return equipmentName;
    }
}
