package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import ca.mohawkcollege.bookclub.objects.BookClub;

public class CreateBookClub extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    private Context context;
    private Uri imageFile;

    /**
     * Gathers data for new book club and writes it to BookClubs table in firebase
     * @param savedInstanceState - saved data from last login
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book_club);
        context = this;

        // Set instance of database and user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference("BookClubs");

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        final TextView name = findViewById(R.id.createBookClubNameEditText);

        // When user clicks on button to upload book club image
        Button imageButton = findViewById(R.id.createBookClubUploadImageBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        if(user != null){
            // When create book club button is clicked
            Button button = findViewById(R.id.createBookClubBtn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (name.getText() == null || TextUtils.isEmpty(name.getText())) {
                        Toast.makeText(getApplicationContext(), "Book club name cannot be empty.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (imageFile == null) {
                        Toast.makeText(getApplicationContext(), "You must select an image!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    writeNewClub(user.getUid(), name.getText().toString());

                    Intent acceptIntent = new Intent(view.getContext(), RetrieveClubInfo.class);
                    acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    view.getContext().startActivity(acceptIntent);
                }
            });
        }
    }

    // Allow user to choose book club image from gallery
    public void chooseImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Display gallery image in createBookClub view
     * @param requestCode - code determining where the request came from
     * @param resultCode - code determining what the result was
     * @param data - data returned from the activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            imageFile = data.getData();
            try {
                // Display image when user still creating book club
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFile);

                ImageView imageView = findViewById(R.id.createBookClubUploadedImageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Writing chosen image uri to firebase BookClubs table
    private void uploadImage(final OnUploadImage onUploadImage) {
        StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
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
                        Toast.makeText(context, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Returns image Uri
    public interface OnUploadImage {
        void onComplete(Uri result);
    }

    // Add new book club to database
    public void writeNewClub(final String user, final String bookClubName){
        uploadImage(new OnUploadImage() {
            /**
             * Writes book club data to database once image uri is retrieved
             * @param result - result of uploaded record
             */
            @Override
            public void onComplete(Uri result) {
                String key = mDatabase.push().getKey();

                if (key != null) {
                    BookClub bookClub = new BookClub(user, bookClubName, result.toString(), key);
                    mDatabase.child(key).setValue(bookClub);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Book club name cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
