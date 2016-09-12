package com.di.tang.correspondedbyaidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int MESSAGE_NEW_ARRIVED = 0x123;

    private IBookManager mRemoteBookManager;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case MESSAGE_NEW_ARRIVED:
                    Log.d(TAG, "handleMessage: " + msg.obj);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private IOnNewBookArrivedListener listener = new IOnNewBookArrivedListener.Stub(){

        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_ARRIVED, newBook).sendToTarget();
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
            try{
                mRemoteBookManager = bookManager;
                List<Book> list = bookManager.getBookList();
                Log.d(TAG, "onServiceConnected: " + "List type: "
                        + list.getClass().getCanonicalName());
                Log.d(TAG, "onServiceConnected: " + list.toString());
                bookManager.registerListener(listener);
            }catch (RemoteException e){
                Log.e(TAG, "onServiceConnected: " + e.toString() );
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, BookManagerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mRemoteBookManager != null && mRemoteBookManager.asBinder().isBinderAlive()){
            try{
                Log.d(TAG, "onDestroy: unregister listener " + listener);
                mRemoteBookManager.unregisterListener(listener);
            }catch(RemoteException e){
                Log.d(TAG, "onDestroy: unregisterListener SUCCESS");
            }
        }
        unbindService(serviceConnection);
    }


}
