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



    // Método que carga los datos de la base de datos y devuelve un DefaultTableModel
    public DefaultTableModel cargarDatos() {
        DefaultTableModel model = new DefaultTableModel();
        
        // Usar la conexión de la clase DBConnection
        try (Connection connection = DBConnection.conectardb()) {
            if (connection != null) {
                Statement statement = connection.createStatement();
                String query = "SELECT * FROM mi_tabla"; // Cambia "mi_tabla" por el nombre de tu tabla
                ResultSet resultSet = statement.executeQuery(query);

                // Obtener los metadatos de la tabla (para configurar las columnas)
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Agregar las columnas al modelo de la tabla
                for (int i = 1; i <= columnCount; i++) {
                    model.addColumn(metaData.getColumnName(i));
                }

                // Agregar las filas de datos al modelo de la tabla
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


