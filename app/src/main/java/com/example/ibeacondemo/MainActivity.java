package com.example.ibeacondemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibeacondemo.util.ListData;
import com.example.ibeacondemo.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBTAdapter;
    private BluetoothLeAdvertiser mBTAdvertiser;
    private static final int REQUEST_ENABLE_BT = 1;
    private EditText etUUID;
    private Switch switchView;
    private TextView timeView, tv_card01, tv_card02;
    private long Major, Minor;
    private String Time;
    private Handler handler;
    private Handler datahandler;
    private String key, DES, MD5End, XOR2, uuid;
    private TextView tv_key, tv_des, tv_md5, tv_xor, tv_uuid, tv_major, tv_minor;
    ListData listData = new ListData(key, DES, MD5End, XOR2, uuid, Major, Minor);

    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        startService(new Intent(this, MainService.class));
    }


    private void initView() {
        etUUID = findViewById(R.id.et_uuid);
        switchView = findViewById(R.id.switch_view);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flag = true;
                } else {
                    flag = false;
                    stopAdvertise();
                }
            }
        });
        timeView = findViewById(R.id.timeView);
       /* tv_key =findViewById(R.id.key);
        tv_des =findViewById(R.id.des);
        tv_md5 =findViewById(R.id.md5);
        tv_xor =findViewById(R.id.xor);
        tv_uuid =findViewById(R.id.uuid);
        tv_major =findViewById(R.id.major);
        tv_minor =findViewById(R.id.minor);*/
        tv_card01 = findViewById(R.id.card01);
        tv_card02 = findViewById(R.id.card02);
    }

    private void initData() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                timeView.setText((String) msg.obj);
            }
        };
        Threads thread = new Threads();
        thread.start();

        /*datahandler = new Handler(){
            public void handleMessage(Message msg) {
                tv_key.setText(listData.getKey());
                tv_des.setText(listData.getDES());
                tv_md5.setText(listData.getMD5End());
                tv_xor.setText(listData.getXOR());
                tv_uuid.setText(listData.getUUid());
                tv_major.setText(""+listData.getMajor());
                tv_minor.setText(""+listData.getMinor());
            }
        };*/
    }


    public static boolean isBLESupported(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private void setupBLE() {
        if (!isBLESupported(this)) {
            Toast.makeText(this, "device not support ble", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        BluetoothManager manager = getManager(this);
        if (manager != null) {
            mBTAdapter = manager.getAdapter();
        }
        if ((mBTAdapter == null) || (!mBTAdapter.isEnabled())) {
            Toast.makeText(this, "bluetooth not open", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static BluetoothManager getManager(Context context) {
        return (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void startAdvertise(String uuid, int major, int minor) {
        if (mBTAdapter == null) {
            return;
        }
        if (mBTAdvertiser == null) {
            mBTAdvertiser = mBTAdapter.getBluetoothLeAdvertiser();
        }
        mBTAdapter.setName("Test");
        if (mBTAdvertiser != null) {
            mBTAdvertiser.startAdvertising(
                    createAdvertiseSettings(true, 0),
                    createAdvertiseData(
                            UUID.fromString(uuid),
                            major, minor, (byte) 0xc5),
                    mAdvCallback);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopAdvertise() {
        if (mBTAdvertiser != null) {
            mBTAdvertiser.stopAdvertising(mAdvCallback);
            mBTAdvertiser = null;
            //switchView.setChecked(false);
        }
        setProgressBarIndeterminateVisibility(false);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AdvertiseSettings createAdvertiseSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        builder.setConnectable(connectable);
        builder.setTimeout(timeoutMillis);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        return builder.build();
    }


    public AdvertiseData createAdvertiseData(UUID proximityUuid, int major,
                                             int minor, byte txPower) {
        if (proximityUuid == null) {
            throw new IllegalArgumentException("proximitiUuid null");
        }
        byte[] manufacturerData = new byte[23];
        ByteBuffer bb = ByteBuffer.wrap(manufacturerData);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put((byte) 0x02);
        bb.put((byte) 0x15);
        bb.putLong(proximityUuid.getMostSignificantBits());
        bb.putLong(proximityUuid.getLeastSignificantBits());
        bb.putShort((short) major);
        bb.putShort((short) minor);
        bb.put(txPower);

        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.addManufacturerData(0x004c, manufacturerData);
        AdvertiseData adv = builder.build();
        return adv;
    }

    private final AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        public void onStartSuccess(android.bluetooth.le.AdvertiseSettings settingsInEffect) {
            if (settingsInEffect != null) {
                Log.d("debug", "onStartSuccess TxPowerLv="
                        + settingsInEffect.getTxPowerLevel()
                        + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.d("debug", "onStartSuccess, settingInEffect is null");
            }
            //switchView.setChecked(true);
            setProgressBarIndeterminateVisibility(false);
        }

        public void onStartFailure(int errorCode) {
            Log.d("debug", "onStartFailure errorCode=" + errorCode);
            //switchView.setChecked(false);
        }
    };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetMessage(MessageWrap message) {
        Log.e("TAG", "onGetMessage:..... ");
        if (message.bool && flag) {
            initss();
        } else {
            flag = false;
        }
        stopAdvertise();
    }

    private void initss() {
        String card01 = tv_card01.getText().toString().trim();
        String card03 = tv_card02.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
        key = sdf.format(new Date());
        /*String card = "0100020004000000";
        String time = "2018122511233000";*/
        String card2 = etUUID.getText().toString().trim();
        if (card2.length() < 8) {
            Toast.makeText(this, "卡号长度必须为8位", Toast.LENGTH_SHORT).show();
            return;
        }
        String card = card01.concat(card2).concat(card03);
        byte[] keys = Util.merge2BytesTo1Byte(key);
        byte[] cards = Util.merge2BytesTo1Byte(card);
        byte[] DESCard = null;
        try {
            DESCard = Util.encode(cards, keys);

        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] DESCards = Util.concat(keys, DESCard);
        DES = Util.byte2hex(DESCard);
        String MD5Start = Util.byte2hex(DESCards);
        uuid = MD5Start.substring(0, 8).concat("-").concat(MD5Start.substring(8, 12)).concat("-").concat(MD5Start.substring(12, 16))
                .concat("-").concat(MD5Start.substring(16, 20)).concat("-").concat(MD5Start.substring(20, 32));
        MD5End = Util.md5(MD5Start);
        String MD1 = MD5End.substring(0, 8);
        String MD2 = MD5End.substring(8, 16);
        String MD3 = MD5End.substring(16, 24);
        String MD4 = MD5End.substring(24, 32);
        String XOR = Util.XorEncryptAndBaseNew(MD1, MD2);
        String XOR1 = Util.XorEncryptAndBaseNew(XOR, MD3);
        XOR2 = Util.XorEncryptAndBaseNew(XOR1, MD4);
        Major = Integer.valueOf(XOR2.substring(0, 4), 16);
        Minor = Integer.valueOf(XOR2.substring(4, 8), 16);
        listData.setDES(DES);
        listData.setKey(key);
        listData.setMD5End(MD5End);
        listData.setXOR(XOR2);
        listData.setUUid(uuid);
        listData.setMajor(Major);
        listData.setMinor(Minor);
        //datahandler.sendMessage(datahandler.obtainMessage(100, listData));
        //Log.e("---------",key+"-"+uuid+"-"+MD5Start+"-"+MD5End+"-"+XOR2+"-"+Major+"-"+Minor);
        int major = 0;
        if (Major != 0) {
            major = (int) Major;
        }

        int minor = 0;
        if (Minor != 0) {
            minor = (int) Minor;
        }
        startAdvertise(uuid, major, minor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //EventBus注册
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setupBLE();
        switchView.setChecked(flag);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAdvertise();
        switchView.setChecked(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                return;
            }
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    class Threads extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss SS");
                    Time = sdf.format(new Date());
                    handler.sendMessage(handler.obtainMessage(100, Time));
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
