package io.github.mthli.Bitocle.Commit;

import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;
import io.github.mthli.Bitocle.Repo.RepoItem;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

public class CommitTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;

    private RepoItem repoItem;

    private RepositoryService repositoryService;
    private CommitService commitService;

    private CommitItemAdapter commitItemAdapter;
    private List<CommitItem> commitItemList;

    public CommitTask(
            MainFragment mainFragment,
            RepoItem repoItem
    ) {
        this.mainFragment = mainFragment;
        this.repoItem = repoItem;
    }

    @Override
    protected void onPreExecute() {
        mainFragment.setContentShown(false);
        mainFragment.setRefreshStatus(true);

        context = mainFragment.getContentView().getContext();

        GitHubClient gitHubClient = mainFragment.getGitHubClient();
        repositoryService = new RepositoryService(gitHubClient);
        commitService = new CommitService(gitHubClient);

        commitItemAdapter = mainFragment.getCommitItemAdapter();
        commitItemList = mainFragment.getCommitItemList();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        Iterator<RepositoryCommit> iterator;
        try {
            Repository repository = repositoryService.getRepository(repoItem.getOwner(), repoItem.getTitle());
            PageIterator<RepositoryCommit> pageIterator = commitService.pageCommits(repository, 20);
            iterator = pageIterator.next().iterator();
        } catch (IOException i) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        commitItemList.clear();
        while (iterator.hasNext()) {
            RepositoryCommit commit = iterator.next();

            String date = simpleDateFormat.format(commit.getCommit().getAuthor().getDate());
            commitItemList.add(
                    new CommitItem(
                            context.getResources().getDrawable(R.drawable.ic_action_push),
                            commit.getCommit().getAuthor().getName(),
                            date,
                            commit.getCommit().getMessage()
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
            if (commitItemList.size() == 0) {
                mainFragment.setContentEmpty(true);
                mainFragment.setEmptyText(R.string.commit_empty_list);
                mainFragment.setContentShown(true);
            } else {
                mainFragment.setContentEmpty(false);
                commitItemAdapter.notifyDataSetChanged();
                mainFragment.setContentShown(true);
            }
        } else {
            mainFragment.setContentEmpty(true);
            mainFragment.setEmptyText(R.string.commit_empty_get_data_failed);
            mainFragment.setContentShown(true);
        }
        mainFragment.setRefreshStatus(false);
    }
}
