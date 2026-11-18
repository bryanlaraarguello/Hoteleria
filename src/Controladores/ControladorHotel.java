/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controladores;
import Controladores.ProcesosListener;
import javax.swing.SwingUtilities;
import java.time.format.DateTimeParseException;
import Hilos.ActualizadorIndicadores;
import Hilos.SupervisorReservas;
import Modelo.GestorHotel;
import Modelo.Habitacion;
import Modelo.HabitacionFactory;
import Modelo.Huesped;
import Modelo.IndicadoresHotel;
import Modelo.Reserva;
import Modelo.ResultadoDisponibilidad;
import java.util.ArrayList;
import java.time.LocalDate;
/**
 *
 * @author bryan
 */
public class ControladorHotel {
    // Encabezados para los JTable (la vista crea su DefaultTableModel con ellos)
    public static final String[] COLUMNAS_HUESPEDES =
            {"Identificación", "Nombre completo", "Teléfono", "Correo"};
    public static final String[] COLUMNAS_HABITACIONES =
            {"Número", "Tipo", "Capacidad", "Tarifa por noche"};
    public static final String[] COLUMNAS_RESERVAS =
            {"Código", "Huésped", "Habitación", "Personas", "Entrada", "Salida",
             "Salida real", "Noches", "Importe", "Estado", "Recepcionista"};
    public static final String[] COLUMNAS_SIMULACION =
            {"Código", "Huésped", "Habitación", "Entrada", "Estado actual"};

    private static boolean procesosActivos = false;
    private static Thread hiloSupervisor;
    private static Thread hiloActualizador;

    private final GestorHotel gestor = GestorHotel.getInstancia();

    private ProcesosListener listener;  
    private int hilosPendientes = 0;     
    public String getNombreRecepcionistaActivo() {
        if (gestor.getSesionActiva() == null) {
            return "";
        }
        return gestor.getSesionActiva().getNombre();
    }

    public void cerrarSesion() {
        gestor.cerrarSesion();
    }
    public String revisarVencidasAlCargarSesion() {
        int actualizadas = gestor.marcarVencidas(LocalDate.now());
        return "Revisión de reservas vencidas realizada correctamente.\n"
                + "Reservas actualizadas a Vencida: " + actualizadas;
    }
    public void registrarHuesped(String identificacion, String nombre, String telefono, String correo) {
        gestor.registrarHuesped(identificacion, nombre, telefono, correo);
    }

