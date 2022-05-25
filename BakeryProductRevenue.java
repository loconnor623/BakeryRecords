// CSC 285-BD1 Final Project
// Liam O'Connor
// August 4, 2021
// Professor: Jack Haley

package bakery;

import java.io.*;
import java.io.RandomAccessFile;
import java.text.NumberFormat;

public class BakeryProductRevenue extends BakeryProduct {

    // Variable declarations
    private int amountMade, amountSold; // From these calculate the max revenue and theoretical revenue.
    private double discountsGiven;
    private char willSellNextDay; // Y: yes, N: no, M: maybe/unknown at this point


    // Constructors

    public BakeryProductRevenue(String name, double price) {
        super(name, price);
        setAmountMade(0);
        setAmountSold(0);
        setDiscountsGiven(0);
        setWillSellNextDay('N');
    }

    public BakeryProductRevenue(String name, int price, int amountMade, int amountSold, double discountsGiven, char willSellNextDay) {
        super(name, price);
        setAmountMade(amountMade);
        setAmountSold(amountSold);
        setDiscountsGiven(discountsGiven);
        setWillSellNextDay(willSellNextDay);
    }

    // Setters

    public void setAmountMade(int amountMade) {
        this.amountMade = amountMade;
    }

    public void setAmountSold(int amountSold) {
        this.amountSold = amountSold;
    }

    public void setDiscountsGiven(double discountsGiven) {
        this.discountsGiven = discountsGiven;
    }

    public void setWillSellNextDay(char willSellNextDay) {
        if (Character.isLowerCase(willSellNextDay))
            willSellNextDay = Character.toUpperCase(willSellNextDay);
        this.willSellNextDay = willSellNextDay;
    }

    // Getters

    public int getAmountMade() {
        return amountMade;
    }

    public int getAmountSold() {
        return amountSold;
    }

    public double getDiscountsGiven() {
        return discountsGiven;
    }

    public char getWillSellNextDay() {
        return willSellNextDay;
    }

    // File IO Functions

    //define write method - to write data to a file
    public void write(RandomAccessFile file) throws IOException {

        StringBuffer buff;
        if(this.getName() != null){
            buff = new StringBuffer(this.getName());
        }else{
            buff = new StringBuffer(50);
        }
        buff.setLength(50); //set length
        file.writeChars(buff.toString()); //write string as sequense of characters

        file.writeDouble(this.getPrice());
        file.writeInt(amountMade);
        file.writeInt(amountSold);
        file.writeDouble(discountsGiven);
        file.writeChar(willSellNextDay);
    }

    public void read(RandomAccessFile file) throws IOException {

        char[] arr = new char[50];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = file.readChar(); //read chars from a file
        }
        this.setName(new String(arr));

        this.setPrice(file.readDouble());
        amountMade = file.readInt();
        amountSold = file.readInt();
        discountsGiven = file.readDouble();
        willSellNextDay = file.readChar();
    }

    // Other Functions

    public String toString() {
        NumberFormat dollar = NumberFormat.getCurrencyInstance();

        return
            (super.toString() +
            "\n Amount made: " + amountMade +
            "\n Amount sold: " + amountSold +
            "\n Discounts given: " + dollar.format(discountsGiven) +
            "\n Will sell next day: " + willSellNextDay);
    }

    public static int size() {
        return BakeryProduct.size() + 18; // (4 * 2) + 8 + 2
    }
}
