package grupo5.buzones;

import grupo5.Correo;

public interface Buzon {
    void depositar(Correo correo, Thread thread);
    Correo extraer();
    boolean vacio();
}
