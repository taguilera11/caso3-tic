package grupo5;

import grupo5.buzones.BuzonBasico;
import grupo5.buzones.BuzonEntrada;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        Cliente[] clientes = new Cliente[5];
        BuzonEntrada buzon = new BuzonEntrada(10);

        for (int i = 0; i < clientes.length; i++) {
            clientes[i] = new Cliente(i, buzon);
            clientes[i].start();
        }


    }
}