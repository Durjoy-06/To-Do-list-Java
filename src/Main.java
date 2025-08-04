public class Main {
    public static void main(String[] args) {
        Task task = new Task("Buy Milk", "2 liters of milk from store");
        System.out.println(task);
        task.markAsDone();
        System.out.println(task);
    }
}
