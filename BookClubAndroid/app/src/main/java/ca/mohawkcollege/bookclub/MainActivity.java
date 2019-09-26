package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static int RC_SIGN_IN = 1;

    /**
     * Signs in user, retrieves list of user's book clubs, and allows users to create a new book club
     * @param savedInstanceState - saved data from last login
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mAuth = FirebaseAuth.getInstance();

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        );

        // Load list of user's book clubs
        Intent intent = new Intent(MainActivity.this, RetrieveClubInfo.class);
        startActivity(intent);


        // TODO: Change button to Floating Action Button
        Button createButton = findViewById(R.id.createBookClubBtn);
        createButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Directs user to next CreateBookClub activity
             * @param view - current view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateBookClub.class);
                startActivity(intent);
            }
        });

        Button button = findViewById(R.id.signOutBtn);
        button.setOnClickListener(new View.OnClickListener() {
            /**
             * Sign out user when signOut button is clicked
             * @param view - current view
             */
            @Override
            public void onClick(final View view) {
                AuthUI.getInstance()
                        .signOut(view.getContext())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(view.getContext(), "User signed out", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Toast.makeText(this, "User: " + user.getEmail(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
