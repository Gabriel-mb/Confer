package models;

public class Equipment {
    private Integer idEquipment;
    private String name;

    public Equipment(Integer idEquipment, String name) {
        this.idEquipment = idEquipment;
        this.name = name;
    }

    public Integer getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(Integer idEquipment) {
        this.idEquipment = idEquipment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return idEquipment + ", " + name;
    }
}
