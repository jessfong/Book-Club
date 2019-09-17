package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class RetrieveClubInfo extends AppCompatActivity {

    FirebaseUser firebaseUser;
    //BookClub bookClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_club_info);

        // Get database references
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("BookClub");

        // Set up arrayList, adapter, and listView variables
        final ListView listView = findViewById(R.id.bookClubListView);
        final ArrayList<BookClub> arrayList = new ArrayList<BookClub>();
        final ArrayAdapter<BookClub> arrayAdapter = new ArrayAdapter<BookClub>(this, R.layout.book_club_info, R.id.infoNameTextView, arrayList);
        //bookClub = new BookClub();

        // TODO: DELETE after debugging
        Toast.makeText(RetrieveClubInfo.this, "hello", Toast.LENGTH_SHORT).show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren())
                {
                    // TODO: DELETE after debugging
                    Toast.makeText(RetrieveClubInfo.this, "hello1", Toast.LENGTH_SHORT).show();

                    // Converting data snapshot into book club type
                    BookClub bookClub = ds.getValue(BookClub.class);

                    //Toast.makeText(RetrieveClubInfo.this, firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
                    if(bookClub.userId == firebaseUser.getUid()){
                        arrayList.add(bookClub);
                    }
                }
                listView.setAdapter(arrayAdapter);
                Toast.makeText(RetrieveClubInfo.this, "Download completed.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
