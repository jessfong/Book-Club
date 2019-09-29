package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookClubDetails extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_club_details);

        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();

        // Get book club record id form last activity
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        final BookClub bookClub = (BookClub)bundle.getSerializable("recordId");

        // Setting details
        TextView clubNameTextView = findViewById(R.id.clubNameTextView);
        clubNameTextView.setText(bookClub.name);

        TextView adminNameTextView = findViewById(R.id.clubAdminTextView);
        adminNameTextView.setText(bookClub.userId);

        ImageView infoBookClubImageView = findViewById(R.id.clubImageView);
        Glide.with(this)
                .load(Uri.parse(bookClub.imageUrl))
                .into(infoBookClubImageView);

        // If user wants to delete a book club
        Button deleteClubBtn = findViewById(R.id.deleteClubBtn);
        deleteClubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // If current user is club admin allow them to delete
                if(user.getUid().equals(bookClub.userId)){
                    Toast.makeText(BookClubDetails.this, "Allowed to delete", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(BookClubDetails.this, "Not allowed to delete", Toast.LENGTH_LONG).show();
                Log.i("userId", "Your id: " + bookClub.userId + ". adminId: " + user.getUid() + ".");
            }
        });


        // Populate members list view (after adding functionality to add members)
        DatabaseReference databaseReference = firebaseDatabase.getReference("BookClubs/" + bookClub.recordId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
