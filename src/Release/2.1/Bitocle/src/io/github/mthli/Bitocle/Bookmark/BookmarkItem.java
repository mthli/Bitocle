package io.github.mthli.Bitocle.Bookmark;

public class BookmarkItem {
    private String title;
    private String type;
    private String owner;
    private String name;
    private String path;
    private String sha;
    private String key;

    public BookmarkItem (
            String title,
            String type,
            String owner,
            String name,
            String path,
            String sha,
            String key
    ) {
        super();

        this.title = title;
        this.type = type;
        this.owner = owner;
        this.name = name;
        this.path = path;
        this.sha = sha;
        this.key = key;
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
}
