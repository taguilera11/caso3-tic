package grupo5;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import grupo5.buzones.BuzonEntrega;

public class ServidorEntrega extends Thread {
    private static final AtomicInteger counter = new AtomicInteger(1);
    private int idServidor;
    private BuzonEntrega buzonEntrega;
    private int finRecibidos;
    private int totalServidores;
    private boolean activo;
    private Random random = new Random();
    


    public ServidorEntrega(BuzonEntrega buzonEntrega,int totalServidores){

        this.idServidor = counter.getAndIncrement();
        this.buzonEntrega=buzonEntrega;
        this.totalServidores=totalServidores;
    }

    @Override
    public void run(){
        System.out.println("[SERVIDOR " + idServidor + "] iniciado.");

        activo=true;

        //Espera activa y chequea desde el inicio del sistema si algo llega al buzon de entrega
        while(activo){

            Correo c = buzonEntrega.extraer();

            if(c ==null){

                continue;
            }
            //Deje el mennsaje de cuando si se condicionaba pero dada la correcion en Slack ya no se condiciona el arranque
            if (c.esInicio()){
                System.out.println("[SERVIDOR " + idServidor + "] recibió INICIO. Listo para procesar correos desde el arranque.");
                continue;
            }


            if( c.esFin()){

                //esto era para corregir lo del correo restante, pero como se corrigio en filtros no hace nada pero pue slo dejo por si acaso
                synchronized (this) {
                finRecibidos++;
                if (finRecibidos == totalServidores) {
                     //ultimo servidor: vacía la cola
                    while (!buzonEntrega.vacio()) {
                        Correo restante = buzonEntrega.extraer();
                        if (restante != null){
                            procesar(restante);
                            System.out.println("[SERVIDOR " + idServidor + "] vaciando residual: " + restante);
                        }
                    }
                }
            }
                System.out.println("[SERVIDOR " + idServidor + "] recibió FIN. Terminando...");
                activo=false;
                break;

            }

            procesar(c);
        }

        System.out.println("[SERVIDOR " + idServidor + "] finalizado.");

    }
    

    private void procesar(Correo c){

        try {
            System.out.println("[SERVIDOR " + idServidor + "] procesando correo id=" + c.getId()
                    + " origen=" + c.getOrigen());
            Thread.sleep(100 + random.nextInt(200)); //tiempo de procesamiento
            System.out.println("[SERVIDOR " + idServidor + "] entregó correo id=" + c.getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }

}