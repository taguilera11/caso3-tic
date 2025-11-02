package grupo5.buzones;

import grupo5.Correo;

public class BuzonEntrada extends BuzonBasico{
    public BuzonEntrada(int capacidad) {
        super(capacidad);
    }

    @Override
    public synchronized void depositar(Correo correo) {
        while (lleno()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        super.depositar(correo);
        notify();
    }
}
