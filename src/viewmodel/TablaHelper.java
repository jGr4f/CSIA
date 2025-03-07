/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewmodel;

/**
 *
 * @author juanr
 */
import java.sql.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import viewmodel.Adm;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class TablaHelper {
    
    private Adm adm;
    private Connection conexion;

    public TablaHelper(Connection conexion) {
        this.adm = new Adm();  
        this.conexion = conexion; 
    }

    public void actualizarTabla(JTable tabla) {
        DefaultTableModel model = adm.cargarDatos(); 
        tabla.setModel(model); 
        tabla.revalidate();
        tabla.repaint();
    }

    public void guardarCambios(JTable tabla) {
    DefaultTableModel model = (DefaultTableModel) tabla.getModel();
    String query = "UPDATE perfiles SET nomperfil = ?, password = ? WHERE id_perfiles = ?";

    for (int i = 0; i < model.getRowCount(); i++) {
        try {
            String nuevoNomperfil = model.getValueAt(i, 1).toString();
            String nuevaPassword = model.getValueAt(i, 2).toString();
            int idPerfil = Integer.parseInt(model.getValueAt(i, 0).toString());

            // Verificar si los valores son diferentes antes de realizar la actualización
            String verificarQuery = "SELECT nomperfil, password FROM perfiles WHERE id_perfiles = ?";
            try (PreparedStatement psVerificar = conexion.prepareStatement(verificarQuery)) {
                psVerificar.setInt(1, idPerfil);
                ResultSet rs = psVerificar.executeQuery();

                if (rs.next()) {
                    String perfilActual = rs.getString("nomperfil");
                    String passwordActual = rs.getString("password");

                    // Solo actualizar si los valores han cambiado
                    if (!nuevoNomperfil.equals(perfilActual) || !nuevaPassword.equals(passwordActual)) {
                        try (PreparedStatement psUpdate = conexion.prepareStatement(query)) {
                            psUpdate.setString(1, nuevoNomperfil);
                            psUpdate.setString(2, nuevaPassword);
                            psUpdate.setInt(3, idPerfil);

                            psUpdate.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    System.out.println("Datos de perfiles actualizados correctamente.");
}

    
    public void eliminarRegistro(JTable tabla) {
    DefaultTableModel model = (DefaultTableModel) tabla.getModel();
    int filaSeleccionada = tabla.getSelectedRow(); // Obtener la fila seleccionada

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(null, "Seleccione una fila para eliminar.");
        return;
    }

    int id = Integer.parseInt(model.getValueAt(filaSeleccionada, 0).toString()); // Obtener ID

    String query = "DELETE FROM perfiles WHERE id_perfiles = ?";

    try (PreparedStatement ps = conexion.prepareStatement(query)) {
        ps.setInt(1, id);

        int filasAfectadas = ps.executeUpdate();
        
        if (filasAfectadas > 0) {
            model.removeRow(filaSeleccionada); // Eliminar la fila de la JTable
            JOptionPane.showMessageDialog(null, "Registro eliminado correctamente.");
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar el registro.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al eliminar el registro.");
    }
}

    

}
