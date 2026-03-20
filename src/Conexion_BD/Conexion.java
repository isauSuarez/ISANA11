package Conexion_BD;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    private static final String URL = "jdbc:mysql://localhost:3306/ets_soporte?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "JTi1100..";

    public static Connection getConexion() throws SQLException {
        Connection con = DriverManager.getConnection(URL, USER, PASS);
        System.out.println("Conexion exitosa a la base de datos ets_soporte.");
        return con;
    }

    public static void probarConexion() {
        try (Connection con = getConexion()) {
            if (con != null && !con.isClosed()) {
                System.out.println("Prueba completada: la conexion esta activa.");
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        }
    }
}
