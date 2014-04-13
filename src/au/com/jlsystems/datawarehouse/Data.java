package au.com.jlsystems.datawarehouse;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Justin Levy on 3/04/14.
 */
public class Data implements Parcelable{
    private long id;
    private long fieldId;
    private String date;
    private String index;
    private String user;
    private String data;

    public Data(long id, long fieldId, String date, String index, String user, String data) {
        this.id = id;
        this.fieldId = fieldId;
        this.date = date;
        this.index = index;
        this.user = user;
        this.data = data;
    }

    public Data(Parcel source){
        this.id = source.readLong();
        this.fieldId = source.readLong();
        this.date = source.readString();
        this.index = source.readString();
        this.user = source.readString();
        this.data = source.readString();
    }

    public long getId() {
        return id;
    }

    public long getFieldId() {
        return fieldId;
    }

    public String getDate() {
        return date;
    }

    public String getIndex() {
        return index;
    }

    public String getUser() {
        return user;
    }

    public String getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(fieldId);
        dest.writeString(date);
        dest.writeString(index);
        dest.writeString(user);
        dest.writeString(data);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
          public Data createFromParcel(Parcel source) {
                return new Data(source);
          }
          public Data[] newArray(int size) {
                return new Data[size];
          }
    };
}
