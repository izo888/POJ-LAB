import java.util.*;

abstract class Asset {
    protected String symbol;
    protected String name;
    protected double currentPrice;

    public Asset(String symbol, String name, double initialPrice) {
        if (symbol == null || symbol.isEmpty() || name == null || name.isEmpty() || initialPrice < 0) {
            throw new IllegalArgumentException("Niepoprawne dane aktywa.");
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
        return symbol.equals(asset.symbol);
    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }
}

class Stock extends Asset {
    public Stock(String symbol, String name, double initialPrice) {
        super(symbol, name, initialPrice);
    }

    @Override
    public void updatePrice() {
        Random random = new Random();
        double percentChange = (random.nextDouble() - 0.5) * 0.1; // Losowa zmiana -5% do +5%
        currentPrice *= (1 + percentChange);
        if (currentPrice < 0) {
            currentPrice = 0; // Cena nie może być ujemna
        }
    }
}

class Bond extends Asset {
    private double interestRate;

    public Bond(String symbol, String name, double initialPrice, double interestRate) {
        super(symbol, name, initialPrice);
        this.interestRate = interestRate;
    }

    @Override
    public void updatePrice() {
        // Prosta symulacja - cena obligacji rośnie o stopę procentową
        currentPrice *= (1 + interestRate);
    }
}

record PortfolioPosition(Asset asset, int quantity) {}

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
            positions.put(symbol, new PortfolioPosition(asset, existingPosition.quantity() + quantity));
        } else {
            positions.put(symbol, new PortfolioPosition(asset, quantity));
        }
    }

    public double calculateAssetsValue() {
        double totalValue = 0.0;
        for (PortfolioPosition pos : positions.values()) {
            totalValue += pos.asset().getCurrentPrice() * pos.quantity();
        }
        return totalValue;
    }

    public double calculateTotalValue() {
        return calculateAssetsValue() + cash;
    }

    public Map<String, PortfolioPosition> getPositions() {
        return Collections.unmodifiableMap(positions); // Zwraca niemodyfikowalny widok
    }

    public double getCash() {
        return cash;
    }
}

public class StockMarketSimStage2 {

    public static void main(String[] args) {
        // Utwórz aktywa
        List<Asset> assets = new ArrayList<>();
        assets.add(new Stock("CDR", "CD Projekt", 300.0));
        assets.add(new Stock("PKO", "PKO BP", 40.0));
        assets.add(new Bond("PLGB1025", "Obligacja Skarbu Państwa", 1000.0, 0.05)); // 5%

        // Utwórz portfel
        Portfolio portfolio = new Portfolio(10000.0);

        // Dodaj aktywa do portfela
        portfolio.addAsset(assets.get(0), 10); // 10 CDR
        portfolio.addAsset(assets.get(1), 50); // 50 PKO
        portfolio.addAsset(assets.get(2), 2);  // 2 Obligacje

        // Symulacja
        for (int i = 0; i < 10; i++) {
            System.out.println("--- Krok " + i + " ---");
            for (Asset asset : assets) {
                asset.updatePrice();
                System.out.println(asset.getSymbol() + " cena: " + asset.getCurrentPrice() + " PLN");
            }
            System.out.println("Wartość portfela: " + portfolio.calculateTotalValue() + " PLN");
        }

        // Finalny stan portfela
        System.out.println("\n--- Finalny Stan Portfela ---");
        System.out.println("Gotówka: " + portfolio.getCash() + " PLN");
        for (Map.Entry<String, PortfolioPosition> entry : portfolio.getPositions().entrySet()) {
            PortfolioPosition pos = entry.getValue();
            System.out.println(pos.asset().getSymbol() + " (" + pos.asset().getName() + "): " + pos.quantity() + " szt. Wartość: " + pos.asset().getCurrentPrice() * pos.quantity() + " PLN");
        }
        System.out.println("Całkowita wartość portfela: " + portfolio.calculateTotalValue() + " PLN");
    }
}
