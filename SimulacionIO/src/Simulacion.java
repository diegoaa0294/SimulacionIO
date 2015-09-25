
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
    double tiempoTotal;     // Tiempo total en segundos para correr cada simulación
    int velocidad;       // Velocidad de la simulación, 0 = modo rápido, 1 = modo lento
    double duracionToken;   // Tiempo en segundos durante el cuál a cada máquina se le asigna el token
    
    int contadorSimulacion;
    String computadoraConToken;
    
    Archivo archivoActual; // Representa el archivo que está siendo revisado por el antivirus

    // Interfaz gráfica de usuario
    Interfaz interfaz;

    // Eventos
    double [] eventos;   // Tiempos de ocurrencia de cada evento

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
    boolean interrupcion;
    
    // Tiempo restante de token
    double tiempoToken;

    
    // Colas de objetos Archivo
    LinkedList<Archivo> colaEnvioAntivirus;          //Archivos que se envian al antivirus y todavía no han llegado
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
        contadorSimulacion = 1;
        computadoraConToken = "Ninguna";
        tiempoToken = 0;

        // Los primeros eventos a ocurrir
        eventos[0] = 0;
        eventos[1] = 0;
        eventos[2] = 0;
        
        // Se desprograman los demás eventos
        for (int i = 3; i < 13; i++) {
            eventos[i] = -1;    // -1 representa tiempo infito
        }
        
        eventos[6] = 5; // A es la primera computadora a la que le llega el token
        
        // Se inicializan los booleanos
        antivirusLibre = true;
        linea1routerLibre = true;
        linea2routerLibre = true;
        interrupcion = false;
        
        // Se crean las colas
        colaEnvioAntivirus = new LinkedList();
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
        
        interfaz.escribirResultado("Iniciando simulación "+contadorSimulacion+"\n\n");
        interfaz.escribirResultado("_______________________________________________________________________\n\n");
        
        while( interfaz.interrupcion == false && reloj <= tiempoTotal  ){
            
            /* Para probar */
            for (int i = 0; i < 13; i++) {
            interfaz.escribirResultado("Siguiente evento:  eventos["+i+"]: " + eventos[i] + "\n");
            }
            interfaz.escribirResultado("\n\n");
            
            // Ejecuta el siguiente evento a ocurrir
            siguienteEvento();
            
            // Muestra el reloj del sistema
            interfaz.escribirResultado("Reloj:  " + reloj + " \n\n");
            
            // Muestra los tamaños de las colas
            interfaz.escribirResultado("Cola A prioridad 1:  " + colaA1.size() + " \n");
            interfaz.escribirResultado("Cola A prioridad 2:  " + colaA2.size() + " \n");
            interfaz.escribirResultado("Cola B prioridad 1:  " + colaB1.size() + " \n");
            interfaz.escribirResultado("Cola B prioridad 2:  " + colaB2.size() + " \n");
            interfaz.escribirResultado("Cola C prioridad 1:  " + colaC1.size() + " \n");
            interfaz.escribirResultado("Cola C prioridad 2:  " + colaC2.size() + " \n");
            interfaz.escribirResultado("Cola de envío al antivirus:  " + colaEnvioAntivirus.size() + " \n");
            interfaz.escribirResultado("Cola entrada antivirus:  " + colaEntradaAntivirus.size() + " \n");
            interfaz.escribirResultado("Cola salida antivirus:  " + colaSalidaAntivirus.size() + " \n\n");
            
            // Muestra cual computadora tiene el token
            interfaz.escribirResultado("Computadora con token:  " + computadoraConToken + " \n");
            interfaz.escribirResultado("Tiempo restante de token:  " + tiempoToken + " \n\n");
            
            // Muestra el archivo que está siendo revisado por el router
            interfaz.escribirResultado("Archivo en revisión:\n");
            
            if( archivoActual != null ){
                interfaz.escribirResultado("Procedencia: "+archivoActual.computadora+"\n");
                interfaz.escribirResultado("Prioridad: "+archivoActual.prioridad+"\n");
                interfaz.escribirResultado("Tamaño: "+archivoActual.tamano+"\n");
                interfaz.escribirResultado("Virus: "+archivoActual.virus+"\n\n");
            }
            else{
                interfaz.escribirResultado("Ninguno\n\n");
            }
            
            // Muestra las líneas del router
            interfaz.escribirResultado("Línea 1 del router libre:  " + linea1routerLibre + " \n");
            interfaz.escribirResultado("Línea 2 del router libre:  " + linea2routerLibre + " \n\n");
            
            interfaz.escribirResultado("------------------------------------------------------------------- \n\n");
            
            
            delay(); // Delay de 1 segundo entre cada evento
        }
        
        // Si la ejecución de la simulación de interrumpió
        if( interfaz.interrupcion ){
            interrupcion = true;
            interfaz.escribirResultado("Simulación interrumpida.\n\n");
        }
        
        finalizarSimulacion();
        contadorSimulacion++;
    }
    
    
    
    // Finaliza una simulación
    private void finalizarSimulacion(){
        
        if( interrupcion == false ){
            interfaz.escribirResultado("\nSimulación "+ contadorSimulacion +" finalizada.\n\n\n\n");
        }
        
        reiniciarVariables();
        // Imprimir estadísticas
    }
    
    
    
    // Reinicia las variables globales
    void reiniciarVariables(){
    
        reloj = 0;
	computadoraConToken = "Ninguna";
        tiempoToken = 0;
	
        // Los primeros eventos a ocurrir
        eventos[0] = 0;
        eventos[1] = 0;
        eventos[2] = 0;
        
        // Se desprograman los demás eventos
        for (int i = 3; i < 13; i++) {
            eventos[i] = -1;    // -1 representa tiempo infito
        }
        
        eventos[6] = 5; // A es la primera computadora a la que le llega el token
        
        // Se inicializan los booleanos
        antivirusLibre = true;
        linea1routerLibre = true;
        linea2routerLibre = true;
        
        // Se limpian todas las colas
        colaEnvioAntivirus.clear();
        colaEntradaAntivirus.clear();
        colaSalidaAntivirus.clear();
        colaA1.clear();
        colaA2.clear();
        colaB1.clear();
        colaB2.clear();
        colaC1.clear();
        colaC2.clear();
        
        // No hay archivo en el antivirus
        archivoActual = null;
    }
    
    
    
    void finalizarSimulacionGlobal(){
        
        // Hay que mostrar estadisticas globales
        
        // Si no hubo interrupción
        if( interrupcion == false ){
            interfaz.escribirResultado("Simulación global finalizada.\n\n");
        }
        
        contadorSimulacion = 1;
        interrupcion = false;
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
    
    
    
    void siguienteEvento() {

        int menor = 0;

        // Se escoje el evento con el menor tiempo de ocurrencia
        for (int i = 1; i < 13; i++) {

            if (eventos[i] != -1) {

                if (eventos[menor] == -1) {
                    menor = i;
                } 
                else if (eventos[i] < eventos[menor]) {
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
    private Archivo buscarArchivo( LinkedList<Archivo> cola ){
        
        Archivo A = null;
        
        // Si la cola no está vacía
        if( cola.size() > 0 ){
            
            // Itera sobre la cola
            for(int i = 0; i < cola.size(); i++) {
                
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
    
    
    // Evento en el que llega un archivo a la computadora A
    void llegaArchivoA() {
        
        interfaz.escribirResultado("Evento:  Llegó un archivo a la computadora A\n\n");

        reloj = eventos[0]; // Actualiza el reloj

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
    
    
    
    // Evento en el que llega un archivo a la computadora B
    void llegaArchivoB() {

        interfaz.escribirResultado("Evento:  Llegó un archivo a la computadora B\n\n");
        
        reloj = eventos[1]; // Actualiza el reloj

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

    
    
    // Evento en el que llega un archivo a la computadora C
    void llegaArchivoC() {

        interfaz.escribirResultado("Evento:  Llegó un archivo a la computadora C\n\n");
        
        reloj = eventos[2]; // Actualiza el reloj

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
        eventos[2] = reloj + generarDistB();//generarNormal();
    }

    
    
    // Evento en el que se desocupa la computadora A
    void seLiberaA() {
        
        interfaz.escribirResultado("Evento:  Se liberó la computadora A\n\n");
        
        reloj = eventos[3]; // Se actualiza el reloj
        Archivo archivoEnvio;
        
        
        // Si hay archivos en la cola de prioridad 1 que den tiempo de enviar,
        // se saca el más grande, si no se busca en la cola de prioridad 2.
        if( ( archivoEnvio = buscarArchivo( colaA1 ) ) == null ){
            archivoEnvio = buscarArchivo( colaA2 );
        }
        
        
        // Si se encontró algún archivo
        if( archivoEnvio != null ){
            
            interfaz.escribirResultado("Enviando archivo al antivirus...\n\n");
            
            // Se calcula el tiempo de llegada del archivo al antivirus y se
            // encola el archivo en la cola de envío al antivirus
            eventos[9] = reloj + archivoEnvio.tamano*1/2+1/4;
            colaEnvioAntivirus.add( archivoEnvio );
            
            tiempoToken = tiempoToken - archivoEnvio.tamano*1/2;      // Se resta tiempo de token
            eventos[3] = reloj + archivoEnvio.tamano*1/2;   // Se calcula seLiberaA
        }
        
        // Si no encuentra archivos en ninguna de las dos colas
        // libera el token y se lo pasa a la siguiente computadora.
        else{
            interfaz.escribirResultado("Se liberó el token, no se pueden enviar archivos\n\n");
            
            tiempoToken = 0;
            computadoraConToken = "";
            eventos[7] = reloj; // Libera el token, programa llegaTokenB
            eventos[3] = -1; // Se desprograma seLiberaA
        }
    }

    
    
    // Evento en el que se desocupa la computadora B
    void seLiberaB() {
        
        interfaz.escribirResultado("Evento:  Se liberó la computadora B\n\n");
        
        reloj = eventos[4]; // Se actualiza el reloj
        Archivo archivoEnvio;
        
        // Si hay archivos en la cola de prioridad 1 que den tiempo de enviar,
        // se saca el más grande, si no se busca en la cola de prioridad 2.
        if( ( archivoEnvio = buscarArchivo( colaB1 ) ) == null ){
            archivoEnvio = buscarArchivo( colaB2 );
        }
        
        // Si se encontró algún archivo
        if( archivoEnvio != null ){
            
            interfaz.escribirResultado("Enviando archivo al antivirus...\n\n");
            
            // Se calcula el tiempo de llegada del archivo al antivirus y se
            // encola el archivo en la cola de envío al antivirus
            eventos[9] = reloj + archivoEnvio.tamano*1/2+1/4;
            colaEnvioAntivirus.add( archivoEnvio );
            
            tiempoToken = tiempoToken - archivoEnvio.tamano*1/2;      // Se resta tiempo de token
            eventos[4] = reloj + archivoEnvio.tamano*1/2;   // Se calcula seLiberaB
        }
        
        // Si no encuentra archivos en ninguna de las dos colas
        else{
            interfaz.escribirResultado("Se liberó el token, no se pueden enviar archivos\n\n");
            
            tiempoToken = 0;
            computadoraConToken = "";
            eventos[8] = reloj; // Libera el token, programa llegaTokenC
            eventos[4] = -1; // Se desprograma seLiberaB
        }
    }

    
    
    // Evento en el que se desocupa la computadora C
    void seLiberaC() {
        
        interfaz.escribirResultado("Evento:  Se liberó la computadora C\n\n");
        
        reloj = eventos[5]; // Se actualiza el reloj
        Archivo archivoEnvio;
        
        // Si hay archivos en la cola de prioridad 1 que den tiempo de enviar,
        // se saca el más grande, si no se busca en la cola de prioridad 2.
        if( ( archivoEnvio = buscarArchivo( colaC1 ) ) == null ){
            archivoEnvio = buscarArchivo( colaC2 );
        }
        
        // Si se encontró algún archivo
        if( archivoEnvio != null ){
            
            interfaz.escribirResultado("Enviando archivo al antivirus...\n\n");
            
            // Se calcula el tiempo de llegada del archivo al antivirus y se
            // encola el archivo en la cola de envío al antivirus
            eventos[9] = reloj + archivoEnvio.tamano*1/2+1/4;
            colaEnvioAntivirus.add( archivoEnvio );
            
            tiempoToken = tiempoToken - archivoEnvio.tamano*1/2;      // Se resta tiempo de token
            eventos[5] = reloj + archivoEnvio.tamano*1/2;   // Se calcula seLiberaC
        }
        
        // Si no encuentra archivos en ninguna de las dos colas
        else{
            interfaz.escribirResultado("Se liberó el token, no se pueden enviar archivos\n\n");
            
            tiempoToken = 0;
            computadoraConToken = "";
            eventos[6] = reloj; // Libera el token, programa llegaTokenA
            eventos[5] = -1; // Se desprograma seLiberaC
        }
    }
    
    
    
    // Evento en el que el token llega a la computadora A
    void llegaTokenA() {
        
        interfaz.escribirResultado("Evento:  Llegó el token a la computadora A\n\n");
        computadoraConToken = "A";
        
        reloj = eventos[6];                 // Se actualiza el reloj
        eventos[6] = -1;                    // infinito, desprograma llegaTokenA
        eventos[7] = reloj + duracionToken; // Programa llegaTokenB
        tiempoToken = duracionToken;        // Asigna todo el tiempo del token
        Archivo archivoEnvio;
        
        
        // Si hay archivos en la cola de prioridad 1 que den tiempo de enviar,
        // se saca el más grande, si no se busca en la cola de prioridad 2.
        if( ( archivoEnvio = buscarArchivo( colaA1 ) ) == null ){
            archivoEnvio = buscarArchivo( colaA2 );
        }
        
        
        // Si se encontró algún archivo
        if( archivoEnvio != null ){
            
            interfaz.escribirResultado("Enviando archivo al antivirus...\n\n");
            
            // Se calcula el tiempo de llegada del archivo al antivirus y se
            // encola el archivo en la cola de envío al antivirus
            eventos[9] = reloj + archivoEnvio.tamano*1/2+1/4;
            colaEnvioAntivirus.add( archivoEnvio );
            
            tiempoToken = tiempoToken - archivoEnvio.tamano*1/2;      // Se resta tiempo de token
            eventos[3] = reloj + archivoEnvio.tamano*1/2;   // Se calcula seLiberaA
        }
        
        // Si no encuentra archivos en ninguna de las dos colas
        else{
            interfaz.escribirResultado("Se liberó el token, no se pueden enviar archivos\n\n");
            
            tiempoToken = 0;
            computadoraConToken = "";
            eventos[7] = reloj; // Libera el token, programa llegaTokenB
        }
    }
    
    
    
    // Evento en el que el token llega a la computadora B
    void llegaTokenB() {
        
        interfaz.escribirResultado("Evento:  Llegó el token a la computadora B\n\n");
        computadoraConToken = "B";
        
        reloj = eventos[7];                 // Se actualiza el reloj
        eventos[7] = -1;                    // infinito, desprograma llegaTokenB
        eventos[8] = reloj + duracionToken; // Programa llegaTokenC
        tiempoToken = duracionToken;        // Asigna todo el tiempo del token
        Archivo archivoEnvio;
        
        // Si hay archivos en la cola de prioridad 1 que den tiempo de enviar,
        // se saca el más grande, si no se busca en la cola de prioridad 2.
        if( ( archivoEnvio = buscarArchivo( colaB1 ) ) == null ){
            archivoEnvio = buscarArchivo( colaB2 );
        }
        
        // Si se encontró algún archivo
        if( archivoEnvio != null ){
            
            interfaz.escribirResultado("Enviando archivo al antivirus...\n\n");
            
            // Se calcula el tiempo de llegada del archivo al antivirus y se
            // encola el archivo en la cola de envío al antivirus
            eventos[9] = reloj + archivoEnvio.tamano*1/2+1/4;
            colaEnvioAntivirus.add( archivoEnvio );
            
            tiempoToken = tiempoToken - archivoEnvio.tamano*1/2;      // Se resta tiempo de token
            eventos[4] = reloj + archivoEnvio.tamano*1/2;   // Se calcula seLiberaB
        }
        
        // Si no encuentra archivos en ninguna de las dos colas
        else{
            interfaz.escribirResultado("Se liberó el token, no se pueden enviar archivos\n\n");
            
            tiempoToken = 0;
            computadoraConToken = "";
            eventos[8] = reloj; // Libera el token, programa llegaTokenC
        }
    }

    
    
    // Evento en el que el token llega a la computadora C
    void llegaTokenC() {
        
        interfaz.escribirResultado("Evento:  Llegó el token a la computadora C\n\n");
        computadoraConToken = "C";
        
        reloj = eventos[8];                 // Se actualiza el reloj
        eventos[8] = -1;                    // infinito, desprograma llegaTokenC
        eventos[6] = reloj + duracionToken; // Programa llegaTokenA
        tiempoToken = duracionToken;        // Asigna todo el tiempo del token
        Archivo archivoEnvio;
        
        // Si hay archivos en la cola de prioridad 1 que den tiempo de enviar,
        // se saca el más grande, si no se busca en la cola de prioridad 2.
        if( ( archivoEnvio = buscarArchivo( colaC1 ) ) == null ){
            archivoEnvio = buscarArchivo( colaC2 );
        }
        
        // Si se encontró algún archivo
        if( archivoEnvio != null ){
            
            interfaz.escribirResultado("Enviando archivo al antivirus...\n\n");
            
            // Se calcula el tiempo de llegada del archivo al antivirus y se
            // encola el archivo en la cola de envío al antivirus
            eventos[9] = reloj + archivoEnvio.tamano*1/2+1/4;
            colaEnvioAntivirus.add( archivoEnvio );
            
            tiempoToken = tiempoToken - archivoEnvio.tamano*1/2;      // Se resta tiempo de token
            eventos[5] = reloj + archivoEnvio.tamano*1/2;   // Se calcula seLiberaC
        }
        
        // Si no encuentra archivos en ninguna de las dos colas
        else{
            interfaz.escribirResultado("Se liberó el token, no se pueden enviar archivos\n\n");
            
            tiempoToken = 0;
            computadoraConToken = "";
            eventos[6] = reloj; // Libera el token, programa llegaTokenA
        }
    }

    
    
    
    // Evento en el que llega un archivo al servidor antivirus
    void llegaArchivoAntivirus() {
        
        interfaz.escribirResultado("Evento:  Llegó un archivo al antivirus\n\n");
        
        reloj = eventos[9]; // Actualiza el reloj
        
        // Saca el primer archivo de la cola de envío
        // y lo mete en la cola de entrada al antivirus.
        colaEntradaAntivirus.add( colaEnvioAntivirus.pop() );
        
        
        // Si el antivirus está libre
        if ( antivirusLibre ) {
            
            // Saca el primer archivo de la cola de envío
            // y lo mete al antivirus como archivo actual.
            archivoActual = colaEntradaAntivirus.pop();
            
            
            // El antivirus ahora está ocupado
            antivirusLibre = false;
            
            interfaz.escribirResultado("Revisando archivo...\n\n");
            
            
            // Programa el evento seLiberaAntivirus según el número
            // de virus que tiene el archivo actual.
            switch ( archivoActual.virus ) {
                case 0:
                    //caso en que tenga 0 virus y pase a la primera revisión
                    eventos[10] = reloj + archivoActual.tamano/(8); 
                    break;
                case 1:
                    //caso en que tenga 1 virus y pase a la segunda revisión
                    eventos[10] = reloj +archivoActual.tamano/(8*2);
                    break;
                case 2:
                    //caso en que tenga 2 virus y pase a la tercera revisión
                    eventos[10] = reloj +archivoActual.tamano/(8*3); 
                    break;
                case 3:
                    //caso en que tenga 3 virus, ya no pasará
                    eventos[10] = reloj +archivoActual.tamano/(8*3); 
                    break;

                default:
                    break;
            }
        }

        eventos[9] = -1; // De desprograma el evento
    }
    
    
    
    // Evento en el que se libera el servidor antivirus
    void seLiberaAntivirus() {
        
        interfaz.escribirResultado("Evento:  Se liberó el antivirus\n\n");
        
        reloj = eventos[10]; // Actualiza el reloj

        antivirusLibre = true;
        
        //Pregunta si el archivo que acaba de revisar es válido
        if ( archivoActual.virus < 3 ){
            
            
            // Mete el archivo que acaba de revisar a la cola de salida
            colaSalidaAntivirus.add( archivoActual );
            
            // Si la primera línea del router está libre
            if ( linea1routerLibre ) {
                
                interfaz.escribirResultado("Enviando archivo al router...\n\n");
                eventos[11] = reloj + (archivoActual.tamano / 64); //el tiempo de transmisión al router es 0
            }
            
            // Si la segunda línea del router está libre
            else if( linea2routerLibre ) {
                interfaz.escribirResultado("Enviando archivo al router...\n\n");
                eventos[12] = reloj + (archivoActual.tamano / 64); //el tiempo de transmisión al router es 0
            }
            
            // Si la ninguna línea del router está libre
            else {
                interfaz.escribirResultado("Router ocupado...\n\n");
            }
        }
        
        
        // Si hay archivos en la cola de entrada
        if( !colaEntradaAntivirus.isEmpty() ){
            
            interfaz.escribirResultado("Revisando archivo...\n\n");
            
            // Saca el primer archivo de la cola de entrada
            // y lo mete al antivirus como archivo actual.
            archivoActual = colaEntradaAntivirus.pop();
            
            
            // El antivirus ahora está ocupado
            antivirusLibre = false;
            
            
            // Programa el evento seLiberaAntivirus según el número
            // de virus que tiene el archivo actual.
            switch ( archivoActual.virus ) {
                case 0:
                    //caso en que tenga 0 virus y pase a la primera revisión
                    eventos[10] = reloj +archivoActual.tamano/(8); 
                    break;
                case 1:
                    //caso en que tenga 1 virus y pase a la segunda revisión
                    eventos[10] = reloj +archivoActual.tamano/(8*2);
                    break;
                case 2:
                    //caso en que tenga 2 virus y pase a la tercera revisión
                    eventos[10] = reloj +archivoActual.tamano/(8*3); 
                    break;
                case 3:
                    //caso en que tenga 3 virus, ya no pasará
                    eventos[10] = reloj +archivoActual.tamano/(8*3); 
                    break;

                default:
                    break;
            }
        }
        else{
            eventos[10] = -1; //Se desprograma este evento.
        }
    }
    
    
    
    // Evento en el que se libera la línea 1 del router
    void seLiberaLinea1() {
        
        interfaz.escribirResultado("Evento:  Se liberó la línea 1 del router\n\n");
        
        reloj = eventos[11]; //Se actualiza el reloj
        
        // Si hay archivos en la cola de salida del antivirus
        if ( colaSalidaAntivirus.size() > 0 ) {
            
            interfaz.escribirResultado("Router enviando archivo por línea 1...\n\n");
            
            Archivo actual;
            actual = colaSalidaAntivirus.pop(); //**Creo que hay que hacer algo con este archivo
            eventos[11] = reloj + (actual.tamano / 64);
            linea1routerLibre = false;
        }
        else {
            linea1routerLibre = true;
            eventos[11] = -1; //Se desprograma el evento
        }
    }
    
    
    
    // Evento en el que se libera la línea 2 del router
    void seLiberaLinea2() {
        
        interfaz.escribirResultado("Evento:  Se liberó la línea 2 del router\n\n");
        
        reloj = eventos[12]; //Se actualiza el reloj
        
        
        // Si hay archivos en la cola de salida del antivirus
        if ( colaSalidaAntivirus.size() > 0 ) {
            
            interfaz.escribirResultado("Router enviando archivo por línea 2...\n\n");
            
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
    
    
    // Genera un número aleatorio con distribución exponencial
    private double generarExponencial() {
        
        // f(x) = lambda*e^(-lambda*x)
        // F(x) = 1-e^(-lambda*x)
        // x = - ln(1-r)/lambda
        
        double lambda = 0.2;

        double r = Math.random();   //Número aleatorio entre 0 y 1 con distribución uniforme.

        double x = -Math.log(1 - r) / lambda;

        return x;
    }

    
    
    // Genera un número aleatorio con distribución normal
    private double generarNormal() {

        return 0;
    }
    
    
    
    //Genera un número aleatorio con distribución f(x) = x/40
    private double generarDistB() {

        // f(x) = x/40
        // F(x) = ( (x^2)-64 ) / 80
        // x = sqrt(80r + 64)
        
        double r = Math.random();   //Número aleatorio entre 0 y 1 con distribución uniforme.

        double x = Math.sqrt(80 * r + 64);

        return x;
    }
    
    
    
    // Genera una priorida 1 ó 2 aleatoria
    private int generarPrioridad() {

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
    

    
    // Genera un tamaño de 1 a 64 aleatorio
    private int generarTamano() {

        // El tamaño de cada paquete es uniforme entre 1 y 64
        int min = 1;
        int max = 64;

        Random r = new Random();
        int tamano = r.nextInt(max - min + 1) + min; //Número aleatorio entre 1 y 64 con distribución uniforme.

        return tamano;
    }
    
    
    
    // Genera un número de virus aleatorio
    private int generarNumVirus() {
        
        int min = 1;
        int max = 100;
        int probabilidad;
        int numero_virus;

        Random r = new Random();
        probabilidad = r.nextInt(max - min + 1) + min; //Numero aleatorio entre 1 y 100
        //Pregunto si da en el rango de virus, 1 virus
        if (probabilidad < 6) {
            probabilidad = r.nextInt(max - min + 1) + min; //Numero aleatorio entre 1 y 100
            //Pregunto si da en el rango de virus, 2 virus
            if (probabilidad < 6) {
                probabilidad = r.nextInt(max - min + 1) + min; //Numero aleatorio entre 1 y 100
                //Pregunto si da en el rango de virus, 3 virus
                if (probabilidad < 6) {
                    numero_virus = 100;
                } else {
                    numero_virus = 2;
                }
            } else {
                numero_virus = 1;
            }
        } else {
            numero_virus = 0;
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
                
                // Si no se interrumpe la simulación
                if( Sim.interrupcion == false ){
                    Sim.iniciarSimulacion();
                }
                else{
                    break; // Si se interrumple la simulación
                }
            }
                    
            Sim.finalizarSimulacionGlobal();
        }

    }
}