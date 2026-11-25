public class IdleState implements StateOfVendingMachine {
    @Override
    public void selectSnack(VendingMachine m, String snackName) {
        Snack snack = m.findSnack(snackName);
        if (snack == null) {
            System.out.println("  [Idle] Unknown snack: " + snackName);
            return;
        }
        m.setSelectedSnack(snack);
        System.out.printf("  [Idle] Selected %s ($%.2f). Please insert money.%n", snack.getName(), snack.getPrice());
        m.setState(new WaitingForMoneyState());
    }

    @Override public void insertMoney(VendingMachine m, double amount) {
        System.out.printf("  [Idle] Select a snack first. Returning $%.2f.%n", amount);
    }
    @Override public void dispenseSnack(VendingMachine m) {
        System.out.println("  [Idle] Nothing to dispense. Select a snack first.");
    }
    @Override public void cancel(VendingMachine m) {
        System.out.println("  [Idle] Nothing to cancel.");
    }
}
