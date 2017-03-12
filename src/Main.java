import java.lang.reflect.Array;
import java.util.*;

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
    private static Map<String, ArrayList> sorted_map;
    private static HashMap<String, ArrayList<String>> map;
    private static HashMap<String, ArrayList<String>> trademap;


    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();
        m = scanner.nextInt();
        scanner.nextLine();

        map = new HashMap<>();
        trademap = new HashMap<>();
        String input[] = new String[n];
        String queries[] = new String[m];
        for (int i = 0; i < n; i++) {
            input[i] = scanner.nextLine();
        }
        for (int i = 0; i < m; i++) {
            queries[i] = scanner.nextLine();
        }

        for (int i = 0; i < n; i++) {
            System.out.println(input[i]);
        }
        for (int i = 0; i < m; i++) {
            System.out.println(queries[i]);
        }

        //TRANSACTION
        for (int i = 0; i < n; i++) {
            total = 0;
            t_buy = 0;
            t_sell = 0;
            t_rollback = 0;
            arrayList = new ArrayList<>();
            tradeList = new ArrayList<>();
            String str[] = input[i].split(" ");
            if (str[0].equalsIgnoreCase("Transaction")) {
                timestamp = Integer.parseInt(str[1]);
                arrayList.add(str[2]); //account_id
                arrayList.add(str[3]); //transaction_id
                arrayList.add(str[4]); //trader_id
                for (int j = 5; j < str.length; j++) {
                    if (str[j].equalsIgnoreCase(buy)) {
                        System.out.println(buy);
                        t_buy++;
                        arrayList.add(str[++j]); //tradeable
                        units = Integer.parseInt(str[++j]);
                        price = Integer.parseInt(str[++j]);
                        //calculate amount
                        arrayList.add("-" + Integer.toString(units * price));
                    } else if (str[j].equalsIgnoreCase(sell)) {
                        System.out.println(sell);
                        t_sell++;
                        arrayList.add(str[++j]); //tradeable
                        units = Integer.parseInt(str[++j]);
                        price = Integer.parseInt(str[++j]);
                        //calculate amount
                        arrayList.add("+" + Integer.toString(units * price));
                    } else if (str[j].equalsIgnoreCase(rollback)) {
                        //add rollback info
                        t_rollback++;
                        arrayList.add(rollback);
                        arrayList.add(str[++j]); //transaction_ID
                    }

                }
                tradeList.add(Integer.toString(t_buy));
                tradeList.add(Integer.toString(t_sell));
                tradeList.add(Integer.toString(t_rollback));
                tradeList.add(Integer.toString(total));

                map.put(Integer.toString(timestamp), arrayList);
                trademap.put(str[1], tradeList);
                System.out.println(trademap);
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

        System.out.println(map);
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
        System.out.println("Log function");
        for (String key : map.keySet()) {
            if ((map.get(key)).get(0).equals(Integer.toString(acc_id))) {
                System.out.print(map.get(key).get(1) + " ");
                System.out.println(key);
            }
        }
    }

    public static void log(int acc_id, String tradeable_name) {
        System.out.println("Log function");
        for (String key : map.keySet()) {
            if ((map.get(key)).get(0).equals(Integer.toString(acc_id))) {
                int total = Integer.parseInt(trademap.get(key).get(0)) + Integer.parseInt(trademap.get(key).get(1));
                for(int i=0; i< total; i++) {
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

        for (String key : map.keySet()){
            if ((map.get(key)).get(0).equals(Integer.toString(account_id)) && Integer.valueOf(key)<=timestamp){
                int total = Integer.parseInt(trademap.get(key).get(0)) + Integer.parseInt(trademap.get(key).get(1));

            }
        }
    }


    public static void mVolatile(int account_id, int timestamp1, int timestamp2) {
        HashMap<String, ArrayList> volmap=new HashMap<>();
        ArrayList<Integer> vollist = new ArrayList<>();
        for (String key : map.keySet()) {
            if ((map.get(key)).get(0).equals(Integer.toString(account_id))
                    && Integer.valueOf(key) >= timestamp1
                    && Integer.valueOf(key) <= timestamp2) {
                int total = Integer.parseInt(trademap.get(key).get(0)) + Integer.parseInt(trademap.get(key).get(1));
                vollist.add(total);
            }
        }
        Integer i = Collections.max(vollist);
        for (String key : map.keySet()) {
            if((trademap.get(key).get(3).equals(Integer.toString(i))))
            {
                System.out.println(	(map.get(key)).get(3) );
            }
        }

    }




    public static void triage(int k, int acc_id, String tradeable_name, int timestamp1, int timestamp2) {

        HashMap<String, ArrayList> transmap=new HashMap<>();
        ArrayList<String> translist;
        int count = 0;
        for (String key : map.keySet()) {
            if ((map.get(key)).get(0).equals(Integer.toString(acc_id))
                    && Integer.valueOf(key) >= timestamp1
                    && Integer.valueOf(key) <= timestamp2) {
                int total = Integer.parseInt(trademap.get(key).get(0)) + Integer.parseInt(trademap.get(key).get(1));
                 for(int i=0; i< total; i++){
                     String tradable = (map.get(key)).get(3 + i*2);
                     if (tradable.equalsIgnoreCase(tradeable_name)){
                         translist= new ArrayList<>();
                         translist.add(map.get(key).get(2));
                         translist.add(map.get(key).get(1));
                         String trans_key= (map.get(key).get(4 + i*2)).substring(1);
                         transmap.put(trans_key, translist);
                         count++;
                     }
                 }
            }
        }
        System.out.println("Triage Function");
        String result[][]= new String[count][2];
        int i=0;
        for (String key : transmap.keySet()){
            result[i][0]= transmap.get(key).get(0).toString();
            System.out.println(result[i][0]);
            result[i][1]= transmap.get(key).get(1).toString();
            System.out.println(result[i][1]);
            i++;

        }
        System.out.println(transmap);
        for (int j=result.length-1; j>=result.length-k; j--){
            System.out.print(result[j][0]+" ");
            System.out.println(result[j][1]);
        }
    }

    public static void merge(int acc_id1, int acc_id2) {

    }

    public static void alert(int timestamp1, int timestamp2) {

        ArrayList<String> traderlist = new ArrayList<>();
        for (String key : map.keySet()) {
            if (Integer.valueOf(key) >= timestamp1 && Integer.valueOf(key) <= timestamp2){
                int rollback = Integer.parseInt(trademap.get(key).get(2));
                if (rollback!=0) {
                    traderlist.add(map.get(key).get(2));
                }
            }
        }

        Collections.sort(traderlist);
        for (int i=0; i<traderlist.size(); i++){
            System.out.print(traderlist.get(i));
        }
        System.out.println();
    }


}