package ca.mohawkcollege.bookclub.helpers;

import android.net.Uri;

/**
 * Interface for images that have finished uploading
 */
public interface OnUploadImage {
    void onComplete(Uri result);
}
