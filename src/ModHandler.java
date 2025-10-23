import java.io.File;
import java.io.FileWriter;

public class ModHandler { // TODO -- MAKE SURE saveEnabledMods() WORKS, ALLOW FOR CREATION OF DYNAMIC MODPACKS, GET FILE LOADING AND TRANSFER AND THE LIKE WORKING

    public void createModpackSaveFile(String modpackName) { // I apologize for the nonsensical names here, I am incredibly tired as I make this -- I will not fix them later
        File thoseWhoEnable = new File(modpackName + "enabled_mods.txt");
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
    }

    public void saveEnabledMods(String modpackName, File[] mostEnabledMan) { // see above comment
        // saves the names of enabled mods to the text file
        File thoseWhoEnable = new File(modpackName + "enabled_mods.txt");
        try {
            FileWriter writer = new FileWriter(thoseWhoEnable); // open the file for writing
            for (File mod : mostEnabledMan) { // write each mod name on a new line
                writer.write(mod.getName() + System.lineSeparator());
            } writer.close(); // close the writer
        } catch (Exception e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
}
