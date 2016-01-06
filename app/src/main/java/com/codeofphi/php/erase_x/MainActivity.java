package com.codeofphi.php.erase_x;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter blueadapter;
    private SparseArray<BluetoothDevice> mdevices;
    private final static int REQUEST_ENABLE_BT = 1;
    private RecyclerView mRecyclerView;
    private BLEDeviceAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<BLEDevice> bleDevices;
    ArrayList<String>macids;
    public TextView message_box ;
    public static UUID writeuuid;
    public BluetoothLeScanner lescanner;
    private BluetoothGattCharacteristic characteristic;
    private ScanCallback mscancallback;
    /*below is for <5 android*/
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

            if (macids.contains(device.getAddress()))
                return; //duplicate
            else {
                macids.add(device.getAddress());
            }
            BLEDevice bleDevice = new BLEDevice();
            bleDevice.setName(device.getName());
            bleDevice.setRssi(rssi + "");
            bleDevice.setMacid(device.getAddress());
            bleDevice.device = device;
            bleDevices.add(bleDevice);
            mAdapter.updateData(bleDevices);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        macids = new ArrayList<String>();
        blueadapter = manager.getAdapter();
        mdevices = new SparseArray<BluetoothDevice>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_items);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        bleDevices = new ArrayList<BLEDevice>();
        mAdapter = new BLEDeviceAdapter(bleDevices, getApplicationContext());
        mAdapter.monclick = clickon_item;
        mRecyclerView.setAdapter(mAdapter);
        message_box = (TextView)findViewById(R.id.textView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (blueadapter == null || !blueadapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
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

    public void button_clicked(View v) {
        if(v.getId() == R.id.b_scan) {
            TextView printbox = (TextView) findViewById(R.id.textView);
            printbox.setText(getResources().getString(R.string.searching_ble));
            bleDevices.clear();
            macids.clear();
            blueadapter.stopLeScan(leScanCallback);
            blueadapter.startLeScan(leScanCallback);

            task_after_x_secs task = new task_after_x_secs();
            task.execute(4000);

        }
        if(v.getId() == R.id.b_stop){
            TextView printbox = (TextView) findViewById(R.id.textView);
            printbox.setText(getResources().getString(R.string.stop_scan_ble));
            blueadapter.stopLeScan(leScanCallback);
        }

    }

    private class task_after_x_secs extends AsyncTask<Integer,Integer,String>{

        @Override
        protected String doInBackground(Integer... params) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            blueadapter.stopLeScan(leScanCallback);
            message_box.setText("Scan complete");
        }

    }

    private final View.OnClickListener clickon_item = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = mRecyclerView.getChildAdapterPosition(v);
            message_box.setText("you clicked on " + position);
            BLEDevice dev =bleDevices.get(position);
            BluetoothGatt bluegatt = dev.device.connectGatt(getApplicationContext(),false,mgattcallback);//TODO connect
            dev.device.createBond();

        }
    };

    private BluetoothGattCallback mgattcallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      Toast toast = Toast.makeText(getApplicationContext(),"status = "+ status, Toast.LENGTH_SHORT);
//                      BluetoothProfile.STATE_CONNECTED =2
                      toast.show();
                      toast = Toast.makeText(getApplicationContext(),"new state = "+ newState, Toast.LENGTH_SHORT);
                      toast.show();
                      if(newState == BluetoothProfile.STATE_CONNECTED)
                      {
                          gatt.discoverServices();
                          Toast to  = Toast.makeText(getApplicationContext(),"Discovering services",Toast.LENGTH_SHORT);
                          to.show();
                      }
                  }
              });
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            super.onServicesDiscovered(gatt, status);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast to = Toast.makeText(getApplicationContext(),"Discover status ="+status,Toast.LENGTH_SHORT);
//                    to.show();
                    List<BluetoothGattService> services = gatt.getServices();
                    for(BluetoothGattService service:services){
                        List<BluetoothGattCharacteristic>characteristics = service.getCharacteristics();
                        for(BluetoothGattCharacteristic charecs:characteristics){
                            if(charecs.getUuid().toString().contains("0000900a")){
                                characteristic = charecs;
                                Toast toast = Toast.makeText(getApplicationContext(),"found one write service",Toast.LENGTH_SHORT);
                                toast.show();
                                byte[] arr = {99};
                                charecs.setValue(arr);
                                Toast to = Toast.makeText(getApplicationContext(),"writing 99",Toast.LENGTH_SHORT);
                                to.show();
                                gatt.writeCharacteristic(charecs);
                            }
                        }

                    }
                }
            });
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(getApplicationContext(),"byte written to"+characteristic.getValue()[0], Toast.LENGTH_SHORT);
                    // BluetoothProfile.STATE_CONNECTED =2
                    toast.show();
                }
            });
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, final int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast t = Toast.makeText(getApplicationContext(),"write status ="+status,Toast.LENGTH_SHORT);
                    t.show();
                }
            });

        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
        }
    };

}
