/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewmodel;
import model.*;
import java.sql.*;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 *
 * @author estudiante
 */
public class Log {
    public int verificarUsuario(JTextField us, JPasswordField cont) {
    String query = "{CALL verificarUsuario(?, ?, ?)}"; // Llamada al procedimiento almacenado
    String usuario = us.getText();
    String contraseña = new String(cont.getPassword());

    try (Connection con = DBConnection.conectardb();
         CallableStatement cs = con.prepareCall(query)) {

        // Asignar parámetros de entrada
        cs.setString(1, usuario);
        cs.setString(2, contraseña);
        
        // Registrar el parámetro de salida
        cs.registerOutParameter(3, Types.INTEGER);
        
        // Ejecutar el procedimiento
        cs.execute();
        
        // Obtener el resultado de salida (ID del rol)
        int idRol = cs.getInt(3);
        
        System.out.println("ID de Rol obtenido: " + idRol);
        return idRol;

    } catch (SQLException ex) {
        ex.printStackTrace();
        return -1; // En caso de error
    }
}


}
