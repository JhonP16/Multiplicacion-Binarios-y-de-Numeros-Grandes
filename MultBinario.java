import java.util.Scanner;

public class MultBinario {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Multiplicador de Binarios ===");
        
        // Entrada de datos
        System.out.print("Introduce el primer número binario: ");
        String bin1 = sc.nextLine().trim();
        
        System.out.print("Introduce el segundo número binario: ");
        String bin2 = sc.nextLine().trim();

        // Validar que sean binarios (solo 0s y 1s)
        if (!bin1.matches("[01]+") || !bin2.matches("[01]+")) {
            System.out.println("Error: Solo se permiten 0 y 1.");
        } else {
            String resultado = multiplicar(bin1, bin2);
            
            System.out.println("\n--- Proceso Completado ---");
            System.out.println("Multiplicando: " + bin1);
            System.out.println("Multiplicador: " + bin2);
            System.out.println("Resultado (Binario): " + resultado);
            
            // Convertimos a decimal solo para verificar que el cálculo es correcto
            long decimal = Long.parseLong(resultado, 2);
            System.out.println("Resultado (Decimal): " + decimal);
        }
        
        sc.close();
    }

    // Algoritmo de multiplicación por desplazamiento y suma
    public static String multiplicar(String m1, String m2) {
        String producto = "0";
        String desplazamiento = "";

        // Recorremos el multiplicador de derecha a izquierda
        for (int i = m2.length() - 1; i >= 0; i--) {
            if (m2.charAt(i) == '1') {
                // Si el bit es 1, aplicamos el desplazamiento actual al multiplicando
                String sumandoActual = m1 + desplazamiento;
                producto = sumarBinarios(producto, sumandoActual);
            }
            // En cada iteración, el desplazamiento aumenta una posición (un cero más)
            desplazamiento += "0";
        }
        return producto;
    }

    // Suma lógica de dos cadenas binarias bit a bit
    public static String sumarBinarios(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1;
        int j = b.length() - 1;
        int acarreo = 0;

        while (i >= 0 || j >= 0 || acarreo != 0) {
            int suma = acarreo;
            if (i >= 0) suma += a.charAt(i--) - '0';
            if (j >= 0) suma += b.charAt(j--) - '0';
            
            sb.append(suma % 2); // El bit resultante es el residuo (0 o 1)
            acarreo = suma / 2;   // El acarreo es el cociente
        }
        return sb.reverse().toString();
    }
}