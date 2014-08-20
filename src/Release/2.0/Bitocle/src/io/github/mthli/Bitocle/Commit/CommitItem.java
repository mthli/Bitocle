package io.github.mthli.Bitocle.Commit;

import android.graphics.drawable.Drawable;

public class CommitItem {
    private Drawable icon;
    private String committer;
    private String date;
    private String content;

    public CommitItem (
            Drawable icon,
            String committer,
            String date,
            String content
    ) {
        super();

        this.icon = icon;
        this.committer = committer;
        this.date = date;
        this.content = content;
    }

    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getCommitter() {
        return committer;
    }
    public void setCommitter(String committer) {
        this.committer = committer;
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
}
