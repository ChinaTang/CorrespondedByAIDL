package com.di.tang.correspondedbyaidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by tangdi on 2016/9/12.
 */
public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";

    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean(false);

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();

    private RemoteCallbackList<IOnNewBookArrivedListener> listeners
            = new RemoteCallbackList<IOnNewBookArrivedListener>();


    private void onNewBookArriv(Book book) throws RemoteException {
        mBookList.add(book);
        final int N = listeners.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener ArrivedListener = listeners.getBroadcastItem(i);
            if (ArrivedListener != null) {
                ArrivedListener.onNewBookArrived(book);
            }
        }
        listeners.finishBroadcast();
    }

    private Runnable ServiceWorker = new Runnable() {
        @Override
        public void run() {
            while (!mIsServiceDestoryed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new Book#" + bookId);
                try {
                    onNewBookArriv(newBook);
                } catch (RemoteException e) {
                    Log.e(TAG, "run: " + e.toString());
                }
            }
        }
    };

    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {

            listeners.register(listener);
            Log.d(TAG, "registerListener: " + "listeners size " + "[" + "]");
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            listeners.unregister(listener);
            Log.d(TAG, "unregisterListener: " + "listeners size " + "[" + "]");
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Ios"));
        new Thread(ServiceWorker).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


}
