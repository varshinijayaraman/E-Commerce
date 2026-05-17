import java.util.Map;

public interface Cart {
    void add(String productId, int quantity);
    Map<String, Integer> getItems(); // productId -> quantity
    double getTotal(Catalog catalog);
    void clear();
}

