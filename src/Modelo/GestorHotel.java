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
public class GestorHotel {
     private static GestorHotel instancia;

    private final ArrayList<Recepcionista> recepcionistas = new ArrayList<>();
    private final ArrayList<Huesped> huespedes = new ArrayList<>();
    private final ArrayList<Habitacion> habitaciones = new ArrayList<>();
    private final ArrayList<Reserva> reservas = new ArrayList<>();
    private Recepcionista sesionActiva;
    private int contadorReservas = 0;
    private GestorHotel() {
        recepcionistas.add(new Recepcionista("recepcion1", "Hotel123", "Recepcionista 1"));
        recepcionistas.add(new Recepcionista("recepcion2", "Hotel456", "Recepcionista 2"));
        sesionActiva = null;
    }
    public static synchronized GestorHotel getInstancia() {
        if (instancia == null) {
            instancia = new GestorHotel();
        }
        return instancia;
    }
    public synchronized Recepcionista iniciarSesion(String usuario, String contrasena) {
        if (sesionActiva != null) {
            throw new IllegalArgumentException(
                    "Ya existe una sesión activa. Debe cerrarla antes de ingresar con otro usuario.");
        }
        for (Recepcionista r : recepcionistas) {
            if (r.validarCredenciales(usuario.trim(), contrasena)) {
                sesionActiva = r;
                return r;
            }
        }
        throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
    }
    public synchronized void cerrarSesion() {
        sesionActiva = null;
    }
    public synchronized Recepcionista getSesionActiva() {
        return sesionActiva;
    }
    public synchronized Huesped registrarHuesped(String identificacion, String nombre,
            String telefono, String correo) {
        if (esVacio(identificacion) || esVacio(nombre) || esVacio(telefono) || esVacio(correo)) {
            throw new IllegalArgumentException("Todos los campos del huésped son obligatorios.");
        }
        identificacion = identificacion.trim();
        nombre = nombre.trim();
        telefono = telefono.trim();
        correo = correo.trim();

        if (!esCorreoValido(correo)) {
            throw new IllegalArgumentException(
                    "El correo electrónico no es válido (ejemplo: nombre@dominio.com).");
        }
        if (buscarHuesped(identificacion) != null) {
            throw new IllegalArgumentException("Ya existe un huésped con la identificación " + identificacion + ".");
        }
        Huesped h = new Huesped(identificacion, nombre, telefono, correo);
        huespedes.add(h);
        return h;
    }
    public synchronized ArrayList<Huesped> obtenerHuespedes() {
        ArrayList<Huesped> copia = new ArrayList<>();
        for (Huesped h : huespedes) {
            copia.add(h);
        }
        return copia;
    }
    private Huesped buscarHuesped(String identificacion) {
        for (Huesped h : huespedes) {
            if (h.getIdentificacion().equals(identificacion)) {
                return h;
            }
        }
        return null;
    }
    public synchronized Habitacion registrarHabitacion(int numero, String tipo) {
        if (numero <= 0) {
            throw new IllegalArgumentException("El número de habitación debe ser un entero positivo.");
        }
        if (buscarHabitacion(numero) != null) {
            throw new IllegalArgumentException("Ya existe la habitación número " + numero + ".");
        }
        Habitacion h = HabitacionFactory.crearHabitacion(tipo, numero);
        if (h == null) {
            throw new IllegalArgumentException("Debe seleccionar un tipo de habitación válido.");
        }
        habitaciones.add(h);
        return h;
    }
    public synchronized ArrayList<Habitacion> obtenerHabitaciones() {
        ArrayList<Habitacion> copia = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            copia.add(h);
        }
        return copia;
    }
    private Habitacion buscarHabitacion(int numero) {
        for (Habitacion h : habitaciones) {
            if (h.getNumero() == numero) {
                return h;
            }
        }
        return null;
    }
    public synchronized Reserva registrarReserva(String identificacionHuesped, int numeroHabitacion,
            int cantidadPersonas, LocalDate entrada, LocalDate salida) {

        if (sesionActiva == null) {
            throw new IllegalArgumentException("Debe iniciar sesión para registrar reservas.");
        }
        Huesped huesped = buscarHuesped(identificacionHuesped);
        if (huesped == null) {
            throw new IllegalArgumentException("Debe seleccionar un huésped registrado.");
        }
        Habitacion habitacion = buscarHabitacion(numeroHabitacion);
        if (habitacion == null) {
            throw new IllegalArgumentException("Debe seleccionar una habitación registrada.");
        }
        if (cantidadPersonas <= 0) {
            throw new IllegalArgumentException("La cantidad de personas debe ser mayor que cero.");
        }
        if (cantidadPersonas > habitacion.getCapacidadMaxima()) {
            throw new IllegalArgumentException("La habitación " + habitacion.getNumero() + " ("
                    + habitacion.getTipo() + ") admite máximo " + habitacion.getCapacidadMaxima()
                    + " persona(s).");
        }
        if (entrada == null || salida == null) {
            throw new IllegalArgumentException("Las fechas de entrada y salida son obligatorias.");
        }
        if (entrada.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de entrada no puede ser anterior a la fecha actual.");
        }
        if (!salida.isAfter(entrada)) {
            throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada.");
        }
        if (hayConflicto(habitacion, entrada, salida)) {
            throw new IllegalArgumentException("La habitación " + habitacion.getNumero()
                    + " no está disponible durante todo el período solicitado.");
        }
        contadorReservas++;
        Reserva reserva = new Reserva(generarCodigo(contadorReservas), huesped, habitacion,
                cantidadPersonas, entrada, salida, sesionActiva);
        reservas.add(reserva);
        return reserva;
    }
    private String generarCodigo(int numero) {
        if (numero < 10) {
            return "R00" + numero;
        } else if (numero < 100) {
            return "R0" + numero;
        } else {
            return "R" + numero;
        }
    }
    private boolean hayConflicto(Habitacion habitacion, LocalDate entrada, LocalDate salida) {
        for (Reserva r : reservas) {
            if (r.getHabitacion().getNumero() == habitacion.getNumero()
                    && r.bloqueaDisponibilidad()
                    && r.seSuperponeCon(entrada, salida)) {
                return true;
            }
        }
        return false;
    }
    public synchronized ArrayList<Reserva> obtenerReservas() {
        ArrayList<Reserva> copia = new ArrayList<>();
        for (Reserva r : reservas) {
            copia.add(r);
        }
        return copia;
    }
    private Reserva buscarReserva(String codigo) {
        if (esVacio(codigo)) {
            throw new IllegalArgumentException("Debe indicar el código de la reserva.");
        }
        for (Reserva r : reservas) {
            if (r.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return r;
            }
        }
        throw new IllegalArgumentException("La reserva " + codigo.trim() + " no existe.");
    }
    public synchronized ResultadoDisponibilidad consultarDisponibilidad(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("Debe seleccionar una fecha.");
        }
        int cantidad = CalculosRecursivos.contarHabitacionesDisponibles(habitaciones, reservas, fecha);

        ArrayList<Habitacion> libres = new ArrayList<>();
        for (Habitacion h : habitaciones) {
            if (CalculosRecursivos.estaDisponible(h, reservas, fecha)) {
                libres.add(h);
            }
        }
        return new ResultadoDisponibilidad(fecha, cantidad, libres);
    }
    public synchronized Reserva realizarCheckIn(String codigo) {
        Reserva r = buscarReserva(codigo);
        if (!r.getEstado().equals(Reserva.CONFIRMADA)) {
            throw new IllegalArgumentException("Solo se puede hacer check-in a reservas Confirmadas "
                    + "(estado actual: " + r.getEstado() + ").");
        }
        if (!LocalDate.now().equals(r.getFechaEntrada())) {
            throw new IllegalArgumentException("El check-in solo puede realizarse en la fecha de entrada "
                    + "programada (" + r.getFechaEntrada() + ").");
        }
        r.cambiarEstado(Reserva.HOSPEDADO);
        return r;
    }
    public synchronized Reserva realizarCheckOut(String codigo) {
        Reserva r = buscarReserva(codigo);
        if (!r.getEstado().equals(Reserva.HOSPEDADO)) {
            throw new IllegalArgumentException("Solo se puede hacer check-out a reservas en estado Hospedado "
                    + "(estado actual: " + r.getEstado() + ").");
        }
        r.registrarSalidaReal(LocalDate.now());   
        r.cambiarEstado(Reserva.FINALIZADA);
        return r;
    }
    public synchronized int marcarVencidas(LocalDate fechaActual) {
        int cantidad = 0;
        for (Reserva r : reservas) {
            if (r.getEstado().equals(Reserva.CONFIRMADA) && r.getFechaEntrada().isBefore(fechaActual)) {
                r.cambiarEstado(Reserva.VENCIDA);
                cantidad++;
            }
        }
        return cantidad;
    }
    public synchronized ArrayList<Reserva> simularVencimientos(LocalDate fechaReferencia) {
        if (fechaReferencia == null) {
            throw new IllegalArgumentException("Debe indicar una fecha de referencia.");
        }
        if (fechaReferencia.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de referencia no puede ser anterior a la fecha actual.");
        }
        ArrayList<Reserva> resultado = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getEstado().equals(Reserva.CONFIRMADA) && r.getFechaEntrada().isBefore(fechaReferencia)) {
                resultado.add(r);
            }
        }
        return resultado;
    }
    public synchronized IndicadoresHotel calcularIndicadores() {
        int total = habitaciones.size();
        int confirmadas = 0;
        int vencidas = 0;
        ArrayList<Integer> ocupadas = new ArrayList<>();   // números de habitación con huésped hospedado
        for (Reserva r : reservas) {
            String estado = r.getEstado();
            if (estado.equals(Reserva.CONFIRMADA)) {
                confirmadas++;
            } else if (estado.equals(Reserva.VENCIDA)) {
                vencidas++;
            } else if (estado.equals(Reserva.HOSPEDADO)) {
                Integer numero = r.getHabitacion().getNumero();
                if (!ocupadas.contains(numero)) {
                    ocupadas.add(numero);
                }
            }
        }
        long ingresos = CalculosRecursivos.calcularIngresosEsperados(reservas);
        return new IndicadoresHotel(total, ocupadas.size(), total - ocupadas.size(),
                confirmadas, vencidas, ingresos);
    }
    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
    private boolean esCorreoValido(String correo) {
        int arroba = correo.indexOf("@");
        if (arroba <= 0 || arroba != correo.lastIndexOf("@")) {
            return false;
        }
        String antes = correo.substring(0, arroba).trim();
        String despues = correo.substring(arroba + 1).trim();
        return !antes.isBlank() && !despues.isBlank() && despues.contains(".");
    }
}
