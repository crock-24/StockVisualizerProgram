import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

/**
 * Author: Cody Rorick
 * Date: 2/9/2020
 * Summary: This class implements the stockAnalyzerInterface interface to obtain the necessary methods for stock information
 */
public class StockAnalyzer2 implements StockAnalyzerInterface2 {
    //initializing all of the stock ArrayList's that we will need to collect required information and use for future purposes these will keep accumulating each time a loadStockData method is called on the same StockAnalyzer object
    private ArrayList<AbstractStock> stock = new ArrayList<AbstractStock>();
    private ArrayList<Long> timeStamp = new ArrayList<Long>();
    private ArrayList<Double> open = new ArrayList<Double>();
    private ArrayList<Double> high = new ArrayList<Double>();
    private ArrayList<Double> low = new ArrayList<Double>();
    private ArrayList<Double> close = new ArrayList<Double>();
    private ArrayList<Double> volume = new ArrayList<Double>();

    @Override
    public ArrayList<AbstractStock> loadStockData(File file) throws FileNotFoundException {
        //Initializing all local variables to the loadStockData method
        Scanner scan = new Scanner(file);
        String symbol;
        ArrayList<String> data = new ArrayList<String>();
        ArrayList<String[]> newData = new ArrayList<String[]>();

        //initializing local variables that will have to eventually be passed to the global variables, these will reset every time a loadStockData method is called
        ArrayList<Long> timeStamp1 = new ArrayList<Long>();
        ArrayList<Double> open1 = new ArrayList<Double>();
        ArrayList<Double> high1 = new ArrayList<Double>();
        ArrayList<Double> low1 = new ArrayList<Double>();
        ArrayList<Double> close1 = new ArrayList<Double>();
        ArrayList<Double> volume1 = new ArrayList<Double>();
        //This variable will count how many times the loop that assigns a new stock object will iterate
        int count = 0;

        //Puts string data from csv file into an array of lines
        while (scan.hasNextLine()) {
                data.add(scan.nextLine());
        }

        //newdata gets a string array of each line of data, separated by where the commas were, and then assigns it into an ArrayList called newData
        for(int k = 0; k < data.size(); k++){
            String [] newdata = data.get(k).split(",");
            newData.add(newdata);
        }

        //Removes any lines with the string headers from the newData ArrayList
        for(int i = 0; i < newData.size(); i++) {
            String[] newdata1 = newData.get(i);
            if (newdata1[0].equals("Date")) {
                newData.remove(i);
            }
        }

        //reads through each array of data in the newData ArrayList individually by assigning it to a String array called newdata4, and then picking it apart and assigning its specific parts to distinct ArrayLists
        for(int i = 0; i < newData.size(); i++) {
            String[] newdata4 = newData.get(i);
            for (int j = 0; j < newdata4.length; j++) {
                //Data before the first comma had date information, converting it to a timestamp here and adding it to an ArrayList
                if (j == 0 && !newdata4[j + 1].equals("null")) {
                    //splitting the date into year, month, and day, then turns it into a timestamp
                    String[] dateData = newdata4[j].split("-");
                    int year = Integer.parseInt(dateData[0]);
                    int month = Integer.parseInt(dateData[1]) - 1;
                    int day = Integer.parseInt(dateData[2]);
                    Date date = new Date(year, month, day, 23, 59, 59);
                    timeStamp1.add(date.getTime());
                }
                //Data before second comma had open information, adding it to an ArrayList
                else if (j == 1 && !newdata4[j].equals("null")) {
                    open1.add(Double.parseDouble(newdata4[j]));
                }
                //Data before third comma had high information, adding it to an ArrayList
                else if (j == 2 && !newdata4[j].equals("null")) {
                    high1.add(Double.parseDouble(newdata4[j]));
                }
                //Data before fourth comma had low information, adding it to an ArrayList
                else if (j == 3 && !newdata4[j].equals("null")) {
                    low1.add(Double.parseDouble(newdata4[j]));
                }
                //Data before fifth comma had close information, adding it to an ArrayList
                else if (j == 4 && !newdata4[j].equals("null")) {
                    close1.add(Double.parseDouble(newdata4[j]));
                }
                //Data after last comma had volume information, adding it to an ArrayList, also incrementing the count variable that decides how many arguments the stock object will need
                else if (j == 6 && !newdata4[j].equals("null")) {
                    volume1.add(Double.parseDouble(newdata4[j]));
                    count++;
                }
            }
        }
        //creates the symbol name and removes the .csv extension from it
        String[] symbol1 = file.getName().split("c");
        symbol = symbol1[0].substring(0, symbol1[0].length()-1);
        // loads pertinent stock data into an AbstractStock ArrayList
        for(int k = 0; k < count; k++) {
            this.timeStamp.add(timeStamp1.get(k));
            this.open.add(open1.get(k));
            this.low.add(low1.get(k));
            this.close.add(close1.get(k));
            this.volume.add(volume1.get(k));
            Stock stock1 = new Stock(symbol, timeStamp1.get(k), open1.get(k), high1.get(k), low1.get(k), close1.get(k), volume1.get(k));
            this.stock.add(stock1);
        }
        //returns the required type of AbstractStock ArrayList
        return stock;
    }

