import java.math.BigInteger;
import java.util.Scanner;

public class KaratsubaMultiplicacion {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== MULTIPLICACIÓN KARATSUBA ===");
        System.out.println("Ingrese el primer número:");
        String num1 = scanner.nextLine().trim();
        System.out.println("Ingrese el segundo número:");
        String num2 = scanner.nextLine().trim();
        
        if (!esValido(num1) || !esValido(num2)) {
            System.out.println("Error: Solo dígitos permitidos");
            scanner.close();
            return;
        }
        
        // Convertir a arrays (LSB en índice 0)
        int[] x = stringToArray(num1);
        int[] y = stringToArray(num2);
        
        // Ejecutar Karatsuba
        int[] resultado = karatsuba(x, y);
        
        System.out.println("\nResultado Karatsuba: " + arrayToString(resultado));
        
        // Verificación con BigInteger (maneja números grandes)
        BigInteger big1 = new BigInteger(num1);
        BigInteger big2 = new BigInteger(num2);
        BigInteger producto = big1.multiply(big2);
        System.out.println("Verificación:        " + producto.toString());
        
        scanner.close();
    }
    
    // Implementación de KARATSUBA
    public static int[] karatsuba(int[] x, int[] y) {
        // Normalizar: quitar ceros a la izquierda
        x = quitarCeros(x);
        y = quitarCeros(y);
        
        // Caso base: usar multiplicación clásica para números pequeños
        if (x.length < 10 || y.length < 10) {
            return multiplicacionDirecta(x, y);
        }
        
        // Asegurar que n sea potencia de 2 o usar el máximo
        int n = Math.max(x.length, y.length);
        // Calcular la mitad, dividir el problema
        int m = n / 2;
        
        // Dividir x en a (parte alta) y b (parte baja) = [a|b]
        int[] b = subArray(x, 0, m);        // m dígitos menos significativos
        int[] a = subArray(x, m, x.length); // resto (más significativos)
        
        // Dividir y en c (parte alta) y d (parte baja) = [c|d]
        int[] d = subArray(y, 0, m);
        int[] c = subArray(y, m, y.length);
        
        // Recursión: z0 = b*d, z2 = a*c
        int[] z0 = karatsuba(b, d);
        int[] z2 = karatsuba(a, c);
        
        // z1 = (a+b)*(c+d) - z2 - z0
        int[] aMasB = sumar(a, b);
        int[] cMasD = sumar(c, d);
        int[] z1 = karatsuba(aMasB, cMasD);
        z1 = restar(z1, z2);
        z1 = restar(z1, z0);
        
        // Combinar: resultado = z2*10^(2m) + z1*10^m + z0
        int[] termino1 = shiftIzquierda(z2, 2 * m);  // * 10^2m
        int[] termino2 = shiftIzquierda(z1, m);      // * 10^m
        
        return quitarCeros(sumar(sumar(termino1, termino2), z0));
    }
    
    // Multiplicación Directa
    public static int[] multiplicacionDirecta(int[] x, int[] y) {
        if (esCero(x) || esCero(y)) return new int[]{0};
        
        int[] resultado = new int[x.length + y.length];
        
        for (int i = 0; i < x.length; i++) {
            int carry = 0;
            for (int j = 0; j < y.length; j++) {
                int producto = resultado[i + j] + x[i] * y[j] + carry;
                resultado[i + j] = producto % 10;
                carry = producto / 10;
            }
            int pos = i + y.length;
            while (carry > 0) {
                int suma = resultado[pos] + carry;
                resultado[pos] = suma % 10;
                carry = suma / 10;
                pos++;
            }
        }
        return quitarCeros(resultado);
    }
    
    // SUMA con manejo de carry
    public static int[] sumar(int[] a, int[] b) {
        int max = Math.max(a.length, b.length);
        int[] res = new int[max + 1];
        int carry = 0;
        
        for (int i = 0; i < max || carry > 0; i++) {
            int da = (i < a.length) ? a[i] : 0;
            int db = (i < b.length) ? b[i] : 0;
            int suma = da + db + carry;
            res[i] = suma % 10;
            carry = suma / 10;
        }
        return quitarCeros(res);
    }
    
    // RESTA (asume a >= b)
    public static int[] restar(int[] a, int[] b) {
        int[] res = new int[a.length];
        int borrow = 0;
        
        for (int i = 0; i < a.length; i++) {
            int db = (i < b.length) ? b[i] : 0;
            int diff = a[i] - db - borrow;
            if (diff < 0) {
                diff += 10;
                borrow = 1;
            } else {
                borrow = 0;
            }
            res[i] = diff;
        }
        
        if (borrow > 0) {
            // Esto no debería pasar
            throw new RuntimeException("Resultado negativo en resta");
        }
        
        return quitarCeros(res);
    }
    
    // Multiplicar por 10^n (agregar n ceros al final/LSB)
    public static int[] shiftIzquierda(int[] num, int n) {
        if (esCero(num)) return new int[]{0};
        int[] res = new int[num.length + n];
        System.arraycopy(num, 0, res, n, num.length);
        return res;
    }
    
    // Obtener sub-array desde inicio hasta fin 
    // Si inicio >= length, devuelve [0]
    public static int[] subArray(int[] arr, int inicio, int fin) {
        if (inicio >= arr.length) return new int[]{0};
        if (fin > arr.length) fin = arr.length;
        if (inicio < 0) inicio = 0;
        
        int[] res = new int[fin - inicio];
        System.arraycopy(arr, inicio, res, 0, fin - inicio);
        return quitarCeros(res);
    }
    
    // Quitar ceros a la izquierda (MSB), pero mantener al menos uno
    public static int[] quitarCeros(int[] arr) {
        int i = arr.length - 1;
        while (i > 0 && arr[i] == 0) i--;
        
        int[] res = new int[i + 1];
        System.arraycopy(arr, 0, res, 0, i + 1);
        return res;
    }
    
    public static boolean esCero(int[] arr) {
        return arr.length == 1 && arr[0] == 0;
    }
    
    public static int[] stringToArray(String s) {
        int n = s.length();
        int[] arr = new int[n];
        for (int i = 0; i < n; i++) {
            arr[i] = s.charAt(n - 1 - i) - '0';
        }
        return quitarCeros(arr);
    }
    
    public static String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder(arr.length);
        for (int i = arr.length - 1; i >= 0; i--) {
            sb.append(arr[i]);
        }
        return sb.toString();
    }
    
    public static boolean esValido(String s) {
        return s.matches("\\d+");
    }
}