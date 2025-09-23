import java.util.Scanner; 
public class SimpleIO { 
    public static void main(String[] args) { 
        Scanner sc = new Scanner(System.in); 
        boolean hadError = false;
        while (sc.hasNextLine()) { 
            String line = sc.nextLine(); 
            if (line.startsWith("ERROR")) { 
                // Mensaje de error → stderr 
                System.err.println("stderr: " + line); 
                hadError = true;
            } else { 
                // Mensaje normal → stdout 
                System.out.println("stdout: " + line); 
            } 
        } 
        sc.close();
        if (hadError) System.exit(1);
    } 
}