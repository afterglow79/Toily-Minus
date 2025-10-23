import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    static File[] modFiles;
    public static void main(String[] args) {

        System.out.println("Starting");

        modFiles = getFiles();

        WindowHandler mainWindow = new WindowHandler();
        mainWindow.createLabels(modFiles);
        mainWindow.createWindow();

        Scanner scanner = new Scanner(System.in);

        if (Objects.equals(scanner.nextLine(), "test")){
            System.out.println(Arrays.toString(mainWindow.getEnabledModsIndexes()));
            System.out.println(Arrays.toString(mainWindow.getEnabledModsNames()));
        }
    }



    public static File[] getFiles(){ // get all files in a given directory
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input a directory to scan for mods:");
        String dirPath = scanner.nextLine();
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

        return files;
    }
}
