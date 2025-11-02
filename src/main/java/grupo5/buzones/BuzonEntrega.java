package grupo5.buzones;

public class BuzonEntrega extends BuzonEntrada {
    private int numeroServidores;

    public BuzonEntrega(int capacidad, int numeroServidores) {
        super(capacidad);
        this.numeroServidores = numeroServidores;
    }
}
