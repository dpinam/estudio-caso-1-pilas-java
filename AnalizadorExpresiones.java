import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Análisis de expresiones aritméticas usando pilas.
 * Ofrece: tokenización, validación de paréntesis, conversión de infija a
 * postfija (algoritmo Shunting-Yard) y evaluación de la expresión postfija.
 */
public class AnalizadorExpresiones {

    /**
     * Divide la expresión en tokens: números literales, variables, operadores y paréntesis.
     * @throws IllegalArgumentException si aparece un carácter no válido
     */
    public static List<Token> tokenizar(String expresion) {
        List<Token> tokens = new ArrayList<>();
        int i = 0;
        while (i < expresion.length()) {
            char c = expresion.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                int inicio = i;
                while (i < expresion.length()
                        && (Character.isDigit(expresion.charAt(i)) || expresion.charAt(i) == '.')) {
                    i++;
                }
                String numero = expresion.substring(inicio, i);
                if (numero.chars().filter(ch -> ch == '.').count() > 1 || numero.equals(".")) {
                    throw new IllegalArgumentException("Número mal formado: '" + numero + "'");
                }
                tokens.add(new Token(Token.Tipo.NUMERO, numero));
            } else if (Character.isLetter(c) || c == '_') {
                int inicio = i;
                while (i < expresion.length()
                        && (Character.isLetterOrDigit(expresion.charAt(i)) || expresion.charAt(i) == '_')) {
                    i++;
                }
                tokens.add(new Token(Token.Tipo.VARIABLE, expresion.substring(inicio, i)));
            } else if ("+-*/%^".indexOf(c) >= 0) {
                tokens.add(new Token(Token.Tipo.OPERADOR, String.valueOf(c)));
                i++;
            } else if (c == '(') {
                tokens.add(new Token(Token.Tipo.PAREN_ABRE, "("));
                i++;
            } else if (c == ')') {
                tokens.add(new Token(Token.Tipo.PAREN_CIERRA, ")"));
                i++;
            } else {
                throw new IllegalArgumentException("Carácter no válido: '" + c + "' (posición " + i + ")");
            }
        }
        return tokens;
    }

    /**
     * Verifica que los paréntesis estén balanceados usando una pila.
     * @return mensaje de error, o null si están balanceados
     */
    public static String validarParentesis(List<Token> tokens) {
        PilaTokens pila = new PilaTokens();
        for (Token t : tokens) {
            if (t.getTipo() == Token.Tipo.PAREN_ABRE) {
                pila.push(t);
            } else if (t.getTipo() == Token.Tipo.PAREN_CIERRA) {
                if (pila.estaVacia()) {
                    return "Paréntesis de cierre ')' sin su correspondiente de apertura.";
                }
                pila.pop();
            }
        }
        if (!pila.estaVacia()) {
            return "Hay " + pila.size() + " paréntesis de apertura '(' sin cerrar.";
        }
        return null;
    }

    /**
     * Valida la secuencia de tokens: no deben ir dos operandos ni dos operadores
     * seguidos, ni haber paréntesis vacíos, y la expresión debe terminar en operando o ')'.
     * @return mensaje de error, o null si la sintaxis es correcta
     */
    public static String validarSintaxis(List<Token> tokens) {
        if (tokens.isEmpty()) {
            return "La expresión está vacía.";
        }
        String errorParentesis = validarParentesis(tokens);
        if (errorParentesis != null) {
            return errorParentesis;
        }
        Token anterior = null;
        for (Token actual : tokens) {
            // Se espera un operando o '(' al inicio o después de un operador o '('
            boolean seEsperaOperando = anterior == null
                    || anterior.esOperador()
                    || anterior.getTipo() == Token.Tipo.PAREN_ABRE;
            if (seEsperaOperando) {
                if (actual.esOperador()) {
                    return "Operador '" + actual + "' inesperado: falta un operando antes.";
                }
                if (actual.getTipo() == Token.Tipo.PAREN_CIERRA) {
                    return "Paréntesis vacío o expresión incompleta antes de ')'.";
                }
            } else {
                if (actual.esOperando() || actual.getTipo() == Token.Tipo.PAREN_ABRE) {
                    return "Falta un operador antes de '" + actual + "'.";
                }
            }
            anterior = actual;
        }
        if (anterior.esOperador() || anterior.getTipo() == Token.Tipo.PAREN_ABRE) {
            return "La expresión termina de forma incompleta en '" + anterior + "'.";
        }
        return null;
    }

    /**
     * Convierte una expresión infija (lista de tokens) a notación postfija
     * mediante el algoritmo Shunting-Yard. La pila guarda operadores y paréntesis.
     * Se asume que la sintaxis ya fue validada.
     */
    public static List<Token> infijaAPostfija(List<Token> infija) {
        List<Token> salida = new ArrayList<>();
        PilaTokens pila = new PilaTokens();
        for (Token t : infija) {
            if (t.esOperando()) {
                salida.add(t);
            } else if (t.esOperador()) {
                while (!pila.estaVacia() && pila.peek().esOperador()
                        && (pila.peek().precedencia() > t.precedencia()
                        || (pila.peek().precedencia() == t.precedencia() && !t.asociativoDerecha()))) {
                    salida.add(pila.pop());
                }
                pila.push(t);
            } else if (t.getTipo() == Token.Tipo.PAREN_ABRE) {
                pila.push(t);
            } else { // PAREN_CIERRA
                while (!pila.estaVacia() && pila.peek().getTipo() != Token.Tipo.PAREN_ABRE) {
                    salida.add(pila.pop());
                }
                pila.pop(); // descarta el '('
            }
        }
        while (!pila.estaVacia()) {
            salida.add(pila.pop());
        }
        return salida;
    }

    /**
     * Evalúa una expresión postfija con una pila de operandos.
     * @param variables valores asignados a las variables numéricas
     * @throws IllegalArgumentException si falta el valor de una variable
     * @throws ArithmeticException si hay división entre cero
     */
    public static double evaluarPostfija(List<Token> postfija, Map<String, Double> variables) {
        PilaTokens pila = new PilaTokens();
        for (Token t : postfija) {
            if (t.getTipo() == Token.Tipo.NUMERO) {
                pila.push(t);
            } else if (t.getTipo() == Token.Tipo.VARIABLE) {
                Double valor = variables.get(t.getValor());
                if (valor == null) {
                    throw new IllegalArgumentException("La variable '" + t.getValor() + "' no tiene valor asignado.");
                }
                pila.push(new Token(Token.Tipo.NUMERO, String.valueOf(valor)));
            } else {
                double b = Double.parseDouble(pila.pop().getValor());
                double a = Double.parseDouble(pila.pop().getValor());
                double resultado = aplicar(t.getValor(), a, b);
                pila.push(new Token(Token.Tipo.NUMERO, String.valueOf(resultado)));
            }
        }
        return Double.parseDouble(pila.pop().getValor());
    }

    private static double aplicar(String operador, double a, double b) {
        switch (operador) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                if (b == 0) {
                    throw new ArithmeticException("División entre cero.");
                }
                return a / b;
            case "%":
                if (b == 0) {
                    throw new ArithmeticException("Módulo entre cero.");
                }
                return a % b;
            case "^":
                return Math.pow(a, b);
            default:
                throw new IllegalArgumentException("Operador desconocido: " + operador);
        }
    }

    /** Une una lista de tokens en una cadena legible separada por espacios. */
    public static String unir(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        for (Token t : tokens) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(t);
        }
        return sb.toString();
    }
}
