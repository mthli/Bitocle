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

import java.util.List;

public class BookmarkItemAdapter extends ArrayAdapter<BookmarkItem> {
    private Context context;
    private int layoutResId;
    private List<BookmarkItem> list;

    public BookmarkItemAdapter (
            Context context,
            int layoutRedId,
            List<BookmarkItem> list
    ) {
        super(context, layoutRedId, list);

        this.context = context;
        this.layoutResId = layoutRedId;
        this.list = list;
    }

    private class Holder {
        ImageView icon;
        TextView title;
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
            holder.content = (TextView) view.findViewById(R.id.bookmark_item_content);
            holder.owner = (TextView) view.findViewById(R.id.bookmark_item_owner);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        BookmarkItem item = list.get(position);
        if (item.getType().equals("tree")) {
            holder.icon.setImageDrawable(
                    context.getResources().getDrawable(R.drawable.ic_type_folder)
            );
        } else {
            holder.icon.setImageDrawable(
                    context.getResources().getDrawable(R.drawable.ic_type_file)
            );
        }
        holder.content.setText(item.getName()
                        + "/"
                        + item.getPath()
        );
        holder.title.setText(item.getTitle());
        holder.owner.setText(item.getOwner());

        return view;
    }
}