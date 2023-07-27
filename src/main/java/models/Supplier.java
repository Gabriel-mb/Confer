package models;

public class Supplier {

    private String supplierName;
    private Integer supplierId;

    public Supplier(String supplierName, Integer supplierId) {
        this.supplierName = supplierName;
        this.supplierId = supplierId;
    }

    public Supplier(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public String toString() {
        return supplierName;
    }
}
