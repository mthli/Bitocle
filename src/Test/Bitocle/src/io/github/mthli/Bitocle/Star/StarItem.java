package io.github.mthli.Bitocle.Star;

import android.graphics.drawable.Drawable;

public class StarItem {
    private String name;
    private String date;
    private String description;
    private String lang;
    private int star;
    private int fork;
    private String owner;
    private String git;

    public StarItem(
            String name,
            String date,
            String description,
            String lang,
            int star,
            int fork,
            String owner,
            String git
    ) {
        super();

        this.name = name;
        this.date = date;
        this.description = description;
        this.lang = lang;
        this.star = star;
        this.fork = fork;
        this.owner = owner;
        this.git = git;
    }

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
}
