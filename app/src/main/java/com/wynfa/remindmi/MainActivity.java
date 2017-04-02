package com.wynfa.remindmi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.widget.TextView;

import com.wynfa.remindmi.db.ReminderContract;
import com.wynfa.remindmi.db.ReminderDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ReminderDbHelper mHelper;
    private ListView mReminderListView;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* This sets the view of the activity as activity_amin.xml
        *  activity_main.xml controls the UI and Android Interface */
        setContentView(R.layout.activity_main);

        /* This creates our database manager */
        mHelper = new ReminderDbHelper(this);

        /* This creates our list of reminders */
        mReminderListView = (ListView) findViewById(R.id.list_reminder);

        /* This updates our UI */
        updateUI();
    }

    /* This "inflates" or renders the menu in the main activity and makes use of
    *  onOptionsItemSelected() method for the user's input with the other items */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* This is what defines how the application reacts to user input by selection
    *  of any of the menu items found  within the main menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_add_reminder:
                Intent intent = new Intent(MainActivity.this, add_reminder.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /* This updates the UI every time there is an action done */
    private void updateUI() {
        ArrayList<String> reminderList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(ReminderContract.ReminderEntry.TABLE,
            new String[]{ReminderContract.ReminderEntry._ID, ReminderContract.ReminderEntry.COL_REMINDER_TITLE, ReminderContract.ReminderEntry.COL_REMINDER_TIME},
            null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(ReminderContract.ReminderEntry.COL_REMINDER_TITLE);
            reminderList.add(cursor.getString(idx));
            Log.d(TAG, "Reminder: " + cursor.getString(idx));
        }

        if(mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_reminder,         /* which view to use for reminders */
                    R.id.reminder_title,            /* where to place the string of data */
                    reminderList);                  /* Where to get all the data */
            mReminderListView.setAdapter(mAdapter); /* Set is as the adapter of the ListView instance */
        } else {
            mAdapter.clear();
            mAdapter.addAll(reminderList);
            mAdapter.notifyDataSetChanged();
        }
        cursor.close();
        db.close();
    }

    /* This deletes the reminder */
    public void deleteReminder(View view) {
        View parent = (View) view.getParent();
        TextView reminderTextView = (TextView) parent.findViewById(R.id.reminder_title);
        String reminder = String.valueOf(reminderTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(ReminderContract.ReminderEntry.TABLE,
                ReminderContract.ReminderEntry.COL_REMINDER_TITLE + " = ?",
                new String[]{reminder});
        db.close();
        updateUI();
    }

    public void editReminder(View view) {

    }
}

