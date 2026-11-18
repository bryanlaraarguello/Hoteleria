/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author bryan
 */
public class CalculosRecursivos {
    public static long calcularIngresosEsperados(ArrayList<Reserva> reservas) {
        return calcularIngresosEsperados(reservas, 0);
    }
    private static long calcularIngresosEsperados(ArrayList<Reserva> reservas, int posicion) {
        if (posicion >= reservas.size()) {
            return 0;
        }
        Reserva actual = reservas.get(posicion);
        long aporte = 0;
        if (actual.getEstado().equals(Reserva.CONFIRMADA)) {
            aporte = actual.getImporteTotal();
        }
        return aporte + calcularIngresosEsperados(reservas, posicion + 1);
    }
    public static int contarHabitacionesDisponibles(ArrayList<Habitacion> habitaciones,
            ArrayList<Reserva> reservas, LocalDate fecha) {
        return contarHabitacionesDisponibles(habitaciones, reservas, fecha, 0);
    }
    private static int contarHabitacionesDisponibles(ArrayList<Habitacion> habitaciones,
            ArrayList<Reserva> reservas, LocalDate fecha, int posicion) {
        if (posicion >= habitaciones.size()) {
            return 0;
        }
        int suma = 0;
        if (estaDisponible(habitaciones.get(posicion), reservas, fecha)) {
            suma = 1;
        }
        return suma + contarHabitacionesDisponibles(habitaciones, reservas, fecha, posicion + 1);
    }
    public static boolean estaDisponible(Habitacion habitacion, ArrayList<Reserva> reservas, LocalDate fecha) {
        for (Reserva r : reservas) {
            if (r.getHabitacion().getNumero() == habitacion.getNumero()
                    && r.bloqueaDisponibilidad()
                    && r.incluyeFecha(fecha)) {
                return false;
            }
        }
        return true;
    }
}
