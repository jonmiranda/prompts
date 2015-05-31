package net.jonmiranda.prompts.ui.main;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.squareup.otto.Bus;

import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.events.DateEvent;

import java.util.Calendar;

import javax.inject.Inject;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = DatePickerFragment.class.getCanonicalName();

    @Inject Bus mBus;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        final Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        mBus.post(new DateEvent(Utils.stripDate(date)));
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
