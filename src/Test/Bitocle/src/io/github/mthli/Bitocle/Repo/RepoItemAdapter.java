package io.github.mthli.Bitocle.Repo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;

import java.util.List;

public class RepoItemAdapter extends ArrayAdapter<RepoItem> {
    private MainFragment fragment;
    private Context context;
    private int layoutResId;
    private List<RepoItem> list;

    public RepoItemAdapter(
            MainFragment fragment,
            Context context,
            int layoutResId,
            List<RepoItem> list
    ) {
        super(context, layoutResId, list);

        this.fragment = fragment;
        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;
    }

    private class Holder {
        ImageView icon;
        TextView name;
        TextView date;
        TextView description;
        TextView info;
        TextView owner;
        ImageButton overflow;
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
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.icon = (ImageView) view.findViewById(R.id.repo_item_icon);
            holder.name = (TextView) view.findViewById(R.id.repo_item_name);
            holder.date = (TextView) view.findViewById(R.id.repo_item_date);
            holder.description = (TextView) view.findViewById(R.id.repo_item_description);
            holder.info = (TextView) view.findViewById(R.id.repo_item_info);
            holder.owner = (TextView) view.findViewById(R.id.repo_item_owner);
            holder.overflow = (ImageButton) view.findViewById(R.id.repo_item_overflow);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        RepoItem item = list.get(position);
        holder.icon.setImageDrawable(
                context.getResources().getDrawable(R.drawable.ic_type_repo)
        );
        holder.name.setText(item.getName());
        holder.date.setText(item.getDate());
        String description = item.getDescription();
        if (description.length() == 0) {
            description = context.getString(R.string.repo_empty_description);
        }
        holder.description.setText(description);
        String lang = item.getLang();
        if (lang.equals("null")) { //
            context.getString(R.string.repo_item_unknown);
        }
        holder.info.setText(lang
                        + "   "
                        + context.getString(R.string.repo_item_star)
                        + " " + item.getStar()
                        + "   "
                        + context.getString(R.string.repo_item_fork)
                        + " " + item.getFork()
        );
        holder.owner.setText(item.getOwner());

        final PopupMenu menu = new PopupMenu(context, holder.overflow);
        menu.getMenuInflater().inflate(
                R.menu.repo_item_overflow,
                menu.getMenu()
        );
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                /* Do something */
                return false;
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });

        return view;
    }
}
