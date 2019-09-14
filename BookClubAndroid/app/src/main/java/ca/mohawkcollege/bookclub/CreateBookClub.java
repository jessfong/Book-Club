package ca.mohawkcollege.bookclub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateBookClub extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book_club);

        // Set instance of database and user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("BookClubs");

        final TextView name = findViewById(R.id.createBookClubNameEditText);

        // Ensure user is signed in
        if(user != null){
            // When create book club button is clicked
            Button button = findViewById(R.id.createBookClubBtn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    writeNewClub(user.getUid(), name.getText().toString());
                }
            });
        }
    }

    // Add new book club to database
    public void writeNewClub(String user, String bookClubName){
        String key = mDatabase.push().getKey();

        if (bookClubName != null) {
            BookClub bookClub = new BookClub(user, bookClubName);
            mDatabase.child(key).setValue(bookClub);
        }
        else{
            Toast.makeText(getApplicationContext(), "Book club name cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }
}
