package ca.mohawkcollege.bookclub.helpers;

import android.content.Context;
import android.net.Uri;
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
import java.util.Collections;
import java.util.List;

import ca.mohawkcollege.bookclub.R;
import ca.mohawkcollege.bookclub.objects.Meeting;

/**
 * Meeting adaptor
 */
public class MeetingAdaptor extends ArrayAdapter<Meeting> {

    private final Context context;
    private List<Meeting> meetings;

    /**
     * Constructor to set context and arrayList variables for later use
     * @param context - context
     * @param bookClubInfoResourceId - book_club_info layout resource id
     */
    public MeetingAdaptor(@NonNull Context context, int bookClubInfoResourceId) {
        super(context, bookClubInfoResourceId);
        this.context = context;
        this.meetings = new ArrayList<>();
    }

    /**
     * Adds current meeting to meetings arrayList
     * @param meeting - meeting to add
     */
    @Override
    public void add(Meeting meeting) {
        super.add(meeting);
        meetings.add(meeting);
    }

    /**
     * Reverse the order of meetings in the list
     */
    public void reverse() {
        Collections.reverse(meetings);
    }

    /**
     * Clears adapter of all meeting objects
     */
    @Override
    public void clear() {
        super.clear();
        meetings.clear();
    }

    /**
     * Gets selected meeting
     * @param index - index of selected meeting
     * @return view of selected meeting
     */
    public Meeting getItem(int index) {
        return this.meetings.get(index);
    }

    /**
     * Overriding get view method to create a view for each item in the arrayList
     * @param position    - record number in the database
     * @param convertView - view of the listItem
     * @param parent      - parent view of the listItem
     * @return custom view of listItem
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.book_meeting_info, parent, false);

        Meeting meeting = meetings.get(position);

        TextView infoNameTextView = listItem.findViewById(R.id.bookClubNameTextView);
        infoNameTextView.setText(meeting.location);

        TextView infoAdminNameTextView = listItem.findViewById(R.id.bookClubAdminTextView);
        infoAdminNameTextView.setText(meeting.date);

        ImageView imageView = listItem.findViewById(R.id.bookMeetingImage);
        if (meeting.bookThumb != null) {
            Glide.with(context)
                    .load(Uri.parse(meeting.bookThumb))
                    .into(imageView);
        } else {
            imageView.setImageDrawable(getContext().getDrawable(R.mipmap.ic_no_image));
        }

        return listItem;
    }
}
