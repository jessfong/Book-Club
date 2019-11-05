package ca.mohawkcollege.bookclub.helpers;

import android.os.AsyncTask;
import com.google.gson.Gson;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ca.mohawkcollege.bookclub.objects.bookobjects.Book;

public class DownloadAsyncTask extends AsyncTask<String, Void, String> {

    private OnDownloadAsyncTask onDownloadAsyncTask;

    public DownloadAsyncTask(OnDownloadAsyncTask onDownloadAsyncTask) {
        this.onDownloadAsyncTask = onDownloadAsyncTask;
    }

    @Override
    protected String doInBackground(String... params) {
        StringBuilder results = new StringBuilder();

        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    results.append(line);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return results.toString();
    }

    protected void onPostExecute(String result) {
        Gson gson = new Gson();
        Book book = gson.fromJson(result, Book.class);

        onDownloadAsyncTask.onComplete(book);
    }

    public interface OnDownloadAsyncTask{
        void onComplete(Book book);
    }
}