import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private String fileName;

    public Logger(){
        init();
    }


    private void init(){
        generateDirectory();
        generateLogFile();
    }

    private void generateLogFile(){
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.toString().replace(":", "-");
        String logFileName = "logs/log_" + timestamp + ".txt";
        File logFile = new File(logFileName);
        try {
            if (logFile.createNewFile()) {
                System.out.println("Log file created: " + logFileName);
            } else {
                System.out.println("Log file already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the log file.");
            e.printStackTrace();
        }
        setFileName(logFileName);
    }

    private void setFileName(String fileName){
        this.fileName = fileName;
    }

    private void generateDirectory(){
        File logDir = new File("logs");
        if (!logDir.exists()) {
            if (logDir.mkdir()) {
                System.out.println("Logs directory created: " + logDir.getName());
            } else {
                System.out.println("Failed to create logs directory.");
            }
        } else {
            System.out.println("Logs directory already exists.");
        }
    }

    public void log(String message){
        // Append the message to the log file
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName, true))) {

            LocalDateTime now = LocalDateTime.now(); // https://stackoverflow.com/questions/23068676/how-to-get-current-timestamp-in-string-format-in-java-yyyy-mm-dd-hh-mm-ss
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedNow = now.format(formatter);

            writer.write(formattedNow +":    " + message);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the log file.");
            e.printStackTrace();
        }
    }

}
