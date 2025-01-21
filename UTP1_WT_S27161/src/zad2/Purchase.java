/**
 *
 *  @author Wieczorek Tomasz S27161
 *
 */

package zad2;


public class Purchase {

    private String id;
    private String name;
    private String product;
    private double cost;
    private double amount;


    public Purchase(String id, String name, String product, double cost, double amount) {
        this.id = id;
        this.name = name;
        this.product = product;
        this.cost = cost;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return id + ";" + name + ";" + product + ";" + cost + ";" + amount;
    }
}
