package com.mobio.analytics.client.inapp.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.text.HtmlCompat;

import com.mobio.analytics.R;
import com.mobio.analytics.client.model.digienty.Properties;
import com.mobio.analytics.client.model.digienty.Push;
import com.mobio.analytics.client.model.factory.PendingIntentFactory;
import com.mobio.analytics.client.model.old.ScreenConfigObject;
import com.mobio.analytics.client.service.ClickNotificationService;
import com.mobio.analytics.client.utility.DownloadImageNotificationTask;
import com.mobio.analytics.client.utility.DownloadManager;
import java.util.HashMap;
import java.util.List;

public class RichNotification {
    private static final String MY_CHANNEL_ID = "CHANNEL_SDK_MOBIO";
    private static final String MY_CHANNEL_NAME = "Sdk Mobio";
    private static final String TYPE_TEXT = "TEXT";
    private static final String TYPE_BIG_PICTURE = "BIG_PICTURE";
    private static final String TYPE_CAROUSEL = "CAROUSEL";
    private static final String TYPE_AUTO_CAROUSEL = "AUTO_CAROUSEL";
    private static final String TYPE_RATING = "RATING";
    private static final String TYPE_RAW_HTML = "RAW_HTML";
    private static final String TYPE_COUNTDOWN_TIMER = "COUNTDOWN_TIMER";
    private static final String TYPE_ZERO_BEZEL = "ZERO_BEZEL";
    private static final String TYPE_INPUT = "INPUT";

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context, String channelId, String channelName, int importance) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
            Log.d("RichNotification", "channel created");
        } else {
            Log.d("RichNotification", "channel already exists");
        }
    }

    public static void showRichNotification(Context context, Push push, int id, HashMap<String, ScreenConfigObject> configActivityMap){
        new RichNotification().show(context, push, id, configActivityMap);
    }

    private Class<?> findDes(Push push, HashMap<String, ScreenConfigObject> configActivityMap){
        Class<?> classDes = null;
        Class<?> classInitial = null;
        for (int i = 0; i < configActivityMap.values().size(); i++) {
            ScreenConfigObject screenConfigObject = (ScreenConfigObject) configActivityMap.values().toArray()[i];
            if (screenConfigObject.getTitle().equals(push.getAlert().getDesScreen())) {
                classDes = screenConfigObject.getClassName();
                break;
            }
            if (screenConfigObject.isInitialScreen()) {
                classInitial = screenConfigObject.getClassName();
            }
        }
        if (classDes == null) {
            classDes = classInitial;
        }
        return classDes;
    }

    private void show(Context context, Push push, int reqId, HashMap<String, ScreenConfigObject> configActivityMap){

        if(push == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, MY_CHANNEL_ID, MY_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        }

        PendingIntent deletePendingIntent = PendingIntentFactory.getPushDeletePendingIntent(context, push, reqId);
        PendingIntent contentPendingIntent = PendingIntentFactory.getPushClickPendingIntent(context, push, reqId, findDes(push, configActivityMap));

        Properties richNotification = push.getAlert().getValueMap("rich_notification", Properties.class);
        String style;

        if(richNotification == null) {
            style = "";
        }
        else {
            style = richNotification.getString("style");
        }

        if(style == null) style = "";

        switch (style) {
            default: {
                String urlImage = push.getAlert().getString("background_image");

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(push.getAlert().getTitle())
                        .setContentText(push.getAlert().getBody())
                        .setContentIntent(contentPendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .setGroup("SDKMobio")
                        .setAutoCancel(true);

                    new DownloadImageNotificationTask(context, builder, reqId).execute(urlImage);
                break;
            }
            case TYPE_BIG_PICTURE: {
                RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.push_collapsed);
                collapsedView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                collapsedView.setTextViewText(R.id.notificationText, push.getAlert().getBody());

                String urlImage = push.getAlert().getString("background_image");

                Bitmap bigPicture = DownloadManager.getBitmapFromURL(urlImage, false);

                RemoteViews bigPictureView = new RemoteViews(context.getPackageName(), R.layout.push_big_picture);
                bigPictureView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                bigPictureView.setTextViewText(R.id.notificationText, push.getAlert().getBody());
                bigPictureView.setInt(R.id.notificationText, "setMaxLines", 4);

                if (bigPicture != null) {
                    bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.VISIBLE);
                    bigPictureView.setImageViewBitmap(R.id.big_picture_imageview, getResizedBitmap(bigPicture, 500));
                } else {
                    bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.GONE);
                }

                createNotification(context, collapsedView, bigPictureView, contentPendingIntent, deletePendingIntent, reqId);
                break;
            }

            case TYPE_INPUT: {
                RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.push_collapsed);
                collapsedView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                collapsedView.setTextViewText(R.id.notificationText, push.getAlert().getBody());

                PendingIntent actionYesPendingIntent = PendingIntentFactory.getPushActionPendingIntent(context, push, reqId, ClickNotificationService.TYPE_INPUT_YES);
                PendingIntent actionNoPendingIntent = PendingIntentFactory.getPushActionPendingIntent(context, push, reqId, ClickNotificationService.TYPE_INPUT_NO);
                PendingIntent actionMaybePendingIntent = PendingIntentFactory.getPushActionPendingIntent(context, push, reqId, ClickNotificationService.TYPE_INPUT_MAYBE);

                String urlImage = push.getAlert().getString("background_image");

                Bitmap bigPicture = DownloadManager.getBitmapFromURL(urlImage, false);

                RemoteViews bigPictureView = new RemoteViews(context.getPackageName(), R.layout.push_input);
                bigPictureView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                bigPictureView.setTextViewText(R.id.notificationText, push.getAlert().getBody());
                bigPictureView.setInt(R.id.notificationText, "setMaxLines", 3);

                bigPictureView.setTextViewText(R.id.tv_action1, "Yes");
                bigPictureView.setOnClickPendingIntent(R.id.tv_action1, actionYesPendingIntent);
                bigPictureView.setTextViewText(R.id.tv_action2, "No");
                bigPictureView.setOnClickPendingIntent(R.id.tv_action2, actionNoPendingIntent);
                bigPictureView.setTextViewText(R.id.tv_action3, "Maybe");
                bigPictureView.setOnClickPendingIntent(R.id.tv_action3, actionMaybePendingIntent);

                if (bigPicture != null) {
                    bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.VISIBLE);
                    bigPictureView.setImageViewBitmap(R.id.big_picture_imageview, getResizedBitmap(bigPicture, 500));
                } else {
                    bigPictureView.setViewVisibility(R.id.big_picture_imageview, View.GONE);
                }

                Notification notification = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(collapsedView)
                        .setCustomBigContentView(bigPictureView)
                        .setContentIntent(contentPendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(reqId, notification);

                break;
            }
            case TYPE_CAROUSEL: {
                PendingIntent clickLeftPendingIntent = PendingIntentFactory.getPushClickLeftPendingIntent(context, push, reqId);
                PendingIntent clickRightPendingIntent = PendingIntentFactory.getPushClickRightPendingIntent(context, push, reqId);

                RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.push_collapsed);
                collapsedView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                collapsedView.setTextViewText(R.id.notificationText, push.getAlert().getBody());

                List<String> listImage = (List<String>) richNotification.get("image_url");

                String urlImage = listImage.get(0);

                Bitmap img = DownloadManager.getBitmapFromURL(urlImage, true);
                if (img == null) {
                    img = DownloadManager.getBitmapFromURL(urlImage, false);
                    if (img == null) {
                        // Use a placeholder image
                        img = BitmapFactory.decodeResource(context.getResources(), R.mipmap.voucher);
                    }
                }

                RemoteViews carouselView = new RemoteViews(context.getPackageName(), R.layout.push_carousel_landscape);
                carouselView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                carouselView.setTextViewText(R.id.notificationText, push.getAlert().getBody());
                carouselView.setImageViewBitmap(R.id.carousel_landscape_image, getResizedBitmap(img, 500));
                carouselView.setOnClickPendingIntent(R.id.carousel_landscape_image, contentPendingIntent);
                carouselView.setOnClickPendingIntent(R.id.left, clickLeftPendingIntent);
                carouselView.setOnClickPendingIntent(R.id.right, clickRightPendingIntent);

