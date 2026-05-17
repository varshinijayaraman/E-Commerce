import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Catalog catalog = new SimpleCatalog(Arrays.asList(
                new BasicProduct("p1", "Keyboard", 25.50),
                new BasicProduct("p2", "Mouse", 12.99),
                new BasicProduct("p3", "USB Cable", 5.75)
        ));

        Cart cart = new SimpleCart();
        CheckoutService checkoutService = new SimpleCheckoutService();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== Simple E-Commerce ===");
            System.out.println("1) List products");
            System.out.println("2) Add to cart");
            System.out.println("3) View cart");
            System.out.println("4) Checkout");
            System.out.println("0) Exit");
            System.out.print("Choose: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("0")) {
                break;
            }

            switch (choice) {
                case "1" -> {
                    System.out.println("Products:");
                    for (Product p : catalog.listAll()) {
                        System.out.println("- " + p.getId() + ": " + p.getName() + " ($" + p.getPrice() + ")");
                    }
                }
                case "2" -> {
                    System.out.print("Enter product id: ");
                    String id = sc.nextLine().trim();
                    Product p = catalog.getById(id);
                    if (p == null) {
                        System.out.println("Unknown product id: " + id);
                        break;
                    }

                    System.out.print("Enter quantity: ");
                    int qty;
                    try {
                        qty = Integer.parseInt(sc.nextLine().trim());
                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid quantity.");
                        break;
                    }

                    cart.add(id, qty);
                    System.out.println("Added to cart: " + qty + " x " + p.getName());
                }
                case "3" -> {
                    System.out.println("Cart items:");
                    Map<String, Integer> items = cart.getItems();
                    if (items.isEmpty()) {
                        System.out.println("(empty)");
                        break;
                    }
                    for (Map.Entry<String, Integer> e : items.entrySet()) {
                        Product p = catalog.getById(e.getKey());
                        String name = (p == null) ? e.getKey() : p.getName();
                        double price = (p == null) ? 0 : p.getPrice();
                        System.out.println("- " + name + " (id=" + e.getKey() + ") qty=" + e.getValue()
                                + " line=$" + String.format("%.2f", price * e.getValue()));
                    }
                    System.out.println("Total = $" + String.format("%.2f", cart.getTotal(catalog)));
                }
                case "4" -> checkoutService.checkout(cart, catalog);
                default -> System.out.println("Invalid choice.");
            }
        }

        System.out.println("Goodbye!");
    }
}

