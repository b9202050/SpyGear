package ntu.embedded.spycar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import ntu.embedded.spycar.JoyStickView.OnJoystickMoveListener;

public class MainActivity extends ActionBarActivity implements OnJoystickMoveListener {
    private static final String TAG = "SpyGear";

    private static final int REQUEST_CONNECT = 0;
    private boolean mProcessMenu = false;

    public static final String TOPIC = "NTU_Embedded";
    public static final String TOPIC_STATUS = "NTU_Embedded";
    public static final int QOS = 0;
    public static final int TIMEOUT = 3;

    private static String sClientId = "SpyCarAndroid";
    private static MqttClient mMqttClient;

    private JoyStickView mJoyStickView;
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mJoyStickView = (JoyStickView) findViewById(R.id.joy_stick_view);
        mJoyStickView.setOnJoystickMoveListener(this, JoyStickView.DEFAULT_LOOP_INTERVAL);
        mJoyStickView.setEnabled(false);

        mWebview = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CONNECT) {
                String brokerIp = data.getStringExtra("brokerIp");
                String brokerPort = data.getStringExtra("brokerPort");
                String webcamIp = data.getStringExtra("webcamIp");

                processConnect(brokerIp, brokerPort);
                mJoyStickView.setEnabled(true);
                if (mWebview != null) {
                    mWebview.loadUrl("http://" + webcamIp + ":8080/javascript_simple.html");
                }
            }
        }
        mProcessMenu = false;
    }

    @Override
    public void onDestroy() {
        if (mMqttClient != null && mMqttClient.isConnected()) {
            try {
                mMqttClient.disconnect();
            }
            catch (MqttException me) {
                Log.d(getClass().getName(), me.toString());
            }
        }

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.connect_menu) {
            if (!mProcessMenu) {
                mProcessMenu = true;
                startActivityForResult(new Intent(this, ConnectActivity.class),
                        REQUEST_CONNECT);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void processConnect(String brokerIp, String brokerPort) {
        String broker = "tcp://" + brokerIp + ":" + brokerPort;

        try {
            sClientId = sClientId + System.currentTimeMillis();

            MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(true);
            mqttConnectOptions.setConnectionTimeout(TIMEOUT);

            mMqttClient = new MqttClient(broker, sClientId, new MemoryPersistence());
            mMqttClient.connect(mqttConnectOptions);
            mMqttClient.subscribe(TOPIC_STATUS);

            Toast.makeText(this, R.string.connected, Toast.LENGTH_LONG).show();
        }
        catch (MqttException me) {
            Toast.makeText(this, R.string.connect_failure, Toast.LENGTH_LONG).show();
        }
    }

    private void sendCommand(ControlType action) {
        String content = String.valueOf(action.getCode());

        MqttMessage message = new MqttMessage(content.getBytes());
        message.setQos(QOS);

        try {
            if (mMqttClient != null) {
                mMqttClient.publish(TOPIC, message);
            }
        }
        catch (MqttException me) {
            Log.d(getClass().getName(), me.toString());
        }
    }

    @Override
    public void onValueChanged(int angle, int power, int direction) {
        Log.d(TAG, "Power="+String.valueOf(power) + " direction="+direction);

        switch (direction) {
            case JoyStickView.FRONT:
                sendCommand(ControlType.FORWARD);
                break;

            case JoyStickView.RIGHT:
                sendCommand(ControlType.RIGHT);
                break;

            case JoyStickView.BOTTOM:
                sendCommand(ControlType.BACKWARD);
                break;

            case JoyStickView.LEFT:
                sendCommand(ControlType.LEFT);
                break;

            case JoyStickView.FRONT_RIGHT:
            case JoyStickView.RIGHT_BOTTOM:
            case JoyStickView.BOTTOM_LEFT:
            case JoyStickView.LEFT_FRONT:
                // are we support this ?
                break;

            default:
                sendCommand(ControlType.STOP);
                return;
        }
    }
}
