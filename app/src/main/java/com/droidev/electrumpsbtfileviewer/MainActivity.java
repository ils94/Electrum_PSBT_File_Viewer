package com.droidev.electrumpsbtfileviewer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private String fileContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            Uri uri = intent.getData();
            readTextFile(uri);
        }
    }

    private void readTextFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            fileContent = stringBuilder.toString();
            textView.setText(fileContent);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to open file.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_copy) {
            copyToClipboard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Text File Content", fileContent);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "PSBT copied!", Toast.LENGTH_SHORT).show();

        openElectrumApp();
    }

    private void openElectrumApp() {
        try {
            Intent launchIntent = new Intent();
            launchIntent.setClassName("org.electrum.electrum", "org.kivy.android.PythonActivity");
            Toast.makeText(this, "Opening Electrum Bitcoin Wallet...", Toast.LENGTH_SHORT).show();
            startActivity(launchIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to open Electrum: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}