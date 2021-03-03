package org.techtown.networktest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)    //LOLLIPOP 버전 이상에서 동작
public class NetworkConnectionCheck extends ConnectivityManager.NetworkCallback {   // 네트워크 변경에 대한 알림에 사용되는 Callback Class

    private Context context;
    private NetworkRequest networkRequest;
    private ConnectivityManager connectivityManager;
    public static Thread timeThread = null;

    public NetworkConnectionCheck(Context context){
        this.context=context;
        networkRequest =
                new NetworkRequest.Builder()                                        // addTransportType : 주어진 전송 요구 사항을 빌더에 추가
                        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)   // TRANSPORT_CELLULAR : 이 네트워크가 셀룰러 전송을 사용함을 나타냅니다.
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)       // TRANSPORT_WIFI : 이 네트워크가 Wi-Fi 전송을 사용함을 나타냅니다.
                        .build();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE); // CONNECTIVITY_SERVICE : 네트워크 연결 관리 처리를 검색
    }

    public void register() { this.connectivityManager.registerNetworkCallback(networkRequest, this);}

    public void unregister() {
        this.connectivityManager.unregisterNetworkCallback(this);
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        // 네트워크가 연결되었을 때 할 동작
        Toast.makeText(this.context, "network available", Toast.LENGTH_SHORT).show();
        timeThread = new Thread(new timeThread());
        timeThread.start(); //쓰레드실행

    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);

        // 네트워크 연결이 끊겼을 때 할 동작
        Toast.makeText(this.context, "network lost", Toast.LENGTH_SHORT).show();

    }

    Handler handler = new Handler(Looper.myLooper()) { //실시간 날짜를 출력해주는 핸들러
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String dateTime = bundle.getString("dateTime");
            MainActivity.textView.setText(dateTime);
        }
    };


    public class timeThread implements Runnable {
        //타이머 쓰레드
        @Override
        public void run() {
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("dateTime", "안녕2");
            msg.setData(bundle);
            handler.sendMessage(msg);
        }
    }

}
