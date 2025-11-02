package grupo5;

import grupo5.buzones.BuzonBasico;
import grupo5.buzones.BuzonEntrada;

public class Main {
    public static void main(String[] args) {
        Cliente[] clientes = new Cliente[5];
        BuzonEntrada buzon = new BuzonEntrada(25);

        for (int i = 0; i < clientes.length; i++) {
            clientes[i] = new Cliente(5, buzon);
            clientes[i].start();
        }

        for (int i = 0; i < clientes.length; i++) {
            try {
                clientes[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(buzon.lleno());

        for (int i = 0; i < 25; i++) {
            Correo correo = buzon.extraer();
            System.out.println(correo.toString());
        }
    }
}