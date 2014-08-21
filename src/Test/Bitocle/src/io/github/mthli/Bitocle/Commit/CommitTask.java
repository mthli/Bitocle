package io.github.mthli.Bitocle.Commit;

import android.os.AsyncTask;
import io.github.mthli.Bitocle.Main.Flag;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;
import io.github.mthli.Bitocle.Repo.RepoItem;
import io.github.mthli.Bitocle.Star.StarItem;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class CommitTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment fragment;
    private int flag;

    private RepoItem repoItem;
    private StarItem starItem;

    private CommitService commitService;
    Iterator<RepositoryCommit> iterator;

    private CommitItemAdapter adapter;
    private List<CommitItem> list;

    public CommitTask(
            MainFragment fragment,
            RepoItem repoItem,
            StarItem starItem
    ) {
        this.fragment = fragment;
        this.repoItem = repoItem;
        this.starItem = starItem;
    }

    @Override
    protected void onPreExecute() {
        flag = fragment.getFlag();

        GitHubClient client = fragment.getClient();
        commitService = new CommitService(client);

        adapter = fragment.getCommitItemAdapter();
        list = fragment.getCommitItemList();

        fragment.setContentShown(false);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (flag == Flag.REPO_COMMIT_FIRST || flag == Flag.REPO_COMMIT_REFRESH) {
            RepositoryId id = RepositoryId.create(repoItem.getOwner(), repoItem.getName());
            PageIterator<RepositoryCommit> pageIterator = commitService.pageCommits(id, 17);
            iterator = pageIterator.next().iterator();
        } else {
            RepositoryId id = RepositoryId.create(starItem.getOwner(), starItem.getName());
            PageIterator<RepositoryCommit> pageIterator = commitService.pageCommits(id, 17);
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
                RepositoryCommit commit = iterator.next();
                list.add(
                        new CommitItem(
                                commit.getCommit().getAuthor().getName(),
                                format.format(commit.getCommit().getAuthor().getDate()),
                                commit.getCommit().getMessage()
                        )
                );
            }

            if (list.size() == 0) {
                fragment.setContentEmpty(true);
                fragment.setEmptyText(R.string.commit_empty_list);
                fragment.setContentShown(true);
            } else {
                fragment.setContentEmpty(false);
                adapter.notifyDataSetChanged();
                fragment.setContentShown(true);
            }
        } else {
            fragment.setContentEmpty(true);
            fragment.setEmptyText(R.string.commit_empty_error);
            fragment.setContentShown(true);
        }
    }
}
