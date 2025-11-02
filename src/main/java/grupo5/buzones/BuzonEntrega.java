package grupo5.buzones;

import grupo5.Correo;
import grupo5.TipoCorreo;

public class BuzonEntrega extends BuzonBasico{
    private int numeroServidores;

    public BuzonEntrega(int capacidad, int numeroServidores) {
        super(capacidad);
        this.numeroServidores = numeroServidores;
    }

    @Override
    public synchronized void depositar(Correo correo, Thread thread) {
        while (lleno()) {

            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }   

        cola.add(correo);
        System.out.println("[BUZÓN ENTREGA] " + thread.getName() +
            " depositó: " + correo.getTipo() + " (id=" + correo.getId() + ")");

        // Si se deposita un FIN  replicar para todos los servidores
        if (correo.esFin()) {
            replicarFin();
        }

        notifyAll(); // despierta consumidores
    }

    @Override
    public synchronized Correo extraer() {
        try {
            // Espera pasiva mientras el buzón esté vacío
            while (cola.isEmpty()) {
                wait();
            }

            Correo correo = cola.poll();

            System.out.println("[BUZÓN ENTREGA] se extrajo: " + correo.getTipo() + " (id=" + correo.getId() + ")");
            System.out.println("Restantes en cola " + cola.size());
            // Libera espacio y notifica a los productores
            notifyAll();

            return correo;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    //Duplica un mensaje FIN tantas veces como servidores haya,de modo que todos los servidores puedan recibir su propio FIN.
    public synchronized void replicarFin() {
        for (int i = 1; i < numeroServidores; i++) {
            Correo fin = new Correo(TipoCorreo.FIN, false, "BuzonEntrega-FIN");
            cola.add(fin);
        }

        System.out.println("[BUZÓN ENTREGA] replicó FIN para " + numeroServidores + " servidores");
        notifyAll();
    }
}

