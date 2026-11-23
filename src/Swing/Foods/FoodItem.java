package Swing.Foods;

public abstract class FoodItem {

    public static final String AVAILABLE = "AVAILABLE";
    public static final String CLAIMED = "CLAIMED";
    public static final String ON_ROUTE = "ON ROUTE";
    public static final String DELIVERED = "DELIVERED";

    private final String batchId;
    private final String name;
    private final String donorName;
    private int quantity;
    private String status = AVAILABLE;
    private String claimedBy = "-";
    private String volunteer = "-";

    protected FoodItem(String batchId, String name, int quantity, String donorName) {
        this.batchId = batchId;
        this.name = name;
        this.quantity = quantity;
        this.donorName = donorName;
    }
    //comment
    public abstract boolean isSafeForDistribution();

    public abstract int getHoursLeft();

    public abstract String getTypeName();

    protected abstract String extraCsv();


    public abstract FoodItem copyWith(String newId, int newQuantity);

    public String toCsv() {
        return String.join(",", getTypeName(), batchId, name, String.valueOf(quantity),
                donorName, extraCsv(), status, claimedBy, volunteer);
    }

    public String getBatchId() {
        return batchId;
    }

    public String getName() {
        return name;
    }

    public String getDonorName() {
        return donorName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(String claimedBy) {
        this.claimedBy = claimedBy;
    }

    public String getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(String volunteer) {
        this.volunteer = volunteer;
    }
}