// IOnNewBookArrivedListener.aidl
package com.di.tang.correspondedbyaidl;

// Declare any non-default types here with import statements
import com.di.tang.correspondedbyaidl.Book;
interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onNewBookArrived(in Book newBook);
}
