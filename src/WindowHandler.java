import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class WindowHandler{ // TODO -- MAKE BUTTONS FOR EACH MOD (DYNAMICALLY LOADED)
    public static JFrame mainWindow = new JFrame("Toily Minus");
    private static DefaultTableModel model;
    public static Boolean[] tableStates;
    private static Object[][] data;
    private static File[] mods;
    private static String mcModsPath;
    private static String modsPath;
    private static String modpackName;
    public static ModHandler modHandler = new ModHandler();

    public void createWindow() {
        mainWindow.setSize(1280, 720);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.getContentPane().setLayout(new FlowLayout());
        createHomeScreen();
        mainWindow.setVisible(true);
    }

    public void createLabels(File[] files) {
        Container content = mainWindow.getContentPane();

        String[] columnNames = {"Is Enabled", "Mod Name"};
        data = new Object[files.length][2];
        tableStates = new Boolean[files.length];
        for (int i = 0; i < files.length; i++) {
            data[i][0] = Boolean.FALSE; // Default to disabled
            tableStates[i] = false;
        }

        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory()) {
                data[i][1] = files[i].getName();
            }
        }

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
            }

            for (int i = 0; i < tableStates.length; i++) {
                if (tableStates[i] == true) {
                    System.out.println(data[i][1] + "   is enabled");
                }
            }
            System.out.println();

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
            System.out.println("Enabled mods saved.");
        });

        JButton saveAndLoadButton = new JButton("Save and Load Enabled Mods");
        saveAndLoadButton.addActionListener(e -> {
            modHandler.saveEnabledMods(getEnabledModsNames());
            modHandler.moveEnabledModsToModpackFolder();
            modHandler.loadEnabledMods();
            System.out.println("Enabled mods saved and loaded into Minecraft mods folder.");
        });

        content.add(new JScrollPane(table));
        content.add(saveButton);
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
            clearMainWindow();
            System.out.println("Input modpack name:");
            String modpackName = new Scanner(System.in).next();
            setModpackName(modpackName);
            initModHandler();
            createLabels(mods);
        });

        JButton useExisting = new JButton("Use Existing");
        useExisting.addActionListener(e -> {
            // Action for "Use Existing" button
            System.out.println("\"Use Existing\" button pressed");
            clearMainWindow();
            modpackButtonGenerator();
        });

        Container content = mainWindow.getContentPane();
        content.add(createNew);
        content.add(useExisting);
    }

    public void modpackButtonGenerator() { // Dynamically generate buttons for modpack text files
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
                        setModpackName(modpackName);
                        modHandler.setModpackName(modpackName);
                        modHandler.loadEnabledMods();
                    });
                    Container content = mainWindow.getContentPane();
                    content.add(modpackButton);
                }
            }
            mainWindow.revalidate();
            mainWindow.repaint();
        } else {
            System.out.println("The 'modpacks/' directory does not exist or is not a directory.");
        }
    }

    public void clearMainWindow() {
        Container content = mainWindow.getContentPane();
        content.removeAll();
        mainWindow.revalidate();
        mainWindow.repaint();
    }

    public void setMods(File[] modFiles){
        mods = modFiles;
    }


    public void setMcModsPath(String modsPath) { mcModsPath = modsPath; }

    public void setModpackName(String modpack){ modpackName = modpack; }

    public void setModsPath(String mods) { modsPath = mods; }

    private void initModHandler(){

        modHandler.setModsFolderPathMC(mcModsPath); // hard
        modHandler.setModsFolderPath(modsPath);
        modHandler.setModpackName(modpackName);
    }

    public void init(String modsPath, String mcModsPath, String modpackTextFolder, File[] modFiles){
        setModsPath(modsPath);
        setMcModsPath(mcModsPath);
        setMods(modFiles);
        createWindow();
    }
}