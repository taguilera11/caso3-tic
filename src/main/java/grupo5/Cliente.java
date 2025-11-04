package grupo5;

import grupo5.buzones.BuzonEntrada;

import java.util.concurrent.atomic.AtomicInteger;

public class Cliente extends Thread {
    private static final AtomicInteger counter = new AtomicInteger(1);

    private final int idCliente;
    private final int numCorreosAProducir;
    private BuzonEntrada buzon;

    public Cliente(int numCorreosAProducir, BuzonEntrada buzon) {
        this.idCliente = counter.getAndIncrement();
        this.numCorreosAProducir = numCorreosAProducir;
        this.buzon = buzon;
    }

    @Override
    public void run() {
        Correo primerCorreo = new Correo(TipoCorreo.INICIO, false, "Cliente "+this.idCliente);
        buzon.depositar(primerCorreo,this);
        for (int i = 0; i < numCorreosAProducir-2; i++) {
            Correo correo = crearCorreo("Cliente "+this.idCliente);
            buzon.depositar(correo, this);
        }
        Correo ultimoCorreo = new Correo(TipoCorreo.FIN, false,"Cliente "+this.idCliente);
        buzon.depositar(ultimoCorreo, this);
        System.out.println("[Cliente " + this.idCliente + "]: Ha terminado de enviar correos.");
    }

    private Correo crearCorreo(String origen) {
        boolean esSpam = Math.random() < 0.2; // 20% de probabilidad de ser spam
        return new Correo(TipoCorreo.NORMAL, esSpam, origen);
    }

    public int getIdCliente() {
        return idCliente;
    }
}
