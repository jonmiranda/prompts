package net.jonmiranda.prompts;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.container) ViewPager mViewPager;

    private PagerAdapter mPagerAdapter;

    private String[] PROMPTS = {
            "What is something you learned today?",
            "What made you laugh today?",
            "What three things will you focus on today?",
            "What am I looking forward to most today?",
            "What are three things I'm grateful for?",
            "How are you feeling today?"
    };
    private int[] mColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mColors = getResources().getIntArray(R.array.colors);
        mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = new PlaceholderFragment();
                Bundle bundle = new Bundle();
                bundle.putString(PlaceholderFragment.PROMPT_KEY, PROMPTS[position % PROMPTS.length]);
                bundle.putInt(PlaceholderFragment.COLOR_KEY, mColors[position % mColors.length]);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getCount() {
                return PROMPTS.length;
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        @InjectView(R.id.prompt) TextView mPrompt;
        @InjectView(R.id.editor) EditText mEditor;
        @InjectView(R.id.footer) View mFooter;

        public static final String PROMPT_KEY = "PROMPT_KEY";
        public static final String COLOR_KEY = "COLOR_KEY";

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View root = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.inject(this, root);
            Bundle arguments = getArguments();
            if (arguments != null) {
                mPrompt.setText(arguments.getString(PROMPT_KEY, "No prompt."));

                GradientDrawable shape = (GradientDrawable) mPrompt.getBackground();
                int color = arguments.getInt(COLOR_KEY);
                shape.setColor(color);
                shape.invalidateSelf();

                mEditor.setHighlightColor(color);
            }

            return root;
        }

        @Override
        public void onResume() {
            super.onResume();

            mFooter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: A bit hacky
                    FragmentActivity context = getActivity();
                    context.getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    InputMethodManager imm = (InputMethodManager)
                            context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(mEditor, InputMethodManager.SHOW_IMPLICIT);
                }
            });
        }
    }
}
