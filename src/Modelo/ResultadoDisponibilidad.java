package Modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class ResultadoDisponibilidad {
   private final LocalDate fecha;
    private final int cantidad;
    private final ArrayList<Habitacion> habitaciones;

    public ResultadoDisponibilidad(LocalDate fecha, int cantidad, ArrayList<Habitacion> habitaciones) {
        this.fecha = fecha;
        this.cantidad = cantidad;
        // Copia propia: quien creó el resultado no puede alterarlo después
        this.habitaciones = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            this.habitaciones.add(h);
        }
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public int getCantidad() {
        return cantidad;
    }

    public ArrayList<Habitacion> getHabitaciones() {
        ArrayList<Habitacion> copia = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            copia.add(h);
        }
        return copia;
    } 
}
