package ca.mohawkcollege.bookclub.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ca.mohawkcollege.bookclub.R;
import ca.mohawkcollege.bookclub.objects.AttendingView;
import ca.mohawkcollege.bookclub.objects.Member;

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

    public AttendingView getItem(int index){
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

        // TODO: After creating user profiles, use their name from user profile
        TextView userNameTextView = listItem.findViewById(R.id.attendingName);
        userNameTextView.setText(attendingView.name);

        return listItem;
    }
}
