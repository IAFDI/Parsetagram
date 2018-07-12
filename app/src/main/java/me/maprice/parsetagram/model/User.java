package me.maprice.parsetagram.model;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {

    private static final String KEY_NAME = "username";
    private static final String KEY_IMAGE = "ProfileImage";
    private static final String KEY_USER = "objectId";


    public String getName() {

        return getString(KEY_NAME);
    }

    public void setName(String description){

        put(KEY_NAME, description);
    }

    public ParseFile getImage() {

        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE, image);
    }

    public String getUser() {
        return getString(KEY_USER);
    }

    public void setUser(String user){

        put(KEY_USER, user);
    }

    public static class Query extends ParseQuery<User> {
        public Query(){
            super(User.class);
        }

        public User.Query getTop(){
            setLimit(20);
            return this;
        }

        public User.Query withUser(){
            include("user");
            return this;
        }

    }
}
