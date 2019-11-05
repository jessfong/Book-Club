package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ca.mohawkcollege.bookclub.objects.Attending;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.Member;
import ca.mohawkcollege.bookclub.objects.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static int RC_SIGN_IN = 1;

    /**
     * Signs in user, retrieves list of user's book clubs, and allows users to create a new book club
     * @param savedInstanceState - saved data from last login
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_club_info);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_CALENDAR},
                        1);
            }

            if (checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        2);
            }

            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        3);
            }
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = new ArrayList<>();
        providers.add(new AuthUI.IdpConfig.PhoneBuilder().build());
        //providers.add(new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        );
    }

    /**
     * Gets called whenever an activity returns a result.
     * When the requestCode is RC_SIGN_IN we are checking if the user is signed in.
     * If the resultCode is RESULT_OK user sign in was successful.
     * @param requestCode - code determining where the request came from
     * @param resultCode - code determining what the result was
     * @param data - data returned from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull final Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Failed to get token", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Get new Instance ID token
                                DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
                                Query query = users.orderByChild("userId").equalTo(firebaseUser.getUid());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            User user = child.getValue(User.class);
                                            if (user == null)
                                                continue;

                                            String token = task.getResult().getToken();
                                            user.token = token;
                                            user.phoneNumber = firebaseUser.getPhoneNumber();
                                            user.email = firebaseUser.getEmail();
                                            mDatabase.child(user.userId).setValue(user);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                                });
                            }
                        });


                if (MainActivity.this.getIntent().hasExtra("type")) {
                    String notificationType = MainActivity.this.getIntent().getStringExtra("type");

                    if (notificationType.equals("bookclub")) {
                        boolean accept = MainActivity.this.getIntent().getBooleanExtra("accept", false);
                        String recordId = MainActivity.this.getIntent().getStringExtra("recordId");

                        if (accept) {
                            DatabaseReference members = FirebaseDatabase.getInstance().getReference("Members");
                            Member member = new Member(firebaseUser.getUid(), recordId, firebaseUser.getPhoneNumber());
                            String key = members.push().getKey();
                            members.child(key).setValue(member);
                        }
                    } else {
                        boolean accept = MainActivity.this.getIntent().getBooleanExtra("accept", false);
                        String recordId = MainActivity.this.getIntent().getStringExtra("recordId");
                        String date = MainActivity.this.getIntent().getStringExtra("date");
                        String startTime = MainActivity.this.getIntent().getStringExtra("startTime");
                        String endTime = MainActivity.this.getIntent().getStringExtra("endTime");
                        String location = MainActivity.this.getIntent().getStringExtra("location");

                        if (accept) {
                            DatabaseReference attending = FirebaseDatabase.getInstance().getReference("Attending");
                            String key = attending.push().getKey();
                            Attending attendingUser = new Attending(recordId, firebaseUser.getUid());
                            attending.child(key).setValue(attendingUser);

                            // Split day and time of meeting
                            String[] dateSplit = date.split("/");
                            int day = Integer.parseInt(dateSplit[0]);
                            int month = Integer.parseInt(dateSplit[1]) - 1;
                            int year = Integer.parseInt(dateSplit[2]);

                            String[] startTimeSplit = startTime.split(":");
                            int startHour = Integer.parseInt(startTimeSplit[0]);
                            int startMinute = Integer.parseInt(startTimeSplit[1]);

                            String[] endTimeSplit = endTime.split(":");
                            int endHour = Integer.parseInt(endTimeSplit[0]);
                            int endMinute = Integer.parseInt(endTimeSplit[1]);

                            Calendar beginTime = Calendar.getInstance();
                            beginTime.set(year, month, day, startHour, startMinute);
                            Calendar endTimeCalendar = Calendar.getInstance();
                            endTimeCalendar.set(year, month, day, endHour, endMinute);
                            ContentResolver cr = getContentResolver();
                            ContentValues values = new ContentValues();
                            values.put(CalendarContract.Events.DTSTART, beginTime.getTimeInMillis());
                            values.put(CalendarContract.Events.DTEND, endTimeCalendar.getTimeInMillis());
                            values.put(CalendarContract.Events.TITLE, "BookClub");
                            values.put(CalendarContract.Events.DESCRIPTION, "BookClub Meeting");
                            values.put(CalendarContract.Events.EVENT_LOCATION, location);
                            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
                            values.put(CalendarContract.Events.CALENDAR_ID, 1);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(this, "No permission to write to calendar!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                                    Toast.makeText(this, "No permission to read calendar!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            Uri event = cr.insert(CalendarContract.Events.CONTENT_URI, values);

                            ContentValues reminderValues = new ContentValues();
                            reminderValues.put("event_id", Long.parseLong(event.getLastPathSegment()));
                            reminderValues.put("method", 1);
                            reminderValues.put("minutes", 60);
                            cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);

                            Toast.makeText(this, "Added meeting to calendar!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    int notiId = MainActivity.this.getIntent().getIntExtra("notiId", 0);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(notiId);
                }

                // Load list of user's book clubs
                Intent intent = new Intent(MainActivity.this, RetrieveClubInfo.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "User failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
