package securitymanagement;

/** Safe demonstration: both workers acquire locks in the same order. */
public class DeadlockDemo {
    private static final Object LOCK_A = new Object();
    private static final Object LOCK_B = new Object();

    private static void task(String name) {
        synchronized (LOCK_A) {
            System.out.println(name + " acquired Lock A");
            synchronized (LOCK_B) {
                System.out.println(name + " acquired Lock B; task completed.");
            }
        }
    }

    public static void runDemo() throws InterruptedException {
        Thread one = new Thread(() -> task("Worker 1"));
        Thread two = new Thread(() -> task("Worker 2"));
        one.start();
        two.start();
        one.join();
        two.join();
        System.out.println("Deadlock avoided: all workers use the same lock order.");
    }
}
