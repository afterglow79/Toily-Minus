import java.io.File;
import java.util.Scanner;

public class Main {

    static File[] modFiles;
    public static void main(String[] args) {

        System.out.println("Starting");
        System.out.println("--------------------------------\n");
        Scanner scanner = new Scanner(System.in);

        generateDirectories(); // generate any needed directories

        WindowHandler mainWindow = new WindowHandler();
        mainWindow.init();

    }

    public static void generateDirectories(){
        File modsDir = new File("modpacks");
        if (!modsDir.exists()) {
            if (modsDir.mkdir()) {
                System.out.println("Modpacks directory created: " + modsDir.getName());
            } else {
                System.out.println("Failed to create modpacks directory.");
            }
        }
    }
}
