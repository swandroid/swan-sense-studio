package interdroid.swan.actuator.impl;

import android.app.Notification;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

//import interdroid.swan.R;
import interdroid.swan.actuator.Actuator;
//import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * An {@link Actuator} that creates a notification when the event happens/
 */
public class NotificationActuator extends Actuator {

    public static final String ENTITY = "notification";

    private static final String PARAM_NOTIFICATION_ID = "notification_id";
    private static final String PARAM_AUTO_CANCEL = "auto_cancel";
    private static final String PARAM_CATEGORY = "category";
    private static final String PARAM_COLOR = "color";
    private static final String PARAM_CONTENT_TEXT = "content_text";
    private static final String PARAM_CONTENT_TITLE = "content_title";
    private static final String PARAM_EXTRAS = "extras";
    private static final String PARAM_LIGHTS = "lights";
    private static final String PARAM_ONGOING = "ongoing";
    private static final String PARAM_ONLY_ALERT_ONCE = "only_alert_once";
    private static final String PARAM_PRIORITY = "priority";
    private static final String PARAM_SHOW_WHEN = "show_when";
    private static final String PARAM_SOUND = "sound";
    private static final String PARAM_SUB_TEXT = "sub_text";
    private static final String PARAM_TICKER = "ticker";
    private static final String PARAM_VIBRATE = "vibrate";
    private static final String PARAM_WHEN = "when";

    private static final String[] KEYS = new String[]{
            PARAM_NOTIFICATION_ID,
            PARAM_AUTO_CANCEL,
            PARAM_CATEGORY,
            PARAM_COLOR,
            PARAM_CONTENT_TEXT,
            PARAM_CONTENT_TITLE,
            PARAM_EXTRAS,
            PARAM_LIGHTS,
            PARAM_ONGOING,
            PARAM_ONLY_ALERT_ONCE,
            PARAM_PRIORITY,
            PARAM_SHOW_WHEN,
            PARAM_SOUND,
            PARAM_SUB_TEXT,
            PARAM_TICKER,
            PARAM_VIBRATE,
            PARAM_WHEN
    };

    private static final String[] PATHS = new String[]{"notify"};

    private final Notification notification;

    private final int notificationId;

    private final NotificationManagerCompat notificationManager;

    /**
     * Create a {@link NotificationActuator} object.
     *
     * @param context        the context
     * @param notificationId the unique id for the notification
     * @param autoCancel     whether it should auto cancel or not
     * @param category       the category of the notification
     * @param color          the accent color of the notification
     * @param contentText    the content text of the notification
     * @param contentTitle   the content title of the notification
     * @param extras         extras bundled with the notification
     * @param lightStr       the notification light, format: 'rgb onMs offMs'
     * @param ongoing        whether the notification is ongoing or not
     * @param onlyAlertOnce  whether it should only alert once
     * @param priority       the priority of the notification
     * @param showWhen       whether to show the timestamp
     * @param sound          the Uri of the notification sound
     * @param subText        the sub text
     * @param ticker         the ticker text
     * @param vibrate        vibration pattern
     * @param when           timestamp to show in the notification
     */
    private NotificationActuator(Context context, int notificationId, @Nullable Boolean autoCancel, @Nullable String category, @Nullable Integer color,
                                 @Nullable String contentText, @Nullable String contentTitle, @Nullable Bundle extras,
                                 @Nullable String lightStr, @Nullable Boolean ongoing, @Nullable Boolean onlyAlertOnce,
                                 @Nullable Integer priority, @Nullable Boolean showWhen, @Nullable Uri sound, @Nullable String subText,
                                 @Nullable String ticker, @Nullable long[] vibrate, @Nullable Long when) {
        this.notificationManager = NotificationManagerCompat.from(context);
        this.notificationId = notificationId;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        if (autoCancel != null) {
            builder.setAutoCancel(autoCancel);
        }

        if (category != null) {
            builder.setCategory(category);
        }

        if (color != null) {
            builder.setColor(color);
        }

        if (contentText != null) {
            builder.setContentText(contentText);
        }

        if (contentTitle != null) {
            builder.setContentTitle(contentTitle);
        }

        if (extras != null) {
            builder.setExtras(extras);
        }

        if (lightStr != null) {
            String[] lights = lightStr.split(" ", 3);
            builder.setLights(Integer.parseInt(lights[0]), Integer.parseInt(lights[1]),
                    Integer.parseInt(lights[2]));
        }

        if (ongoing != null) {
            builder.setOngoing(ongoing);
        }

        if (onlyAlertOnce != null) {
            builder.setOnlyAlertOnce(onlyAlertOnce);
        }

        if (priority != null) {
            builder.setPriority(priority);
        }

        if (showWhen != null) {
            builder.setShowWhen(showWhen);
        }

        if (sound != null) {
            builder.setSound(sound);
        }

        if (subText != null) {
            builder.setSubText(subText);
        }

        if (ticker != null) {
            builder.setTicker(ticker);
        }

        if (vibrate != null) {
            builder.setVibrate(vibrate);
        }

        if (when != null) {
            builder.setWhen(when);
        }

        //builder.setSmallIcon(R.drawable.ic_stat_swan);

        this.notification = builder.build();
    }


