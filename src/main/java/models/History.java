package models;

import java.math.BigDecimal;
import java.sql.Date;

public class History {

    private Integer idEquipment;
    private Integer supplierId;
    private String supplierName;
    private String nameEquip;
    private Integer idEmployee;
    private String nameEmployee;
    private java.sql.Date borrowedDay;
    private Integer statusId;
    private String statusName;
    private java.sql.Date devolutionDay;
    private BigDecimal fine;

    public History(Integer idEquipment, Integer supplierId, String nameEquip, Integer idEmployee, String nameEmployee, java.sql.Date borrowedDay, Integer statusId, java.sql.Date devolutionDay, BigDecimal fine) {
        this.idEquipment = idEquipment;
        this.supplierId = supplierId;
        this.nameEquip = nameEquip;
        this.idEmployee = idEmployee;
        this.nameEmployee = nameEmployee;
        this.borrowedDay = borrowedDay;
        this.statusId = statusId;
        this.devolutionDay = devolutionDay;
        this.fine = fine;
    }

    public History(Integer idEquipment, String supplierName, String nameEquip, Integer idEmployee, String nameEmployee, Date borrowedDay, String statusName, Date devolutionDay, BigDecimal fine) {
        this.idEquipment = idEquipment;
        this.supplierName = supplierName;
        this.nameEquip = nameEquip;
        this.idEmployee = idEmployee;
        this.nameEmployee = nameEmployee;
        this.borrowedDay = borrowedDay;
        this.statusName = statusName;
        this.devolutionDay = devolutionDay;
        this.fine = fine;
    }

    public Integer getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(Integer idEquipment) {
        this.idEquipment = idEquipment;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getNameEquip() {
        return nameEquip;
    }

    public void setNameEquip(String nameEquip) {
        this.nameEquip = nameEquip;
    }

    public String getNameEmployee() {
        return nameEmployee;
    }

    public void setNameEmployee(String nameEmployee) {
        this.nameEmployee = nameEmployee;
    }

    public java.sql.Date getBorrowedDay() {
        return borrowedDay;
    }

    public void setBorrowedDay(java.sql.Date borrowedDay) {
        this.borrowedDay = borrowedDay;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public java.sql.Date getDevolutionDay() {
        return devolutionDay;
    }

    public void setDevolutionDay(java.sql.Date devolutionDay) {
        this.devolutionDay = devolutionDay;
    }

    public BigDecimal getFine() {
        return fine;
    }

    public void setFine(BigDecimal fine) {
        this.fine = fine;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getIdEmployee() {
        return idEmployee;
    }

    public void setIdEmployee(Integer idEmployee) {
        this.idEmployee = idEmployee;
    }

    @Override
    public String toString() {
        return "History{" +
                "idEquipment=" + idEquipment +
                ", supplierId=" + supplierId +
                ", nameEquip='" + nameEquip + '\'' +
                ", nameEmployee='" + nameEmployee + '\'' +
                ", borrowedDay=" + borrowedDay +
                ", statusId=" + statusId +
                ", statusName='" + statusName + '\'' +
                ", devolutionDay=" + devolutionDay +
                ", fine=" + fine +
                '}';
    }
}
