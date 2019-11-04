package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.mohawkcollege.bookclub.objects.Member;
import ca.mohawkcollege.bookclub.objects.User;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private static int RC_SIGN_IN = 1;
    public static Context main;
    
    /**
     * Signs in user, retrieves list of user's book clubs, and allows users to create a new book club
     * @param savedInstanceState - saved data from last login
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_club_info);
        Intent intent = getParentActivityIntent();
        Intent intent1 = getIntent();
        main = this;

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
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Failed to get token", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                Toast.makeText(MainActivity.this, "New token 1: " + token, Toast.LENGTH_SHORT).show();
                                User user = new User(firebaseUser.getUid(), firebaseUser.getPhoneNumber(), firebaseUser.getEmail(), token);
                                mDatabase.child(user.userId).setValue(user);
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
                        String time = MainActivity.this.getIntent().getStringExtra("time");
                        String location = MainActivity.this.getIntent().getStringExtra("location");

                        Toast.makeText(main, location, Toast.LENGTH_SHORT).show();
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