    /** Filas para el JTable de huéspedes (cada fila es un Object[] para addRow). */
    public ArrayList<Object[]> obtenerFilasHuespedes() {
        ArrayList<Object[]> filas = new ArrayList<>();
        for (Huesped h : gestor.obtenerHuespedes()) {
            filas.add(new Object[]{h.getIdentificacion(), h.getNombreCompleto(), h.getTelefono(), h.getCorreo()});
        }
        return filas;
    }
    public ArrayList<String> obtenerEtiquetasHuespedes() {
        ArrayList<String> etiquetas = new ArrayList<>();
        for (Huesped h : gestor.obtenerHuespedes()) {
            etiquetas.add(h.getEtiqueta());
        }
        return etiquetas;
    }
    public String[] obtenerTiposHabitacion() {
        return HabitacionFactory.TIPOS;
    }
    public void registrarHabitacion(String numeroTexto, String tipo) {
        if (numeroTexto == null || numeroTexto.isBlank()) {
            throw new IllegalArgumentException("El número de habitación es obligatorio.");
        }
        int numero;
        try {
            numero = Integer.parseInt(numeroTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El número de habitación debe ser un número entero.");
        }
        gestor.registrarHabitacion(numero, tipo);
    }
    
    public String[] obtenerDetallesTipoHabitacion(String tipo) {

    Habitacion habitacion =
        HabitacionFactory.crearHabitacion(tipo, 1);

    if (habitacion == null) {
        return null;
    }

    return new String[]{
        String.valueOf(habitacion.getCapacidadMaxima()),
        String.valueOf(habitacion.getTarifaPorNoche())
    };
    }

    public ArrayList<Object[]> obtenerFilasHabitaciones() {
        ArrayList<Object[]> filas = new ArrayList<>();
        for (Habitacion h : gestor.obtenerHabitaciones()) {   // referencia polimórfica
            filas.add(new Object[]{h.getNumero(), h.getTipo(), h.getCapacidadMaxima(),
                colones(h.getTarifaPorNoche())});
        }
        return filas;
    }

    /** Textos para el JComboBox de habitaciones. La posición del combo coincide con la posición en la lista. */
    public ArrayList<String> obtenerEtiquetasHabitaciones() {
        ArrayList<String> etiquetas = new ArrayList<>();
        for (Habitacion h : gestor.obtenerHabitaciones()) {
            etiquetas.add(h.getEtiqueta());
        }
        return etiquetas;
    }

    public Reserva registrarReserva(int posicionHuesped, int posicionHabitacion, String personasTexto,
            String entradaTexto, String salidaTexto) {
        ArrayList<Huesped> huespedes = gestor.obtenerHuespedes();
        ArrayList<Habitacion> habitaciones = gestor.obtenerHabitaciones();
        if (posicionHuesped < 0 || posicionHuesped >= huespedes.size()) {
            throw new IllegalArgumentException("Debe seleccionar un huésped.");
        }
        if (posicionHabitacion < 0 || posicionHabitacion >= habitaciones.size()) {
            throw new IllegalArgumentException("Debe seleccionar una habitación.");
        }
        if (personasTexto == null || personasTexto.isBlank()) {
            throw new IllegalArgumentException("La cantidad de personas es obligatoria.");
        }
        int personas;
        try {
            personas = Integer.parseInt(personasTexto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La cantidad de personas debe ser un número entero.");
        }
        LocalDate entrada = convertirFecha(entradaTexto);
        LocalDate salida = convertirFecha(salidaTexto);
        return gestor.registrarReserva(huespedes.get(posicionHuesped).getIdentificacion(),
                habitaciones.get(posicionHabitacion).getNumero(), personas, entrada, salida);
    }

    public ArrayList<Object[]> obtenerFilasReservas() {
        ArrayList<Object[]> filas = new ArrayList<>();
        for (Reserva r : gestor.obtenerReservas()) {
            String salidaReal = "";   // vacío mientras no haya check-out
            if (r.getFechaSalidaReal() != null) {
                salidaReal = r.getFechaSalidaReal().toString();
            }
            filas.add(new Object[]{
                r.getCodigo(),
                r.getHuesped().getNombreCompleto(),
                r.getHabitacion().getNumero(),
                r.getCantidadPersonas(),
                r.getFechaEntrada().toString(),
                r.getFechaSalida().toString(),
                salidaReal,
                r.getNoches(),
                colones(r.getImporteTotal()),
                r.getEstado(),
                r.getRecepcionista().getNombre()
            });
        }
        return filas;
    }

    public Reserva realizarCheckIn(String codigoReserva) {
        return gestor.realizarCheckIn(codigoReserva);
    }

    public Reserva realizarCheckOut(String codigoReserva) {
        return gestor.realizarCheckOut(codigoReserva);
    }

    public String consultarDisponibilidad(String fechaTexto) {
        LocalDate fecha = convertirFecha(fechaTexto);
        ResultadoDisponibilidad resultado = gestor.consultarDisponibilidad(fecha);
        String texto = "Habitaciones disponibles el " + resultado.getFecha() + ": " + resultado.getCantidad();
        if (resultado.getCantidad() > 0) {
            texto = texto + "\nNúmeros: ";
            ArrayList<Habitacion> libres = resultado.getHabitaciones();
            for (int i = 0; i < libres.size(); i++) {
                texto = texto + libres.get(i).getNumero();
                if (i < libres.size() - 1) {
                    texto = texto + ", ";
                }
            }
        }
        return texto;
    }

    
    public ArrayList<Object[]> simularVencimientos(String fechaReferenciaTexto) {
        LocalDate fechaReferencia = convertirFecha(fechaReferenciaTexto);
        ArrayList<Object[]> filas = new ArrayList<>();
        for (Reserva r : gestor.simularVencimientos(fechaReferencia)) {
            filas.add(new Object[]{r.getCodigo(), r.getHuesped().getNombreCompleto(),
                r.getHabitacion().getNumero(), r.getFechaEntrada().toString(), r.getEstado()});
        }
        return filas;
    }

    public IndicadoresHotel obtenerIndicadores() {
        return gestor.calcularIndicadores();
    }

    public boolean hayProcesosEnEjecucion() {
        return procesosActivos;
    }

    public void ejecutarProcesosSupervision(ProcesosListener listener) {
        if (procesosActivos) {
            throw new IllegalArgumentException("Los procesos de supervisión ya están en ejecución.");
        }
        procesosActivos = true;
        this.listener = listener;
        this.hilosPendientes = 2;

        hiloSupervisor = new Thread(new SupervisorReservas(gestor, this), "Hilo-SupervisorReservas");
        hiloActualizador = new Thread(new ActualizadorIndicadores(gestor, this), "Hilo-ActualizadorIndicadores");

        hiloSupervisor.start();      // ambos inician sin esperar al otro
        hiloActualizador.start();
    }

    /** Opcional: botón "Detener procesos". Interrumpe ambos hilos (terminan ordenadamente). */
    public void detenerProcesos() {
        if (hiloSupervisor != null) {
            hiloSupervisor.interrupt();
        }
        if (hiloActualizador != null) {
            hiloActualizador.interrupt();
        }
    }

    public void publicarIndicadores(IndicadoresHotel indicadores) {
        SwingUtilities.invokeLater(() -> listener.indicadoresActualizados(indicadores));
    }

    public void publicarResumenSupervision(String mensaje) {
        SwingUtilities.invokeLater(() -> listener.supervisionFinalizada(mensaje));
    }

    public void hiloTerminado() {
        if (descontarHiloPendiente()) {
            IndicadoresHotel finales = gestor.calcularIndicadores();
            SwingUtilities.invokeLater(() -> {
                hiloSupervisor = null;
                hiloActualizador = null;
                procesosActivos = false;
                listener.procesosFinalizados(finales);
            });
        }
    }

    /** synchronized: los dos hilos pueden terminar casi al mismo tiempo. true si ya no queda ninguno. */
    private synchronized boolean descontarHiloPendiente() {
        hilosPendientes--;
        return hilosPendientes == 0;
    }

    private LocalDate convertirFecha(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("La fecha es obligatoria (formato aaaa-mm-dd).");
        }
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha inválida: use el formato aaaa-mm-dd (ejemplo: 2026-12-25).");
        }
    }

    /** 75000 -> "₡75000" */
    private String colones(long monto) {
        return "₡" + monto;
    }
}
