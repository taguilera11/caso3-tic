package grupo5.buzones;

import grupo5.Correo;

public interface Buzon {
    void depositar(Thread thread, Correo correo);
    Correo extraer();
    boolean vacio();
}