    @Override
    public ArrayList<AbstractStock> listBySymbol(String symbol) {
        // stock2 will add all of the appropriate stocks to its ArrayList
        ArrayList<AbstractStock> stock2 = new ArrayList<AbstractStock>();
        // this loop will find the appropriate data for the requested symbol given as a method parameter
        for(int i = 0; i < this.stock.size(); i++){
            if(this.stock.get(i).getSymbol().equals(symbol)){
                stock2.add(this.stock.get(i));
            }
        }
        return stock2;
    }

    @Override
    public ArrayList<AbstractStock> listBySymbolDates(String symbol, Date startDate, Date endDate) {
        // startStamp finds the initial time (in milliseconds) in range, endStamp finds the end time (in milliseconds) in range, and stock2 will add all of the appropriate stocks to its ArrayList
        ArrayList<AbstractStock> stock2 = new ArrayList<AbstractStock>();
        Long startStamp = startDate.getTime();
        Long endStamp = endDate.getTime();
        // this loop will find the appropriate data for the the requested symbol and range of dates given as method parameters
        for (int i = 0; i < this.stock.size(); i++) {
            if (this.stock.get(i).getSymbol().equals(symbol)) {
                if(startStamp <= this.stock.get(i).getTimestamp()){
                    if (this.stock.get(i).getTimestamp() > endStamp) {
                        break;
                    }
                        stock2.add(this.stock.get(i));
                    }
                }
            }
         return stock2;
    }

    @Override
    public ArrayList<AbstractStock> listStocks() {
        return this.stock;
    }

    @Override
    public double averageHigh(String symbol, Date startDate, Date endDate) throws StockNotFoundException {
        // day keeps track of the amount of days associated with the range that is requested, startStamp finds the initial time (in milliseconds) in range, endStamp finds the end time (in milliseconds) in range, and sum accumulates the High data
        int day = 0;
        Long startStamp = startDate.getTime();
        double sum = 0;
        Long endStamp = endDate.getTime();
        // this loop will find the appropriate data for the the requested symbol and range of dates given as method parameters
        for (int i = 0; i < this.stock.size(); i++) {
            if (this.stock.get(i).getSymbol().equals(symbol)) {
                if(startStamp <= this.stock.get(i).getTimestamp()){
                    if (endStamp < this.stock.get(i).getTimestamp()) {
                        break;
                    }
                    sum += this.stock.get(i).getHigh();
                    day++;
                }
            }
        }
        // to make sure the stock has an appropriate amount of data to analyze, if not, a StockNotFound Exception will be thrown
        if(day > 1) {
            double average = (sum / day);
            return average;
        }else{
            throw new StockNotFoundException(symbol);
        }
    }

    @Override
    public double averageLow(String symbol, Date startDate, Date endDate) throws StockNotFoundException {
        // day keeps track of the amount of days associated with the range that is requested, startStamp finds the initial time (in milliseconds) in range, endStamp finds the end time (in milliseconds) in range, and sum accumulates the Low data
        int day = 0;
        Long startStamp = startDate.getTime();
        double sum = 0;
        Long endStamp = endDate.getTime();
        // this loop will find the appropriate data for the the requested symbol and range of dates given as method parameters
        for (int i = 0; i < this.stock.size(); i++) {
            if (this.stock.get(i).getSymbol().equals(symbol)) {
                if(startStamp <= this.stock.get(i).getTimestamp()){
                    if (endStamp < this.stock.get(i).getTimestamp()) {
                        break;
                    }
                    sum += this.stock.get(i).getLow();
                    day++;
                }
            }
        }
        // to make sure the stock has the appropriate amount of data to analyze, if not, a StockNotFound Exception will be thrown
        if(day > 1) {
            double average = (sum / day);
            return average;
        }else{
            throw new StockNotFoundException(symbol);
        }
    }

    @Override
    public double averageVolume(String symbol, Date startDate, Date endDate) throws StockNotFoundException {
        // day keeps track of the amount of days associated with the range that is requested, startStamp finds the initial time (in milliseconds) in range, endStamp finds the end time (in milliseconds) in range, and sum accumulates the volume data
        Long startStamp = startDate.getTime();
        double sum = 0;
        int day = 0;
        Long endStamp = endDate.getTime();
        // this loop will find the appropriate data for the the requested symbol and range of dates given as method parameters
        for (int i = 0; i < this.stock.size(); i++) {
            if (this.stock.get(i).getSymbol().equals(symbol)) {
                if(startStamp <= this.stock.get(i).getTimestamp()){
                    if (endStamp < this.stock.get(i).getTimestamp()) {
                        break;
                    }
                    sum += this.stock.get(i).getVolume();
                    day++;
                }
            }
        }
        // to make sure the stock has an appropriate amount of data to analyze, if not, a StockNotFound Exception will be thrown
        if(day > 1) {
            double average = (sum / day);
            return average;
        }else{
            throw new StockNotFoundException(symbol);
        }
    }
}
