package me.maprice.parsetagram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.gifbitmap.GifBitmapWrapper;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.io.File;
import java.util.List;

import me.maprice.parsetagram.model.Post;
import me.maprice.parsetagram.model.User;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>  {

    List<Post> mPosts;
    Context context;
    ProgressBar pb;

    // create a callback here.
    // that would have a method called onPostClicked(int position, Post post)


    // I would then have a instance variable for my callback

    //pass in the Tweets array in the constructor
    public FeedAdapter(List<Post> posts){
        mPosts = posts;
    }

    // for each row, inflate the layout and cache references into ViewHolder class
    @NonNull
    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.insta_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    //bind values based on the position of the element
    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        //get data according to position
        Post post = mPosts.get(position);
        ParseUser user = post.getUser();

        try {
            holder.tvUsername.setText(user.fetchIfNeeded().getUsername());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvBody.setText(post.getDescription());
        holder.tvTimeCreated.setText(post.getCreatedAt().toString());
        Log.d("Testing date", post.getCreatedAt().toString());

//        int radius = 50; // corner radius, higher value = more rounded
//        int margin = 10; // crop margin, set to 0 for corners with no crop

        Uri url = getUrl(R.drawable.instagram_user_outline_24);
        try {
            ParseFile profImage = user.fetchIfNeeded().getParseFile("ProfileImage");
            Glide.with(context)
                    .load(profImage.getUrl())
                    .error(R.drawable.instagram_user_outline_24)
                    .fitCenter()
                    .into(holder.tvProfileImage);

        } catch (Exception e) {
            Log.d("FeedAdapter", "No Profile Image");
            Glide.with(context)
                    .load(new File(url.getPath()))
                    .error(R.drawable.instagram_user_outline_24)
                    .fitCenter()
                    .into(holder.tvProfileImage);
        }

        Glide.with(context)
                .load(post.getImage().getUrl())
                .fitCenter()
                .centerCrop()
                .into(holder.tvBodyImage);

    }

    public static Uri getUrl(int res){
        return Uri.parse("android.resource://me.maprice.parsetagram/" + res);
    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvUsername;
        public TextView tvBody;
        public ImageView tvBodyImage;
        public ImageView tvProfileImage;
        public TextView tvTimeCreated;


        public ViewHolder(View itemView) {
            super(itemView);

            //perform view by id lookups
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvBody = (TextView) itemView.findViewById(R.id.tvDescription);
            tvBodyImage = (ImageView) itemView.findViewById(R.id.tvImage);
            tvProfileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            tvTimeCreated = (TextView) itemView.findViewById(R.id.dateCreated);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            //item position
            int position = getAdapterPosition();
            //make sure position exists in view
            if (position != RecyclerView.NO_POSITION){
                //get post
                Post post = mPosts.get(position);
                //create intent
                Intent intent = new Intent(context, DetailsActivity.class);
                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                // show the activity
                context.startActivity(intent);
            }

        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }

}
