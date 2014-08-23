package io.github.mthli.Bitocle.Database.Repo;

public class Repo implements Comparable<Repo> {
    public static final String TABLE = "REPO";
    public static final String NAME = "NAME";
    public static final String DATE = "DATE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String LANG = "LANG";
    public static final String STAR = "STAR";
    public static final String FORK = "FORK";
    public static final String OWNER = "OWNER";
    public static final String GIT = "GIT";

    public static final String CREATE_SQL = "CREATE TABLE "
            + TABLE
            + " ("
            + " NAME text,"
            + " DATE text,"
            + " DESCRIPTION text,"
            + " LANG text,"
            + " STAR integer,"
            + " FORK integer,"
            + " OWNER text,"
            + " GIT text"
            + ")";

    private String name;
    private String date;
    private String description;
    private String lang;
    private int star;
    private int fork;
    private String owner;
    private String git;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getLang() {
        return lang;
    }
    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getStar() {
        return star;
    }
    public void setStar(int star) {
        this.star = star;
    }

    public int getFork() {
        return fork;
    }
    public void setFork(int fork) {
        this.fork = fork;
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

    @Override
    public int compareTo(Repo repo) {
        if (this.star >= 0) {
            return ((Integer) repo.getStar()).compareTo(this.star);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
