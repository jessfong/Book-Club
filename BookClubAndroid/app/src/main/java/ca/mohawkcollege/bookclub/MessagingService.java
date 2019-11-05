package ca.mohawkcollege.bookclub;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

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

import ca.mohawkcollege.bookclub.objects.User;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        Map<String, String> extraData = remoteMessage.getData();

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

    public void showBookClubInvite(String title, String message, String bookClubId) {
        int id = 566;

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
                .setSmallIcon(R.drawable.ic_launcher_background)
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

    public void showMeetingInvite(String title, String message, String meetingUID, String date, String startTime, String endTime, String location, String bookTitle, String bookAuthor) {
        int id = 567;

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
                .setSmallIcon(R.drawable.ic_launcher_background)
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
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        } catch (NullPointerException e) {

        }
    }
}
