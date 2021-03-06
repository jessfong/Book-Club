package ca.mohawkcollege.bookclub;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ca.mohawkcollege.bookclub.helpers.BookClubAdaptor;
import ca.mohawkcollege.bookclub.objects.BookClub;
import ca.mohawkcollege.bookclub.objects.Member;

/**
 * Retrieve club info activity
 * Generates list of current user's book clubs
 */
public class RetrieveClubInfo extends AppCompatActivity {

    FirebaseUser firebaseUser;

    /**
     * Overrides method to create retrieve club info layout
     * @param savedInstanceState - saved data from last login
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_club_info);

        // Get database references
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("BookClubs");

        // Set up adapter and listView
        final ListView listView = findViewById(R.id.bookClubListView);
        final BookClubAdaptor bookClubAdaptor = new BookClubAdaptor(this, R.layout.book_club_info);

        databaseReference.addValueEventListener(new ValueEventListener() {
            /**
             * Retrieves each child from BookClubs table and checks if book club belongs to
             * current user or if they are a member of that club.
             * If user is the book club owner or if they are a member of the book club the club is added to an arrayList.
             * When there are no more book clubs in the table the listView is populated with an
             * arrayAdapter that customizes the look of each view(Ex. textview, imageview, etc) in each listItem.
             * The listItem is then displayed in the parent view (the listView)
             * @param dataSnapshot - snapshot of BookClubs table from firebase
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookClubAdaptor.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final BookClub bookClub = child.getValue(BookClub.class);

                    if (bookClub == null) {
                        Toast.makeText(RetrieveClubInfo.this, "Book club error", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Checks which book clubs user is a member of
                    DatabaseReference members = FirebaseDatabase.getInstance().getReference("Members");
                    Query query = members.orderByChild("bookClubId").equalTo(bookClub.recordId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Member member = child.getValue(Member.class);
                                if (member == null)
                                    continue;

                                if (member.userId.equals(firebaseUser.getUid())) {
                                    bookClubAdaptor.add(bookClub);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    // If user is owner of book club add to list of book clubs to display
                    if (bookClub.clubOwner.equals(firebaseUser.getUid()))
                        bookClubAdaptor.add(bookClub);
                }

                listView.setAdapter(bookClubAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        // Start activity when create book club button is clicked
        FloatingActionButton createButton = findViewById(R.id.createBookClubBtn);
        createButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Directs user to next CreateBookClub activity
             * @param view - current view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateBookClub.class);
                startActivity(intent);
            }
        });


        // Start activity when list book club in list is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(view.getContext(), BookClubDetails.class);
                bundle.putSerializable("recordId", bookClubAdaptor.getItem(i));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * Generate list items in view
     * @param menu - list item
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * Start activity when wrench is clicked
     * @param item - wrench icon
     * @return user profile activity
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_user_profile) {
            Intent intent = new Intent(getApplicationContext(), UserProfile.class);
            startActivity(intent);
        }

        return true;
    }
}
