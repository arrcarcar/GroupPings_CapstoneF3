package Swing;

public class PerishableFood extends FoodItem {

    private final int hoursUntilExpiration;
    private final boolean requiresRefrigeration;

    public PerishableFood(String id, String name, int qty, String donor,
                          int hoursUntilExpiration, boolean requiresRefrigeration) {
        super(id, name, qty, donor);
        this.hoursUntilExpiration = hoursUntilExpiration;
        this.requiresRefrigeration = requiresRefrigeration;
    }

    @Override
    public boolean isSafeForDistribution() {
        return hoursUntilExpiration > 2;
    }

    @Override
    public int getHoursLeft() {
        return hoursUntilExpiration;
    }

    @Override
    public String getTypeName() {
        return "Perishable";
    }

    @Override
    protected String extraCsv() {
        return hoursUntilExpiration + "," + requiresRefrigeration;
    }
}
