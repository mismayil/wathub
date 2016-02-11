package webb8.wathub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import webb8.wathub.models.Post;

public class PostActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static final int PROFILE = 0;
    private static final int MESSAGES = 1;
    private static final int ALL_POSTS = 2;
    private static final int BOOK_EXCHANGE_POSTS = 3;
    private static final int CARPOOL_POSTS = 4;
    private static final int GROUP_STUDY_POSTS = 5;
    private static final int FAVORITES = 6;
    private static final int LOG_OUT = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        PostFragment.setThisActivity(this);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
            case PROFILE:
            case MESSAGES:
            case ALL_POSTS:
            case BOOK_EXCHANGE_POSTS:
            case CARPOOL_POSTS:
            case GROUP_STUDY_POSTS:
            case FAVORITES:
                fragmentManager.beginTransaction()
                    .replace(R.id.container, PostFragment.newInstance(position))
                    .commit();
                break;
            case LOG_OUT:
                ParseUser.logOut();
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case PROFILE:
                mTitle = getString(R.string.title_profile);
                break;
            case MESSAGES:
                mTitle = getString(R.string.title_messaging);
                break;
            case ALL_POSTS:
                mTitle = getString(R.string.title_all_posts);
                break;
            case BOOK_EXCHANGE_POSTS:
                mTitle = getString(R.string.title_book_exchange_posts);
                break;
            case CARPOOL_POSTS:
                mTitle = getString(R.string.title_carpool_posts);
                break;
            case GROUP_STUDY_POSTS:
                mTitle = getString(R.string.title_group_study_posts);
                break;
            case FAVORITES:
                mTitle = getString(R.string.title_favorites);
                break;
            case LOG_OUT:
                mTitle = getString(R.string.title_log_out);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.post, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * General Post Fragment
     */
    public static class PostFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static PostActivity thisActivity;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PostFragment newInstance(int sectionNumber) {
            PostFragment fragment = new PostFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PostFragment() {
        }

        public static void setThisActivity(PostActivity activity) {
            thisActivity = activity;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_post, container, false);
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            switch (sectionNumber) {
                case PROFILE:
                    break;
                case MESSAGES:
                    break;
                case ALL_POSTS:
                    ParseQuery<ParseObject> query = Post.getQuery();
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e == null) {

                            } else {
                                Toast.makeText(thisActivity.getApplicationContext(), R.string.error_loading_posts, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    break;
                case BOOK_EXCHANGE_POSTS:
                    break;
                case CARPOOL_POSTS:
                    break;
                case GROUP_STUDY_POSTS:
                    break;
                case FAVORITES:
                    break;
            }
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((PostActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}