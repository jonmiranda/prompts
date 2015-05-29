package net.jonmiranda.prompts.ui.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.otto.Bus;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.events.ThemeChangeEvent;

import javax.inject.Inject;

public class ColorPickerDialog extends DialogFragment {

    public static final String TAG = ColorPickerDialog.class.getCanonicalName();
    public static final String CURRENT_THEME_COLOR_KEY = "CURRENT_THEME_COLOR_KEY";

    @Inject Bus mBus;

    private int mCurrentThemeColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mCurrentThemeColor = args.getInt(CURRENT_THEME_COLOR_KEY);
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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(true);

        final RecyclerView recyclerView = new RecyclerView(getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        final ColorPickerAdapter adapter = new ColorPickerAdapter(mCurrentThemeColor,
                getResources().getIntArray(R.array.material_colors));
        recyclerView.setAdapter(adapter);
        final FrameLayout frameLayout = new FrameLayout(getActivity());
        frameLayout.addView(recyclerView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.full_spacer);
        params.topMargin = margin;
        params.leftMargin = margin;
        params.rightMargin = margin;
        recyclerView.setLayoutParams(params);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(frameLayout)
                .setTitle(getString(R.string.set_theme_color_title))
                .setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBus.post(new ThemeChangeEvent(adapter.mCurrentColor));
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
        ;
        return builder.create();
    }

    public static ColorPickerDialog newInstance(int themeColor) {
        ColorPickerDialog dialog = new ColorPickerDialog();
        Bundle args = new Bundle();
        args.putInt(CURRENT_THEME_COLOR_KEY, themeColor);
        dialog.setArguments(args);
        return dialog;
    }

    public static class ColorPickerAdapter extends RecyclerView.Adapter<ColorPickerAdapter.ViewHolder> {

        private int[] mColors;
        private int mCurrentColor;

        public ColorPickerAdapter(int currentColor, int[] colors) {
            mCurrentColor = currentColor;
            mColors = colors;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.color_picker_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            ((GradientDrawable) holder.circle.getBackground()).setColor(mColors[position]);
            if (mColors[position] == mCurrentColor) {
                holder.root.setBackgroundColor(Color.LTGRAY);
            }

            holder.circle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentColor = mColors[position];
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mColors.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            protected ImageView circle;
            protected View root;

            public ViewHolder(View view) {
                super(view);
                root = view;
                circle = (ImageView) view.findViewById(R.id.circle);
            }
        }
    }
}
