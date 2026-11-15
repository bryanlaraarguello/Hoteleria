/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

/**
 *
 * @author bryan
 */
public class HabitacionFactory {
    
    public static final String[] TIPOS = {"Individual", "Doble", "Familiar"};

    public static Habitacion crearHabitacion(String tipo, int numero) {
        if (tipo == null) {
            return null;
        }
        if (tipo.equalsIgnoreCase("Individual")) {
            return new HabitacionIndividual(numero);
        } else if (tipo.equalsIgnoreCase("Doble")) {
            return new HabitacionDoble(numero);
        } else if (tipo.equalsIgnoreCase("Familiar")) {
            return new HabitacionFamiliar(numero);
        } else {
            return null;
        }
    }
    
}
