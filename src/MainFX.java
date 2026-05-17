import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.Map;

public class MainFX extends Application {
    @Override
    public void start(Stage stage) {
        Catalog catalog = new SimpleCatalog(Arrays.asList(
                new BasicProduct("p1", "Keyboard", 25.50),
                new BasicProduct("p2", "Mouse", 12.99),
                new BasicProduct("p3", "USB Cable", 5.75)
        ));

        Cart cart = new SimpleCart();
        CheckoutService checkoutService = new SimpleCheckoutService();

        ListView<String> productList = new ListView<>();
        for (Product p : catalog.listAll()) {
            productList.getItems().add(p.getId() + " - " + p.getName() + " ($" + p.getPrice() + ")");
        }

        ListView<String> cartList = new ListView<>();

        TextField qtyField = new TextField("1");
        qtyField.setPrefWidth(80);

        Label totalLabel = new Label("Total: $0.00");

        Runnable refreshCart = () -> {
            cartList.getItems().clear();
            Map<String, Integer> items = cart.getItems();
            if (items.isEmpty()) {
                cartList.getItems().add("(empty)");
            } else {
                for (Map.Entry<String, Integer> e : items.entrySet()) {
                    Product p = catalog.getById(e.getKey());
                    String name = (p == null) ? e.getKey() : p.getName();
                    double price = (p == null) ? 0 : p.getPrice();
                    double line = price * e.getValue();
                    cartList.getItems().add(name + " (qty=" + e.getValue() + ", line=$" + String.format("%.2f", line) + ")");
                }
            }
            totalLabel.setText("Total: $" + String.format("%.2f", cart.getTotal(catalog)));
        };

        Button addBtn = new Button("Add to cart");
        addBtn.setOnAction(e -> {
            int idx = productList.getSelectionModel().getSelectedIndex();
            if (idx < 0) {
                showAlert(Alert.AlertType.WARNING, "Select a product first");
                return;
            }

            String selected = productList.getItems().get(idx);
            String productId = selected.split(" - ")[0].trim();

            int qty;
            try {
                qty = Integer.parseInt(qtyField.getText().trim());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Quantity must be a number");
                return;
            }

            try {
                cart.add(productId, qty);
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, ex.getMessage());
                return;
            }

            refreshCart.run();
        });

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction(e -> {
            if (cart.getItems().isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Cart is empty");
                return;
            }
            double total = cart.getTotal(catalog);
            checkoutService.checkout(cart, catalog);
            refreshCart.run();
            showAlert(Alert.AlertType.INFORMATION, "Checkout complete! Total = $" + String.format("%.2f", total));
        });

        refreshCart.run();

        VBox left = new VBox(10, new Label("Products"), productList);
        left.setPadding(new Insets(10));

        VBox right = new VBox(10, new Label("Cart"), cartList);
        right.setPadding(new Insets(10));

        HBox controls = new HBox(10, new Label("Quantity:"), qtyField, addBtn, checkoutBtn);
        controls.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setLeft(left);
        root.setCenter(right);
        root.setBottom(new VBox(controls, totalLabel));

        Scene scene = new Scene(root, 700, 420);
        stage.setTitle("Simple E-Commerce (JavaFX)");
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

