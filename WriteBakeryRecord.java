// CSC 285-BD1 Final Project
// Liam O'Connor
// August 4, 2021
// Professor: Jack Haley


// Imports from bakery package
import bakery.BakeryProductRevenueTotal;

// Imports from JavaFX
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Imports from Java
import java.io.RandomAccessFile;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;


public class WriteBakeryRecord extends Application {

    private DatePicker dpDate;
    private TextField tfName, tfPrice, tfAmountMade, tfAmountSold, tfDiscountsGiven, tfWillSellNextDay;
    private Button btnBack, btnNext, btnDone;

    private RandomAccessFile file;
    private BakeryProductRevenueTotal bprTotal;
    private int productPage;

    @Override
    public void start(Stage primaryStage) {

        bprTotal = new BakeryProductRevenueTotal();

        // Open output file
        try {
            file = new RandomAccessFile("BakeryRecords.dat", "rw");
        }
        catch (IOException ioe) {
            System.err.println("Could not open file: " + ioe.toString());
            System.exit(1);
        }

        // One pane to rule them all
        VBox rootPane = new VBox();

        // *** DATE *** //

        // HBox holding the date field.
        HBox datePane = new HBox(10);
        datePane.setAlignment(Pos.CENTER);

        datePane.setPadding(new Insets(20, 10, 5, 10)); // Top, left, bottom, right

        dpDate = new DatePicker();
        dpDate.setPromptText("Default set to today's date.");
        datePane.getChildren().addAll(new Label("Date (MM/DD/YYYY): "), dpDate);
        System.out.println(String.valueOf(dpDate.getEditor().getText()));


        // Display current date
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/YYYY, hh:mm");


        Label lblToday = new Label("Today's date is: " + String.valueOf(dateFormat.format(today)));
        StackPane todayPane = new StackPane(lblToday);


        // Separator between date and product info.
        Separator separator = new Separator();
        separator.setPadding(new Insets(10));

                        // *** PRODUCT INFO *** //

        // Setup for GridPane
        GridPane productsPane = new GridPane();
        productsPane.setAlignment(Pos.CENTER);
        productsPane.setHgap(8);
        productsPane.setVgap(10);
        productsPane.setPadding(new Insets(20, 10, 2, 10)); // Top, left, bottom, right

        // Product fields

        productsPane.add(new Label("Product name: "), 0, 0); // over, down
        tfName = new TextField();
        tfName.setEditable(false);
        productsPane.add(tfName, 1, 0);

        productsPane.add(new Label("Price: "), 0, 1);
        tfPrice = new TextField();
        tfPrice.setEditable(false);
        productsPane.add(tfPrice, 1, 1);

        productsPane.add(new Label("Amount made: "), 0, 2);
        tfAmountMade = new TextField();
        tfAmountMade.setPromptText("Default: 0");
        productsPane.add(tfAmountMade, 1, 2);

        productsPane.add(new Label("Amount sold: "), 0, 3);
        tfAmountSold = new TextField();
        tfAmountSold.setPromptText("Default: 0");
        productsPane.add(tfAmountSold, 1, 3);

        productsPane.add(new Label("Discounts given: "), 0, 4);
        tfDiscountsGiven = new TextField();
        tfDiscountsGiven.setPromptText("Default: 0.00");
        productsPane.add(tfDiscountsGiven, 1, 4);

        productsPane.add(new Label("Will sell next day: "), 0, 5);
        tfWillSellNextDay = new TextField();
        tfWillSellNextDay.setPromptText("Default: N");
        productsPane.add(tfWillSellNextDay, 1, 5);

        // Buttons

        btnBack = new Button("Back");
        btnBack.setMaxWidth(Double.MAX_VALUE);
        BackButton backHandler = new BackButton();
        btnBack.setOnAction(backHandler);
        productsPane.add(btnBack, 0, 6);
        btnBack.setDisable(true); // because the productPage starts at 0

        btnNext = new Button("Next");
        btnNext.setMaxWidth(Double.MAX_VALUE);
        NextButton nextHandler = new NextButton();
        btnNext.setOnAction(nextHandler);
        productsPane.add(btnNext, 1, 6);

        btnDone = new Button("Done");
        btnDone.setMaxWidth(Double.MAX_VALUE);
        DoneButton doneHandler = new DoneButton();
        btnDone.setOnAction(doneHandler);
        productsPane.add(btnDone, 0, 7, 2, 1);

        // Add everything to the rootPane VBox
        rootPane.getChildren().addAll(datePane, todayPane, separator, productsPane);

        productPage = 0;
        fillTextFields();

                    // *** SETTING THE STAGE *** //

        // Set scene
        Scene scene = new Scene(rootPane);

        primaryStage.setTitle("Bakery Records");
        primaryStage.setWidth(350);
        primaryStage.setHeight(475);

        primaryStage.setScene(scene);

        primaryStage.show();
    }

