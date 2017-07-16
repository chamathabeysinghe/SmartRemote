package lk.ac.mrt.cse.smartremotecontroller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.smartremotecontroller.bluetooth.BluetoothManager;
import lk.ac.mrt.cse.smartremotecontroller.database.DataContract;
import lk.ac.mrt.cse.smartremotecontroller.database.RemoteControllerDBHelper;
import lk.ac.mrt.cse.smartremotecontroller.models.Button;

public class MainActivity extends AppCompatActivity {


    Spinner spinner;
    ArrayAdapter<String> spinnerAdapter;
    ArrayList<String> remoteNames;

    ButtonListAdapter listAdapter;
    ArrayList<String> buttonNames;
    ArrayList<Button> buttons;
    ListView listView;

    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager.getInstance(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfigurationActivity();
            }
        });
        RemoteControllerDBHelper.getDbHelper(this);
        db=RemoteControllerDBHelper.getDbHelper(null).getReadableDatabase();

        spinner= (Spinner) findViewById(R.id.spinner);
        remoteNames=new ArrayList<>();
        spinnerAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,remoteNames);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("SELECTED::",spinner.getSelectedItem().toString());
                initializeButtonList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initializeRemoteList();

        buttonNames=new ArrayList<>();
        buttons=new ArrayList<>();
        listAdapter=new ButtonListAdapter(this,0,buttons);
        listView= (ListView) findViewById(R.id.button_list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("CLICKED","ITEMT CLICKED ");
                sendSignal(i);
            }
        });
        initializeButtonList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeRemoteList();
        initializeButtonList();


    }

    /*
    called by the list items
     */
    private void sendSignal(int position){
        Log.e("POSTIOTION",""+position);
        Button button=buttons.get(position);
        Log.e("SEND BUTTON: ",button.getButtonName()+" "+button.getRemoteBrand()+" "+button.getSignal());
        BluetoothManager.getInstance(null).sendSignal(button.getSignal(),button.getRemoteBrand());
    }

    private void initializeRemoteList(){
        String projection[]={DataContract.RemoteControllerColumns.COLUMN_NAME_REMOTE_NAME};
        Cursor cursor=db.query(
                true,
                DataContract.RemoteControllerColumns.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex(DataContract.RemoteControllerColumns.COLUMN_NAME_REMOTE_NAME));
            remoteNames.add(name);
        }
        spinnerAdapter.notifyDataSetChanged();
    }

    private void initializeButtonList(){
        if(spinner.getSelectedItem()==null)return;
        buttonNames.clear();
        buttons.clear();
        String projection[]={DataContract.RemoteControllerColumns.COLUMN_NAME_REMOTE_NAME,DataContract.RemoteControllerColumns.COLUMN_NAME_BRAND_NAME
        ,DataContract.RemoteControllerColumns.COLUMN_NAME_BUTTON_NAME,DataContract.RemoteControllerColumns.COLUMN_NAME_BUTTON_SIGNAL};

        String selection=DataContract.RemoteControllerColumns.COLUMN_NAME_REMOTE_NAME+"=?";
        String selectionArgs[]={spinner.getSelectedItem().toString()};
        Cursor cursor=db.query(
                DataContract.RemoteControllerColumns.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        while (cursor.moveToNext()){
            String buttonName=cursor.getString(cursor.getColumnIndex(DataContract.RemoteControllerColumns.COLUMN_NAME_BUTTON_NAME));
            String remoteName=cursor.getString(cursor.getColumnIndex(DataContract.RemoteControllerColumns.COLUMN_NAME_REMOTE_NAME));
            String brancName=cursor.getString(cursor.getColumnIndex(DataContract.RemoteControllerColumns.COLUMN_NAME_BRAND_NAME));
            String signal=cursor.getString(cursor.getColumnIndex(DataContract.RemoteControllerColumns.COLUMN_NAME_BUTTON_SIGNAL));

            buttonNames.add(buttonName);
            buttons.add(new Button(buttonName,remoteName,brancName,signal));
        }
        listAdapter.notifyDataSetChanged();
    }


    private void openConfigurationActivity(){
        Intent i=new Intent(this,ConfigurationActivity.class);
        startActivity(i);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


class ButtonListAdapter extends ArrayAdapter<Button>{
    List<Button> buttonList;
    Context context;
    public ButtonListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Button> objects) {
        super(context, resource, objects);
        this.context=context;
        this.buttonList=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get the property we are displaying
        Button property = buttonList.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_view_layout, null);

        TextView description = (TextView) view.findViewById(R.id.description);
        TextView buttonName = (TextView) view.findViewById(R.id.buttonName);



        buttonName.setText(property.getButtonName());
        description.setText(property.getRemoteBrand()+" | "+property.getRemoteBrand());



        return view;
    }
}