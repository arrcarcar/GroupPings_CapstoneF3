package Swing.Foods;

import Swing.Foods.FoodItem;

public class NonPerishableFood extends FoodItem {

    private final int daysUntilBestBefore;
    private final boolean packagingIntact;

    public NonPerishableFood(String id, String name, int qty, String donor,
                             int daysUntilBestBefore, boolean packagingIntact) {
        super(id, name, qty, donor);
        this.daysUntilBestBefore = daysUntilBestBefore;
        this.packagingIntact = packagingIntact;
    }

    //the ff is NOT PERMANENT, for testing purposes only
    public NonPerishableFood(){
        this("#1002", "Generic", 5, "John", 24, true);
    }

    @Override
    public boolean isSafeForDistribution() {
        return daysUntilBestBefore > 0 && packagingIntact;
    }
    //comment
    @Override
    public int getHoursLeft() {
        return daysUntilBestBefore * 24;
    }

    @Override
    public String getTypeName() {
        return "Non-perishable";
    }

    @Override
    protected String extraCsv() {
        return daysUntilBestBefore + "," + packagingIntact;
    }


    @Override
    public FoodItem copyWith(String newId, int newQuantity) {
        return new NonPerishableFood(newId, getName(), newQuantity, getDonorName(),
                daysUntilBestBefore, packagingIntact);
    }
}