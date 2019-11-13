package ca.mohawkcollege.bookclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ca.mohawkcollege.bookclub.helpers.BookItemAdaptor;
import ca.mohawkcollege.bookclub.helpers.DownloadAsyncTask;
import ca.mohawkcollege.bookclub.objects.bookobjects.Book;
import ca.mohawkcollege.bookclub.objects.bookobjects.Items;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Book club list
 */
public class BookList extends AppCompatActivity implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    private String url;
    private ListView bookList;
    private BookItemAdaptor bookItemAdaptor;
    public int totalBooks;

    /**
     * Overrides method to create book list layout
     * @param savedInstanceState - bundle data from last activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the list of found books
        bookItemAdaptor = new BookItemAdaptor(this, R.layout.book_item_info);
        url = getIntent().getStringExtra("search");
        bookList = findViewById(R.id.booksList);
        bookList.setOnScrollListener(this);
        bookList.setOnItemClickListener(this);

        // Download book items for the given search parameters
        DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.OnDownloadAsyncTask() {
            @Override
            public void onComplete(Book book) {
                if (book != null && book.items != null && !book.items.isEmpty()) {
                    for (Items items : book.items) {
                        bookItemAdaptor.add(items);
                    }

                    totalBooks = book.totalItems;
                    bookList.setAdapter(bookItemAdaptor);
                } else {
                    Toast.makeText(BookList.this, "No books found with given search term.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
        downloadAsyncTask.execute(url);
    }

    /**
     * Brings user to previous activity when back button is clicked
     * @param item - back button
     * @return view of previous activity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Overrides method to display list of book items on scroll
     * @param absListView - book items list view
     * @param firstIndex - index of first item in the list
     * @param visibleCount - visible count of items in the list
     * @param totalCount - total number of items in the list
     */
    @Override
    public void onScroll(AbsListView absListView, int firstIndex, int visibleCount, int totalCount) {}

    /**
     * Updates the view when the list is scrolled
     * @param absListView - book items list view
     * @param scrollState - state of view
     */
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && (bookList.getLastVisiblePosition() - bookList.getHeaderViewsCount() - bookList.getFooterViewsCount()) >= (bookItemAdaptor.getCount() - 1)) {
            if (totalBooks != bookItemAdaptor.getCount() - 1) {
                Toast.makeText(getApplicationContext(), "Loading books...", Toast.LENGTH_SHORT).show();

                String newUrl = url + "&startIndex=" + (bookItemAdaptor.getCount() - 1);

                DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(new DownloadAsyncTask.OnDownloadAsyncTask() {
                    @Override
                    public void onComplete(Book book) {
                        if (book != null) {
                            totalBooks = book.totalItems;

                            bookItemAdaptor.addAll(book.items);
                            bookItemAdaptor.notifyDataSetChanged();
                        }
                    }
                });
                downloadAsyncTask.execute(newUrl);
            } else {
                Toast.makeText(getApplicationContext(), "No more books!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Overrides method to display selected item from list
     * @param adapterView - book item adaptor view
     * @param view - current activity's view
     * @param i - index of item from list
     * @param l - default parameter
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Items items = bookItemAdaptor.getItem(i);

        Intent intent = new Intent(getApplicationContext(), CreateMeeting.class);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("title", items.volumeInfo.title);
        intent.putExtra("authors", items.getAuthors());

        if (items.volumeInfo.imageLinks != null) {
            if (items.volumeInfo.imageLinks.smallThumbnail != null) {
                intent.putExtra("thumb", items.volumeInfo.imageLinks.smallThumbnail);
            } else if (items.volumeInfo.imageLinks.thumbnail != null) {
                intent.putExtra("thumb", items.volumeInfo.imageLinks.thumbnail);
            }
        }

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
