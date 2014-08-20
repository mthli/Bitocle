package io.github.mthli.Bitocle.Main;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.devspark.progressfragment.ProgressFragment;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;
import io.github.mthli.Bitocle.Bookmark.BookmarkItem;
import io.github.mthli.Bitocle.Bookmark.BookmarkItemAdapter;
import io.github.mthli.Bitocle.Bookmark.BookmarkTask;
import io.github.mthli.Bitocle.Commit.CommitItem;
import io.github.mthli.Bitocle.Commit.CommitItemAdapter;
import io.github.mthli.Bitocle.Commit.CommitTask;
import io.github.mthli.Bitocle.Content.ContentItem;
import io.github.mthli.Bitocle.Content.ContentItemAdapter;
import io.github.mthli.Bitocle.Content.RepoContentTask;
import io.github.mthli.Bitocle.Content.StarContentTask;
import io.github.mthli.Bitocle.Database.Bookmark.BAction;
import io.github.mthli.Bitocle.Database.Bookmark.Bookmark;
import io.github.mthli.Bitocle.R;
import io.github.mthli.Bitocle.Repo.*;
import io.github.mthli.Bitocle.Star.StarItem;
import io.github.mthli.Bitocle.Star.StarItemAdapter;
import io.github.mthli.Bitocle.Star.StarTask;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import org.eclipse.egit.github.core.client.GitHubClient;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

import java.util.*;

public class MainFragment extends ProgressFragment {
    public static final int REPO_ID = 0;
    public static final int STAR_ID = 1;
    public static final int BOOKMARK_ID = 2;
    public static final int REPO_CONTENT_ID = 3;
    public static final int STAR_CONTENT_ID = 4;
    public static final int COMMIT_ID = 5;

    private int currentId = 0;
    private int flag = 0;

    private View view;
    private ListView listView;
    private ActionBar actionBar;
    private PullToRefreshLayout pull;
    private String title;
    private String subTitle;

    private MenuItem star;
    private MenuItem bookmark;
    private MenuItem about;
    private MenuItem logout;
    private AutoCompleteTextView search;
    private View line;
    private InputMethodManager imm;

    private RepoItemAdapter repoItemAdapter;
    private List<RepoItem> repoItemList = new ArrayList<RepoItem>();

    private BookmarkItemAdapter bookmarkItemAdapter;
    private List<BookmarkItem> bookmarkItemList = new ArrayList<BookmarkItem>();

    private StarItemAdapter starItemAdapter;
    private List<StarItem> starItemList = new ArrayList<StarItem>();

    private ContentItemAdapter contentItemAdapter;
    private List<ContentItem> contentItemList = new ArrayList<ContentItem>();

    private CommitItemAdapter commitItemAdapter;
    private List<CommitItem> commitItemList = new ArrayList<CommitItem>();

    private GitHubClient client;
    private String owner;
    private String name;

    private Tree root;
    private TreeEntry entry;
    private List<Tree> roots = new ArrayList<Tree>();
    private List<Map<String, String>> buffer = new ArrayList<Map<String, String>>();
    private boolean toggle = false;

    private RepoTask repoTask;
    private BookmarkTask bookmarkTask;
    private StarTask starTask;
    private AddTask addTask;
    private RepoContentTask repoContentTask;
    private StarContentTask starContentTask;
    private CommitTask commitTask;

