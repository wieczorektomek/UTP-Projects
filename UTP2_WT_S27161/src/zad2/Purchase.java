/**
 *
 *  @author Wieczorek Tomasz S27161
 *
 */

package zad2;


import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

public class Purchase {

    VetoableChangeSupport vetoableSupport = new VetoableChangeSupport(this);

    private String prod;
    private String data;
    private double price;

    public Purchase(String prod, String data, double price) {
        this.prod = prod;
        this.data = data;
        this.price = price;
    }

    public synchronized void setData(String newData) throws PropertyVetoException {
        String oldData = data;
        vetoableSupport.fireVetoableChange("data", oldData, newData);
        this.data = newData;
    }

    public synchronized void setPrice(double newPrice) throws PropertyVetoException {
        Double oldPrice = price;
        if (newPrice < 1000) {
            throw new PropertyVetoException("Price change to: " + newPrice + " not allowed", null);
        }
        else {
            vetoableSupport.fireVetoableChange("price", oldPrice, newPrice);
            this.price = newPrice;
        }
    }


    public synchronized void addPropertyVetoListener(VetoableChangeListener listener){
        vetoableSupport.addVetoableChangeListener(listener);
    }

    @Override
    public String toString() {
        return "Purchase [" + "prod=" + prod + ", data=" + data + ", price=" + price + ']';
    }
}
