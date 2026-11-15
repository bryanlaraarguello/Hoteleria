package Modelo;
public class HabitacionDoble extends Habitacion {
    
    public HabitacionDoble(int numero) {
        super(numero);
    }

    @Override
    public String getTipo() {
        return "Doble";
    }

    @Override
    public int getCapacidadMaxima() {
        return 2;
    }

    @Override
    public long getTarifaPorNoche() {
        return 40000;
    }
}
