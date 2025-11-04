package grupo5;

import grupo5.buzones.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== INICIO PRUEBA CASO 3 ===");

        // Parámetros básicos
        int numClientes = 3;
        int numFiltros = 2;
        int numServidores = 2;
        int capacidadEntrada = 15;
        int capacidadEntrega = 10;

        //Crear buzones
        BuzonEntrada buzonEntrada = new BuzonEntrada(capacidadEntrada);
        BuzonCuarentena buzonCuarentena = new BuzonCuarentena();
        BuzonEntrega buzonEntrega = new BuzonEntrega(capacidadEntrega,numServidores);

        // Crear actores
        Cliente[] clientes = new Cliente[numClientes];
        Filtro[] filtros = new Filtro[numFiltros];
        ServidorEntrega[] servidores = new ServidorEntrega[numServidores];
        ManejadorCuarentena manejador = new ManejadorCuarentena(buzonCuarentena, buzonEntrega);

        //Inicializar clientes
        for (int i = 0; i < numClientes; i++) {
            clientes[i] = new Cliente(5,buzonEntrada);
        }

        // Inicializar filtros
        for (int i = 0; i < numFiltros; i++) {
            filtros[i] = new Filtro(i + 1, buzonEntrada, buzonCuarentena, buzonEntrega, numClientes);
        }

        // Inicializar servidores
        for (int i = 0; i < numServidores; i++) {
            servidores[i] = new ServidorEntrega(buzonEntrega,numServidores);
        }

        // Lanzar hilos en orden lógico
        manejador.start(); // Manejador primero (puede estar “en espera”)
        for (Filtro filtro : filtros) filtro.start();
        for (ServidorEntrega servidor : servidores) servidor.start();
        for (Cliente cliente : clientes) cliente.start();

        //  Esperar que todos los clientes terminen
        for (Cliente cliente : clientes) {
            try {
                cliente.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //  Esperar filtros
        for (Filtro filtro : filtros) {
            try {
                filtro.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //  Esperar manejador
        try {
            manejador.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 🔹 Esperar servidores
        for (ServidorEntrega servidor : servidores) {
            try {
                servidor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        
        }

        

        System.out.println("=== FIN DE EJECUCIÓN ===");
        System.out.println("Buzón de entrada vacío: " + buzonEntrada.vacio());
        System.out.println("Buzón de cuarentena vacío: " + buzonCuarentena.vacio());
        System.out.println("Buzón de entrega vacío: " + buzonEntrega.vacio());
    }
}
