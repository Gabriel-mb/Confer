package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import models.Employee;
public class EmployeeDAO {
    private final Connection connection;
    public EmployeeDAO(Connection connection) { this.connection = connection; }

    public void create(Employee employee) throws SQLException { // Implementar um verificador para confirmar que a operação foi realizada
        String sql = "INSERT INTO employee (IDEMPLOYEE, NAME) VALUES (?, ?)";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, employee.getId());
        pstm.setString(2, employee.getName());

        pstm.execute();

        pstm.close();
    }

    public void readName(String name) throws SQLException {
        String sql = "SELECT * FROM employee WHERE NAME LIKE ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, "%" + name + "%");

        pstm.execute();

        ResultSet rst = pstm.getResultSet();
        while (rst.next()) {
            Employee employee = new Employee(rst.getInt(1),rst.getString(2));
        }

        pstm.close();
        rst.close();
    }

    public Employee readId(Integer id) throws SQLException{
        verifyId(id);
        String sql = "SELECT * FROM employee WHERE idemployee = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);

        pstm.execute();

        ResultSet rst = pstm.getResultSet();
        int counter = 0;
        if (rst.next()) {
            Employee employee = new Employee(rst.getInt(1), rst.getString(2));
            counter++;
            pstm.close();
            rst.close();
            return employee;
        }
        return null;
    }

    public void updateId(Integer id, Integer newid) throws SQLException{
        verifyId(id);
        verifyId(newid);
        String sql = "UPDATE employee SET IDEMPLOYEE = ? WHERE IDEMPLOYEE = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, newid);
        pstm.setInt(2, id);

        pstm.execute();

        pstm.close();
    }

    public void updateName(Integer id, String name) throws SQLException {
        verifyId(id);
        String sql = "UPDATE employee SET NAME = ? WHERE IDEMPLOYEE = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setString(1, name);
        pstm.setInt(2, id);

        pstm.execute();

        pstm.close();
    }

    public void delete(Integer id) throws SQLException {
        verifyId(id);
        String sql = "DELETE FROM employee WHERE IDEMPLOYEE = ?";

        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);

        Integer rows = pstm.executeUpdate();

        pstm.close();
    }

    public List<Employee> listEmployees() throws SQLException{
        String sql = "SELECT * FROM employee";
        Statement stmt = connection.createStatement();
        ResultSet rst = stmt.executeQuery(sql);
        List<Employee> employees = new ArrayList<>();

        while  (rst.next()) {
            Integer id = rst.getInt(1);
            String name = rst.getString(2);
            employees.add(new Employee(id,name));
        }
        return employees;
    }

    public void verifyId(Integer id) throws SQLException {
        if (id.toString().length() != 8){
            throw new SQLException("O número ID do funcionário deve ter exatamente 8 caracteres!");
        }
    }
    public Boolean checkIfIdExists(Integer id) throws SQLException {
        String sql = "SELECT * FROM employee WHERE IDEMPLOYEE = ?";
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setInt(1, id);
        pstm.execute();
        ResultSet rst = pstm.getResultSet();
        if (rst.next()) {
            pstm.close();
            return true;
        } else {
            pstm.close();
            return false;
        }
    }
}

