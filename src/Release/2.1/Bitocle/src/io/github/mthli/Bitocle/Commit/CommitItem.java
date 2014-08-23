package io.github.mthli.Bitocle.Commit;

import android.graphics.drawable.Drawable;

public class CommitItem {
    private String committer;
    private String date;
    private String message;

    public CommitItem (
            String committer,
            String date,
            String message
    ) {
        super();

        this.committer = committer;
        this.date = date;
        this.message = message;
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
        return message;
    }
    public void setContent(String message) {
        this.message = message;
    }
}
