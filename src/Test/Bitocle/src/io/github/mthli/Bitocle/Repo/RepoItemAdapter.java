package io.github.mthli.Bitocle.Repo;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import io.github.mthli.Bitocle.Database.Bookmark.BAction;
import io.github.mthli.Bitocle.Database.Repo.RAction;
import io.github.mthli.Bitocle.Main.Flag;
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

        final RepoItem repoItem = list.get(position);
        holder.icon.setImageDrawable(
                context.getResources().getDrawable(R.drawable.ic_type_repo)
        );
        holder.name.setText(repoItem.getName());
        holder.date.setText(repoItem.getDate());
        String description = repoItem.getDescription();
        if (description.length() == 0) {
            description = context.getString(R.string.repo_empty_description);
        }
        holder.description.setText(description);
        String lang = repoItem.getLang();
        if (lang.equals("null")) {
            context.getString(R.string.repo_item_unknown);
        }
        holder.info.setText(lang
                        + "   "
                        + context.getString(R.string.repo_item_star)
                        + " " + repoItem.getStar()
                        + "   "
                        + context.getString(R.string.repo_item_fork)
                        + " " + repoItem.getFork()
        );
        holder.owner.setText(repoItem.getOwner());

        final PopupMenu menu = new PopupMenu(context, holder.overflow);
        menu.getMenuInflater().inflate(
                R.menu.repo_item_overflow,
                menu.getMenu()
        );

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.repo_item_overflow_commit:
                        fragment.setLocation(position);
                        fragment.changeToCommit(Flag.REPO_COMMIT_FIRST);
                        break;
                    case R.id.repo_item_overflow_remove:
                        RAction rAction = new RAction(context);
                        BAction bAction = new BAction(context);
                        try {
                            rAction.openDatabase(true);
                            bAction.openDatabase(true);
                        } catch (SQLException s) {
                            rAction.closeDatabase();
                            bAction.closeDatabase();
                            SuperToast.create(
                                    context,
                                    context.getString(R.string.overflow_remove_failed),
                                    SuperToast.Duration.VERY_SHORT,
                                    Style.getStyle(Style.RED)
                            ).show();
                            return false;
                        }

                        rAction.deleteRepo(repoItem.getGit());
                        String key = repoItem.getOwner() + "/" + repoItem.getName();
                        bAction.unMarkByKey(key);
                        rAction.closeDatabase();
                        bAction.closeDatabase();

                        fragment.changeToRepo(Flag.REPO_SECOND);

                        if (position > 0) {
                            fragment.getListView().setSelection(position - 1);
                        }

                        SuperToast.create(
                                context,
                                context.getString(R.string.overflow_remove_successful),
                                SuperToast.Duration.VERY_SHORT,
                                Style.getStyle(Style.BLUE)
                        ).show();
                        break;
                    default:
                        break;
                }

                return true;
            }
        });

        return view;
    }
}
