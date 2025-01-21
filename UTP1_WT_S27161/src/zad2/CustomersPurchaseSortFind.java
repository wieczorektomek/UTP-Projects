/**
 * @author Wieczorek Tomasz S27161
 */

package zad2;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CustomersPurchaseSortFind {

    List<Purchase> purchases;

    public CustomersPurchaseSortFind() {
        purchases = new ArrayList<>();
    }

    void readFile(String fname) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fname));
            String line;
            String[] data;
            while ((line = br.readLine()) != null) {
                data = line.split(";");
                purchases.add(new Purchase(data[0], data[1], data[2], Double.parseDouble(data[3]), Double.parseDouble(data[4])));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void showSortedBy(String comp) {
        switch (comp) {
            case "Nazwiska": {
                System.out.println("Nazwiska");
                purchases.stream()
                        .sorted((p1, p2) -> p1.getId().compareTo(p2.getId()))
                        .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
                        .forEach(System.out::println);
                break;
            }
            case "Koszty": {
                System.out.println("Koszty");
                purchases.stream()
                        .sorted((p1, p2) -> p1.getId().compareTo(p2.getId()))
                        .sorted((p1, p2) -> Double.compare(p2.getCost() * p2.getAmount(), p1.getCost() * p1.getAmount()))
                        .forEach(p -> System.out.println(p + " (koszt:" + p.getCost() * p.getAmount() + ")"));
                break;
            }
        }
    }

    public void showPurchaseFor(String id) {
        System.out.println("Klient " + id);
        purchases.stream()
                .filter(p -> p.getId().equals(id))
                .forEach(System.out::println);
    }
}
