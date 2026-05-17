public class SimpleCheckoutService implements CheckoutService {
    @Override
    public void checkout(Cart cart, Catalog catalog) {
        if (cart.getItems().isEmpty()) {
            System.out.println("Cart is empty. Nothing to checkout.");
            return;
        }

        double total = cart.getTotal(catalog);
        System.out.println("Checkout complete! Total = $" + String.format("%.2f", total));
        cart.clear();
    }
}

