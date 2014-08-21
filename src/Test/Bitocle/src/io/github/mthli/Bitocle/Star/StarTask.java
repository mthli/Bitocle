package io.github.mthli.Bitocle.Star;

import android.os.AsyncTask;
import android.view.MenuItem;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.WatcherService;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class StarTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment fragment;

    private StarItemAdapter adapter;
    private List<StarItem> list;

    private MenuItem bookmark;

    private WatcherService service;
    private Iterator<Repository> iterator;

    public StarTask(MainFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        adapter = fragment.getStarItemAdapter();
        list = fragment.getStarItemList();
        bookmark = fragment.getBookmark();

        GitHubClient client = fragment.getClient();
        service = new WatcherService(client);

        fragment.setContentEmpty(false);
        fragment.setContentShown(false);
        bookmark.setVisible(false);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        PageIterator<Repository> pageIterator = service.pageWatched(17);
        iterator = pageIterator.next().iterator();

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {
        /* Do nothing */
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            list.clear();
            while (iterator.hasNext()) {
                Repository r = iterator.next();
                list.add(
                        new StarItem(
                                r.getName(),
                                format.format(r.getCreatedAt()),
                                r.getDescription(),
                                r.getLanguage(),
                                r.getWatchers(),
                                r.getForks(),
                                r.getOwner().getLogin(),
                                r.getGitUrl()
                        )
                );
            }

            if (list.size() == 0) {
                bookmark.setVisible(false);
                fragment.setContentEmpty(true);
                fragment.setEmptyText(R.string.repo_empty_list);
                fragment.setContentShown(true);
            } else {
                bookmark.setVisible(true);
                fragment.setContentEmpty(false);
                adapter.notifyDataSetChanged();
                fragment.setContentShown(true);
            }
        } else {
            bookmark.setVisible(false);
            fragment.setContentEmpty(true);
            fragment.setEmptyText(R.string.repo_empty_error);
            fragment.setContentShown(true);
        }
    }
}
