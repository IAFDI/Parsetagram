package me.maprice.parsetagram;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import me.maprice.parsetagram.model.Post;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder>  {

    private List<Post> mPosts;
    Context context;
    ProgressBar pb;

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

        //populate the views

        //TODO - find out how to get username
        //holder.tvUsername.setText(post.getUser());
        holder.tvBody.setText(post.getDescription());

//        int radius = 50; // corner radius, higher value = more rounded
//        int margin = 10; // crop margin, set to 0 for corners with no crop

        Glide.with(context)
                .load(post.getImage())
                .into(holder.tvBodyImage);

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


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername;
        public TextView tvBody;
        public ImageView tvBodyImage;


        public ViewHolder(View itemView) {
            super(itemView);

            //perform view by id lookups
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            tvBody = (TextView) itemView.findViewById(R.id.tvDescription);
            tvBodyImage = (ImageView) itemView.findViewById(R.id.tvImage);

        }
    }

}
