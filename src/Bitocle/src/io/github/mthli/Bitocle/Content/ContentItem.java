package io.github.mthli.Bitocle.Content;

import android.graphics.drawable.Drawable;

public class ContentItem implements Comparable<ContentItem> {
    private Drawable icon;
    private String title;
    private String type;
    private long size;
    private String repoPath;
    private String sha;

    public ContentItem(
            Drawable icon,
            String title,
            String type,
            long size,
            String repoPath,
            String sha
    ) {
        super();

        this.icon = icon;
        this.title = title;
        this.type = type;
        this.size = size;
        this.repoPath = repoPath;
        this.sha = sha;
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

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public String getRepoPath() {
        return repoPath;
    }
    public void setRepoPath(String path) {
        this.repoPath = path;
    }

    public String getSha() {
        return sha;
    }
    public void setSha(String sha) {
        this.sha = sha;
    }

    @Override
    public int compareTo(ContentItem contentItem) {
        if (this.type != null) {
            return this.type.toLowerCase().compareTo(contentItem.getType().toLowerCase());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
