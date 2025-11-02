package grupo5;

import grupo5.buzones.BuzonCuarentena;
import grupo5.buzones.BuzonEntrega;
import java.util.Random;

public class ManejadorCuarentena extends Thread {
    private BuzonCuarentena cuarentena;
    private BuzonEntrega entrega;
    private boolean activo;
    private Random random = new Random();


    public ManejadorCuarentena(BuzonCuarentena cuarentena,BuzonEntrega entrega){

        this.cuarentena=cuarentena;
        this.entrega=entrega;

    }

    @Override
    public void run(){
        activo=true;

        while (activo){

            try{
                revisarCorreos();

                if(!activo) break;

                Thread.sleep(1000); //No consume CPU por 1 segundo para luego revisar el buzon de cuarentena
            }
            catch(InterruptedException e){
                System.err.println("[MANEJADOR CUARENTENA] interrumpido.");
                Thread.currentThread().interrupt();
                break;
            }
            
        }

        System.out.println("[MANEJADOR CUARENTENA] terminado.");

        }

    public synchronized void revisarCorreos() throws InterruptedException{

        if (cuarentena.obtenerMensajes().isEmpty()){

            return;
        }

        for (Correo c: cuarentena.obtenerMensajes()){

            //Si se llega al FIN desactivar al manejador
            if(c.esFin()){

                activo=false;
                System.out.println("[MANEJADOR CUARENTENA] recibió FIN, cerrando...");
                this.interrupt();
                return;

            }

            //Decrementar tiempo cuarentena
            long nuevoTiempo= c.getTiempoCuarentena()-1000;
            c.setTiempoCuarentena(nuevoTiempo);
            
            //Escenario en que el tiempo llegue a 0

            if (c.getTiempoCuarentena()<=0){
                int numero =random.nextInt(21)+1;// genera un numero del 1 al 21

                if (numero % 7 == 0){
                    //Lo descartamos
                    cuarentena.remover(c);
                    System.out.println("[MANEJADOR CUARENTENA] descartó correo SPAM id=" + c.getId());

                }
                else{ //Mandarlo al buzon de entrega

                    cuarentena.remover(c);
                    depositarSemiActivo(c, entrega);
                    System.out.println("[MANEJADOR CUARENTENA] liberó correo id=" + c.getId() + " a entrega");

                }
            }


            }
        }

    private void depositarSemiActivo(Correo correo, BuzonEntrega buzon) throws InterruptedException {
        int reintentos = 0;
        while (true) {
            synchronized (buzon) {
                if (!buzon.lleno()) {
                    buzon.depositar(correo, this);
                    return; //deposito exitoso
                }
            }

        // Espera semi-activa (caso buzon lleno) cuenta cuántas veces el hilo ha intentado depositar un mensaje sin lograrlo
        if (reintentos % 5 == 0)
            Thread.sleep(10);  //pausa breve
        else
            Thread.yield();    //cede CPU

        reintentos++;
        }
    }


    }

