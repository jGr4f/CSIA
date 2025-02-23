/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;
import java.sql.*;

/**
 *
 * @author juanr
 */
public class DBConnection {
    static String url = "jdbc:mysql://localhost:3306/csia"; // URL del servidor local y la base de datos llamada csia
    static String user = "root"; // Usuario
    static String pass = "estudiante"; // Contraseña
    
    public static Connection conectardb(){
        Connection con = null;
        try{
            con = DriverManager.getConnection(url, user, pass);
            System.out.println("Conexion a base completada.");
        }catch(SQLException e){
            e.printStackTrace();
        }
        
        return con;
    }
    
}
