package ca.mohawkcollege.bookclub;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ca.mohawkcollege.bookclub.helpers.AttendingAdapter;
import ca.mohawkcollege.bookclub.objects.Attending;
import ca.mohawkcollege.bookclub.objects.AttendingView;
import ca.mohawkcollege.bookclub.objects.BookClub;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.User;

/**
 * Attending activity
 */
public class AttendingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Meeting meeting;
    private BookClub bookClub;

    /**
     * Overrides method to create attending layout
     * @param savedInstanceState - bundle data from last activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_attending);

        final String meetingId = getIntent().getStringExtra("meetingID");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Bundle bundle = getIntent().getExtras();
        meeting = (Meeting) bundle.getSerializable("meeting");

        // Deletes meeting from firebase when the delete meeting button is clicked
        final Button deleteMeeting = findViewById(R.id.deleteMeeting);
        deleteMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookClub != null && firebaseUser.getUid().equals(bookClub.clubOwner)) {
                    DatabaseReference meetings = FirebaseDatabase.getInstance().getReference("Meetings");
                    meetings.child(meeting.meetingId)
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("DELETED", "DocumentSnapshot successfully deleted!");
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("ERROR DELETING", "Error deleting document", e);
                                }
                            });

                    Toast.makeText(AttendingActivity.this, "Deleted meeting!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        // Gets list of book club members
        DatabaseReference members = FirebaseDatabase.getInstance().getReference("BookClubs");
        Query query = members.orderByChild("recordId").equalTo(meeting.bookClubId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    bookClub = child.getValue(BookClub.class);
                    if (bookClub == null)
                        continue;

                    if (!firebaseUser.getUid().equals(bookClub.clubOwner)) {
                        deleteMeeting.setVisibility(Button.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        TextView owner = findViewById(R.id.meetingTextView);
        owner.setText(getIntent().getStringExtra("owner"));

        TextView location = findViewById(R.id.meetingLocation);
        location.setText(meeting.location);

        TextView date = findViewById(R.id.meetingDate);
        date.setText(String.format("%s @ %s - %s", meeting.date, meeting.startTime, meeting.endTime));

        // Display map of meeting location
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Attending");

        final ListView listView = findViewById(R.id.attendingListView);
        final AttendingAdapter attendingAdaptor = new AttendingAdapter(this, R.layout.book_attending_info);

        // Gets list of attendees
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                attendingAdaptor.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final Attending attending = child.getValue(Attending.class);
                    if (attending == null || !attending.meetingId.equals(meetingId))
                        continue;

                    // Checks which book clubs user is a member of
                    DatabaseReference members = FirebaseDatabase.getInstance().getReference("Users");
                    Query query = members.orderByChild("userId").equalTo(attending.attendingUserId);
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

                                AttendingView attendingView = new AttendingView(name, user.imageUrl);
                                attendingAdaptor.add(attendingView);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                }

                listView.setAdapter(attendingAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    /**
     * Displays location of the meeting on a google map
     * @param googleMap - map of meeting location
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (meeting != null) {
            LatLng location = new LatLng(meeting.latitude, meeting.longitude);
            googleMap.addMarker(new MarkerOptions().position(location)
                    .title("BookClub Location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
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
