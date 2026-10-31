import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Task 2
class Stock {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price > 0) {
            this.price = price;
        }
    }

    public void displayStock() {
        System.out.printf("%-10s %-20s Rs. %.2f%n",
                symbol, name, price);
    }
}

// Transaction class
class Transaction {
    private String type;
    private String symbol;
    private int quantity;
    private double price;
    private String date;

    public Transaction(String type, String symbol,
                       int quantity, double price) {
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        this.date = LocalDateTime.now().format(formatter);
    }

    @Override
    public String toString() {
        return date + " | " + type + " | " + symbol
                + " | Qty: " + quantity
                + " | Price: Rs. " + String.format("%.2f", price);
    }
}

// User class
class User {
    private String name;
    private double cash;
    private Map<String, Integer> holdings;

    public User(String name, double initialCash) {
        this.name = name;
        this.cash = initialCash;
        this.holdings = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public double getCash() {
        return cash;
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }

    public boolean buyStock(Stock stock, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return false;
        }

        double totalCost = stock.getPrice() * quantity;

        if (cash < totalCost) {
            System.out.println("Insufficient cash balance!");
            return false;
        }

        cash -= totalCost;

        holdings.put(
                stock.getSymbol(),
                holdings.getOrDefault(stock.getSymbol(), 0)
                        + quantity
        );

        System.out.println("Stock purchased successfully!");
        System.out.printf("Total cost: Rs. %.2f%n", totalCost);

        return true;
    }

    public boolean sellStock(Stock stock, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be positive.");
            return false;
        }

        int owned = holdings.getOrDefault(
                stock.getSymbol(), 0
        );

        if (owned < quantity) {
            System.out.println("You do not own enough shares!");
            return false;
        }

        double totalValue = stock.getPrice() * quantity;

        cash += totalValue;

        int remaining = owned - quantity;

        if (remaining == 0) {
            holdings.remove(stock.getSymbol());
        } else {
            holdings.put(stock.getSymbol(), remaining);
        }

        System.out.println("Stock sold successfully!");
        System.out.printf("Sale proceeds: Rs. %.2f%n", totalValue);

        return true;
    }
}

// Main application class
public class StockTradingPlatform {

    private static final Scanner sc = new Scanner(System.in);

    private static final List<Stock> market = new ArrayList<>();

    private static final List<Transaction> transactions =
            new ArrayList<>();

    private static User user;

    // Initialize sample stock market data
    private static void initializeMarket() {
        market.add(new Stock("TCS", "Tata Consultancy", 3500));
        market.add(new Stock("INFY", "Infosys", 1800));
        market.add(new Stock("RELIANCE", "Reliance", 2900));
        market.add(new Stock("HDFCBANK", "HDFC Bank", 1700));
        market.add(new Stock("ITC", "ITC Limited", 450));
    }

    // Display market
    private static void displayMarket() {
        System.out.println("\n========== STOCK MARKET ==========");
        System.out.printf("%-10s %-20s %s%n",
                "Symbol", "Company", "Price");

        for (Stock stock : market) {
            stock.displayStock();
        }
    }

    // Find stock by symbol
    private static Stock findStock(String symbol) {
        for (Stock stock : market) {
            if (stock.getSymbol().equalsIgnoreCase(symbol)) {
                return stock;
            }
        }

        return null;
    }

    // Buy stock
    private static void buyStock() {
        displayMarket();

        System.out.print("\nEnter stock symbol: ");
        String symbol = sc.next().toUpperCase();

        Stock stock = findStock(symbol);

        if (stock == null) {
            System.out.println("Stock not found!");
            return;
        }

        System.out.print("Enter quantity: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid whole number.");
            sc.next();
            return;
        }

        int quantity = sc.nextInt();

