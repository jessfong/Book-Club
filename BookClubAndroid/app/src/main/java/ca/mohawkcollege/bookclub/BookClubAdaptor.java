package ca.mohawkcollege.bookclub;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class BookClubAdaptor extends ArrayAdapter<BookClub> {

    private final Context context;
    private List<BookClub> bookClubs;

    /**
     * Constructor to set context and arrayList variables for later use
     * @param context - context
     * @param bookClubInfoResourceID - book_club_info layout resource id
     */
    BookClubAdaptor(@NonNull Context context, int bookClubInfoResourceID) {
        super(context, bookClubInfoResourceID);
        this.context = context;
        this.bookClubs = new ArrayList<>();
    }

    /**
     * Adds current book club info to bookClubs arrayList
     * @param bookClub - record to add to arrayList
     */
    @Override
    public void add(BookClub bookClub) {
        super.add(bookClub);
        bookClubs.add(bookClub);
    }

    /**
     * Clears adapter of all book club objects
     */
    @Override
    public void clear() {
        super.clear();
        bookClubs.clear();
    }

    public BookClub getItem(int index){
        return this.bookClubs.get(index);
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
            listItem = LayoutInflater.from(context).inflate(R.layout.book_club_info, parent, false);

        BookClub bookClub = bookClubs.get(position);

        TextView infoNameTextView = listItem.findViewById(R.id.bookClubNameTextView);
        infoNameTextView.setText(bookClub.name);

        TextView infoAdminNameTextView = listItem.findViewById(R.id.bookClubAdminTextView);
        infoAdminNameTextView.setText(bookClub.userId);

        ImageView infoBookClubImageView = listItem.findViewById(R.id.bookClubImageView);
        Glide.with(context)
                .load(Uri.parse(bookClub.imageUrl))
                .into(infoBookClubImageView);

        return listItem;
    }
}
