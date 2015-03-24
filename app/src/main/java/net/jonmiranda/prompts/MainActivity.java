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

import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.datepicker.DatePickerFragment;
import net.jonmiranda.prompts.presenters.MainPresenter;
import net.jonmiranda.prompts.views.MainView;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends FragmentActivity implements MainView {

    @InjectView(R.id.container) ViewPager mViewPager;
    @InjectView(R.id.date) TextView mDate;

    @Inject PromptApplication mApplication;
    private MainPresenter mPresenter;

    private PagerAdapter mPagerAdapter;

    private int mPosition = 0;
    private String mRealmDate;

    public String[] mPrompts;
    private int[] mColors;

    private static final String DATE_KEY = "DATE_KEY";
    private static final String POSITION_KEY = "POSITION_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        ((PromptApplication) getApplication()).inject(this);
        if (savedInstanceState == null) {
            mPresenter = new MainPresenter(this, Calendar.getInstance());
        } else {
            Calendar date = Calendar.getInstance();
            mPosition = savedInstanceState.getInt(POSITION_KEY, 0);
            if (savedInstanceState.getSerializable(DATE_KEY) != null) {
                date = (Calendar) savedInstanceState.getSerializable(DATE_KEY);
            }
            mPresenter = new MainPresenter(this, date);
        }
        mApplication.inject(mPresenter);

        mColors = getResources().getIntArray(R.array.colors);
        mPrompts = getResources().getStringArray(R.array.prompts);

        mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = new PromptFragment();
                Bundle bundle = new Bundle();
                bundle.putString(PromptFragment.PROMPT_KEY, mPrompts[position % mPrompts.length]);
                bundle.putInt(PromptFragment.COLOR_KEY, mColors[position % mColors.length]);
                bundle.putString(PromptFragment.DATE_KEY, mRealmDate);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getCount() {
                return mPrompts.length;
            }
        };
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                mPresenter.onPageSelected();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public void showDate(Calendar date) {
        mDate.setText(Utils.getPrettyDateString(date));
        mRealmDate = Utils.getRealmDateString(date);
    }

    @Override @OnClick(R.id.date)
    public void showDatePicker() {
        DialogFragment datePickerFragment = new DatePickerFragment();
        mApplication.inject(datePickerFragment);
        datePickerFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    @Override @OnClick(R.id.arrow_left)
    public void showPreviousDate() {
        mPresenter.updateDate(-1);
    }

    @Override @OnClick(R.id.arrow_right)
    public void showNextDate() {
        mPresenter.updateDate(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(DATE_KEY, mPresenter.getDate());
        outState.putInt(POSITION_KEY, mPosition);
    }

    // http://developer.android.com/training/animation/screen-slide.html
    public static class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.90f;

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
