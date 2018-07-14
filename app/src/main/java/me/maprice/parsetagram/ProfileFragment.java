package me.maprice.parsetagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.maprice.parsetagram.model.Post;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment{

    private ImageButton profileImage;
    private TextView profileName;
    FeedAdapter feedAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer;
    private String id;
    private String imagePath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.profile_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //find the recycler view
        rvPosts = (RecyclerView) getActivity().findViewById(R.id.recycle_view);
        //initialize data source
        posts = new ArrayList<>();
        //construct adapter from this data source
        feedAdapter = new FeedAdapter(posts); // when the post was clicked i would then call callback.onDisplayDetails(post);
        //RecyclerView setup (layout manager, use adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        //set adapter
        rvPosts.setAdapter(feedAdapter);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeView);

        profileImage = (ImageButton) getActivity().findViewById(R.id.image);
        profileName = (TextView) getActivity().findViewById(R.id.username);



        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLaunchCamera();

            }
        });

        final ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile file = currentUser.getParseFile("ProfileImage");

        try {
            profileName.setText(currentUser.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        try {
            id  = currentUser.fetchIfNeeded().getObjectId().toString();
            Log.i("test", "test");
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        Glide.with(getContext())
//                .load(file.getUrl())
//                .apply(RequestOptions.fitCenterTransform().circleCrop())
//                .into(profileImage);

        Uri url = getUrl(R.drawable.instagram_user_outline_24);
        try {
            ParseFile profImage = currentUser.fetchIfNeeded().getParseFile("ProfileImage");
            Glide.with(getContext())
                    .load(profImage.getUrl())
                    .apply(
                            RequestOptions.fitCenterTransform()
                                    .error(R.drawable.instagram_user_outline_24)
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);

        } catch (Exception e) {
            Log.d("FeedAdapter", "No Profile Image");
            Glide.with(getContext())
                    .load(new File(url.getPath()))
                    .apply(
                            RequestOptions.fitCenterTransform()
                                    .error(R.drawable.instagram_user_outline_24)
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }


        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                Log.d("Feed Fragment", "Swipe Container refreshed");
                // Make sure you call swipeContainer.setRefreshing(false)
                refresh(currentUser);

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_purple, android.R.color.holo_blue_light,
                R.color.colorAccent);

        generateFeed(currentUser);

    }

    public void generateFeed(ParseUser user) {
        Post.Query query = new Post.Query();
        query.getTop();
        query.whereEqualTo("user", user);
        // Specify the object id
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objectList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objectList.size(); i++) {
                        posts.add(0,objectList.get(i));
                        feedAdapter.notifyItemInserted(0);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public static Uri getUrl(int res){
        return Uri.parse("android.resource://me.maprice.parsetagram/" + res);
    }

    public void refresh(ParseUser user){

        //pb.setVisibility(ProgressBar.VISIBLE);
        feedAdapter.clear();
        posts.clear();
        // Specify which class to query
        Post.Query query = new Post.Query();
        query.getTop();
        query.whereEqualTo("user", user);
        // Specify the object id
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objectList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objectList.size(); i++) {
                        posts.add(0,objectList.get(i));
                        feedAdapter.notifyItemInserted(0);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        feedAdapter.addAll(posts);
        swipeContainer.setRefreshing(false);
        //pb.setVisibility(ProgressBar.INVISIBLE);
    }

    public final String APP_TAG = "Parsetagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 210;
    public String photoFileName = "photo.jpg";
    File photoFile;

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "me.maprice.parsetagram", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        imagePath = mediaStorageDir.getPath() + File.separator + fileName;
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(takenImage, 246, 150, true);
                Drawable d = new BitmapDrawable(getResources(), scaledBitmap);
                profileImage.setBackground(d);
                final ParseUser user = ParseUser.getCurrentUser();
                final File file = photoFile;
                final ParseFile image = new ParseFile(file);
                image.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            user.put("ProfileImage", image);
                            Log.d("ProfileFragment", "New Profile Image saved");
                        } else {
                            e.printStackTrace();
                        }
                    }
                });

            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
