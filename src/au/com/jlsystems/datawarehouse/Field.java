package au.com.jlsystems.datawarehouse;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Justin Levy on 6/04/14.
 */
public class Field implements Parcelable {
    private long id;
    private long workareaId;
    private long order;
    private String name;

    public Field(long id, long workareaId, long order, String name) {
        this.id = id;
        this.workareaId = workareaId;
        this.order = order;
        this.name = name;
    }

    public Field(Parcel source){
        this.id = source.readLong();
        this.workareaId = source.readLong();
        this.order = source.readLong();
        this.name = source.readString();
    }

    public long getId() {
        return id;
    }

    public long getWorkareaId() {
        return workareaId;
    }

    public String getName() {
        return name;
    }

    public long getOrder() {
        return order;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(workareaId);
        dest.writeLong(order);
        dest.writeString(name);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
          public Field createFromParcel(Parcel source) {
                return new Field(source);
          }
          public Field[] newArray(int size) {
                return new Field[size];
          }
    };

    @Override
    public String toString(){
        return String.format("(%d, %d, %d, %s)", id, workareaId, order, name);
    }
}
