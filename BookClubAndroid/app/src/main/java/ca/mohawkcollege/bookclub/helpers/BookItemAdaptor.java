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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ca.mohawkcollege.bookclub.R;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.bookobjects.Items;


public class BookItemAdaptor extends ArrayAdapter<Items> {

    private final Context context;
    private List<Items> itemsList;

    /**
     *      * Constructor to set context and arrayList variables for later use
     *      * @param context - context
     *      * @param bookClubInfoResourceId - book_club_info layout resource id
     */
    public BookItemAdaptor(@NonNull Context context, int bookClubInfoResourceId) {
        super(context, bookClubInfoResourceId);
        this.context = context;
        this.itemsList = new ArrayList<>();
    }

    /**
     * Adds current book club info to bookClubs arrayList
     * @param items - record to add to arrayList
     */
    @Override
    public void add(Items items) {
        super.add(items);
        itemsList.add(items);
    }

    @Override
    public void addAll(Items... items) {
        super.addAll(items);
        for (Items item: items) {
            add(item);
        }
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public void addAll(Collection<? extends Items> collection) {
        if (collection != null) {
            super.addAll(collection);
            for (Items item: collection) {
                add(item);
            }
        }
    }

    public void reverse() {
        Collections.reverse(itemsList);
    }

    /**
     * Clears adapter of all book club objects
     */
    @Override
    public void clear() {
        super.clear();
        itemsList.clear();
    }

    public Items getItem(int index){
        return this.itemsList.get(index);
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
            listItem = LayoutInflater.from(context).inflate(R.layout.book_item_info, parent, false);

        Items items = itemsList.get(position);

        TextView title = listItem.findViewById(R.id.bookItemTitle);
        title.setText(items.volumeInfo.title);

        TextView authors = listItem.findViewById(R.id.bookItemAuthor);
        authors.setText(items.getAuthors());

        ImageView thumbnail = listItem.findViewById(R.id.bookItemImage);
        if (items.volumeInfo.imageLinks != null) {
            if (items.volumeInfo.imageLinks.smallThumbnail != null) {
                Glide.with(context)
                        .load(Uri.parse(items.volumeInfo.imageLinks.smallThumbnail))
                        .into(thumbnail);
            } else if (items.volumeInfo.imageLinks.thumbnail != null) {
                Glide.with(context)
                        .load(Uri.parse(items.volumeInfo.imageLinks.thumbnail))
                        .into(thumbnail);
            }
        } else {
            // TODO: No imaage placeholder?
        }

        return listItem;
    }
}
