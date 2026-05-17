import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleCatalog implements Catalog {
    private final List<Product> products;

    public SimpleCatalog(List<Product> products) {
        this.products = new ArrayList<>(products);
    }

    @Override
    public Product getById(String id) {
        for (Product p : products) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<Product> listAll() {
        return Collections.unmodifiableList(products);
    }
}

