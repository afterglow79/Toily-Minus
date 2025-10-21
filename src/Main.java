import java.io.File;
import java.util.Scanner;

public class Main {

    static File[] modFiles;
    public static void main(String[] args) {
        System.out.println("Starting");

        modFiles = getFiles();

        WindowHandler mainWindow = new WindowHandler();
        mainWindow.createLabels(modFiles);
        mainWindow.createWindow();
    }



    public static File[] getFiles(){ // get all files in a given directory
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input a directory to scan for mods:");
        File workingDir = new File("C:\\Users\\cakeb\\curseforge\\minecraft\\Instances\\aydne (1)\\mods");
        File[] files = workingDir.listFiles();

        if (files != null){
            for (File file: files){
                if (file.isDirectory()) {

                } else {
                    System.out.println("File found: " + file.getName());
                }
            }
        } else {
            System.out.println("The directory is empty or does not exist.");
        }
        return files;
    }
}
