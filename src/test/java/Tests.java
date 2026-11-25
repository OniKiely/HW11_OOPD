import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** One JUnit test per method of the vending machine classes. */
public class Tests {
    private static final double DELTA = 1e-9;

    private VendingMachine vm;
    private PrintStream originalOut;
    private ByteArrayOutputStream console;

    @Before
    public void setUp() {
        vm = new VendingMachine();
        vm.addSnack(new Snack("Coke", 1.50, 2));
        vm.addSnack(new Snack("Snickers", 1.75, 1));
        originalOut = System.out;
        console = new ByteArrayOutputStream();
        System.setOut(new PrintStream(console));   // capture printed messages
    }

    @After
    public void tearDown() { System.setOut(originalOut); }

    private String output() { return console.toString(); }


    @Test public void snack_getName()  { assertEquals("Coke", new Snack("Coke", 1.5, 2).getName()); }
    @Test public void snack_getPrice() { assertEquals(1.5, new Snack("Coke", 1.5, 2).getPrice(), DELTA); }
    @Test public void snack_getQuantity() { assertEquals(2, new Snack("Coke", 1.5, 2).getQuantity()); }
    @Test public void snack_inStock() {
        assertTrue(new Snack("A", 1, 1).inStock());
        assertFalse(new Snack("A", 1, 0).inStock());
    }
    @Test public void snack_decrementQuantity() {
        Snack s = new Snack("A", 1, 1);
        s.decrementQuantity();
        s.decrementQuantity();
        assertEquals(0, s.getQuantity());
    }
    @Test public void snack_toString() {
        assertEquals("Coke ($1.50, qty 2)", new Snack("Coke", 1.5, 2).toString());
    }


    @Test public void handler_setNext() {
        SnackDispenseHandler a = new SnackDispenseHandler(new Snack("A", 1, 1));
        SnackDispenseHandler b = new SnackDispenseHandler(new Snack("B", 1, 1));
        assertSame(b, a.setNext(b));
    }
    @Test public void handler_handle() {
        Snack coke = new Snack("Coke", 1.5, 1), pepsi = new Snack("Pepsi", 1.5, 1);
        SnackDispenseHandler h1 = new SnackDispenseHandler(coke);
        h1.setNext(new SnackDispenseHandler(pepsi));
        assertTrue(h1.handle("Pepsi", 2.0));        // passed down the chain
        assertEquals(0, pepsi.getQuantity());
        assertEquals(1, coke.getQuantity());
        assertFalse(h1.handle("Pepsi", 2.0));       // now sold out
    }


    @Test public void idle_selectSnack() {
        new IdleState().selectSnack(vm, "Coke");
        assertTrue(vm.getState() instanceof WaitingForMoneyState);
        assertEquals("Coke", vm.getSelectedSnack().getName());
    }
    @Test public void idle_insertMoney() {
        new IdleState().insertMoney(vm, 1.0);
        assertTrue(output().contains("Select a snack first"));
    }
    @Test public void idle_dispenseSnack() {
        new IdleState().dispenseSnack(vm);
        assertTrue(output().contains("Nothing to dispense"));
    }
    @Test public void idle_cancel() {
        new IdleState().cancel(vm);
        assertTrue(output().contains("Nothing to cancel"));
    }

    @Test public void waiting_selectSnack() {
        vm.selectSnack("Coke");
        new WaitingForMoneyState().selectSnack(vm, "Snickers");
        assertTrue(output().contains("Already selected"));
        assertEquals("Coke", vm.getSelectedSnack().getName());
    }
    @Test public void waiting_insertMoney() {
        vm.selectSnack("Coke");
        new WaitingForMoneyState().insertMoney(vm, 1.50);
        assertTrue(vm.getState() instanceof IdleState);
        assertEquals(1, vm.findSnack("Coke").getQuantity());
    }
    @Test public void waiting_dispenseSnack() {
        vm.selectSnack("Coke");
        new WaitingForMoneyState().dispenseSnack(vm);
        assertTrue(output().contains("Not enough money"));
        assertEquals(2, vm.findSnack("Coke").getQuantity());
    }
    @Test public void waiting_cancel() {
        vm.selectSnack("Coke");
        vm.insertMoney(0.50);
        new WaitingForMoneyState().cancel(vm);
        assertTrue(vm.getState() instanceof IdleState);
        assertEquals(0, vm.getBalance(), DELTA);
    }

