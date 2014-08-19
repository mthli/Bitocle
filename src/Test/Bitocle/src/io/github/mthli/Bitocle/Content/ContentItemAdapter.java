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
import org.eclipse.egit.github.core.TreeEntry;

import java.math.BigDecimal;
import java.util.List;

public class ContentItemAdapter extends ArrayAdapter<ContentItem> {
    private Context context;
    private int layoutResId;
    private List<ContentItem> list;

    public ContentItemAdapter(
            Context context,
            int layoutResId,
            List<ContentItem> list
    ) {
        super(context, layoutResId, list);

        this.context = context;
        this.layoutResId = layoutResId;
        this.list = list;
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

        ContentItem item = list.get(position);
        TreeEntry entry = item.getEntry();
        if (entry.getType().equals("tree")) {
            holder.icon.setImageDrawable(
                    context.getResources().getDrawable(R.drawable.ic_type_folder)
            );
            holder.info.setText("#");
        } else {
            holder.icon.setImageDrawable(
                    context.getResources().getDrawable(R.drawable.ic_type_file)
            );
            double r = round(item.getEntry().getSize() / 1024);
            String size = r + " " + "KB";
            holder.info.setText(size);
        }
        holder.title.setText(getName(item.getEntry().getPath()));

        return view;
    }

    private static String getName(String path) {
        String[] arr = path.split("/");
        return arr[arr.length - 1];
    }

    /* Maybe do something */
    private static double round(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
        double d = bigDecimal.doubleValue();

        return d;
    }
}
