// CSC 285-BD1 Final Project
// Liam O'Connor
// August 4, 2021
// Professor: Jack Haley

// package imports
import bakery.BakeryProductRevenue;
import bakery.BakeryProductRevenueTotal;

// Imports from JavaFX
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Separator;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// Other Imports
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.RandomAccessFile;
import java.io.*;
import java.text.NumberFormat;


public class ReadBakeryRecords extends Application {

    // JavaFX
    private TextField tfDate, tfMaxRevenue, tfActualRevenue, tfMostPopular, tfLeastPopular;
    private Button btnSummary, btnDetails, btnPrevRecord, btnNextRecord, btnDone;

    VBox rootPane;
    GridPane summaryPane;
    StackPane detailsPane;

    // Other
    BakeryProductRevenueTotal bprTotal;
    private RandomAccessFile file;

    @Override
    public void start(Stage primaryStage) {

        // Open input file
        try {
            file = new RandomAccessFile("BakeryRecords.dat", "r");
        }
        catch (IOException ioe) {
            System.err.println("Could not open file: " + ioe.toString());
            System.exit(1);
        }

        // Read values into the object
        bprTotal = new BakeryProductRevenueTotal();
        try {
            bprTotal.read(file);
        } catch (IOException ioe) {
            System.err.println("Unable to read file: " + ioe.toString());
        }


        rootPane = new VBox();

                    // *** SUMMARY AND DETAILS BUTTONS *** //

        HBox viewPane = new HBox(0);
        viewPane.setAlignment(Pos.CENTER);
        viewPane.setPadding(new Insets(15));

        btnSummary = new Button("Summary");
        btnSummary.setMaxWidth(Double.MAX_VALUE);
        SummaryHandler summaryHandler = new SummaryHandler();
        btnSummary.setOnAction(summaryHandler);
        viewPane.getChildren().add(btnSummary);
        btnSummary.setDisable(true);

        btnDetails = new Button("Details");
        btnDetails.setMaxWidth(Double.MAX_VALUE);
        DetailsHandler detailsHandler = new DetailsHandler();
        btnDetails.setOnAction(detailsHandler);
        viewPane.getChildren().add(btnDetails);


                        // *** DATE *** //

        // HBox holding the date field.
        HBox datePane = new HBox(10);
        datePane.setAlignment(Pos.CENTER);

        datePane.setPadding(new Insets(20, 10, 5, 10)); // Top, left, bottom, right

        tfDate = new TextField();
        tfDate.setEditable(false);
        datePane.getChildren().addAll(new Label("Date: "), tfDate);

        // Display current date
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/YYYY, hh:mm");


        Label lblToday = new Label("Today's date is: " + String.valueOf(dateFormat.format(today)));
        StackPane todayPane = new StackPane(lblToday);


        // Separator between date and product info.
        Separator separator = new Separator();
        separator.setPadding(new Insets(10));


                        // *** SUMMARY PANE *** //

        summaryPane = new GridPane();
        summaryPane.setAlignment(Pos.CENTER);
        summaryPane.setHgap(8);
        summaryPane.setVgap(10);
        summaryPane.setPadding(new Insets(20, 10, 2, 10)); // Top, left, bottom, right

        summaryPane.add(new Label("Max Revenue: "), 0, 0); // over, down
        tfMaxRevenue = new TextField();
        tfMaxRevenue.setEditable(false);
        summaryPane.add(tfMaxRevenue, 1, 0);

        summaryPane.add(new Label("Actual Revenue: "), 0, 1);
        tfActualRevenue = new TextField();
        tfActualRevenue.setEditable(false);
        summaryPane.add(tfActualRevenue, 1, 1);

        summaryPane.add(new Label("Most Popular: "), 0, 2);
        tfMostPopular = new TextField();
        tfMostPopular.setEditable(false);
        summaryPane.add(tfMostPopular, 1, 2);

        summaryPane.add(new Label("Least Popular: "), 0, 3);
        tfLeastPopular = new TextField();
        tfLeastPopular.setEditable(false);
        summaryPane.add(tfLeastPopular, 1, 3);

        // Buttons

        btnPrevRecord = new Button("Previous Record");
        btnPrevRecord.setMaxWidth(Double.MAX_VALUE);
        PrevRecordHandler prevRecordHandler = new PrevRecordHandler();
        btnPrevRecord.setOnAction(prevRecordHandler);
        btnPrevRecord.setDisable(true);
        summaryPane.add(btnPrevRecord, 0, 4);

        btnNextRecord = new Button("Next Record");
        btnNextRecord.setMaxWidth(Double.MAX_VALUE);
        NextRecordHandler nextRecordHandler = new NextRecordHandler();
        btnNextRecord.setOnAction(nextRecordHandler);
        try {
            if (file.getFilePointer() == file.length()) // if there is only one record in the file, disable next button
                btnNextRecord.setDisable(true);
        } catch (IOException ioe) {
            System.err.println("Unable to access file: " + ioe.toString());
        }
        summaryPane.add(btnNextRecord, 1, 4);

        btnDone = new Button("Done");
        btnDone.setMaxWidth(Double.MAX_VALUE);
        DoneHandler doneHandler = new DoneHandler();
        btnDone.setOnAction(doneHandler);
        summaryPane.add(btnDone, 0, 5, 2, 1);

        fillTextFields();

                        // *** DETAILS  PANE *** //

        detailsPane = new StackPane();
        TableView<BakeryProductRevenue> tvDetails = new TableView<>();
        tvDetails.setEditable(false);

        ObservableList<BakeryProductRevenue> olProducts =
            FXCollections.observableArrayList(bprTotal.products);

        // Add table columns for each category in BakeryProductRevenue

        TableColumn<BakeryProductRevenue, String> tcName = new TableColumn<>("Product Name");
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tvDetails.getColumns().add(tcName);

        TableColumn<BakeryProductRevenue, Double> tcPrice = new TableColumn<>("Price");
        tcPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tvDetails.getColumns().add(tcPrice);

        TableColumn<BakeryProductRevenue, Integer> tcAmountMade = new TableColumn<>("Amount Made");
        tcAmountMade.setCellValueFactory(new PropertyValueFactory<>("amountMade"));
        tvDetails.getColumns().add(tcAmountMade);

        TableColumn<BakeryProductRevenue, Integer> tcAmountSold = new TableColumn<>("Amount Sold");
        tcAmountSold.setCellValueFactory(new PropertyValueFactory<>("amountSold"));
        tvDetails.getColumns().add(tcAmountSold);

        TableColumn<BakeryProductRevenue, Double> tcDiscountsGiven = new TableColumn<>("Discounts Given");
        tcDiscountsGiven.setCellValueFactory(new PropertyValueFactory<>("discountsGiven"));
        tvDetails.getColumns().add(tcDiscountsGiven);

        TableColumn<BakeryProductRevenue, Character> tcWillSellNextDay = new TableColumn<>("Will sell next day");
        tcWillSellNextDay.setCellValueFactory(new PropertyValueFactory<>("willSellNextDay"));
        tvDetails.getColumns().add(tcWillSellNextDay);

        // Fill the table and put it in the pane
        tvDetails.setItems(olProducts);
        detailsPane.getChildren().add(tvDetails);


        // Add everything to the rootPane VBox
        rootPane.getChildren().addAll(viewPane, datePane, todayPane, separator, summaryPane);


        // *** SETTING THE STAGE *** //

        // Set scene
        Scene scene = new Scene(rootPane);

        primaryStage.setTitle("Bakery Records");
        primaryStage.setWidth(650);
        primaryStage.setHeight(475);

        primaryStage.setScene(scene);

        primaryStage.show();

    }
    // Display functions

