import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Programa principal: menú interactivo para operar la pila de tokens y
 * analizar expresiones aritméticas.
 */
public class Main {

    private static final Scanner TECLADO = new Scanner(System.in);
    private static final PilaTokens PILA = new PilaTokens();
    private static final Map<String, Double> VARIABLES = new HashMap<>();

    public static void main(String[] args) {
        int opcion;
        do {
            menu();
            opcion = leerEntero("Seleccione una opción: ");
            System.out.println();
            ejecutar(opcion);
        } while (opcion != 0);
        System.out.println("Hasta pronto.");
    }

    /** Muestra el menú principal. */
    private static void menu() {
        System.out.println("==============================================");
        System.out.println("   ANALIZADOR DE EXPRESIONES ARITMÉTICAS");
        System.out.println("==============================================");
        System.out.println(" --- Operaciones sobre la pila ---");
        System.out.println(" 1. Apilar (push) un token");
        System.out.println(" 2. Desapilar (pop)");
        System.out.println(" 3. Ver la cima (peek)");
        System.out.println(" 4. Mostrar la pila");
        System.out.println(" 5. Vaciar la pila");
        System.out.println(" --- Análisis de expresiones ---");
        System.out.println(" 6. Verificar paréntesis y sintaxis");
        System.out.println(" 7. Convertir a notación postfija");
        System.out.println(" 8. Evaluar una expresión");
        System.out.println(" 9. Definir el valor de una variable");
        System.out.println(" 0. Salir");
        System.out.println("----------------------------------------------");
    }

    private static void ejecutar(int opcion) {
        try {
            switch (opcion) {
                case 1:
                    apilarToken();
                    break;
                case 2:
                    System.out.println("Se extrajo: " + PILA.pop());
                    break;
                case 3:
                    System.out.println("Cima: " + PILA.peek());
                    break;
                case 4:
                    System.out.println("Pila (" + PILA.size() + " elementos): " + PILA);
                    break;
                case 5:
                    PILA.limpiar();
                    System.out.println("La pila fue vaciada.");
                    break;
                case 6:
                    verificar();
                    break;
                case 7:
                    convertir();
                    break;
                case 8:
                    evaluar();
                    break;
                case 9:
                    definirVariable();
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println();
    }

    private static void apilarToken() {
        System.out.print("Escriba el token (número, variable, operador o paréntesis): ");
        String texto = TECLADO.nextLine().trim();
        List<Token> tokens = AnalizadorExpresiones.tokenizar(texto);
        if (tokens.size() != 1) {
            System.out.println("Debe ingresar exactamente un token.");
            return;
        }
        PILA.push(tokens.get(0));
        System.out.println("Apilado: " + tokens.get(0));
    }

    private static List<Token> pedirExpresionValida() {
        System.out.print("Ingrese la expresión (ej. (a + 3) * 2 ^ 2): ");
        String texto = TECLADO.nextLine();
        List<Token> tokens = AnalizadorExpresiones.tokenizar(texto);
        String error = AnalizadorExpresiones.validarSintaxis(tokens);
        if (error != null) {
            System.out.println("Expresión inválida: " + error);
            return null;
        }
        return tokens;
    }

    private static void verificar() {
        List<Token> tokens = pedirExpresionValida();
        if (tokens != null) {
            System.out.println("La expresión es válida: " + AnalizadorExpresiones.unir(tokens));
        }
    }

    private static void convertir() {
        List<Token> tokens = pedirExpresionValida();
        if (tokens != null) {
            System.out.println("Postfija: " + AnalizadorExpresiones.unir(AnalizadorExpresiones.infijaAPostfija(tokens)));
        }
    }

    private static void evaluar() {
        List<Token> tokens = pedirExpresionValida();
        if (tokens == null) {
            return;
        }
        // Solicita el valor de las variables que aún no estén definidas
        for (Token t : tokens) {
            if (t.getTipo() == Token.Tipo.VARIABLE && !VARIABLES.containsKey(t.getValor())) {
                VARIABLES.put(t.getValor(), leerDecimal("Valor de la variable '" + t.getValor() + "': "));
            }
        }
        List<Token> postfija = AnalizadorExpresiones.infijaAPostfija(tokens);
        double resultado = AnalizadorExpresiones.evaluarPostfija(postfija, VARIABLES);
        System.out.println("Postfija: " + AnalizadorExpresiones.unir(postfija));
        System.out.println("Resultado: " + resultado);
    }

    private static void definirVariable() {
        System.out.print("Nombre de la variable: ");
        String nombre = TECLADO.nextLine().trim();
        if (!nombre.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            System.out.println("Nombre de variable no válido.");
            return;
        }
        VARIABLES.put(nombre, leerDecimal("Valor de " + nombre + ": "));
        System.out.println(nombre + " = " + VARIABLES.get(nombre));
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(TECLADO.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static double leerDecimal(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Double.parseDouble(TECLADO.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
            }
        }
    }
}
