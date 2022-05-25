// CSC 285-BD1 Final Project
// Liam O'Connor
// August 4, 2021
// Professor: Jack Haley'

package bakery;

import java.text.NumberFormat; // for the toString() method

public class BakeryProduct {

    // Variable declarations
    private String name; // Max 50 characters
    private double price;

    // constructors

    public BakeryProduct(String name, double price) {
        setName(name);
        setPrice(price);
    }

    // Setters

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // Getters

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    // Other functions

    public String toString() {
        NumberFormat dollar = NumberFormat.getCurrencyInstance();

        return
            ("\n Name: " + this.name +
             "\n Price: " + dollar.format(this.price));
    }

    public static int size() {
        return 108; // (50 * 2) + 8 = 108
    }


}
