/**
 * Nodo de la pila: contiene un Token y una referencia al nodo inferior.
 * Es la unidad básica de la lista enlazada simple sobre la que se construye la pila.
 */
public class Nodo {

    private final Token dato;
    private Nodo siguiente;

    public Nodo(Token dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Token getDato() {
        return dato;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }
}
