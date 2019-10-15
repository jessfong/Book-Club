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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
        showNotification(title, body, extraData.get("bookClubId"));
    }

    public void showNotification(String title, String message, String bookClubId) {
        Intent acceptIntent = new Intent(this, MainActivity.class);
        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        acceptIntent.putExtra("accept", true);
        acceptIntent.putExtra("recordId", bookClubId);
        PendingIntent acceptPendingIntent =
                PendingIntent.getActivity(this, 0, acceptIntent, 0);

        Intent declineIntent = new Intent(this, MainActivity.class);
        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        acceptIntent.putExtra("accept", false);
        acceptIntent.putExtra("recordId", bookClubId);
        PendingIntent declinePendingIntent =
                PendingIntent.getActivity(this, 0, declineIntent, 0);

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
        manager.notify(566, builder.build());
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("User");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        User user = new User(firebaseUser.getUid(), firebaseUser.getPhoneNumber(), firebaseUser.getEmail(), s);
        mDatabase.child(user.userId).setValue(user);
    }
}
