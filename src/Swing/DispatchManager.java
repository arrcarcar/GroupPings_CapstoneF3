package Swing;

import java.util.ArrayList;
import java.util.List;

public final class DispatchManager {

    private final List<FoodItem> inventory;
    private DispatchStrategy currentStrategy = new UrgencyFirstStrategy();
    private int nextId;

    public DispatchManager() {
        inventory = FileManager.loadInventory();
        nextId = computeNextId();
    }

    public void addFoodItem(boolean perishable, String name, String quantityText,
                            String expiryText, boolean flag, String donor)
            throws InvalidFoodInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidFoodInputException("Food name cannot be left blank!");
        }
        if (name.contains(",")) {
            throw new InvalidFoodInputException("Food name cannot contain commas!");
        }
        int quantity;
        int expiry;
        try {
            quantity = Integer.parseInt(quantityText.trim());
            expiry = Integer.parseInt(expiryText.trim());
        } catch (NumberFormatException e) {
            throw new InvalidFoodInputException("Quantity and expiry must be whole numbers!");
        }
        if (quantity <= 0) {
            throw new InvalidFoodInputException("Quantity must be greater than zero!");
        }
        if (expiry <= 0) {
            throw new InvalidFoodInputException("Expiration value must be greater than zero!");
        }
        String id = String.format("B%03d", nextId++);
        inventory.add(perishable
                ? new PerishableFood(id, name.trim(), quantity, donor, expiry, flag)
                : new NonPerishableFood(id, name.trim(), quantity, donor, expiry, flag));
        save();
    }

    public void setDispatchStrategy(DispatchStrategy strategy) {
        this.currentStrategy = strategy;
    }


    public List<FoodItem> processMatching() {
        return getAvailable(currentStrategy);
    }

    public List<FoodItem> getAvailable(DispatchStrategy strategy) {
        List<FoodItem> available = new ArrayList<>();
        for (FoodItem item : inventory) {
            if (FoodItem.AVAILABLE.equals(item.getStatus()) && item.isSafeForDistribution()) {
                available.add(item);
            }
        }
        return strategy.sortAndMatch(available);
    }

    public void claim(String id, String recipient) {
        FoodItem item = find(id);
        if (item != null && FoodItem.AVAILABLE.equals(item.getStatus())) {
            item.setStatus(FoodItem.CLAIMED);
            item.setClaimedBy(recipient);
            save();
        }
    }


    public void claim(String id, String recipient, int amount) throws InvalidFoodInputException {
        FoodItem item = find(id);
        if (item == null || !FoodItem.AVAILABLE.equals(item.getStatus())) {
            throw new InvalidFoodInputException("That batch is no longer available.");
        }

        if (amount <= 0 || amount > item.getQuantity()) {
            throw new InvalidFoodInputException("You can only take 1 to " + item.getQuantity()
                    + " of " + item.getName() + ".");
        }

        if (amount == item.getQuantity()) {
            item.setStatus(FoodItem.CLAIMED);
            item.setClaimedBy(recipient);
        } else {
            item.setQuantity(item.getQuantity() - amount);
            String newId = String.format("B%03d", nextId++);
            FoodItem taken = item.copyWith(newId, amount);
            taken.setStatus(FoodItem.CLAIMED);
            taken.setClaimedBy(recipient);
            inventory.add(taken);
        }
        save();
    }

    public List<FoodItem> getActiveDeliveries() {
        List<FoodItem> active = new ArrayList<>();
        for (FoodItem item : inventory) {
            if (FoodItem.CLAIMED.equals(item.getStatus())
                    || FoodItem.ON_ROUTE.equals(item.getStatus())) {
                active.add(item);
            }
        }
        return active;
    }

    public void assignVolunteer(String id, String volunteer) throws InvalidFoodInputException {
        if (volunteer == null || volunteer.trim().isEmpty()) {
            throw new InvalidFoodInputException("Volunteer name cannot be blank!");
        }
        if (volunteer.contains(",")) {
            throw new InvalidFoodInputException("Volunteer name cannot contain commas!");
        }
        FoodItem item = find(id);
        if (item != null && FoodItem.CLAIMED.equals(item.getStatus())) {
            item.setVolunteer(volunteer.trim());
            item.setStatus(FoodItem.ON_ROUTE);
            save();
        }
    }

    public void markDelivered(String id) {
        FoodItem item = find(id);
        if (item != null && FoodItem.ON_ROUTE.equals(item.getStatus())) {
            item.setStatus(FoodItem.DELIVERED);
            save();
        }
    }

    public void save() {
        FileManager.saveInventory(inventory);
    }

    private FoodItem find(String id) {
        for (FoodItem item : inventory) {
            if (item.getBatchId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    private int computeNextId() {
        int max = 0;
        for (FoodItem item : inventory) {
            max = Math.max(max, numericPart(item.getBatchId()));
        }
        return max + 1;
    }

    private static int numericPart(String batchId) {
        try {
            return Integer.parseInt(batchId.substring(1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return 0;
        }
    }
}