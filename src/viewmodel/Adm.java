/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewmodel;
import model.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author estudiante
 */
public class Adm {


    public DefaultTableModel cargarDatos() {
        DefaultTableModel model = new DefaultTableModel();
        
        
        try (Connection connection = DBConnection.conectardb()) {
            if (connection != null) {
                Statement statement = connection.createStatement();
                String query = "SELECT * FROM perfiles"; 
                ResultSet resultSet = statement.executeQuery(query);

                
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

               
                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(metaData.getColumnName(i));
                }

                
                while (resultSet.next()) {
                    Object[] rowData = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        rowData[i - 1] = resultSet.getObject(i);
                    }
                    model.addRow(rowData);
                }
            } else {
                System.out.println("No se pudo establecer la conexión.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return model;
    }
}


