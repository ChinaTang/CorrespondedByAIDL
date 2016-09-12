package com.di.tang.correspondedbyaidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
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

    private CopyOnWriteArrayList<IOnNewBookArrivedListener> listeners
            = new CopyOnWriteArrayList<IOnNewBookArrivedListener>();


    private void onNewBookArriv(Book book) throws RemoteException {
        mBookList.add(book);
        for (int i = 0; i < listeners.size(); i++) {
            IOnNewBookArrivedListener ArrivedListener = listeners.get(i);
            ArrivedListener.onNewBookArrived(book);
        }
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
            if (!listeners.contains(listener)) {
                listeners.add(listener);
            } else {
                Log.w(TAG, "registerListener: " + "already exists");
            }
            Log.d(TAG, "registerListener: " + "listeners size " + "[" + listeners.size() + "]");
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            if (listeners.contains(listener)) {
                listeners.remove(listener);
                Log.d(TAG, "unregisterListener: " + "remove SUCCESS");
            } else {
                Log.w(TAG, "unregisterListener: " + "not found , cannot unregisterListener");
            }
            Log.d(TAG, "unregisterListener: " + "listeners size " + "[" + listeners.size() + "]");
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
