package io.github.mthli.Bitocle.Bookmark;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import io.github.mthli.Bitocle.R;
import org.eclipse.egit.github.core.RepositoryContents;

import java.util.List;

public class BookmarkItemAdapter extends ArrayAdapter<BookmarkItem> {
    private Context context;
    private int layoutResId;
    private List<BookmarkItem> bookmarkItemList;

    public BookmarkItemAdapter (
            Context context,
            int layoutRedId,
            List<BookmarkItem> bookmarkItemList
    ) {
        super(context, layoutRedId, bookmarkItemList);

        this.context = context;
        this.layoutResId = layoutRedId;
        this.bookmarkItemList = bookmarkItemList;
    }

    private class Holder {
        ImageView icon;
        TextView title;
        TextView date;
        TextView content;
        TextView owner;
    }

    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup viewGroup
    ) {
        Holder holder;
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(layoutResId, viewGroup, false);

            holder = new Holder();
            holder.icon = (ImageView) view.findViewById(R.id.bookmark_item_icon);
            holder.title = (TextView) view.findViewById(R.id.bookmark_item_title);
            holder.date = (TextView) view.findViewById(R.id.bookmark_item_date);
            holder.content = (TextView) view.findViewById(R.id.bookmark_item_content);
            holder.owner = (TextView) view.findViewById(R.id.bookmark_item_owner);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        if (bookmarkItemList.size() > 0) {
            BookmarkItem anItem = bookmarkItemList.get(position);
            holder.icon.setImageDrawable(anItem.getIcon());
            holder.title.setText(anItem.getTitle());
            holder.date.setText(anItem.getDate());
            String str = anItem.getRepoName() + context.getString(R.string.repo_path_root) + anItem.getRepoPath();
            if (anItem.getType().equals(RepositoryContents.TYPE_DIR)) {
                str = str + context.getString(R.string.repo_path_root);
            }
            holder.content.setText(str);
            holder.owner.setText(anItem.getRepoOwner());
        }

        return view;
    }
}
