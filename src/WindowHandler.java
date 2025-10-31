import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class WindowHandler{ // TODO -- Allow for deletion of modpacks, differentiation between loaders, and the ability to search for mods when making a modpack
    public static JFrame mainWindow = new JFrame("Toily Minus");
    private static DefaultTableModel model;
    public static Boolean[] tableStates;
    private static Object[][] data;
    private static File[] mods;
    private static String mcModsPath;
    private static String modsPath;
    private static String modpackName;
    public static ModHandler modHandler;
    private static File modsDirectory;
    private static File mcModsDirectory;
    private boolean isEditingModpack = false;
    private boolean[] enabledMods;
    public static Logger logger;
    public static String modLoader;

    public void createWindow() throws FileNotFoundException {
        mainWindow.setSize(1280, 720);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.getContentPane().setLayout(new FlowLayout());
        generateFiles();
        Scanner fileScanner = new Scanner(modsDirectory);;

        if (fileScanner.hasNextLine()) {
            String modsDirPath = fileScanner.nextLine();
            setModsPath(modsDirPath);
        } else {
            getModsDirectory();
        }

        fileScanner = new Scanner(mcModsDirectory);
        if (fileScanner.hasNextLine()) {
            String mcModsDirPath = fileScanner.nextLine();
            setMcModsPath(mcModsDirPath);
        } else {
            getMinecraftModsDirectory();
        }

        setMods(getFiles(modsPath));
        createHomeScreen();
        mainWindow.setVisible(true);
    }

    public void createLabels(File[] files) {
        Container content = mainWindow.getContentPane();

        String[] columnNames = {"Is Enabled", "Mod Name"};
        data = new Object[files.length][2];
        tableStates = new Boolean[files.length];

        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory()) {
                data[i][1] = files[i].getName();
            }
        }

        if (isEditingModpack) {
            getModStates();
            for (int i = 0; i < files.length; i++) {
                data[i][0] = enabledMods[i]; // Set checkbox state based on enabledMods
                tableStates[i] = enabledMods[i];
            }
        } else {
            for (int i = 0; i < files.length; i++) {
                data[i][0] = Boolean.FALSE; // Default to disabled
                tableStates[i] = false;
            }} isEditingModpack = false;

        model = new DefaultTableModel(data, columnNames); // https://stackoverflow.com/questions/7391877/how-to-add-checkboxes-to-jtable-swing

        model.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();
            if (column == 0) { // Check if the checkbox column was modified
                boolean isChecked = (boolean) model.getValueAt(row, column);
                tableStates[row] = isChecked;

                System.out.println("Row " + row + " checkbox is now: " + isChecked + " | " + data[row][1]);
                System.out.println("\n Current table states:    " + Arrays.toString(tableStates));
                System.out.println();

                logger.log("Row " + row + " checkbox is now: " + isChecked + " | " + data[row][1]);
                logger.log(" Current table states:    " + Arrays.toString(tableStates));
            }
            logger.log(""); // intentionally blank for a newline
            logger.log("---------------- Enabled Mods ----------------");
            for (int i = 0; i < tableStates.length; i++) {
                if (tableStates[i] == true) {
                    System.out.println(data[i][1] + "   is enabled");
                    logger.log(data[i][1] + " is enabled");
                }
            }
            System.out.println();
            logger.log("------------------------------------------------\n");

        });

        JTable table = new JTable(model){
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 1:
                        return String.class;
                    default:
                        return Object.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // only allow toggling the checkbox
            }
        };
        table.setSize(content.getWidth()/ 4, content.getHeight());
        JButton saveButton = new JButton("Save Enabled Mods");
        saveButton.addActionListener(e -> {
            modHandler.saveEnabledMods(getEnabledModsNames());
            modHandler.createNewModpackFolder();
            modHandler.setModsFolderPath(modsPath);
            modHandler.moveEnabledModsToModpackFolder();
            System.out.println("Enabled mods saved.");
            logger.log("Enabled mods saved.");
            clearMainWindow();
            createHomeScreen();
        });

        JButton saveAndLoadButton = new JButton("Save and Load Enabled Mods");
        saveAndLoadButton.addActionListener(e -> {
            modHandler.saveEnabledMods(getEnabledModsNames());
            modHandler.createNewModpackFolder();
            setModsPath(modsPath);
            setMcModsPath(mcModsPath);
            modHandler.setModsFolderPath(modsPath);
            modHandler.setModsFolderPathMC(mcModsPath);
            modHandler.moveEnabledModsToModpackFolder();
            modHandler.loadEnabledMods();
            System.out.println("Enabled mods saved and loaded into Minecraft mods folder.");
            logger.log("Enabled mods saved and loaded into Minecraft mods folder.");
            clearMainWindow();
            System.out.println("Window cleared and making new home screen");
            logger.log("Window cleared and making new home screen");
            createHomeScreen();
        });
        content.add(new JScrollPane(table));
        content.add(saveButton);
        content.add(saveAndLoadButton);

        mainWindow.revalidate();
        mainWindow.repaint();
    }

    public String[] getEnabledModsNames(){
        String[] enabledModNames = new String[data.length];
        int count = 0;
        for (int i = 0; i < tableStates.length; i++) {
            if (tableStates[i]) {
                enabledModNames[count] = (String) data[i][1];
                count++;
            }
        }
        return Arrays.copyOf(enabledModNames, count); // return only the filled portion
    }

    public void createHomeScreen(){

        JButton createNew = new JButton("Create New");
        createNew.addActionListener(e -> {
            // Action for "Create New" button
            System.out.println("\"Create New\" button pressed");
            logger.log("Create New button pressed");
            clearMainWindow();

            mainWindow.revalidate();
            mainWindow.repaint();
            getModLoader();
            System.out.println("Creating a modpack using " + modLoader);
            logger.log("Creating a modpack using " + modLoader);

            JTextField modpackNameField = new JTextField(20);
            JLabel modpackNameLabel = new JLabel("Enter Modpack Name:");
            JButton submitButton = new JButton("Submit");
            submitButton.addActionListener(ev -> {
                System.out.println("Modpack name submitted: " + modpackNameField.getText());
                logger.log("Modpack name submitted: " + modpackNameField.getText());
                modpackName = modpackNameField.getText();
                setModpackName(modpackName);
                initModHandler();
            });

            Container content = mainWindow.getContentPane();

            content.add(modpackNameLabel);
            content.add(modpackNameField);
            content.add(submitButton);

            mainWindow.revalidate();
            mainWindow.repaint();

            createLabels(mods);
        });


        JButton useExisting = new JButton("Use Existing");
        useExisting.addActionListener(e -> {
            // Action for "Use Existing" button
            System.out.println("\"Use Existing\" button pressed");
            logger.log("Use Existing button pressed");
            getModLoader();
            System.out.println("Loading modpacks that use " + modLoader);
            logger.log("Loading modpacks that use " + modLoader);
            getModLoader();
            clearMainWindow();
            modpackButtonGenerator();
        });


        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(ev -> {
            System.out.println("Quit button pressed. Exiting application.");
            logger.log("Quit button pressed. Exiting application.");
            System.exit(0);
        });

        JButton editModpackButton = new JButton("Edit Modpack");
        editModpackButton.addActionListener(e -> {
            // Action for "Edit Modpack" button
            isEditingModpack = true;
            System.out.println("\"Edit Modpack\" button pressed");
            logger.log("\"Edit Modpack\" button pressed");
            getModLoader();
            System.out.println("Loading modpacks that use " + modLoader + " for editing");
            logger.log("Loading modpacks that use " + modLoader + " for editing");
            clearMainWindow();
            modpackButtonGenerator();
        });

        Container content = mainWindow.getContentPane();
        content.add(createNew);
        content.add(useExisting);
        content.add(editModpackButton);
        content.add(quitButton);

        mainWindow.revalidate();
        mainWindow.repaint();
    }

    private void modpackButtonGenerator() { // Dynamically generate buttons for modpack text files
        File modpacksDir = new File("modpacks/");
        if (modpacksDir.exists() && modpacksDir.isDirectory()) {
            for (File modpackTextFile : modpacksDir.listFiles()) {
                if (modpackTextFile.isFile() && modpackTextFile.getName().endsWith(".txt")) {
                    String modName = modpackTextFile.getName().replaceFirst("[.][^.]+$", ""); // remove the .txt extension
                    modName = modpackTextFile.getName().substring(0, modpackTextFile.getName().length() - 4); // remove the .txt extension
                    modName = modName.replace("_enabled_mods", ""); // remove the _enabled_mods part
                    JButton modpackButton = new JButton(modName);
                    String finalModName = modName;
                    modpackButton.addActionListener(e -> {
                        System.out.println("Button \"" + finalModName + "\" was pressed.");
                        logger.log("Button \"" + finalModName + "\" was pressed.");
                        setModpackName(finalModName);
                        modHandler.setModpackName(modpackName);
                        modHandler.setModsFolderPathMC(mcModsPath);
                        if (!isEditingModpack) {
                            modHandler.loadEnabledMods();
                            System.exit(0);
                        } else {
                            clearMainWindow();
                            createLabels(getFiles(modsPath));
                        }
                    });
                    Container content = mainWindow.getContentPane();
                    content.add(modpackButton);
                }
            }
            mainWindow.revalidate();
            mainWindow.repaint();
        } else {
            System.out.println("The 'modpacks/' directory does not exist or is not a directory.");
            logger.log("The 'modpacks/' directory does not exist or is not a directory.");
        }
    }

    private void clearMainWindow() {
        Container content = mainWindow.getContentPane();
        content.removeAll();
        mainWindow.revalidate();
        mainWindow.repaint();
    }


    private void initModHandler(){
        modHandler.setModsFolderPathMC(mcModsPath);
        modHandler.setModsFolderPath(modsPath);
        modHandler.setModpackName(modpackName);
    }

    public void init(Logger logger){
        this.logger = logger;
        this.modHandler = new ModHandler(logger);
        try {
            createWindow();
            logger.log("Window initialized successfully.");
        } catch (FileNotFoundException e) {
            logger.log("An error occurred while initializing the window.");
            logger.log(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void getModsDirectory() {
        JDialog dialog = new JDialog(mainWindow, "Enter Mods Directory", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(400, 150);

        JTextField modsDirField = new JTextField(20);
        JLabel modsDirLabel = new JLabel("Enter Mods Directory (not Minecraft mods folder):");
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(ev -> {
            String modsDirPath = modsDirField.getText();
            setModsPath(modsDirPath);

            try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter("modsDirectories.txt"))) {
                writer.write(modsDirPath);
                System.out.println("Successfully wrote mods directory to the file.");
                logger.log("Successfully wrote mods directory to the file.");
            } catch (Exception e) {
                System.out.println("An error occurred while writing to the file.");
                logger.log("An error occurred while writing to the file.");
                logger.log(e.getMessage());
                e.printStackTrace();
            }

            dialog.dispose(); // Close the dialog
        });

        dialog.add(modsDirLabel);
        dialog.add(modsDirField);
        dialog.add(submitButton);

        dialog.setVisible(true); // Wait until the dialog is closed
    }

    private void getMinecraftModsDirectory() {
        JDialog dialog = new JDialog(mainWindow, "Enter Minecraft Mods Directory", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(400, 150);

        JTextField mcModsDirField = new JTextField(20);
        JLabel mcModsDirLabel = new JLabel("Enter Minecraft Mods Directory (minecraft/mods):");
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(ev -> {
            String mcModsDirPath = mcModsDirField.getText();
            setMcModsPath(mcModsDirPath);

            try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter("mcModsDirectories.txt"))) {
                writer.write(mcModsDirPath);
                System.out.println("Successfully wrote Minecraft mods directory to the file.");
                logger.log("Successfully wrote Minecraft mods directory to the file.");
            } catch (Exception e) {
                System.out.println("An error occurred while writing to the file.");
                logger.log("An error occurred while writing to the file.");
                logger.log(e.getMessage());
                e.printStackTrace();
            }

            dialog.dispose(); // Close the dialog
        });

        dialog.add(mcModsDirLabel);
        dialog.add(mcModsDirField);
        dialog.add(submitButton);

        dialog.setVisible(true); // Wait until the dialog is closed
    }

    private static void generateFiles() { // makes any required files if they do not exist

        modsDirectory = new File("modsDirectories.txt");
        mcModsDirectory = new File("mcModsDirectories.txt");

        try {
            if (!modsDirectory.exists()) {
                modsDirectory.createNewFile();
                System.out.println("Created file: modsDirectories.txt");
                logger.log("Created file: modsDirectories.txt");
            }

            if (!mcModsDirectory.exists()) {
                mcModsDirectory.createNewFile();
                System.out.println("Created file: mcModsDirectories.txt");
                logger.log("Created file: mcModsDirectories.txt");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating files.");
            logger.log("An error occurred while creating files.");
            logger.log(e.getMessage());
            e.printStackTrace();
        }
    }

    private static File[] getFiles(String dirPath){ // get all files in a given directory
        File workingDir = new File(dirPath);
        File[] files = workingDir.listFiles();

        if (files != null){
            for (File file: files){
                if (!file.isDirectory()) {
                    System.out.println("File found: " + file.getName());
                    logger.log("File found: " + file.getName());
                }
            }
        } else {
            System.out.println("The directory is empty or does not exist.");
            logger.log("The directory is empty or does not exist.");
        }
        System.out.println();
        return files;
    }

    private void getModStates(){
        enabledMods = new boolean[data.length];
        String filename = "modpacks/" + modLoader + modpackName + "_enabled_mods.txt";
        try (Scanner scanner = new Scanner(new File(filename))) {
            int index = 0;
            while (scanner.hasNextLine() && index < data.length) {
                String modName = scanner.nextLine();
                for (int i = 0; i < data.length; i++) {
                    if (data[i][1].equals(modName)) {
                        enabledMods[i] = true;
                        break;
                    }
                }
                index++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the file: " + filename);
            logger.log("An error occurred while reading the file: " + filename);
            e.printStackTrace();
        }
    }

    private void getModLoader(){ // TODO -- MAKE THIS WORK
        JDialog dialog = new JDialog(mainWindow, "Select Mod Loader", true);
        dialog.setLayout(new FlowLayout());
        dialog.setSize(400, 150);

        String[] loaders = {"Forge", "Fabric", "Quilt", "NeoForge"};
        JComboBox<String> loaderComboBox = new JComboBox<>(loaders);
        JLabel loaderLabel = new JLabel("Select Mod Loader:");
        JButton submitButton = new JButton("Submit");

        submitButton.addActionListener(ev -> {
            String selectedLoader = (String) loaderComboBox.getSelectedItem();
            modLoader = selectedLoader;
            System.out.println("Mod loader selected: " + selectedLoader);
            logger.log("Mod loader selected: " + selectedLoader);
            dialog.dispose(); // Close the dialog
            makeNewDirectory("modpacks/" + modLoader + "/");

        });

        dialog.add(loaderLabel);
        dialog.add(loaderComboBox);
        dialog.add(submitButton);
        dialog.setVisible(true); // Wait until the dialog is closed
    }

    public void makeNewDirectory(String dirName){
        File newDir = new File(dirName);
        System.out.println("Creating new directory: " + newDir.getAbsolutePath());
        logger.log("Trying to create loader directory: " + newDir.getAbsolutePath());
        if (!newDir.exists()){
            if (newDir.mkdir()){
                System.out.println("Directory created: " + newDir.getName());
                logger.log("Directory created: " + newDir.getName());
            } else {
                System.out.println("Failed to create directory.");
                logger.log("Failed to create directory: " + newDir.getName());
            }
        } else {
            System.out.println("Directory already exists.");
            logger.log("Directory already exists: " + newDir.getName());
        }
    }

    public void setMods(File[] modFiles){ mods = modFiles; logger.log("Mods set in WindowHandler."); }

    public void setMcModsPath(String modsPath) { mcModsPath = modsPath; logger.log("Minecraft mods path set to: " + modsPath); }

    public void setModpackName(String modpack){ modpackName = modpack; logger.log("Modpack name set to: " + modpack); }

    public void setModsPath(String mods) { modsPath = mods; logger.log("Mods path set to: " + modsPath); }
}