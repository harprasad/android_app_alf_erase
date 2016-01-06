package com.codeofphi.php.erase_x;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by dell on 12/5/2015.
 */
public class BLEDeviceAdapter extends RecyclerView.Adapter<BLEDeviceAdapter
        .ViewHolder> {

    ArrayList<BLEDevice> bleDevices;
    public View.OnClickListener monclick;
    // Provide a suitable constructor (depends on the kind of dataset)
    public BLEDeviceAdapter(ArrayList<BLEDevice> bleDevices, Context context) {
        this.bleDevices = bleDevices;
        mContext = context;

    }
    Context mContext;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device_list, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setOnClickListener(monclick);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvDeviceName.setText(bleDevices.get(position).getName());
        holder.tvDeviceRSSI.setText(bleDevices.get(position).getRssi());
        holder.tvMacid.setText(bleDevices.get(position).getMacid());
    }

    @Override
    public int getItemCount() {
        return bleDevices.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName;
        TextView tvDeviceRSSI;
        TextView tvMacid;
        public ViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tvDeviceName);
            tvDeviceRSSI = (TextView) itemView.findViewById(R.id.tvDeviceRSSI);
            tvMacid      = (TextView) itemView.findViewById(R.id.tvDeviceMAC);

        }

    }

    public void updateData(ArrayList<BLEDevice> bleDevices){
        this.bleDevices = bleDevices;
    }
}