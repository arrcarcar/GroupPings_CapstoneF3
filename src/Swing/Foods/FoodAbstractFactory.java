package Swing.Foods;

public class FoodAbstractFactory {
    public static FoodItem instantiate(String batchID, String name, int qty, String type, int hrs, String donor){
        type = type.toLowerCase();
        switch (type) {
            case "perishable":
                return new PerishableFood();
            case "nonperishable":
                return new NonPerishableFood();
            default:
                return null;
        }
    }
    public static FoodItem instantiate(String[] arr){
        return instantiate(arr[0], arr[1], Integer.parseInt(arr[2]), arr[3], Integer.parseInt(arr[4]), arr[5]);
    }
}
