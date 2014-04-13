package au.com.jlsystems.datawarehouse;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Justin Levy on 23/03/14.
 */
public class Workarea implements Parcelable{

    private long id;
    private String name;
    private String indexName;

    public Workarea(long id, String name, String indexName){
        this.id = id;
        this.name = name;
        this.indexName = indexName;
    }

    public Workarea(Parcel source){
        this.id = source.readLong();
        this.name = source.readString();
        this.indexName = source.readString();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIndexName() {
        return indexName;
    }

    @Override
    public String toString() {
        return String.format("[%d, %s, %s]",id , name, indexName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(indexName);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
          public Workarea createFromParcel(Parcel source) {
                return new Workarea(source);
          }
          public Workarea[] newArray(int size) {
                return new Workarea[size];
          }
    };
}
