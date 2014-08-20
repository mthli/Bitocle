package io.github.mthli.Bitocle.Repo;

import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import io.github.mthli.Bitocle.Database.Repo.RAction;
import io.github.mthli.Bitocle.Database.Repo.Repo;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AddTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment fragment;
    private Context context;
    private String query;

    private RepositoryService service;

    private PullToRefreshLayout pull;

    private ListView listView;
    private RepoItemAdapter adapter;
    private List<RepoItem> list;

    private String git;

    public AddTask(MainFragment fragment, String query) {
        this.fragment = fragment;
        this.query = query;
    }

    @Override
    protected void onPreExecute() {
        context = fragment.getContentView().getContext();

        GitHubClient client = fragment.getClient();
        service = new RepositoryService(client);

        pull = fragment.getPull();

        listView = fragment.getListView();
        adapter = fragment.getRepoItemAdapter();
        list = fragment.getRepoItemList();

        pull.setRefreshing(true);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        String[] arr = query.split("/");
        if (arr.length < 2) {
            return false;
        }
        String owner = arr[0].toLowerCase();
        String name = arr[1].toLowerCase();

        Repository r;
        try {
            r = service.getRepository(owner, name);
        } catch (IOException i) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        RAction action = new RAction(context);
        try {
            action.openDatabase(true);
        } catch (SQLException s) {
            action.closeDatabase();
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
            git = r.getGitUrl();
            action.addRepo(repo);
        }

        action.closeDatabase();

        if (isCancelled()) {
            return false;
        }
        return true;
    }

    @Override
    protected void onCancelled() {
        pull.setRefreshing(false);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        /* Do nothing */
    }

    @Override
    protected void onPostExecute(Boolean result) {
        pull.setRefreshing(false);

        if (result) {
            RAction action = new RAction(context);
            try {
                action.openDatabase(true);
            } catch (SQLException s) {
                SuperToast.create(
                        fragment.getActivity(),
                        context.getString(R.string.repo_add_failed),
                        SuperToast.Duration.VERY_SHORT,
                        Style.getStyle(Style.RED)
                ).show();

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
                int position = 0;
                for (RepoItem r : list) {
                    if (r.getGit().equals(git)) {
                        break;
                    }
                    position++;
                }
                fragment.setContentEmpty(false);
                adapter.notifyDataSetChanged();
                fragment.setContentShown(true);
                listView.smoothScrollToPosition(position);
                SuperToast.create(
                        fragment.getActivity(),
                        context.getString(R.string.repo_add_successful),
                        SuperToast.Duration.VERY_SHORT,
                        Style.getStyle(Style.BLUE)
                ).show();
            }
        } else {
            SuperToast.create(
                    fragment.getActivity(),
                    context.getString(R.string.repo_add_failed),
                    SuperToast.Duration.VERY_SHORT,
                    Style.getStyle(Style.RED)
            ).show();
        }
    }
}
