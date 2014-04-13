package au.com.jlsystems.datawarehouse;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//import android.util.Log;

/**
 * Created by Justin Levy on 23/03/14.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "workarea.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    public static final String TABLE_WORKAREA = "workarea";
    public static final String TABLE_WORKAREA_ID = "_ID";
    public static final String TABLE_WORKAREA_NAME = "name";
    public static final String TABLE_WORKAREA_INDEX = "index_name";
    public static final String[] TABLE_WORKAREA_ALL_COLUMNS = {
            TABLE_WORKAREA_ID,
            TABLE_WORKAREA_NAME,
            TABLE_WORKAREA_INDEX};
    private static final String[] TABLE_WORKAREA_ALL_COLUMNS_TYPES = {
            "INTEGER PRIMARY KEY AUTOINCREMENT",
            "TEXT NOT NULL",
            "TEXT NOT NULL"};
    private static final String[] TABLE_WORKAREA_UNIQUE_INDEX = {TABLE_WORKAREA_NAME};
    public static final String TABLE_WORKAREA_SORT_ORDER = TABLE_WORKAREA_NAME + " ASC";

    public static final String TABLE_FIELD = "fields";
    public static final String TABLE_FIELD_ID = "_ID";
    public static final String TABLE_FIELD_WORKAREA_ID = "workarea_id";
    public static final String TABLE_FIELD_ORDER = "sort_order";
    public static final String TABLE_FIELD_NAME = "name";
    public static final String[] TABLE_FIELD_ALL_COLUMNS = {
            TABLE_FIELD_ID,
            TABLE_FIELD_WORKAREA_ID,
            TABLE_FIELD_ORDER,
            TABLE_FIELD_NAME};
    private static final String[] TABLE_FIELD_ALL_COLUMNS_TYPES = {
            "INTEGER PRIMARY KEY AUTOINCREMENT",
            "INTEGER NOT NULL REFERENCES " + columnReference(TABLE_WORKAREA, TABLE_WORKAREA_ID),
            "INTEGER NOT NULL DEFAULT 0",
            "TEXT NOT NULL"};
    private static final String[] TABLE_FIELD_UNIQUE_INDEX = {
            TABLE_FIELD_WORKAREA_ID,
            TABLE_FIELD_NAME};
    public static final String TABLE_FIELD_SORT_ORDER = TABLE_FIELD_ORDER + " ASC";

    public static final String TABLE_DATA = "data";
    public static final String TABLE_DATA_ID = "_ID";
    public static final String TABLE_DATA_FIELD_ID = "field_id";
    public static final String TABLE_DATA_DATE = "date";
    public static final String TABLE_DATA_INDEX = "item";
    public static final String TABLE_DATA_USER = "user";
    public static final String TABLE_DATA_DATA = "data";
    public static final String[] TABLE_DATA_ALL_COLUMNS = {
            TABLE_DATA_ID,
            TABLE_DATA_FIELD_ID,
            TABLE_DATA_DATE,
            TABLE_DATA_INDEX,
            TABLE_DATA_USER,
            TABLE_DATA_DATA};
    private static final String[] TABLE_DATA_ALL_COLUMNS_TYPES = {
            "INTEGER PRIMARY KEY AUTOINCREMENT",
            "INTEGER NOT NULL REFERENCES " + columnReference(TABLE_FIELD, TABLE_FIELD_ID),
            "TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP",
            "TEXT NOT NULL",
            "TEXT",
            "TEXT"};
    private static final String[] TABLE_DATA_UNIQUE_INDEX = {
            TABLE_DATA_DATE,
            TABLE_DATA_FIELD_ID,
            TABLE_DATA_INDEX,
            TABLE_DATA_USER};
    public static final String TABLE_DATA_SORT_ORDER = TABLE_DATA_DATE + " DESC";

    // sql SELECT statements
    public static final String SELECT_WORKAREAS = join(" ", new String[]{
            "SELECT", join(", ", TABLE_WORKAREA_ALL_COLUMNS),
            "FROM", TABLE_WORKAREA,
            "ORDER BY", TABLE_WORKAREA_SORT_ORDER});
    public static final String SELECT_WORKAREA_FIELDS = join(" ", new String[]{
            "SELECT", join(", ", TABLE_FIELD_ALL_COLUMNS),
            "FROM", TABLE_FIELD,
            "WHERE", TABLE_FIELD_WORKAREA_ID, "= ?",
            "ORDER BY", TABLE_FIELD_SORT_ORDER});
    public static final String SELECT_INDICES = join(" ", new String[]{
            "SELECT DISTINCT", TABLE_DATA_INDEX,
            "FROM", TABLE_DATA,
            "INNER JOIN", TABLE_FIELD,
            "ON", columnFullName(TABLE_DATA, TABLE_DATA_FIELD_ID), "=", columnFullName(TABLE_FIELD, TABLE_FIELD_ID),
            "WHERE", TABLE_FIELD_WORKAREA_ID, "= ?",
            "ORDER BY", TABLE_DATA_INDEX});
    public static final String SELECT_DATA = join(" ", new String[]{
            "SELECT", TABLE_DATA_DATA,
            "FROM", TABLE_DATA,
            "WHERE", TABLE_DATA_FIELD_ID, "= ?", "AND", TABLE_DATA_INDEX, "= ?",
            "ORDER BY", TABLE_DATA_SORT_ORDER,
            "LIMIT 1"});
    public static final String SELECT_DATA_HISTORY = join(" ", new String[]{
            "SELECT", join(", ", TABLE_DATA_ALL_COLUMNS),
            "FROM", TABLE_DATA,
            "WHERE", TABLE_DATA_FIELD_ID, "= ?", "AND", TABLE_DATA_INDEX, "= ?",
            "ORDER BY", TABLE_DATA_SORT_ORDER});

    // sql INSERT statements
//    public static final String INSERT_WORKAREA = join(" ", new String[]{
//            "INSERT INTO", columnReference(TABLE_WORKAREA, TABLE_WORKAREA_ALL_COLUMNS),
//            "VALUES (NULL, ?, ?)"});
//    public static final String INSERT_FIELD = join(" ", new String[]{
//            "INSERT INTO", columnReference(TABLE_FIELD, TABLE_FIELD_ALL_COLUMNS),
//            "VALUES (NULL, ?)"});
//    public static final String INSERT_JOIN = join(" ", new String[]{
//            "INSERT INTO", columnReference(TABLE_FIELD, TABLE_FIELD_ALL_COLUMNS),
//            "VALUES (NULL, ?, ?, ?)"});
//    public static final String INSERT_DATA = join(" ", new String[]{
//            "INSERT INTO", columnReference(TABLE_DATA, TABLE_DATA_ALL_COLUMNS),
//            "VALUES (NULL, NULL, ?, ?, ?, ?)"});

    // sql UPDATE statements
//    public static final String UPDATE_WORKAREA_NAME = join(" ", new String[]{
//            "UDPATE", TABLE_WORKAREA,
//            "SET", TABLE_WORKAREA_NAME, "= ?",
//            "WHERE", TABLE_WORKAREA_ID, "= ?"});
//    public static final String UPDATE_WORKAREA_INDEX = join(" ", new String[]{
//            "UDPATE", TABLE_WORKAREA,
//            "SET", TABLE_WORKAREA_INDEX, "= ?",
//            "WHERE", TABLE_WORKAREA_ID, "= ?"});
//    public static final String UPDATE_FIELD = join(" ", new String[]{
//            "UDPATE", TABLE_FIELD,
//            "SET", TABLE_FIELD_NAME, "= ?",
//            "WHERE", TABLE_FIELD_ID, "= ?"});
//    public static final String UPDATE_FIELD_ORDER = join(" ", new String[]{
//            "UDPATE", TABLE_FIELD,
//            "SET", TABLE_FIELD_ORDER, "= ?",
//            "WHERE", TABLE_FIELD_ID, "= ?"});

    // sql UPDATE WHERE clauses
    public static final String UPDATE_WORKAREA_WHERE = join(" ", new String[]{TABLE_WORKAREA_ID, "= ?"});
    public static final String UPDATE_FIELD_WHERE = join(" ", new String[]{TABLE_FIELD_ID, "= ?"});
    public static final String UPDATE_INDEX_WHERE = join(" ", new String[]{
            TABLE_DATA_INDEX, "= ?",
            "AND", TABLE_DATA_FIELD_ID, "IN (SELECT", TABLE_FIELD_ID, "FROM", TABLE_FIELD, "WHERE", TABLE_FIELD_WORKAREA_ID, "= ?)"});


    // Database creation sql statement
    private static final String[] DATABASE_CREATE = {
            assembleTableCreate(
            TABLE_WORKAREA,
            TABLE_WORKAREA_ALL_COLUMNS,
            TABLE_WORKAREA_ALL_COLUMNS_TYPES,
            TABLE_WORKAREA_UNIQUE_INDEX),
            assembleTableCreate(
                    TABLE_FIELD,
                    TABLE_FIELD_ALL_COLUMNS,
                    TABLE_FIELD_ALL_COLUMNS_TYPES,
                    TABLE_FIELD_UNIQUE_INDEX),
            assembleTableCreate(
            TABLE_DATA,
            TABLE_DATA_ALL_COLUMNS,
            TABLE_DATA_ALL_COLUMNS_TYPES,
            TABLE_DATA_UNIQUE_INDEX)};

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        for(String sql : DATABASE_CREATE){
            sqLiteDatabase.execSQL(sql);
        }
    }

    private static String columnReference(String table, String column){
        return String.format("%s(%s)", table, column);
    }

    private static String columnFullName(String table, String column){
        return String.format("%s.%s", table, column);
    }

    private static String columnReference(String table, String[] columns){
        return String.format("%s(%s)", table, join(", ", columns));
    }

    private static String assembleTableCreate(String tableName, String[] columnNames, String[] columnTypes, String[] uniqueIndex) {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(tableName);
        sb.append(" (");
        int columnNamesLength = columnNames.length;
        if (columnNamesLength > 0) {
            sb.append(columnNames[0]);
            sb.append(" ");
            sb.append(columnTypes[0]);
        }
        for (int i = 1; i < columnNames.length; i++) {
            sb.append(", ");
            sb.append(columnNames[i]);
            sb.append(" ");
            sb.append(columnTypes[i]);
        }
        if(uniqueIndex.length > 0){
            sb.append(", ");
            sb.append("UNIQUE (");
            sb.append(join(", ", uniqueIndex));
            sb.append(")");
        }
        sb.append(");");
        return sb.toString();
    }

    private static String join(String delimiter, String[] strings){
        if(strings.length == 0) return "";
        StringBuilder sb = new StringBuilder(strings[0]);
        for(int i = 1, len = strings.length; i < len; i++){
            sb.append(delimiter);
            sb.append(strings[i]);
        }
        return sb.toString();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
//        Log.w(MySQLiteHelper.class.getName(),
//                "Upgrading database from version " + oldVersion + " to "
//                        + newVersion + ", which will destroy all old data."
//        );
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKAREA);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_FIELD);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(sqLiteDatabase);
    }
}
