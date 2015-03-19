package net.jonmiranda.prompts;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.events.DateEvent;
import net.jonmiranda.prompts.datepicker.DatePickerFragment;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends FragmentActivity implements DateEvent.Listener {

    @InjectView(R.id.container) ViewPager mViewPager;
    @InjectView(R.id.date) TextView mDate;

    @Inject PromptApplication mApplication;
    @Inject Bus mBus;

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
        ((PromptApplication) getApplication()).inject(this);

        showDate(Calendar.getInstance());

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

    public void showDate(Calendar date) {
        mDate.setText(Utils.getPrettyDateString(date));
        mRealmDate = Utils.getRealmDateString(date);
    }

    @OnClick(R.id.date)
    public void showDatePicker() {
        DialogFragment datePickerFragment = new DatePickerFragment();
        mApplication.inject(datePickerFragment);
        datePickerFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    @Override @Subscribe
    public void onDateChanged(DateEvent event) {
        mDate.setText(event.pretty);
        mRealmDate = event.date;
    }

    @Produce
    public DateEvent produceDate() {
        return new DateEvent(mRealmDate, mDate.getText().toString());
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

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }
}
