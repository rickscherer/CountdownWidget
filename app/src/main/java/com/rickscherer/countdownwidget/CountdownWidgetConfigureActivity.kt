package com.rickscherer.countdownwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import java.util.Calendar

private const val PREFS_NAME = "com.example.countdownwidget.CountdownWidget"
private const val PREF_PREFIX_KEY = "target_date_"
private const val PREF_TEXT_KEY = "custom_text_"
private const val PREF_COLOR_KEY = "widget_color_"

internal fun saveTargetDate(context: Context, appWidgetId: Int, time: Long) {
    context.getSharedPreferences(PREFS_NAME, 0).edit().putLong(PREF_PREFIX_KEY + appWidgetId, time).apply()
}

internal fun loadTargetDate(context: Context, appWidgetId: Int): Long {
    return context.getSharedPreferences(PREFS_NAME, 0).getLong(PREF_PREFIX_KEY + appWidgetId, -1L)
}

internal fun deleteTargetDate(context: Context, appWidgetId: Int) {
    context.getSharedPreferences(PREFS_NAME, 0).edit().remove(PREF_PREFIX_KEY + appWidgetId).apply()
}

internal fun saveCustomText(context: Context, appWidgetId: Int, text: String) {
    context.getSharedPreferences(PREFS_NAME, 0).edit().putString(PREF_TEXT_KEY + appWidgetId, text).apply()
}

internal fun loadCustomText(context: Context, appWidgetId: Int): String {
    return context.getSharedPreferences(PREFS_NAME, 0).getString(PREF_TEXT_KEY + appWidgetId, "Label") ?: "Label"
}

internal fun saveWidgetColor(context: Context, appWidgetId: Int, color: Int) {
    context.getSharedPreferences(PREFS_NAME, 0).edit().putInt(PREF_COLOR_KEY + appWidgetId, color).apply()
}

internal fun loadWidgetColor(context: Context, appWidgetId: Int): Int {
    return context.getSharedPreferences(PREFS_NAME, 0).getInt(PREF_COLOR_KEY + appWidgetId, Color.parseColor("#42A5F5"))
}

class CountdownWidgetConfigureActivity : AppCompatActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private var chosenColor: Int = Color.parseColor("#42A5F5")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_countdown_widget_configure)
        setResult(Activity.RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        chosenColor = loadWidgetColor(this, appWidgetId)

        val datePicker = findViewById<DatePicker>(R.id.date_picker)
        val customTextInput = findViewById<EditText>(R.id.et_custom_text)
        customTextInput.setText(loadCustomText(this, appWidgetId))

        findViewById<Button>(R.id.btn_pick_color).setOnClickListener {
            ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(chosenColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("ok") { _, selectedColor, _ ->
                    chosenColor = selectedColor
                }
                .setNegativeButton("cancel") { _, _ -> }
                .build()
                .show()
        }

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            val context: Context = this@CountdownWidgetConfigureActivity
            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)

            saveTargetDate(context, appWidgetId, calendar.timeInMillis)
            saveCustomText(context, appWidgetId, customTextInput.text.toString())
            saveWidgetColor(context, appWidgetId, chosenColor)

            val appWidgetManager = AppWidgetManager.getInstance(context)
            updateAppWidget(context, appWidgetManager, appWidgetId)

            val resultValue = Intent().apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            setResult(Activity.RESULT_OK, resultValue)
            finish()
        }
    }
}