package au.com.jlsystems.datawarehouse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
//import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Justin Levy on 23/03/14.
 */

public class WorkareaDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    public WorkareaDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addWorkarea(Workarea workarea) {
//        Log.w(this.getClass().getName(), String.format("addWorkarea %s", workarea));
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_WORKAREA_NAME, workarea.getName());
        values.put(MySQLiteHelper.TABLE_WORKAREA_INDEX, workarea.getIndexName());

        long insertId = -1;
        try {
            insertId = database.insert(MySQLiteHelper.TABLE_WORKAREA, null, values);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        return insertId;
    }

    public void deleteWorkarea(long workareaId) {
        database.delete(MySQLiteHelper.TABLE_WORKAREA,
                MySQLiteHelper.UPDATE_WORKAREA_WHERE,
                new String[]{String.valueOf(workareaId)});
    }

    public long updateWorkarea(Workarea workarea) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_WORKAREA_NAME, workarea.getName());
        values.put(MySQLiteHelper.TABLE_WORKAREA_INDEX, workarea.getIndexName());
        long rowsAffected = 0;
        try {
            rowsAffected = database.update(MySQLiteHelper.TABLE_WORKAREA, values,
                    MySQLiteHelper.UPDATE_WORKAREA_WHERE,
                    new String[]{String.valueOf(workarea.getId())});
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public long addField(Field field) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_FIELD_WORKAREA_ID, field.getWorkareaId());
        values.put(MySQLiteHelper.TABLE_FIELD_ORDER, field.getOrder());
        values.put(MySQLiteHelper.TABLE_FIELD_NAME, field.getName());

        long insertId = -1;
        try {
            insertId = database.insert(MySQLiteHelper.TABLE_FIELD, null, values);
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        return insertId;
    }

    public long updateField(Field field) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_FIELD_ORDER, field.getOrder());
        values.put(MySQLiteHelper.TABLE_FIELD_NAME, field.getName());
        long rowsAffected = 0;
        try {
            rowsAffected = database.update(MySQLiteHelper.TABLE_FIELD, values,
                    MySQLiteHelper.UPDATE_FIELD_WHERE,
                    new String[]{String.valueOf(field.getId())});
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public ArrayList<Workarea> getAllWorkAreas() {
        ArrayList<Workarea> workareas = new ArrayList<Workarea>();
        Cursor cursor = database.rawQuery(MySQLiteHelper.SELECT_WORKAREAS, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Workarea Workarea = new Workarea(cursor.getLong(0), cursor.getString(1), cursor.getString(2));
            workareas.add(Workarea);
            cursor.moveToNext();
        }
        cursor.close();
        return workareas;
    }

    public ArrayList<Field> getAllFieldsForWorkarea(long workareaId) {
        ArrayList<Field> fields = new ArrayList<Field>();
        Cursor cursor = database.rawQuery(MySQLiteHelper.SELECT_WORKAREA_FIELDS, new String[]{String.valueOf(workareaId)});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Field field = new Field(cursor.getLong(0), cursor.getLong(1), cursor.getLong(2), cursor.getString(3));
            fields.add(field);
            cursor.moveToNext();
        }
        cursor.close();
        return fields;
    }

    public ArrayList<String> getAllRowIndices(long workAreaId) {
        ArrayList<String> indices = new ArrayList<String>();
        Cursor cursor = database.rawQuery(MySQLiteHelper.SELECT_INDICES, new String[]{String.valueOf(workAreaId)});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String field = cursor.getString(0);
            indices.add(field);
            cursor.moveToNext();
        }
        cursor.close();
        return indices;
    }

    public long updateRowIndex(long workAreaId, String oldIndex, String newIndex){
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_DATA_INDEX, newIndex);
        long rowsAffected = 0;
        try {
            rowsAffected = database.update(MySQLiteHelper.TABLE_DATA, values,MySQLiteHelper.UPDATE_INDEX_WHERE,
                    new String[]{oldIndex, String.valueOf(workAreaId)});
        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
        }
        return rowsAffected;
    }

    public String getData(long joinId, String index) {
        String data = "";
        Cursor cursor = database.rawQuery(MySQLiteHelper.SELECT_DATA, new String[]{String.valueOf(joinId), index});
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            data = cursor.getString(0);
        }
        cursor.close();
        return data;
    }

    public long addData(long fieldId, String index, String user, String data) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_DATA_FIELD_ID, fieldId);
        values.put(MySQLiteHelper.TABLE_DATA_INDEX, index);
        values.put(MySQLiteHelper.TABLE_DATA_USER, user);
        values.put(MySQLiteHelper.TABLE_DATA_DATA, data);

        return database.insert(MySQLiteHelper.TABLE_DATA, null, values);
    }

    public ArrayList<Data> getHistory(long fieldId, String index) {
        ArrayList<Data> history = new ArrayList<Data>();
        Cursor cursor = database.rawQuery(MySQLiteHelper.SELECT_DATA_HISTORY, new String[]{String.valueOf(fieldId), index});
//        Log.w(this.getClass().getName(), String.format("getHistory %s %s", new String[]{String.valueOf(fieldId), index}));

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Data dataHistory = new Data(cursor.getLong(0), cursor.getLong(1),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            history.add(dataHistory);
            cursor.moveToNext();
        }
        cursor.close();
        return history;
    }

    public void loadDummyData() {
        Workarea wa1 = new Workarea(0, "Emba Dies", "Die#");
        Workarea wa2 = new Workarea(0, "Langston Dies", "Die#");
        Workarea wa3 = new Workarea(0, "Legend", "Symbol");
        wa1 = new Workarea(addWorkarea(wa1), wa1.getName(), wa1.getIndexName());
        wa2 = new Workarea(addWorkarea(wa2), wa2.getName(), wa2.getIndexName());
        wa3 = new Workarea(addWorkarea(wa3), wa3.getName(), wa3.getIndexName());

        Field f1 = new Field(0, wa1.getId(), 2, "L/E");
        Field f2 = new Field(0, wa1.getId(), 1, "1st");
        Field f3 = new Field(0, wa2.getId(), 1, "D/S Hopper");
        Field f4 = new Field(0, wa3.getId(), 1, "Meaning");
        f1 = new Field(addField(f1), f1.getWorkareaId(), f1.getOrder(), f1.getName());
        f2 = new Field(addField(f2), f2.getWorkareaId(), f2.getOrder(), f2.getName());
        f3 = new Field(addField(f3), f3.getWorkareaId(), f3.getOrder(), f3.getName());
        f4 = new Field(addField(f4), f4.getWorkareaId(), f4.getOrder(), f4.getName());

        addData(f1.getId(), "1", "Justin", "S");
        addData(f2.getId(), "1", "Justin", "B");
        addData(f1.getId(), "2", "Justin", "-");
        addData(f2.getId(), "2", "Justin", "B");
        addData(f1.getId(), "5", "Justin", "S");
        addData(f2.getId(), "5", "Justin", "B");
        addData(f3.getId(), "1090", "Justin", "755");
        addData(f4.getId(), "S", "Justin", "Small");
        addData(f4.getId(), "B", "Justin", "Big");
        addData(f4.getId(), "-", "Justin", "Same");
        addData(f4.getId(), "V", "Justin", "5 Panel");
    }
}