//                for (int i = 0; i < listImage.size(); i++) {
//                    LogMobio.logD("QuanLA", i+"");
//                    RemoteViews imageView = new RemoteViews(context.getPackageName(), R.layout.image_view_rounded);
//                    loadImageURLIntoRemoteView(R.id.flipper_img, listImage.get(i), imageView, context);
//                    carouselView.addView(R.id.carousel_landscape_image, imageView);
//                }

                createNotification(context, collapsedView, carouselView, contentPendingIntent, deletePendingIntent, reqId);
                break;
            }
            case TYPE_AUTO_CAROUSEL: {
                RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.push_collapsed);
                collapsedView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                collapsedView.setTextViewText(R.id.notificationText, push.getAlert().getBody());

                List<String> listImage = (List<String>) richNotification.get("image_url");

                RemoteViews carouselView = new RemoteViews(context.getPackageName(), R.layout.push_auto_carousel_landscape);
                carouselView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                carouselView.setTextViewText(R.id.notificationText, push.getAlert().getBody());

                for (int i = 0 ; i < listImage.size(); i++)
                {
                    RemoteViews viewFlipperImage = new RemoteViews(context.getPackageName(), R.layout.image_view_rounded);
                    //for loop start
                    if (TextUtils.isEmpty(listImage.get(i)))
                    {
                        // set default image
                        viewFlipperImage.setImageViewResource(R.id.imageView,R.mipmap.voucher);
                    }
                    else
                    {
                        Bitmap remotePicture = DownloadManager.getBitmapFromURL(listImage.get(i), true);
                        if (remotePicture == null) {
                            remotePicture = DownloadManager.getBitmapFromURL(listImage.get(i), false);
                            if (remotePicture == null) {
                                // Use a placeholder image
                                remotePicture = BitmapFactory.decodeResource(context.getResources(), R.mipmap.voucher);
                            }
                        }

                        viewFlipperImage.setImageViewBitmap(R.id.imageView, getResizedBitmap(remotePicture, 500));

                    }
                    carouselView.addView(R.id.carousel_landscape_image, viewFlipperImage);
                }

                createNotification(context, collapsedView, carouselView, contentPendingIntent, deletePendingIntent, reqId);

                break;
            }
            case TYPE_RATING: {
                RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.push_collapsed);
                collapsedView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                collapsedView.setTextViewText(R.id.notificationText, push.getAlert().getBody());

                RemoteViews npsView = new RemoteViews(context.getPackageName(), R.layout.push_rating);
                npsView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                npsView.setTextViewText(R.id.notificationText, push.getAlert().getBody());

                String urlImage = push.getAlert().getString("background_image");


                if (urlImage != null) {
                    Bitmap img = DownloadManager.getBitmapFromURL(urlImage, false);
                    npsView.setViewVisibility(R.id.rate_frame, View.VISIBLE);
                    if (img != null) {
                        npsView.setViewVisibility(R.id.rate_image, View.VISIBLE);
                        npsView.setImageViewBitmap(R.id.rate_image, getResizedBitmap(img, 500));
                    } else {
                        npsView.setInt(R.id.rate_frame, "setBackgroundColor", Color.RED);
                    }
                }

                if (push.getAlert().getTitle() != null) {
                    npsView.setViewVisibility(R.id.rate_frame, View.VISIBLE);
                }

                if (push.getAlert().getBody() != null) {
                    npsView.setViewVisibility(R.id.rate_frame, View.VISIBLE);
                }

                int ratePosition = push.getAlert().getInt("position_rate", 0);

                for (int i = 1; i <= 5; i++) {
                    final PendingIntent rateClickPendingIntent = PendingIntentFactory.getPushClickRatePendingIntent(context, push, reqId, i);

                    int id = context.getResources().getIdentifier("rate_" + i, "id", context.getPackageName());
                    npsView.setOnClickPendingIntent(id, rateClickPendingIntent);

                    if (i <= ratePosition) {
                        npsView.setImageViewResource(id, R.mipmap.star_selected);
                    } else {
                        npsView.setImageViewResource(id, R.mipmap.star_unselected);
                    }
                }

                createNotification(context, collapsedView, npsView, contentPendingIntent, deletePendingIntent, reqId);
                break;
            }

            case TYPE_RAW_HTML: {
                Spanned styledBigTitle;
                Spanned styledBigText;
                if(push.getAlert().getBodyHTML() != null) {
                    styledBigTitle = HtmlCompat.fromHtml(push.getAlert().getTitle(), HtmlCompat.FROM_HTML_MODE_COMPACT);
                    styledBigText = HtmlCompat.fromHtml(push.getAlert().getBodyHTML(), HtmlCompat.FROM_HTML_MODE_COMPACT);
                }
                else {
                    styledBigTitle = HtmlCompat.fromHtml(push.getAlert().getTitle(), HtmlCompat.FROM_HTML_MODE_COMPACT);
                    styledBigText = HtmlCompat.fromHtml(push.getAlert().getBody(), HtmlCompat.FROM_HTML_MODE_COMPACT);
                }

                Notification notification = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setContentTitle(push.getAlert().getTitle())
                        .setContentText(push.getAlert().getBody())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .setBigContentTitle(styledBigTitle)
                                .bigText(styledBigText))
                        .setContentIntent(contentPendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(reqId, notification);
                break;
            }
            case TYPE_COUNTDOWN_TIMER:
                long timeCount = richNotification.getInt("timer", 0) * 1000L;
                RemoteViews collapsedTimerView = new RemoteViews(context.getPackageName(), R.layout.push_timer_colapsed);
                collapsedTimerView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                collapsedTimerView.setTextViewText(R.id.notificationText, push.getAlert().getBody());
                collapsedTimerView.setChronometer(R.id.notificationTimer, SystemClock.elapsedRealtime() + timeCount, null, true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    collapsedTimerView.setChronometerCountDown(R.id.notificationTimer, true);
                }

                RemoteViews npsView = new RemoteViews(context.getPackageName(), R.layout.push_timer);
                npsView.setTextViewText(R.id.notificationTitle, push.getAlert().getTitle());
                npsView.setTextViewText(R.id.notificationText, push.getAlert().getBody());
                npsView.setChronometer(R.id.notificationTimer, SystemClock.elapsedRealtime() + timeCount, null, true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    npsView.setChronometerCountDown(R.id.notificationTimer, true);
                }

                String urlImage = push.getAlert().getString("background_image");
                Bitmap bigPicture = DownloadManager.getBitmapFromURL(urlImage, false);
                if (bigPicture != null) {
                    npsView.setViewVisibility(R.id.big_picture_imageview, View.VISIBLE);
                    npsView.setImageViewBitmap(R.id.big_picture_imageview, getResizedBitmap(bigPicture, 500));
                } else {
                    npsView.setViewVisibility(R.id.big_picture_imageview, View.GONE);
                }

                Notification notification = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(collapsedTimerView)
                        .setCustomBigContentView(npsView)
                        .setTimeoutAfter(timeCount)
                        .setContentIntent(contentPendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManager.notify(reqId, notification);
                break;
            case TYPE_ZERO_BEZEL:
                String urlImageBezel = push.getAlert().getString("background_image");;

                Bitmap bigPictureBezel = DownloadManager.getBitmapFromURL(urlImageBezel, false);
                RemoteViews collapsedView = new RemoteViews(context.getPackageName(), R.layout.push_zero_bezel_collapse);
                collapsedView.setTextViewText(R.id.title, push.getAlert().getTitle());
                collapsedView.setTextViewText(R.id.msg, push.getAlert().getBody());


                RemoteViews bigPictureView = new RemoteViews(context.getPackageName(), R.layout.push_zero_bezel);
                bigPictureView.setTextViewText(R.id.title, push.getAlert().getTitle());
                bigPictureView.setTextViewText(R.id.msg, push.getAlert().getBody());
                bigPictureView.setInt(R.id.msg, "setMaxLines", 4);
                bigPictureView.setTextViewText(R.id.tv_sub, context.getApplicationInfo().loadLabel(context.getPackageManager()).toString());

                if (bigPictureBezel != null) {
                    collapsedView.setViewVisibility(R.id.big_image, View.VISIBLE);
                    collapsedView.setImageViewBitmap(R.id.big_image, getResizedBitmap(bigPictureBezel, 500));

                    bigPictureView.setViewVisibility(R.id.big_image, View.VISIBLE);
                    bigPictureView.setImageViewBitmap(R.id.big_image, getResizedBitmap(bigPictureBezel, 500));
                } else {
                    collapsedView.setViewVisibility(R.id.big_image, View.GONE);
                    bigPictureView.setViewVisibility(R.id.big_image, View.GONE);
                }

//                createNotification(context, collapsedView, bigPictureView, contentPendingIntent, deletePendingIntent, reqId);
                Notification notificationBezel = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setCustomContentView(collapsedView)
                        .setCustomBigContentView(bigPictureView)
                        .setContentIntent(contentPendingIntent)
                        .setDeleteIntent(deletePendingIntent)
                        .build();

                NotificationManagerCompat notificationManagerBezel = NotificationManagerCompat.from(context);
                notificationBezel.flags = Notification.FLAG_AUTO_CANCEL;
                notificationManagerBezel.notify(reqId, notificationBezel);
                break;
        }

    }

    private void createNotification(Context context, RemoteViews contentView, RemoteViews bigContentView, PendingIntent contentPendingIntent, PendingIntent deletePendingIntent, int reqId){
        Notification notification = new NotificationCompat.Builder(context, MY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(contentView)
                .setCustomBigContentView(bigContentView)
                .setContentIntent(contentPendingIntent)
                .setDeleteIntent(deletePendingIntent)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(reqId, notification);
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
