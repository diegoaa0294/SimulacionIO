/**
 *
 * @author Diego
 * @author Jean Carlos
 *  
 */

//package SimulacionIO;

public class Simulacion {
    
int [] eventos;
int reloj;
int tiempoFin;
// colas


class Archivo{ // Estructura de archivo
    
    int prioridad; // 1 o 2
    int tamano;
    char tipo;     // A, B o C
    
    Archivo( int p, int t, char tipo){ // Costructor
        prioridad = p;
        tamano = t;
        this.tipo = tipo;
    }
};



Simulacion( int fin ){ // Constructor
    
    eventos = new int [10];
    reloj = 0;
    tiempoFin = fin;
    
    eventos[0] = 0; // El primer evento a ocurrir es llegaArchivoA
    
    // Se desprograman los demas eventos
    for(int i = 1; i < 9; i++){
        eventos[i] = -1;
    }
}


void iniciarSimulacion(){
    siguenteEvento();
}

void finalizarSimulacion(){

}

void siguenteEvento(){
    
    int siguiente = 10000;
    
    // Se escoje el evento con el menor tiempo de ocurrencia
    for(int i = 0; i < 9; i++){
        
        if( eventos[i] < siguiente && eventos[i] != -1 ){
            siguiente = i;
        }
    }
    
    switch ( siguiente ) {
        
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
        llegaTokenA();
        break;
        
        case 4:
        llegaTokenB();    
        break;
            
        case 5:
        llegaTokenC();
        break;
        
        case 6:
        llegaArchivoAntivirus();
        break;
        
        case 7:
        seLiberaAntivirus();
        break;
            
        case 8:
        seLiberaLinea1();
        break;
        
        case 9:
        seLiberaLinea2();
        break;
        
        default:
        break;
    }
}


/*     EVENTOS     */

void llegaArchivoA(){

}
void llegaArchivoB(){

}
void llegaArchivoC(){

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


/*     NUMEROS ALEATORIOS     */
    
int generarExponencial(){
return 0;
}

int generarUniforme(){
return 0;
}

int generarNormal(){
return 0;
}

// Falta f(x) = x/40


    
    public static void main(String[] args) {
        
        Simulacion S = new Simulacion( 90 );
        S.iniciarSimulacion();
        
    }
}