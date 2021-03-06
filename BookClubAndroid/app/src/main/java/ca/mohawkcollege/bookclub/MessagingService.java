package ca.mohawkcollege.bookclub;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ca.mohawkcollege.bookclub.objects.User;

/**
 * Messaging service activity
 */
public class MessagingService extends FirebaseMessagingService {
    private final static AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * Creates a notification when a new invite is recieved
     * @param remoteMessage - message
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> extraData = remoteMessage.getData();
        String title = extraData.get("title");
        String body = extraData.get("body");

        if (extraData.get("notificationType").equals("invite")) {
            showBookClubInvite(title, body, extraData.get("bookClubId"));
        } else {
            String meetingUID = extraData.get("meetingUID");
            String date = extraData.get("date");
            String startTime = extraData.get("startTime");
            String endTime = extraData.get("endTime");
            String location = extraData.get("location");
            String bookTitle = extraData.get("bookTitle");
            String bookAuthor = extraData.get("bookAuthor");
            showMeetingInvite(title, body, meetingUID, date, startTime, endTime, location, bookTitle, bookAuthor);
        }
    }

    /**
     * Create a book club notification
     * @param title - title of notification
     * @param message - message of notification
     * @param bookClubId - id of book club in the invite
     */
    public void showBookClubInvite(String title, String message, String bookClubId) {
        int id = atomicInteger.incrementAndGet();

        Intent acceptIntent = new Intent(this, MainActivity.class);
        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        acceptIntent.putExtra("type", "bookclub");
        acceptIntent.putExtra("accept", true);
        acceptIntent.putExtra("recordId", bookClubId);
        acceptIntent.putExtra("notiId", id);
        PendingIntent acceptPendingIntent =
                PendingIntent.getActivity(this, 1, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent declineIntent = new Intent(this, MainActivity.class);
        declineIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        declineIntent.putExtra("type", "bookclub");
        declineIntent.putExtra("accept", false);
        declineIntent.putExtra("recordId", bookClubId);
        declineIntent.putExtra("notiId", id);
        PendingIntent declinePendingIntent =
                PendingIntent.getActivity(this, 2, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "BookClub")
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentText(message)
                .setAutoCancel(true)
                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Accept", acceptPendingIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Decline", declinePendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("BookClub", "Book Clubs", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(id, builder.build());
    }

    /**
     * Create a meeting notification
     * @param title - title of meeting notification
     * @param message - message of meeting notification
     * @param meetingUID - id of meeting
     * @param date - meeting date
     * @param startTime - meeting start time
     * @param endTime - meeting end time
     * @param location - meeting location
     * @param bookTitle - title of book being reviewed in meeting
     * @param bookAuthor - author of book being reviewed in meeting
     */
    public void showMeetingInvite(String title, String message, String meetingUID, String date, String startTime, String endTime, String location, String bookTitle, String bookAuthor) {
        int id = atomicInteger.incrementAndGet();

        Intent acceptIntent = new Intent(this, MainActivity.class);
        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        acceptIntent.putExtra("type", "meeting");
        acceptIntent.putExtra("accept", true);
        acceptIntent.putExtra("recordId", meetingUID);
        acceptIntent.putExtra("date", date);
        acceptIntent.putExtra("startTime", startTime);
        acceptIntent.putExtra("endTime", endTime);
        acceptIntent.putExtra("notiId", id);
        acceptIntent.putExtra("location", location);
        acceptIntent.putExtra("bookTitle", bookTitle);
        acceptIntent.putExtra("bookAuthor", bookAuthor);
        PendingIntent acceptPendingIntent =
                PendingIntent.getActivity(this, 2, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent declineIntent = new Intent(this, MainActivity.class);
        declineIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        declineIntent.putExtra("type", "meeting");
        declineIntent.putExtra("accept", false);
        declineIntent.putExtra("recordId", meetingUID);
        declineIntent.putExtra("date", date);
        declineIntent.putExtra("startTime", startTime);
        declineIntent.putExtra("endTime", endTime);
        declineIntent.putExtra("notiId", id);
        declineIntent.putExtra("location", location);
        declineIntent.putExtra("bookTitle", bookTitle);
        declineIntent.putExtra("bookAuthor", bookAuthor);
        PendingIntent declinePendingIntent =
                PendingIntent.getActivity(this, 4, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "BookClub")
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText(message)
                .setAutoCancel(true)
                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Accept", acceptPendingIntent)
                .addAction(R.drawable.common_google_signin_btn_icon_dark, "Decline", declinePendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("BookClub", "Book Clubs", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        manager.notify(id, builder.build());
    }

    /**
     * Set new user's data to previous user's data before their token was changed
     * @param s - new user token
     */
    @Override
    public void onNewToken(@NonNull final String s) {
        super.onNewToken(s);
        try {
            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference users = FirebaseDatabase.getInstance().getReference("Users");
            Query query = users.orderByChild("userId").equalTo(firebaseUser.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user == null)
                            continue;

                        user.token = s;
                        user.phoneNumber = firebaseUser.getPhoneNumber();
                        user.email = firebaseUser.getEmail();
                        users.child(user.userId).setValue(user);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        } catch (NullPointerException e) {
        }
    }
}
