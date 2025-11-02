package grupo5;

import java.util.concurrent.atomic.AtomicInteger;

public class Correo {
    //Con AtomicInteger nos aseguramos que el id sea unico en un entorno multihilo
    private static final AtomicInteger counter = new AtomicInteger(1);

    private int id;
    private TipoCorreo tipo;
    private boolean esSpam;
    private Long tiempoCuarentena;
    private String origen;

    public Correo(TipoCorreo tipo, boolean esSpam, String origen) {
        this.id = counter.getAndIncrement();
        this.tipo = tipo;
        this.esSpam = esSpam;
        this.tiempoCuarentena = 0L;
        this.origen = origen;
    }

    public boolean esFin() {
        return this.tipo == TipoCorreo.FIN;
    }

    public boolean esInicio() {
        return this.tipo == TipoCorreo.INICIO;
    }

    public boolean esNormal() {
        return this.tipo == TipoCorreo.NORMAL;
    }

    public int getId() {
        return id;
    }

    public TipoCorreo getTipo() {
        return tipo;
    }

    public boolean getEsSpam() {
        return esSpam;
    }

    public Long getTiempoCuarentena() {
        return tiempoCuarentena;
    }

    public String getOrigen() {
        return origen;
    }

    public void setTiempoCuarentena(Long tiempoCuarentena) {
        this.tiempoCuarentena = tiempoCuarentena;
    }
}
