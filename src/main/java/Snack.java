public class Snack {
    private final String name;
    private final double price;
    private int quantity;

    public Snack(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName()  { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public boolean inStock() { return quantity > 0; }
    public void decrementQuantity() { if (quantity > 0) quantity--; }

    @Override
    public String toString() {
        return String.format("%s ($%.2f, qty %d)", name, price, quantity);
    }
}
