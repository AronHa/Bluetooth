package aronharder.bluetooth;

/**
 * Bluetooth
 * A Ruby Editor for Android smartphones
 * Created 2016-05-16
 * By Aron Harder
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity implements View.OnClickListener {
    private String filename;

    private EditText text;
    private Button save;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (EditText) findViewById(R.id.text);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(this);

        filename = getIntent().getStringExtra("File Name");
        if (filename.equals(".rb")){
            Toast.makeText(this,"No filename given",Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this,filename,Toast.LENGTH_LONG).show();
        }
        loadFile();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.back:
                Intent optsIntent = new Intent(getApplicationContext(),StartActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(optsIntent);
                break;
            case R.id.save:
                saveFile();
                break;
        }
    }

    //http://www.mkyong.com/java/how-to-read-file-in-java-fileinputstream/
    public void loadFile(){
        File f = new File(Environment.getExternalStorageDirectory(), filename);
        if (f.exists()) {
            try {
                String content = "";
                FileInputStream fIn = new FileInputStream(f);
                int n = fIn.read();
                while (n != -1) {
                    content+= (char) n;
                    n = fIn.read();
                }
                text.setText(content);
            } catch (IOException e) {
                Log.e("ERROR", "Could not find file", e);
            }
        }
    }

    //http://stackoverflow.com/questions/8330276/write-a-file-in-external-storage-in-android
    public void saveFile(){
        try {
            File f = new File(Environment.getExternalStorageDirectory(), filename);
            //f.createNewFile(); //Not needed?
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(text.getText().toString());
            myOutWriter.close();
            fOut.close();
            Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
        } catch (IOException e){
            Log.e("ERROR", "Could not create file",e);
        }
    }
}
