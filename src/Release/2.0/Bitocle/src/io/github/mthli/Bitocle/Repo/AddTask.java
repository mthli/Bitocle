package io.github.mthli.Bitocle.Repo;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.Toast;
import io.github.mthli.Bitocle.Database.Repo.RAction;
import io.github.mthli.Bitocle.Database.Repo.Repo;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class AddTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;
    private String query;

    private RepositoryService repositoryService;

    private RepoItemAdapter repoItemAdapter;
    private List<RepoItem> repoItemList;

    public AddTask(MainFragment mainFragment, String query) {
        this.mainFragment = mainFragment;
        this.query = query;
    }

    @Override
    protected void onPreExecute() {
        mainFragment.getPullToRefreshLayout().setRefreshing(true);

        context = mainFragment.getContentView().getContext();
        GitHubClient gitHubClient = mainFragment.getGitHubClient();
        repositoryService = new RepositoryService(gitHubClient);

        repoItemAdapter = mainFragment.getRepoItemAdapter();
        repoItemList = mainFragment.getRepoItemList();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String[] arr = query.split(context.getString(R.string.repo_path_root));
        if (arr.length < 2) {
            return false;
        }
        String repoOwner = arr[0].toLowerCase();
        String repoName = arr[1].toLowerCase();

        Repository r;
        try {
            r = repositoryService.getRepository(repoOwner, repoName);
        } catch (IOException i) {
            return false;
        }

        if (isCancelled()) {
            // mainFragment.getPullToRefreshLayout().setRefreshing(false); //
            return false;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        RAction rAction = new RAction(context);
        try {
            rAction.openDatabase(true);
        } catch (SQLException s) {
            rAction.closeDatabase();
            return false;
        }

        if (!rAction.checkRepo(r.getGitUrl())) {
            String date = simpleDateFormat.format(r.getCreatedAt());

            String lang;
            if (r.getLanguage() == null) {
                lang = context.getString(R.string.repo_lang_unknown);
            } else {
                lang = r.getLanguage();
            }

            String info = lang
                    + "   "
                    + context.getString(R.string.repo_item_info_star) + " " + r.getWatchers()
                    + "   "
                    + context.getString(R.string.repo_item_info_fork) + " " + r.getForks();

            Repo repo = new Repo();
            repo.setTitle(r.getName());
            repo.setDate(date);
            repo.setContent(r.getDescription());
            repo.setInfo(info);
            repo.setOwner(repoOwner);
            repo.setGit(r.getGitUrl());
            rAction.addRepo(repo);
        }

        List<Repo> repoList = rAction.listRepos();
        repoItemList.clear();
        for (Repo repo : repoList) {
            repoItemList.add(
                    new RepoItem(
                            context.getResources().getDrawable(R.drawable.ic_type_repo),
                            repo.getTitle(),
                            repo.getDate(),
                            repo.getContent(),
                            repo.getInfo(),
                            repo.getOwner(),
                            repo.getGit()
                    )
            );
        }
        Collections.sort(repoItemList);
        rAction.closeDatabase();

        if (isCancelled()) {
            // mainFragment.getPullToRefreshLayout().setRefreshing(false);
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
        mainFragment.getPullToRefreshLayout().setRefreshing(false);
        repoItemAdapter.notifyDataSetChanged();

        if (result) {
            mainFragment.setContentEmpty(false);
            mainFragment.setContentShown(true);
            Toast.makeText(
                    context,
                    context.getString(R.string.repo_add_successful),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            Toast.makeText(
                    context,
                    context.getString(R.string.repo_add_failed),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
