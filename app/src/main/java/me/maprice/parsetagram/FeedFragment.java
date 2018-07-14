package me.maprice.parsetagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import me.maprice.parsetagram.model.Post;
import me.maprice.parsetagram.model.User;

public class FeedFragment extends Fragment {

    FeedAdapter feedAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar pb;

    // create a custom callback here
    // that would have a method called onDisplayDetails(Post post)

    // I would then have an instance variable so that way I can notify the callback
    // at the appropriate time.

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        feedAdapter = new FeedAdapter(posts); // when the post was clicked i would then call callback.onDisplayDetails(post);
        //RecyclerView setup (layout manager, use adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        //set adapter
        rvPosts.setAdapter(feedAdapter);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
        //progress bar
        pb = (ProgressBar) getActivity().findViewById(R.id.progressBar);
        pb.setVisibility(ProgressBar.INVISIBLE);

        Log.d("Feed Fragment", "Swipe Container created");
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                Log.d("Feed Fragment", "Swipe Container refreshed");
                // Make sure you call swipeContainer.setRefreshing(false)
                refresh();

            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_purple, android.R.color.holo_blue_light,
                R.color.colorAccent);

        generateFeed();
    }




    public void generateFeed() {
        // Specify which class to query
        Post.Query query = new Post.Query();
        query.getTop();
        //no query conditions
        // Specify the object id
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objectList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objectList.size(); i++) {
                        posts.add(objectList.get(i));
                        feedAdapter.notifyItemInserted(objectList.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        //Collections.reverse(posts);

    }

    public void refresh(){

        pb.setVisibility(ProgressBar.VISIBLE);
        feedAdapter.clear();
        posts.clear();
        // Specify which class to query
        Post.Query query = new Post.Query();
        query.getTop();
        // Specify the object id
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objectList, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objectList.size(); i++) {
                        posts.add(objectList.get(i));
                        feedAdapter.notifyItemInserted(objectList.size()-1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
        feedAdapter.addAll(posts);
        //Collections.reverse(posts);
        swipeContainer.setRefreshing(false);
        pb.setVisibility(ProgressBar.INVISIBLE);
    }
}
