package com.uwaterloo.portfoliorebalancing;

import com.facebook.stetho.Stetho;
import com.orm.SugarApp;

/**
 * Created by jocye on 2016-06-02.
 */
public class ProtfolioRebalancingApp extends SugarApp {
    public void onCreate(){
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
