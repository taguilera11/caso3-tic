package grupo5.buzones;

import grupo5.Correo;

import java.util.ArrayList;
import java.util.List;

public class BuzonCuarentena extends BuzonBasico {

    public BuzonCuarentena() {
        super(Integer.MAX_VALUE);
    }

    @Override
    public synchronized void depositar(Correo correo, Thread thread) {
        cola.add(correo);
        // Notifica que hay mensajes disponibles
        notifyAll();
    }

    @Override
    public synchronized Correo extraer() {
        while (cola.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                return null;
            }
        }
        return cola.poll();
    }

    /*
      Devuelve una copia de los correos actuales sin eliminarlos.
      Esto permite al manejador de cuarentena iterar sobre ellos
      sin interferir con la cola principal.
     */
    public synchronized List<Correo> obtenerMensajes() {
        return new ArrayList<>(cola);
    }

    //Elimina un correo específico (por ejemplo, cuando el tiempo de cuarentena llega a 0 o se descarta).
    public synchronized void remover(Correo correo) {
        cola.remove(correo);
        notifyAll();
    }
}


