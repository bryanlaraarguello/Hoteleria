/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;
import Modelo.GestorHotel;
import Modelo.Recepcionista;
/**
 *
 * @author bryan
 */
public class ControladorLogin {
   
    private final GestorHotel gestor = GestorHotel.getInstancia();

    /**
     * @param usuario    texto del campo usuario
     * @param contrasena texto del JPasswordField (new String(txt.getPassword()))
     * @return el recepcionista autenticado
     */
    public Recepcionista iniciarSesion(String usuario, String contrasena) {
        if (usuario == null || usuario.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar el usuario.");
        }
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar la contraseña.");
        }
        return gestor.iniciarSesion(usuario, contrasena);
    }
}

