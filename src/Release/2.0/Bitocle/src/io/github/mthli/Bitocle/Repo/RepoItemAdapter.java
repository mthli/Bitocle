package io.github.mthli.Bitocle.Repo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.view.*;
import android.widget.*;
import io.github.mthli.Bitocle.Commit.CommitItemAdapter;
import io.github.mthli.Bitocle.Commit.CommitTask;
import io.github.mthli.Bitocle.Database.Bookmark.BAction;
import io.github.mthli.Bitocle.Database.Repo.RAction;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.Main.RefreshType;
import io.github.mthli.Bitocle.R;

import java.util.List;

public class RepoItemAdapter extends ArrayAdapter<RepoItem> {
    private MainFragment mainFragment;
    private Context context;
    private int layoutResId;
    private List<RepoItem> repoItemList;

    public RepoItemAdapter(
            MainFragment mainFragment,
            Context context,
            int layoutResId,
            List<RepoItem> repoItemList
    ) {
        super(context, layoutResId, repoItemList);

        this.mainFragment = mainFragment;
        this.context = context;
        this.layoutResId = layoutResId;
        this.repoItemList = repoItemList;
    }

    private class Holder {
        ImageView icon;
        TextView title;
        TextView date;
        TextView content;
        TextView info;
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
            LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
            view = layoutInflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.icon = (ImageView) view.findViewById(R.id.repo_item_icon);
            holder.title = (TextView) view.findViewById(R.id.repo_item_title);
            holder.date = (TextView) view.findViewById(R.id.repo_item_date);
            holder.content = (TextView) view.findViewById(R.id.repo_item_content);
            holder.info = (TextView) view.findViewById(R.id.repo_item_info);
            holder.overflow = (ImageButton) view.findViewById(R.id.repo_item_overflow);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        if (repoItemList.size() > 0) {
            final RepoItem repoItem = repoItemList.get(position);
            holder.icon.setImageDrawable(repoItem.getIcon());
            holder.title.setText(repoItem.getTitle());
            holder.date.setText(repoItem.getDate());
            holder.content.setText(repoItem.getContent());
            holder.info.setText(repoItem.getInfo());

            final PopupMenu popupMenu = new PopupMenu(context, holder.overflow);
            popupMenu.getMenuInflater().inflate(R.menu.repo_item_overflow, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    mainFragment.getActionMenu().close(true);
                    switch (menuItem.getItemId()) {
                        case R.id.repo_item_overflow_commit:
                            ListView listView = mainFragment.getListView();
                            CommitItemAdapter commitItemAdapter = mainFragment.getCommitItemAdapter();

                            ActionBar actionBar = mainFragment.getActionBar();
                            actionBar.setTitle(repoItem.getTitle());
                            actionBar.setSubtitle(context.getString(R.string.commit_label));
                            actionBar.setDisplayHomeAsUpEnabled(true);

                            listView.setAdapter(commitItemAdapter);
                            commitItemAdapter.notifyDataSetChanged();
                            mainFragment.setRefreshType(RefreshType.COMMIT_FIRST);
                            mainFragment.CURRENT_ID = MainFragment.COMMIT_ID;

                            mainFragment.setLocation(position);
                            CommitTask commitTask = new CommitTask(mainFragment, repoItem);
                            mainFragment.setCommitTask(commitTask);
                            commitTask.execute();
                            break;
                        case R.id.repo_item_overflow_remove:
                            RAction rAction = new RAction(context);
                            BAction bAction = new BAction(context);
                            try {
                                rAction.openDatabase(true);
                            } catch (SQLException s) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.repo_remove_failed),
                                        Toast.LENGTH_SHORT
                                ).show();
                                rAction.closeDatabase();
                                return false;
                            }
                            try {
                                bAction.openDatabase(true);
                            } catch (SQLException s) {
                                Toast.makeText(
                                        context,
                                        context.getString(R.string.bookmark_remove_failed),
                                        Toast.LENGTH_SHORT
                                ).show();
                                bAction.closeDatabase();
                                return false;
                            }

                            rAction.deleteRepo(repoItem.getGit());
                            String key = repoItem.getOwner() + context.getString(R.string.repo_path_root) + repoItem.getTitle();
                            bAction.unMarkByKey(key);
                            bAction.closeDatabase();
                            rAction.closeDatabase();

                            mainFragment.setRefreshType(RefreshType.REPO_ALREADY);
                            RepoTask repoTask = new RepoTask(mainFragment);
                            mainFragment.setRepoTask(repoTask);
                            repoTask.execute();

                            break;
                        default:
                            break;
                    }
                    return true;
                }
            });

            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainFragment.getActionMenu().close(true);
                    popupMenu.show();
                }
            });
        }

        return view;
    }
}
