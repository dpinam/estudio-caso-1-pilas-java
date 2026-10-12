/**
 * Representa un token de una expresión aritmética.
 * Cada token tiene un tipo (número, variable, operador, paréntesis)
 * y su valor textual tal como aparece en la expresión.
 */
public class Token {

    /** Categorías posibles de un token. */
    public enum Tipo {
        NUMERO, VARIABLE, OPERADOR, PAREN_ABRE, PAREN_CIERRA
    }

    private final Tipo tipo;
    private final String valor;

    public Token(Tipo tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public boolean esOperando() {
        return tipo == Tipo.NUMERO || tipo == Tipo.VARIABLE;
    }

    public boolean esOperador() {
        return tipo == Tipo.OPERADOR;
    }

    /** Precedencia del operador: mayor número = se evalúa primero. */
    public int precedencia() {
        if (!esOperador()) {
            return -1;
        }
        switch (valor) {
            case "^":
                return 3;
            case "*":
            case "/":
            case "%":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return -1;
        }
    }

    /** El operador de potencia se asocia por la derecha; los demás por la izquierda. */
    public boolean asociativoDerecha() {
        return esOperador() && valor.equals("^");
    }

    @Override
    public String toString() {
        return valor;
    }
}
