package me.maprice.parsetagram;

import android.app.Application;
import android.content.res.Configuration;

import com.parse.Parse;
import com.parse.ParseObject;

import me.maprice.parsetagram.model.Post;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Post.class);
        final Parse.Configuration config = new Parse.Configuration.Builder(this)
                .applicationId("petrichor")
                .clientKey("rider-rider-pumpkin-spider")
                .server("http://iafdi-insta.herokuapp.com/parse")
                .build();

        Parse.initialize(config);
    }
}
