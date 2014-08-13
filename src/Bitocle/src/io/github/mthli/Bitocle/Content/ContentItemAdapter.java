package io.github.mthli.Bitocle.Content;

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

import java.math.BigDecimal;
import java.util.List;

public class ContentItemAdapter extends ArrayAdapter<ContentItem> {
    private Context context;
    private int layoutResId;
    private List<ContentItem> contentItemList;

    public ContentItemAdapter(
            Context context,
            int layoutResId,
            List<ContentItem> contentItemList
    ) {
        super(context, layoutResId, contentItemList);

        this.context = context;
        this.layoutResId = layoutResId;
        this.contentItemList = contentItemList;
    }

    private class Holder {
        ImageView icon;
        TextView title;
        TextView info;
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
            holder.icon = (ImageView) view.findViewById(R.id.content_item_icon);
            holder.title = (TextView) view.findViewById(R.id.content_item_title);
            holder.info = (TextView) view.findViewById(R.id.content_item_info);

            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        ContentItem anItem = contentItemList.get(position);
        holder.icon.setImageDrawable(anItem.getIcon());
        holder.title.setText(anItem.getTitle());
        if (anItem.getType().equals(RepositoryContents.TYPE_DIR)) {
            holder.info.setText(context.getString(R.string.content_item_info_sharp));
        } else {
            double r = round(anItem.getSize() / 1024);
            String size = r + " " + context.getString(R.string.content_item_info_kb);
            holder.info.setText(size);
        }

        return view;
    }

    private static double round(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        double d = bigDecimal.doubleValue();

        return d;
    }
}
