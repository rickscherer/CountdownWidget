package com.rickscherer.countdownwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.widget.RemoteViews
import androidx.core.graphics.drawable.toBitmap
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Calendar
import kotlin.math.abs

class CountdownWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            deleteTargetDate(context, appWidgetId)
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.widget_layout)

    // Handle the date countdown
    val targetMillis = loadTargetDate(context, appWidgetId)
    if (targetMillis != -1L) {
        val today = LocalDate.now()
        val targetDate = LocalDate.ofInstant(
            java.time.Instant.ofEpochMilli(targetMillis),
            ZoneId.systemDefault()
        )
        val daysBetween = ChronoUnit.DAYS.between(today, targetDate)
        val dayCount = abs(daysBetween)
        val label = when {
            daysBetween > 0 -> "days left"
            daysBetween < 0 -> "days ago"
            else -> "is Today!"
        }
        views.setTextViewText(R.id.tv_day_count, dayCount.toString())
        views.setTextViewText(R.id.tv_label, label)
    } else {
        views.setTextViewText(R.id.tv_day_count, "???")
        views.setTextViewText(R.id.tv_label, "Set Date")
    }

    // Load and display the custom text
    val customText = loadCustomText(context, appWidgetId)
    views.setTextViewText(R.id.tv_custom_text, customText)

    // DYNAMIC COLOR LOGIC
    val widgetColor = loadWidgetColor(context, appWidgetId)
    val hsv = FloatArray(3)
    Color.colorToHSV(widgetColor, hsv)
    hsv[2] *= 0.8f // make it 20% darker
    val strokeColor = Color.HSVToColor(hsv)
    val circleDrawable = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(widgetColor)
        setStroke(4, strokeColor)
    }
    views.setImageViewBitmap(R.id.iv_circle_background, circleDrawable.toBitmap(200, 200))

    // Set up the click to re-open configuration
    val configIntent = Intent(context, CountdownWidgetConfigureActivity::class.java).apply {
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
    }
    val configPendingIntent = PendingIntent.getActivity(
        context,
        appWidgetId,
        configIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    views.setOnClickPendingIntent(R.id.widget_root, configPendingIntent)

    // Update the widget and schedule the next refresh
    appWidgetManager.updateAppWidget(appWidgetId, views)
    scheduleNextUpdate(context, appWidgetId)
}

private fun scheduleNextUpdate(context: Context, appWidgetId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val nextMidnight = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 1)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, 1)
    }
    val intent = Intent(context, CountdownWidgetProvider::class.java).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        appWidgetId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    alarmManager.set(AlarmManager.RTC, nextMidnight.timeInMillis, pendingIntent)
}