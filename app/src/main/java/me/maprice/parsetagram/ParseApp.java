package me.maprice.parsetagram;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.Parse;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Parse.Configuration config = new Parse.Configuration.Builder(this)
                .applicationId("petrichor")
                .clientKey("rider-rider-pumpkin-spider")
                .server("http://iafdi-insta.herokuapp.com/parse")
                .build();

        Parse.initialize(config);
    }
}
