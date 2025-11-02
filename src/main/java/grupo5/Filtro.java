package grupo5;

import grupo5.buzones.BuzonCuarentena;
import grupo5.buzones.BuzonEntrada;
import grupo5.buzones.BuzonEntrega;
import java.util.Random;

public class Filtro extends Thread{
    private int idFiltro;
    private BuzonEntrada buzonEntrada;
    private BuzonCuarentena buzonCuarentena;
    private BuzonEntrega buzonEntrega;
    private  int finesRecibidos;
    private  static int finesTotalesRecibidos;
    private int totalClientes;
    private static boolean finEnviado=false;
    private Random random= new Random();
    

    public Filtro(int idFiltro, BuzonEntrada buzonEntrada, BuzonCuarentena buzonCuarentena, BuzonEntrega buzonEntrega, int totalClientes) {
        this.idFiltro = idFiltro;
        this.buzonEntrada = buzonEntrada;
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
        this.finesRecibidos = 0;
        this.totalClientes = totalClientes;
    }

    @Override
    public void run() {
        System.out.println("[FILTRO " + idFiltro + "] iniciado.");
        try {
            while (!finEnviado) {

                Correo c = buzonEntrada.extraer();
                if (c == null) continue;

                //Procesar INICIO
                if (c.esInicio()) {
                    System.out.println("[FILTRO " + idFiltro + "] recibió INICIO de " + c.getOrigen());
                    // Reenviar al buzón de entrega para los servidores
                    buzonEntrega.depositar(c, this);
                    continue;
                }

                //Procesar FIN
                if (c.esFin()) {
                    finesRecibidos++;

                    synchronized(Filtro.class){

                        finesTotalesRecibidos++;
                        System.out.println("[FILTRO " + idFiltro + "] recibió FIN (" + finesRecibidos + "/" + totalClientes + ")");
                    
                        // Si ya recibió todos los FIN 
                        if (!finEnviado && finesTotalesRecibidos >= totalClientes) {
                            // Espera hasta que la cuarentena esté vacía
                            System.out.println("[FILTRO " + idFiltro + "] esperando a que cuarentena se vacíe antes de enviar FIN global...");
                            while (!buzonCuarentena.vacio()) {
                            Thread.sleep(100); // espera semi-activa
                            }

                            enviarFinGlobal();
                            finEnviado = true;
                            System.out.println("[FILTRO " + idFiltro + "] cuarentena vacía. FIN global enviado a entrega.");
                            break;
                        }

                    }

                    continue;
                }

                // Procesar correos normales
                if (c.esNormal()) {
                    procesarCorreo(c);
                }
            }

            System.out.println("[FILTRO " + idFiltro + "] terminado.");

        } catch (InterruptedException e) {
            System.err.println("[FILTRO " + idFiltro + "] interrumpido.");
        }
    }

    private void procesarCorreo(Correo c) throws InterruptedException {
        if (c.getEsSpam()) {
            // Asignar tiempo aleatorio de cuarentena entre 10 y 20 segundos
            long tiempo = 10000 + random.nextInt(10000);
            c.setTiempoCuarentena(tiempo);

            //Espera semi activa
            depositarSemiActivo(c, buzonCuarentena);

            System.out.println("[FILTRO " + idFiltro + "] envió SPAM a cuarentena (Tiempo=" + tiempo + "ms)");
        } else {
            //Espera semiactiva
            depositarSemiActivo(c, buzonEntrega);
            System.out.println("[FILTRO " + idFiltro + "] envió correo válido a entrega");
        }


    }

    private void depositarSemiActivo(Correo correo, Object buzon) throws InterruptedException {
        int reintentos = 0;
        while (true) {
            synchronized (buzon) {
                if (!(buzon instanceof BuzonCuarentena) && !(buzon instanceof BuzonEntrega))
                    throw new IllegalArgumentException("Buzón no válido para depósito");

                if (buzon instanceof BuzonCuarentena bc) {
                    if (!bc.lleno()) {
                        bc.depositar(correo, this);
                        break;
                    }
                } else if (buzon instanceof BuzonEntrega be) {
                    if (!be.lleno()) {
                        be.depositar(correo, this);
                        break;
                    }
                }
            }

            // Espera semi-activa por si el buzon esta lleno
            if (reintentos % 5 == 0)
                Thread.sleep(10);  // pausa corta cada pocos intentos
            else
                Thread.yield();    //cede CPU temporalmente

            reintentos++;
        }
    }

    private void enviarFinGlobal() throws InterruptedException {
        //Crea un correo FIN
        Correo finCorreo = new Correo(TipoCorreo.FIN, false, "Filtro-" + idFiltro);
        buzonCuarentena.depositar(finCorreo, this);
        buzonEntrega.depositar(finCorreo, this);
        System.out.println("[FILTRO " + idFiltro + "] envió FIN global a cuarentena y entrega");

        //Despierta a los demás filtros que puedan estar bloqueados en extraer()
        synchronized (buzonEntrada) {
        buzonEntrada.notifyAll();
        }
    }

}

