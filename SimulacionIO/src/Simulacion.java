
import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;
import static java.lang.Thread.sleep;
import java.util.Random;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 *
 * @author Jean Carlo Zuñiga 
 * @author Diego Angulo Alfaro
 *  
 */


public class Simulacion {
    
    double reloj;        // Reloj de la simulación

    // Valores especificados por el usuario
    int numSimulaciones; // Número de veces que se va a correr la simulación
    int tiempoTotal;     // Tiempo total en segundos para correr cada simulación
    int velocidad;       // Velocidad de la simulación, 0 = modo rápido, 1 = modo lento
    int duracionToken;   // Tiempo en segundos durante el cuál a cada máquina se le asigna el token

    Archivo archivoActual; //Archivo que está llegando al antivirus

    // Interfaz gráfica de usuario
    Interfaz interfaz;

    // Eventos
    double[] eventos;   // Tiempos de ocurrencia de cada evento

    /* Los índices del arreglo representan los eventos

     0  -> llegaArchivoA()
     1  -> llegaArchivoB()
     2  -> llegaArchivoC()
     3  -> seLiberaA()
     4  -> seLiberaB()
     5  -> seLiberaC()
     6  -> llegaTokenA()
     7  -> llegaTokenB()
     8  -> llegaTokenC()
     9  -> llegaArchivoAntivirus(int indice)
     10 -> seLiberaAntivirus()
     11 -> seLiberaLinea1()
     12 -> seLiberaLinea2()
     */

    
    /* Banderas de la simulación */
    boolean antivirusLibre;
    boolean linea1routerLibre;
    boolean linea2routerLibre;

    
    // Colas de objetos Archivo
    LinkedList<Archivo> archivosPorRecibirAntivirus; //Para el evento "llega archivo a antivirus"
    LinkedList<Archivo> colaEntradaAntivirus;        //Archivos que esperan para ser atendidos por el antivirus
    LinkedList<Archivo> colaSalidaAntivirus ;        //Archivos que esperan para ser enviados por el reouter

    
    // Estructura de archivo
    class Archivo {

        int prioridad;      // 1 o 2
        int tamano;         // 1 a 64 paquetes
        int virus;          // Cantidad de virus del archivo
        char computadora;   // A, B o C

        // Constructor
        Archivo(int p, int t, int v, char c) {
            prioridad = p;
            tamano = t;
            virus = v;
            computadora = c;
        }
    };


    // Constructor
    Simulacion(){
    
        // Crea la interfaz gráfica
        crearInterfaz();

        eventos = new double[14];
        reloj = 0;

        // Los primeros eventos a ocurrir
        eventos[0] = 0;
        eventos[1] = 0;
        eventos[2] = 0;

        //booleanos se setean como libres al inicio
        antivirusLibre = true;
        linea1routerLibre = true;
        linea2routerLibre = true;
        
        // Se desprograman los demás eventos
        for (int i = 3; i < 14; i++) {
            eventos[i] = -1;    // -1 representa tiempo infito
        }
        
        // Se crean las colas
        archivosPorRecibirAntivirus = new LinkedList();
        colaEntradaAntivirus = new LinkedList();
        colaSalidaAntivirus = new LinkedList();   
    }
    
    
    
    // Crea y muestra la interfaz
    private void crearInterfaz() {

        JFrame.setDefaultLookAndFeelDecorated(true);

        // Agrega el look and feel
        try {
            UIManager.setLookAndFeel(new SyntheticaAluOxideLookAndFeel());
        } catch (Exception ex) {
            System.err.println("Error agregando look and feel");
        }

        interfaz = new Interfaz();
        interfaz.setVisible(true);
    }

    
    
