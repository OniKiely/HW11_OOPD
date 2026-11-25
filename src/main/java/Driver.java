public class Driver {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();
        // Chain order: Coke -> Pepsi -> Cheetos -> Doritos -> KitKat -> Snickers
        vm.addSnack(new Snack("Coke",    1.50, 5));
        vm.addSnack(new Snack("Pepsi",   1.50, 5));
        vm.addSnack(new Snack("Cheetos", 2.00, 3));
        vm.addSnack(new Snack("Doritos", 2.00, 3));
        vm.addSnack(new Snack("KitKat",  1.75, 4));
        vm.addSnack(new Snack("Snickers",1.75, 2));   // will hit 0
        vm.printInventory();

        System.out.println("\n1) Exact money for Coke");
        vm.selectSnack("Coke"); vm.insertMoney(1.50);

        System.out.println("\n2) Pepsi, paying in two inserts");
        vm.selectSnack("Pepsi"); vm.insertMoney(1.00); vm.insertMoney(1.00);

        System.out.println("\n3) Doritos with overpayment (change)");
        vm.selectSnack("Doritos"); vm.insertMoney(5.00);

        System.out.println("\n4) Cheetos, then cancel");
        vm.selectSnack("Cheetos"); vm.insertMoney(0.50); vm.cancel();

        System.out.println("\n5) Buy Snickers until quantity hits 0");
        vm.selectSnack("Snickers"); vm.insertMoney(1.75);
        vm.selectSnack("Snickers"); vm.insertMoney(1.75);
        vm.printInventory();

        System.out.println("\n6) Try Snickers again (sold out -> refund)");
        vm.selectSnack("Snickers"); vm.insertMoney(2.00);

        System.out.println("\n7) Invalid actions in Idle / unknown snack");
        vm.insertMoney(1.00); vm.selectSnack("Twix");

        System.out.println();
        vm.printInventory();
    }
}
