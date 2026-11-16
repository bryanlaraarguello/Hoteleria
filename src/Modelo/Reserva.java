package Modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Reserva {
    public static final String CONFIRMADA = "Confirmada";
    public static final String HOSPEDADO = "Hospedado";
    public static final String FINALIZADA = "Finalizada";
    public static final String VENCIDA = "Vencida";
    private final String codigo;
    private final Huesped huesped;
    private final Habitacion habitacion;
    private final int cantidadPersonas;
    private final LocalDate fechaEntrada;
    private final LocalDate fechaSalida;
    private final int noches;
    private final long importeTotal;
    private final Recepcionista recepcionista;

    private LocalDate fechaSalidaReal;
    private String estado;

    public Reserva(String codigo, Huesped huesped, Habitacion habitacion, int cantidadPersonas,
            LocalDate fechaEntrada, LocalDate fechaSalida, Recepcionista recepcionista) {
        this.codigo = codigo;
        this.huesped = huesped;
        this.habitacion = habitacion;
        this.cantidadPersonas = cantidadPersonas;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.recepcionista = recepcionista;
        this.noches = (int) ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        this.importeTotal = noches * habitacion.getTarifaPorNoche();
        this.fechaSalidaReal = null;
        this.estado = CONFIRMADA;
    }

    public String getCodigo() {
        return codigo;
    }

    public Huesped getHuesped() {
        return huesped;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public int getCantidadPersonas() {
        return cantidadPersonas;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }

    public LocalDate getFechaSalida() {
        return fechaSalida;
    }

    public synchronized LocalDate getFechaSalidaReal() {
        return fechaSalidaReal;
    }

    public int getNoches() {
        return noches;
    }

    public long getImporteTotal() {
        return importeTotal;
    }

    public synchronized String getEstado() {
        return estado;
    }

    public Recepcionista getRecepcionista() {
        return recepcionista;
    }

    public synchronized boolean bloqueaDisponibilidad() {
        return estado.equals(CONFIRMADA) || estado.equals(HOSPEDADO);
    }

    public boolean incluyeFecha(LocalDate fecha) {
        return !fecha.isBefore(fechaEntrada) && fecha.isBefore(fechaSalida);
    }

    public boolean seSuperponeCon(LocalDate nuevaEntrada, LocalDate nuevaSalida) {
        return nuevaEntrada.isBefore(fechaSalida) && nuevaSalida.isAfter(fechaEntrada);
    }

    public synchronized void cambiarEstado(String nuevo) {
        if (estado.equals(CONFIRMADA) && (nuevo.equals(HOSPEDADO) || nuevo.equals(VENCIDA))) {
            estado = nuevo;
        } else if (estado.equals(HOSPEDADO) && nuevo.equals(FINALIZADA)) {
            estado = nuevo;
        } else {
            throw new IllegalArgumentException("Transición no permitida: " + estado + " -> " + nuevo);
        }
    }

    public synchronized void registrarSalidaReal(LocalDate fecha) {
        this.fechaSalidaReal = fecha;
    }
}
