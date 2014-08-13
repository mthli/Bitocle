package io.github.mthli.Bitocle.Content;

import android.content.Context;
import android.os.AsyncTask;
import io.github.mthli.Bitocle.Main.MainFragment;
import io.github.mthli.Bitocle.Main.RefreshType;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.ContentsService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContentTask extends AsyncTask<Void, Integer, Boolean> {
    private MainFragment mainFragment;
    private Context context;
    private int refreshType = 0;

    private ContentsService contentsService;
    private RepositoryId repositoryId;
    private String repoPath;

    private ContentItemAdapter contentItemAdapter;
    private List<ContentItem> contentItemList;
    private List<List<ContentItem>> contentItemListBuffer;

    public ContentTask(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    @Override
    protected void onPreExecute() {
        mainFragment.setRefreshStatus(true);

        context = mainFragment.getContentView().getContext();

        refreshType = mainFragment.getRefreshType();

        GitHubClient gitHubClient = mainFragment.getGitHubClient();
        contentsService = new ContentsService(gitHubClient);
        String repoOwner = mainFragment.getRepoOwner();
        String repoName = mainFragment.getRepoName();
        repositoryId = RepositoryId.create(repoOwner, repoName);
        repoPath = mainFragment.getRepoPath();

        contentItemAdapter = mainFragment.getContentItemAdapter();
        contentItemList = mainFragment.getContentItemList();
        contentItemListBuffer = mainFragment.getContentItemListBuffer();

        mainFragment.setContentShown(false);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (!repoPath.equals(context.getString(R.string.repo_path_root))) {
            try {
                repoPath = URLEncoder.encode(repoPath, context.getString(R.string.content_text_encode));
            } catch (UnsupportedEncodingException u) {
                return false;
            }
        }

        List<RepositoryContents> repositoryContentsList;
        try {
            repositoryContentsList = contentsService.getContents(repositoryId, repoPath);
        } catch (IOException i) {
            return false;
        }

        if (isCancelled()) {
            return false;
        }

        contentItemList.clear();
        for (RepositoryContents contents : repositoryContentsList) {
            if (contents.getType().equals(RepositoryContents.TYPE_DIR)) {
                contentItemList.add(
                        new ContentItem(
                                context.getResources().getDrawable(R.drawable.ic_type_folder),
                                contents.getName(),
                                contents.getType(),
                                0,
                                contents.getPath(),
                                contents.getSha()
                        )
                );
            } else {
                contentItemList.add(
                        new ContentItem(
                                context.getResources().getDrawable(R.drawable.ic_type_file),
                                contents.getName(),
                                contents.getType(),
                                contents.getSize(),
                                contents.getPath(),
                                contents.getSha()
                        )
                );
            }
        }
        Collections.sort(contentItemList);

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
            if (refreshType == RefreshType.CONTENT_FIRST) {
                if (contentItemList.size() == 0) {
                    mainFragment.setContentEmpty(true);
                    mainFragment.setEmptyText(R.string.content_empty_dir);
                    mainFragment.setContentShown(true);
                } else {
                    mainFragment.setContentEmpty(false);
                    contentItemAdapter.notifyDataSetChanged();
                    mainFragment.setContentShown(true);
                }

                List<ContentItem> contentItems = new ArrayList<ContentItem>();
                for (ContentItem c : contentItemList) {
                    contentItems.add(c);
                }
                contentItemListBuffer.add(contentItems);
            } else {
                if (contentItemList.size() == 0) {
                    mainFragment.setContentEmpty(true);
                    mainFragment.setEmptyText(R.string.content_empty_dir);
                    mainFragment.setContentShown(true);
                } else {
                    mainFragment.setContentEmpty(false);
                    contentItemAdapter.notifyDataSetChanged();
                    mainFragment.setContentShown(true);
                }

                contentItemListBuffer.remove(contentItemListBuffer.size() - 1);
                List<ContentItem> contentItems = new ArrayList<ContentItem>();
                for (ContentItem c : contentItemList) {
                    contentItems.add(c);
                }
                contentItemListBuffer.add(contentItems);
            }
        } else {
            mainFragment.setContentEmpty(true);
            mainFragment.setEmptyText(R.string.content_empty_get_data_failed);
            mainFragment.setContentShown(true);
        }

        mainFragment.setRefreshStatus(false);
    }
}
