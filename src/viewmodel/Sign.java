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
    public void insertarUsuarioConDatos(JTextField nomu, JPasswordField pass, JTextField nom, JTextField ape, JTextField ndoc, JComboBox<String> fac, JComboBox<String> roll) {
    String nomup = nomu.getText();
    String passp = new String(pass.getPassword());

    String nomp = nom.getText();
    String apep = ape.getText();
    int ndocp = Integer.parseInt(ndoc.getText());
    String facp = fac.getSelectedItem().toString(); // Nombre de la facultad seleccionada
    String rolp = roll.getSelectedItem().toString(); // Nombre del rol seleccionado

    // Consultas SQL
    String checkQuery = "SELECT COUNT(*) FROM perfiles WHERE nomperfil = ?";
    String query1 = "INSERT INTO perfiles (nomperfil, password) VALUES (?, ?)";
    String getFacultadIdQuery = "SELECT id_facultad FROM facultades WHERE nombre_facultad = ?";
    String getRolIdQuery = "SELECT id_rol FROM roles WHERE nombre_rol = ?";
    String query2 = "INSERT INTO datosest (id_perfiles, nombres, apellidos, ndoc, id_facultad, id_rol) VALUES (?, ?, ?, ?, ?, ?)";

    try {
        Connection con = DBConnection.conectardb();

        // Verificación de usuario existente
        PreparedStatement checkStmt = con.prepareStatement(checkQuery);
        checkStmt.setString(1, nomup);
        ResultSet checkRs = checkStmt.executeQuery();
        checkRs.next();
        int count = checkRs.getInt(1);

        checkRs.close();
        checkStmt.close();

        if (count > 0) {
            System.out.println("El usuario ya está registrado.");
            return;
        }

        // Insertar en perfiles
        PreparedStatement ps1 = con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
        ps1.setString(1, nomup);
        ps1.setString(2, passp);
        ps1.executeUpdate();

        // Obtener el ID generado de perfiles
        ResultSet rs = ps1.getGeneratedKeys();
        int idPerfiles = -1;
        if (rs.next()) {
            idPerfiles = rs.getInt(1);
        }
        rs.close();
        ps1.close();

        // Obtener id_facultad desde el nombre seleccionado
        PreparedStatement getFacultadStmt = con.prepareStatement(getFacultadIdQuery);
        getFacultadStmt.setString(1, facp);
        ResultSet facRs = getFacultadStmt.executeQuery();

        int idFacultad = -1;
        if (facRs.next()) {
            idFacultad = facRs.getInt(1);
        } else {
            System.out.println("Error: La facultad seleccionada no existe.");
            return;
        }
        facRs.close();
        getFacultadStmt.close();

        // Obtener id_rol desde el nombre seleccionado
        PreparedStatement getRolStmt = con.prepareStatement(getRolIdQuery);
        getRolStmt.setString(1, rolp);
        ResultSet rolRs = getRolStmt.executeQuery();

        int idRol = -1;
        if (rolRs.next()) {
            idRol = rolRs.getInt(1);
        } else {
            System.out.println("Error: El rol seleccionado no existe.");
            return;
        }
        rolRs.close();
        getRolStmt.close();

        // Insertar en datosest con id_perfiles, id_facultad e id_rol obtenidos
        PreparedStatement ps2 = con.prepareStatement(query2);
        ps2.setInt(1, idPerfiles);
        ps2.setString(2, nomp);
        ps2.setString(3, apep);
        ps2.setInt(4, ndocp);
        ps2.setInt(5, idFacultad);
        ps2.setInt(6, idRol);
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
