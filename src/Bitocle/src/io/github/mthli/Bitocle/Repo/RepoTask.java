package io.github.mthli.Bitocle.Repo;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.Toast;
import io.github.mthli.Bitocle.Database.Repo.RAction;
import io.github.mthli.Bitocle.Database.Repo.Repo;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.Main.RefreshType;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepoTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;
    private int refreshType = 0;
    
    private RepositoryService repositoryService;

    private RepoItemAdapter repoItemAdapter;
    private List<RepoItem> repoItemList;

    public RepoTask(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        mainFragment.setRefreshStatus(true);
        context = mainFragment.getContentView().getContext();
        refreshType = mainFragment.getRefreshType();

        GitHubClient gitHubClient = mainFragment.getGitHubClient();
        repositoryService = new RepositoryService(gitHubClient);

        repoItemAdapter = mainFragment.getRepoItemAdapter();
        repoItemList = mainFragment.getRepoItemList();

        if (refreshType == RefreshType.REPO_FIRST) {
            mainFragment.setContentShown(false);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        RAction rAction = new RAction(context);
        try {
            rAction.openDatabase(true);
        } catch (SQLException s) {
            rAction.closeDatabase();
            return false;
        }

        List<Repository> repositoryList = new ArrayList<Repository>();
        if (refreshType == RefreshType.REPO_FIRST) {
            try {
                repositoryList = repositoryService.getRepositories();
            } catch (IOException i) {
                rAction.closeDatabase();
                return false;
            }
        }

        if (isCancelled()) {
            rAction.closeDatabase();
            return false;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        repoItemList.clear();
        if (refreshType == RefreshType.REPO_FIRST) {
            for (Repository r : repositoryList) {
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
                repoItemList.add(
                        new RepoItem(
                                context.getResources().getDrawable(R.drawable.ic_type_repo),
                                r.getName(),
                                date,
                                r.getDescription(),
                                info,
                                r.getOwner().getLogin(),
                                r.getGitUrl()
                        )
                );

                if (!rAction.checkRepo(r.getGitUrl())) {
                    Repo repo = new Repo();
                    repo.setTitle(r.getName());
                    repo.setDate(date);
                    repo.setContent(r.getDescription());
                    repo.setInfo(info);
                    repo.setOwner(r.getOwner().getLogin());
                    repo.setGit(r.getGitUrl());
                    rAction.addRepo(repo);
                }
            }
        } else {
            List<Repo> repoList = rAction.listRepos();
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
        }
        Collections.sort(repoItemList);
        rAction.closeDatabase();

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
            if (refreshType == RefreshType.REPO_FIRST) {
                if (repoItemList.size() == 0) {
                    mainFragment.setContentEmpty(true);
                    mainFragment.setEmptyText(R.string.repo_empty_list);
                    mainFragment.setContentShown(true);
                } else {
                    mainFragment.setContentEmpty(false);
                    repoItemAdapter.notifyDataSetChanged();
                    mainFragment.setContentShown(true);
                }
            } else if (refreshType == RefreshType.REPO_ALREADY) {
                if (repoItemList.size() == 0) {
                    mainFragment.setContentEmpty(true);
                    mainFragment.setEmptyText(R.string.repo_empty_list);
                    mainFragment.setContentShown(true);
                } else {
                    mainFragment.setContentEmpty(false);
                    repoItemAdapter.notifyDataSetChanged();
                    mainFragment.setContentShown(true);
                }
            } else {
                if (repoItemList.size() == 0) {
                    mainFragment.setContentEmpty(true);
                    mainFragment.setEmptyText(R.string.repo_empty_list);
                    mainFragment.setContentShown(true);
                } else {
                    mainFragment.setContentEmpty(false);
                    repoItemAdapter.notifyDataSetChanged();
                    mainFragment.setContentShown(true);
                }
                Toast.makeText(
                        context,
                        R.string.repo_task_successful,
                        Toast.LENGTH_SHORT
                ).show();
            }
        } else {
            if (refreshType == RefreshType.REPO_FIRST) {
                mainFragment.setContentEmpty(true);
                mainFragment.setEmptyText(R.string.repo_empty_get_data_failed);
                mainFragment.setContentShown(true);
            } else {
                Toast.makeText(
                        context,
                        R.string.repo_task_failed,
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
        mainFragment.setRefreshStatus(false);
    }
}
