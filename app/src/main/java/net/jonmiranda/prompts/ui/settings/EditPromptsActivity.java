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

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.events.PromptUpdateEvent;
import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.storage.Storage;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class EditPromptsActivity extends ActionBarActivity implements PromptUpdateEvent.Listener {

    @Inject Storage mStorage;
    @Inject Bus mBus;

    @InjectView(R.id.list) RecyclerView mListView;
    PromptItemAdapter adapter;

    PromptApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_prompts_layout);
        ButterKnife.inject(this);
        application = ((PromptApplication) getApplication());
        application.inject(this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mListView.setLayoutManager(llm);
        adapter = new PromptItemAdapter(this, mStorage, mStorage.getAllPrompts());
        mListView.setAdapter(adapter);
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

    @Subscribe @Override
    public void onPromptChanged(PromptUpdateEvent event) {
        if (event.key == null) {
            mStorage.createPrompt(event.title, true);
        } else {
            mStorage.updatePrompt(event.key, event.title);
        }
        adapter.notifyDataSetChanged();
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
                PromptItemDialog dialog = PromptItemDialog.newInstance(null, null);
                application.inject(dialog);
                dialog.show(getFragmentManager(), PromptItemDialog.TAG);

                adapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class PromptItemAdapter extends RecyclerView.Adapter<PromptItemAdapter.PromptItemViewHolder> {

        PromptApplication application;
        EditPromptsActivity context;
        Storage storage;
        private List<Prompt> list;

        public PromptItemAdapter(EditPromptsActivity context, Storage storage, List<Prompt> list) {
            this.context = context;
            this.application = (PromptApplication) context.getApplication();
            this.storage = storage;
            this.list = list;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void onBindViewHolder(final PromptItemViewHolder view, final int position) {
            final Prompt prompt = list.get(position);
            view.title.setText(prompt.getTitle());
            view.checkBox.setChecked(prompt.getIsVisible());
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storage.save(prompt, !prompt.getIsVisible());
                    notifyDataSetChanged();
                }
            };
            view.checkBox.setOnClickListener(listener);
            view.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PromptItemDialog dialog =
                            PromptItemDialog.newInstance(prompt.getKey(), prompt.getTitle());
                    application.inject(dialog);
                    dialog.show(context.getFragmentManager(), PromptItemDialog.TAG);
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
