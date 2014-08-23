package io.github.mthli.Bitocle.Database.Bookmark;

public class Bookmark implements Comparable<Bookmark> {
    public static final String TABLE = "BOOKMARK";

    public static final String TITLE = "TITLE";
    public static final String TYPE = "TYPE";
    public static final String OWNER = "OWNER";
    public static final String NAME = "NAME";
    public static final String PATH = "PATH";
    public static final String SHA = "SHA"; //
    public static final String KEY = "KEY"; //

    public static final String CREATE_SQL = "CREATE TABLE "
            + TABLE
            + " ("
            + " TITLE text,"
            + " TYPE text,"
            + " OWNER text,"
            + " NAME text,"
            + " PATH text,"
            + " SHA text,"
            + " KEY text"
            + ")";

    private String title;
    private String type;
    private String owner;
    private String name;
    private String path;
    private String sha;
    private String key;

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

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
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
    public int compareTo(Bookmark item) {
        if (this.type != null) {
            return item.getType().toLowerCase().compareTo(this.type.toLowerCase());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
