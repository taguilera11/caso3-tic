package grupo5.buzones;

import grupo5.Correo;

import java.util.Queue;

public abstract class BuzonBasico implements Buzon {
    protected Queue<Correo> cola;
    protected int capacidad;

    public BuzonBasico(int capacidad) {
        this.capacidad = capacidad;
        this.cola = new java.util.LinkedList<>();
    }

    @Override
    public synchronized void depositar(Thread thread,Correo correo) {
        if (cola.size() < capacidad) {
            cola.add(correo);
        }
    }

    @Override
    public synchronized Correo extraer() {
        if (!cola.isEmpty()) return cola.poll();
        return null;
    }

    @Override
    public synchronized boolean vacio() {
        return cola.isEmpty();
    }

    public synchronized boolean lleno(){
        return cola.size() >= capacidad;
    }
}
