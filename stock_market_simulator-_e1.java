import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class Main {

    class Stock {  // Klasa reprezentująca akcję
        private String symbol;       // Symbol akcji
        private String name;         // Nazwa firmy
        private double initialPrice; // Cena początkowa

        public Stock(String symbol, String name, double initialPrice) { // Konstruktor akcji
            this.symbol = symbol;
            this.name = name;
            this.initialPrice = initialPrice;
        }

        public String getSymbol() {  // Pobierz symbol
            return symbol;
        }

        public String getName() {    // Pobierz nazwę
            return name;
        }

        public double getInitialPrice() { // Pobierz cenę
            return initialPrice;
        }
        public boolean equals(Object obj) {  // Porównaj akcje
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Stock stock = (Stock) obj;
            return symbol.equals(stock.symbol);
        }
        public int hashCode() {   // Kod haszujący
            return symbol.hashCode();
        }
    }

    class Portfolio {  // Klasa reprezentująca portfel
        private double cash;             // Gotówka w portfelu
        private Map<Stock, Integer> stocksInPortfolio; // Akcje w portfelu

        public Portfolio(double initialCash) {  // Konstruktor portfela
            this.cash = initialCash;
            this.stocksInPortfolio = new HashMap<>();
        }

        public void addStock(Stock stock, int quantity) {  // Dodaj akcje
            if (stocksInPortfolio.containsKey(stock)) {
                stocksInPortfolio.put(stock, stocksInPortfolio.get(stock) + quantity);
            } else {
                stocksInPortfolio.put(stock, quantity);
            }
        }

        public double getCash() {  // Pobierz gotówkę
            return cash;
        }

        public Map<Stock, Integer> getStocksInPortfolio() {  // Pobierz akcje
            return Collections.unmodifiableMap(stocksInPortfolio); // Niemodyfikowalny widok
        }

        public double calculateStockValue() {  // Oblicz wartość akcji
            double totalValue = 0.0;
            for (Map.Entry<Stock, Integer> entry : stocksInPortfolio.entrySet()) {
                totalValue += entry.getValue() * entry.getKey().getInitialPrice();
            }
            return totalValue;
        }

        public double calculateTotalValue() {  // Oblicz wartość portela
            return calculateStockValue() + cash;
        }
    }

    public static void main(String[] args) {  // Główna metoda
        Stock cdr = new Stock("CDR", "CD Projekt", 300.0);  // Akcja 1
        Stock pko = new Stock("PKO", "PKO BP", 40.0);     // Akcja 2
        Stock kgh = new Stock("KGH", "KGHM", 120.0);     // Akcja 3

        Portfolio portfolio = new Portfolio(10000.0);  // Portfel

        portfolio.addStock(cdr, 10);  // Dodaj akcje
        portfolio.addStock(pko, 50);
        portfolio.addStock(kgh, 20);

        System.out.println("Stan Portfela");  // Wyświetl stan
        System.out.println("Gotówka: " + portfolio.getCash() + " PLN");
        System.out.println("Posiadane akcje:");
        for (Map.Entry<Stock, Integer> entry : portfolio.getStocksInPortfolio().entrySet()) {
            Stock stock = entry.getKey();
            int quantity = entry.getValue();
            double stockValue = quantity * stock.getInitialPrice();
            System.out.println(stock.getSymbol() + " (" + stock.getName() + "): " + quantity + " szt. @ "
                    + stock.getInitialPrice() + " PLN/szt. = " + stockValue + " PLN");
        }
        System.out.println("Wartość akcji: " + portfolio.calculateStockValue() + " PLN");
        System.out.println("Wartość całkowita portfela: " + portfolio.calculateTotalValue() + " PLN");
    }
}
