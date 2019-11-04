package ca.mohawkcollege.bookclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.schibstedspain.leku.LocationPickerActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ca.mohawkcollege.bookclub.objects.BookClub;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.Member;
import ca.mohawkcollege.bookclub.objects.User;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;

public class CreateMeetingViewTop extends Fragment {

    private String dateText;
    private String startTimeText;
    private String endTimeText;

    public CreateMeetingViewTop() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View topView = inflater.inflate(R.layout.create_meeting_top, container, false);

        // Get book clud id from bundle
        final String bookClubId = getArguments().getString("bookClubId");

        final EditText location = topView.findViewById(R.id.locationEditText);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationPickerIntent = new LocationPickerActivity.Builder()
                        .withGeolocApiKey("AIzaSyB-4S7datIzBLXeAs2LooGKru3VPRyvMXE")
                        .shouldReturnOkOnBackPressed()
                        .withGooglePlacesEnabled()
                        .withGoogleTimeZoneEnabled()
                        .build(topView.getContext());

                startActivityForResult(locationPickerIntent, 5);
            }
        });

        final EditText dateEditText = topView.findViewById(R.id.dateEditText);
        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy");
                    dateFormat.setLenient(false);
                    dateFormat.parse(dateEditText.getText().toString());
                    dateText = dateEditText.getText().toString();
                } catch (ParseException e) {
                    dateEditText.setError("Date must be in dd/M/yyyy format!");
                    dateText = null;
                }
            }
        });

        final EditText startTimeEditText = topView.findViewById(R.id.startTimeEditText);
        startTimeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    DateFormat timeFormat = new SimpleDateFormat("hh:mm");
                    timeFormat.setLenient(false);
                    timeFormat.parse(startTimeEditText.getText().toString());
                    startTimeText = startTimeEditText.getText().toString();
                } catch (ParseException e) {
                    startTimeEditText.setError("Time must be in hh:mm format!");
                    startTimeText = null;
                }
            }
        });

        final EditText endTimeEditText = topView.findViewById(R.id.endTimeEditText);
        endTimeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                try {
                    DateFormat timeFormat = new SimpleDateFormat("hh:mm");
                    timeFormat.setLenient(false);
                    timeFormat.parse(endTimeEditText.getText().toString());
                    endTimeText = endTimeEditText.getText().toString();
                } catch (ParseException e) {
                    endTimeEditText.setError("Time must be in hh:mm format!");
                    endTimeText = null;
                }
            }
        });

        // When invite members button is clicked a meeting invite gets sent to everyone in the club
        Button inviteButton = topView.findViewById(R.id.setMeetingBtn);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationEditText = topView.findViewById(R.id.locationEditText);
                if (locationEditText.getText() == null || TextUtils.isEmpty(locationEditText.getText())) {
                    Toast.makeText(getContext(), "Location cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String locationText = locationEditText.getText().toString();

                if (dateText == null) {
                    Toast.makeText(getContext(), "Date cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (startTimeText == null) {
                    Toast.makeText(getContext(), "Time cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (endTimeText == null) {
                    Toast.makeText(getContext(), "Time cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new firebase meeting object
                DatabaseReference meetings = FirebaseDatabase.getInstance().getReference("Meetings");
                Meeting meeting = new Meeting(bookClubId, locationText, dateText, startTimeText, endTimeText);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == 5) {
                String address = data.getStringExtra(LOCATION_ADDRESS);
                Toast.makeText(getContext(), address, Toast.LENGTH_SHORT).show();
            }
        }
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
