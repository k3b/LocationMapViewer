package de.k3b.android.locationMapViewer.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class GeoIntentDemoActivity extends Activity {
    private static final int ACTION_ID = 4711;
    private String appName;
    private EditText editMime;
    private EditText editUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_intent_demo);
        appName = getString(R.string.app_name) + ":";

        Button rundDemo = (Button) findViewById(R.id.runDemo);
        editMime = (EditText) findViewById(R.id.editMime);
        editUri = (EditText) findViewById(R.id.editUri);

        rundDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDemo();
            }
        });
    }

    protected void startDemo() {
        String uriString = editUri.getText().toString(); // "geo:53.2,8.8?(name)&z=5";
        String mimeString = editMime.getText().toString(); // null or */*
        if ((mimeString != null) && (mimeString.length() == 0)) mimeString = null;

        Uri uri = Uri.parse(uriString);
        Intent demo = new Intent();
        demo.setDataAndType(uri, mimeString);
        Toast.makeText(this, appName + "Starting " + uriString + "-" + demo.getType(), Toast.LENGTH_SHORT).show();
        try {
            startActivityForResult(Intent.createChooser(demo,"Choose app to show location"), ACTION_ID);
        } catch (Exception e) {
            Toast.makeText(this, appName + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String resultIntent = getUri(data);
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, appName + "got result " + resultIntent, Toast.LENGTH_LONG).show();
    }

    private static String getUri(Intent src) {
        String intentUri = (src != null) ? src.toUri(Intent.URI_INTENT_SCHEME) : null;
        return intentUri;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geo_intent_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
