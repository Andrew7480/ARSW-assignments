import java.util.ArrayList;
import java.util.List;
 
public class HiloContador extends Thread {
 
    private int inicio;
    private int fin;
 
    public HiloContador(int inicio, int fin) {
        this.inicio = inicio;
        this.fin = fin;
    }
 
    @Override
    public void run() {
        for (int contador = inicio; contador <= fin; contador++) {
            System.out.println(contador);
        }
    }
 
    public static void main(String[] args) throws InterruptedException {
 
        int num = 5000000;
        int numHilos = 1000;
 
        List<HiloContador> listaHilos = new ArrayList<HiloContador>();
 
        int rango = num / numHilos;
 
        long tiempoInicio = System.nanoTime();
 
        for (int i = 0; i < numHilos; i++) {
 
            int inicio = i * rango + 1;
       
            int fin;
            if (i == numHilos - 1) {
                fin = num;
            } else {
                fin = (i + 1) * rango;
            }
       
            listaHilos.add(new HiloContador(inicio, fin));
        }
       
        for (HiloContador hilo : listaHilos) {
            hilo.start();
        }
       
        for (HiloContador hilo : listaHilos) {
            hilo.join();
        }
 
        long tiempoFin = System.nanoTime();
 
        System.out.println("Tiempo: " +
                ((tiempoFin - tiempoInicio) / 1_000_000.0) + " ms");
    }
}