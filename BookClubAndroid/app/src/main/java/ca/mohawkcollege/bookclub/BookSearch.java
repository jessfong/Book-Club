package ca.mohawkcollege.bookclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Book search activity
 */
public class BookSearch extends AppCompatActivity {

    /**
     * Overrides method to create book search layout
     * @param savedInstanceState - bundle data from last activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Looks for all books with given search requirements
        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.searchEditText);

                if (editText.getText() == null || TextUtils.isEmpty(editText.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "You must enter search text!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String searchValue = editText.getText().toString();
                CheckBox authorCheck = findViewById(R.id.authorCheckBox);
                CheckBox titleCheck = findViewById(R.id.titleCheckBox);

                if (!authorCheck.isChecked() && !titleCheck.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Auth or title must be checked!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = "https://www.googleapis.com/books/v1/volumes?q=";
                if (authorCheck.isChecked() && titleCheck.isChecked()) {
                    url = url + "intitle:" + searchValue + "+inauthor:" + searchValue;
                } else if (authorCheck.isChecked()) {
                    url = url + "inauthor:" + searchValue;
                } else {
                    url = url + "intitle:" + searchValue;
                }

                Intent intent = new Intent(getApplicationContext(), BookList.class);
                intent.putExtra("search", url);
                startActivityForResult(intent, 103);
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
     * Override method for onActivityResult
     * @param requestCode - request code of activity
     * @param resultCode - result code of activity
     * @param data - data from activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 103) {
            setResult(Activity.RESULT_OK, data);
            finish();
        }
    }
}
