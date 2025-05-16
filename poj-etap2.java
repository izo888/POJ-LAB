import java.util.*;

// 1. Abstrakcyjna klasa Asset
abstract class Asset {
    protected String symbol;
    protected String name;
    protected double currentPrice;

    public Asset(String symbol, String name, double initialPrice) {
        if (symbol == null || symbol.isEmpty() || name == null || name.isEmpty() || initialPrice < 0) {
            throw new IllegalArgumentException("Invalid asset data");
        }
        this.symbol = symbol;
        this.name = name;
        this.currentPrice = initialPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public abstract void updatePrice();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(symbol, asset.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}

// 2. Klasa Stock dziedzicząca po Asset
class Stock extends Asset {
    public Stock(String symbol, String name, double initialPrice) {
        super(symbol, name, initialPrice);
    }

    @Override
    public void updatePrice() {
        double change = (Math.random() - 0.5) * 0.1; // +/- 5%
        this.currentPrice *= (1 + change);
        if (this.currentPrice < 0) {
            this.currentPrice = 0;
        }
    }
}

// 3. Klasa Bond dziedzicząca po Asset
class Bond extends Asset {
    private double interestRate;

    public Bond(String symbol, String name, double initialPrice, double interestRate) {
        super(symbol, name, initialPrice);
        this.interestRate = interestRate;
    }

    @Override
    public void updatePrice() {
        // Prosta symulacja wzrostu ceny obligacji
        this.currentPrice *= (1 + interestRate / 365); // Dzienny wzrost
    }
}

// 4. Record dla pozycji w portfelu
record PortfolioPosition(Asset asset, int quantity) {
}

// 5. Klasa Portfolio
class Portfolio {
    private double cash;
    private Map<String, PortfolioPosition> positions;

    public Portfolio(double initialCash) {
        this.cash = initialCash;
        this.positions = new HashMap<>();
    }

    public void addAsset(Asset asset, int quantity) {
        String symbol = asset.getSymbol();
        if (positions.containsKey(symbol)) {
            PortfolioPosition existingPosition = positions.get(symbol);
            positions.put(symbol, new PortfolioPosition(existingPosition.asset(), existingPosition.quantity() + quantity));
        } else {
            positions.put(symbol, new PortfolioPosition(asset, quantity));
        }
    }

    public double getCash() {
        return cash;
    }

    public Map<String, PortfolioPosition> getPositions() {
        return Collections.unmodifiableMap(positions); // zwracamy niemodyfikowalny widok
    }

    public double calculateAssetsValue() {
        double totalValue = 0;
        for (PortfolioPosition pos : positions.values()) {
            totalValue += pos.asset().getCurrentPrice() * pos.quantity();
        }
        return totalValue;
    }

    public double calculateTotalValue() {
        return calculateAssetsValue() + cash;
    }
}

// 6. Klasa główna
public class Main {

    public static void main(String[] args) {
        // Lista dostępnych aktywów
        List<Asset> assets = new ArrayList<>();
        assets.add(new Stock("CDR", "CD Projekt", 300.0));
        assets.add(new Stock("PKO", "PKO BP", 40.0));
        assets.add(new Bond("BND1", "Bond 1", 100.0, 0.05)); // 5% rocznie

        // Portfel
        Portfolio portfolio = new Portfolio(10000.0);

        // Dodanie aktywów do portfela
        portfolio.addAsset(assets.get(0), 10);
        portfolio.addAsset(assets.get(1), 50);
        portfolio.addAsset(assets.get(2), 20);

        // Symulacja
        for (int i = 0; i < 10; i++) {
            System.out.println("--- Krok " + i + " ---");
            for (Asset asset : assets) {
                asset.updatePrice();
                System.out.println(asset.getSymbol() + " cena: " + asset.getCurrentPrice());
            }
            System.out.println("Wartość portfela: " + portfolio.calculateTotalValue());
        }

        // Finalny stan portfela
        System.out.println("\n--- Finalny Stan Portfela ---");
        System.out.println("Gotówka: " + portfolio.getCash() + " PLN");
        System.out.println("Pozycje:");
        for (PortfolioPosition pos : portfolio.getPositions().values()) {
            System.out.println("- " + pos.asset().getName() + " (" + pos.asset().getSymbol() + "): " +
                    pos.quantity() + " szt. @ " + pos.asset().getCurrentPrice() + " PLN");
        }
        System.out.println("Wartość całkowita: " + portfolio.calculateTotalValue() + " PLN");

        // Test wyjątku
        try {
            new Stock("ERR", "Error Stock", -10);
        } catch (IllegalArgumentException e) {
            System.out.println("\nWyjątek złapany: " + e.getMessage());
        }
    }
}
