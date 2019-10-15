package ca.mohawkcollege.bookclub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class CreateMeeting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        // Inflates the top portion of creating meeting view
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CreateMeetingViewTop top = new CreateMeetingViewTop();
        fragmentTransaction.replace(R.id.createMeetingTop, top);
        
        // Inflates the bottom portion of creating meeting view
        CreateMeetingViewBottom bottom = new CreateMeetingViewBottom();
        fragmentTransaction.replace(R.id.createMeetingBottom, bottom);
        fragmentTransaction.commit();
    }
}
