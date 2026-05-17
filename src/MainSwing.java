import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class MainSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Catalog catalog = new SimpleCatalog(Arrays.asList(
                    new BasicProduct("p1", "Keyboard", 25.50),
                    new BasicProduct("p2", "Mouse", 12.99),
                    new BasicProduct("p3", "USB Cable", 5.75)
            ));

            Cart cart = new SimpleCart();
            CheckoutService checkoutService = new SimpleCheckoutService();

            JFrame frame = new JFrame("Simple E-Commerce (Swing)");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(650, 420);
            frame.setLocationRelativeTo(null);

            DefaultListModel<String> productModel = new DefaultListModel<>();
            for (Product p : catalog.listAll()) {
                productModel.addElement(p.getId() + " - " + p.getName() + " ($" + p.getPrice() + ")");
            }

            DefaultListModel<String> cartModel = new DefaultListModel<>();
            JList<String> productList = new JList<>(productModel);
            JList<String> cartList = new JList<>(cartModel);

            JTextField qtyField = new JTextField("1");
            JLabel totalLabel = new JLabel("Total: $0.00");

            Runnable refreshCart = () -> {
                cartModel.clear();
                Map<String, Integer> items = cart.getItems();
                if (items.isEmpty()) {
                    cartModel.addElement("(empty)");
                } else {
                    for (Map.Entry<String, Integer> e : items.entrySet()) {
                        Product p = catalog.getById(e.getKey());
                        String name = (p == null) ? e.getKey() : p.getName();
                        double price = (p == null) ? 0 : p.getPrice();
                        double line = price * e.getValue();
                        cartModel.addElement(name + " (qty=" + e.getValue() + ", line=$" + String.format("%.2f", line) + ")");
                    }
                }
                totalLabel.setText("Total: $" + String.format("%.2f", cart.getTotal(catalog)));
            };

            JButton addBtn = new JButton("Add to cart");
            addBtn.addActionListener(ev -> {
                int idx = productList.getSelectedIndex();
                if (idx < 0) {
                    JOptionPane.showMessageDialog(frame, "Select a product first.", "No product selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String selected = productModel.getElementAt(idx);
                String productId = selected.split(" - ")[0].trim();

                int qty;
                try {
                    qty = Integer.parseInt(qtyField.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Quantity must be a number.", "Invalid quantity", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    cart.add(productId, qty);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage(), "Invalid quantity", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                refreshCart.run();
            });

            JButton checkoutBtn = new JButton("Checkout");
            checkoutBtn.addActionListener(ev -> {
                double total = cart.getTotal(catalog);
                if (cart.getItems().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Cart is empty.", "Checkout", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                checkoutService.checkout(cart, catalog);
                refreshCart.run();
                JOptionPane.showMessageDialog(frame, "Checkout complete! Total = $" + String.format("%.2f", total), "Checkout", JOptionPane.INFORMATION_MESSAGE);
            });

            refreshCart.run();

            JPanel left = new JPanel(new BorderLayout());
            left.add(new JLabel("Products" , SwingConstants.CENTER), BorderLayout.NORTH);
            left.add(new JScrollPane(productList), BorderLayout.CENTER);

            JPanel right = new JPanel(new BorderLayout());
            right.add(new JLabel("Cart" , SwingConstants.CENTER), BorderLayout.NORTH);
            right.add(new JScrollPane(cartList), BorderLayout.CENTER);

            JPanel controls = new JPanel();
            controls.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5);
            c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.WEST;

            controls.add(new JLabel("Quantity:"), c);
            c.gridx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            controls.add(qtyField, c);

            c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.fill = GridBagConstraints.NONE;
            controls.add(addBtn, c);

            c.gridy = 2;
            controls.add(checkoutBtn, c);

            c.gridy = 3;
            controls.add(totalLabel, c);

            frame.setLayout(new BorderLayout(10, 10));
            frame.add(left, BorderLayout.WEST);
            frame.add(right, BorderLayout.CENTER);
            frame.add(controls, BorderLayout.SOUTH);

            frame.setVisible(true);
        });
    }
}

