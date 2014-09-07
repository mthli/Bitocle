package io.github.mthli.Bitocle.Content;

import android.content.Context;
import android.os.AsyncTask;
import android.view.MenuItem;
import io.github.mthli.Bitocle.Main.Flag;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.DataService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class StarContentTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment fragment;
    private Context context;
    private int flag = 0;

    private ContentItemAdapter adapter;
    private List<ContentItem> list;
    private MenuItem bookmark;

    private DataService dataService;
    private String owner;
    private String name;
    private Tree root;
    private TreeEntry entry;

    public StarContentTask(MainFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        context = fragment.getContentView().getContext();
        flag = fragment.getFlag();

        adapter = fragment.getContentItemAdapter();
        list = fragment.getContentItemList();
        bookmark = fragment.getBookmark();

        GitHubClient client = fragment.getClient();
        dataService = new DataService(client);
        owner = fragment.getOwner();
        name = fragment.getName();
        root = fragment.getRoot();
        entry = fragment.getEntry();

        if (flag == Flag.STAR_CONTENT_FIRST || flag == Flag.STAR_CONTENT_REFRESH) {
            fragment.setContentEmpty(false);
            fragment.setContentShown(false);
            bookmark.setVisible(false);
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (flag == Flag.STAR_CONTENT_FIRST || flag == Flag.STAR_CONTENT_REFRESH) {
            String master = "heads/master";
            RepositoryId id = RepositoryId.create(owner, name);

            Reference ref;
            String sha;
            if (!fragment.isToggle()) {
                try {
                    ref = dataService.getReference(id, master);
                } catch (IOException i) {
                    return false;
                }
                sha = ref.getObject().getSha();

                if (isCancelled()) {
                    return false;
                }
            } else {
                sha = fragment.getBookmarkItem().getSha();
            }

            try {
                root = dataService.getTree(id, sha, true);
            } catch (IOException i) {
                return false;
            }
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
            if (flag == Flag.STAR_CONTENT_FIRST || flag == Flag.STAR_CONTENT_REFRESH) {
                fragment.setRoot(root);
            }
            List<TreeEntry> entries = root.getTree();

            if (flag == Flag.STAR_CONTENT_FIRST || flag == Flag.STAR_CONTENT_REFRESH) {
                list.clear();
                for (TreeEntry e : entries) {
                    String[] a = e.getPath().split("/");
                    if (a.length == 1) {
                        list.add(new ContentItem(e));
                    }
                }
                Collections.sort(list);

                if (list.size() <= 0) {
                    bookmark.setVisible(false);
                    fragment.setContentEmpty(true);
                    fragment.setEmptyText(R.string.content_empty_list);
                    fragment.setContentShown(true);
                } else {
                    bookmark.setVisible(true);
                    fragment.setContentEmpty(false);
                    adapter.notifyDataSetChanged();
                    fragment.setContentShown(true);
                }
            } else {
                list.clear();
                String[] a = entry.getPath().split("/");
                for (TreeEntry e : entries) {
                    String[] r = e.getPath().split("/");
                    if ((r.length - 1 == a.length) && e.getPath().startsWith(entry.getPath())) {
                        list.add(new ContentItem(e));
                    }
                }
                Collections.sort(list);

                if (list.size() <= 0) {
                    bookmark.setVisible(false);
                    fragment.setContentEmpty(true);
                    fragment.setEmptyText(R.string.content_empty_list);
                    fragment.setContentShown(true);
                } else {
                    bookmark.setVisible(true);
                    fragment.setContentEmpty(false);
                    adapter.notifyDataSetChanged();
                    fragment.setContentShown(true);
                }
            }
        } else {
            bookmark.setVisible(false);
            fragment.setContentEmpty(true);
            fragment.setEmptyText(R.string.content_empty_error);
            fragment.setContentShown(true);
        }
    }
}
