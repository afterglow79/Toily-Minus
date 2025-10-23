import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class WindowHandler{
    public static JFrame mainWindow = new JFrame("Toily Minus");
    private static DefaultTableModel model; // Class-level variable for the model
    public static Boolean[] tableStates;
    private static Object[][] data;

    public void createWindow() {
        mainWindow.setSize(1280, 720);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.getContentPane().setLayout(new FlowLayout());

        mainWindow.setVisible(true);
    }

    public static void createLabels(File[] files) {
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
            }

            for (int i = 0; i < tableStates.length; i++) {
                if (tableStates[i] == true) {
                    System.out.println(data[i][1] + "   is enabled");
                }
            }

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
        content.add(new JScrollPane(table));
    }

    public Boolean[] getEnabledModsIndexes(){
        int count = 0;
        Boolean[] enabledModIndexes = new Boolean[tableStates.length];
        for (int i = 0; i < tableStates.length; i++) {
            if (tableStates[i]) {
                enabledModIndexes[count] = true;
                count++;
        }}
        return Arrays.copyOf(enabledModIndexes, count);
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


}