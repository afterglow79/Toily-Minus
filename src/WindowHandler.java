import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class WindowHandler{
    private static JFrame mainWindow = new JFrame("Afterglow's Mod Manager");

    public void createWindow() {
        mainWindow.setSize(1280, 720);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.getContentPane().setLayout(new FlowLayout());

        mainWindow.setVisible(true);
    }

    public static void createLabels(File[] files) {
        Container content = mainWindow.getContentPane();

        String[] columnNames = {"Is Enabled", "Mod Name"};
        Object[][] data = new Object[files.length][2];

        for (int i = 0; i < files.length; i++) {
            data[i][0] = Boolean.FALSE; // Default to disabled
        }

        for (int i = 0; i < files.length; i++) {
            if (!files[i].isDirectory()) {
                data[i][1] = files[i].getName();
            }
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames); // https://stackoverflow.com/questions/7391877/how-to-add-checkboxes-to-jtable-swing
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
        };
        table.setSize(content.getWidth()/ 4, content.getHeight());
        content.add(new JScrollPane(table));
    }
}