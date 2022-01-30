import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Cody Rorick
 * Date: 3/5/2020
 * Summary: This program creates a line chart from data read in using the StockAnalyzer class
 */
public class StockGraph extends Application{
    //this initializes all of the variables needed in different sections of this program
    private Stage stage = null;
    private BorderPane pane = null;
    private ComboBox<String> comboBox = null;
    private Label label = null;
    private ArrayList<CheckBox> checkBox = null;
    private LineChart lineChart = null;
    private TextField statusbar = null;
    private String stockSymbol = null;
    private CategoryAxis xAxis = null;
    private NumberAxis yAxis = null;
    private ArrayList<AbstractStock> stocks = new ArrayList<AbstractStock>();
    private XYChart.Series seriesOpen = new XYChart.Series();
    private XYChart.Series seriesHigh = new XYChart.Series();
    private XYChart.Series seriesLow = new XYChart.Series();
    private XYChart.Series seriesClose = new XYChart.Series();
    private final int stageWidth = 800;
    private final int stageHeight = 800;
    private ArrayList<File> file = new ArrayList<File>();
    private StockAnalyzer analyzer = new StockAnalyzer();

    private String[] getParameter() {
        Parameters params = getParameters();
        List<String> parameters = params.getRaw();
        String[] stockNames = null;
        if(!parameters.isEmpty()){
            stockNames = new String[parameters.size()];
            int i = 0;
            while ( i < parameters.size()){
                stockNames[i] = parameters.get(i);
                i++;
            }
        }else{
            stockNames = new String[1];
            stockNames[0] = "AAPL";
        }
        stockSymbol = stockNames[0];

        return stockNames;
    }

    private void makeStockData(){
            String[] stockNames1 = getParameter();
            for(int i = 0; i < stockNames1.length; i++){
                file.add(new File(getParameter()[i] +".csv"));
            }
            try{
                for(int i = 0; i < file.size(); i++) {
                    analyzer.loadStockData(file.get(i));
                }
                for(int j = 0; j < analyzer.listBySymbol(getParameter()[0]).size(); j++) {
                    stocks.add(analyzer.listBySymbol(getParameter()[0]).get(j));
                }
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }
    }

    private Stage loadStage(Stage stage){
        pane = new BorderPane();
        Scene scene = new Scene(pane, stageWidth, stageHeight);
        pane.setBottom(makeStatusBar());
        pane.setTop(makeSelectionBar());
        pane.setCenter(makeLineChart());
        stage.setScene(scene);
        stage.setTitle("Stock Analyzer Line Chart");

        return stage;
    }

    private LineChart makeLineChart(){
        //setting up the axis and their titles
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        xAxis.setLabel("Date");
        yAxis.setLabel("Stock Price");
        seriesOpen.setName("OPEN");
        seriesHigh.setName("HIGH");
        seriesLow.setName("LOW");
        seriesClose.setName("CLOSE");
        dataLoader();
        //loading the stock data into their respective series
        lineChart = new LineChart(xAxis,yAxis);
        lineChart.getData().addAll(seriesOpen, seriesHigh, seriesLow, seriesClose);
        // this sets all of the data to not be visible when the program is first opened
        seriesOpen.getNode().setVisible(false);
        seriesHigh.getNode().setVisible(false);
        seriesClose.getNode().setVisible(false);
        seriesLow.getNode().setVisible(false);
        // this sets the title of the stock graph to be whichever symbol is selected
        lineChart.setTitle("Stock Monitoring: " + stockSymbol);
        //this makes the dates expand along the line chart, and the line chart not have any symbols representing the data points
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(false);

        return lineChart;
    }

    private void dataLoader() {
        for(int i = 0; i < getDateData().size(); i++) {
            seriesOpen.getData().add(new XYChart.Data(getDateData().get(i), stocks.get(i).getOpen()));
        }
        for(int i = 0; i < getDateData().size(); i++) {
            seriesHigh.getData().add(new XYChart.Data(getDateData().get(i), stocks.get(i).getHigh()));
        }
        for(int i = 0; i < getDateData().size(); i++) {
            seriesLow.getData().add(new XYChart.Data(getDateData().get(i), stocks.get(i).getLow()));
        }
        for(int i = 0; i < getDateData().size(); i++) {
            seriesClose.getData().add(new XYChart.Data(getDateData().get(i), stocks.get(i).getClose()));
        }
    }

    private ComboBox<String> makeComboBox(){
        //this creates a comboBox and has a list of strings that match the parameters given in the command line
        comboBox = new ComboBox<String> ();
        for(int i = 0; i < getParameter().length; i++){
            comboBox.getItems().add(getParameter()[i]);
        }
        //this makes the first entry in the combobox the one that is selected
        comboBox.getSelectionModel().select(0);

        return comboBox;
    }

