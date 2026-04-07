package Menus_Inicio;

public class SesionEmpleado {

    private static int idEmpleado = 0;
    private static String nombreCompleto = "";
    private static String rol = "";

    public static void iniciarSesion(int id, String nombre, String apellido, String rolEmp) {
        idEmpleado = id;
        nombreCompleto = nombre + " " + apellido;
        rol = rolEmp;
        System.out.println("Sesion iniciada -> ID: " + idEmpleado);
    }

    public static int getIdEmpleado() {
        return idEmpleado;
    }

    public static String getNombreCompleto() {
        return nombreCompleto;
    }

    public static String getRol() {
        return rol;
    }

    public static void cerrarSesion() {
        System.out.println("Cerrando sesion -> ID actual: " + idEmpleado);
        idEmpleado = 0;
        nombreCompleto = "";
        rol = "";
    }
}