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

    // static: si se cambia de usuario (nuevo FrmHotel / nuevo controlador) se sigue sabiendo
    // si hay procesos activos, y así no se inicia una segunda pareja de hilos.
    // Solo se leen y escriben desde el EDT (el botón y el invokeLater final), por eso
    // no necesitan sincronización.
    private static boolean procesosActivos = false;
    private static Thread hiloSupervisor;
    private static Thread hiloActualizador;

    private final GestorHotel gestor = GestorHotel.getInstancia();

    private ProcesosListener listener;   // quién recibe los avisos de los hilos (la vista)
    private int hilosPendientes = 0;     // cuántos de los dos hilos aún no terminan

    // ==================================================================
    // Sesión
    // ==================================================================

    /** Texto para la etiqueta del recepcionista activo. */
    public String getNombreRecepcionistaActivo() {
        if (gestor.getSesionActiva() == null) {
            return "";
        }
        return gestor.getSesionActiva().getNombre();
    }

    /** Llamar solo cuando el usuario CONFIRMÓ el diálogo de "Cambiar usuario". Los datos se conservan. */
    public void cerrarSesion() {
        gestor.cerrarSesion();
    }

    /**
     * Revisión de vencidas al cargar la sesión (llamar al abrir FrmHotel).
     * Devuelve el mensaje de éxito para mostrar en un JOptionPane.
     */
    public String revisarVencidasAlCargarSesion() {
        int actualizadas = gestor.marcarVencidas(LocalDate.now());
        return "Revisión de reservas vencidas realizada correctamente.\n"
                + "Reservas actualizadas a Vencida: " + actualizadas;
    }

    // ==================================================================
    // Huéspedes
    // ==================================================================

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

    /** Textos para el JComboBox de huéspedes. La posición del combo coincide con la posición en la lista. */
    public ArrayList<String> obtenerEtiquetasHuespedes() {
        ArrayList<String> etiquetas = new ArrayList<>();
        for (Huesped h : gestor.obtenerHuespedes()) {
            etiquetas.add(h.getEtiqueta());
        }
        return etiquetas;
    }

    // ==================================================================
    // Habitaciones
    // ==================================================================

    /** Para llenar el JComboBox de tipos (Individual, Doble, Familiar). */
    public String[] obtenerTiposHabitacion() {
        return HabitacionFactory.TIPOS;
    }

    /** tipo = jCBTipo.getSelectedItem().toString() (igual que en el ejemplo de clase). */
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

    // ==================================================================
    // Reservas, check-in y check-out
    // ==================================================================

    /**
     * @param posicionHuesped    jCBHuesped.getSelectedIndex() (-1 si no hay selección)
     * @param posicionHabitacion jCBHabitacion.getSelectedIndex()
     * @param personasTexto      texto del campo cantidad de personas
     * @param entradaTexto       texto "aaaa-mm-dd"
     * @param salidaTexto        texto "aaaa-mm-dd"
     * @return la reserva creada (para mostrar su código)
     */
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

    // ==================================================================
    // Disponibilidad y simulación
    // ==================================================================

    /** Texto listo para mostrar, por ejemplo: "Habitaciones disponibles el 2026-11-25: 3" + números. */
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

    /** Solo informativa: NO modifica los estados reales. */
    public ArrayList<Object[]> simularVencimientos(String fechaReferenciaTexto) {
        LocalDate fechaReferencia = convertirFecha(fechaReferenciaTexto);
        ArrayList<Object[]> filas = new ArrayList<>();
        for (Reserva r : gestor.simularVencimientos(fechaReferencia)) {
            filas.add(new Object[]{r.getCodigo(), r.getHuesped().getNombreCompleto(),
                r.getHabitacion().getNumero(), r.getFechaEntrada().toString(), r.getEstado()});
        }
        return filas;
    }

    // ==================================================================
    // Indicadores y procesos concurrentes
    // ==================================================================

    /** Foto actual de los indicadores (para mostrarlos al abrir la ventana). */
    public IndicadoresHotel obtenerIndicadores() {
        return gestor.calcularIndicadores();
    }

    public boolean hayProcesosEnEjecucion() {
        return procesosActivos;
    }

    /**
     * Botón "Ejecutar procesos de supervisión" (se llama desde el EDT). Crea los dos Thread,
     * los inicia sin esperar al otro y REGRESA INMEDIATAMENTE (no usa join en el EDT).
     * Los resultados llegan al listener dentro del EDT.
     */
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

    // ---- Métodos que llaman los hilos (corren en los hilos, NO en el EDT) ----

    /** Pasa al EDT los indicadores intermedios del ActualizadorIndicadores. */
    public void publicarIndicadores(IndicadoresHotel indicadores) {
        SwingUtilities.invokeLater(() -> listener.indicadoresActualizados(indicadores));
    }

    /** Pasa al EDT el mensaje final del SupervisorReservas. */
    public void publicarResumenSupervision(String mensaje) {
        SwingUtilities.invokeLater(() -> listener.supervisionFinalizada(mensaje));
    }

    /**
     * Cada hilo la llama al terminar. El ÚLTIMO en terminar hace la actualización final de
     * los indicadores (en su propio hilo, sin crear un tercero y sin bloquear el EDT)
     * y entrega el resultado definitivo a la vista en el EDT.
     */
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

    // ==================================================================
    // Auxiliares de conversión
    // ==================================================================

    /** Texto "aaaa-mm-dd" -> LocalDate. */
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
