package io.github.mthli.Bitocle.Commit;

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

public class CommitItemAdapter extends ArrayAdapter<CommitItem> {
    private Context context;
    private int layoutResId;
    private List<CommitItem> commitItemList;

    public CommitItemAdapter (
            Context context,
            int layoutResId,
            List<CommitItem> commitItemList
    ) {
        super(context, layoutResId, commitItemList);

        this.context = context;
        this.layoutResId = layoutResId;
        this.commitItemList = commitItemList;
    }

    private class Holder {
        ImageView icon;
        TextView content;
        TextView date;
        TextView committer;
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
            holder.icon = (ImageView) view.findViewById(R.id.commit_item_icon);
            holder.committer = (TextView) view.findViewById(R.id.commit_item_committer);
            holder.date = (TextView) view.findViewById(R.id.commit_item_date);
            holder.content = (TextView) view.findViewById(R.id.commit_item_content);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        if (commitItemList.size() > 0) {
            CommitItem anItem = commitItemList.get(position);
            holder.icon.setImageDrawable(anItem.getIcon());
            holder.committer.setText(anItem.getCommitter());
            holder.date.setText(anItem.getDate());
            holder.content.setText(anItem.getContent());
        }

        return view;
    }
}