    // Inicializa parámetros de simulación
    void inicializarParametros() {

        // Espera a que los datos del usario sean válidos
        while (interfaz.datosValidos() == false) {
            try {
                sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Asigna los valores
        numSimulaciones = interfaz.getNumeroSimulaciones();
        tiempoTotal = interfaz.getTiempoTotal();
        velocidad = interfaz.getVelocidad();
        duracionToken = interfaz.getDuracionToken();
    }
    
    
    
    void iniciarSimulacion() {
        interfaz.escribirResultado("\nIniciando simulación\n\n");
        delay();
        siguenteEvento();
    }
    
    
    
    void finalizarSimulacion(){
        
        interfaz.escribirResultado("\n\nSimulacion finalizada.");
        interfaz.fin();
    }
    
    
    
    // Retrasa el programa un segundo
    private void delay(){
        
        // Si la velocidad es modo lento
        if( velocidad == 1 ){
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(Simulacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    
    void siguenteEvento() {

        int menor = 0;

        // Se escoje el evento con el menor tiempo de ocurrencia
        for (int i = 1; i < 13; i++) {

            if (eventos[i] != -1) {

                if (eventos[menor] == -1) {
                    menor = i;
                } else if (eventos[i] < eventos[menor]) {
                    menor = i;
                }
            }
        }
        
        
        // Llama el evento correspondiente
        switch (menor) {

            case 0:
                llegaArchivoA();
                break;

            case 1:
                llegaArchivoB();
                break;

            case 2:
                llegaArchivoC();
                break;

            case 3:
                seLiberaA();
                break;

            case 4:
                seLiberaB();
                break;

            case 5:
                seLiberaC();
                break;

            case 6:
                llegaTokenA();
                break;

            case 7:
                llegaTokenB();
                break;

            case 8:
                llegaTokenC();
                break;

            case 9:
                llegaArchivoAntivirus(9);
                break;

            case 10:
                llegaArchivoAntivirus(10);
                break;

            case 11:
                seLiberaAntivirus();
                break;

            case 12:
                seLiberaLinea1();
                break;

            case 13:
                seLiberaLinea2();
                break;

            default:
                break;
        }
    }


    
    /*----------------------------- EVENTOS ----------------------------------*/
    
    
    void llegaArchivoA() {

        reloj = eventos[0];

        // Crea el archivo
        int prioridad = generarPrioridad();
        int tamano = generarTamano();
        int virus = generarNumVirus();
        Archivo A = new Archivo(prioridad, tamano, virus, 'A');

        // Encolar el archivo
        
        
        eventos[0] = reloj + generarExponencial();
    }
    
    
    
    void llegaArchivoB() {

        reloj = eventos[1];

        //Crea el archivo
        int prioridad = generarPrioridad();
        int tamano = generarTamano();
        int virus = generarNumVirus();
        Archivo A = new Archivo(prioridad, tamano, virus, 'B');

        //Encolar archivo
        
        
        eventos[1] = reloj + generarDistB();
    }

    
    
    void llegaArchivoC() {

        reloj = eventos[2];

        // Crea el archivo
        int prioridad = generarPrioridad();
        int tamano = generarTamano();
        int virus = generarNumVirus();
        Archivo A = new Archivo(prioridad, tamano, virus, 'C');

        //Encolar archivo
        
        
        eventos[2] = reloj + generarNormal();
    }

    
    
    void seLiberaA() {

    }

    
    
    void seLiberaB() {

    }

    
    
    void seLiberaC() {

    }
    
    

    void llegaTokenA() {

    }
    
    

    void llegaTokenB() {

    }

    
    
    void llegaTokenC() {

    }

    
    
    void llegaArchivoAntivirus(int indice) {
        
        reloj = eventos[indice];

        archivoActual = archivosPorRecibirAntivirus.pop();

        if (antivirusLibre) {
            antivirusLibre = false;

            switch (archivoActual.virus) {
                case 0:
                    eventos[11] = 1; //no me sé bien los tiempos
                    break;
                case 1:
                    eventos[11] = 1; //no me sé bien los tiempos
                    break;
                case 2:
                    eventos[11] = 1; //no me sé bien los tiempos
                    break;
                case 3:
                    eventos[11] = 1; //no me sé bien los tiempos
                    break;

                default:
                    break;
            }
        } 
        else {
            colaEntradaAntivirus.addLast(archivoActual);
        }

        eventos[indice] = -1; //Desprogramo el evento

    }
    
    

    void seLiberaAntivirus() {
        
        reloj = eventos[11];

        antivirusLibre = true;

        if (archivoActual.virus < 3) //Si es un archivo valido
        {
            if (linea1routerLibre) {
                eventos[12] = reloj + (archivoActual.tamano / 64); //el tiempo de transmisión al router es 0
            } else {
                if (linea2routerLibre) {
                    eventos[13] = reloj + (archivoActual.tamano / 64); //el tiempo de transmisión al router es 0
                } else {
                    colaSalidaAntivirus.addLast(archivoActual);
                }
            }
        }
        if (!colaEntradaAntivirus.isEmpty()) {
            antivirusLibre = false;
            archivoActual = colaEntradaAntivirus.pop();
            switch (archivoActual.virus) {
                case 0:
                    eventos[11] = 1; //no me sé bien los tiempos
                    break;
                case 1:
                    eventos[11] = 1; //no me sé bien los tiempos
                    break;
                case 2:
                    eventos[11] = 1; //no me sé bien los tiempos
                    break;
                case 3:
                    eventos[11] = 1; //no me sé bien los tiempos
                    break;

                default:
                    break;
            }
        } 
        else {
            eventos[11] = -1; //Se desprograma este evento.
        }
    }
    
    

    void seLiberaLinea1() {
        
        reloj = eventos[12];

        if (!colaSalidaAntivirus.isEmpty()) {
            Archivo actual;
            actual = colaSalidaAntivirus.pop();
            eventos[12] = reloj + (actual.tamano / 64);
            linea1routerLibre = false;
        } 
        else {
            linea1routerLibre = true;
            eventos[12] = -1; //Se desprograma el evento
        }
    }
    
    

    void seLiberaLinea2() {
        
        reloj = eventos[13];

        if (!colaSalidaAntivirus.isEmpty()) {
            Archivo actual;
            actual = colaSalidaAntivirus.pop();
            eventos[13] = reloj + (actual.tamano / 64);
            linea2routerLibre = false;
        } 
        else {
            linea2routerLibre = true;
            eventos[13] = -1; //Se desprograma el evento
        }
    }

    

    /*------------------------ NÚMEROS ALEATORIOS ----------------------------*/
    
    
    double generarExponencial() {

        double lambda = 1 / 5;

        double r = Math.random();   //Número aleatorio entre 0 y 1 con distribución uniforme.

        double x = -Math.log(1 - r) / lambda;

        return x;
    }

    
    
    double generarNormal() {

        return 0;
    }
    
    

    double generarDistB() {

        // f(x) = x/40
        // F(x) = ( (x^2)-64 ) / 80
        // x = sqrt(80r + 64) despejando x 
        
        double r = Math.random();   //Número aleatorio entre 0 y 1 con distribución uniforme.

        double x = Math.sqrt(80 * r + 64);

        return x;
    }
    
    
    
    int generarPrioridad() {

        int prioridad;

        Random r = new Random();
        int x = r.nextInt(100 - 1 + 1) + 1; //Número aleatorio entre 1 y 100 con distribución uniforme.

        //Cada archivo tiene una probabilidad de 0.25 de ser de prioridad 1 y 0.75 de ser prioridad 2
        if (x <= 25) {
            prioridad = 1;
        } 
        else {
            prioridad = 2;
        }

        return prioridad;
    }
    

    
    int generarTamano() {

        // El tamaño de cada paquete es uniforme entre 1 y 64
        int min = 1;
        int max = 64;

        Random r = new Random();
        int tamano = r.nextInt(max - min + 1) + min; //Número aleatorio entre 1 y 64 con distribución uniforme.

        return tamano;
    }
    
    

    int generarNumVirus() {
    int min = 0;
    int max = 3;
    
    Random r = new Random();
    int numero_virus = r.nextInt(max - min + 1) + min; //Número aleatorio entre 1 y 64 con distribución uniforme.
    
    return numero_virus;
    }


    
    
    public static void main(String[] args) {

        Simulacion Sim = new Simulacion();
        
        // Ejecución del programa
        while(true){
            
            Sim.inicializarParametros();
            
            // Ejecuta la simulación el número de veces indicado por el usuario
            for( int i = 0; i < Sim.numSimulaciones; i++ ){
                
                Sim.iniciarSimulacion();
            }
                    
            Sim.finalizarSimulacion();
        }
    }
}