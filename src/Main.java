import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Math.abs;

public class Main {

    //initialised global variables
    private static int n;
    private static int m;
    private static int timestamp;
    private static int units;
    private static int price;
    private static int t_buy;
    private static int t_sell;
    private static int t_rollback;
    private static int total;
    private static String ts;
    private static String acc_id;

    //  private static String mTimestamp = "TIMESTAMP";
    //  private static String mAccountID = "ACCOUNT_ID";
    //  private static String mTransID = "TRANSACTION_ID";
    //  private static String mTraderID = "TRADER_ID";
    //  private static String mTrade = "TRADE";

    private static String buy = "BUY";
    private static String sell = "SELL";
    private static String rollback = "ROLLBACK";

    private static String q_log = "LOG";
    private static String q_position = "POSITION";
    private static String q_volatile = "VOLATILE";
    private static String q_triage = "TRIAGE";
    private static String q_merge = "MERGE";
    private static String q_alert = "ALERT";

    private static ArrayList<String> arrayList;
    private static ArrayList<String> tradeList;
    private static ArrayList<String> unitList;
    private static ArrayList<String> branchList;
    private static HashMap<String, ArrayList<String>> map;
    private static HashMap<String, ArrayList<String>> trademap;
    private static HashMap<String, ArrayList<String>> unitmap;


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        m = scanner.nextInt();
        scanner.nextLine();

        map = new HashMap<>();
        trademap = new HashMap<>();
        unitmap = new HashMap<>();
        branchList = new ArrayList<>();
        String input[] = new String[n];
        String queries[] = new String[m];
        for (int i = 0; i < n; i++) {
            input[i] = scanner.nextLine();
        }
        for (int i = 0; i < m; i++) {
            queries[i] = scanner.nextLine();
        }

        //TRANSACTION
        for (int i = 0; i < n; i++) {
            total = 0;
            t_buy = 0;
            t_sell = 0;
            t_rollback = 0;
            arrayList = new ArrayList<>();
            tradeList = new ArrayList<>();
            unitList = new ArrayList<>();
            String str[] = input[i].split(" ");
            if (str[0].equalsIgnoreCase("Transaction")) {
                timestamp = Integer.parseInt(str[1]);

                arrayList.add("1");
                arrayList.add(str[2]); //account_id
                arrayList.add(str[3]); //transaction_id
                arrayList.add(str[4]); //trader_id

                unitList.add(str[2]); //account_id
                unitList.add(str[3]); //transaction_id

                for (int j = 5; j < str.length; j++) {
                    if (str[j].equalsIgnoreCase(buy)) {
                        t_buy++;
                        String trade_item = str[++j];
                        arrayList.add(trade_item); //tradeable
                        unitList.add(trade_item);

                        units = Integer.parseInt(str[++j]);
                        price = Integer.parseInt(str[++j]);
                        unitList.add("+" + units);
                        unitList.add("-" + price);

                        //calculate amount
                        arrayList.add("-" + Integer.toString(units * price));
                    } else if (str[j].equalsIgnoreCase(sell)) {
                        t_sell++;
                        String trade_item = str[++j];
                        arrayList.add(trade_item); //tradeable
                        unitList.add(trade_item);

                        units = Integer.parseInt(str[++j]);
                        price = Integer.parseInt(str[++j]);
                        unitList.add("-" + units);
                        unitList.add("+" + price);

                        //calculate amount
                        arrayList.add("+" + Integer.toString(units * price));
                    } else if (str[j].equalsIgnoreCase(rollback)) {
                        //add rollback info
                        t_rollback++;
                        arrayList.add(rollback);
                        arrayList.add(str[++j]); //transaction_ID
                    }

                }
                total = t_buy + t_sell;
                tradeList.add(Integer.toString(t_buy));
                tradeList.add(Integer.toString(t_sell));
                tradeList.add(Integer.toString(t_rollback));
                tradeList.add(Integer.toString(total));

                map.put(Integer.toString(timestamp), arrayList);

                unitmap.put(Integer.toString(timestamp), unitList);


                trademap.put(str[1], tradeList);

            }//Branching
            else {
                ts = (str[1]);
                acc_id = (str[2]);

                branchList.add(ts);
                branchList.add(acc_id);
            }
        }

