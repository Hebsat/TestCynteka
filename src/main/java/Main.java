import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        String inputPath = "d://input.txt";
        String outputPath = "d://output.txt";

        FileHandler fileHandler = new FileHandler(inputPath, outputPath);
        try {
            fileHandler.handleFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
