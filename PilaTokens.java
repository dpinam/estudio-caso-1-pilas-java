/**
 * Pila (LIFO) de tokens implementada con nodos enlazados.
 * Todas las operaciones fundamentales (push, pop, peek) son O(1).
 */
public class PilaTokens {

    private Nodo cima;
    private int tamano;

    public PilaTokens() {
        this.cima = null;
        this.tamano = 0;
    }

    /** Inserta un token en la cima de la pila. O(1). */
    public void push(Token token) {
        Nodo nuevo = new Nodo(token);
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamano++;
    }

    /**
     * Elimina y devuelve el token de la cima. O(1).
     * @throws IllegalStateException si la pila está vacía (underflow)
     */
    public Token pop() {
        if (estaVacia()) {
            throw new IllegalStateException("Pila vacía: no hay elementos para extraer.");
        }
        Token dato = cima.getDato();
        cima = cima.getSiguiente();
        tamano--;
        return dato;
    }

    /**
     * Devuelve el token de la cima sin eliminarlo. O(1).
     * @throws IllegalStateException si la pila está vacía
     */
    public Token peek() {
        if (estaVacia()) {
            throw new IllegalStateException("Pila vacía: no hay cima que consultar.");
        }
        return cima.getDato();
    }

    public boolean estaVacia() {
        return cima == null;
    }

    public int size() {
        return tamano;
    }

    public void limpiar() {
        cima = null;
        tamano = 0;
    }

    /** Representación de la pila, de la cima hacia el fondo. */
    @Override
    public String toString() {
        if (estaVacia()) {
            return "[ vacía ]";
        }
        StringBuilder sb = new StringBuilder("cima -> ");
        Nodo actual = cima;
        while (actual != null) {
            sb.append("[").append(actual.getDato()).append("]");
            actual = actual.getSiguiente();
            if (actual != null) {
                sb.append(" ");
            }
        }
        sb.append(" <- fondo");
        return sb.toString();
    }
}
