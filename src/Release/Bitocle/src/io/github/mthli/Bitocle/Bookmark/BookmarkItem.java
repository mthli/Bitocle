package io.github.mthli.Bitocle.Bookmark;

import android.graphics.drawable.Drawable;

public class BookmarkItem implements Comparable<BookmarkItem> {
    private Drawable icon;
    private String title;
    private String date;
    private String type;
    private String repoOwner;
    private String repoName;
    private String repoPath;
    private String sha;
    private String key;

    public BookmarkItem (
            Drawable icon,
            String title,
            String date,
            String type,
            String repoOwner,
            String repoName,
            String repoPath,
            String sha,
            String key
    ) {
        super();

        this.icon = icon;
        this.title = title;
        this.date = date;
        this.type = type;
        this.repoOwner = repoOwner;
        this.repoName = repoName;
        this.repoPath = repoPath;
        this.sha = sha;
        this.key = key;
    }

    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getRepoOwner() {
        return repoOwner;
    }
    public void setRepoOwner(String repoOwner) {
        this.repoOwner = repoOwner;
    }

    public String getRepoName() {
        return repoName;
    }
    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoPath() {
        return repoPath;
    }
    public void setRepoPath(String repoPath) {
        this.repoPath = repoPath;
    }

    public String getSha() {
        return sha;
    }
    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int compareTo(BookmarkItem bookmarkItem) {
        if (this.type != null) {
            return this.type.toLowerCase().compareTo(bookmarkItem.getType().toLowerCase());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