    @Test public void dispensing_dispenseSnack() {
        vm.setSelectedSnack(vm.findSnack("Coke"));
        vm.addBalance(2.00);
        new DispensingSnackState().dispenseSnack(vm);
        assertTrue(output().contains("Change returned: $0.50"));
        assertEquals(1, vm.findSnack("Coke").getQuantity());
        assertTrue(vm.getState() instanceof IdleState);
    }
    @Test public void dispensing_selectSnack() {
        new DispensingSnackState().selectSnack(vm, "Coke");
        assertTrue(output().contains("Please wait"));
    }
    @Test public void dispensing_insertMoney() {
        new DispensingSnackState().insertMoney(vm, 1.0);
        assertTrue(output().contains("Please wait"));
    }
    @Test public void dispensing_cancel() {
        new DispensingSnackState().cancel(vm);
        assertTrue(output().contains("Too late"));
    }

    @Test public void machine_addSnack() {
        assertNotNull(vm.findSnack("Snickers"));
        assertNotNull(vm.getDispenser());
    }
    @Test public void machine_findSnack() {
        assertNotNull(vm.findSnack("coke"));         // case-insensitive
        assertNull(vm.findSnack("Twix"));
    }
    @Test public void machine_selectSnack() {
        vm.selectSnack("Coke");
        assertTrue(vm.getState() instanceof WaitingForMoneyState);
    }
    @Test public void machine_insertMoney() {
        vm.selectSnack("Coke");
        vm.insertMoney(0.25);
        assertEquals(0.25, vm.getBalance(), DELTA);
    }
    @Test public void machine_dispenseSnack() {
        vm.setSelectedSnack(vm.findSnack("Snickers"));
        vm.addBalance(1.75);
        vm.setState(new DispensingSnackState());
        vm.dispenseSnack();
        assertEquals(0, vm.findSnack("Snickers").getQuantity());
    }
    @Test public void machine_cancel() {
        vm.selectSnack("Coke");
        vm.cancel();
        assertTrue(vm.getState() instanceof IdleState);
    }
    @Test public void machine_setState() {
        StateOfVendingMachine s = new DispensingSnackState();
        vm.setState(s);
        assertSame(s, vm.getState());
    }
    @Test public void machine_getState() { assertTrue(vm.getState() instanceof IdleState); }  // starts Idle
    @Test public void machine_getDispenser() { assertNotNull(vm.getDispenser()); }
    @Test public void machine_setSelectedSnack() {
        Snack s = vm.findSnack("Coke");
        vm.setSelectedSnack(s);
        assertSame(s, vm.getSelectedSnack());
    }
    @Test public void machine_getSelectedSnack() { assertNull(vm.getSelectedSnack()); }
    @Test public void machine_addBalance() {
        vm.addBalance(1.0);
        vm.addBalance(0.5);
        assertEquals(1.5, vm.getBalance(), DELTA);
    }
    @Test public void machine_getBalance() { assertEquals(0.0, vm.getBalance(), DELTA); }
    @Test public void machine_reset() {
        vm.setSelectedSnack(vm.findSnack("Coke"));
        vm.addBalance(1.0);
        vm.reset();
        assertEquals(0.0, vm.getBalance(), DELTA);
        assertNull(vm.getSelectedSnack());
    }
    @Test public void machine_printInventory() {
        vm.printInventory();
        assertTrue(output().contains("Coke ($1.50, qty 2)"));
        assertTrue(output().contains("Snickers"));
    }
}
