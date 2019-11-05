package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import ca.mohawkcollege.bookclub.helpers.AttendingAdapter;
import ca.mohawkcollege.bookclub.helpers.MeetingAdaptor;
import ca.mohawkcollege.bookclub.objects.Attending;
import ca.mohawkcollege.bookclub.objects.AttendingView;
import ca.mohawkcollege.bookclub.objects.Meeting;
import ca.mohawkcollege.bookclub.objects.Member;
import ca.mohawkcollege.bookclub.objects.User;

public class AttendingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attending);

        final String meetingId = getIntent().getStringExtra("meetingID");

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = firebaseDatabase.getReference("Attending");

        final ListView listView = findViewById(R.id.attendingListView);
        final AttendingAdapter attendingAdaptor = new AttendingAdapter(this, R.layout.book_attending_info);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                attendingAdaptor.clear();

                for (DataSnapshot child: dataSnapshot.getChildren())
                {
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
                                    String contact = getContactName(getApplicationContext(), user.phoneNumber);
                                    if (contact == null) {
                                        name = user.phoneNumber;
                                    } else {
                                        name = contact;
                                    }
                                }

                                AttendingView attendingView = new AttendingView(name, user.imageUrl);
                                attendingAdaptor.add(attendingView);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) { }
                    });
                }

                listView.setAdapter(attendingAdaptor);
            }

            /**
             * Called when there was an error retrieving the Meetings table from firebase
             * @param databaseError - error that prevented retrieval of data
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public String getContactName(final String phoneNumber, Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Do not have permission to read contacts!", Toast.LENGTH_SHORT).show();
                return "";
            }
        }
        
        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName=cursor.getString(0);
            }
            cursor.close();
        }

        return contactName;
    }

    private String getContactName(Context context, String number) {

        String name = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Do not have permission to read contacts!", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        // define the columns I want the query to return
        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // query time
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            }

            cursor.close();
        }
        return name;
    }
}
