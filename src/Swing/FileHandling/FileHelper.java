package Swing.FileHandling;

import java.io.*;
import java.util.ArrayList;

public class FileHelper {
    public static void removeFromFile(String lineToRemove, String filepath) throws IOException {
        ArrayList<String> keptLines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(lineToRemove)){ continue; }
                keptLines.add(line);
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filepath))) {
            for (String keptLine : keptLines) {
                bw.write(keptLine);
                bw.newLine();
            }
        }
    }
}
