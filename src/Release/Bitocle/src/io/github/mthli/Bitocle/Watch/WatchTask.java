package io.github.mthli.Bitocle.Watch;

import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.WatcherService;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class WatchTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;

    private WatcherService watcherService;

    private WatchItemAdapter watchItemAdapter;
    private List<WatchItem> watchItemList;

    public WatchTask(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        mainFragment.setRefreshStatus(true);
        context = mainFragment.getContentView().getContext();

        GitHubClient gitHubClient = mainFragment.getGitHubClient();
        watcherService = new WatcherService(gitHubClient);

        watchItemAdapter = mainFragment.getWatchItemAdapter();
        watchItemList = mainFragment.getWatchItemList();

        mainFragment.setContentShown(false);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        PageIterator<Repository> pageIterator = watcherService.pageWatched(20);
        Iterator<Repository> iterator = pageIterator.next().iterator();

        if (isCancelled()) {
            return false;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        watchItemList.clear();
        while (iterator.hasNext()) {
            Repository r = iterator.next();
            String date = simpleDateFormat.format(r.getCreatedAt());

            String lang;
            if (r.getLanguage() == null) {
                lang = context.getString(R.string.repo_lang_unknown);
            } else {
                lang = r.getLanguage();
            }

            String info = lang
                    + "   "
                    + context.getString(R.string.watch_item_info_star) + " " + r.getWatchers()
                    + "   "
                    + context.getString(R.string.watch_item_info_fork) + " " + r.getForks();
            watchItemList.add(
                    new WatchItem(
                            context.getResources().getDrawable(R.drawable.ic_type_repo),
                            r.getName(),
                            date,
                            r.getDescription(),
                            info,
                            r.getOwner().getLogin(),
                            r.getGitUrl()
                    )
            );
        }

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            if (watchItemList.size() == 0) {
                mainFragment.setContentEmpty(true);
                mainFragment.setEmptyText(R.string.watch_empty_list);
                mainFragment.setContentShown(true);
            } else {
                mainFragment.setContentEmpty(false);
                watchItemAdapter.notifyDataSetChanged();
                mainFragment.setContentShown(true);
            }
        } else {
            mainFragment.setContentEmpty(true);
            mainFragment.setEmptyText(R.string.watch_empty_get_data_failed);
            mainFragment.setContentShown(true);
        }
        mainFragment.setRefreshStatus(false);
    }
}
