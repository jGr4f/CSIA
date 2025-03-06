/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package viewmodel;

/**
 *
 * @author juanr
 */

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
        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ps.setString(1, model.getValueAt(i, 1).toString()); 
            ps.setString(2, model.getValueAt(i, 2).toString());  // Se usa el valor real de la tabla
            ps.setInt(3, Integer.parseInt(model.getValueAt(i, 0).toString()));

            ps.executeUpdate();
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
