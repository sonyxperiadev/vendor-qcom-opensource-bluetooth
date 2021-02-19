/******************************************************************************
 *  Copyright (c) 2013, The Linux Foundation. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above
 *        copyright notice, this list of conditions and the following
 *        disclaimer in the documentation and/or other materials provided
 *        with the distribution.
 *      * Neither the name of The Linux Foundation nor the names of its
 *        contributors may be used to endorse or promote products derived
 *        from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT
 *  ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 *  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 *  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 *  BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 *  OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 *  IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/

package org.codeaurora.bluetooth.bttestapp.hidd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import org.codeaurora.bluetooth.bttestapp.R;


public class HidDeviceActivity extends AppCompatActivity implements
        HidDeviceWrapper.HidEventListener {

    private static final String TAG= "HidDeviceActivity";

    private Context mCtx;
    private HidDeviceWrapper mHidDeviceWrapper;
    BluetoothAdapter mAdapter;
    private BluetoothDevice mDevice ;

    private boolean mAppReady = false;

    private boolean mConnected = false;

    private boolean mBootMode = false;

    private boolean mNumLockLed = false;

    private boolean mCapsLockLed = false;

    private boolean mScrollLockLed = false;

    private EditText mEtBtAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_hid_device);
        mEtBtAddress =(EditText)findViewById(R.id.id_et_btaddress);
        mCtx = getApplicationContext();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent intent = new Intent(this, HidDeviceWrapper.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        HidFragmentPagerAdapter sectionsPagerAdapter = new HidFragmentPagerAdapter(this,
                getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    void queryLeds(KeyboardFragment fr) {
        fr.updateLeds(mNumLockLed, mCapsLockLed, mScrollLockLed);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mHidDeviceWrapper = ((HidDeviceWrapper.LocalBinder) service).getService();
            mHidDeviceWrapper.setEventListener(HidDeviceActivity.this);
            log("onServiceConnected");

        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            mHidDeviceWrapper = null;
            log("onServiceDisconnected " + className);

        }
    };



    @Override
    public void onApplicationState(boolean registered) {
        log("onApplicationState " + registered);
    }

    @Override
    public void onPluggedDeviceChanged(BluetoothDevice device) {
        log("onPluggedDeviceChanged" + device);
    }

    @Override
    public void onConnectionState(BluetoothDevice device, boolean connected) {
        log("onConnectionState " + device + " " + connected);
    }

    @Override
    public void onProtocolModeState(boolean bootMode) {
        log("onProtocolModeState " + bootMode);
    }

    @Override
    public void onKeyboardLedState(boolean numLock, boolean capsLock, boolean scrollLock) {
        log("onKeyboardLedState(): numLock=" + numLock + " capsLock=" + capsLock
                + " scrollLock=" + scrollLock);

        mNumLockLed = numLock;
        mCapsLockLed = capsLock;
        mScrollLockLed = scrollLock;

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment fr  : fragments) {
            Log.d(TAG, "onKeyboardLedState  " + fr);
            if (fr instanceof  KeyboardFragment) {
                KeyboardFragment frkey = (KeyboardFragment)fr;
                frkey.updateLeds(numLock, capsLock, scrollLock);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestroy");
        if (mHidDeviceWrapper != null) {
            unbindService(mConnection);
        }
    }

    public void connect(View v){
        String address = mEtBtAddress.getText().toString().trim();
        log("address = " + address );
        if (address.length() != 17) {
            mEtBtAddress.setError("Enter Proper Address");
            return;
        }
        try {
            mDevice = mAdapter.getRemoteDevice(address);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, e.toString());
            return;
        }
        if (mDevice != null && mHidDeviceWrapper != null) {
            mHidDeviceWrapper.connect(mDevice);
            log("Connect " + mDevice);
        } else {
            log("Error in connect "+ mDevice +" " + mHidDeviceWrapper );
        }
    }


    public void disconnect(View view){
        mHidDeviceWrapper.disconnect(mDevice);
        log("disconnect");
    }

    HidDeviceWrapper getHidDeviceWrapper() {
        return mHidDeviceWrapper;
    }

    private void log(String msg) {
        Log.d(TAG,msg);
    }
}
