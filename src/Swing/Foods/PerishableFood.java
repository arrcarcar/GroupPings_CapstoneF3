package Swing.Foods;

import Swing.Foods.FoodItem;

public class PerishableFood extends FoodItem {

    private final int hoursUntilExpiration;
    private final boolean requiresRefrigeration;

    public PerishableFood(String id, String name, int qty, String donor,
                          int hoursUntilExpiration, boolean requiresRefrigeration) {
        super(id, name, qty, donor);
        this.hoursUntilExpiration = hoursUntilExpiration;
        this.requiresRefrigeration = requiresRefrigeration;
    }
    //the ff is NOT PERMANENT, for testing purposes only
    public PerishableFood(){
        this("#1002", "Generic", 5, "John", 24, true);
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
