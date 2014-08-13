package io.github.mthli.Bitocle.Main;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.*;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import io.github.mthli.Bitocle.Bookmark.BookmarkItem;
import io.github.mthli.Bitocle.Bookmark.BookmarkItemAdapter;
import io.github.mthli.Bitocle.Bookmark.BookmarkTask;
import io.github.mthli.Bitocle.Commit.CommitItem;
import io.github.mthli.Bitocle.Commit.CommitItemAdapter;
import io.github.mthli.Bitocle.Commit.CommitTask;
import io.github.mthli.Bitocle.Content.ContentItem;
import io.github.mthli.Bitocle.Content.ContentItemAdapter;
import io.github.mthli.Bitocle.Content.ContentTask;
import io.github.mthli.Bitocle.Database.Bookmark.BAction;
import io.github.mthli.Bitocle.Database.Bookmark.Bookmark;
import io.github.mthli.Bitocle.R;
import io.github.mthli.Bitocle.Repo.*;
import io.github.mthli.Bitocle.Watch.WatchItem;
import io.github.mthli.Bitocle.Watch.WatchItemAdapter;
import io.github.mthli.Bitocle.Watch.WatchTask;
import io.github.mthli.Bitocle.WebView.MimeType;
import io.github.mthli.Bitocle.WebView.WebViewActivity;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.GitHubClient;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainFragment extends ProgressFragment {
    public static final int REPO_ID = 0;
    public static final int BOOKMARK_ID = 1;
    public static final int CONTENT_ID = 2;
    public static final int HISTORY_ID = 3;
    public static final int COMMIT_ID = 4;
    public int CURRENT_ID = 0;

    private View view;
    private ListView listView;

    private ActionBar actionBar;
    private String titleName;
    private String titlePath;

    private int refreshType = 0;
    private boolean refreshStatus = false;
    private boolean multiChoiceStatus = false;
    private boolean searchViewStatus = false;

    private int location = 0;

    private RepoTask repoTask;
    private BookmarkTask bookmarkTask;
    private ContentTask contentTask;
    private AddTask addTask;
    private WatchTask watchTask;
    private CommitTask commitTask;

    private GitHubClient gitHubClient;
    private String repoOwner;
    private String repoName;
    private String repoPath;

    private RepoItemAdapter repoItemAdapter;
    private List<RepoItem> repoItemList = new ArrayList<RepoItem>();
    private BookmarkItemAdapter bookmarkItemAdapter;
    private List<BookmarkItem> bookmarkItemList = new ArrayList<BookmarkItem>();
    private ContentItemAdapter contentItemAdapter;
    private List<ContentItem> contentItemList = new ArrayList<ContentItem>();
    private List<List<ContentItem>> contentItemListBuffer = new ArrayList<List<ContentItem>>();
    private WatchItemAdapter watchItemAdapter;
    private List<WatchItem> watchItemList = new ArrayList<WatchItem>();
    private CommitItemAdapter commitItemAdapter;
    private List<CommitItem> commitItemList = new ArrayList<CommitItem>();

    private FloatingActionMenu actionMenu;

    private PullToRefreshLayout pullToRefreshLayout;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.main_fragment);
        view = getContentView();
        setContentShown(true);

        listView = (ListView) view.findViewById(R.id.main_fragment_listview);

        actionBar = getActivity().getActionBar();

        ViewGroup viewGroup = (ViewGroup) view;
        pullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .setup(pullToRefreshLayout);

        ImageView imageView = new ImageView(view.getContext());
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_plus));
        final FloatingActionButton actionButton = new FloatingActionButton.Builder(getActivity())
                .setContentView(imageView)
                .build();
        SubActionButton.Builder builder = new SubActionButton.Builder(getActivity());
        ImageView imageViewHistory = new ImageView(view.getContext());
        imageViewHistory.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_history));
        SubActionButton buttonHistory = builder.setContentView(imageViewHistory).build();
        ImageView imageViewAdd = new ImageView(view.getContext());
        imageViewAdd.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_add));
        SubActionButton buttonAdd = builder.setContentView(imageViewAdd).build();
        ImageView imageViewRefresh = new ImageView(view.getContext());
        imageViewRefresh.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_am_refresh));
        SubActionButton buttonRefresh = builder.setContentView(imageViewRefresh).build();
        actionMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(buttonRefresh)
                .addSubActionView(buttonAdd)
                .addSubActionView(buttonHistory)
                .attachTo(actionButton)
                .build();

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!refreshStatus) {
                    switch (CURRENT_ID) {
                        case REPO_ID:
                            refreshType = RefreshType.REPO_BUTTON;
                            repoTask = new RepoTask(MainFragment.this);
                            repoTask.execute();
                            break;
                        case BOOKMARK_ID:
                            refreshType = RefreshType.BOOKMARK_BUTTON;
                            bookmarkTask = new BookmarkTask(MainFragment.this);
                            bookmarkTask.execute();
                            break;
                        case CONTENT_ID:
                            refreshType = RefreshType.CONTENT_BUTTON;
                            contentTask = new ContentTask(MainFragment.this);
                            contentTask.execute();
                            break;
                        case HISTORY_ID:
                            refreshType = RefreshType.WATCH_BUTTON;
                            watchTask = new WatchTask(MainFragment.this);
                            watchTask.execute();
                            break;
                        case COMMIT_ID:
                            refreshType = RefreshType.COMMIT_BUTTON;
                            commitTask = new CommitTask(MainFragment.this, repoItemList.get(location));
                            commitTask.execute();
                            break;
                        default:
                            break;
                    }
                }

                if (searchViewStatus) {
                    searchViewDown();
                }
                actionMenu.close(true);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!refreshStatus && !multiChoiceStatus) {
                    searchViewUp();
                }
                actionMenu.close(true);
            }
        });

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CURRENT_ID != HISTORY_ID) {
                    if (repoTask != null && repoTask.getStatus() == AsyncTask.Status.RUNNING) {
                        repoTask.cancel(true);
                    }
                    if (bookmarkTask != null && bookmarkTask.getStatus() == AsyncTask.Status.RUNNING) {
                        bookmarkTask.cancel(true);
                    }
                    if (contentTask != null && contentTask.getStatus() == AsyncTask.Status.RUNNING) {
                        contentTask.cancel(true);
                    }
                    if (commitTask != null && commitTask.getStatus() == AsyncTask.Status.RUNNING) {
                        commitTask.cancel(true);
                    }
                    setContentShown(true);

                    actionBar.setTitle(R.string.watch_label);
                    actionBar.setSubtitle(null);
                    actionBar.setDisplayHomeAsUpEnabled(true);

                    listView.setAdapter(watchItemAdapter);
                    watchItemAdapter.notifyDataSetChanged();
                    refreshType = RefreshType.WATCH_FIRST;
                    CURRENT_ID = HISTORY_ID;

                    refreshStatus = false; //
                    watchTask = new WatchTask(MainFragment.this);
                    watchTask.execute();
                }
                actionMenu.close(true);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.login_sp), Context.MODE_PRIVATE);
        String oAuth = sharedPreferences.getString(getString(R.string.login_sp_oauth), null);
        gitHubClient = new GitHubClient();
        gitHubClient.setOAuth2Token(oAuth);

        repoItemAdapter = new RepoItemAdapter(
                MainFragment.this,
                view.getContext(),
                R.layout.repo_item,
                repoItemList
        );
        repoItemAdapter.notifyDataSetChanged();
        listView.setAdapter(repoItemAdapter);

        bookmarkItemAdapter = new BookmarkItemAdapter(
                view.getContext(),
                R.layout.bookmark_item,
                bookmarkItemList
        );
        bookmarkItemAdapter.notifyDataSetChanged();

        contentItemAdapter = new ContentItemAdapter(
                view.getContext(),
                R.layout.content_item,
                contentItemList
        );
        contentItemAdapter.notifyDataSetChanged();

        watchItemAdapter = new WatchItemAdapter(
                MainFragment.this,
                view.getContext(),
                R.layout.watch_item,
                watchItemList
        );
        watchItemAdapter.notifyDataSetChanged();

        commitItemAdapter = new CommitItemAdapter(
                view.getContext(),
                R.layout.commit_item,
                commitItemList
        );
        commitItemAdapter.notifyDataSetChanged();

        final Intent intent = getActivity().getIntent();
        if (intent.getBooleanExtra(getString(R.string.login_intent), false)) {
            refreshType = RefreshType.REPO_FIRST;
            repoTask = new RepoTask(this);
            repoTask.execute();
        } else {
            refreshType = RefreshType.REPO_ALREADY;
            repoTask = new RepoTask(this);
            repoTask.execute();
        }
        CURRENT_ID = REPO_ID;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchViewDown();
                actionMenu.close(true);

                switch (CURRENT_ID) {
                    case REPO_ID:
                        RepoItem repoItem = repoItemList.get(position);
                        repoOwner = repoItem.getOwner();
                        repoName = repoItem.getTitle();
                        repoPath = getString(R.string.repo_path_root);

                        titleName = repoName;
                        titlePath = repoName + getString(R.string.repo_path_root);

                        actionBar.setTitle(titleName);
                        actionBar.setSubtitle(titlePath);
                        actionBar.setDisplayHomeAsUpEnabled(true);

                        contentItemListBuffer.clear();

                        listView.setAdapter(contentItemAdapter);
                        contentItemAdapter.notifyDataSetChanged();
                        refreshType = RefreshType.CONTENT_FIRST;
                        CURRENT_ID = CONTENT_ID;
                        contentTask = new ContentTask(MainFragment.this);
                        contentTask.execute();
                        break;
                    case BOOKMARK_ID:
                        BookmarkItem bookmarkItem = bookmarkItemList.get(position);

                        repoOwner = bookmarkItem.getRepoOwner();
                        repoName = bookmarkItem.getRepoName();

                        if (bookmarkItem.getType().equals(RepositoryContents.TYPE_DIR)) {
                            repoPath = bookmarkItem.getRepoPath();

                            titleName = repoName;
                            titlePath = repoName
                                    + getString(R.string.repo_path_root)
                                    + bookmarkItem.getRepoPath()
                                    + getString(R.string.repo_path_root);

                            actionBar.setTitle(titleName);
                            actionBar.setSubtitle(titlePath);
                            actionBar.setDisplayHomeAsUpEnabled(true);

                            listView.setAdapter(contentItemAdapter);
                            contentItemAdapter.notifyDataSetChanged();
                            refreshType = RefreshType.CONTENT_FIRST;
                            CURRENT_ID = CONTENT_ID;
                            contentTask = new ContentTask(MainFragment.this);
                            contentTask.execute();
                        } else {
                            if (MimeType.isUnSupport(bookmarkItem.getTitle())) {
                                Toast.makeText(
                                        view.getContext(),
                                        R.string.content_mimetype_unsupport,
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Intent intentToWebView = new Intent(getActivity(), WebViewActivity.class);
                                intentToWebView.putExtra(getString(R.string.content_intent_repoowner), repoOwner);
                                intentToWebView.putExtra(getString(R.string.content_intent_reponame), repoName);
                                intentToWebView.putExtra(getString(R.string.content_intent_filename), bookmarkItem.getTitle());
                                intentToWebView.putExtra(
                                        getString(R.string.content_intent_filepath),
                                        repoName
                                                + getString(R.string.repo_path_root)
                                                + bookmarkItem.getRepoPath()
                                );
                                intentToWebView.putExtra(getString(R.string.content_intent_sha), bookmarkItem.getSha());
                                startActivity(intentToWebView);
                            }
                        }

                        break;
                    case CONTENT_ID:
                        ContentItem contentItem = contentItemList.get(position);

                        if (contentItem.getType().equals(RepositoryContents.TYPE_DIR)) {
                            repoPath = contentItem.getRepoPath();

                            titleName = contentItem.getTitle();
                            titlePath = repoName
                                    + getString(R.string.repo_path_root)
                                    + contentItem.getRepoPath()
                                    + getString(R.string.repo_path_root);

                            actionBar.setTitle(titleName);
                            actionBar.setSubtitle(titlePath);
                            actionBar.setDisplayHomeAsUpEnabled(true);

                            refreshType = RefreshType.CONTENT_FIRST;
                            contentTask = new ContentTask(MainFragment.this);
                            contentTask.execute();
                        } else {
                            if (MimeType.isUnSupport(contentItem.getTitle())) {
                                Toast.makeText(
                                        view.getContext(),
                                        R.string.content_mimetype_unsupport,
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Intent intentToWebView = new Intent(getActivity(), WebViewActivity.class);
                                intentToWebView.putExtra(getString(R.string.content_intent_repoowner), repoOwner);
                                intentToWebView.putExtra(getString(R.string.content_intent_reponame), repoName);
                                intentToWebView.putExtra(getString(R.string.content_intent_filename), contentItem.getTitle());
                                intentToWebView.putExtra(
                                        getString(R.string.content_intent_filepath),
                                        repoName
                                                + getString(R.string.repo_path_root)
                                                + contentItem.getRepoPath()
                                );
                                intentToWebView.putExtra(getString(R.string.content_intent_sha), contentItem.getSha());
                                startActivity(intentToWebView);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Integer integer = listView.getCheckedItemCount();
                mode.setTitle(integer.toString());
                
                actionMenu.close(true);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (CURRENT_ID == BOOKMARK_ID) {
                    MenuInflater menuInflater = getActivity().getMenuInflater();
                    menuInflater.inflate(R.menu.bookmark_choice_menu, menu);
                    return true;
                } else if (CURRENT_ID == CONTENT_ID) {
                    MenuInflater menuInflater = getActivity().getMenuInflater();
                    menuInflater.inflate(R.menu.content_choice_menu, menu);
                    /* Do somthing */
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                searchViewDown();
                actionMenu.close(true);
                multiChoiceStatus = true;
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
                actionMenu.close(true);

                BAction bAction = new BAction(view.getContext());
                try {
                    bAction.openDatabase(true);
                } catch (SQLException s) {
                    Toast.makeText(
                            view.getContext(),
                            getString(R.string.bookmark_database_open_error),
                            Toast.LENGTH_SHORT
                    ).show();
                    bAction.closeDatabase();
                    return false;
                }

                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                switch (menuItem.getItemId()) {
                    case R.id.content_choice_menu_add_bookmark:
                        for (int i = 0; i < sparseBooleanArray.size(); i++) {
                            if (sparseBooleanArray.valueAt(i)) {
                                ContentItem contentItem = contentItemAdapter.getItem(sparseBooleanArray.keyAt(i));
                                if (!bAction.checkBookmark(contentItem.getSha())) {
                                    Bookmark bookmark = new Bookmark();
                                    bookmark.setTitle(contentItem.getTitle());
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    Date date = new Date();
                                    bookmark.setDate(simpleDateFormat.format(date));
                                    bookmark.setType(contentItem.getType());
                                    bookmark.setRepoOwner(repoOwner);
                                    bookmark.setRepoName(repoName);
                                    bookmark.setRepoPath(contentItem.getRepoPath());
                                    bookmark.setSha(contentItem.getSha());
                                    String key = repoOwner + getString(R.string.repo_path_root) + repoName;
                                    bookmark.setKey(key);
                                    bAction.addBookmark(bookmark);
                                }
                            }
                        }
                        onDestroyActionMode(mode);
                        Toast.makeText(
                                view.getContext(),
                                getString(R.string.bookmark_add_successful),
                                Toast.LENGTH_SHORT
                        ).show();
                        break;
                    case R.id.bookmark_choice_menu_remove_bookmark:
                        for (int i = 0; i < sparseBooleanArray.size(); i++) {
                            if (sparseBooleanArray.valueAt(i)) {
                                BookmarkItem bookmarkItem = bookmarkItemAdapter.getItem(sparseBooleanArray.keyAt(i));
                                bAction.unMarkBySha(bookmarkItem.getSha());
                            }
                        }
                        onDestroyActionMode(mode);
                        Toast.makeText(
                                view.getContext(),
                                getString(R.string.bookmark_remove_successful),
                                Toast.LENGTH_SHORT
                        ).show();
                        bookmarkTask = new BookmarkTask(MainFragment.this);
                        bookmarkTask.execute();
                        break;
                    default:
                        break;
                }
                bAction.closeDatabase();

                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                SparseBooleanArray sparseBooleanArray = listView.getCheckedItemPositions();
                for (int i = 0; i < sparseBooleanArray.size(); i++) {
                    if (sparseBooleanArray.valueAt(i)) {
                        listView.setItemChecked(i, false);
                    }
                }

                multiChoiceStatus = false;
                actionMenu.close(true);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                actionMenu.close(true);
                searchViewDown();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                /* Do nothing */
            }
        });
    }

    public void changeToRepo() {
        actionBar.setTitle(getString(R.string.app_name));
        actionBar.setSubtitle(null);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        if (bookmarkTask != null && bookmarkTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookmarkTask.cancel(true);
        }
        if (contentTask != null && contentTask.getStatus() == AsyncTask.Status.RUNNING) {
            contentTask.cancel(true);
        }
        if (watchTask != null && watchTask.getStatus() == AsyncTask.Status.RUNNING) {
            watchTask.cancel(true);
        }
        if (commitTask != null && commitTask.getStatus() == AsyncTask.Status.RUNNING) {
            commitTask.cancel(true);
        }
        setContentShown(true);

        contentItemListBuffer.clear();
        listView.setAdapter(repoItemAdapter);
        repoItemAdapter.notifyDataSetChanged();
        refreshType = RefreshType.REPO_ALREADY;
        CURRENT_ID = REPO_ID;
        repoTask = new RepoTask(MainFragment.this);
        repoTask.execute();
    }

    public void changeToBookmark() {
        if (repoTask != null && repoTask.getStatus() == AsyncTask.Status.RUNNING) {
            repoTask.cancel(true);
        }
        if (contentTask != null && contentTask.getStatus() == AsyncTask.Status.RUNNING) {
            contentTask.cancel(true);
        }
        if (watchTask != null && watchTask.getStatus() == AsyncTask.Status.RUNNING) {
            watchTask.cancel(true);
        }
        if (commitTask != null && commitTask.getStatus() == AsyncTask.Status.RUNNING) {
            commitTask.cancel(true);
        }
        setContentShown(true);

        if (CURRENT_ID != BOOKMARK_ID) {
            actionBar.setTitle(getString(R.string.bookmark_label));
            actionBar.setSubtitle(null);
            actionBar.setDisplayHomeAsUpEnabled(true);

            listView.setAdapter(bookmarkItemAdapter);
            bookmarkItemAdapter.notifyDataSetChanged();
            refreshType = RefreshType.BOOKMARK_FIRST;
            CURRENT_ID = BOOKMARK_ID;
            bookmarkTask = new BookmarkTask(MainFragment.this);
            bookmarkTask.execute();
        }
    }

    public void changeToContent() {
        if (repoTask != null && repoTask.getStatus() == AsyncTask.Status.RUNNING) {
            repoTask.cancel(true);
        }
        if (bookmarkTask != null && bookmarkTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookmarkTask.cancel(true);
        }
        if (contentTask != null && contentTask.getStatus() == AsyncTask.Status.RUNNING) {
            contentTask.cancel(true);
        }
        if (watchTask != null && watchTask.getStatus() == AsyncTask.Status.RUNNING) {
            watchTask.cancel(true);
        }
        if (commitTask != null && commitTask.getStatus() == AsyncTask.Status.RUNNING) {
            commitTask.cancel(true);
        }

        actionBar.setTitle(titleName);
        actionBar.setSubtitle(titlePath);
        actionBar.setDisplayHomeAsUpEnabled(true);

        refreshType = RefreshType.CONTENT_FIRST;
        CURRENT_ID = CONTENT_ID;

        listView.setAdapter(contentItemAdapter);
        commitItemAdapter.notifyDataSetChanged();
    }

    public void backToParent() {
        String[] arr = titlePath.split(getString(R.string.repo_path_root));
        titleName = arr[arr.length - 2];
        titlePath = arr[0];
        for (int i = 1; i < arr.length - 1; i++) {
            titlePath = titlePath + getString(R.string.repo_path_root) + arr[i];
        }
        titlePath = titlePath + getString(R.string.repo_path_root);

        actionBar.setTitle(titleName);
        actionBar.setSubtitle(titlePath);
        actionBar.setDisplayHomeAsUpEnabled(true);

        contentItemListBuffer.remove(contentItemListBuffer.size() - 1);
        List<ContentItem> contentItems = contentItemListBuffer.get(contentItemListBuffer.size() - 1);

        contentItemList.clear();
        for (ContentItem c: contentItems) {
            contentItemList.add(c);
        }
        contentItemAdapter.notifyDataSetChanged();
    }

    private void searchViewUp() {
        actionBar.setDisplayHomeAsUpEnabled(true);
        searchViewStatus = true;

        MenuItem searchItem = MainActivity.searchItem;
        SearchView searchView = MainActivity.searchView;
        searchView.onActionViewExpanded();
        searchItem.setVisible(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pullToRefreshLayout.setRefreshing(true);
                actionMenu.close(true);
                searchViewDown();
                addTask = new AddTask(MainFragment.this, query);
                addTask.execute();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                /* Do nothing */
                return false;
            }
        });
    }

    public void searchViewDown() {
        if (CURRENT_ID == REPO_ID) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        searchViewStatus = false;

        MenuItem searchItem = MainActivity.searchItem;
        SearchView searchView = MainActivity.searchView;
        searchView.onActionViewCollapsed();
        searchItem.setVisible(false);
    }

    public void cancelAllTasks() {
        if (repoTask != null && repoTask.getStatus() == AsyncTask.Status.RUNNING) {
            repoTask.cancel(true);
        }
        if (bookmarkTask != null && bookmarkTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookmarkTask.cancel(true);
        }
        if (contentTask != null && contentTask.getStatus() == AsyncTask.Status.RUNNING) {
            contentTask.cancel(true);
        }
        if (addTask != null && addTask.getStatus() == AsyncTask.Status.RUNNING) {
            addTask.cancel(true);
        }
        if (watchTask != null && watchTask.getStatus() == AsyncTask.Status.RUNNING) {
            watchTask.cancel(true);
        }
        if (commitTask != null && commitTask.getStatus() == AsyncTask.Status.RUNNING) {
            commitTask.cancel(true);
        }
    }

    public ListView getListView() {
        return listView;
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getRefreshType() {
        return refreshType;
    }
    public void setRefreshType(int refreshType) {
        this.refreshType = refreshType;
    }

    public boolean getSearchViewStatus() {
        return searchViewStatus;
    }

    public void setRefreshStatus(boolean refreshStatus) {
        this.refreshStatus = refreshStatus;
    }

    public void setRepoTask(RepoTask repoTask) {
        this.repoTask = repoTask;
    }

    public void setCommitTask(CommitTask commitTask) {
        this.commitTask = commitTask;
    }

    public GitHubClient getGitHubClient() {
        return gitHubClient;
    }

    public String getRepoOwner() {
        return repoOwner;
    }

    public String getRepoName() {
        return repoName;
    }

    public String getRepoPath() {
        return repoPath;
    }

    public RepoItemAdapter getRepoItemAdapter() {
        return repoItemAdapter;
    }

    public List<RepoItem> getRepoItemList() {
        return repoItemList;
    }

    public BookmarkItemAdapter getBookmarkItemAdapter() {
        return bookmarkItemAdapter;
    }

    public List<BookmarkItem> getBookmarkItemList() {
        return bookmarkItemList;
    }

    public ContentItemAdapter getContentItemAdapter() {
        return contentItemAdapter;
    }

    public List<ContentItem> getContentItemList() {
        return contentItemList;
    }

    public List<List<ContentItem>> getContentItemListBuffer() {
        return contentItemListBuffer;
    }

    public WatchItemAdapter getWatchItemAdapter() {
        return watchItemAdapter;
    }

    public List<WatchItem> getWatchItemList() {
        return watchItemList;
    }

    public CommitItemAdapter getCommitItemAdapter() {
        return commitItemAdapter;
    }

    public List<CommitItem> getCommitItemList() {
        return commitItemList;
    }

    public FloatingActionMenu getActionMenu() {
        return actionMenu;
    }

    public PullToRefreshLayout getPullToRefreshLayout() {
        return pullToRefreshLayout;
    }
}
