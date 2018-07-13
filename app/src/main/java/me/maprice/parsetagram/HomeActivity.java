package me.maprice.parsetagram;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

import me.maprice.parsetagram.model.Post;

public class HomeActivity extends AppCompatActivity {

    private static final String imagePath = "/storage/self/primary/DCIM/Camera/IMG_20180710_114230.jpg";
    private EditText description;
    private Button createButton;
    private Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }


        description = (EditText) findViewById(R.id.descriptionText);
        createButton = (Button) findViewById(R.id.createButton);
        refreshButton = (Button) findViewById(R.id.refreshButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final String postDescription = description.getText().toString();
               final ParseUser user = ParseUser.getCurrentUser();


               final File file = new File(imagePath);
               final ParseFile image = new ParseFile(file);
               image.saveInBackground(new SaveCallback() {
                   @Override
                   public void done(ParseException e) {
                       if (e == null){
                           createPost(postDescription, image, user);
                       }else{
                           e.printStackTrace();
                       }
                   }
               });

            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadTopPosts();
            }
        });

    }

    private void createPost(String description, ParseFile imageFile, ParseUser user){
        final Post newPost = new Post();

        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.d("HomeActivity", "Successfully created post");
                } else {
                   e.printStackTrace();
                }
            }
        });

    }

    private void loadTopPosts(){
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
