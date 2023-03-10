import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class OrderBook {
    private TreeMap<Integer, Integer> bidBook;
    private TreeMap<Integer, Integer> askBook;

    public OrderBook() {
        bidBook = new TreeMap<>(Comparator.reverseOrder());
        askBook = new TreeMap<>();
    }

    static void readAndWriteToFile() {
        OrderBook orderBook = new OrderBook();

        try (BufferedReader br = new BufferedReader(new FileReader("input.txt"));
             FileWriter fw = new FileWriter("output.txt")) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens[0].equals("u")) {
                    orderBook.updateOrder(tokens);
                } else if (tokens[0].equals("q")) {
                    if (tokens[1].equals("best_bid")) {
                        fw.write(orderBook.getBestBid() + "\n");
                    } else if (tokens[1].equals("best_ask")) {
                        fw.write(orderBook.getBestAsk() + "\n");
                    } else if (tokens[1].equals("size")) {
                        int price = Integer.parseInt(tokens[2]);
                        fw.write(orderBook.getSizeAtPrice(price) + "\n");
                    }
                } else if (tokens[0].equals("o")) {
                    orderBook.processMarketOrder(tokens);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateOrder(String[] tokens) {
        int price = Integer.parseInt(tokens[1]);
        int size = Integer.parseInt(tokens[2]);
        if (tokens[3].equals("bid")) {
            if (size == 0) {
                bidBook.remove(price);
            } else {
                bidBook.put(price, size);
            }
        } else if (tokens[3].equals("ask")) {
            if (size == 0) {
                askBook.remove(price);
            } else {
                askBook.put(price, size);
            }
        }
    }

    public void processMarketOrder(String[] tokens) {
        int size = Integer.parseInt(tokens[2]);
        if (tokens[1].equals("buy")) {
            processBuyOrder(size);
        } else if (tokens[1].equals("sell")) {
            processSellOrder(size);
        }
    }

    private void processBuyOrder(int size) {
        Iterator<Map.Entry<Integer, Integer>> iterator = askBook.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int askPrice = entry.getKey();
            int askSize = entry.getValue();
            if (size >= askSize) {
                iterator.remove();
                size -= askSize;
            } else {
                askBook.put(askPrice, askSize - size);
                break;
            }
        }
    }

    private void processSellOrder(int size) {
        Iterator<Map.Entry<Integer, Integer>> iterator = bidBook.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            int bidPrice = entry.getKey();
            int bidSize = entry.getValue();
            if (size >= bidSize) {
                iterator.remove();
                size -= bidSize;
            } else {
                bidBook.put(bidPrice, bidSize - size);
                break;
            }
        }
    }

    public String getBestBid() {
        if (bidBook.isEmpty()) {
            return "NA";
        }
        int bestBidPrice = bidBook.firstKey();
        int bestBidSize = bidBook.get(bestBidPrice);
        return bestBidPrice + "," + bestBidSize;
    }

    public String getBestAsk() {
        if (askBook.isEmpty()) {
            return "NA";
        }
        int bestAskPrice = askBook.firstKey();
        int bestAskSize = askBook.get(bestAskPrice);
        return bestAskPrice + "," + bestAskSize;
    }

    public String getSizeAtPrice(int price) {
        if (bidBook.containsKey(price)) {
            return bidBook.get(price) + "";
        } else if (askBook.containsKey(price)) {
            return askBook.get(price) + "";
        } else {
            return "NA";
        }
    }
}
