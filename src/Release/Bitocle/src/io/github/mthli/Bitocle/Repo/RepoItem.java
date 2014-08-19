package io.github.mthli.Bitocle.Repo;

import android.graphics.drawable.Drawable;

public class RepoItem implements Comparable<RepoItem> {
    private Drawable icon;
    private String title;
    private String date;
    private String content;
    private String info;
    private String owner;
    private String git;

    public RepoItem(
            Drawable icon,
            String title,
            String date,
            String content,
            String info,
            String owner,
            String git
    ) {
        super();

        this.icon = icon;
        this.title = title;
        this.date = date;
        this.content = content;
        this.owner = owner;
        this.info = info;
        this.git = git;
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

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public String getGit() {
        return git;
    }
    public void setGit(String git) {
        this.git = git;
    }

    @Override
    public int compareTo(RepoItem repoItem) {
        if (this.title != null) {
            return this.title.toLowerCase().compareTo(repoItem.getTitle().toLowerCase());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
