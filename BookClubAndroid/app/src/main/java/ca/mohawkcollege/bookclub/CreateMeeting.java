package ca.mohawkcollege.bookclub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import ca.mohawkcollege.bookclub.objects.BookClub;

public class CreateMeeting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        // Get book club record id form last activity
        Intent intent = getIntent();
        String bookClubId = intent.getStringExtra("recordId");

        // Declare fragment manager and transaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Inflates the top portion of creating meeting view
        CreateMeetingViewTop top = new CreateMeetingViewTop();
        Bundle bundle = new Bundle();
        bundle.putString("bookClubId", bookClubId);
        top.setArguments(bundle);
        fragmentTransaction.replace(R.id.createMeetingTop, top);

        // Inflates the bottom portion of creating meeting view
        CreateMeetingViewBottom bottom = new CreateMeetingViewBottom();
        fragmentTransaction.replace(R.id.createMeetingBottom, bottom);
        fragmentTransaction.commit();
    }
}
