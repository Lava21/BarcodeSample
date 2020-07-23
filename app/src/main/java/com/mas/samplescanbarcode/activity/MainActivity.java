package com.mas.samplescanbarcode.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mas.samplescanbarcode.R;
import com.mas.samplescanbarcode.adapter.MyAdapter;
import com.mas.samplescanbarcode.helper.PracticeDatabaseHelper;
import com.mas.samplescanbarcode.model.Code;
import com.mas.samplescanbarcode.model.ListItem;

import java.util.ArrayList;
import java.util.List;

import nl.qbusict.cupboard.QueryResultIterable;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    IntentIntegrator scan;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<ListItem> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final PracticeDatabaseHelper databaseHelper = new PracticeDatabaseHelper(this);
        final SQLiteDatabase db = databaseHelper.getWritableDatabase();

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean beep = sharedPreferences.getBoolean("beep", true);
        Boolean frontCamera = sharedPreferences.getBoolean("frontCamera",false);
        int camId;
        if (frontCamera == false){
            camId = 0;
        } else {
            camId = 1;
        }
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        recyclerView = findViewById(R.id.rvContentMain);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();
        adapter = new MyAdapter(listItems, this);
        recyclerView.setAdapter(adapter);
        CardView cardView = findViewById(R.id.cvItem);

        Cursor codes = cupboard().withDatabase(db).query(Code.class).orderBy("_id DESC").getCursor();

        try {
            QueryResultIterable<Code> itr = cupboard().withCursor(codes).iterate(Code.class);
            for (Code bunny : itr){
                ListItem listItem = new ListItem(bunny._id, bunny.name, bunny.type);
                listItems.add(listItem);
                adapter = new MyAdapter(listItems, this);
                recyclerView.setAdapter(adapter);
            }
        } finally {
            codes.close();
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                ListItem item = listItems.get(position);

                cupboard().withDatabase(db).delete(Code.class, item.get_id());
                listItems.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeRemoved(position, listItems.size());
            }
        }).attachToRecyclerView(recyclerView);

        scan = new IntentIntegrator(this);
        scan.setBeepEnabled(beep);
        scan.setCameraId(camId);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan.initiateScan();
            }
        });

        ImageView ivAbout = findViewById(R.id.ivAbout);
        ivAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);
            }
        });

        ImageView ivSettings = findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clearAll){
            PracticeDatabaseHelper databaseHelper = new PracticeDatabaseHelper(this);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            Cursor codes = cupboard().withDatabase(db).query(Code.class).orderBy("_id DESC").getCursor();
            try {
                if (codes.getCount() > 0){
                    cupboard().withDatabase(db).delete(Code.class, null);
                    listItems.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    return true;
                }
            } finally {
                codes.close();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() == null){
                Snackbar snackbar = Snackbar.make(findViewById(R.id.rvContentMain), "Result Not Found", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                Code codeObj = new Code(result.getContents(), result.getFormatName());
                PracticeDatabaseHelper dbHelper = new PracticeDatabaseHelper(this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                long id = cupboard().withDatabase(db).put(codeObj);
                listItems.clear();
                adapter.notifyDataSetChanged();
                Cursor codes = cupboard().withDatabase(db).query(Code.class).orderBy("_id DESC").getCursor();
                try {
                    QueryResultIterable<Code> itr = cupboard().withCursor(codes).iterate(Code.class);
                    for (Code bunny : itr){
                        ListItem listItem = new ListItem(bunny._id, bunny.name, bunny.type);
                        listItems.add(listItem);
                        adapter = new MyAdapter(listItems, this);
                        recyclerView.setAdapter(adapter);
                    }
                } finally {
                    codes.close();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void cardClick(View card){
        TextView textView = findViewById(R.id.tvCode);
        String code = textView.getText().toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, code);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("beep")) {
            scan.setBeepEnabled(sharedPreferences.getBoolean(key, true));
        }

        if (key.equals("frontCamera")){
            int camId;
            if (sharedPreferences.getBoolean(key, false) == false){
                camId = 0;
            } else {
                camId = 1;
            }
            scan.setCameraId(camId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }
}