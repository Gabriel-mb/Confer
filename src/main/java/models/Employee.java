package models;

import java.sql.SQLException;

public class Employee {

    private Integer id;
    private String name;

    public Employee(Integer id, String name) throws SQLException {
        if (id.toString().length() != 8){
            throw new SQLException("O número ID do funcionário deve ter exatamente 8 caracteres!"); // Usar catch para exibir o erro para o usuário
        }
        // Pode ser feito outro if para name caso length ultrapase 45 carateres!

        this.id = id;
        this.name = name;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return   "id: " + id + ", " + "name: " + name;

    }
}
