package models;

import java.sql.Date;

public class Epi {
    private Integer quantity;
    private Integer numCa;

    private String epiName;
    private Date date;
    private Integer employeeId;



    public Epi( String epiName, Integer numCa, Integer quantity) {
        this.quantity = quantity;
        this.numCa = numCa;
        this.epiName = epiName;
    }

    public Epi(String epiName) {
        this.epiName = epiName;
    }

    public Epi(Integer quantity, Integer numCa, String epiName, Date date, Integer employeeId) {
        this.quantity = quantity;
        this.numCa = numCa;
        this.epiName = epiName;
        this.date = date;
        this.employeeId = employeeId;
    }

    public Epi(Integer quantity, Integer numCa, String epiName, Date date) {
        this.quantity = quantity;
        this.numCa = numCa;
        this.epiName = epiName;
        this.date = date;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getNumCa() {
        return numCa;
    }

    public void setNumCa(Integer numCa) {
        this.numCa = numCa;
    }

    public String getEpiName() {
        return epiName;
    }

    public void setEpiName(String epiName) {
        this.epiName = epiName;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Epi{" +
                "quantity=" + quantity +
                ", numCa=" + numCa +
                ", epiName='" + epiName + '\'' +
                ", date=" + date +
                ", employeeId=" + employeeId +
                '}';
    }
}
