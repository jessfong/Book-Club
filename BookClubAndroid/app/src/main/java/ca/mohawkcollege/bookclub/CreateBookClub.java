package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CreateBookClub extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book_club);

        // Set instance of database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("BookClubs");

        // TODO: Ensure user is signed in

        // Getting book club name
        TextView name = findViewById(R.id.createBookClubNameEditText);
        final String nameText = name.getText().toString();

        // When create book club button is clicked
        Button button = findViewById(R.id.createBookClubBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               /* Map<String, Object> bookClub = new HashMap<>();
                bookClub.put("name", nameText);

                mDatabase.collection("cities").document("LA")
                        .set(bookClub)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "book club added", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "failed adding book club", Toast.LENGTH_SHORT).show();
                            }
                        });*/




                writeNewClub(nameText);
            }
        });
    }

    public void writeNewClub(String bookClubName){
        // Add new book club to database
        BookClub bookClub = new BookClub(bookClubName);

        String key = mDatabase.push().getKey();
        mDatabase.child(key).child(bookClubName).setValue(bookClub)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "book club added", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
