package com.fortradebot.chart;

import com.badlogic.gdx.Game;
import com.fortradebot.chart.api_for_data.Exmo;
import com.fortradebot.chart.screen.MainScreen;

/**
 * Created by Knyazev D.A. for resume on 20.09.2018.
 */

public class Outrun extends Game {
    private MainScreen mainScreen;
    private Exmo exmo;

    @Override
    public void create() {
        exmo = new Exmo("your API key", "your API secret");
        setMainScreen();
    }

    private void setMainScreen(){
        if (mainScreen == null){
            setScreen(mainScreen = new MainScreen(this, exmo));
        }else {
            setScreen(mainScreen);
        }
    }
}
