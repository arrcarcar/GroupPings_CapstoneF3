package Swing;

public class NonPerishableFood extends FoodItem {

    private final int daysUntilBestBefore;
    private final boolean packagingIntact;

    public NonPerishableFood(String id, String name, int qty, String donor,
                             int daysUntilBestBefore, boolean packagingIntact) {
        super(id, name, qty, donor);
        this.daysUntilBestBefore = daysUntilBestBefore;
        this.packagingIntact = packagingIntact;
    }

    @Override
    public boolean isSafeForDistribution() {
        return daysUntilBestBefore > 0 && packagingIntact;
    }

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