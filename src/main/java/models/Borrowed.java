package models;

public class Borrowed {

    private Integer idEmployee;
    private Integer idEquipment;
    private Integer quantity;

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

    @Override
    public String toString() {
        return idEmployee + ", " + idEquipment + ", " + quantity;
    }
}
