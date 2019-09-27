package ca.mohawkcollege.bookclub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

public class BookClubDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_club_details);

        // Get club ids from previous activity
        ArrayList<String> clubIds = getIntent().getStringArrayListExtra("clubId");
        Iterator i = clubIds.iterator();

        // Get details for each book club
        while(i.hasNext()){
            Toast.makeText(BookClubDetails.this, "record Id = " + i.next(), Toast.LENGTH_SHORT).show();


        }
    }
}