        if (user.buyStock(stock, quantity)) {
            transactions.add(
                    new Transaction("BUY", symbol,
                            quantity, stock.getPrice())
            );
            saveData();
        }
    }

    // Sell stock
    private static void sellStock() {
        displayPortfolio();

        System.out.print("\nEnter stock symbol: ");
        String symbol = sc.next().toUpperCase();

        Stock stock = findStock(symbol);

        if (stock == null) {
            System.out.println("Stock not found!");
            return;
        }

        System.out.print("Enter quantity: ");

        if (!sc.hasNextInt()) {
            System.out.println("Please enter a valid whole number.");
            sc.next();
            return;
        }

        int quantity = sc.nextInt();

        if (user.sellStock(stock, quantity)) {
            transactions.add(
                    new Transaction("SELL", symbol,
                            quantity, stock.getPrice())
            );
            saveData();
        }
    }

    // Display portfolio and calculate unrealized P/L
    private static void displayPortfolio() {
        System.out.println("\n========== YOUR PORTFOLIO ==========");

        System.out.printf("Available Cash: Rs. %.2f%n",
                user.getCash());

        double holdingsValue = 0;

        System.out.printf("%-10s %-12s %-15s %-15s%n",
                "Symbol", "Quantity", "Market Price", "Market Value");

        for (Map.Entry<String, Integer> entry :
                user.getHoldings().entrySet()) {

            Stock stock = findStock(entry.getKey());

            if (stock != null) {
                int quantity = entry.getValue();

                double value = stock.getPrice() * quantity;

                holdingsValue += value;

                System.out.printf(
                        "%-10s %-12d Rs. %-11.2f Rs. %.2f%n",
                        stock.getSymbol(),
                        quantity,
                        stock.getPrice(),
                        value
                );
            }
        }

        System.out.printf("%nTotal Holdings Value: Rs. %.2f%n",
                holdingsValue);

        System.out.printf("Total Portfolio Value: Rs. %.2f%n",
                user.getCash() + holdingsValue);
    }

    // Display transaction history
    private static void displayTransactions() {
        System.out.println("\n========== TRANSACTION HISTORY ==========");

        if (transactions.isEmpty()) {
            System.out.println("No transactions yet.");
            return;
        }

        for (Transaction transaction : transactions) {
            System.out.println(transaction);
        }
    }

    // Simulate a market price update
    private static void updatePrices() {
        Random random = new Random();

        for (Stock stock : market) {
            double change = (random.nextDouble() * 10) - 5;

            double newPrice = stock.getPrice()
                    * (1 + change / 100);

            stock.setPrice(newPrice);
        }

        System.out.println("Simulated market prices updated!");
    }

    // Save portfolio and transaction history
    private static void saveData() {
        try (PrintWriter writer =
                     new PrintWriter(new FileWriter("portfolio.txt"))) {

            writer.println("User: " + user.getName());
            writer.printf("Available Cash: %.2f%n",
                    user.getCash());

            writer.println("\nHoldings:");

            for (Map.Entry<String, Integer> entry :
                    user.getHoldings().entrySet()) {

                writer.println(entry.getKey()
                        + "," + entry.getValue());
            }

            writer.println("\nTransactions:");

            for (Transaction transaction : transactions) {
                writer.println(transaction);
            }

        } catch (IOException e) {
            System.out.println("Error saving portfolio: "
                    + e.getMessage());
        }
    }

    // Main menu
    public static void main(String[] args) {

        initializeMarket();

        System.out.println("====================================");
        System.out.println("      STOCK TRADING PLATFORM");
        System.out.println("====================================");

        System.out.print("Enter your name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            name = "Trader";
        }

        user = new User(name, 100000);

        int choice;

        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. View Stock Market");
            System.out.println("2. Buy Stocks");
            System.out.println("3. Sell Stocks");
            System.out.println("4. View Portfolio");
            System.out.println("5. View Transaction History");
            System.out.println("6. Update Simulated Market Prices");
            System.out.println("7. Exit");

            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input. Enter a number.");
                sc.next();
                continue;
            }

            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    displayMarket();
                    break;

                case 2:
                    buyStock();
                    break;

                case 3:
                    sellStock();
                    break;

                case 4:
                    displayPortfolio();
                    break;

                case 5:
                    displayTransactions();
                    break;

                case 6:
                    updatePrices();
                    break;

                case 7:
                    saveData();
                    System.out.println("Portfolio saved successfully.");
                    System.out.println("Thank you for using the platform!");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 7);

        sc.close();
    }
}