package me.maprice.parsetagram;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Fragment> fragments = new ArrayList<>();
    private ViewPager viewPager;
    private ExampleAdapter adapter;
    private BottomNavigationView bottomNavigation;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the placeholder fragments to be passed to the ViewPager
        fragments.add(new FeedFragment());
        fragments.add(new CreateFragment());
        fragments.add(new NotYetImplementedFragment());
        fragments.add(new NotYetImplementedFragment());

        // Grab a reference to our view pager.
        viewPager = findViewById(R.id.view);

        // Instantiate our ExampleAdapter which we will use in our ViewPager
        adapter = new ExampleAdapter(getSupportFragmentManager(), fragments);
        // Attach our adapter to our view pager.
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigation.setSelectedItemId(R.id.homeFeed);
                        break;
                    case 1:
                        bottomNavigation.setSelectedItemId(R.id.create);
                        break;
                    case 2:
                        bottomNavigation.setSelectedItemId(R.id.user);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Grab a reference to our bottom navigation view
        bottomNavigation = findViewById(R.id.bottom_navigation);

        //grab reference to other navigation view
        navigationView = findViewById(R.id.logout_menu);

        // Handle the click for each item on the bottom navigation view.
        // we then delegate this out to the view pager adapter such that it can switch the
        // page which we are currently displaying
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                 switch (menuItem.getItemId()) {
                     case R.id.homeFeed:
                         viewPager.setCurrentItem(0);
                         return true;
                     case R.id.create:
                         viewPager.setCurrentItem(1);
                         return true;
                     case R.id.user:
                         viewPager.setCurrentItem(2);
                         return true;
                 }
                 return false;
             }
         });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //Toast.makeText(getBaseContext(), "Logged out", Toast.LENGTH_SHORT).show();
                Log.d("Main Activity", "Logged out");
                logOut();
                Intent i = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            }
        });


    }


    public void logOut(){
        ParseUser.logOut();
    }

    static class ExampleAdapter extends FragmentStatePagerAdapter {

        /**
         * The list of fragments which we are going to be displaying in the view pager.
         */
        private final List<Fragment> fragments;

        public ExampleAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);

            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
