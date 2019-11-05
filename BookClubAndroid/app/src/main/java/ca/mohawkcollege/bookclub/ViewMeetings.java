package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;

import ca.mohawkcollege.bookclub.helpers.BookClubAdaptor;
import ca.mohawkcollege.bookclub.helpers.MeetingAdaptor;
import ca.mohawkcollege.bookclub.objects.BookClub;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.Member;

public class ViewMeetings extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private BookClub bookClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meetings);

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = firebaseDatabase.getReference("Meetings");

        // Get book club record id form last activity
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        bookClub = (BookClub)bundle.getSerializable("recordId");

        TextView adminNameTextView = findViewById(R.id.clubAdminTextView);
        adminNameTextView.setText(bookClub.clubOwner);

        // Set up adapter and listView
        final ListView listView = findViewById(R.id.meetingsListView);
        final MeetingAdaptor meetingAdaptor = new MeetingAdaptor(this, R.layout.book_meeting_info);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                meetingAdaptor.clear();

                for (DataSnapshot child: dataSnapshot.getChildren())
                {
                    final Meeting meeting = child.getValue(Meeting.class);
                    if (meeting != null && meeting.bookClubId.equals(bookClub.recordId)) {
                        meetingAdaptor.add(meeting);
                    }
                }

                meetingAdaptor.reverse();
                listView.setAdapter(meetingAdaptor);
            }

            /**
             * Called when there was an error retrieving the Meetings table from firebase
             * @param databaseError - error that prevented retrieval of data
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(view.getContext(), AttendingActivity.class);
                bundle.putString("meetingID", meetingAdaptor.getItem(i).meetingId);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
