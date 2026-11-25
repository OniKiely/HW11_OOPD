import java.util.LinkedHashMap;
import java.util.Map;

public class VendingMachine {
    private final Map<String, Snack> snacks = new LinkedHashMap<>();
    private StateOfVendingMachine state = new IdleState();   // starts Idle
    private SnackDispenseHandler dispenser;
    private Snack selectedSnack;
    private double balance;

    private SnackDispenseHandler tail;
    public void addSnack(Snack snack) {
        snacks.put(snack.getName().toLowerCase(), snack);
        SnackDispenseHandler h = new SnackDispenseHandler(snack);
        if (dispenser == null) dispenser = h; else tail.setNext(h);
        tail = h;
    }


    public void selectSnack(String name)  { state.selectSnack(this, name); }
    public void insertMoney(double amount){ state.insertMoney(this, amount); }
    public void dispenseSnack()           { state.dispenseSnack(this); }
    public void cancel()                  { state.cancel(this); }


    public Snack findSnack(String name)   { return snacks.get(name.toLowerCase()); }
    public void setState(StateOfVendingMachine s) { this.state = s; }
    public StateOfVendingMachine getState() { return state; }
    public SnackDispenseHandler getDispenser() { return dispenser; }
    public Snack getSelectedSnack() { return selectedSnack; }
    public void setSelectedSnack(Snack s) { selectedSnack = s; }
    public double getBalance() { return balance; }
    public void addBalance(double a) { balance += a; }
    public void reset() { balance = 0; selectedSnack = null; }

    public void printInventory() {
        System.out.println("Inventory:");
        snacks.values().forEach(s -> System.out.println("  " + s));
    }
}
