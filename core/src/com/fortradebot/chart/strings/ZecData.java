package com.fortradebot.chart.strings;

/**
 * Created by Knyazev D.A. for resume on 20.09.2018.
 */

public class ZecData {
    private static double askDouble, bidDouble, cashCoinDouble, cashFiatDouble;
    private static String askCount, bidCount;


    public static String cash(String purse, String coin){
        if (purse != null) {
            String result = purse.substring(purse.indexOf(coin)).substring(6, purse.indexOf(","));
            purse = result.replaceAll("\"", "").replaceAll(",", "");
            cashCoinDouble = Double.parseDouble(purse);
        }
        return purse;
    }
    public static String cashRub(String purse, String fiat){
        if (purse != null) {
            String result = purse.substring(purse.indexOf(fiat)).substring(6, purse.indexOf(","));
            purse = result;
            cashFiatDouble = Double.parseDouble(purse);
        }
        return purse;
    }

    //Минимальная цена продажи
    public static String askTop(String askTop){
        if (askTop != null) {
            String result = askTop.substring(askTop.indexOf("ask_top"));
            result = result.substring(0, result.indexOf(","));
            askTop = result.replaceAll("(\\bask_top\".*?\\b)","").replaceAll("\"","");
            askDouble = Double.parseDouble(askTop);
        }
        return askTop;
    }

    //Максимальная цена покупки
    public static String bidTop(String bidTop){
        if (bidTop != null) {
            String result = bidTop.substring(bidTop.indexOf("bid_top"));
            result = result.substring(0, result.indexOf(","));
            bidTop = result.replaceAll("(\\bbid_top\".*?\\b)","").replaceAll("\"","");
            bidDouble = Double.parseDouble(bidTop);
        }
        return bidTop;
    }

    //Ордеры на продажу
    public static String ask(String ask){
        if (ask != null){
            String result = ask.substring(ask.indexOf("\"ask\":"));
            ask = result.replaceAll("]\\,\\[", "\n");
            ask = ask.substring(8, ask.length()-4);
            ask = ask.substring(0, ask.indexOf("]]"));
            askCount(ask);
        }
        return ask;
    }

    //Ордеры на покупку
    public static String bid(String bid){
        if (bid != null){
            String result = bid.substring(bid.indexOf("\"bid\":"));
            bid = result.replaceAll("]\\,\\[", "\n");
            bid = bid.substring(8, bid.length()-4);
            bidCount(bid);
        }
        return bid;
    }

    //Метод преобразует строку ордера продажи в количество продоваемых коинов
    private static String askCount(String ask){
        if (ask != null){
            askCount = ask.substring(ask.indexOf(","), ask.lastIndexOf("\",")).replace("\"", "").replace(",", "");
        }
        return askCount;
    }

    //Метод преобразует строку ордера продажи в количество продоваемых коинов
    public static String bidCount(String bid){
        if (bid != null){
            bidCount = bid.substring(bid.indexOf(","), bid.lastIndexOf("\",")).replace("\"", "").replace(",", "");
        }
        return bidCount;
    }

    public static double getAskDouble() {
        return askDouble;
    }

    public static void setAskDouble(double askDouble) {
        ZecData.askDouble = askDouble;
    }

    public static double getBidDouble() {
        return bidDouble;
    }

    public static void setBidDouble(double bidDouble) {
        ZecData.bidDouble = bidDouble;
    }

    public static String getAskCount() {
        return askCount;
    }

    public static void setAskCount(String askCount) {
        ZecData.askCount = askCount;
    }

    public static String getBidCount() {
        return bidCount;
    }

    public static void setBidCount(String bidCount) {
        ZecData.bidCount = bidCount;
    }

    public static double getCashCoinDouble() {
        return cashCoinDouble;
    }

    public static void setCashCoinDouble(double cashCoinDouble) {
        ZecData.cashCoinDouble = cashCoinDouble;
    }

    public static double getCashFiatDouble() {
        return cashFiatDouble;
    }

    public static void setCashFiatDouble(double cashFiatDouble) {
        ZecData.cashFiatDouble = cashFiatDouble;
    }
}
