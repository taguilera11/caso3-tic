package grupo5;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Config {
    private int numeroClientes;
    private int numeroCorreosPorCliente;
    private int numeroFiltros;
    private int numeroServidores;
    private int capacidadEntrada;
    private int capacidadEntrega;

    private static Config instance;

    /*
     Permite formatos de archivo de configuración como los siguientes:

     Opción por clave-valor (con separadores = o : , comentarios con #):
     numeroClientes=10 numeroCorreosPorCliente=50 numeroFiltros=3 numeroServidores=2 capacidadEntrada=100 capacidadEntrega=200

     Opción por líneas (orden fijo: 6 números, sin comentarios): 10 50 3 2 100 200
     */
    private Config(String filename) {
        try (InputStream input = getClass().getResourceAsStream(filename)) {
            if (input == null) {
                throw new FileNotFoundException("No se encontró " + filename);
            }

            Scanner scanner = new Scanner(input, StandardCharsets.UTF_8);
            List<Integer> valores = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                line = line.split("=", 2)[1].trim();
                valores.add(Integer.parseInt(line));
            }
            if (valores.size() != 6) {
                throw new IllegalArgumentException("El archivo de configuración debe contener exactamente 6 valores.");
            }
            this.numeroClientes = valores.get(0);
            this.numeroCorreosPorCliente = valores.get(1);
            this.numeroFiltros = valores.get(2);
            this.numeroServidores = valores.get(3);
            this.capacidadEntrada = valores.get(4);
            this.capacidadEntrega = valores.get(5);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config getInstance(String ruta) {
        if (instance == null) {
            instance = new Config(ruta);
        }
        return instance;
    }

    public static Config getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Config no inicializada. Llama a getInstance(ruta) primero.");
        }
        return instance;
    }
    public int getNumeroClientes() {
        return numeroClientes;
    }

    public int getNumeroCorreosPorCliente() {
        return numeroCorreosPorCliente;
    }

    public int getNumeroFiltros() {
        return numeroFiltros;
    }

    public int getNumeroServidores() {
        return numeroServidores;
    }

    public int getCapacidadEntrada() {
        return capacidadEntrada;
    }

    public int getCapacidadEntrega() {
        return capacidadEntrega;
    }

    @Override
    public String toString() {
        return "Config{" +
                "numeroClientes=" + numeroClientes +
                ", numeroCorreosPorCliente=" + numeroCorreosPorCliente +
                ", numeroFiltros=" + numeroFiltros +
                ", numeroServidores=" + numeroServidores +
                ", capacidadEntrada=" + capacidadEntrada +
                ", capacidadEntrega=" + capacidadEntrega +
                '}';
    }
}