        //PROCESSING OF BRANCHING
        int branch_number = branchList.size();
        for (int i = 0; i < branch_number; i++) {
            String common_timestamp = branchList.get(i);
            String new_acc_id = branchList.get(++i);
            for (String key : map.keySet()) {
                if (Integer.parseInt(key) <= Integer.parseInt(common_timestamp)) {
                    arrayList = new ArrayList<>();
                    int no_of_acc = Integer.parseInt(map.get(key).get(0)) + 1;
                    map.get(key).set(0, Integer.toString(no_of_acc));
                    arrayList = map.get(key);
                    arrayList.add(no_of_acc, new_acc_id);
                    map.put(key, arrayList);
                }
            }
        }

        //QUERIES
        for (int i = 0; i < m; i++) {
            String str[] = queries[i].split(" ");

            //LOG
            if (str[0].equalsIgnoreCase(q_log)) {
                if (str.length == 2) {
                    int acc_id = Integer.parseInt(str[1]);
                    log(acc_id);
                } else {
                    int acc_id = Integer.parseInt(str[1]);
                    String tradeable_name = str[2];
                    log(acc_id, tradeable_name);
                }
            }

            //POSITION
            if (str[0].equalsIgnoreCase(q_position)) {
                int acc_id = Integer.parseInt(str[1]);
                int timestamp = Integer.parseInt(str[2]);
                position(acc_id, timestamp);
            }

            //VOLATILE
            if (str[0].equalsIgnoreCase(q_volatile)) {
                int acc_id = Integer.parseInt(str[1]);
                int timestamp1 = Integer.parseInt(str[2]);
                int timestamp2 = Integer.parseInt(str[3]);
                mVolatile(acc_id, timestamp1, timestamp2);
            }

            //TRIAGE
            if (str[0].equalsIgnoreCase(q_triage)) {
                int acc_id = Integer.parseInt(str[2]);
                String tradeable_name = str[3];
                int timestamp1 = Integer.parseInt(str[4]);
                int timestamp2 = Integer.parseInt(str[5]);
                triage(Integer.parseInt(str[1]), acc_id, tradeable_name, timestamp1, timestamp2);
            }

            //MERGE
            if (str[0].equalsIgnoreCase(q_merge)) {
                int acc_id1 = Integer.parseInt(str[1]);
                int acc_id2 = Integer.parseInt(str[2]);
                merge(acc_id1, acc_id2);
            }

            //ALERT
            if (str[0].equalsIgnoreCase(q_alert)) {
                int timestamp1 = Integer.parseInt(str[1]);
                int timestamp2 = Integer.parseInt(str[2]);
                alert(timestamp1, timestamp2);
            }

        }
    }

    /*
    QUERY COMMAND IMPLEMENTATIONS:

    Log <account_id>:
    Returns the <transaction_id> <timestamp> representing all the transactions that have occurred on that account.

    Log <account_id> <tradeable_name>:
    Returns the <transaction_id> <timestamp> representing all the transactions that have occurred for a particular tradeable on that account.

    Position <account_id> <timestamp>:

     */

    public static void log(int acc_id) {
        ArrayList<String> sortedKeys = new ArrayList<>();
        boolean contains;
        int n;
        for (String key : map.keySet()) {
            contains = false;
            n = Integer.parseInt((map.get(key)).get(0));
            for (int i = 0; i<n; i++){
                if (map.get(key).get(i+1).equals(Integer.toString(acc_id)))
                    contains = true;
            }
            if (contains) {

                sortedKeys.add(key);
                //System.out.print(map.get(key).get(1) + " ");
                //System.out.println(key);
            }
        }
        Collections.sort(sortedKeys);
        for (int i = sortedKeys.size()-1; i>=0; i--){
            n = Integer.parseInt((map.get(sortedKeys.get(i))).get(0));
            System.out.print(map.get(sortedKeys.get(i)).get(n+1) + " ");
            System.out.println(sortedKeys.get(i));
        }

    }

    public static void log(int acc_id, String tradeable_name) {
        for (String key : map.keySet()) {
            if ((map.get(key)).get(0).equals(Integer.toString(acc_id))) {
                int total = Integer.parseInt(trademap.get(key).get(0)) + Integer.parseInt(trademap.get(key).get(1));
                for (int i = 0; i < total; i++) {
                    String tradable = (map.get(key)).get(3 + i * 2);
                    if (tradable.equalsIgnoreCase(tradeable_name)) {
                        System.out.print(map.get(key).get(1) + " ");
                        System.out.println(key);
                    }
                }
            }
        }
    }


    public static void position(int account_id, int timestamp) {

        String trans = "";
        ArrayList<String> trans_array = new ArrayList<>();
        HashMap<String, ArrayList<Integer>> hashMap = new HashMap<>();
        ArrayList<Integer> itemlist;

        for (String key1 : map.keySet()) {
            if (map.get(key1).contains(rollback)) {
                int p = arrayList.indexOf(rollback);
                trans = arrayList.get(p + 1);
                trans_array.add(trans);
            }
        }

        for (String key : unitmap.keySet()) {
            if ((unitmap.get(key)).get(0).equals(Integer.toString(account_id))
                    && Integer.valueOf(key) <= timestamp) {
                if (!unitmap.get(key).contains(trans)) {

                    int total = Integer.parseInt(trademap.get(key).get(0)) + Integer.parseInt(trademap.get(key).get(1));
                    for (int i = 0; i < total; i++) {
                        String tradable = unitmap.get(key).get(2 + i * 3);
                        String trade_unit = unitmap.get(key).get(3 + i * 3);
                        String trade_price = unitmap.get(key).get(4 + i * 3);
                        itemlist = new ArrayList<>();
                        int int_unit = Integer.parseInt(trade_unit.substring(1));
                        int int_price = Integer.parseInt(trade_price.substring(1));

                        int map_unit = 0;
                        int map_price = 0;

                        if (trade_unit.charAt(0) == '+')//BUY
                        {
                            if (hashMap.containsKey(tradable)) {
                                map_unit = (hashMap.get(tradable).get(0)) + int_unit;
                                map_price = (hashMap.get(tradable).get(1)) - int_price;
                            } else {
                                map_unit = int_unit;
                                map_price = -int_price;
                            }
                            itemlist.add(map_unit);
                            itemlist.add(map_price);
                        } else if (trade_unit.charAt(0) == '-')//SELL
                        {
                            if (hashMap.containsKey(tradable)) {
                                map_unit = (hashMap.get(tradable).get(0)) - int_unit;
                                map_price = (hashMap.get(tradable).get(1)) + int_price;
                            } else {
                                map_unit = -int_unit;
                                map_price = int_price;
                            }
                            itemlist.add(map_unit);
                            itemlist.add(map_price);
                        }
                        hashMap.put(tradable, itemlist);

                    }


                }
            }
        }

        for (String key : hashMap.keySet()) {
            System.out.print(key + " ");
            if (hashMap.get(key).get(0) > 0)
                System.out.print("(+)" + abs(hashMap.get(key).get(0)) + " ");
            else
                System.out.print("(-)" + abs(hashMap.get(key).get(0)) + " ");
            if (hashMap.get(key).get(1) > 0)
                System.out.println("(+)" + abs(hashMap.get(key).get(1)));
            else
                System.out.println("(-)" + abs(hashMap.get(key).get(1)));
        }

    }


    public static void mVolatile(int account_id, int timestamp1, int timestamp2) {
        HashMap<String, Integer> hashMap = new HashMap<>();

        for (String key : map.keySet()) {
            if ((map.get(key)).get(0).equals(Integer.toString(account_id))
                    && Integer.valueOf(key) >= timestamp1
                    && Integer.valueOf(key) <= timestamp2) {
                int total = Integer.parseInt(trademap.get(key).get(0)) + Integer.parseInt(trademap.get(key).get(1));
                for (int i = 0; i < total; i++) {
                    String tradable = (map.get(key)).get(3 + i * 2);
                    String adder = map.get(key).get(4 + i * 2);
                    int amount = 0;
                    int req = Integer.parseInt(adder.substring(1));
                    if (adder.charAt(0) == '+') {
                        if (hashMap.containsKey(tradable))
                            amount = hashMap.get(tradable) + req;
                        else
                            amount = req;
                    } else if (adder.charAt(0) == '-') {
                        if (hashMap.containsKey(tradable))
                            amount = hashMap.get(tradable) - req;
                        else
                            amount = -req;
                    }
                    hashMap.put(tradable, amount);
                }
            }
        }
        for (String key : hashMap.keySet()) {
            System.out.print(key + " ");
        }
        System.out.println();


    }


    public static void triage(int k, int acc_id, String tradeable_name, int timestamp1, int timestamp2) {

        HashMap<String, ArrayList> transmap = new HashMap<>();
        ArrayList<String> translist;
        int count = 0;
        for (String key : map.keySet()) {
            if ((map.get(key)).get(0).equals(Integer.toString(acc_id))
                    && Integer.valueOf(key) >= timestamp1
                    && Integer.valueOf(key) <= timestamp2) {
                int total = Integer.parseInt(trademap.get(key).get(0)) + Integer.parseInt(trademap.get(key).get(1));
                for (int i = 0; i < total; i++) {
                    String tradable = (map.get(key)).get(3 + i * 2);
                    if (tradable.equalsIgnoreCase(tradeable_name)) {
                        translist = new ArrayList<>();
                        translist.add(map.get(key).get(2));
                        translist.add(map.get(key).get(1));
                        String trans_key = (map.get(key).get(4 + i * 2)).substring(1);
                        transmap.put(trans_key, translist);
                        count++;
                    }
                }
            }
        }
        String result[][] = new String[count][2];
        int i = 0;
        for (String key : transmap.keySet()) {
            result[i][0] = transmap.get(key).get(0).toString();
            result[i][1] = transmap.get(key).get(1).toString();
            i++;

        }
        for (int j = result.length - 1; j >= result.length - k; j--) {
            System.out.print(result[j][0] + " ");
            System.out.println(result[j][1]);
        }
    }

    public static void merge(int acc_id1, int acc_id2) {


        String trans = "";
        HashMap<String, ArrayList<Integer>> hashMap = new HashMap<>();
        ArrayList<Integer> itemlist;

        for (String key : map.keySet()) {
            if (map.get(key).contains(rollback)) {
                int p = arrayList.indexOf(rollback);
                trans = arrayList.get(p + 1);
            }
        }

        ArrayList<String> keyList = new ArrayList<>();

        for (String key : map.keySet()) {
            if ((map.get(key)).contains(Integer.toString(acc_id1)) || (map.get(key)).contains(Integer.toString(acc_id2))) {
                keyList.add(key);
            }
        }

        for (String key : keyList) {
            if ((unitmap.get(key)).get(0).equals(Integer.toString(acc_id1)) ||
                    (unitmap.get(key)).get(0).equals(Integer.toString(acc_id2))) {
                if (!unitmap.get(key).contains(trans)) {

                    int total = Integer.parseInt(trademap.get(key).get(0)) + Integer.parseInt(trademap.get(key).get(1));
                    for (int i = 0; i < total; i++) {
                        String tradable = unitmap.get(key).get(2 + i * 3);
                        String trade_unit = unitmap.get(key).get(3 + i * 3);
                        String trade_price = unitmap.get(key).get(4 + i * 3);
                        itemlist = new ArrayList<>();
                        int int_unit = Integer.parseInt(trade_unit.substring(1));
                        int int_price = Integer.parseInt(trade_price.substring(1));

                        int map_unit = 0;
                        int map_price = 0;

                        if (trade_unit.charAt(0) == '+')//BUY
                        {
                            if (hashMap.containsKey(tradable)) {
                                map_unit = (hashMap.get(tradable).get(0)) + int_unit;
                                map_price = (hashMap.get(tradable).get(1)) - int_price;
                            } else {
                                map_unit = int_unit;
                                map_price = -int_price;
                            }
                            itemlist.add(map_unit);
                            itemlist.add(map_price);
                        } else if (trade_unit.charAt(0) == '-')//SELL
                        {
                            if (hashMap.containsKey(tradable)) {
                                map_unit = (hashMap.get(tradable).get(0)) - int_unit;
                                map_price = (hashMap.get(tradable).get(1)) + int_price;
                            } else {
                                map_unit = -int_unit;
                                map_price = int_price;
                            }
                            itemlist.add(map_unit);
                            itemlist.add(map_price);
                        }
                        hashMap.put(tradable, itemlist);

                    }


                }
            }
        }

        for (String key : hashMap.keySet()) {
            System.out.print(key + " ");
            if (hashMap.get(key).get(0) > 0)
                System.out.print("(+)" + abs(hashMap.get(key).get(0)) + " ");
            else
                System.out.print("(-)" + abs(hashMap.get(key).get(0)) + " ");
            if (hashMap.get(key).get(1) > 0)
                System.out.println("(+)" + abs(hashMap.get(key).get(1)));
            else
                System.out.println("(-)" + abs(hashMap.get(key).get(1)));
        }


    }

    public static void alert(int timestamp1, int timestamp2) {

        ArrayList<String> traderlist = new ArrayList<>();
        for (String key : map.keySet()) {
            if (Integer.valueOf(key) >= timestamp1 && Integer.valueOf(key) <= timestamp2) {
                int rollback = Integer.parseInt(trademap.get(key).get(2));
                if (rollback != 0) {
                    traderlist.add(map.get(key).get(2));
                }
            }
        }

        Collections.sort(traderlist);
        for (int i = 0; i < traderlist.size(); i++) {
            System.out.print(traderlist.get(i));
        }
        System.out.println();
    }

}