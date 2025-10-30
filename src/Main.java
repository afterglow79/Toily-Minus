import java.io.File;
import java.util.Scanner;

public class Main {

    static File[] modFiles;

    public static void main(String[] args) {

        Logger logger = new Logger();
        logger.log("Application started.");
        logger.log("Program starting at: " + java.time.LocalDateTime.now());
        System.out.println("Starting at: " + java.time.LocalDateTime.now());
        System.out.println("--------------------------------\n");
        Scanner scanner = new Scanner(System.in);
        logger.log("Scanner initialized.");

        generateDirectories(); // generate any needed directories
        logger.log("Directories generated.");
        WindowHandler mainWindow = new WindowHandler();
        logger.log("Window variable set.");
        mainWindow.init(logger);


    }

    public static void generateDirectories() {
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