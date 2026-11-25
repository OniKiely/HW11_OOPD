public class SnackDispenseHandler {
    private final Snack snack;
    private SnackDispenseHandler next;

    public SnackDispenseHandler(Snack snack) { this.snack = snack; }

    public SnackDispenseHandler setNext(SnackDispenseHandler next) {
        this.next = next;
        return next;
    }

    public boolean handle(String snackName, double money) {
        if (snack.getName().equalsIgnoreCase(snackName)) {
            if (snack.inStock() && money >= snack.getPrice()) {
                snack.decrementQuantity();
                return true;
            }
            return false;
        }
        return next != null && next.handle(snackName, money);
    }
}
