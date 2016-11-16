package com.uwaterloo.portfoliorebalancing.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

/**
 * Created by yuweixu on 2015-10-26.
 */
public class Tick extends SugarRecord<Tick> implements Parcelable {
    private double price;
    private String symbol, timestamp;
    private long simulationId;
    private int seq;

    public Tick() {}

    public Tick(String symbol, double price, String timestamp) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
    }

    public Tick(String symbol, double price, String timestamp, long simulationId, int seq) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
        this.simulationId = simulationId;
        this.seq = seq;
    }

    public int getIndex(){return seq;}
    public String getSymbol() {return symbol;}
    public String getDate() {return timestamp;}
    public double getPrice() {return price;}
    public long getSimulationId() {return simulationId;}

    public Tick(Parcel in) {
        String [] data = new String[3];
        in.readStringArray(data);

        this.price = Float.parseFloat(data[0]);
        this.symbol = data[1];
        this.timestamp = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String [] {
                this.price+"",
                this.symbol,
                this.timestamp
        });
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Tick createFromParcel(Parcel in) {
            return new Tick(in);
        }

        public Tick[] newArray(int size) {
            return new Tick[size];
        }
    };
}
