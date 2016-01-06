package com.codeofphi.php.erase_x;

import android.bluetooth.BluetoothDevice;

/**
 * Created by dell on 12/5/2015.
 */
public class BLEDevice {

    String rssi;
    String name;
    String macid;
    BluetoothDevice device;


    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacid() {
        return macid;
    }

    public void setMacid(String macid) {
        this.macid = macid;
    }
}
