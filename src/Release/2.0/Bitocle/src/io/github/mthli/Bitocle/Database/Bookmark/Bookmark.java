package io.github.mthli.Bitocle.Database.Bookmark;

public class Bookmark {
    public static final String TABLE = "BOOKMARK";

    public static final String TITLE = "TITLE";
    public static final String DATE = "DATE";
    public static final String TYPE = "TYPE";
    public static final String REPO_OWNER = "REPO_OWNER";
    public static final String REPO_NAME = "REPO_NAME";
    public static final String REPO_PATH = "REPO_PATH";
    public static final String SHA = "SHA";
    public static final String KEY = "KEY";

    public static final String CREATE_SQL = "CREATE TABLE "
            + TABLE
            + " ("
            + " TITLE text,"
            + " DATE text,"
            + " TYPE text,"
            + " REPO_OWNER text,"
            + " REPO_NAME text,"
            + " REPO_PATH text,"
            + " SHA text,"
            + " KEY text"
            + ")";

    private String title;
    private String date;
    private String type;
    private String repoOwner;
    private String repoName;
    private String repoPath;
    private String sha;
    private String key;

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
}
