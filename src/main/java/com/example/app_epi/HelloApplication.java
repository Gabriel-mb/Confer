package com.example.app_epi;

import dao.ConnectionDAO;
import dao.EmployeeDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Employee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //launch();
        try(Connection connection = new ConnectionDAO().connect()){ //Por ser um client-side não será um grande problema abrir apenas uma conexão, caso contrário seria mais adequado utilizar um connect/disconnect em cada método DAO.
            EmployeeDAO employeeDao = new EmployeeDAO(connection);

            Employee ricardo = new Employee(20423024, "Ricardo Almeida Carvalho");
            /*
            employeeDao.create(ricardo);
            employeeDao.readName("luiz");
            employeeDao.readName("ricardo");
            employeeDao.readId(2042);
            employeeDao.updateId(12345678, 40273033);
            employeeDao.updateName(20423024, "Ricardo Almeida Bastos");
            employeeDao.delete(20423024);
            */

            // PRECISO INSERIR UM connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}