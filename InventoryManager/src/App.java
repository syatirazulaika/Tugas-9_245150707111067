import java.nio.file.*;
import java.util.*;

public class App {
    public static void main(String[] args) throws Exception {
        Path dataDir  = Paths.get("data");
        Path csvFile  = dataDir.resolve("products.csv");
        InventoryService service = new InventoryService();

        service.initDataDirectory(dataDir);           
        List<Product> products = service.loadProducts(csvFile); 

        service.runMenu(products);                   
        service.saveProducts(products, csvFile);      
}
}
