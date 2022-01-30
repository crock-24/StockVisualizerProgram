import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
/**
 * Author: Cody Rorick
 * Date: 2/9/2020
 * Summary: This subclass of Abstract stock creates a Stock object by passing the parameters in its constructor to its superclass, and it contains a toString method that will print out the necessary stock data when it is printed
 */
//passes the constructor data to AbstractStock
public class Stock extends AbstractStock {
    public Stock(String symbol, Long timestamp, double open, double high, double low, double close, double volume) {
        super(symbol, timestamp, open, high, low, close, volume);
    }
//utilizes the accessor methods in the superclass to print out a toString Statement about the object
    @Override
    public String toString() {
        return String.format("%s: %.2f, %.2f%n", super.getSymbol(), super.getOpen(), super.getClose());
    }

    public static void main(String[] args) {
        StockAnalyzer analyzer = new StockAnalyzer();
        File file = new File("AAPL.csv");
        File file1 = new File("SPCE.csv");
        ArrayList<AbstractStock> stock = null;
        try {
            stock = analyzer.loadStockData(file1);
            stock = analyzer.loadStockData(file);
            for(int i = 0; i < stock.size(); i++) {
                System.out.println(stock.get(i).getTimestamp());
            }


        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }
}

