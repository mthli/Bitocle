package io.github.mthli.Bitocle.Database.Repo;

public class Repo {
    public static final String TABLE = "REPO";
    public static final String TITLE = "TITLE";
    public static final String DATE = "DATE";
    public static final String CONTENT = "CONTENT";
    public static final String INFO = "INFO";
    public static final String OWNER = "OWNER";
    public static final String GIT = "GIT";

    public static final String CREATE_SQL = "CREATE TABLE "
            + TABLE
            + " ("
            + " TITLE text,"
            + " DATE text,"
            + " CONTENT text,"
            + " INFO text,"
            + " OWNER text,"
            + " GIT text"
            + ")";

    private String title;
    private String date;
    private String content;
    private String info;
    private String owner;
    private String git;

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

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getGit() {
        return git;
    }
    public void setGit(String git) {
        this.git = git;
    }
}
