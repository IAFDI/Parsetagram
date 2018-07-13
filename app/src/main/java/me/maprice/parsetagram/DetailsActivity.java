package me.maprice.parsetagram;

import android.graphics.Movie;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.maprice.parsetagram.model.Post;

public class DetailsActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView postDescription;
    private ImageView userImage;
    private TextView userName;
    private TextView dateCreated;

    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        postImage = (ImageView) findViewById(R.id.photo);
        postDescription = (TextView) findViewById(R.id.comment);
        userImage = (ImageView) findViewById(R.id.user);
        userName = (TextView) findViewById(R.id.username);
        dateCreated = (TextView) findViewById(R.id.date);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        ParseUser user = post.getUser();

        postDescription.setText(post.getDescription());
        dateCreated.setText(post.getCreatedAt().toString());

        try {
            userName.setText(user.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Uri url = getUrl(R.drawable.instagram_user_outline_24);
        try {
            ParseFile profImage = user.fetchIfNeeded().getParseFile("ProfileImage");
            Glide.with(this)
                    .load(profImage.getUrl())
                    .apply(
                            RequestOptions.fitCenterTransform()
                                    .error(R.drawable.instagram_user_outline_24)
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(userImage);

        } catch (Exception e) {
            Log.d("FeedAdapter", "No Profile Image");
            Glide.with(this)
                    .load(new File(url.getPath()))
                    .apply(
                            RequestOptions.fitCenterTransform()
                                    .error(R.drawable.instagram_user_outline_24)
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(userImage);
        }

        Glide.with(this)
                .load(post.getImage().getUrl())
                .apply(
                        RequestOptions.bitmapTransform(
                                new RoundedCornersTransformation(10, 10))
                )
                .apply(RequestOptions.fitCenterTransform())
                .apply(RequestOptions.centerCropTransform())
                .into(postImage);

    }

    public static Uri getUrl(int res){
        return Uri.parse("android.resource://me.maprice.parsetagram/" + res);
    }
}
