import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Scanner;

public class ModHandler { // TODO -- MAKE THIS WORK, DOES NOT CREATE THE RIGHT DIRECTORY CURRENTLY

    private static String modsFolderPathMC; // will be passed through, this is the path to the minecraft/mods folder
    private static String modsFolderPath; // path to the mods folder being used for modpack creation
    private static String modpackName = "modpacks/";
    private static File[] modsInTheModpack;
    private static File[] modsInModpackForLoadingModpacks;
    private static Logger logger;
    private static String loader;

    public ModHandler(Logger logger) {
        this.logger = logger;
    }
    public void createModpackSaveFile() { // I apologize for the nonsensical names here, I am incredibly tired as I make this -- I will not fix them later
        File thoseWhoEnable = new File(modpackName + "_enabled_mods.txt");
        try {
            if (thoseWhoEnable.createNewFile()) { // Create the file if it does not exist
                System.out.println("File created: " + thoseWhoEnable.getName());
                logger.log("Modpack save file created: " + thoseWhoEnable.getName());
            } else { // If the file already exists, say so
                System.out.println("File already exists.");
                logger.log("Modpack save file already exists: " + thoseWhoEnable.getName());
            }
        } catch (Exception e) { // Catch any errors
            System.out.println("An error occurred.");
            logger.log("An error occurred while creating modpack save file: " + thoseWhoEnable.getName());
            logger.log(e.getMessage());
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
            logger.log("Successfully wrote to the file.");

        } catch (Exception e) {
            System.out.println("An error occurred while writing to the file.");
            logger.log("An error occurred while writing to the file: " + filename);
            logger.log(e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    public void loadEnabledMods() {
        String filename = modpackName + "_modpack";
        getModsInModpack(filename);
        try{
            clearMCModsFolder(); // clear the minecraft mods folder before loading new mods
            for (File mod : modsInModpackForLoadingModpacks){
                fileCopier(mod, new File(modsFolderPathMC, mod.getName())); // copy the mod file to the minecraft mods folder
                System.out.println("Enabled mod: " + mod);
                logger.log("Enabled mod: " + mod);
            }
        } catch (IOException e) {
            logger.log("An error occurred while reading from the file.");
            logger.log(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public void getModsInModpack(String modpack){

        modpack = modpack.replace("_enabled_mods.txt", ""); // remove the _enabled_mods part

        File modpackFolder = new File(modpack);
        System.out.println(modpackFolder.getName());
        File[] files = modpackFolder.listFiles();

        System.out.println("Modpack to load mods from: " + modpackName);
        System.out.println("Loading mods from modpack folder: " + modpack);
        System.out.println("Files found: " + Arrays.toString(files));

        logger.log("Modpack to load mods from: " + modpackName);
        logger.log("Loading mods from modpack folder: " + modpack);
        logger.log("Files found: " + Arrays.toString(files));

        if (files!=null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    System.out.println("File: " + file.getName() + "  is in the modpack folder.");
                    logger.log("File: " + file.getName() + "  is in the modpack folder.");
                    modsInModpackForLoadingModpacks = files;
                }
            }
        }
    }

    public void moveEnabledModsToModpackFolder(){ // move the enabled mods to the modpack folder
        File modpackFolder = new File(modpackName + "_modpack");
        File initalModsFolder = new File(modsFolderPath);
        String filename = modpackName + "_enabled_mods.txt";

        try { clearModpackFolder(); } catch (IOException e) { throw new RuntimeException(); }

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String modName = scanner.nextLine();
                File modFile = new File(initalModsFolder, modName);
                if (modFile.exists()) {
                    fileCopier(modFile, new File(modpackFolder, modFile.getName())); // copy the mod file to the modpack folder
                    System.out.println("Moved mod to modpack folder: " + modName);
                    logger.log("Moved mod to modpack folder: " + modName);
                } else {
                    System.out.println("Mod file not found: " + modName);
                    logger.log("Mod file not found: " + modName);
                }
            }
        }catch (FileNotFoundException e) {
            logger.log("Mod file not found: " + filename);
            logger.log(e.getMessage());
            throw new RuntimeException(e);

        }catch (IOException e) {
            logger.log("An error occurred while moving mods to modpack folder.");
            logger.log(e.getMessage());
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
                logger.log("Modpack folder created: " + modpackFolder.getName());
            } else {
                System.out.println("Failed to create modpack folder.");
                logger.log("Failed to create modpack folder: " + modpackFolder.getName());
            }
        } else {
            System.out.println("Modpack folder already exists.");
            logger.log("Modpack folder already exists: " + modpackFolder.getName());
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
                    logger.log("File: " + file.getName() + "  is in the modpack folder.");
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
                        logger.log("Deleted file: " + file.getName());
                    } else {
                        System.out.println("Failed to delete file: " + file.getName());
                        logger.log("Failed to delete file: " + file.getName());
                    }
                }
            }
        }
        System.out.println();
    }

    public static void clearModpackFolder() throws IOException{
        File dir = new File(modpackName + "_modpack");
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    if (file.delete()) {
                        System.out.println("Deleted file: " + file.getName());
                        logger.log("Deleted file: " + file.getName());
                    } else {
                        System.out.println("Failed to delete file: " + file.getName());
                        logger.log("Failed to delete file: " + file.getName());
                    }
                }
            }
        }
        System.out.println();
    }

    private static void fileCopier(File source, File dest) throws  IOException { // https://stackoverflow.com/questions/16433915/how-to-copy-file-from-one-location-to-another-location
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
        logger.log("Copied " + source.getName() + " to " + dest.getName());
    }

    public void setModsFolderPath(String path){ modsFolderPath = path; logger.log("Set mods folder path to: " + path); }

    public void setModsFolderPathMC(String path){ modsFolderPathMC = path; logger.log("Set mods folder path to: " + path); }

    public void setModpackName(String name){ modpackName = "modpacks/" + loader + name; logger.log("Set modpack name to: " + name); }

    public void setLoader(String modLoader){ loader = modLoader; logger.log("Set loader to: " + loader); }
}
