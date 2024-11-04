import java.io.*;
import java.util.Map;
import javax.swing.*;
import java.awt.*;

public class Project2 {
    public static void main(String[] args) {
        // Create a frame to serve as the parent for the file chooser
        JFrame frame = new JFrame();
        frame.setSize(100, 100);   // Size doesn't matter much as it won't be visible
        frame.setLocationRelativeTo(null);  // Center the invisible frame
        
        // Create and configure the file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Mini Language File");
        
        // Show the open file dialog with the frame as parent
        int returnValue = fileChooser.showOpenDialog(frame);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String filePath = selectedFile.getAbsolutePath();

            System.out.println("Trying to access: " + filePath);

            try {
                FileReader fileReader = new FileReader(filePath);
                Lexer lexer = new Lexer(fileReader);
                Parser parser = new Parser(lexer);

                Map<String, Object> astRoot = parser.parse();
                parser.printAST(astRoot);

                System.out.println("Finished Syntax analysis on " + filePath);

            } catch (FileNotFoundException e) {
                System.out.println("Error: file not found");
            } catch (IOException e) {
                System.out.println("Error reading file");
            } catch (LexicalException e) {
                System.out.println(e.getMessage());
            } catch (ParseException e) {
                System.out.println(e.getMessage());
            }
        }
        
        // Make sure to exit the program after we're done
        System.exit(0);
    }
}