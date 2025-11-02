package grupo5;

import grupo5.buzones.BuzonCuarentena;
import grupo5.buzones.BuzonEntrada;
import grupo5.buzones.BuzonEntrega;

public class Filtro extends Thread{
    private int idFiltro;
    private BuzonEntrada buzonEntrada;
    private BuzonCuarentena buzonCuarentena;
    private BuzonEntrega buzonEntrega;
    private int finesRecibidos;
    private int totalClientes;
    private boolean finEnviado;

    public Filtro(int idFiltro, BuzonEntrada buzonEntrada, BuzonCuarentena buzonCuarentena, BuzonEntrega buzonEntrega, int totalClientes) {
        this.idFiltro = idFiltro;
        this.buzonEntrada = buzonEntrada;
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
        this.finesRecibidos = 0;
        this.totalClientes = totalClientes;
        this.finEnviado = false;
    }

    @Override
    public void run() {

    }

    private void procesarCorreo(Correo correo){
        if (correo.getEsSpam()){

        }

    }

}