            // *** TEXTFIELD FUNCTIONS *** //

    // Takes information from bprTotal and displays it on the present page
    void fillTextFields() {
        DecimalFormat twoDigits = new DecimalFormat( "0.00" );

        dpDate.getEditor().setText(bprTotal.getDate());
        tfName.setText(bprTotal.products[productPage].getName());
        tfPrice.setText(String.valueOf(twoDigits.format(bprTotal.products[productPage].getPrice())));
        tfAmountMade.setText(String.valueOf(bprTotal.products[productPage].getAmountMade()));
        tfAmountSold.setText(String.valueOf(bprTotal.products[productPage].getAmountSold()));
        tfDiscountsGiven.setText(String.valueOf(twoDigits.format(bprTotal.products[productPage].getDiscountsGiven())));
        tfWillSellNextDay.setText(String.valueOf(bprTotal.products[productPage].getWillSellNextDay()));
    }

    // Takes information that was written in each field and saves it into bprTotal
    void saveTextFields() {
        bprTotal.setDate(dpDate.getEditor().getText());
        try {
            bprTotal.products[productPage].setAmountMade(Integer.parseInt(tfAmountMade.getText()));
        } catch (NumberFormatException nfe) {
            System.err.println("Must enter \"Amount Made\" as an integer.");
        }
        try {
            bprTotal.products[productPage].setAmountSold(Integer.parseInt(tfAmountSold.getText()));
        } catch (NumberFormatException nfe) {
            System.err.println("Must enter \"Amount Sold\" as an integer.");
        }
        try {
            bprTotal.products[productPage].setDiscountsGiven(Double.parseDouble(tfDiscountsGiven.getText()));
        } catch (NumberFormatException nfe) {
            System.err.println("Must enter \"Discounts Given\" as a double.");
        }
        bprTotal.products[productPage].setWillSellNextDay(tfWillSellNextDay.getText().charAt(0));
    }



                // *** BUTTON HANDLERS *** //

    class BackButton implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            saveTextFields();
            --productPage;
            fillTextFields();
            btnNext.setDisable(false); // Pushing this button means you can't be at the end.
            if (productPage == 0)
                btnBack.setDisable(true);
        }
    }

    class NextButton implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            saveTextFields();
            ++productPage;
            fillTextFields();
            btnBack.setDisable(false); // Pushing this button means you can't be at the beginning.
            if (productPage == (bprTotal.products.length - 1))
                btnNext.setDisable(true);
        }
    }

    class DoneButton implements EventHandler<ActionEvent> {

        @Override
        public void handle (ActionEvent e) {
            // Write data to the output file.
            saveTextFields();
            try {
                file.seek(file.length());
                bprTotal.write(file);
            } catch (IOException ioe) {
                System.err.println("Unable to write to file: " + ioe.toString());
            }

            // Close the file.
            try {
                file.close();
            }
            catch (IOException ioe) {
                System.err.println("File not closed properly: " + ioe.toString());
                System.exit(1);
            }

            System.out.println(bprTotal.toString());
            System.exit(0);
        }
    }

    // Unnecessary in most circumstances. Included for the minority.
    public static void main(String[] args) {
        launch(args);
    }
}


// IO FUNCTIONS (ran out of time for these. Was planning to sort by date.)
/*
// Rearranges M/d/YYYY date format into YYYYMd for easier sorting.
String processDate(String date) {
    String year = date.substring(date.lastIndexOf("/") + 1);
    String month = date.substring(0, indexOf("/"));
    String day = date.substring(date.indexOf("/") + 1, date.lastIndexOf("/"));

    return year + month + day;
}

void addBakeryRecord(RandomAccessFile file) {
    thisDate = processDate(bprTotal.getdate());
    try {
        while (file.getFilePointer() != file.length() && )
    }
}

String checkDate(RandomAccessFile file) throws EOFException {
    // int numRecords = (file.length() / bprTotal.size());
    String tempDate;
    int dateSize = 10;

    char[] arr = new char[dateSize];
    for (int i = 0; i < arr.length; i++) {
        arr[i] = file.readChar();
    }
    tempDate = new String(arr);
    file.seek(file.getFilePointer() - (dateSize * 2)); // Gets file pointer back to where it started.

    return tempDate;

}
*/
