package ca.mohawkcollege.bookclub.helpers;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ca.mohawkcollege.bookclub.R;
import ca.mohawkcollege.bookclub.objects.AttendingView;

/**
 * Attending members adapter
 */
public class AttendingAdapter extends ArrayAdapter<AttendingView> {

    private final ArrayList<AttendingView> attendingViews;
    private final Context context;

    /**
     * Constructor to set context and arrayList variables for later use
     * @param context - context
     * @param memberInfoResourceId - member_info layout resource id
     */
    public AttendingAdapter(Context context, int memberInfoResourceId) {
        super(context, memberInfoResourceId);
        this.context = context;
        this.attendingViews = new ArrayList<>();
    }

    /**
     * Adds current member info to members arrayList
     * @param attendingView - record to add to arrayList
     */
    @Override
    public void add(AttendingView attendingView) {
        super.add(attendingView);
        attendingViews.add(attendingView);
    }

    /**
     * Gets selected member from list
     * @param index index of member from list
     * @return view of for selected member
     */
    public AttendingView getItem(int index) {
        return this.attendingViews.get(index);
    }

    /**
     * Overriding get view method to create a view for each item in the arrayList
     * @param position - record number in the database
     * @param convertView - view of the listItem
     * @param parent - parent view of the listItem
     * @return custom view of listItem
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.book_attending_info, parent, false);

        AttendingView attendingView = attendingViews.get(position);

        TextView userNameTextView = listItem.findViewById(R.id.attendingName);
        userNameTextView.setText(attendingView.name);

        ImageView attendingImage = listItem.findViewById(R.id.attendingImage);

        if (attendingView.imageUrl != null && !TextUtils.isEmpty(attendingView.imageUrl)) {
            Glide.with(context)
                    .load(Uri.parse(attendingView.imageUrl))
                    .into(attendingImage);
        } else {
            attendingImage.setImageDrawable(getContext().getDrawable(R.mipmap.ic_no_image));
        }

        return listItem;
    }
}
