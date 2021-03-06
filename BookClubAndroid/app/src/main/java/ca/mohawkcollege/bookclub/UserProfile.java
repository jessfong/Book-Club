package ca.mohawkcollege.bookclub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import ca.mohawkcollege.bookclub.helpers.OnUploadImage;
import ca.mohawkcollege.bookclub.objects.User;

/**
 * User profile activity
 * Allow user to upload a user profile name and picture
 */
public class UserProfile extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri imageFile;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private DatabaseReference users;
    private User userData;

    /**
     * Overrides method to create user profile layout
     * @param savedInstanceState - saved data from last login
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_user_profile);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final EditText displayName = findViewById(R.id.userProfileName);
        final ImageView imageView = findViewById(R.id.userProfilePic);

        // Get saved user profile data from user database table
        users = FirebaseDatabase.getInstance().getReference("Users");
        Query query = users.orderByChild("userId").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user == null)
                        continue;

                    if (user.name != null && !TextUtils.isEmpty(user.name)) {
                        displayName.setText(user.name);
                    }

                    if (user.imageUrl != null && !TextUtils.isEmpty(user.imageUrl)) {
                        imageFile = Uri.parse(user.imageUrl);
                        Glide.with(getApplicationContext())
                                .load(imageFile)
                                .into(imageView);
                    }

                    userData = user;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        // Starts chooseImage method when upload image button is clicked
        Button imageButton = findViewById(R.id.userProfilePicUploadImageBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        // Saves image and user name when user clicks save button
        Button saveChanges = findViewById(R.id.saveUserProfile);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = displayName.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(UserProfile.this, "You must fill in a display name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                userData.name = name;
                users.child(userData.userId).setValue(userData);
                Toast.makeText(UserProfile.this, "Saved user profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Brings user to previous activity when back button is clicked
     * @param item - back button
     * @return view of previous activity
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts activity when user wants to choose a profile picture
     */
    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Override method for onActivityResult
     * @param requestCode - request code of activity
     * @param resultCode - result code of activity
     * @param data - data from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageFile = data.getData();
            try {
                // Display image when user still creating book club
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFile);

                ImageView imageView = findViewById(R.id.userProfilePic);
                imageView.setImageBitmap(bitmap);

                uploadImage(new OnUploadImage() {
                    @Override
                    public void onComplete(Uri result) {
                        userData.imageUrl = result.toString();
                        users.child(userData.userId).setValue(userData);
                        Toast.makeText(UserProfile.this, "Uploaded profile picture!", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Uploads the chosen image to firebase storage
     * @param onUploadImage - onUploadImage object
     */
    private void uploadImage(final OnUploadImage onUploadImage) {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(imageFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    /**
                     * Called when chosen image is successfully uploaded.
                     * Gets image url from database
                     * @param taskSnapshot - snapshot of storage
                     */
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            /**
                             * Gets image url from database when done uploading
                             * @param task
                             */
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.getResult() != null) {
                                    onUploadImage.onComplete(task.getResult());
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    /**
                     * Displays error message when image failed to upload
                     * @param e - error that happened
                     */
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
