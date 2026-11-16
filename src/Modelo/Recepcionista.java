package Modelo;
public class Recepcionista {
    private final String usuario;
    private final String contrasena;
    private final String nombre;

    public Recepcionista(String usuario, String contrasena, String nombre) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }
    
    public boolean validarCredenciales(String usuarioIngresado, String contrasenaIngresada) {
        return usuario.equals(usuarioIngresado) && contrasena.equals(contrasenaIngresada);
    }
}
