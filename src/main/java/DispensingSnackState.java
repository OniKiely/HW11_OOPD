public class DispensingSnackState implements StateOfVendingMachine {
    @Override
    public void dispenseSnack(VendingMachine m) {
        Snack wanted = m.getSelectedSnack();
        double balance = m.getBalance();
        boolean dispensed = m.getDispenser().handle(wanted.getName(), balance);

        if (dispensed) {
            double change = balance - wanted.getPrice();
            System.out.printf("  [Dispensing] Enjoy your %s!%s%n", wanted.getName(),
                    change > 0.0001 ? String.format(" Change returned: $%.2f", change) : "");
        } else {
            System.out.printf("  [Dispensing] %s is sold out. Refunding $%.2f.%n", wanted.getName(), balance);
        }
        m.reset();
        m.setState(new IdleState());         // back to Idle either way
    }

    @Override public void selectSnack(VendingMachine m, String n) { System.out.println("  [Dispensing] Please wait."); }
    @Override public void insertMoney(VendingMachine m, double a) { System.out.println("  [Dispensing] Please wait."); }
    @Override public void cancel(VendingMachine m)               { System.out.println("  [Dispensing] Too late to cancel."); }
}
