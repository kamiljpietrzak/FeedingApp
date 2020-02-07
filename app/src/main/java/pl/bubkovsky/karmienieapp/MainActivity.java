package pl.bubkovsky.karmienieapp;


import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    DatabaseHelper myDB;
    EditText info, amountOfMilk;
    Button feeding, endOfFeeding, refresh, btnViewAll;
    TextView lastFeeding, timeOfFeeding;
    CheckBox left_breast, right_breast, bottle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myDB = new DatabaseHelper(this);
        info = findViewById(R.id.info);
        amountOfMilk = findViewById(R.id.ilosc_mleka);
        feeding = findViewById(R.id.feed_start);
        endOfFeeding = findViewById(R.id.feed_stop);
        lastFeeding = findViewById(R.id.textView_last_feed);
        refresh = findViewById(R.id.refresh_button);
        left_breast = findViewById(R.id.checkBox);
        right_breast = findViewById(R.id.checkBox2);
        bottle = findViewById(R.id.checkBox3);
        timeOfFeeding = findViewById(R.id.time_of_feeding);
        btnViewAll = findViewById(R.id.view_all);
        findViewById(R.id.db_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { exportDB(); }});
        feed_click();
        feed_stop_click();
        refreshclick();
        viewAll();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void feed_click (){
        feeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInserted(myDB.start_feed());
            }
        });

    }

    public void refreshclick (){
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastFeeding.setText(myDB.getDataFromLastIndex());
                try {
                    timeOfFeeding.setText(myDB.calculateTimeOfFeeding());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void viewAll(){
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = myDB.getAllData();
                if(cursor.getCount()==0){
                    showMessage("Error", "Nothing  found");
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                while(cursor.moveToNext()){
                    buffer.append("ID: " + cursor.getString(0)+"\n");
                    buffer.append("Start Karmienia: " + cursor.getString(1)+"\n");
                    buffer.append("Info: " + cursor.getString(2)+"\n");
                    buffer.append("Ilość mleka: " + cursor.getString(3)+"\n");
                    buffer.append("Stop Karmienia: " + cursor.getString(4)+"\n");
                    buffer.append("Pierś lewa: " + cursor.getString(5)+"\n");
                    buffer.append("Pierś prawa: " + cursor.getString(6)+"\n");
                    buffer.append("Butla: " + cursor.getString(7)+"\n");
                }
                showMessage("Dane", buffer.toString());
            }
        });
    }

    public void feed_stop_click (){
        endOfFeeding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInserted(myDB.stop_feed(info.getText().toString(), amountOfMilk.getText().toString()));
                lastFeeding.setText(String.valueOf(Calendar.getInstance().getTime()));
                try {
                    timeOfFeeding.setText(myDB.calculateTimeOfFeeding());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if ((left_breast).isChecked()) {
                    myDB.check_box(1);
                }else if ((right_breast).isChecked()){
                    myDB.check_box(2);
                }else if ((bottle).isChecked()){
                    myDB.check_box(3);
                }
            }
        });
    }
    public void showMessage (String title, String message){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.show();
    }
    private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "pl.bubkovsky.karmienieapp" +"/databases/Karmienie.db";
        String backupDBPath = "Karmienie.db";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    private void isInserted(boolean check) {
        if(check){
            Toast.makeText(MainActivity.this, "Uzupełnione", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(MainActivity.this, "Nie uzupełnione", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
