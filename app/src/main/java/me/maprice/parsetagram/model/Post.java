package me.maprice.parsetagram.model;

import android.text.format.DateUtils;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.text.SimpleDateFormat;
import java.util.Locale;

@ParseClassName("POST")
public class Post extends ParseObject{
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_USER = "user";
    private static final String KEY_DATE = "createdAt";

    public Post(){
        //
    }

    public String getDate(){
        return getString(KEY_DATE);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }


    public static class Query extends ParseQuery<Post> {
        public Query(){
            super(Post.class);
        }

        public Query getTop(){
            setLimit(20);
            orderByDescending("createdAt");
            return this;
        }

        public Query withUser(){
            include("user");
            return this;
        }

    }


}
