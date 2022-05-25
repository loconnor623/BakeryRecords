// CSC 285-BD1 Final Project
// Liam O'Connor
// August 7, 2021
// Professor: Jack Haley

/*
This class is going to use an Arraylist of BakeryProductRevenue. Each bakery product is going to have
    its own set of funds. This class is going to pull them together, find totals and statistics.

*/

package bakery;

import java.io.*;
import java.io.RandomAccessFile;
import java.util.Date;
import java.text.SimpleDateFormat;

public class BakeryProductRevenueTotal /*implements Bakery_Iface*/ {

    private String date; // "M/d/YYYY" max 10 char
    public SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/YYYY");

    // Entire catalog of baked goods
    public BakeryProductRevenue products[] = {
        // Pastries
        new BakeryProductRevenue("Ajver Sfiha", 4.00),
        new BakeryProductRevenue("Beef and Tomato Sfiha", 6.00),
        new BakeryProductRevenue("Egg Salad Pie", 4.50),
        new BakeryProductRevenue("Kale and Onion Pie", 5.00),
        new BakeryProductRevenue("Spinach and Feta Pie", 5.00),
        new BakeryProductRevenue("Three Cheese and Chive fatayer", 5.00),
        // Breads
        new BakeryProductRevenue("Pita", 6.50),
        new BakeryProductRevenue("Za'atar Focaccia", 7.00),
    };
    // Number of products in the array
    public static final int NUM_PRODUCTS = 8;

    public BakeryProductRevenueTotal() {
        date = String.valueOf(dateFormat.format(new Date())); // set date to today's date by default
    }

    public BakeryProductRevenueTotal(String date) {
        setDate(date);
    }

    // Setter
    public void setDate(String date) {
        this.date = date;
    }

    // Getter
    public String getDate() {
        return date;
    }

    // File IO Functions

    public void write(RandomAccessFile file) throws IOException {
        // write date
        StringBuffer buff;
        if(this.getDate() != null){
            buff = new StringBuffer(date);
        }else{
            buff = new StringBuffer(10);
        }
        buff.setLength(10); //set length
        file.writeChars(buff.toString());

        // write products array
        for (int i = 0; i < products.length; i++) {
            products[i].write(file);
        }
    }

    public void read(RandomAccessFile file) throws IOException {
        // Read in the date
        char[] arr = new char[10];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = file.readChar();
        }
        date = new String(arr);

        // Read everything into the products array
        for (int i = 0; i < products.length; i++) {
            products[i].read(file);
        }
    }

    // Other Functions

    public double calcMaxRevenue() {
        double maxRevenue = 0;

        for (int i = 0; i < products.length; i++) {
            maxRevenue += (products[i].getAmountMade() * products[i].getPrice());
        }

        return maxRevenue;
    }

    public double calcActualRevenue() {
        double actualRevenue = 0;

        for (int i = 0; i < products.length; i++) {
            actualRevenue += (products[i].getAmountSold() * products[i].getPrice());
            actualRevenue -= products[i].getDiscountsGiven();
        }

        return actualRevenue;
    }

    // For calculating most popular item
    public int findMaxAmountSold() {
        int max = 0;
        for (int i = 0; i < products.length; i++)
            if (max < products[i].getAmountSold())
                max = products[i].getAmountSold();

        return max;
    }

    // For calculating least popular item
    public int findMinAmountSold() {
        int min = findMaxAmountSold(); // Originally had it set to 0, was causing problems.

        for (int i = 0; i < products.length; i++)
            if (products[i].getAmountMade() > 0)  // if we sold the product that day
                if (min > products[i].getAmountSold())
                    min = products[i].getAmountSold();

        return min;
    }

    public String calcMostPopular() { // Most popular based on number sold
        int max = findMaxAmountSold();
        String list = "";
        for (int i = 0; i < products.length; i++) {
            if (products[i].getAmountSold() == max) {
                if (list != "")
                    list += ", ";
                list += products[i].getName();
            }
        }

        return list;
    }

    public String calcLeastPopular() {
        int min = findMinAmountSold();
        String list = "";
        for (int i = 0; i < products.length; i++) {
            if (products[i].getAmountMade() > 0) { // If product was sold that day
                if (products[i].getAmountSold() == min) {
                    if (list != "")
                        list += ", ";
                    list += products[i].getName();
                }
            }
        }

        return list;
    }


    public String toString() {
        return
           ("\n Date: " + date +
            "\n Max revenue: " + calcMaxRevenue() +
            "\n Actual revenue: " + calcActualRevenue() +
            "\n Most popular items: " + calcMostPopular() +
            "\n Least popular items: " + calcLeastPopular());
    }

    public static int size() {
        return BakeryProductRevenue.size() * NUM_PRODUCTS + 20; // Size of products array + 10 char date
    }

}
