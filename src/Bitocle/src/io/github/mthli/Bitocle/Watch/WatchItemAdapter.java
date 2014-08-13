package io.github.mthli.Bitocle.Watch;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.github.mthli.Bitocle.Database.Repo.RAction;
import io.github.mthli.Bitocle.Database.Repo.Repo;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;

import java.util.List;

public class WatchItemAdapter extends ArrayAdapter<WatchItem> {
    private MainFragment mainFragment;
    private Context context;
    private int layoutResId;
    private List<WatchItem> watchItemList;

    public WatchItemAdapter(
            MainFragment mainFragment,
            Context context,
            int layoutResId,
            List<WatchItem> watchItemList
    ) {
        super(context, layoutResId, watchItemList);

        this.mainFragment = mainFragment;
        this.context = context;
        this.layoutResId = layoutResId;
        this.watchItemList = watchItemList;
    }

    private class Holder {
        ImageView icon;
        TextView title;
        TextView date;
        TextView content;
        TextView info;
        ImageButton add;
    }

    @Override
    public View getView(
            final int position,
            final View convertView,
            ViewGroup viewGroup
    ) {
        Holder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            view = layoutInflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.icon = (ImageView) view.findViewById(R.id.watch_item_icon);
            holder.title = (TextView) view.findViewById(R.id.watch_item_title);
            holder.date = (TextView) view.findViewById(R.id.watch_item_date);
            holder.content = (TextView) view.findViewById(R.id.watch_item_content);
            holder.info = (TextView) view.findViewById(R.id.watch_item_info);
            holder.add = (ImageButton) view.findViewById(R.id.watch_item_add);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        if (watchItemList.size() > 0) {
            final WatchItem watchItem = watchItemList.get(position);
            holder.icon.setImageDrawable(watchItem.getIcon());
            holder.title.setText(watchItem.getTitle());
            holder.date.setText(watchItem.getDate());
            holder.content.setText(watchItem.getContent());
            holder.info.setText(watchItem.getInfo());

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainFragment.getActionMenu().close(true);

                    RAction rAction = new RAction(context);
                    try {
                        rAction.openDatabase(true);
                    } catch (SQLException s) {
                        rAction.closeDatabase();
                        return;
                    }

                    if (!rAction.checkRepo(watchItem.getGit())) {
                        Repo repo = new Repo();
                        repo.setTitle(watchItem.getTitle());
                        repo.setDate(watchItem.getDate());
                        repo.setContent(watchItem.getContent());
                        repo.setInfo(watchItem.getInfo());
                        repo.setOwner(watchItem.getOwner());
                        repo.setGit(watchItem.getGit());
                        rAction.addRepo(repo);
                    }
                    rAction.closeDatabase();

                    Toast.makeText(
                            context,
                            context.getString(R.string.watch_word_pin) + " " + watchItem.getTitle(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }

        return view;
    }
}
