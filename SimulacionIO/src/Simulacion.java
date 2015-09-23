
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
    
    int contadorSimulacion;
    
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
     9  -> llegaArchivoAntivirus()
     10 -> seLiberaAntivirus()
     11 -> seLiberaLinea1()
     12 -> seLiberaLinea2()
     */

    
    /* Banderas de la simulación */
    boolean antivirusLibre;
    boolean linea1routerLibre;
    boolean linea2routerLibre;
    
    // Tiempos de token
    double tokenA;
    double tokenB;
    double tokenC;

    
    // Colas de objetos Archivo
    LinkedList<Archivo> colaEntradaAntivirus;        //Archivos que esperan para ser atendidos por el antivirus
    LinkedList<Archivo> colaSalidaAntivirus ;        //Archivos que esperan para ser enviados por el reouter
    
    LinkedList<Archivo> colaA1;
    LinkedList<Archivo> colaA2;
    LinkedList<Archivo> colaB1;
    LinkedList<Archivo> colaB2;
    LinkedList<Archivo> colaC1;
    LinkedList<Archivo> colaC2;
    
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

        eventos = new double[13];
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
        colaEntradaAntivirus = new LinkedList();
        colaSalidaAntivirus = new LinkedList();
        
        colaA1 =  new LinkedList();
        colaA2 =  new LinkedList();
        colaB1 =  new LinkedList();
        colaB2 =  new LinkedList();
        colaC1 =  new LinkedList();
        colaC2 =  new LinkedList();
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
        while ( interfaz.datosValidos() == false ) {
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
        
        /*
        while( reloj < tiempoTotal  ){
            siguienteEvento();
        }
        
        
        */
        
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
                llegaArchivoAntivirus();
                break;

            case 10:
                seLiberaAntivirus();
                break;

            case 11:
                seLiberaLinea1();
                break;

            case 12:
                seLiberaLinea2();
                break;

            default:
                break;
        }
    }
    
    
    
    // Busca y saca de una cola el Arcihvo más grande que se puede enviar en
    // el tiempo especificado en tiempoToken y devuelve dicho Archivo.
    // Devuelve null si no encuentra un Archivo que satisfaga la condición.
    private Archivo buscarArchivo( LinkedList<Archivo> cola, double tiempoToken ){
        
        Archivo A = null;
        
        // Si la cola no está vacía
        if( cola.size() > 0 ){
            
            // Itera sobre la cola
            for (int i = 1; i < cola.size(); i++) {
                
                // Si el archivo actual da tiempo de enviar
                if( cola.get(i).tamano*1/2+1/4 <= tiempoToken ){
                    
                    // Si todavía no se le ha asignado nada a A
                    if( A == null  ){
                        A = cola.get(i);
                    }
                    // Si el archivo actual es más grande que A
                    else if( A.tamano < cola.get(i).tamano ){
                        A = cola.get(i);
                    }
                }
            }
            
            if( A != null ){
                cola.remove(A); // Remueve el archivo de la cola
            }
        }
        
        return A; // Devuelve el archivo
    }

    
    /*----------------------------- EVENTOS ----------------------------------*/
    
    
    void llegaArchivoA() {

        reloj = eventos[0];

        // Crea el archivo
        int prioridad = generarPrioridad();
        int tamano = generarTamano();
        int virus = generarNumVirus();
        Archivo A = new Archivo(prioridad, tamano, virus, 'A');

        // Encola el archivo
        if( prioridad == 1 ){
            colaA1.add( A );
        }
        else{
            colaA2.add( A );
        }
        
        // Programa el evento llegaArchivoA
        eventos[0] = reloj + generarExponencial();
    }
    
    
    
    void llegaArchivoB() {

        reloj = eventos[1];

        //Crea el archivo
        int prioridad = generarPrioridad();
        int tamano = generarTamano();
        int virus = generarNumVirus();
        Archivo A = new Archivo(prioridad, tamano, virus, 'B');

        // Encola el archivo
        if( prioridad == 1 ){
            colaB1.add( A );
        }
        else{
            colaB2.add( A );
        }
        
        
        // Programa el evento llegaArchivoB
        eventos[1] = reloj + generarDistB();
        
    }

    
    
    void llegaArchivoC() {

        reloj = eventos[2];

        // Crea el archivo
        int prioridad = generarPrioridad();
        int tamano = generarTamano();
        int virus = generarNumVirus();
        Archivo A = new Archivo(prioridad, tamano, virus, 'C');

        // Encola el archivo
        if( prioridad == 1 ){
            colaC1.add( A );
        }
        else{
            colaC2.add( A );
        }
        
        
        // Programa el evento llegaArchivoC
        eventos[2] = reloj + generarNormal();
        
    }

    
    
    void seLiberaA() {

    }

    
    
    void seLiberaB() {

    }

    
    
    void seLiberaC() {

    }
    
    

    void llegaTokenA() {
        
        reloj = eventos[6];                 // Se actualiza el reloj
        eventos[6] = -1;                    // infinito
        eventos[7] = reloj + duracionToken; // Programa llegaTokenB
        tokenA = duracionToken;
        Archivo archivoEnvio;
        
        
        // Si hay archivos en la cola de prioridad 1 que den tiempo de enviar,
        // se saca el más grande, si no se busca en la cola de prioridad 2.
        if( ( archivoEnvio = buscarArchivo(colaA1, tokenA) ) == null ){
            archivoEnvio = buscarArchivo(colaA2, tokenA);
        }
        
        // Si se encontró algún archivo
        if( archivoEnvio != null ){
            
            // Se calcula el tiempo de llegada del archivo al antivirus
            //evento[] = archivoEnvio.tamano*1/2+1/4;
            
            archivosPorRecibirAntivirus.add( archivoEnvio ); // Se encola el archivo
            
            tokenA = tokenA - archivoEnvio.tamano*1/2;      // Se resta tiempo de token

            eventos[3] = archivoEnvio.tamano*1/2;          // Se calcula seLiberaA
        }
        else{
            tokenA = 0;
            eventos[7] = reloj; // Libera el token, programa llegaTokenB
        }
    }
    
    

    void llegaTokenB() {
        
        reloj = eventos[7];
        eventos[7] = -1; // infinito
        eventos[8] = reloj + duracionToken; // Programa llegaTokenC
        tokenB = duracionToken;
        Archivo archivoEnvio;
        
        
    }

    
    
    void llegaTokenC() {
        
        reloj = eventos[8];
        eventos[8] = -1; // infinito
        eventos[6] = reloj + duracionToken; // Programa llegaTokenA
        tokenC = duracionToken;
        Archivo archivoEnvio;
    }

    
    
    void llegaArchivoAntivirus() {
        
        reloj = eventos[9];

        archivoActual = colaEntradaAntivirus.pop();

        if (antivirusLibre) {
            antivirusLibre = false;

            switch (archivoActual.virus) {
                case 0:
                    //caso en que tenga 0 virus y pase a la primera revisión
                    eventos[10] = archivoActual.tamano/(8); 
                    break;
                case 1:
                    //caso en que tenga 1 virus y pase a la segunda revisión
                    eventos[10] = archivoActual.tamano/(8*2);
                    break;
                case 2:
                    //caso en que tenga 2 virus y pase a la tercera revisión
                    eventos[10] = archivoActual.tamano/(8*3); 
                    break;
                case 3:
                    //caso en que tenga 3 virus, ya no pasará
                    eventos[10] = archivoActual.tamano/(8*3); 
                    break;

                default:
                    break;
            }
        } 
        else {
            colaEntradaAntivirus.addLast(archivoActual);
        }

        eventos[9] = -1; //Desprogramo el evento

    }
    
    

    void seLiberaAntivirus() {
        
        reloj = eventos[10];

        antivirusLibre = true;
        //Pregunta si el archivo que acaba de revisar es válido
        if (archivoActual.virus < 3) 
        {
            if (linea1routerLibre) {
                eventos[11] = reloj + (archivoActual.tamano / 64); //el tiempo de transmisión al router es 0
            } else {
                if (linea2routerLibre) {
                    eventos[12] = reloj + (archivoActual.tamano / 64); //el tiempo de transmisión al router es 0
                } else {
                    colaSalidaAntivirus.addLast(archivoActual);
                }
            }
        }
        if (!colaEntradaAntivirus.isEmpty()) 
        {
            antivirusLibre = false;
            archivoActual = colaEntradaAntivirus.pop();
            switch (archivoActual.virus) {
                case 0:
                    //caso en que tenga 0 virus y pase a la primera revisión
                    eventos[10] = archivoActual.tamano/(8); 
                    break;
                case 1:
                    //caso en que tenga 1 virus y pase a la segunda revisión
                    eventos[10] = archivoActual.tamano/(8*2);
                    break;
                case 2:
                    //caso en que tenga 2 virus y pase a la tercera revisión
                    eventos[10] = archivoActual.tamano/(8*3); 
                    break;
                case 3:
                    //caso en que tenga 3 virus, ya no pasará
                    eventos[10] = archivoActual.tamano/(8*3); 
                    break;

                default:
                    break;
            }
        } 
        else 
        {
            eventos[10] = -1; //Se desprograma este evento.
        }
    }
    
    

    void seLiberaLinea1() {
        
        reloj = eventos[11];

        if (!colaSalidaAntivirus.isEmpty()) {
            Archivo actual;
            actual = colaSalidaAntivirus.pop();
            eventos[11] = reloj + (actual.tamano / 64);
            linea1routerLibre = false;
        } 
        else {
            linea1routerLibre = true;
            eventos[11] = -1; //Se desprograma el evento
        }
    }
    
    

    void seLiberaLinea2() {
        
        reloj = eventos[12];

        if (!colaSalidaAntivirus.isEmpty()) {
            Archivo actual;
            actual = colaSalidaAntivirus.pop();
            eventos[12] = reloj + (actual.tamano / 64);
            linea2routerLibre = false;
        } 
        else {
            linea2routerLibre = true;
            eventos[12] = -1; //Se desprograma el evento
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
    int min = 1;
    int max = 100;
    int probabilidad;
    int numero_virus;
        
    Random r = new Random();
    probabilidad = r.nextInt(max - min + 1) + min; //Numero aleatorio entre 1 y 100
    //Pregunto si da en el rango de virus, 1 virus
    if(probabilidad < 6)
    {
        probabilidad = r.nextInt(max - min + 1) + min; //Numero aleatorio entre 1 y 100
        //Pregunto si da en el rango de virus, 2 virus
        if(probabilidad < 6)
        {
            probabilidad = r.nextInt(max - min + 1) + min; //Numero aleatorio entre 1 y 100
            //Pregunto si da en el rango de virus, 3 virus
            if(probabilidad < 6)
            {
                numero_virus=100;
            }
            else
            {
                numero_virus=2;
            }
        }
        else
        {
            numero_virus=1;
        }
    }
    else
    {
        numero_virus=0;
    }
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