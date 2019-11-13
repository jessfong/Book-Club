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

import ca.mohawkcollege.bookclub.R;
import ca.mohawkcollege.bookclub.objects.User;

/**
 * Member adaptor
 */
public class MemberAdapter extends ArrayAdapter<User> {

    private final ArrayList<User> members;
    private final Context context;

    /**
     * Constructor to set context and arrayList variables for later use
     * @param context - context
     * @param memberInfoResourceId - member_info layout resource id
     */
    public MemberAdapter(Context context, int memberInfoResourceId) {
        super(context, memberInfoResourceId);
        this.context = context;
        this.members = new ArrayList<>();
    }

    /**
     * Adds current member info to members arrayList
     * @param member - member to add
     */
    @Override
    public void add(User member) {
        super.add(member);
        members.add(member);
    }

    /**
     * Gets selected member from list
     * @param index - index of selected member
     * @return view of selected member
     */
    public User getItem(int index) {
        return this.members.get(index);
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
            listItem = LayoutInflater.from(context).inflate(R.layout.member_info, parent, false);

        User member = members.get(position);

        TextView userNameTextView = listItem.findViewById(R.id.userNameTextView);
        userNameTextView.setText(member.getName());

        ImageView userPicImageView = listItem.findViewById(R.id.userPicImageView);
        if (member.imageUrl != null) {
            Glide.with(context)
                    .load(Uri.parse(member.imageUrl))
                    .into(userPicImageView);
        } else {
            userPicImageView.setImageDrawable(getContext().getDrawable(R.mipmap.ic_no_image));
        }

        return listItem;
    }
}
