package com.example.ibeacondemo;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ibeacondemo.ui.activity.LoginActivity;
import com.example.ibeacondemo.util.BrightnessUtil;
import com.example.ibeacondemo.util.DoubleClickHelper;
import com.example.ibeacondemo.util.ListData;
import com.example.ibeacondemo.util.SPUtils;
import com.example.ibeacondemo.util.Util;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.tamsiree.rxfeature.tool.RxQRCode;
import com.tamsiree.rxkit.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static com.hjq.http.EasyUtils.postDelayed;
import static com.tamsiree.rxkit.RxTool.getContext;


public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter mBTAdapter;
    private BluetoothLeAdvertiser mBTAdvertiser;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int RQ_WRITE_SETTINGS = 100;
    private EditText etUUID;
    //    private Switch switchView;
    private ToggleButton switchView;
    private TextView timeView, cardNo, tv_card01, tv_card02;
    private long Major, Minor;
    private String Time;
    private Handler handler;
    private Handler datahandler;
    private String key, DES, MD5End, XOR2, uuid;
    ListData listData = new ListData(key, DES, MD5End, XOR2, uuid, Major, Minor);

    private ImageView mQRImageCode;
    private TextView tv_time_second, mTvRefresh;
    private int second = 5;

    private boolean flag = true; //开启循坏 5s一次
    //    private boolean flag; //默认不开启
    private boolean power_switch; //开关按钮

    private static android.os.Handler Handler = new Handler();
    private static Runnable mRunnable = null;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 6000:
                    initQRcode();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BrightnessUtil.setBrightness(this, 250);
        setContentView(R.layout.activity_main);
        initView();
        initData();
//        initQRcode();
//        AuthCode(tv_time_second, second);
        startService(new Intent(this, MainService.class));
//        checkPermissions();

    }

    /**
     * 生成二维码
     */
    private void initQRcode() {
//        RxQRCode.createQRCode("时间戳:" + System.currentTimeMillis(), 600, 600, mQRImageCode);
        if (uuid != null) {
            RxQRCode.createQRCode((uuid.replace("-", "")) + XOR2, 600, 600, mQRImageCode);
            Log.i("TAG", "二维码数据: " + (uuid.replace("-", "")) + XOR2);
        }

    }


    /**
     * 二维码定时器
     *
     * @param view
     * @param second
     */
    private void AuthCode(final TextView view, final int second) {
        mRunnable = new Runnable() {
            int mSumNum = second;

            @Override
            public void run() {
                Handler.postDelayed(mRunnable, 1000);
                view.setText(mSumNum + "");
                view.setEnabled(false);
                mSumNum--;
                if (mSumNum < 0) {
                    view.setText(1 + "");
                    view.setEnabled(true);
//                    Message message = new Message();
//                    message.what = 6000;
//                    mHandler.sendMessage(message);
                    // 干掉这个定时器，下次减不会累加
                    Handler.removeCallbacks(mRunnable);
                    AuthCode(view, second);
                }
            }
        };
        Handler.postDelayed(mRunnable, 0);
    }


    private void initView() {
        mQRImageCode = findViewById(R.id.iv_code);
        tv_time_second = findViewById(R.id.tv_time_second);
//        etUUID = findViewById(R.id.et_uuid);
        cardNo = findViewById(R.id.cardNo);

        switchView = findViewById(R.id.switch_view);
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    power_switch = true;
                    initss();
                    RxToast.info("正在发送通行信号");
                } else {
                    power_switch = false;
                    stopAdvertise();
                    RxToast.info("通行信号已关闭");
                }
            }
        });
        timeView = findViewById(R.id.timeView);

        //手动刷新二维码
        /*mTvRefresh = findViewById(R.id.tv_refresh);
        mTvRefresh.setOnClickListener(view -> {
            Handler.removeCallbacks(mRunnable);
            initQRcode();
            RxToast.success("二维码更新成功");
            tv_time_second.setText(second + "");
            AuthCode(tv_time_second, second);
        });*/

//        tv_card01 = findViewById(R.id.card01);
//        tv_card02 = findViewById(R.id.card02);
    }

    @SuppressLint("HandlerLeak")
    private void initData() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                timeView.setText((String) msg.obj);
            }
        };
        Threads thread = new Threads();
        thread.start();
    }


    public static boolean isBLESupported(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 检测蓝牙
     */
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

    /**
     * 获取权限
     */
    private void checkPermissions() {

        XXPermissions.with(this)
                .permission(Permission.WRITE_SETTINGS)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
                            RxToast.success("权限获取成功");
                        } else {
                            RxToast.error("获取部分权限成功，但部分权限未正常授予");
                        }
                    }


                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            RxToast.info("被永久拒绝授权，请手动授予录音和日历权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(MainActivity.this, permissions);
                        } else {
                            RxToast.error("权限失败");
                        }
                    }
                });


