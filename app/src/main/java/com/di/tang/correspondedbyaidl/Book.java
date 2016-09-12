package com.di.tang.correspondedbyaidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tangdi on 2016/9/9.
 */
public class Book implements Parcelable {
    private int bookId;
    private String bookName;
    private boolean isMale;


    protected Book(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
        isMale = in.readByte() != 0;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(bookId);
        parcel.writeString(bookName);
        parcel.writeByte((byte) (isMale ? 1 : 0));
    }

    public Book(int id, String name){
        bookId = id;
        bookName = name;
        isMale = false;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(bookId))
                .append(bookName)
                .append(String.valueOf(isMale));
        return stringBuilder.toString();
    }
}