    public int getCurrentId() {
        return currentId;
    }

    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }

    public ListView getListView() {
        return listView;
    }

    public PullToRefreshLayout getPull() {
        return pull;
    }

    public void setStar(MenuItem star) {
        this.star = star;
    }

    public void setBookmark(MenuItem bookmark) {
        this.bookmark = bookmark;
    }

    public void setAbout(MenuItem about) {
        this.about = about;
    }

    public void setLogout(MenuItem logout) {
        this.logout = logout;
    }

    public AutoCompleteTextView getSearch() {
        return search;
    }

    public RepoItemAdapter getRepoItemAdapter() {
        return repoItemAdapter;
    }
    public List<RepoItem> getRepoItemList() {
        return repoItemList;
    }

    public StarItemAdapter getStarItemAdapter() {
        return starItemAdapter;
    }
    public List<StarItem> getStarItemList() {
        return starItemList;
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

    public CommitItemAdapter getCommitItemAdapter() {
        return commitItemAdapter;
    }
    public List<CommitItem> getCommitItemList() {
        return commitItemList;
    }

    public GitHubClient getClient() {
        return client;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public Tree getRoot() {
        return root;
    }
    public void setRoot(Tree root) {
        this.root = root;
    }

    public TreeEntry getEntry() {
        return entry;
    }

    public List<Tree> getRoots() {
        return roots;
    }

    public boolean isToggle() {
        return toggle;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentView(R.layout.main_fragment);
        view = getContentView();
        setContentShown(true);

        listView = (ListView) view.findViewById(R.id.main_fragment_listview);

        actionBar = getActivity().getActionBar();

        ViewGroup group = (ViewGroup) view;
        pull = new PullToRefreshLayout(group.getContext());
        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(group)
                .setup(pull);

        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        search = ((MainActivity) getActivity()).getSearch();
        line = ((MainActivity) getActivity()).getLine();

        final Drawable searchIcon = getResources().getDrawable(R.drawable.ic_action_cancel);
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (searchIcon != null && event.getAction() == MotionEvent.ACTION_UP) {
                    int eventX = (int) event.getRawX();
                    int eventY = (int) event.getRawY();

                    Rect rectR = new Rect();
                    search.getGlobalVisibleRect(rectR);
                    rectR.left = rectR.right - 100;
                    if (rectR.contains(eventX, eventY)) {
                        search.setText("");
                    }
                }
                return false;
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String query = search.getText().toString();
                    if (query.length() > 0) {
                        if (addTask != null && addTask.getStatus() == AsyncTask.Status.FINISHED) {
                            addTask = new AddTask(MainFragment.this, query);
                            addTask.execute();
                        } else if (addTask == null) {
                            addTask = new AddTask(MainFragment.this, query);
                            addTask.execute();
                        } else {
                            /* Do nothing */
                        }
                    }
                }
                return false;
            }
        });

        search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickWhenSearchItem(view);
            }
        });

        /*
        autoAdapter = new SimpleAdapter(
                view.getContext(),
                autoList,
                R.layout.auto_item,
                new String[] {"owner", "name"},
                new int[] {R.id.auto_item_owner, R.id.auto_item_name}
        );
        autoAdapter.notifyDataSetChanged();
        search.setAdapter(autoAdapter);
        */

        repoItemAdapter = new RepoItemAdapter(
                MainFragment.this,
                view.getContext(),
                R.layout.repo_item,
                repoItemList
        );
        repoItemAdapter.notifyDataSetChanged();
        listView.setAdapter(repoItemAdapter);

        starItemAdapter = new StarItemAdapter(
                MainFragment.this,
                view.getContext(),
                R.layout.repo_item,
                starItemList
        );
        starItemAdapter.notifyDataSetChanged();

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

        commitItemAdapter = new CommitItemAdapter(
                view.getContext(),
                R.layout.commit_item,
                commitItemList
        );
        commitItemAdapter.notifyDataSetChanged();

        SharedPreferences sp = getActivity().getSharedPreferences(
                getString(R.string.login_sp),
                Context.MODE_PRIVATE
        );
        String OAuth = sp.getString(getString(R.string.login_sp_oauth), null);
        client = new GitHubClient();
        client.setOAuth2Token(OAuth);

        Intent intent = getActivity().getIntent();
        if (intent.getBooleanExtra(getString(R.string.login_intent), false)) {
            flag = Flag.REPO_FIRST;
            repoTask = new RepoTask(MainFragment.this);
            repoTask.execute();
        } else {
            flag = Flag.REPO_SECOND;
            repoTask = new RepoTask(MainFragment.this);
            repoTask.execute();
        }
        currentId = REPO_ID;
        toggle = false;

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                Integer integer = listView.getCheckedItemCount();
                mode.setTitle(integer.toString());
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = getActivity().getMenuInflater();
                switch (currentId) {
                    case BOOKMARK_ID:
                        inflater.inflate(R.menu.bookmark_choice_menu, menu);
                        return true;
                    case REPO_CONTENT_ID:
                        inflater.inflate(R.menu.content_choice_menu, menu);
                        return true;
                    case STAR_CONTENT_ID:
                        inflater.inflate(R.menu.content_choice_menu, menu);
                        return true;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                /* Do nothing */
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return clickWhenActionItem(item, view.getContext());
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                SparseBooleanArray array = listView.getCheckedItemPositions();
                for (int i = 0; i < array.size(); i++) {
                    if (array.valueAt(i)) {
                        listView.setItemChecked(i, false);
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (currentId) {
                    case REPO_ID:
                        clickWhenRepo(position);
                        break;
                    case STAR_ID:
                        clickWhenStar(position);
                        break;
                    case BOOKMARK_ID:
                        clickWhenBookmark(position);
                        break;
                    case REPO_CONTENT_ID:
                        clickWhenRepoContent(position);
                        break;
                    case STAR_CONTENT_ID:
                        clickWhenStarContent(position);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void clickWhenSearchItem(View view) {
        allTaskDown();

        hideWhenContent();

        TextView ownerText = (TextView) view.findViewById(R.id.auto_item_owner);
        TextView nameText = (TextView) view.findViewById(R.id.auto_item_name);
        owner = ownerText.getText().toString();
        name = nameText.getText().toString();

        title = name;
        subTitle = name;
        actionBar.setTitle(title);
        actionBar.setSubtitle(subTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);

        root = null;
        entry = null;
        roots.clear();
        buffer.clear();

        Map<String, String> map = new HashMap<String, String>();
        map.put("prefix", "/");
        map.put("suffix", "/");
        map.put("owner", owner);
        map.put("name", name);
        buffer.add(map);

        listView.setAdapter(contentItemAdapter);
        contentItemAdapter.notifyDataSetChanged();
        flag = Flag.REPO_CONTENT_FIRST;
        currentId = REPO_CONTENT_ID;
        repoContentTask = new RepoContentTask(MainFragment.this);
        repoContentTask.execute();
    }

    private boolean clickWhenActionItem(MenuItem item, Context context) {
        BAction action = new BAction(context);
        try {
            action.openDatabase(true);
        } catch (SQLException s) {
            SuperToast.create(
                    context,
                    getString(R.string.content_database_error),
                    SuperToast.Duration.VERY_SHORT,
                    Style.getStyle(Style.RED)
            ).show();
            return false;
        }

        SparseBooleanArray array = listView.getCheckedItemPositions();
        switch (item.getItemId()) {
            case R.id.content_choice_add:
                for (int i = 0; i < array.size(); i++) {
                    if (array.valueAt(i)) {
                        ContentItem c = contentItemAdapter.getItem(array.keyAt(i));
                        TreeEntry e = c.getEntry();
                        if (!action.checkBookmark(e.getSha())) {
                            Bookmark b = new Bookmark();
                            String[] arr = e.getPath().split("/");
                            b.setTitle(arr[arr.length - 1]);
                            b.setType(e.getType());
                            b.setOwner(owner);
                            b.setName(name);

                            Map<String, String> map = buffer.get(buffer.size() - 1);
                            if (toggle) {
                                String str = map.get("prefix");
                                if (str.equals("/")) {
                                    b.setPath(e.getPath());
                                } else {
                                    b.setPath(str + "/" + e.getPath());
                                }
                            } else {
                                b.setPath(e.getPath());
                            }

                            b.setSha(e.getSha());
                            b.setKey(owner + "/" + name);
                            action.addBookmark(b);
                        }
                        listView.setItemChecked(i, false);
                    }
                }
                SuperToast.create(
                        context,
                        getString(R.string.content_add_successful),
                        SuperToast.Duration.VERY_SHORT,
                        Style.getStyle(Style.BLUE)
                ).show();
                break;
            case R.id.bookmark_choice_remove:
                for (int i = 0; i < array.size(); i++) {
                    if (array.valueAt(i)) {
                        BookmarkItem b = bookmarkItemAdapter.getItem(array.keyAt(i));
                        action.unMarkBySha(b.getSha());
                        listView.setItemChecked(i, false);
                    }
                }
                SuperToast.create(
                        context,
                        getString(R.string.bookmark_remove_successful),
                        SuperToast.Duration.VERY_SHORT,
                        Style.getStyle(Style.BLUE)
                ).show();
                bookmarkTask = new BookmarkTask(MainFragment.this);
                bookmarkTask.execute();
                break;
            default:
                break;
        }
        action.closeDatabase();

        return true;
    }

    private void clickWhenRepo(int position) {
        allTaskDown();

        hideWhenContent();

        RepoItem item = repoItemList.get(position);

        owner = item.getOwner();
        name = item.getName();

        title = name;
        subTitle = name;
        actionBar.setTitle(title);
        actionBar.setSubtitle(subTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);

        root = null;
        entry = null;
        roots.clear();
        buffer.clear();

        Map<String, String> map = new HashMap<String, String>();
        map.put("prefix", "/");
        map.put("suffix", "/");
        map.put("owner", owner);
        map.put("name", name);
        buffer.add(map);

        listView.setAdapter(contentItemAdapter);
        contentItemAdapter.notifyDataSetChanged();
        flag = Flag.REPO_CONTENT_FIRST;
        currentId = REPO_CONTENT_ID;
        repoContentTask = new RepoContentTask(MainFragment.this);
        repoContentTask.execute();
    }

    private void clickWhenStar(int position) {
        allTaskDown();

        StarItem item = starItemList.get(position);
        owner = item.getOwner();
        name = item.getName();

        title = name;
        subTitle = name;
        actionBar.setTitle(title);
        actionBar.setSubtitle(subTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);

        root = null;
        entry = null;
        roots.clear();
        buffer.clear();

        Map<String, String> map = new HashMap<String, String>();
        map.put("prefix", "/");
        map.put("suffix", "/");
        map.put("owner", owner);
        map.put("name", name);
        buffer.add(map);

        listView.setAdapter(contentItemAdapter);
        contentItemAdapter.notifyDataSetChanged();
        flag = Flag.STAR_CONTENT_FIRST;
        currentId = STAR_CONTENT_ID;
        starContentTask = new StarContentTask(MainFragment.this);
        starContentTask.execute();
    }

    private BookmarkItem bookmarkItem;

    private void clickWhenBookmark(int position) {
        allTaskDown();

        bookmarkItem = bookmarkItemList.get(position);

        owner = bookmarkItem.getOwner();
        name = bookmarkItem.getName();

        if (bookmarkItem.getType().equals("tree")) {
            hideWhenContent();

            title = bookmarkItem.getTitle();
            subTitle = name + "/" + bookmarkItem.getPath();
            actionBar.setTitle(title);
            actionBar.setSubtitle(subTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);

            listView.setAdapter(contentItemAdapter);
            contentItemAdapter.notifyDataSetChanged();

            Map<String, String> map = new HashMap<String, String>();
            map.put("prefix", bookmarkItem.getPath());
            map.put("suffix", "/");
            map.put("owner", owner);
            map.put("name", name);
            buffer.add(map);
            entry = null;

            switch (flag) {
                case Flag.REPO_FIRST:
                case Flag.REPO_SECOND:
                case Flag.REPO_REFRESH:
                case Flag.REPO_CONTENT_FIRST:
                case Flag.REPO_CONTENT_SECOND:
                case Flag.REPO_CONTENT_REFRESH:
                    flag = Flag.REPO_CONTENT_FIRST;
                    currentId = REPO_CONTENT_ID;
                    repoContentTask = new RepoContentTask(MainFragment.this);
                    repoContentTask.execute();
                    break;
                case Flag.STAR_FIRST:
                case Flag.STAR_SECOND:
                case Flag.STAR_REFRESH:
                case Flag.STAR_CONTENT_FIRST:
                case Flag.STAR_CONTENT_SECOND:
                case Flag.STAR_CONTENT_REFRESH:
                    flag = Flag.STAR_CONTENT_FIRST;
                    currentId = STAR_CONTENT_ID;
                    starContentTask = new StarContentTask(MainFragment.this);
                    starContentTask.execute();
                    break;
                default:
                    break;
            }
        } else {

        }
    }

    public BookmarkItem getBookmarkItem() {
        return bookmarkItem;
    }

    private void clickWhenRepoContent(int position) {
        allTaskDown();

        ContentItem item = contentItemList.get(position);
        Map<String, String> map = buffer.get(buffer.size() - 1);

        if (item.getEntry().getType().equals("tree")) {
            entry = item.getEntry();
            map.put("suffix", entry.getPath());

            String[] arr = entry.getPath().split("/");
            title = arr[arr.length - 1];
            if (toggle) {
                String str = map.get("prefix");
                if (str.equals("/")) {
                    subTitle = name
                            + "/"
                            + entry.getPath();
                } else {
                    subTitle = name
                            + "/"
                            + str
                            + "/"
                            + entry.getPath();
                }
            } else {
                subTitle = name + "/" + entry.getPath();
            }
            actionBar.setTitle(title);
            actionBar.setSubtitle(subTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);

            flag = Flag.REPO_CONTENT_SECOND;
            currentId = REPO_CONTENT_ID;
            repoContentTask = new RepoContentTask(MainFragment.this);
            repoContentTask.execute();
        } else {
            /* Do something */
        }
    }

    private void clickWhenStarContent(int position) {
        allTaskDown();

        ContentItem item = contentItemList.get(position);
        Map<String, String> map = buffer.get(buffer.size() - 1);

        if (item.getEntry().getType().equals("tree")) {
            entry = item.getEntry();
            map.put("suffix", entry.getPath());

            String[] arr = entry.getPath().split("/");
            title = arr[arr.length - 1];
            if (toggle) {
                String str = map.get("prefix");
                if (str.equals("/")) {
                    subTitle = name
                            + "/"
                            + entry.getPath();
                } else {
                    subTitle = name
                            + "/"
                            + str
                            + "/"
                            + entry.getPath();
                }
            } else {
                subTitle = name + "/" + entry.getPath();
            }
            actionBar.setTitle(title);
            actionBar.setSubtitle(subTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);

            flag = Flag.STAR_CONTENT_SECOND;
            currentId = STAR_CONTENT_ID;
            starContentTask = new StarContentTask(MainFragment.this);
            starContentTask.execute();
        } else {
            /* Do something */
        }
    }

    public void hideWhenStar() {
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        search.setText("");
        search.clearFocus();
        search.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        star.setVisible(false);
        bookmark.setVisible(true);
        about.setVisible(false);
        logout.setVisible(false);
    }

    public void hideWhenContent() {
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        search.setText("");
        search.clearFocus();
        search.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        star.setVisible(false);
        bookmark.setVisible(true);
        about.setVisible(false);
        logout.setVisible(false);
    }

    public void hideWhenBookmark() {
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        search.setText("");
        search.clearFocus();
        search.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        star.setVisible(false);
        bookmark.setVisible(false);
        about.setVisible(false);
        logout.setVisible(false);
    }

    public void hideWhenCommit() {
        imm.hideSoftInputFromWindow(search.getWindowToken(), 0);
        search.setText("");
        search.clearFocus();
        search.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        star.setVisible(false);
        bookmark.setVisible(false);
        about.setVisible(false);
        logout.setVisible(false);
    }

    public void showWhenRepo() {
        search.setVisibility(View.VISIBLE);
        line.setVisibility(View.VISIBLE);
        listView.setHeaderDividersEnabled(true);
        star.setVisible(true);
        bookmark.setVisible(true);
        about.setVisible(true);
        logout.setVisible(true);
    }

    public void changeToRepo(int status) {
        allTaskDown();

        showWhenRepo();
        setContentEmpty(false);
        setContentShown(true);

        actionBar.setTitle(R.string.app_name);
        actionBar.setSubtitle(null);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);

        entry = null;
        root = null;
        roots.clear();
        buffer.clear();
        toggle = false;

        listView.setAdapter(repoItemAdapter);
        repoItemAdapter.notifyDataSetChanged();
        flag = status;
        currentId = REPO_ID;
        repoTask = new RepoTask(MainFragment.this);
        repoTask.execute();
    }

    public void changeToBookmark() {
        allTaskDown();
        hideWhenBookmark();
        setContentEmpty(false);
        setContentShown(true);

        actionBar.setTitle(R.string.bookmark_label);
        actionBar.setSubtitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);

        toggle = true;

        listView.setAdapter(bookmarkItemAdapter);
        bookmarkItemAdapter.notifyDataSetChanged();
        currentId = MainFragment.BOOKMARK_ID;
        bookmarkTask = new BookmarkTask(MainFragment.this);
        bookmarkTask.execute();
    }

    private int location;

    public void setLocation(int location) {
        this.location = location;
    }

    public void changeToCommit(int status) {
        allTaskDown();
        hideWhenCommit();
        setContentEmpty(false);
        setContentShown(true);

        entry = null;
        root = null;
        roots.clear();
        buffer.clear();
        toggle = false;

        listView.setAdapter(commitItemAdapter);
        contentItemAdapter.notifyDataSetChanged();
        if (currentId == REPO_ID) {
            RepoItem repoItem = repoItemList.get(location);
            actionBar.setTitle(repoItem.getName());
            flag = status;
            commitTask = new CommitTask(
                    MainFragment.this,
                    repoItem,
                    null
            );
            commitTask.execute();
        } else {
            StarItem starItem = starItemList.get(location);
            actionBar.setTitle(starItem.getName());
            flag = status;
            commitTask = new CommitTask(
                    MainFragment.this,
                    null,
                    starItem
            );
            commitTask.execute();
        }
        actionBar.setSubtitle(R.string.commit_label);
        actionBar.setDisplayHomeAsUpEnabled(true);
        currentId = COMMIT_ID;
    }

    public void changeToStar(boolean isFirst) {
        allTaskDown();

        hideWhenStar();
        setContentEmpty(false);
        setContentShown(true);

        actionBar.setTitle(R.string.star_label);
        actionBar.setSubtitle(null);
        actionBar.setDisplayHomeAsUpEnabled(true);

        entry = null;
        root = null;
        roots.clear();
        buffer.clear();
        toggle = false;

        listView.setAdapter(starItemAdapter);
        starItemAdapter.notifyDataSetChanged();
        currentId = MainFragment.STAR_ID;
        if (isFirst) {
            flag = Flag.STAR_FIRST;
            starTask = new StarTask(MainFragment.this);
            starTask.execute();
        } else {
            flag = Flag.STAR_SECOND;
        }
    }

    public void backFromBookmark() {
        allTaskDown();

        switch (flag) {
            case Flag.REPO_FIRST:
            case Flag.REPO_SECOND:
            case Flag.REPO_REFRESH:
                changeToRepo(Flag.REPO_SECOND);
                break;
            case Flag.REPO_CONTENT_FIRST:
            case Flag.REPO_CONTENT_SECOND:
            case Flag.REPO_CONTENT_REFRESH:
                setContentEmpty(false);
                setContentShown(true);

                bookmark.setVisible(true);
                actionBar.setTitle(title);
                actionBar.setSubtitle(subTitle);
                actionBar.setDisplayHomeAsUpEnabled(true);

                currentId = REPO_CONTENT_ID;
                listView.setAdapter(contentItemAdapter);
                contentItemAdapter.notifyDataSetChanged();
                break;
            case Flag.STAR_FIRST:
            case Flag.STAR_SECOND:
            case Flag.STAR_REFRESH:
                changeToStar(false);
                break;
            case Flag.STAR_CONTENT_FIRST:
            case Flag.STAR_CONTENT_SECOND:
            case Flag.STAR_CONTENT_REFRESH:
                setContentEmpty(false);
                setContentShown(true);

                bookmark.setVisible(true);
                actionBar.setTitle(title);
                actionBar.setSubtitle(subTitle);
                actionBar.setDisplayHomeAsUpEnabled(true);

                currentId = STAR_CONTENT_ID;
                listView.setAdapter(contentItemAdapter);
                contentItemAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    public void backFromCommit() {
        allTaskDown();

        switch (flag) {
            case Flag.REPO_COMMIT_FIRST:
            case Flag.REPO_COMMIT_REFRESH:
                changeToRepo(Flag.REPO_SECOND);
                break;
            case Flag.STAR_COMMIT_FIRST:
            case Flag.STAR_COMMIT_REFRESH:
                changeToStar(false);
                break;
            default:
                break;
        }
    }

    public void backToPrevious() {
        allTaskDown();

        String entryPath;
        try {
            entryPath = entry.getPath();
        } catch (NullPointerException n) {
            nullOrLow();
            return;
        }

        String[] entryArr = entryPath.split("/");
        if (entryArr.length <= 0) {
            nullOrLow();
        } else {
            Map<String, String> map = buffer.get(buffer.size() - 1);
            String prefix = map.get("prefix");
            owner = map.get("owner"); //
            name = map.get("name"); //

            contentItemList.clear();
            if (entryArr.length == 1) {
                if (prefix.equals("/")) {
                    title = name;
                    subTitle = name;
                } else {
                    title = prefix.split("/")[prefix.split("/").length - 1];
                    subTitle = name + "/" + prefix;
                }
                actionBar.setTitle(title);
                actionBar.setSubtitle(subTitle);
                actionBar.setDisplayHomeAsUpEnabled(true);

                for (TreeEntry e: root.getTree()) {
                    String temp = e.getPath();
                    if (temp.split("/").length == 1) {
                        contentItemList.add(new ContentItem(e));
                    }
                }
                entry = null;
            } else {
                String str = entryArr[0];
                for (int i = 1; i < entryArr.length - 1; i++) {
                    str = str + "/" + entryArr[i];
                }
                title = str.split("/")[str.split("/").length - 1];
                if (prefix.equals("/")) {
                    subTitle = name + "/" + str;
                } else {
                    subTitle = name + "/" + prefix + "/" + str;
                }
                actionBar.setTitle(title);
                actionBar.setSubtitle(subTitle);
                actionBar.setDisplayHomeAsUpEnabled(true);

                for (TreeEntry e : root.getTree()) {
                    String temp = e.getPath();
                    if (
                            (temp.split("/").length - 1 == str.split("/").length)
                            && temp.startsWith(str)
                    ) {
                        contentItemList.add(new ContentItem(e));
                    }
                    if (e.getPath().equals(str)) {
                        entry = e;
                    }
                }
            }
            Collections.sort(contentItemList);
            contentItemAdapter.notifyDataSetChanged();
        }
    }

    private void nullOrLow() {
        if (buffer.size() <= 1) {
            switch (flag) {
                case Flag.REPO_CONTENT_FIRST:
                case Flag.REPO_CONTENT_SECOND:
                case Flag.REPO_CONTENT_REFRESH:
                    changeToRepo(Flag.REPO_SECOND);
                    break;
                case Flag.STAR_CONTENT_FIRST:
                case Flag.STAR_CONTENT_SECOND:
                case Flag.STAR_CONTENT_REFRESH:
                    changeToStar(false);
                    break;
                default:
                    break;
            }
        } else {
            buffer.remove(buffer.size() - 1);
            try {
                root = roots.get(roots.size() - 2);
                roots.remove(roots.size() - 1);
            } catch (ArrayIndexOutOfBoundsException a) {
                root = roots.get(roots.size() - 1);
            }
            Map<String, String> map = buffer.get(buffer.size() - 1);

            String prefix = map.get("prefix");
            String path = map.get("suffix");
            owner = map.get("owner");
            name = map.get("name");

            setContentEmpty(false);
            setContentShown(true);
            contentItemList.clear();

            if (path.equals("/")) {
                if (prefix.equals("/")) {
                    title = name;
                    subTitle = name;
                } else {
                    title = prefix.split("/")[prefix.split("/").length - 1];
                    subTitle = name + "/" + prefix;
                }
                actionBar.setTitle(title);
                actionBar.setSubtitle(subTitle);
                actionBar.setDisplayHomeAsUpEnabled(true);

                for (TreeEntry e: root.getTree()) {
                    String temp = e.getPath();
                    if (temp.split("/").length == 1) {
                        contentItemList.add(new ContentItem(e));
                    }
                }
                entry = null;
            } else {
                String[] arr = path.split("/");
                title = arr[arr.length - 1];
                if (prefix.equals("/")) {
                    subTitle = name + "/" + path;
                } else {
                    subTitle = name + "/" + prefix + "/" + path;
                }
                actionBar.setTitle(title);
                actionBar.setSubtitle(subTitle);
                actionBar.setDisplayHomeAsUpEnabled(true);

                for (TreeEntry e : root.getTree()) {
                    String temp = e.getPath();
                    if (
                            (temp.split("/").length - 1 == arr.length)
                            && temp.startsWith(path)
                    ) {
                        contentItemList.add(new ContentItem(e));
                    }
                    if (temp.equals(path)) {
                        entry = e;
                    }
                }
            }
            Collections.sort(contentItemList);
            contentItemAdapter.notifyDataSetChanged();
        }
    }

    public void allTaskDown() {
        if (repoTask != null && repoTask.getStatus() == AsyncTask.Status.RUNNING) {
            repoTask.cancel(true);
        }
        if (bookmarkTask != null && bookmarkTask.getStatus() == AsyncTask.Status.RUNNING) {
            bookmarkTask.cancel(true);
        }
        if (starTask != null && starTask.getStatus() == AsyncTask.Status.RUNNING) {
            starTask.cancel(true);
        }
        if (addTask != null && addTask.getStatus() == AsyncTask.Status.RUNNING) {
            addTask.cancel(true);
        }
        if (repoContentTask != null && repoContentTask.getStatus() == AsyncTask.Status.RUNNING) {
            repoContentTask.cancel(true);
        }
        if (starContentTask != null && starContentTask.getStatus() == AsyncTask.Status.RUNNING) {
            starContentTask.cancel(true);
        }
        if (commitTask != null && commitTask.getStatus() == AsyncTask.Status.RUNNING) {
            commitTask.cancel(true);
        }
    }

    public void refreshAction() {
        switch (currentId) {
            case REPO_ID:
                if (repoTask != null && repoTask.getStatus() == AsyncTask.Status.FINISHED) {
                    if (flag == Flag.REPO_FIRST) {
                        if (isContentEmpty()) {
                            changeToRepo(Flag.REPO_FIRST);
                        }
                    } else {
                        changeToRepo(Flag.REPO_REFRESH);
                    }
                }
                break;
            case STAR_ID:
                if (starTask != null && starTask.getStatus() == AsyncTask.Status.FINISHED) {
                    changeToStar(true);
                }
                break;
            case BOOKMARK_ID:
                changeToBookmark();
                SuperToast.create(
                        view.getContext(),
                        view.getContext().getString(R.string.bookmark_refresh_successful),
                        SuperToast.Duration.VERY_SHORT,
                        Style.getStyle(Style.BLUE)
                ).show();
                break;
            case REPO_CONTENT_ID:
                if (repoContentTask != null && repoContentTask.getStatus() == AsyncTask.Status.FINISHED) {
                    if (entry == null) {
                        flag = Flag.REPO_CONTENT_REFRESH;
                        repoContentTask = new RepoContentTask(MainFragment.this);
                        repoContentTask.execute();
                    } else {
                        SuperToast.create(
                                view.getContext(),
                                view.getContext().getString(R.string.content_refresh_successful),
                                SuperToast.Duration.VERY_SHORT,
                                Style.getStyle(Style.BLUE)
                        ).show();
                    }
                }
                break;
            case STAR_CONTENT_ID:
                if (starContentTask != null && starContentTask.getStatus() == AsyncTask.Status.FINISHED) {
                    if (entry == null) {
                        flag = Flag.STAR_CONTENT_REFRESH;
                        starContentTask = new StarContentTask(MainFragment.this);
                        starContentTask.execute();
                    } else {
                        SuperToast.create(
                                view.getContext(),
                                view.getContext().getString(R.string.content_refresh_successful),
                                SuperToast.Duration.VERY_SHORT,
                                Style.getStyle(Style.BLUE)
                        ).show();
                    }
                }
                break;
            case COMMIT_ID:
                if (commitTask != null && commitTask.getStatus() == AsyncTask.Status.FINISHED) {
                    switch (flag) {
                        case Flag.REPO_COMMIT_FIRST:
                        case Flag.REPO_COMMIT_REFRESH:
                            changeToCommit(Flag.REPO_COMMIT_REFRESH);
                            break;
                        case Flag.STAR_COMMIT_FIRST:
                        case Flag.STAR_COMMIT_REFRESH:
                            changeToCommit(Flag.STAR_COMMIT_REFRESH);
                            break;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
    }
}