    @Override
    public void performAction(Context context, String expressionId, TimestampedValue[] newValues) {
        notificationManager.notify(notificationId, notification);
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            Bundle config = expression.getConfiguration();

            int id = Integer.parseInt(config.getString(PARAM_NOTIFICATION_ID));

            Boolean autoCancel = parseBoolean(config.getString(PARAM_AUTO_CANCEL));

            String category = config.getString(PARAM_CATEGORY);

            Integer color = null;
            try {
                color = Integer.decode(config.getString(PARAM_COLOR));
            } catch (Exception ignored) {
            }

            String contentText = config.getString(PARAM_CONTENT_TEXT);
            String contentTitle = config.getString(PARAM_CONTENT_TITLE);

            String extraStr = config.getString(PARAM_EXTRAS);

            Bundle extras = null;
            if (extraStr != null) {
                extras = new Bundle();
                for (String s : extraStr.split(",")) {
                    String[] param = s.split(":", 2);

                    if (param.length == 2) {
                        extras.putString(param[0], param[1]);
                    }
                }
            }

            String lightStr = config.getString(PARAM_LIGHTS);

            Boolean ongoing = parseBoolean(config.getString(PARAM_ONGOING));

            Boolean onlyAlertOnce = parseBoolean(config.getString(PARAM_ONLY_ALERT_ONCE));

            Integer priority = null;
            try {
                priority = Integer.parseInt(config.getString(PARAM_PRIORITY));
            } catch (NumberFormatException ignored) {
            }

            Boolean showWhen = parseBoolean(config.getString(PARAM_SHOW_WHEN));

            String soundUri = config.getString(PARAM_SOUND);
            Uri sound = null;
            if (soundUri != null) {
                sound = Uri.parse(soundUri);
            }

            String subText = config.getString(PARAM_SUB_TEXT);

            String ticker = config.getString(PARAM_TICKER);

            long[] vibrate = null;
            String vibrateString = config.getString(PARAM_VIBRATE);
            if (vibrateString != null) {
                String[] pattern = vibrateString.split(" ");
                vibrate = new long[pattern.length];

                for (int i = 0; i < pattern.length; i++) {
                    vibrate[i] = Long.parseLong(pattern[i]);
                }
            }

            Long when = null;
            if (config.containsKey(PARAM_WHEN)) {
                when = Long.parseLong(config.getString(PARAM_WHEN));
            }

            return new NotificationActuator(context, id, autoCancel, category, color, contentText,
                    contentTitle, extras, lightStr, ongoing, onlyAlertOnce, priority, showWhen,
                    sound, subText, ticker, vibrate, when);
        }

        private Boolean parseBoolean(String b) {
            if (b == null || b.isEmpty()) {
                return null;
            }

            return Boolean.valueOf(b);
        }
    }

 /*   public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[KEYS.length];
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            return ENTITY;
        }
    } */
}
