
public class OrderItem {

    private final String ordItemID;
    private String ordItemName;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    // for saving
    public OrderItem(Item items) {
        this.ordItemID = items.getItemId();
        this.ordItemName = items.getItemName();
        this.quantity = 0;
        this.unitPrice = 0.0;
        this.totalPrice = 0.0;
    }

    // for reading
    public OrderItem(String ordItemID, int quantity, double unitPrice) {
        this.ordItemID = ordItemID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = getTotalPrice(unitPrice, quantity);
    }

    public String getOrdItemID() {
        return ordItemID;
    }

    public String getOrdItemName() {
        return ordItemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getTotalPrice(double unitPrice, int quantity) {
        return unitPrice * quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity += quantity;
        this.totalPrice = getTotalPrice(this.unitPrice, this.quantity);
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.totalPrice = getTotalPrice(this.unitPrice, this.quantity);
    }

    public void reduceQuantity(int quantity) {
        if ((this.quantity - quantity) < 0) {
            this.quantity = 0;
        } else {
            this.quantity -= quantity;
        }
        this.totalPrice = getTotalPrice(this.unitPrice, this.quantity);
    }
}
