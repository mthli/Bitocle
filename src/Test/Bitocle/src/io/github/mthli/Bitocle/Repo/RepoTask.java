package io.github.mthli.Bitocle.Repo;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.SimpleAdapter;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import io.github.mthli.Bitocle.Database.Repo.RAction;
import io.github.mthli.Bitocle.Database.Repo.Repo;
import io.github.mthli.Bitocle.Main.Flag;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class RepoTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment fragment;
    private Context context;
    private int flag;

    private RepoItemAdapter adapter;
    private List<RepoItem> list;

    private RepositoryService service;

    public RepoTask(MainFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        context = fragment.getContentView().getContext();
        flag = fragment.getFlag();

        adapter = fragment.getRepoItemAdapter();
        list = fragment.getRepoItemList();

        GitHubClient client = fragment.getClient();
        service = new RepositoryService(client);

        if (flag == Flag.REPO_FIRST) {
            fragment.setContentEmpty(false);
            fragment.setContentShown(false);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (flag == Flag.REPO_FIRST) {
            RAction action = new RAction(context);
            try {
                action.openDatabase(true);
            } catch (SQLException s) {
                action.closeDatabase();
                return false;
            }

            List<Repository> repositories;
            try {
                repositories = service.getRepositories();
            } catch (IOException i) {
                action.closeDatabase();
                return false;
            }

            if (isCancelled()) {
                action.closeDatabase();
                return false;
            }

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            if (flag == Flag.REPO_FIRST) {
                for (Repository r : repositories) {
                    if (!action.checkRepo(r.getGitUrl())) {
                        Repo repo = new Repo();
                        repo.setName(r.getName());
                        repo.setDate(format.format(r.getCreatedAt()));
                        repo.setDescription(r.getDescription());
                        repo.setLang(r.getLanguage());
                        repo.setStar(r.getWatchers());
                        repo.setFork(r.getForks());
                        repo.setOwner(r.getOwner().getLogin());
                        repo.setGit(r.getGitUrl());
                        action.addRepo(repo);
                    }
                }
            }
            action.closeDatabase();
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
            RAction action = new RAction(context);
            try {
                action.openDatabase(true);
            } catch (SQLException s) {
                fragment.setContentEmpty(true);
                fragment.setEmptyText(R.string.repo_empty_error);
                fragment.setContentShown(true);
                return;
            }

            List<Repo> repos = action.listRepos();
            Collections.sort(repos);

            List<Map<String, String>> autoList = new ArrayList<Map<String, String>>();

            list.clear();
            autoList.clear();
            for (Repo r : repos) {
                list.add(
                        new RepoItem(
                                r.getName(),
                                r.getDate(),
                                r.getDescription(),
                                r.getLang(),
                                r.getStar(),
                                r.getFork(),
                                r.getOwner(),
                                r.getGit()
                        )
                );
                Map<String, String> map = new HashMap<String, String>();
                map.put("owner", r.getOwner());
                map.put("name", r.getName());
                autoList.add(map);
            }
            action.closeDatabase();

            SimpleAdapter autoAdapter = new SimpleAdapter(
                    context,
                    autoList,
                    R.layout.auto_item,
                    new String[] {"owner", "name"},
                    new int[] {R.id.auto_item_owner, R.id.auto_item_name}
            );
            autoAdapter.notifyDataSetChanged();
            fragment.getSearch().setAdapter(autoAdapter);

            if (list.size() == 0) {
                fragment.setContentEmpty(true);
                fragment.setEmptyText(R.string.repo_empty_list);
                fragment.setContentShown(true);
            } else {
                fragment.setContentEmpty(false);
                adapter.notifyDataSetChanged();
                fragment.setContentShown(true);
            }

            if (flag == Flag.REPO_REFRESH) {
                SuperToast.create(
                        context,
                        context.getString(R.string.repo_refresh_successful),
                        SuperToast.Duration.VERY_SHORT,
                        Style.getStyle(Style.BLUE)
                ).show();
            }
        } else {
            fragment.setContentEmpty(true);
            fragment.setEmptyText(R.string.repo_empty_error);
            fragment.setContentShown(true);

            if (flag == Flag.REPO_REFRESH) {
                SuperToast.create(
                        context,
                        context.getString(R.string.repo_refresh_failed),
                        SuperToast.Duration.VERY_SHORT,
                        Style.getStyle(Style.RED)
                ).show();
            }
        }
    }
}
