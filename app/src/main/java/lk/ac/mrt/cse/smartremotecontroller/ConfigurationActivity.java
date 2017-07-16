package lk.ac.mrt.cse.smartremotecontroller;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import lk.ac.mrt.cse.smartremotecontroller.bluetooth.BluetoothManager;
import lk.ac.mrt.cse.smartremotecontroller.database.DataContract;
import lk.ac.mrt.cse.smartremotecontroller.database.RemoteControllerDBHelper;

public class ConfigurationActivity extends AppCompatActivity {

    Button saveButton;
    Button captureButton;
    EditText remoteName;
    EditText remoteBrand;
    EditText buttonName;
    ListView buttonListView;
    HashMap<String,String> buttonList=new HashMap<>();
    ArrayList<String> buttonNames=new ArrayList<>();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        this.setTitle("New Remote");
        saveButton= (Button) findViewById(R.id.button2);
        captureButton= (Button) findViewById(R.id.button);
        remoteName= (EditText) findViewById(R.id.editText);
        remoteBrand= (EditText) findViewById(R.id.editText3);
        buttonName= (EditText) findViewById(R.id.editText2);
        buttonListView= (ListView) findViewById(R.id.button_list_view);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSaveButton();
            }
        });
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCaptureButton();
            }
        });
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,buttonNames);
        buttonListView.setAdapter(adapter);
    }

    public void clickSaveButton(){

        SQLiteDatabase db=RemoteControllerDBHelper.getDbHelper(null).getWritableDatabase();

        String remoteName=this.remoteName.getText().toString();
        String remoteBrand=this.remoteBrand.getText().toString();
        Log.e("CLICK","SAVE CLICKED");

        for(Iterator<String> iter=buttonList.keySet().iterator();iter.hasNext();){
            String buttonName=iter.next();
            ContentValues contentValues=new ContentValues();
            contentValues.put(DataContract.RemoteControllerColumns.COLUMN_NAME_REMOTE_NAME,remoteName);
            contentValues.put(DataContract.RemoteControllerColumns.COLUMN_NAME_BRAND_NAME,remoteBrand);
            contentValues.put(DataContract.RemoteControllerColumns.COLUMN_NAME_BUTTON_NAME,buttonName);
            contentValues.put(DataContract.RemoteControllerColumns.COLUMN_NAME_BUTTON_SIGNAL,buttonList.get(buttonName));
            long l=db.insert(DataContract.RemoteControllerColumns.TABLE_NAME,null,contentValues);
            Toast.makeText(this,""+l,Toast.LENGTH_SHORT).show();
        }
    }

    public void clickCaptureButton(){
        Log.e("MYCODE","11111111111111111");

        String buttonName=this.buttonName.getText().toString();
        String signal= BluetoothManager.getInstance(null).getSignal();
        buttonNames.add(buttonName);
        buttonList.put(buttonName,signal);
        adapter.notifyDataSetChanged();
        Log.e("MYCODE","adfad"+signal);

        this.buttonName.setText("");
    }
}
