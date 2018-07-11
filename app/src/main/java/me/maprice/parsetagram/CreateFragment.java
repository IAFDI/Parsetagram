package me.maprice.parsetagram;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.maprice.parsetagram.model.Post;

import static android.app.Activity.RESULT_OK;

//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import static com.parse.ManifestInfo.getPackageManager;

public class CreateFragment extends Fragment{

    private EditText description;
    private ImageButton createButton;
    private ImageButton imageTaken;
    private String imagePath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (Build.VERSION.SDK_INT >= 23) {
//            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }
//        }
//        if (Build.VERSION.SDK_INT >= 23) {
//            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
//            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//            }
//        }
//
        description = (EditText) getActivity().findViewById(R.id.descriptionText);
        createButton = (ImageButton) getActivity().findViewById(R.id.submit);
        imageTaken = (ImageButton) getActivity().findViewById(R.id.imageButton);

        imageTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getActivity(), "Button clicked", Toast.LENGTH_LONG).show();
                //takePicture();
                onLaunchCamera();

            }
        });

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
                            imageTaken.setBackground(R.drawable.camera_shadow_fill);
                            
                        }else{
                            e.printStackTrace();
                        }
                    }
                });
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


    public final String APP_TAG = "Parsetagram";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 219;
    public String photoFileName = "photo.jpg";
    File photoFile;


    //TODO - error in intent formation

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
                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 246);

//                // Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
//                Uri resizedUri = FileProvider.getPhotoFileUri(photoFileName + "_resized");
//                File resizedFile = new File(resizedUri.getPath());
//                try {
//                    resizedFile.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                FileOutputStream fos = null;
//                try {
//                    fos = new FileOutputStream(resizedFile);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                // Write the bytes of the bitmap to file
//                fos.write(bytes.toByteArray());
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                //ImageView ivPreview = (ImageView) getActivity().findViewById(R.id.i);
                Drawable d = new BitmapDrawable(getResources(), scaledBitmap);
                imageTaken.setBackground(d);
               // Intent moveToDescription = new Intent(getContext(), DescriptionPost.class);
//                moveToDescription.putExtra("imageURL", photoFileName);
//                startActivity(moveToDescription);

            } else { // Result was a failure
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }



//    private void takePicture(){
//        Intent imageCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (imageCapture.resolveActivity(getContext().getPackageManager()) != null) {
//            File image = null;
//            try {
//                image = createImageFile();
//            } catch (IOException ex) {
//                //image creation failed
//                Log.d("Create Fragment", "Image creation failed");
//            }
//            if (image != null) {
//                Uri photoURI = FileProvider.getUriForFile(getContext(),
//                        "me.maprice.parsetagram.android.fileprovider", image);
//                imageCapture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(imageCapture, requestCode);
//            }
//
//        } else {
//            Toast.makeText(getActivity(), "Error: No Camera Available", Toast.LENGTH_LONG);
//        }
//    }
//
//    private File createImageFile() throws IOException{
//        //create an image file name
//        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(imageFileName, ".jpg.", storageDir);
//
//        photoPath = image.getAbsolutePath();
//        return image;
//    }
//
//    private void galleryAddPic() {
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(photoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        getContext().sendBroadcast(mediaScanIntent);
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == requestCode && resultCode == RESULT_OK) {
//            galleryAddPic();
//            Intent moveToDescription = new Intent(getContext(), DescriptionPost.class);
//            moveToDescription.putExtra("imageURL", photoPath);
//            startActivity(moveToDescription);
//        }
//    }


}
