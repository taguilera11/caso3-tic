package grupo5.buzones;

import grupo5.Cliente;
import grupo5.Correo;

public class BuzonEntrada extends BuzonBasico{
    public BuzonEntrada(int capacidad) {
        super(capacidad);
    }

    @Override
    public synchronized void depositar(Correo correo, Thread cliente) {
        while (lleno()) {
            try {
                System.out.println("[BuzonEntrada]: Buzon lleno, el cliente " + ((Cliente)cliente).getIdCliente() + " espera para depositar el correo " + correo.getId());
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[Cliente "+((Cliente)cliente).getIdCliente()+"]: Depositando correo " + correo.getId());
        super.depositar(correo, cliente);
        notifyAll();
    }

    @Override
    public synchronized Correo extraer() {
        if (vacio()) {
            try {
                System.out.println("[BuzonEntrada]: Buzon vacio, el repartidor espera para extraer un correo.");
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        Correo correo = super.extraer();
        if (correo != null) {
        System.out.println("[BuzonEntrada]: Extrayendo correo " + correo.getId());
        }
        notify();
        return correo;
    }
}
