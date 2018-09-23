package com.fortradebot.chart.statistic;

import java.util.Date;

/**
 * Created by Knyazev D.A. for resume on 20.09.2018.
 */

public class ZecStats {
    private String date;//Время
    private double ask, askUsd, askRub; //Минимальная цена продажи
    private double bid; //Максимальная цена закупки

    public void zecStatsParameter(double ask, double bid, String currency){
        this.ask = ask;
        this.bid = bid;

        Date date = new Date(System.currentTimeMillis());
        setDate(date.toString());
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }
}
