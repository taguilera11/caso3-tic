package grupo5;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import grupo5.buzones.BuzonEntrega;

public class ServidorEntrega extends Thread {
    private static final AtomicInteger counter = new AtomicInteger(1);
    private int idServidor;
    private BuzonEntrega buzonEntrega;
    private boolean activo;
    private boolean inicioRecibido=false;
    private Random random = new Random();
    


    public ServidorEntrega(int id, BuzonEntrega buzonEntrega){

        this.idServidor = counter.getAndIncrement();
        this.buzonEntrega=buzonEntrega;
    }

    @Override
    public void run(){
        System.out.println("[SERVIDOR " + idServidor + "] iniciado.");

        activo=true;


        while(activo){

            Correo c = buzonEntrega.extraer();

            if(c ==null){

                continue;
            }

            if (c.esInicio()){
                inicioRecibido=true;
                System.out.println("[SERVIDOR " + idServidor + "] recibió INICIO. Listo para procesar correos.");
                continue;
            }

            if (!inicioRecibido) {
                 // Ignora todo hasta recibir su inicio
                continue;
            }



            if( c.esFin()){
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

