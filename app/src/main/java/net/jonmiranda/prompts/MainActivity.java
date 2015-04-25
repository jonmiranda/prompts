package net.jonmiranda.prompts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.datepicker.DatePickerFragment;
import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.presenters.MainPresenter;
import net.jonmiranda.prompts.views.MainView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends FragmentActivity implements MainView {

    @InjectView(R.id.container) ViewPager mViewPager;
    @InjectView(R.id.date) TextView mDate;
    @InjectView(R.id.navigation) LinearLayout mNavigation;
    @InjectView(R.id.settings) ImageButton mSettings;

    @Inject PromptApplication mApplication;
    private MainPresenter mPresenter;

    private PagerAdapter mPagerAdapter;

    private int mPosition = 0;
    private Date mRealmDate;

    public String[] mPrompts;
    private int[] mColors;

    private static final String DATE_KEY = "DATE_KEY";
    private static final String POSITION_KEY = "POSITION_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        ButterKnife.inject(this);
        ((PromptApplication) getApplication()).inject(this);
        if (savedInstanceState == null) {
            mPresenter = new MainPresenter(this, Calendar.getInstance(), mApplication.hasPasscodeEnabled());
        } else {
            Calendar date = Calendar.getInstance();
            mPosition = savedInstanceState.getInt(POSITION_KEY, 0);
            if (savedInstanceState.getSerializable(DATE_KEY) != null) {
                date = (Calendar) savedInstanceState.getSerializable(DATE_KEY);
            }
            mPresenter = new MainPresenter(this, date, mApplication.hasPasscodeEnabled());
        }
        mApplication.inject(mPresenter);
        mPresenter.bind();

        mColors = getResources().getIntArray(R.array.colors);
        mPrompts = getResources().getStringArray(R.array.initial_prompts);

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
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public void initializeAdapter(final List<Prompt> prompts) {
        mPagerAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (mPresenter.showLogin()) {
                    return new PasscodeFragment().newInstance();
                }
                return PromptFragment.newInstance(prompts.get(position).getKey(),
                        mColors[position % mColors.length], mRealmDate);
            }

            @Override
            public int getCount() {
                return mPresenter.showLogin() ? 1 : prompts.size();
            }
        };
        mViewPager.setAdapter(mPagerAdapter);
    }

    @OnClick(R.id.settings)
    public void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void resetAdapter() {
        mViewPager.setAdapter(mPagerAdapter);
        if (mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showPrompts() {
        resetAdapter();
        mSettings.setVisibility(TextView.VISIBLE);
        mNavigation.setVisibility(LinearLayout.VISIBLE);
    }

    @Override
    public void showLogin() {
        resetAdapter();
        mSettings.setVisibility(TextView.INVISIBLE);
        mNavigation.setVisibility(LinearLayout.INVISIBLE);
    }

    @Override
    public void showDate(Calendar date) {
        mDate.setText(Utils.getPrettyDateString(date));
        mRealmDate = Utils.stripDate(date);
    }

    @Override @OnClick(R.id.date)
    public void showDatePicker() {
        DialogFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setCancelable(true);
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
