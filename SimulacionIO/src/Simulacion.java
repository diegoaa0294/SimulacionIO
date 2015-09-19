
import java.util.Random;

/**
 *
 * @author Jean Carlos Zuñiga
 * @author Diego Angulo
 *  
 */


public class Simulacion {
    
double reloj;        // Reloj de la simulación

// Valores especificados por el usuario
int numSimulaciones; // Número de veces que se va a correr la simulación
int tiempoTotal;     // Tiempo total en segundos para correr cada simulación
int velocidad;       // Velocidad de la simulación, 0 = modo rápido, 1 = modo lento
int duracionToken;   // Tiempo en segundos durante el cuál a cada máquina se le asigna el token


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


// Colas
//...


// Estructura de archivo
class Archivo{
    
    int prioridad;      // 1 o 2
    int tamano;         // 1 a 64 paquetes
    int virus;          // Cantidad de virus del archivo
    char computadora;   // A, B o C
    
    // Constructor
    Archivo( int p, int t, int v, char c){
        prioridad = p;
        tamano = t;
        virus = v;
        computadora = c;
    }
};


// Constructor
Simulacion( int ns, int tt, int v, int dt ){
    
    numSimulaciones = ns;
    tiempoTotal = tt;
    velocidad = v;
    duracionToken = dt;
    
    eventos = new double [13];
    reloj = 0;
    
    // Los primeros eventos a ocurrir
    eventos[0] = 0;
    eventos[1] = 0;
    eventos[2] = 0;
    
    // Se desprograman los demás eventos
    for(int i = 3; i < 13; i++){
        eventos[i] = -1;    // -1 representa tiempo infito
    }
}


void iniciarSimulacion(){
    siguenteEvento();
}

void finalizarSimulacion(){

}

void siguenteEvento(){
    
    int menor = 0;
    
    // Se escoje el evento con el menor tiempo de ocurrencia
    for(int i = 1; i < 13; i++){
        
        if(  eventos[i] != -1 ){
            
            if( eventos[menor] == -1 ){
                menor = i;
            }
            else if( eventos[i] < eventos[menor] ){
                menor = i;
            }
        }
    }
    
    switch ( menor ) {
        
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


/* --------------- EVENTOS --------------- */

void llegaArchivoA(){

    reloj = eventos[0];
    
    //Crear archivo
    
    int prioridad = generarPrioridad();
    int tamano = generarTamano();
    int virus = generarNumVirus();
    
    Archivo A = new Archivo( prioridad, tamano, virus, 'A');
    
    //Encolar archivo
    
    eventos[0] = reloj + generarExponencial();
}



void llegaArchivoB(){

    reloj = eventos[1];
    
    //Crear archivo
    
    int prioridad = generarPrioridad();
    int tamano = generarTamano();
    int virus = generarNumVirus();
    
    Archivo A = new Archivo( prioridad, tamano, virus, 'B');
    
    //Encolar archivo
    
    eventos[1] = reloj + generarDistB();
}



void llegaArchivoC(){

    reloj = eventos[2];
    
    //Crear archivo
    
    int prioridad = generarPrioridad();
    int tamano = generarTamano();
    int virus = generarNumVirus();
    
    Archivo A = new Archivo( prioridad, tamano, virus, 'C');
    
    //Encolar archivo
    
    eventos[2] = reloj + generarNormal();
}



void seLiberaA(){

}

void seLiberaB(){

}

void seLiberaC(){

}

void llegaTokenA(){

}
void llegaTokenB(){

}
void llegaTokenC(){

}

void llegaArchivoAntivirus(){
    
}

void seLiberaAntivirus(){
    
}

void seLiberaLinea1(){
    
}

void seLiberaLinea2(){
    
}


/* --------------- NUMEROS ALEATORIOS --------------- */
    
double generarExponencial(){
    
    double lambda = 1/5;
    
    double r = Math.random();   //Número aleatorio entre 0 y 1 con distribución uniforme.
    
    double x = -Math.log( 1-r )/lambda;
    
    return x;
}



double generarNormal(){

    return 0;
}



double generarDistB(){
    
    // f(x) = x/40
    // F(x) = ( (x^2)-64 ) / 80
    
    double r = Math.random();   //Número aleatorio entre 0 y 1 con distribución uniforme.
    
    double x = Math.sqrt( 80*r + 64 );
    
    return x;
}



int generarPrioridad(){
    
    int prioridad;
    
    Random r = new Random();
    int x = r.nextInt(100 - 1 + 1) + 1; //Número aleatorio entre 1 y 100 con distribución uniforme.
    
    //Cada archivo tiene una probabilidad de 0.25 de ser de prioridad 1 y 0.75 de ser prioridad 2
    
    if( x <= 25){
        prioridad = 1;
    }
    else{
        prioridad = 2;
    }
    
    return prioridad;
}



int generarTamano(){
    
    // El tamaño de cada paquete es uniforme entre 1 y 64
    int min = 1;
    int max = 64;

    Random r = new Random();
    int tamano = r.nextInt(max - min + 1) + min; //Número aleatorio entre 1 y 64 con distribución uniforme.
    
    return tamano;
}



int generarNumVirus(){
 
    return 0;
}

    
    public static void main(String[] args) {
        
        Simulacion S = new Simulacion( 1, 100, 0, 15 );
        
        S.iniciarSimulacion();
        
    }
}