package Swing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private static final Path FILE_PATH = Paths.get("inventory_data.txt");
    private static final int FIELD_COUNT = 10;

    private FileManager() {
    }

    public static boolean hasSavedData() {
        return Files.exists(FILE_PATH);
    }

    public static void saveInventory(List<FoodItem> items) {
        List<String> lines = new ArrayList<>();
        for (FoodItem item : items) {
            lines.add(item.toCsv());
        }
        try {
            Files.write(FILE_PATH, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InventoryStorageException(
                    "Could not save your changes to " + FILE_PATH.toAbsolutePath()
                            + ".\nMake sure the file is not open in another program.", e);
        }
    }

    public static List<FoodItem> loadInventory() {
        List<FoodItem> items = new ArrayList<>();
        if (!hasSavedData()) {
            return items;
        }
        try {
            for (String line : Files.readAllLines(FILE_PATH, StandardCharsets.UTF_8)) {
                FoodItem item = parse(line);
                if (item != null) {
                    items.add(item);
                }
            }
        } catch (IOException e) {
            throw new InventoryStorageException(
                    "Could not read the saved inventory at " + FILE_PATH.toAbsolutePath() + ".", e);
        }
        return items;
    }

    private static FoodItem parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] fields = line.split(",");
        if (fields.length < FIELD_COUNT) {
            return null;
        }
        try {
            int quantity = Integer.parseInt(fields[3]);
            int expiry = Integer.parseInt(fields[5]);
            boolean flag = Boolean.parseBoolean(fields[6]);
            FoodItem item = fields[0].equals("Perishable")
                    ? new PerishableFood(fields[1], fields[2], quantity, fields[4], expiry, flag)
                    : new NonPerishableFood(fields[1], fields[2], quantity, fields[4], expiry, flag);
            item.setStatus(fields[7]);
            item.setClaimedBy(fields[8]);
            item.setVolunteer(fields[9]);
            return item;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