    private HBox makeSelectionBar(){
        //this creates the bar that spans the top of the scene and formats it
        HBox selectionPane = new HBox();
        selectionPane.setPadding(new Insets(5, 4, 5, 4));
        selectionPane.setSpacing(10);
        selectionPane.setStyle("-fx-background-color: GRAY");
        label = new Label("Select Stock: ");
        checkBox = new ArrayList<CheckBox>();
        checkBox.add(new CheckBox("Open"));
        checkBox.add(new CheckBox("High"));
        checkBox.add(new CheckBox("Low"));
        checkBox.add(new CheckBox("Close"));
        //this adds all of the elements to the bar that spans the top of the screen
        selectionPane.getChildren().addAll(label, makeComboBox(), checkBox.get(0), checkBox.get(1), checkBox.get(2), checkBox.get(3));
        selectionPane.setPrefWidth(stageWidth);
        selectionPane.setPrefHeight((stageHeight/20));

        return selectionPane;
    }

    private HBox makeStatusBar( ) {
        //this creates the text field at the bottom of the scene and formats it
        HBox statusbarPane = new HBox();
        statusbarPane.setPadding(new Insets(5, 4, 5, 4));
        statusbarPane.setSpacing(10);
        statusbarPane.setStyle("-fx-background-color: #336699");
        statusbar = new TextField();
        statusbar.setEditable(false);
        HBox.setHgrow(statusbar, Priority.ALWAYS);
        statusbarPane.getChildren().add(statusbar);
        statusbarPane.setPrefWidth(stageWidth);
        statusbarPane.setPrefHeight((stageHeight/20));

        return statusbarPane;
    }

    private ArrayList<String> getDateData(){
        ArrayList<LocalDate> dateData2 = new ArrayList<LocalDate>();
        ArrayList<String> dateData3 = new ArrayList<String>();
        //this converts the epoch days to epoch seconds, then it converts epoch seconds into a local date, and then it converts the local date into a string
        for(int i = 0; i < stocks.size(); i++) {
            Instant instant = Instant.ofEpochSecond((stocks.get(i).getTimestamp())*(24)*(60)*(60));
            dateData2.add(instant.atZone(ZoneId.systemDefault()).toLocalDate());
            dateData3.add(dateData2.get(i).toString());
        }

        return dateData3;
    }

    private void ComboBoxSelection(){
        comboBox.setOnAction ( event -> {
            // This clears all of the previous data that was loaded onto the line chart
            stocks.clear();
            seriesOpen.getData().clear();
            seriesClose.getData().clear();
            seriesLow.getData().clear();
            seriesHigh.getData().clear();
            stockSymbol = comboBox.getSelectionModel().getSelectedItem();
            //This changes the stock data that is displayed on the line chart according to its symbol, and changes the title of the line chart data
            lineChart.setTitle("Stock Monitoring: " + stockSymbol);
            for(int i = 0; i < analyzer.listStocks().size(); i++){
                if(analyzer.listStocks().get(i).getSymbol().equals(stockSymbol)) {
                    stocks.add(analyzer.listStocks().get(i));
                }
            }
            // This will set the new data up onto the graph each time a new stock has been selected in the comboBox
            dataLoader();
        } );
    }

    private void CheckBoxSelection(){
        //This will change what data is expressed depending on what checkbox has been selected
            checkBox.get(0).setOnAction(event -> {
                seriesOpen.getNode().setVisible(checkBox.get(0).isSelected());
            });
            checkBox.get(1).setOnAction(event -> {
                seriesHigh.getNode().setVisible(checkBox.get(1).isSelected());
            });
            checkBox.get(2).setOnAction(event -> {
                seriesLow.getNode().setVisible(checkBox.get(2).isSelected());
            });
            checkBox.get(3).setOnAction(event -> {
                seriesClose.getNode().setVisible(checkBox.get(3).isSelected());
            });
    }

    private void pressStock(){
        // This will display the data point that is clicked on in the status bar below the line chart
        lineChart.setOnMousePressed (( MouseEvent event) -> {
            Point2D mouseSceneCoords = new Point2D(event.getSceneX(), event.getSceneY());
            String x = xAxis.getValueForDisplay (xAxis.sceneToLocal(mouseSceneCoords).getX());
            Number y = yAxis.getValueForDisplay (yAxis.sceneToLocal(mouseSceneCoords).getY());
            statusbar.setText( (x == null) ? "" : String.format ("(%s, %f)", x, y ));
        });
    }

    private void HandleEvents(){
        ComboBoxSelection();
        CheckBoxSelection();
        pressStock();
    }

    public void start ( Stage stage ) {
        //this loads all of the data into the program
        makeStockData();
        //this loads all of the gui's components
        this.stage = loadStage(stage);
        //this will handle any changes to the gui
        HandleEvents();
        //this shows the gui to the user
        stage.show();
    }

//this calls the program with the command line arguments, which are going to be symbols of stocks
    public static void main(String[] args) {
        launch(args);
    }
}
