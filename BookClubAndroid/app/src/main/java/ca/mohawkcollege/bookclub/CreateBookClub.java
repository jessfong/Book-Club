package ca.mohawkcollege.bookclub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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

public class CreateBookClub extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private final int PICK_IMAGE_REQUEST = 71;
    private Context context;
    private Uri imageFile;

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
                    writeNewClub(user.getUid(), name.getText().toString());
                    Toast.makeText(context, "Created!", Toast.LENGTH_SHORT).show();
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

    private void uploadImage(final OnUploadImage onUploadImage) {
        StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
        ref.putFile(imageFile)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) { // Image uploaded here complete
                        taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() { // To get image url from database
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) { // Got image url from database done
                                if (task.getResult() != null) {

                                    // Use to download images from fire base storage when listing book clubs on home page
                                    /*Glide.with(context)
                                            .load(task.getResult())
                                            .into(imageView);*/

                                    onUploadImage.onComplete(task.getResult());
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public interface OnUploadImage {
        void onComplete(Uri result);
    }

    // Add new book club to database
    public void writeNewClub(final String user, final String bookClubName){
        uploadImage(new OnUploadImage() {
            @Override
            public void onComplete(Uri result) {
                BookClub bookClub = new BookClub(user, bookClubName, result.toString());

                String key = mDatabase.push().getKey();
                if (key != null) {
                    mDatabase.child(key).setValue(bookClub);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Book club name cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
