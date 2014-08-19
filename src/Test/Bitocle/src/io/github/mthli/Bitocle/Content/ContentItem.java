package io.github.mthli.Bitocle.Content;

import org.eclipse.egit.github.core.TreeEntry;

public class ContentItem implements Comparable<ContentItem> {
    private TreeEntry entry;

    public ContentItem(TreeEntry entry) {
        super();

        this.entry = entry;
    }

    public TreeEntry getEntry() {
        return entry;
    }
    public void setEntry(TreeEntry entry) {
        this.entry = entry;
    }

    @Override
    public int compareTo(ContentItem item) {
        if (this.entry.getType() != null) {
            return item.getEntry().getType().toLowerCase().compareTo(this.entry.getType().toLowerCase());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
