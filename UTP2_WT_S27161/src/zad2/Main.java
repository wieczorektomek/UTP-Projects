/**
 * @author Wieczorek Tomasz S27161
 */

package zad2;


import java.beans.PropertyVetoException;

public class Main {
    public static void main(String[] args) {

        Purchase purch = new Purchase("komputer", "nie ma promocji", 3000.00);
        System.out.println(purch);

        purch.addPropertyVetoListener((evt -> {

            if (evt.getPropertyName().equals("price")) {
                Double price = (Double) evt.getNewValue();
                    System.out.println("Change value of: " + evt.getPropertyName() + " from: " +
                            evt.getOldValue() + " to: " + price);

            } else if (evt.getPropertyName().equals("data")) {
                String data = (String) evt.getNewValue();
                System.out.println("Change value of: data from: " + evt.getOldValue() + " to: " + data);
            }
        }));

        try {
            purch.setData("w promocji");
            purch.setPrice(2000.00);
            System.out.println(purch);

            purch.setPrice(500.00);

        } catch (PropertyVetoException exc) {
            System.out.println(exc.getMessage());
        }
        System.out.println(purch);

    }
}
