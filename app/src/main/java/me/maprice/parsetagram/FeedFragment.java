package me.maprice.parsetagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maprice.parsetagram.model.Post;
import me.maprice.parsetagram.model.User;

public class FeedFragment extends Fragment{

    FeedAdapter feedAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;
    //private SwipeRefreshLayout swipeContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.adapter_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //find the recycler view
        rvPosts = (RecyclerView) getActivity().findViewById(R.id.recycleLayout);
        //initialize data source
        posts = new ArrayList<>();
        //construct adapter from this data source
        feedAdapter = new FeedAdapter(posts);
        //RecyclerView setup (layout manager, use adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        //set adapter
        rvPosts.setAdapter(feedAdapter);
        generateFeed();

    }

    public void generateFeed(String user) {
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        //query conditions
        query.whereEqualTo("user", user);
        // Specify the object id
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objectList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objectList.size(); i++) {
                        posts.add(objectList.get(i));
                        feedAdapter.notifyItemInserted(posts.size() - 1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void generateFeed(){
        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        //no query conditions
        // Specify the object id
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objectList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objectList.size(); i++) {
                        posts.add(objectList.get(i));
                        feedAdapter.notifyItemInserted(posts.size() - 1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

    }
}
