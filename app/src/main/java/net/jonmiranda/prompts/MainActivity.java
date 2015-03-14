package net.jonmiranda.prompts;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends FragmentActivity {

    @InjectView(R.id.container) ViewPager mViewPager;
    @InjectView(R.id.date) TextView mDate;

    private PagerAdapter mPagerAdapter;

    public static final String[] PROMPTS = {
            "What three things will you focus on today?",
            "What are you looking forward to most today?",
            "How are you feeling today?",
            "What did you do today?",
            "Did anything special happen today?",
            "What is something you learned today?",
            "What made you laugh today?",
            "What three things are you grateful for today?",
    };
    private int[] mColors;

    private String mRealmDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mDate.setText(String.format("%1$tA %1$tB %1$te, %1$tY", Calendar.getInstance()));
        Calendar date = Calendar.getInstance();
        mRealmDate = String.format("%d-%d-%d", // TODO: Migrate Realm's date format
                date.get(Calendar.DATE), date.get(Calendar.MONTH), date.get(Calendar.YEAR));

        mColors = getResources().getIntArray(R.array.colors);
        mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = new PromptFragment();
                Bundle bundle = new Bundle();
                bundle.putString(PromptFragment.PROMPT_KEY, PROMPTS[position % PROMPTS.length]);
                bundle.putInt(PromptFragment.COLOR_KEY, mColors[position % mColors.length]);
                bundle.putString(PromptFragment.DATE_KEY, mRealmDate);
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
