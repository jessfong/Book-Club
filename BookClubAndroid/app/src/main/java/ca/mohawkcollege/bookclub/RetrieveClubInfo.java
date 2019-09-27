package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RetrieveClubInfo extends AppCompatActivity {

    FirebaseUser firebaseUser;
    ArrayList<String> clubIds = new ArrayList<String>();

    /**
     * Generates list of current user's book clubs
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
             * Reads and listens for changes that happen to firebase database.
             * Retrieves each child from BookClubs table and checks if each book club belongs to current user.
             * If book club belongs to user that book club is added to an arrayList.
             * When there are no more book clubs in the table the listView is populated with an
             * arrayAdapter that customizes the look of each view(Ex. textview, imageview, etc) in each listItem.
             * The listItem is then displayed in the parent view (the listView)
             * @param dataSnapshot - snapshot of BookClubs table from firebase
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bookClubAdaptor.clear();

                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    BookClub bookClub = child.getValue(BookClub.class);

                    String clubId = child.getKey();
                    clubIds.add(clubId);

                    if (bookClub == null) {
                        Toast.makeText(RetrieveClubInfo.this, "Book club error", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (bookClub.userId.equals(firebaseUser.getUid()))
                        bookClubAdaptor.add(bookClub);
                }

                listView.setAdapter(bookClubAdaptor);
                Toast.makeText(RetrieveClubInfo.this, "Download completed.", Toast.LENGTH_SHORT).show();

                int numItemsInListView = listView.getAdapter().getCount();
                Toast.makeText(RetrieveClubInfo.this, "number of items: " + numItemsInListView, Toast.LENGTH_SHORT).show();
            }

            /**
             * Called when there was an error retrieving the BookClubs table from firebase
             * @param databaseError - error that prevented retrieval of data
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


        // If create new book club is clicked
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


        // If list view item is clicked
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), BookClubDetails.class);
                intent.putExtra("clubId", clubIds);
                startActivity(intent);
            }
        });
    }
}