    void fillTextFields() {
        NumberFormat dollar = NumberFormat.getCurrencyInstance();

        // Fill each TextField with data from the bprTotal object
        tfDate.setText(bprTotal.getDate());
        tfMaxRevenue.setText(String.valueOf(dollar.format(bprTotal.calcMaxRevenue())));
        tfActualRevenue.setText(String.valueOf(dollar.format(bprTotal.calcActualRevenue())));
        tfMostPopular.setText(bprTotal.calcMostPopular());
        tfLeastPopular.setText(bprTotal.calcLeastPopular());
    }

    void displaySummary() {
        rootPane.getChildren().remove(detailsPane);
        rootPane.getChildren().add(summaryPane);
        btnSummary.setDisable(true);
        btnDetails.setDisable(false);
    }

    void displayDetails() {
        rootPane.getChildren().remove(summaryPane);
        rootPane.getChildren().add(detailsPane);
        btnDetails.setDisable(true);
        btnSummary.setDisable(false);
    }

    // *** BUTTON HANDLERS *** //

    class SummaryHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            displaySummary();
        }
    }

    class DetailsHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            displayDetails();
        }
    }

    class PrevRecordHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            try {
                // Go back 2 record sizes to get to the record before the one just read
                file.seek(file.getFilePointer() - (bprTotal.size() * 2));
                bprTotal.read(file);
                fillTextFields();
                btnNextRecord.setDisable(false);

                System.out.println(bprTotal.toString());
                if (file.getFilePointer() == bprTotal.size()) // If just read first record, disable btnPrevRecord
                    btnPrevRecord.setDisable(true);
            } catch (IOException ioe) {
                System.err.println("Unable to read file: " + ioe.toString());
                System.exit(1);
            }
        }
    }

    class NextRecordHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {

            try {
                bprTotal.read(file);
                fillTextFields();
                btnPrevRecord.setDisable(false);

                System.out.println(bprTotal.toString());
                if (file.getFilePointer() == file.length()) // If at end of file, disable next button
                    btnNextRecord.setDisable(true);
            } catch (IOException ioe) {
                System.err.println("Unable to read file: " + ioe.toString());
                System.exit(1);
            }
        }
    }

    class DoneHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle (ActionEvent e) {

            try {
                file.close();
            }
            catch (IOException ioe) {
                System.err.println("File not closed properly: " + ioe.toString());
                System.exit(1);
            }

            System.exit(0);
        }
    }

  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
