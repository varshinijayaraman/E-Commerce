import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimpleCart implements Cart {
    private final Map<String, Integer> items = new HashMap<>();

    @Override
    public void add(String productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be > 0");
        }
        items.merge(productId, quantity, Integer::sum);
    }

    @Override
    public Map<String, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }

    @Override
    public double getTotal(Catalog catalog) {
        double total = 0;
        for (Map.Entry<String, Integer> e : items.entrySet()) {
            Product p = catalog.getById(e.getKey());
            if (p == null) {
                continue; // skip unknown ids
            }
            total += p.getPrice() * e.getValue();
        }
        return total;
    }

    @Override
    public void clear() {
        items.clear();
    }
}

