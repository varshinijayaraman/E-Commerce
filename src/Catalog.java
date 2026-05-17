import java.util.List;

public interface Catalog {
    Product getById(String id);
    List<Product> listAll();
}

