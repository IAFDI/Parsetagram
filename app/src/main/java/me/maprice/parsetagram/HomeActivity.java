package me.maprice.parsetagram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import me.maprice.parsetagram.model.Post;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null){
                    for (int i = 0; i < objects.size(); i++){
                        ParseUser temp = objects.get(i).getUser();
                        Log.d("HomeActivity", "Post[" +i +"] =" + objects.get(i).getDescription()
                       + "\nusername = " + objects.get(i).getUser().getUsername());
                        Log.d("stuff", "stuff");
                    }

                }else{
                    e.printStackTrace();
                }

            }
        });
    }
}
