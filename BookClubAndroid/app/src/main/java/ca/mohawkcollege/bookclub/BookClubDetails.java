package ca.mohawkcollege.bookclub;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import ca.mohawkcollege.bookclub.helpers.MemberAdapter;
import ca.mohawkcollege.bookclub.objects.BookClub;
import ca.mohawkcollege.bookclub.objects.Invite;
import ca.mohawkcollege.bookclub.objects.Member;
import ca.mohawkcollege.bookclub.objects.User;

public class BookClubDetails extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private BookClub bookClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_club_details);

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = firebaseDatabase.getReference();

        // Get book club record id form last activity
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        bookClub = (BookClub) bundle.getSerializable("recordId");

        // Setting details
        TextView clubNameTextView = findViewById(R.id.clubNameTextView);
        clubNameTextView.setText(bookClub.name);

        TextView adminNameTextView = findViewById(R.id.clubAdminTextView);
        adminNameTextView.setText(bookClub.clubOwner);

        ImageView infoBookClubImageView = findViewById(R.id.clubImageView);
        Glide.with(this)
                .load(Uri.parse(bookClub.imageUrl))
                .into(infoBookClubImageView);

        ListView listView = findViewById(R.id.membersListView);
        final MemberAdapter memberAdapter = new MemberAdapter(this, R.layout.member_info);
        DatabaseReference members = FirebaseDatabase.getInstance().getReference("Members");
        members.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Member member = child.getValue(Member.class);

                    if (member.bookClubId.equals(bookClub.recordId)) {
                        memberAdapter.add(member);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setAdapter(memberAdapter);

        // If user wants to delete a book club
        Button deleteClubBtn = findViewById(R.id.deleteClubBtn);
        deleteClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = firebaseUser.getUid();
                String clubOwner = bookClub.clubOwner;

                // If current user is club admin allow them to delete
                if (uid.equals(clubOwner)) {
                    databaseReference.child("BookClubs").child(bookClub.recordId)
                            .removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("DELETED", "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("ERROR DELETING", "Error deleting document", e);
                                }
                            });
                }
                Toast.makeText(BookClubDetails.this, "Only the club admin can delete.", Toast.LENGTH_SHORT).show();
            }
        });

        // If user wants to create a book club meeting, check if user is owner
        final Button createMeetingBtn = findViewById(R.id.createMeetingBtn);
        if (bookClub.clubOwner.equals(firebaseUser.getUid())) {
            createMeetingBtn.setVisibility(Button.VISIBLE);
        } else {
            createMeetingBtn.setVisibility(Button.GONE);
        }

        createMeetingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookClubDetails.this, CreateMeeting.class);
                intent.putExtra("recordId", bookClub.recordId);
                startActivity(intent);
            }
        });

        // Get phone contacts when user clicks on add members button
        Button addMembersBtn = findViewById(R.id.inviteToBookClubBtn);
        addMembersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(intent, 30);
            }
        });

        // If user wants to view meetings
        Button viewMeetingsBtn = findViewById(R.id.viewMeetingsBtn);
        viewMeetingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookClubDetails.this, ViewMeetings.class);
                intent.putExtra("recordId", bookClub);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 30 && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = "+1" + (cursor.getString(numberIndex).replace("(", "").replace(")", "").replace(" ", "").replace("-", "").replace("+1", ""));

                DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
                Query query = users.orderByChild("phoneNumber").equalTo(number);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                User user = child.getValue(User.class);

                                // Add user to invites table
                                // User id, Book club they are gonna join id
                                DatabaseReference invites = FirebaseDatabase.getInstance().getReference("Invites");
                                Invite invite = new Invite(user.userId, bookClub.recordId);
                                String key = invites.push().getKey();
                                invites.child(key).setValue(invite);
                            }
                        } else {
                            Toast.makeText(BookClubDetails.this, "Contact is not a book club user", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(BookClubDetails.this, "On canceled error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            cursor.close();
        }
    }
}
