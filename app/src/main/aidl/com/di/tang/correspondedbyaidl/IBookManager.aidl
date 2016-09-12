// IBookManager.aidl
package com.di.tang.correspondedbyaidl;

// Declare any non-default types here with import statements
import com.di.tang.correspondedbyaidl.Book;
import com.di.tang.correspondedbyaidl.IOnNewBookArrivedListener;

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
  List<Book>getBookList();
   void addBook(in Book book);
   void registerListener(IOnNewBookArrivedListener listener);
   void unregisterListener(IOnNewBookArrivedListener listener);
}
