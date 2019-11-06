package ca.mohawkcollege.bookclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.schibstedspain.leku.LocationPickerActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import ca.mohawkcollege.bookclub.objects.Attending;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.Member;

import static com.schibstedspain.leku.LocationPickerActivityKt.LATITUDE;
import static com.schibstedspain.leku.LocationPickerActivityKt.LOCATION_ADDRESS;
import static com.schibstedspain.leku.LocationPickerActivityKt.LONGITUDE;

public class CreateMeeting extends AppCompatActivity {

    private String dateText;
    private String startTimeText;
    private String endTimeText;
    public String bookTitle;
    public String authors;
    public String thumb;
    public double latitude;
    public double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_create_meeting);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get book club record id form last activity
        Intent intent = getIntent();
        final String bookClubId = intent.getStringExtra("recordId");

        Button locationBtn = findViewById(R.id.getLocationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent locationPickerIntent = new LocationPickerActivity.Builder()
                        .withGeolocApiKey("AIzaSyB-4S7datIzBLXeAs2LooGKru3VPRyvMXE")
                        .shouldReturnOkOnBackPressed()
                        .withGooglePlacesEnabled()
                        .withGoogleTimeZoneEnabled()
                        .build(getApplicationContext());

                startActivityForResult(locationPickerIntent, 5);
            }
        });

        final EditText dateEditText = findViewById(R.id.dateEditText);
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

        final EditText startTimeEditText = findViewById(R.id.startTimeEditText);
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
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    timeFormat.setLenient(false);
                    timeFormat.parse(startTimeEditText.getText().toString());
                    startTimeText = startTimeEditText.getText().toString();
                } catch (ParseException e) {
                    startTimeEditText.setError("Time must be in HH:mm format, using 24 hour time!");
                    startTimeText = null;
                }
            }
        });

        final EditText endTimeEditText = findViewById(R.id.endTimeEditText);
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
                    DateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    timeFormat.setLenient(false);
                    timeFormat.parse(endTimeEditText.getText().toString());
                    endTimeText = endTimeEditText.getText().toString();
                } catch (ParseException e) {
                    endTimeEditText.setError("Time must be in HH:mm format, using 24 hour time!");
                    endTimeText = null;
                }
            }
        });

        // When choose book button is clicked
        Button addBookBtn = findViewById(R.id.addBook);
        addBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateMeeting.this, BookSearch.class);
                startActivityForResult(intent, 102);
            }
        });

        // When invite members button is clicked a meeting invite gets sent to everyone in the club
        Button inviteButton = findViewById(R.id.setMeetingBtn);
        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText locationEditText = findViewById(R.id.locationEditText);
                if (locationEditText.getText() == null || TextUtils.isEmpty(locationEditText.getText())) {
                    Toast.makeText(getApplicationContext(), "Location cannot be empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String locationText = locationEditText.getText().toString();

                if (dateText == null) {
                    Toast.makeText(getApplicationContext(), "Date must be set.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (startTimeText == null) {
                    Toast.makeText(getApplicationContext(), "Start time must be set.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (endTimeText == null) {
                    Toast.makeText(getApplicationContext(), "End time must be set.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (bookTitle == null) {
                    Toast.makeText(getApplicationContext(), "A book must be selected.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create new firebase meeting object
                DatabaseReference meetings = FirebaseDatabase.getInstance().getReference("Meetings");
                String key = meetings.push().getKey();
                Meeting meeting = new Meeting(key, bookClubId, locationText, dateText, startTimeText, endTimeText, bookTitle, authors, thumb, latitude, longitude);
                meetings.child(key).setValue(meeting);

                DatabaseReference attending = FirebaseDatabase.getInstance().getReference("Attending");
                String attendingKey = attending.push().getKey();
                Attending attendingUser = new Attending(key, user.getUid());
                attending.child(attendingKey).setValue(attendingUser);

                Toast.makeText(getApplicationContext(), "Successfully created new meeting!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
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
                EditText locationText = findViewById(R.id.locationEditText);
                locationText.setText(address);

                latitude = data.getDoubleExtra(LATITUDE, 0.0);
                longitude = data.getDoubleExtra(LONGITUDE, 0.0);
            }

            if (requestCode == 102) {
                bookTitle = data.getStringExtra("title");
                authors = data.getStringExtra("authors");
                thumb = data.getStringExtra("thumb");
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
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
