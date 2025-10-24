import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    static File[] modFiles;
    public static void main(String[] args) {

        System.out.println("Starting");
        System.out.println("--------------------------------\n");
        Scanner scanner = new Scanner(System.in);
        generateDirectories(); // generate any needed directories

//        System.out.println("Input a directory to scan for mods:");
//        String dirPath = scanner.nextLine();
        String dirPath = "mods"; // hardcoded for testing
        String mcModsPath = "pretendThisIsMinecraftModsFolder"; // hardcoded for testing
        String modpackTextFolder = "modpackTextFiles"; // hardcoded for testing

        modFiles = getFiles(dirPath);

        WindowHandler mainWindow = new WindowHandler();
        mainWindow.init(dirPath, mcModsPath, modpackTextFolder, modFiles, scanner.nextLine());
//
//
//        if (scanner.nextInt() == 1){
//            System.out.println("Continuing..."); // simple way to pause the program for testing
//        }
//
//        String modpackName = "test2";
//        ModHandler modHandler = new ModHandler();
//
//        // it would make sense to run these through the WindowHandler class because it would update on each checkbox tick/button press
//        modHandler.setModpackName(modpackName);
//        modHandler.setModsFolderPath(dirPath);
//        modHandler.setModsFolderPathMC("pretendThisIsMinecraftModsFolder"); // hardcoded for testing
//
//        modHandler.createNewModpackFolder();
//        modHandler.createModpackSaveFile();
//
//        modHandler.saveEnabledMods(mainWindow.getEnabledModsNames()); // save the enabled mods based on checkbox states in the window
//        modHandler.moveEnabledModsToModpackFolder(); // move the enabled mods to the modpack folder
//        modHandler.loadEnabledMods(); // load the enabled mods into the minecraft mods folder
    }



    public static File[] getFiles(String dirPath){ // get all files in a given directory
        File workingDir = new File(dirPath);
        File[] files = workingDir.listFiles();

        if (files != null){
            for (File file: files){
                if (!file.isDirectory()) {
                    System.out.println("File found: " + file.getName());
                }
            }
        } else {
            System.out.println("The directory is empty or does not exist.");
        }
        System.out.println();
        return files;
    }

    public static void generateDirectories(){
        File modsDir = new File("modpackTextFiles");
        if (!modsDir.exists()){
            if (modsDir.mkdir()){
                System.out.println("Mods directory created: " + modsDir.getName());
            } else {
                System.out.println("Failed to create mods directory.");
            }
        } else {
            System.out.println("Mods directory already exists.");
        }

        modsDir = new File("modpacks");
        if (!modsDir.exists()) {
            if (modsDir.mkdir()) {
                System.out.println("Modpacks directory created: " + modsDir.getName());
            } else {
                System.out.println("Failed to create modpacks directory.");
            }
        }
    }
}
