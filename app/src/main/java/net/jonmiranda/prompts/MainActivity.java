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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.container) ViewPager mViewPager;

    private PagerAdapter mPagerAdapter;

    private static final String[] PROMPTS = {
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
                Fragment fragment = new PromptFragment();
                Bundle bundle = new Bundle();
                bundle.putString(PromptFragment.PROMPT_KEY, PROMPTS[position % PROMPTS.length]);
                bundle.putInt(PromptFragment.COLOR_KEY, mColors[position % mColors.length]);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getCount() {
                return PROMPTS.length;
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PromptFragment extends Fragment {

        @InjectView(R.id.prompt) TextView mPrompt;
        @InjectView(R.id.editor) EditText mEditor;
        @InjectView(R.id.footer) View mFooter;

        public static final String PROMPT_KEY = "PROMPT_KEY";
        public static final String COLOR_KEY = "COLOR_KEY";

        private Realm mRealm;
        public PromptFragment() {
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

            mRealm = Realm.getInstance(getActivity());
            return root;
        }

        /**
         * @return Today's date as a string to be used in the database.
         */
        private String getTodaysDate() {
            Calendar date = Calendar.getInstance();
            return String.format("%d-%d-%d",
                    date.get(Calendar.DATE), date.get(Calendar.MONTH), date.get(Calendar.YEAR));
        }

        private void createOrUpdatePrompt() {
            JSONObject object = new JSONObject();
            try {
                object.put("date", getTodaysDate());
                object.put("prompt", mPrompt.getText());
                object.put("key", object.getString("date") + object.getString("prompt"));
                object.put("response", mEditor.getText());
            } catch (JSONException e) {
            }
            mRealm.beginTransaction();
            mRealm.createOrUpdateObjectFromJson(Prompt.class, object);
            mRealm.commitTransaction();
        }

        /**
         * The footer takes up the rest of the Editor content area so users may expect
         * that they're clicking within the EditText.
         */
        @OnClick(R.id.footer)
        public void onFooterClicked(View view) {
            showKeyboard();
        }

        @OnTextChanged(R.id.editor)
        public void onEditorChanged(CharSequence text) {
            createOrUpdatePrompt(); // TODO: Heavy(?)
        }

        @Override
        public void onResume() {
            super.onResume();

            String date = getTodaysDate();
            String prompt = mPrompt.getText().toString();
            RealmResults<Prompt> results = mRealm.where(Prompt.class)
                    .equalTo("date", date)
                    .equalTo("prompt", prompt)
                    .equalTo("key", date + prompt)
                    .findAll();

            if (results.size() > 0) {
                mEditor.setText(results.get(0).getResponse());
                mEditor.setSelection(mEditor.getText().length());
            }

            showKeyboard();
        }

        private void showKeyboard() {
            FragmentActivity context = getActivity();
            context.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditor, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // http://developer.android.com/training/animation/screen-slide.html
    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            }
        }
    }
}
