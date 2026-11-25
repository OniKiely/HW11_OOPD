public class WaitingForMoneyState implements StateOfVendingMachine {
    @Override
    public void selectSnack(VendingMachine m, String snackName) {
        System.out.println("  [WaitingForMoney] Already selected " + m.getSelectedSnack().getName()
                + ". Insert money or cancel.");
    }

    @Override
    public void insertMoney(VendingMachine m, double amount) {
        m.addBalance(amount);
        Snack s = m.getSelectedSnack();
        System.out.printf("  [WaitingForMoney] Inserted $%.2f (total $%.2f).%n", amount, m.getBalance());
        if (m.getBalance() >= s.getPrice()) {
            m.setState(new DispensingSnackState());
            m.dispenseSnack();               // delegate to the new current state
        } else {
            System.out.printf("  [WaitingForMoney] Need $%.2f more.%n", s.getPrice() - m.getBalance());
        }
    }

    @Override public void dispenseSnack(VendingMachine m) {
        System.out.println("  [WaitingForMoney] Not enough money yet.");
    }

    @Override
    public void cancel(VendingMachine m) {
        System.out.printf("  [WaitingForMoney] Cancelled. Refunding $%.2f.%n", m.getBalance());
        m.reset();
        m.setState(new IdleState());
    }
}
