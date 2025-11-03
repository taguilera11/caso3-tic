package grupo5;

import grupo5.buzones.BuzonCuarentena;
import grupo5.buzones.BuzonEntrada;
import grupo5.buzones.BuzonEntrega;

import java.util.ArrayList;
import java.util.List;

public class Coordinador {
    private Config cfg;
    private BuzonEntrada buzonEntrada;
    private BuzonEntrega buzonEntrega;
    private BuzonCuarentena buzonCuarentena;
    private List<Cliente> hilosClientes;
    private List<Filtro> hilosFiltros;
    private List<ServidorEntrega> hilosServidores;
    private ManejadorCuarentena manejadorCuarentena;

    public Coordinador(Config cfg) {
        this.cfg = cfg;
        this.buzonEntrada = new BuzonEntrada(cfg.getCapacidadEntrada());
        this.buzonCuarentena = new BuzonCuarentena();
        this.buzonEntrega = new BuzonEntrega(cfg.getCapacidadEntrega(), cfg.getNumeroServidores());
        this.hilosClientes = new ArrayList<Cliente>(cfg.getNumeroClientes());
        this.hilosFiltros = new ArrayList<Filtro>(cfg.getNumeroFiltros());
        this.hilosServidores = new ArrayList<ServidorEntrega>(cfg.getNumeroServidores());
        this.manejadorCuarentena = new ManejadorCuarentena(buzonCuarentena, buzonEntrega);
    }

    public void inicializar() {
        //Iniciar clientes productores
        for (int i = 0; i < cfg.getNumeroClientes(); i++) {
            Cliente cliente = new Cliente(cfg.getNumeroCorreosPorCliente(), buzonEntrada);
            hilosClientes.add(cliente);
        }

        //Iniciar filtros de spam
        for (int i = 0; i < cfg.getNumeroFiltros(); i++) {
            Filtro filtro = new Filtro(i + 1, buzonEntrada, buzonCuarentena, buzonEntrega, cfg.getNumeroClientes());
            hilosFiltros.add(filtro);
        }

        //Crear servidores de entrega
        for (int i = 0; i < cfg.getNumeroServidores(); i++) {
            ServidorEntrega servidor = new ServidorEntrega(i + 1, buzonEntrega);
            hilosServidores.add(servidor);
        }

        lanzarHilos();
        esperarFin();
        verificarCierre();
    }

    public void lanzarHilos() {
        manejadorCuarentena.start(); // Manejador primero (puede estar “en espera”)
        for (Filtro filtro : hilosFiltros) filtro.start();
        for (ServidorEntrega servidor : hilosServidores) servidor.start();
        for (Cliente cliente : hilosClientes) cliente.start();

    }

    public void esperarFin() {
        //  Esperar que todos los clientes terminen
        for (Cliente cliente : hilosClientes) {
            try {
                cliente.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //  Esperar filtros
        for (Filtro filtro : hilosFiltros) {
            try {
                filtro.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //  Esperar manejador
        try {
            manejadorCuarentena.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //  Esperar servidores
        for (ServidorEntrega servidor : hilosServidores) {
            try {
                servidor.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void verificarCierre() {
        System.out.println("Buzon Entrada: " + buzonEntrada.vacio() + ", Buzon Cuarentena: " + buzonCuarentena.vacio() + ", Buzon Entrega: " + buzonEntrega.vacio());
        if (buzonEntrada.vacio() && buzonCuarentena.vacio() && buzonEntrega.vacio()) {
            System.out.println("\nTODOS LOS BUZONES ESTÁN VACÍOS. EL SISTEMA PUEDE CERRARSE.\n");
            System.out.println("=== FIN DE EJECUCIÓN ===");
        } else {
            System.out.println("Buzon Entrada: " + buzonEntrada.vacio() + ", Buzon Cuarentena: " + buzonCuarentena.vacio() + ", Buzon Entrega: " + buzonEntrega.vacio());
            System.out.println("Aún hay mensajes en los buzones. El sistema no puede cerrarse.");
        }
    }

    public static void main(String[] args) {
        Config cfg = Config.getInstance("/config.txt");
        Coordinador coordinador = new Coordinador(cfg);
        System.out.println(coordinador.toString());
        coordinador.inicializar();
    }

    @Override
    public String toString() {
        return "Coordinador{" +
                "cfg=" + cfg +
                ", buzonEntrada=" + buzonEntrada +
                ", buzonCuarentena=" + buzonCuarentena +
                ", hilosClientes=" + hilosClientes +
                ", hilosFiltros=" + hilosFiltros +
                ", hilosServidores=" + hilosServidores +
                '}';
    }
}