/*
        //修改系统屏幕亮度需要修改系统设置的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //如果当前平台版本大于23平台
            if (!Settings.System.canWrite(MainActivity.this)) {
                Uri selfPackageUri = Uri.parse("package:"
                        + getPackageName());
                Intent writeScreen = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, selfPackageUri);
                startActivityForResult(writeScreen,RQ_WRITE_SETTINGS);
            }
        } else {
            //Android6.0以下的系统则直接修改亮度
        }*/
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
//        mBTAdapter.setName("Test");
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

    /**
     * 设置广播参数
     *
     * @param connectable
     * @param timeoutMillis
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AdvertiseSettings createAdvertiseSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);//设置广播的模式，低功耗，平衡和低延迟三种模式；
        builder.setConnectable(connectable);
        builder.setTimeout(timeoutMillis);//设置广播的最长时间，设为0时，代表无时间限制会一直广播
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);//设置广播的信号强度
        return builder.build();
    }


    /**
     * 创建ibeacon 广播数据
     *
     * @param proximityUuid
     * @param major
     * @param minor
     * @param txPower
     * @return
     */
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
        Log.e("TAG", "ibeacon 广播数据: " + adv.toString());
        return adv;
    }

    /**
     * 开始广播后的回调。提示广播开启是否成功。
     */
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
        stopAdvertise();
        if (message.bool && flag) {
            initss();
        } else {
            flag = false;
        }
    }

    private void initss() {
//        String card01 = tv_card01.getText().toString().trim();
//        String card03 = tv_card02.getText().toString().trim();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
        key = sdf.format(new Date());
        /*String card = "0100020004000000";
        String time = "2018122511233000";*/
//        String card2 = etUUID.getText().toString().trim();
//        if (card2.length() < 8) {
//            Toast.makeText(this, "卡号长度必须为8位", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        String card = card01.concat(card2).concat(card03);
        //获取记录的卡号
        String card = (String) SPUtils.get(MainActivity.this, "cloudCardNo", "");
//        Log.i("TAG", "initss: " + card);
        if (card.length() < 0) {
            ToastUtils.show("当前未获取到卡号信息");
            return;
        }
        //界面显示卡号 以星号表示
        cardNo.setText(Util.getStarString(card, 2, 2));

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
        Log.e("---------", key + "-" + uuid + "-" + MD5Start + "-" + MD5End + "-" + XOR2 + "-" + Major + "-" + Minor);
        int major = 0;
        if (Major != 0) {
            major = (int) Major;
        }

        int minor = 0;
        if (Minor != 0) {
            minor = (int) Minor;
        }
        //根据开关来判断是否发送广播数据
        if (power_switch) {
            startAdvertise(uuid, major, minor);
        }
        initQRcode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //EventBus注册
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setupBLE();
//        switchView.setChecked(flag);// false 默认不开启

        AuthCode(tv_time_second, second);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAdvertise();
        switchView.setChecked(false);//false 默认不开启
        Handler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == RQ_WRITE_SETTINGS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(MainActivity.this)) {
                    return;
                } else {
                    RxToast.success("权限被拒绝");
                }
            }
        }*/

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
        Handler.removeCallbacks(mRunnable);
        stopService(new Intent(MainActivity.this, MainService.class));
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


    @Override
    public void onBackPressed() {
        if (!DoubleClickHelper.isOnDoubleClick()) {
            RxToast.info("再按一次退出登录");
            return;
        }

        postDelayed(() -> {
            // 进行内存优化，销毁掉界面
            startActivity(new Intent(getContext(), LoginActivity.class));
            //清除key值已经对应的值
            SPUtils.remove(MainActivity.this, "cloudCardNo");
//            boolean cloudCardNo2 = SPUtils.contains(MainActivity.this, "cloudCardNo");
//            Log.e("TAG", "onBackPressed: " + cloudCardNo2);
            finish();
            // 销毁进程（注意：调用此 API 可能导致当前 Activity onDestroy 方法无法正常回调）
            // System.exit(0);
        }, 300);

    }
}
