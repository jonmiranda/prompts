package net.jonmiranda.prompts.ui.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.presenters.EditPromptsPresenter;
import net.jonmiranda.prompts.views.EditPromptsView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditPromptsActivity extends ActionBarActivity implements EditPromptsView {

    @InjectView(R.id.list) RecyclerView mListView;
    PromptItemAdapter mAdapter;

    PromptApplication mApplication;
    EditPromptsPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_prompts_layout);
        ButterKnife.inject(this);
        mApplication = ((PromptApplication) getApplication());

        mPresenter = new EditPromptsPresenter(this);
        mApplication.inject(mPresenter);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(llm);
        mListView.setHasFixedSize(true);
        mAdapter = new PromptItemAdapter(mPresenter, mPresenter.getAllPrompts());
        mListView.setAdapter(mAdapter);
    }

    public void refreshList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    public void onPause() {
        super.onResume();
        mPresenter.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_prompt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add:
                showPromptItemDialog(null, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showPromptItemDialog(String key, String title) {
        PromptItemDialog dialog = PromptItemDialog.newInstance(key, title);
        ((PromptApplication) getApplication()).inject(dialog);
        dialog.show(getFragmentManager(), PromptItemDialog.TAG);
    }

    public static class PromptItemAdapter extends RecyclerView.Adapter<PromptItemAdapter.PromptItemViewHolder> {

        private List<Prompt> mList;
        EditPromptsPresenter mPresenter;

        public PromptItemAdapter(EditPromptsPresenter presenter, List<Prompt> list) {
            mPresenter = presenter;
            mList = list;
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        public void onBindViewHolder(final PromptItemViewHolder view, final int position) {
            final Prompt prompt = mList.get(position);
            view.title.setText(prompt.getTitle());
            view.checkBox.setChecked(prompt.getIsVisible());
            view.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onCheckBoxClicked(prompt);
                }
            });
            view.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.onPromptTitleClicked(prompt);
                }
            });
        }

        @Override
        public PromptItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View item = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.prompt_item, viewGroup, false);
            return new PromptItemViewHolder(item);
        }

        static class PromptItemViewHolder extends RecyclerView.ViewHolder {
            protected CheckBox checkBox;
            protected TextView title;

            public PromptItemViewHolder(View v) {
                super(v);
                checkBox = (CheckBox) v.findViewById(R.id.checkbox);
                title = (TextView) v.findViewById(R.id.title);
            }
        }
    }
}
