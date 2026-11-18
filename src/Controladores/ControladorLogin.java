/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;
import Modelo.GestorHotel;
import Modelo.Recepcionista;
import Vista.FrmLogin;
/**
 *
 * @author bryan
 */
public class ControladorLogin {
   
    private final GestorHotel gestor = GestorHotel.getInstancia();
    private final FrmLogin vista;

    public ControladorLogin(FrmLogin vista) {
        this.vista = vista;

        // Conectar el botón con el controlador
        this.vista.agregarEventoLogin(e -> autenticar());
    }

    public Recepcionista iniciarSesion(String usuario, String contrasena) {

        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar el usuario.");
        }

        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar la contraseña.");
        }

        return gestor.iniciarSesion(usuario, contrasena);
    }

    private void autenticar() {
        try {
            String usuario = vista.getUsuario();
            String contrasena = vista.getContrasena();

            Recepcionista recepcionista =
                    iniciarSesion(usuario, contrasena);

            FrmHotel hotel = new FrmHotel();
            hotel.setRecepcionista(recepcionista.getNombre());
            hotel.setLocationRelativeTo(null);
            hotel.setVisible(true);

            vista.dispose();

        } catch (IllegalArgumentException ex) {
            vista.mostrarMensaje(ex.getMessage());
        }
    }
}

