package ca.mohawkcollege.bookclub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class BookSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = findViewById(R.id.searchEditText);

                if(editText.getText() == null || TextUtils.isEmpty(editText.getText().toString())){
                    Toast.makeText(getApplicationContext(), "You must enter search text!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String searchValue = editText.getText().toString();
                CheckBox authorCheck = findViewById(R.id.authorCheckBox);
                CheckBox titleCheck = findViewById(R.id.titleCheckBox);

                if(!authorCheck.isChecked() && !titleCheck.isChecked()){
                    Toast.makeText(getApplicationContext(), "Auth or title must be checked!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url = "https://www.googleapis.com/books/v1/volumes?q=";
                if (authorCheck.isChecked() && titleCheck.isChecked()) {
                    url = url + "intitle:" + searchValue + "+inauthor:" + searchValue;
                } else if (authorCheck.isChecked()){
                    url = url + "inauthor:" + searchValue;
                } else {
                    url = url + "intitle:" + searchValue;
                }

                Intent intent = new Intent(getApplicationContext(), BookList.class);
                intent.putExtra("search", url);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
