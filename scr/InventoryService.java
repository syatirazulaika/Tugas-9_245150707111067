import java.io.*;
import java.nio.file.*;
import java.util.*;

public class InventoryService {

    public void initDataDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    public List<Product> loadProducts(Path csvPath) throws IOException {
        List<Product> products = new ArrayList<>();
        if (Files.exists(csvPath)) {
            try (BufferedReader br = Files.newBufferedReader(csvPath)) {
                br.readLine(); // skip header
                String line;
                while ((line = br.readLine()) != null) {
                    products.add(Product.fromCsvLine(line));
                }
            }
        }
        return products;
    }

    public void saveProducts(List<Product> products, Path csvPath) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(csvPath)) {
            bw.write("id,name,category,price,quantity\n");
            for (Product p : products) {
                bw.write(p.toCsvLine() + "\n");
            }
        }
    }

    public List<Product> searchProducts(List<Product> products, String keyword) {
        keyword = keyword.toLowerCase();
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.getName().toLowerCase().contains(keyword)) {
                result.add(p);
            }
        }
        return result;
    }

    public void sortProducts(List<Product> products, String criteria) {
        if (criteria.equalsIgnoreCase("price")) {
            products.sort(Comparator.comparingDouble(Product::getPrice));
        } else if (criteria.equalsIgnoreCase("quantity")) {
            products.sort(Comparator.comparingInt(Product::getQuantity));
        }
    }

    public List<Product> filterByPrice(List<Product> products, double min, double max) {
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (p.getPrice() >= min && p.getPrice() <= max) {
                result.add(p);
            }
        }
        return result;
    }

    public void runMenu(List<Product> products) throws IOException {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== INVENTORY MANAGER ===");
            System.out.println("1. Lihat semua");
            System.out.println("2. Tambah produk");
            System.out.println("3. Update stok");
            System.out.println("4. Hapus produk");
            System.out.println("5. Cari produk");
            System.out.println("6. Sort produk");
            System.out.println("7. Filter harga");
            System.out.println("8. Simpan & Keluar");
            System.out.print("Pilih: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1": viewAll(products); break;
                case "2": addProduct(products, sc); break;
                case "3": updateQuantity(products, sc); break;
                case "4": deleteProduct(products, sc); break;
                case "5":
                    System.out.print("Masukkan keyword: ");
                    String keyword = sc.nextLine();
                    List<Product> found = searchProducts(products, keyword);
                    viewAll(found);
                    break;
                case "6":
                    System.out.print("Sort berdasarkan (price/quantity): ");
                    String sortKey = sc.nextLine();
                    sortProducts(products, sortKey);
                    viewAll(products);
                    break;
                case "7":
                    System.out.print("Harga minimum: ");
                    double min = Double.parseDouble(sc.nextLine());
                    System.out.print("Harga maksimum: ");
                    double max = Double.parseDouble(sc.nextLine());
                    List<Product> filtered = filterByPrice(products, min, max);
                    viewAll(filtered);
                    break;
                case "8":
                    running = false;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private void viewAll(List<Product> products) {
        System.out.println("\nDaftar Produk:");
        for (Product p : products) {
            System.out.printf("ID: %d | %s | %s | Rp%.2f | Stok: %d\n",
                p.getId(), p.getName(), p.getCategory(), p.getPrice(), p.getQuantity());
        }
    }

    private void addProduct(List<Product> products, Scanner sc) {
        System.out.print("Nama: ");
        String name = sc.nextLine();
        System.out.print("Kategori: ");
        String category = sc.nextLine();
        System.out.print("Harga: ");
        double price = Double.parseDouble(sc.nextLine());
        System.out.print("Stok: ");
        int qty = Integer.parseInt(sc.nextLine());

        int newId = products.stream().mapToInt(Product::getId).max().orElse(0) + 1;
        products.add(new Product(newId, name, category, price, qty));
        System.out.println("Produk ditambahkan.");
    }

    private void updateQuantity(List<Product> products, Scanner sc) {
        System.out.print("Masukkan ID produk: ");
        int id = Integer.parseInt(sc.nextLine());
        for (Product p : products) {
            if (p.getId() == id) {
                System.out.print("Stok baru: ");
                p.setQuantity(Integer.parseInt(sc.nextLine()));
                System.out.println("Stok diperbarui.");
                return;
            }
        }
        System.out.println("Produk tidak ditemukan.");
    }

    private void deleteProduct(List<Product> products, Scanner sc) {
        System.out.print("Masukkan ID produk: ");
        int id = Integer.parseInt(sc.nextLine());
        products.removeIf(p -> p.getId() == id);
        System.out.println("Produk dihapus.");
    }
}
