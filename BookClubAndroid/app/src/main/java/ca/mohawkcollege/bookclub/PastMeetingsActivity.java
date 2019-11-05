package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ca.mohawkcollege.bookclub.helpers.MeetingAdaptor;
import ca.mohawkcollege.bookclub.objects.Attending;
import ca.mohawkcollege.bookclub.objects.AttendingView;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.User;

public class PastMeetingsActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_meetings);

        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("user");

        TextView userNameTextView = findViewById(R.id.userNameTextView);
        userNameTextView.setText(user.getName());

        final ListView listView = findViewById(R.id.pastMeetingsList);
        final MeetingAdaptor meetingAdaptor = new MeetingAdaptor(this, R.layout.book_meeting_info);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference attending = firebaseDatabase.getReference("Attending");
        Query query = attending.orderByChild("attendingUserId").equalTo(user.userId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(PastMeetingsActivity.this, "This user isn't attending any meetings.", Toast.LENGTH_SHORT).show();
                    return;
                }

                meetingAdaptor.clear();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final Attending attendingData = child.getValue(Attending.class);
                    if (attendingData != null) {
                        DatabaseReference meetings = FirebaseDatabase.getInstance().getReference("Meetings");
                        Query query = meetings.orderByChild("meetingId").equalTo(attendingData.meetingId);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    Meeting meeting = child.getValue(Meeting.class);
                                    if (meeting == null)
                                        continue;

                                    meetingAdaptor.add(meeting);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }

                meetingAdaptor.reverse();
                listView.setAdapter(meetingAdaptor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
