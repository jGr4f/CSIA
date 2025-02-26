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
    public boolean verificarUsuario(JTextField us, JPasswordField cont) {
    String query = "SELECT * FROM perfiles WHERE nomperfil = ? AND password = ?";
    String usuario = us.getText();
    String contraseña = cont.getText();
    
    try (Connection con = DBConnection.conectardb();
         PreparedStatement ps = con.prepareStatement(query)) {
        
        ps.setString(1, usuario);
        ps.setString(2, contraseña);
        
        ResultSet rs = ps.executeQuery();
        
        // Usuario y contraseña correctos
        System.out.println("Inicio de sesión correcto.");
        return rs.next();
        
    } catch (SQLException ex) {
        ex.printStackTrace();
        System.out.println("Usuario o contrasena incorrectos.");
        return false; // Error en los datos
    }
}

}
