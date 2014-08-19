package io.github.mthli.Bitocle.Star;

import android.os.AsyncTask;
import io.github.mthli.Bitocle.Main.Flag;
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
    private int flag;

    private StarItemAdapter adapter;
    private List<StarItem> list;

    private WatcherService service;
    private Iterator<Repository> iterator;

    public StarTask(MainFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        flag = fragment.getFlag();

        adapter = fragment.getStarItemAdapter();
        list = fragment.getStarItemList();

        GitHubClient client = fragment.getClient();
        service = new WatcherService(client);

        if (flag == Flag.STAR_FIRST || flag == Flag.STAR_REFRESH) {
            fragment.setContentShown(false);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (flag == Flag.STAR_FIRST || flag == Flag.STAR_REFRESH) {
            PageIterator<Repository> pageIterator = service.pageWatched(17);
            iterator = pageIterator.next().iterator();
        }

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
                fragment.setContentEmpty(true);
                fragment.setEmptyText(R.string.repo_empty_list);
                fragment.setContentShown(true);
            } else {
                fragment.setContentEmpty(false);
                adapter.notifyDataSetChanged();
                fragment.setContentShown(true);
            }
        } else {
            fragment.setContentEmpty(true);
            fragment.setEmptyText(R.string.repo_empty_error);
            fragment.setContentShown(true);
        }
    }
}
