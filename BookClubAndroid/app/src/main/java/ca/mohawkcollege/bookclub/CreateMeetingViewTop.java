package ca.mohawkcollege.bookclub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;

import ca.mohawkcollege.bookclub.objects.BookClub;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.Member;
import ca.mohawkcollege.bookclub.objects.User;

public class CreateMeetingViewTop extends Fragment {

    public CreateMeetingViewTop() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View topView = inflater.inflate(R.layout.create_meeting_top, container, false);

        // Get book clud id from bundle
        final String bookClubId = getArguments().getString("bookClubId");

        // When invite members button is clicked a meeting invite gets sent to everyone in the club
        Button inviteButton = topView.findViewById(R.id.setMeetingBtn);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationEditText = topView.findViewById(R.id.locationEditText);
                final String locationText = locationEditText.getText().toString();

                EditText dateEditText = topView.findViewById(R.id.dateEditText);
                final String dateText = dateEditText.getText().toString();

                EditText timeEditText = topView.findViewById(R.id.timeEditText);
                final String timeText = timeEditText.getText().toString();

                // Create new firebase meeting object
                DatabaseReference meetings = FirebaseDatabase.getInstance().getReference("Meetings");
                Meeting meeting = new Meeting(bookClubId, locationText, dateText, timeText);
                String key = meetings.push().getKey();
                meetings.child(key).setValue(meeting);
                Toast.makeText(getContext(), "Successfully created new meeting!", Toast.LENGTH_SHORT).show();

                // Send notification to everyone in group

                /*getListOfAttendees(bookClubId, new onComplete() {
                    @Override
                    public void onComplete(StringBuilder listOfMembers) {
                        listOfMembers
                    }
                });*/
            }
        });

        return topView;
    }

    public interface onComplete {
        void onComplete(StringBuilder listOfMembers);   //Callback method notifies when done
    }


    // Finds each member in the club and adds them to a list
    public void getListOfAttendees(final String clubId, final onComplete onComplete) {
        final StringBuilder listOfMembers = new StringBuilder();

        DatabaseReference members = FirebaseDatabase.getInstance().getReference("Members");
        Query query = members.orderByChild("bookClubId").equalTo(clubId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Member member = child.getValue(Member.class);

                    if (member.bookClubId.equals(clubId)) {
                        listOfMembers.append(member.userId);
                    }
                }

                onComplete.onComplete(listOfMembers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
