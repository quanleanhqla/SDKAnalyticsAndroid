package com.mobio.analytics.client.utility;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.InputStream;

public class DownloadImageNotificationTask extends AsyncTask<String, Void, Bitmap> {
    private Context context;
    private NotificationCompat.Builder builder;
    private int reqId;

    public DownloadImageNotificationTask(Context context, NotificationCompat.Builder builder, int reqId) {
        this.context = context;
        this.builder = builder;
        this.reqId = reqId;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            LogMobio.logD("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        builder.setLargeIcon(result);
        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(reqId, notification);
    }
}
