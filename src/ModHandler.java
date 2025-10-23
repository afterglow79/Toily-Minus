import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class ModHandler {

    private static String modsFolderPathMC; // will be passed through, this is the path to the minecraft/mods folder
    private static String modsFolderPath; // path to the mods folder being used for modpack creation
    private static String modpackName;
    private static File[] modsInTheModpack;

    public void createModpackSaveFile() { // I apologize for the nonsensical names here, I am incredibly tired as I make this -- I will not fix them later
        File thoseWhoEnable = new File(modpackName + "_enabled_mods.txt");
        try {
            if (thoseWhoEnable.createNewFile()) { // Create the file if it does not exist
                System.out.println("File created: " + thoseWhoEnable.getName());
            } else { // If the file already exists, say so
                System.out.println("File already exists.");
            }
        } catch (Exception e) { // Catch any errors
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        System.out.println();
    }

    public void saveEnabledMods(String[] mostEnabledMan) { // see above comment
        // saves the names of enabled mods to the text file
        String filename = modpackName + "_enabled_mods.txt";
        String content = String.join("\n", mostEnabledMan); // Join the mod names with new line separators
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(content); // write the content to the file
            System.out.println("Successfully wrote to the file.");


        } catch (Exception e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
        System.out.println();
    }

    public void loadEnabledMods() {
        String filename = modpackName + "_enabled_mods.txt";
        try{
            clearMCModsFolder(); // clear the minecraft mods folder before loading new mods
            for (File mod : modsInTheModpack){
                        fileCopier(mod, new File(modsFolderPathMC, mod.getName())); // copy the mod file to the minecraft mods folder
                        System.out.println("Enabled mod: " + mod);
                    }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void moveEnabledModsToModpackFolder(){ // move the enabled mods to the modpack folder
        File modpackFolder = new File(modpackName + "_modpack");
        File initalModsFolder = new File(modsFolderPath);

        String filename = modpackName + "_enabled_mods.txt";
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String modName = scanner.nextLine();
                File modFile = new File(initalModsFolder, modName);
                if (modFile.exists()) {
                    fileCopier(modFile, new File(modpackFolder, modFile.getName())); // copy the mod file to the modpack folder
                    System.out.println("Moved mod to modpack folder: " + modName);
                } else {
                    System.out.println("Mod file not found: " + modName);
                }
            }
        }catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
        modsInTheModpack = getFiles();

    }


    public void createNewModpackFolder(){
        File modpackFolder = new File(modpackName + "_modpack");
        if (!modpackFolder.exists()){
            if (modpackFolder.mkdir()){
                System.out.println("Modpack folder created: " + modpackFolder.getName());
            } else {
                System.out.println("Failed to create modpack folder.");
            }
        } else {
            System.out.println("Modpack folder already exists.");
        }
        System.out.println();
    }

    private static File[] getFiles(){
        File workingDir = new File(modpackName + "_modpack");
        File[] files = workingDir.listFiles();
        if (files!=null){
            for (File file: files){
                if (!file.isDirectory()) {
                    System.out.println("File: " + file.getName() + "  is in the modpack folder.");
                }
            }
        }
        System.out.println();
        return files;
    }

    public static void clearMCModsFolder() throws IOException {
        File dir = new File(modsFolderPathMC);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.delete()) {
                        System.out.println("Deleted file: " + file.getName());
                    } else {
                        System.out.println("Failed to delete file: " + file.getName());
                    }
                }
            }
        }
        System.out.println();
    }

    private static void fileCopier(File source, File dest) throws  IOException { // https://stackoverflow.com/questions/16433915/how-to-copy-file-from-one-location-to-another-location
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public void setModsFolderPath(String path){
        modsFolderPath = path;
    }

    public void setModsFolderPathMC(String path){
        modsFolderPathMC = path;
    }

    public void setModpackName(String name){
        modpackName = name;
    }
}
