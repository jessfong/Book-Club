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
import java.nio.charset.StandardCharsets;

import ca.mohawkcollege.bookclub.objects.bookobjects.Book;

/**
 * Download async task
 */
public class DownloadAsyncTask extends AsyncTask<String, Void, String> {

    private OnDownloadAsyncTask onDownloadAsyncTask;

    /**
     * Constructor to set downloading task to this task
     * @param onDownloadAsyncTask - task to download
     */
    public DownloadAsyncTask(OnDownloadAsyncTask onDownloadAsyncTask) {
        this.onDownloadAsyncTask = onDownloadAsyncTask;
    }

    /**
     * Create a new http connection to download the task in the background
     * @param params - parameters for the url connection
     * @return results of the task after it is finished downloading
     */
    @Override
    protected String doInBackground(String... params) {
        StringBuilder results = new StringBuilder();

        try {
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int statusCode = conn.getResponseCode();
            if (statusCode == 200) {
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
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

    /**
     * Gets result of the task after it has finished
     * @param result - result of the download task
     */
    protected void onPostExecute(String result) {
        Gson gson = new Gson();
        Book book = gson.fromJson(result, Book.class);

        onDownloadAsyncTask.onComplete(book);
    }

    /**
     * Interface for the task being downloaded
     */
    public interface OnDownloadAsyncTask {
        void onComplete(Book book);
    }
}