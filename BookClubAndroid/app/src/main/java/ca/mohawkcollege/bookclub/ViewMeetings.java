package ca.mohawkcollege.bookclub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ca.mohawkcollege.bookclub.helpers.MeetingAdaptor;
import ca.mohawkcollege.bookclub.objects.BookClub;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.User;

/**
 * View meetings activity
 */
public class ViewMeetings extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private BookClub bookClub;
    private String ownerName;

    /**
     * Overrides method to create view meeting layout
     * @param savedInstanceState - bundle data from last activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_view_meetings);

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Meetings");

        // Get book club record id form last activity
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        bookClub = (BookClub) bundle.getSerializable("recordId");

        // Get list of book club members attending the meeting
        DatabaseReference members = FirebaseDatabase.getInstance().getReference("Users");
        Query query = members.orderByChild("userId").equalTo(bookClub.clubOwner);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user == null)
                        continue;

                    String name = user.name;
                    if (name == null || TextUtils.isEmpty(name)) {
                        name = user.phoneNumber;
                    }

                    TextView adminNameTextView = findViewById(R.id.clubAdminTextView);
                    adminNameTextView.setText(name);
                    ownerName = name;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Set up adapter and listView
        final ListView listView = findViewById(R.id.meetingsListView);
        final MeetingAdaptor meetingAdaptor = new MeetingAdaptor(this, R.layout.book_meeting_info);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                meetingAdaptor.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final Meeting meeting = child.getValue(Meeting.class);
                    if (meeting != null && meeting.bookClubId.equals(bookClub.recordId)) {
                        meetingAdaptor.add(meeting);
                    }
                }

                meetingAdaptor.reverse();
                listView.setAdapter(meetingAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // When a meeting is selected from list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Meeting meeting = meetingAdaptor.getItem(i);
                if (meeting == null || ownerName == null)
                    return;

                Bundle bundle = new Bundle();
                Intent intent = new Intent(view.getContext(), AttendingActivity.class);
                bundle.putString("meetingID", meeting.meetingId);
                bundle.putString("owner", ownerName);
                bundle.putSerializable("meeting", meeting);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
}
