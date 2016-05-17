package aronharder.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Created by aronharder on 5/17/16.
 */
public class StartActivity extends Activity implements View.OnClickListener {
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;

    private EditText filename;
    private Button open;
    private Button bluetooth;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        filename = (EditText) findViewById(R.id.filename);
        open = (Button) findViewById(R.id.open);
        bluetooth = (Button) findViewById(R.id.bluetooth);

        open.setOnClickListener(this);
        bluetooth.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.open:
                Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mainIntent.putExtra("File Name",filename.getText().toString()+".rb");
                startActivity(mainIntent);
                break;
            case R.id.bluetooth:
                sendViaBluetooth(v);
                break;
        }
    }

    //https://www.youtube.com/watch?v=6hQ87u9v7SY
    public void sendViaBluetooth(View v){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
        } else {
            enableBluetooth();
        }
    }
    public void enableBluetooth(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            File f = new File(Environment.getExternalStorageDirectory(),
                    filename.getText().toString()+".rb");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appsList = pm.queryIntentActivities(intent, 0);

            if (appsList.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : appsList) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }
                if (! found) {
                    Toast.makeText(this, "Bluetooth hasn't been found", Toast.LENGTH_LONG).show();
                } else {
                    intent.setClassName(packageName, className);
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(this, "Bluetooth is cancelled", Toast.LENGTH_LONG).show();
        }
    }
}
