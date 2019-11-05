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
import ca.mohawkcollege.bookclub.objects.Member;

public class MemberAdapter extends ArrayAdapter<Member> {

    private final ArrayList<Member> members;
    private final Context context;

    /**
     * Constructor to set context and arrayList variables for later use
     *
     * @param context              - context
     * @param memberInfoResourceId - member_info layout resource id
     */
    public MemberAdapter(Context context, int memberInfoResourceId) {
        super(context, memberInfoResourceId);
        this.context = context;
        this.members = new ArrayList<>();
    }

    /**
     * Adds current member info to members arrayList
     *
     * @param member - record to add to arrayList
     */
    @Override
    public void add(Member member) {
        super.add(member);
        members.add(member);
    }

    public Member getItem(int index) {
        return this.members.get(index);
    }

    /**
     * Overriding get view method to create a view for each item in the arrayList
     *
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

        Member member = members.get(position);

        // TODO: After creating user profiles, use their name from user profile
        TextView userNameTextView = listItem.findViewById(R.id.userNameTextView);
        userNameTextView.setText(member.userId);

        TextView phoneNumberTextView = listItem.findViewById(R.id.phoneNumberTextView);
        phoneNumberTextView.setText(member.phoneNumber);

        // TODO: Ensure imageView is not set if userPicImageView is empty
        /*ImageView userPicImageView = listItem.findViewById(R.id.userPicImageView);
        Glide.with(context)
                .load(Uri.parse(member.imageUrl))
                .into(userPicImageView);*/

        return listItem;
    }
}
