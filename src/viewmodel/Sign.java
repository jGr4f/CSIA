/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewmodel;
import java.sql.*;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import model.*;


/**
 *
 * @author juanr
 */
public class Sign {
    public void insertarUsuarioConDatos(JTextField nomu, JPasswordField pass, JTextField nom, JTextField ape, JTextField ndoc, JComboBox fac) {
    String nomup = nomu.getText();
    String passp = new String(pass.getPassword()); // Método seguro para obtener el password xdxdxd

    String nomp = nom.getText();
    String apep = ape.getText();
    int ndocp = Integer.parseInt(ndoc.getText());
    String facp = fac.getSelectedItem().toString();

    String checkQuery = "SELECT COUNT(*) FROM perfiles WHERE nomperfil = ?";
    String query1 = "INSERT INTO perfiles (nomperfil, password) VALUES (?, ?)";
    String query2 = "INSERT INTO datosest (id_perfiles, nombres, apellidos, ndoc, facultad) VALUES (?, ?, ?, ?, ?)";

    try {
        Connection con = DBConnection.conectardb();

        // Verificacion de usuario
        PreparedStatement checkStmt = con.prepareStatement(checkQuery);
        checkStmt.setString(1, nomup);
        ResultSet checkRs = checkStmt.executeQuery();
        checkRs.next();
        int count = checkRs.getInt(1);

        checkRs.close();
        checkStmt.close();

        if (count > 0) {
            System.out.println("El usuario ya está registrado. Inserción cancelada.");
            return; 
        }

        // Insertar en perfiles
        PreparedStatement ps1 = con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
        ps1.setString(1, nomup);
        ps1.setString(2, passp);
        ps1.executeUpdate();

        // Obtener el ID generado
        ResultSet rs = ps1.getGeneratedKeys();
        int idPerfiles = -1;
        if (rs.next()) {
            idPerfiles = rs.getInt(1); // Obtiene el ID generado
        }

        rs.close();
        ps1.close();

        // Insertar en datosest con el idperfiles obtenido
        PreparedStatement ps2 = con.prepareStatement(query2);
        ps2.setInt(1, idPerfiles);
        ps2.setString(2, nomp);
        ps2.setString(3, apep);
        ps2.setInt(4, ndocp);
        ps2.setString(5, facp);
        ps2.executeUpdate();

        System.out.println("Datos correctamente insertados en las tablas.");

        ps2.close();
        con.close();
    } catch (SQLException ex) {
        System.out.println("Error en la inserción.");
        ex.printStackTrace();
    }
}


}
