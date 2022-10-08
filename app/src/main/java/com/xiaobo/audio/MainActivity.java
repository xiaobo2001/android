package com.xiaobo.audio;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ThemeUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.bugly.crashreport.CrashReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {
    private RadioGroup radioGroup;
    private EditText UserInput;
    private AudioManager audioManager;
    private TextView alert;
    private TextView alertMax;
    private SeekBar seekBarSystem;
    private SeekBar seekBarCall;
    private SeekBar seekBarMusic;
    private Button systemKill;
    private Button callKill;
    private Button audioKill;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CrashReport.initCrashReport(getApplicationContext(), "5c434a83bf", false);
        CrashReport.initCrashReport(getApplicationContext());
//        Bugly.init(getApplicationContext(), "5c434a83bf", false);
        Intent intent = new Intent(this, AudioUpdate.class);
        MainActivity.this.startService(intent);
        seekBarSystem = (SeekBar) findViewById(R.id.SeekBarSystem);
        seekBarCall = (SeekBar) findViewById(R.id.SeekBarCall);
        seekBarMusic = (SeekBar) findViewById(R.id.SeekBarMusic);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        radioGroup = (RadioGroup) findViewById(R.id.RadioGroup);
        UserInput = (EditText) findViewById(R.id.UerInput);
        alert = (TextView) findViewById(R.id.alertTitle);
        alertMax = (TextView) findViewById(R.id.alertTitleMax);
        systemKill = (Button) findViewById(R.id.systemKill);
        callKill = (Button) findViewById(R.id.callKill);
        audioKill = (Button) findViewById(R.id.audioKill);
        alertMax.setText(getString(R.string.SystemMaxAudio) + audioManager.getStreamMaxVolume(1) +
                getString(R.string.CallMaxAudio) + audioManager.getStreamMaxVolume(0) +
                getString(R.string.MusicMaxAudio) + audioManager.getStreamMaxVolume(3));
        getHash();
        setAudioManager(alert);
        setRadioGroup(audioManager, alert, UserInput, alertMax);
        seekBar(audioManager, alert, seekBarSystem,
                seekBarMusic, seekBarCall);
        setAudioImage(seekBarSystem, seekBarMusic, seekBarCall, audioManager);
        setAudioManagerKill(systemKill, callKill, audioKill, audioManager);
//        Toast.makeText(MainActivity.this, "优化用户体验", Toast.LENGTH_LONG).show();
//        try {
//            upDate();
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    @SuppressLint("SetTextI18n")
    public void setRadioGroup(@NonNull AudioManager audioManager, TextView alert, @NonNull EditText UserInput, @NonNull TextView alertMax) {
        setAudioManager(alert);
        UserInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.e("错误", String.valueOf(UserInput.getText()));
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (UserInput.getText().length() > 0) {
//                    Log.e("错误", String.valueOf(Integer.parseInt(String.valueOf(charSequence))));
                    int userNumber = Integer.parseInt(String.valueOf(charSequence));
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
                        @RequiresApi(api = Build.VERSION_CODES.P)
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            switch (i) {
                                case R.id.SystemAudio:
                                    int systemAudioMax = audioManager.getStreamMaxVolume(1);
                                    int systemAudioMin = audioManager.getStreamMinVolume(1);
                                    if (userNumber > systemAudioMax) {
                                        Toast.makeText(MainActivity.this, "最大值为:" + systemAudioMax
                                                + "请重新调整", Toast.LENGTH_SHORT).show();
                                    } else if (userNumber < systemAudioMin) {
                                        Toast.makeText(MainActivity.this, "最小值为:" + systemAudioMin
                                                + "请重新调整", Toast.LENGTH_SHORT).show();
                                    } else {
                                        audioManager.setStreamVolume(1, userNumber
                                                , AudioManager.FLAG_SHOW_UI);
                                        setAudioManager(alert);
                                        seekBar(audioManager, alert, seekBarSystem,
                                                seekBarMusic, seekBarCall);
                                    }
                                    break;
                                case R.id.callAudio:
                                    int callAudioMax = audioManager.getStreamMaxVolume(0);
                                    int callAudioMin = audioManager.getStreamMinVolume(0);
                                    if (userNumber > callAudioMax) {
                                        Toast.makeText(MainActivity.this, "最大值为:" + callAudioMax
                                                + "请重新调整", Toast.LENGTH_SHORT).show();
                                    } else if (userNumber < callAudioMin) {
                                        Toast.makeText(MainActivity.this, "最小值为:" + callAudioMin
                                                + "请重新调整", Toast.LENGTH_SHORT).show();
                                    } else {
                                        audioManager.setStreamVolume(0, userNumber
                                                , AudioManager.FLAG_SHOW_UI);
                                        setAudioManager(alert);
                                        seekBar(audioManager, alert, seekBarSystem,
                                                seekBarMusic, seekBarCall);
                                    }
                                    break;
                                case R.id.musicAudio:
                                    int musicAudioMax = audioManager.getStreamMaxVolume(0);
                                    int musicAudioMin = audioManager.getStreamMinVolume(0);
                                    if (userNumber > musicAudioMax) {
                                        Toast.makeText(MainActivity.this, "最大值为:" + musicAudioMax
                                                + "请重新调整", Toast.LENGTH_SHORT).show();
                                    } else if (userNumber < musicAudioMin) {
                                        Toast.makeText(MainActivity.this, "最小值为:" + musicAudioMin
                                                + "请重新调整", Toast.LENGTH_SHORT).show();
                                    } else {
                                        audioManager.setStreamVolume(3, userNumber
                                                , AudioManager.FLAG_SHOW_UI);
                                        setAudioManager(alert);
                                        seekBar(audioManager, alert, seekBarSystem,
                                                seekBarMusic, seekBarCall);
                                    }
                                    break;
                                default:
                                    Toast.makeText(MainActivity.this, "请选择类型",
                                            Toast.LENGTH_SHORT).show();
                            }
                        }

                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

    public void seekBar(@NonNull AudioManager audioManager, TextView alert, @NonNull SeekBar seekBarSystem,
                        @NonNull SeekBar seekBarMusic, @NonNull SeekBar seekBarCall) {

        seekBarSystem.setMax(audioManager.getStreamMaxVolume(1));
        seekBarSystem.setProgress(audioManager.getStreamVolume(1));
        seekBarCall.setMax(audioManager.getStreamMaxVolume(0));
        seekBarCall.setProgress(audioManager.getStreamVolume(0));
        seekBarMusic.setMax(audioManager.getStreamMaxVolume(3));
        seekBarMusic.setProgress(audioManager.getStreamVolume(3));
        seekBarSystem.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(1, i
                        , AudioManager.FLAG_SHOW_UI);
                setAudioManager(alert);
                setAudioImage(seekBarSystem, seekBarMusic, seekBarCall, audioManager);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarCall.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(0, i
                        , AudioManager.FLAG_SHOW_UI);
                setAudioManager(alert);
                setAudioImage(seekBarSystem, seekBarMusic, seekBarCall, audioManager);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(3, i
                        , AudioManager.FLAG_SHOW_UI);
                setAudioManager(alert);
                setAudioImage(seekBarSystem, seekBarMusic, seekBarCall, audioManager);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void setAudioManager(@NonNull TextView alert) {
        alert.setText("系统音量:" + audioManager.getStreamVolume(1) +
                " 通话音量:" + audioManager.getStreamVolume(0) +
                " 多媒体音量:" + audioManager.getStreamVolume(3));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            setAudioManager(alert);
            seekBar(audioManager, alert, seekBarSystem,
                    seekBarMusic, seekBarCall);
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            setAudioManager(alert);
            seekBar(audioManager, alert, seekBarSystem,
                    seekBarMusic, seekBarCall);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void upDate() throws PackageManager.NameNotFoundException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PackageManager packageManager = MainActivity.this.getPackageManager();
                PackageInfo packageInfo = null;
                try {
                    packageInfo = packageManager.getPackageInfo(MainActivity.this.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                final String URL = "jdbc:mysql://39.100.238.249:3306/xiaobo";
                final String USER = "xiaobo";
                final String PWD = "wwWDFzcjR4wtRLjB";
                String userAlter = "";
                int code = packageInfo.versionCode;
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Connection connection = DriverManager.getConnection(URL, USER, PWD);
                    String sql = "select appValue,appName from app;";
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery(sql);
                    while (resultSet.next()) {
                        int count = resultSet.getInt("appValue");
                        userAlter = resultSet.getString("appValue");
                        Toast.makeText(MainActivity.this, count + userAlter, Toast.LENGTH_SHORT).show();
                        if (count < code) {
                            alert("发现新版本是否更新", 1, userAlter);
                        } else if (count == code) {
                            alert("暂无最新版本", 0, userAlter);
                        } else {
                            alert("APP版本号异常，强制退出", 1, userAlter);
                        }
                    }
                    resultSet.close();
                    statement.close();
                    connection.close();
                } catch (Exception e) {
//                    Log.e("错误", String.valueOf(e));
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void alert(String alert, int i, String url) {
        AlertDialog.Builder systemAlert = new AlertDialog.Builder(MainActivity.this);
        systemAlert.setCancelable(false);
        systemAlert.setTitle("温馨提示");
        systemAlert.setMessage(alert);
        systemAlert.setNeutralButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Toast.makeText(MainActivity.this, "暂无更新", Toast.LENGTH_SHORT).show();
                } else if (i == 1) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                } else {
                    Toast.makeText(MainActivity.this, "暂无更新", Toast.LENGTH_SHORT).show();
                    android.os.Process.killProcess(Process.myPid());
                }
            }
        });
        systemAlert.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        systemAlert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.update:
//                try {
//                    upDate();
//                } catch (PackageManager.NameNotFoundException e) {
//                    e.printStackTrace();
//                }
                Toast.makeText(MainActivity.this, "详情见关于", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about:
                AlertDialog.Builder systemAlter = new AlertDialog.Builder(MainActivity.this);
                systemAlter.setTitle("xiaobo的话");
                systemAlter.setMessage("新增崩溃检测模块，APP更新模块未解决，将在后期修复----xiaobo 微信:liubo520lngw");
                systemAlter.setCancelable(false);
                systemAlter.setIcon(R.drawable.thumb32);
                systemAlter.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "xiaobo编写", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
                systemAlter.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void setAudioImage(@NonNull SeekBar seekBarSystem,
                              @NonNull SeekBar seekBarMusic, @NonNull SeekBar seekBarCall, @NonNull AudioManager audioManage) {
        int seekBarSystemLong = seekBarSystem.getProgress();
        int seekBarCallLong = seekBarCall.getProgress();
        int seekBarMusicLong = seekBarMusic.getProgress();
        if (audioManage.getStreamVolume(1) == audioManage.getStreamMinVolume(1)) {
            seekBarSystem.setThumb(getResources().getDrawable(R.drawable.cross));
        } else if (audioManage.getStreamVolume(1) < (audioManage.getStreamMaxVolume(1)) / 3) {
            seekBarSystem.setThumb(getResources().getDrawable(R.drawable.thumb));
        } else if (audioManage.getStreamVolume(1) == audioManage.getStreamMaxVolume(1)) {
            seekBarSystem.setThumb(getResources().getDrawable(R.drawable.high));
        } else if (audioManage.getStreamVolume(1) > (audioManage.getStreamMaxVolume(1))) {
            seekBarSystem.setThumb(getResources().getDrawable(R.drawable.middle));
        }
        if (audioManage.getStreamVolume(0) == audioManage.getStreamMinVolume(0)) {
            seekBarCall.setThumb(getResources().getDrawable(R.drawable.cross));
        } else if (audioManage.getStreamVolume(0) < (audioManage.getStreamMaxVolume(0)) / 3) {
            seekBarCall.setThumb(getResources().getDrawable(R.drawable.thumb));
        } else if (audioManage.getStreamVolume(0) == audioManage.getStreamMaxVolume(0)) {
            seekBarCall.setThumb(getResources().getDrawable(R.drawable.high));
        } else if (audioManage.getStreamVolume(0) > (audioManage.getStreamMaxVolume(0))) {
            seekBarCall.setThumb(getResources().getDrawable(R.drawable.middle));
        }
        if (audioManage.getStreamVolume(3) == audioManage.getStreamMinVolume(3)) {
            seekBarMusic.setThumb(getResources().getDrawable(R.drawable.cross));
        } else if (audioManage.getStreamVolume(3) < (audioManage.getStreamMaxVolume(3)) / 3) {
            seekBarMusic.setThumb(getResources().getDrawable(R.drawable.thumb));
        } else if (audioManage.getStreamVolume(3) == audioManage.getStreamMaxVolume(3)) {
            seekBarMusic.setThumb(getResources().getDrawable(R.drawable.high));
        } else if (audioManage.getStreamVolume(3) > (audioManage.getStreamMaxVolume(3))) {
            seekBarMusic.setThumb(getResources().getDrawable(R.drawable.middle));
        }
    }

    public void setAudioManagerKill(@NonNull Button systemKill, @NonNull Button callKill, @NonNull Button audioKill, @NonNull AudioManager audioManager) {
        systemKill.setOnClickListener(new View.OnClickListener() {
            final int systemLoad = audioManager.getStreamVolume(1);

            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            audioManager.setStreamVolume(1, systemLoad
                                    , AudioManager.FLAG_SHOW_UI);
                        }
                    }
                }).start();
            }
        });
        callKill.setOnClickListener(new View.OnClickListener() {
            final int callLoad = audioManager.getStreamVolume(0);

            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            int systemLoad = audioManager.getStreamVolume(1);
                            audioManager.setStreamVolume(0, callLoad
                                    , AudioManager.FLAG_SHOW_UI);
                        }
                    }
                }).start();
            }
        });
        audioKill.setOnClickListener(new View.OnClickListener() {
            final int audioLoad = audioManager.getStreamVolume(3);

            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            audioManager.setStreamVolume(3, audioLoad
                                    , AudioManager.FLAG_SHOW_UI);
                        }
                    }
                }).start();
            }
        });
    }

    public void getHash() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            int code = sign.hashCode();
//            Toast.makeText(MainActivity.this, String.valueOf(code), Toast.LENGTH_SHORT).show();
            if (code != 1406639594) {
                Toast.makeText(MainActivity.this, R.string.appAlert, Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else {
//                Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
            }
            Log.e("error", String.valueOf(code));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}