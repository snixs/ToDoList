package com.grey.todolist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    DataBase dbHelper;
    private ListView all_tasks;
    private ArrayAdapter<String> mAdapter;
    private EditText field_text;
    private SharedPreferences prefs;
    private String name_list;
    private TextView mDateDisplay;
    private DatePickerDialog.OnDateSetListener mDateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DataBase(this);
        all_tasks = (ListView)findViewById(R.id.tasks_list);
        mDateDisplay = (TextView) findViewById(R.id.listDate);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        name_list = prefs.getString("listName","");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mDateDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this,mDateListener,year,month,day);
                dialog.show();

            }
        });
        mDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month+1;
                String date = month + "/" + day + "/" + year;
                mDateDisplay.setText(date);
            }
        };
        changeTextAction();
        loadAllTasks();
    }

    private void changeTextAction() {
    }

    private void loadAllTasks() {
        ArrayList<String> taskList = dbHelper.getAllTasks();
        if(mAdapter == null) {
            mAdapter = new ArrayAdapter<String>(this, R.layout.rows, R.id.txt_task, taskList);
            all_tasks.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.add_new_task) {
            final EditText userTaskGet = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.add_task)
                    .setMessage(R.string.add_dialog)
                    .setView(userTaskGet)
                    .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!userTaskGet.getText().toString().matches(""))
                            {
                                String task = String.valueOf(userTaskGet.getText());
                                dbHelper.insertData(task);
                                loadAllTasks();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(View view) {
        View parent = (View)view.getParent();
        TextView txt_task = (TextView)parent.findViewById(R.id.txt_task);
        String task = String.valueOf(txt_task.getText());
        dbHelper.deleteData(task);
        loadAllTasks();
    }
}
