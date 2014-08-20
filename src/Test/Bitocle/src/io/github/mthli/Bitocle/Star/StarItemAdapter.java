package io.github.mthli.Bitocle.Star;

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
import io.github.mthli.Bitocle.Database.Repo.Repo;
import io.github.mthli.Bitocle.Main.Flag;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class StarItemAdapter extends ArrayAdapter<StarItem> {
    private MainFragment fragment;
    private Context context;
    private int layoutResId;
    private List<StarItem> list;

    public StarItemAdapter(
            MainFragment fragment,
            Context context,
            int layoutResId,
            List<StarItem> list
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

        final StarItem starItem = list.get(position);
        holder.icon.setImageDrawable(
                context.getResources().getDrawable(R.drawable.ic_type_repo)
        );
        holder.name.setText(starItem.getName());
        holder.date.setText(starItem.getDate());
        String description = starItem.getDescription();
        if (description.length() == 0) {
            description = context.getString(R.string.repo_empty_description);
        }
        holder.description.setText(description);
        String lang = starItem.getLang();
        if (lang == null) {
            lang = context.getString(R.string.repo_item_unknown);
        }
        holder.info.setText(lang
                        + "   "
                        + context.getString(R.string.repo_item_star)
                        + " " + starItem.getStar()
                        + "   "
                        + context.getString(R.string.repo_item_fork)
                        + " " + starItem.getFork()
        );
        holder.owner.setText(starItem.getOwner());

        final PopupMenu menu = new PopupMenu(context, holder.overflow);
        menu.getMenuInflater().inflate(
                R.menu.star_item_overflow,
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
                    case R.id.star_item_overflow_commit:
                        fragment.setLocation(position);
                        fragment.changeToCommit(Flag.STAR_COMMIT_FIRST);
                        break;
                    case R.id.star_item_overflow_add:
                        RAction action = new RAction(context);
                        try {
                            action.openDatabase(true);
                        } catch (SQLException s) {
                            action.closeDatabase();
                            SuperToast.create(
                                    context,
                                    context.getString(R.string.overflow_add_failed),
                                    SuperToast.Duration.VERY_SHORT,
                                    Style.getStyle(Style.RED)
                            ).show();
                            return false;
                        }

                        if (!action.checkRepo(starItem.getGit())) {
                            Repo repo = new Repo();
                            repo.setName(starItem.getName());
                            repo.setDate(starItem.getDate());
                            repo.setDescription(starItem.getDescription());
                            if (starItem.getLang() == null) {
                                repo.setLang(
                                        context.getString(R.string.repo_item_unknown)
                                );
                            } else {
                                repo.setLang(starItem.getLang());
                            }
                            repo.setStar(starItem.getStar());
                            repo.setFork(starItem.getFork());
                            repo.setOwner(starItem.getOwner());
                            repo.setGit(starItem.getGit());
                            action.addRepo(repo);
                        }
                        action.closeDatabase();

                        SuperToast.create(
                                context,
                                context.getString(R.string.overflow_add_successful),
                                SuperToast.Duration.VERY_SHORT,
                                Style.getStyle(Style.BLUE)
                        ).show();
                        break;
                    default:
                        break;
                }

                return true; //
            }
        });


        return view;
    }
}
