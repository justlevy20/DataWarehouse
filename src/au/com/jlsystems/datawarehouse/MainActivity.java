package au.com.jlsystems.datawarehouse;

/**
 * Created by Justin Levy on 23/03/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.Attributes;

//import android.util.Log;

public class MainActivity extends Activity {

    private String user;
    private long currentWorkareaId;
    private WorkareaDataSource dataSource;
    private Workarea currentWorkarea;
    private ArrayList<Workarea> workareas;
    private ArrayList<Field> fields;
    private ArrayList<String> indices;

    private int[] colWidths = {};
    private int[] rowHeights = {};
    private MyTextView[][] data = {};
    private String packageName;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = this.getApplication().getPackageName();

        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = app_preferences.getString("UserName", "Unknown User");
        if (user.equals("Unknown User")) { //TODO: from settings
            user = "Justin";
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putString("UserName", user);
            editor.commit(); // Very important
        }
        currentWorkareaId = app_preferences.getLong("CurrentWorkarea", 1);
        dataSource = new WorkareaDataSource(this);

        loadDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        dm.setToDefaults();
        TextView tv = new TextView(this);
        Log.d(this.getClass().getName(), String.format("Theme %f %f", dm.xdpi, tv.getTextScaleX()));
//        Log.w(this.getClass().getName(), "End of onCreate");

    }

    private void loadDisplay() {
        loadWorkarea();

        Resources res = getResources();
        int colourHeading = res.getColor(R.color.colourHeading);
        int colourIndex = res.getColor(R.color.colourIndex);
        int colourEven = res.getColor(R.color.colourEven);
        int colourOdd = res.getColor(R.color.colourOdd);

        setContentView(R.layout.main);
        if (currentWorkarea == null) return;
        String appName = getString(R.string.app_name);
        setTitle(getString(R.string.app_title, appName, currentWorkarea.getName()));
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layoutMain);

        ViewTreeObserver vto = layout.getViewTreeObserver();
        assert vto != null;
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //            @Override
            public void onGlobalLayout() {
                for (int i = 0, rowslength = rowHeights.length; i < rowslength; i++) {
                    if (rowHeights[i] < data[i][0].getChangeTextHeightTo()) {
                        rowHeights[i] = data[i][0].getChangeTextHeightTo();
                        setRowHeight(i);
                    }
                }
                for (int j = 0, colslength = colWidths.length; j < colslength; j++) {
                    if (colWidths[j] < data[0][j].getChangeTextWidthTo()) {
                        colWidths[j] = data[0][j].getChangeTextWidthTo();
                        setColWidth(j);
                    }
                }
            }
        });
//        data[0][0].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        data[0][0].setBackgroundColor(colourIndex);

        View.OnClickListener clicklistener = new View.OnClickListener() {

            //            @Override
            public void onClick(View v) {
                Click(v);
            }
        };

        data[0][0].setOnClickListener(clicklistener);

        //setup left column with row labels
        LinearLayout llLeftCol = new LinearLayout(this);
        llLeftCol.setOrientation(LinearLayout.VERTICAL);
        llLeftCol.addView(data[0][0]);

        LinkedScrollView lsvLeftCol = new LinkedScrollView(this);
        lsvLeftCol.setVerticalScrollBarEnabled(false); //this one will look wrong

        MyColumnLayout tlLeftCol = new MyColumnLayout(this);
        tlLeftCol.columnHeading = data[0][0];
        data[0][0].myColumnLayout = tlLeftCol;
        for (int i = 1, rowsLength = data.length; i < rowsLength; i++) {
            MyTextView row = data[i][0];
            row.setMinWidth(colWidths[0]);
            row.setMinHeight(rowHeights[i]);
            row.setBackgroundColor(colourHeading);
            row.setOnClickListener(clicklistener);
            tlLeftCol.addView(row);
        }
        lsvLeftCol.addView(tlLeftCol);
        llLeftCol.addView(lsvLeftCol);
        layout.addView(llLeftCol);

        //add the main horizontal scroll
        HorizontalScrollView hsvMainContent = new HorizontalScrollView(this);
        hsvMainContent.setHorizontalScrollBarEnabled(false); //you could probably leave this one enabled if you want

        LinearLayout llMainContent = new LinearLayout(this); //Scroll view needs a single child
        llMainContent.setOrientation(LinearLayout.VERTICAL);

        //add the headings
        LinearLayout tlColHeadings = new LinearLayout(this);
        tlColHeadings.setOrientation(LinearLayout.HORIZONTAL);
        for (int j = 1, colslength = data[0].length; j < colslength; j++) {
            MyTextView tv = data[0][j];
            tv.setMinWidth(colWidths[j]);
            tv.setMinHeight(rowHeights[0]);
            tv.setBackgroundColor(colourHeading);
            tv.setGravity(Gravity.RIGHT);
            tv.setOnClickListener(clicklistener);
            tlColHeadings.addView(tv);
        }

        llMainContent.addView(tlColHeadings);

        //now lets add the main content
        LinkedScrollView lsvMainVertical = new LinkedScrollView(this);
        lsvMainVertical.setVerticalScrollBarEnabled(false); //this will not be visible most of the time anyway

        LinearLayout tlMainContent = new LinearLayout(this);
        tlMainContent.setOrientation(LinearLayout.HORIZONTAL);

        for (int j = 1, colslength = data[0].length; j < colslength; j++) {
            MyColumnLayout tr = new MyColumnLayout(this);
            tr.columnHeading = data[0][j];
            data[0][j].myColumnLayout = tr;
            for (int i = 1, rowslength = data.length; i < rowslength; i++) {
                MyTextView tv = data[i][j];
                tv.setMinWidth(colWidths[j]);
                tv.setMinHeight(rowHeights[i]);
                tv.setId(R.id.tvBody);
                tv.setGravity(Gravity.RIGHT);
                tv.setBackgroundColor((i & 1) == 0 ? colourEven : colourOdd);
                tv.setOnClickListener(clicklistener);
                tr.addView(tv);
            }
            tlMainContent.addView(tr);
        }

        lsvMainVertical.addView(tlMainContent);
        llMainContent.addView(lsvMainVertical);
        hsvMainContent.addView(llMainContent);
        layout.addView(hsvMainContent);

        //the magic
        lsvMainVertical.others.add(lsvLeftCol);
        lsvLeftCol.others.add(lsvMainVertical);
    }

    private void loadWorkarea() {
        currentWorkarea = null;

        try {
            dataSource.open();
//            dataSource.loadDummyData(); //todo remove this

            workareas = dataSource.getAllWorkAreas();
            dataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (Workarea wa : workareas) {
            if (wa.getId() == currentWorkareaId) {
                currentWorkarea = wa;
                break;
            }
        }
        if (currentWorkarea == null) {
            if (workareas.size() > 0) {
                currentWorkarea = workareas.get(0);
                currentWorkareaId = currentWorkarea.getId();
            }
        }
        if (currentWorkarea != null) {
            Log.w(this.getClass().getName(), String.format("loadWorkArea %s", currentWorkarea));
            populateTable();
            SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = app_preferences.edit();
            editor.putLong("CurrentWorkarea", currentWorkareaId);
            editor.commit(); // Very important
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.my_activity_actions, menu);
        MenuItem miAdd = menu.add(Menu.NONE, R.id.action_add, Menu.NONE, R.string.action_add);
        miAdd.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        SubMenu miOpenWorkarea = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, R.string.action_open_workareas);
        SubMenu miEditWorkarea = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, R.string.action_edit_workareas);
        SubMenu miDataTransfer = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, R.string.action_data_transfer);
        MenuItem miSettings = menu.add(Menu.NONE, R.id.action_settings, Menu.NONE, R.string.action_settings);
        MenuItem mihelp = menu.add(Menu.NONE, R.id.action_help, Menu.NONE, R.string.action_help);
        mihelp.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        try {
            dataSource.open();
            List<Workarea> workareas = dataSource.getAllWorkAreas();
            for (Workarea wa : workareas) {
                MenuItem miWorkarea = miOpenWorkarea.add((int) wa.getId(), R.id.action_show_workarea, Menu.NONE, wa.getName());
                miWorkarea.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            }
            dataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        MenuItem miNewField = miEditWorkarea.add(Menu.NONE, R.id.action_new_field, Menu.NONE, R.string.action_new_field);
        miNewField.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        MenuItem miReorderFields = miEditWorkarea.add(Menu.NONE, R.id.action_reorder_fields, Menu.NONE, R.string.action_reorder_fields);
        miReorderFields.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        MenuItem miRenameWorkarea = miEditWorkarea.add(Menu.NONE, R.id.action_rename_workarea, Menu.NONE, R.string.action_rename_workarea);
        miRenameWorkarea.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        MenuItem miNewWorkarea = miEditWorkarea.add(Menu.NONE, R.id.action_new_workarea, Menu.NONE, R.string.action_new_workarea);
        miNewWorkarea.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        MenuItem miOpenFile = miDataTransfer.add(Menu.NONE, R.id.action_open_file, Menu.NONE, R.string.action_open_file);
        miOpenFile.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        MenuItem miSaveAsFile = miDataTransfer.add(Menu.NONE, R.id.action_save_as_file, Menu.NONE, R.string.action_save_as_file);
        miOpenFile.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        MenuItem miImportFile = miDataTransfer.add(Menu.NONE, R.id.action_import_file, Menu.NONE, R.string.action_import_file);
        miOpenFile.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                openAddRow();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_help:
                openHelp();
                return true;
            case R.id.action_open_file:
                openFile();
                return true;
            case R.id.action_save_as_file:
                openSaveAsFile();
                return true;
            case R.id.action_import_file:
                openImportFile();
                return true;
            case R.id.action_rename_workarea:
                openRenameWorkarea();
                return true;
            case R.id.action_show_workarea:
                openShowWorkArea(item.getGroupId());
                return true;
            case R.id.action_new_workarea:
                openNewWorkarea();
                return true;
            case R.id.action_new_field:
                openNewField();
                return true;
            case R.id.action_reorder_fields:
                openReorderFields();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openReorderFields() {
//        Log.w(this.getClass().getName(), String.format("openReorderFields"));
        Bundle bundle = new Bundle();
        bundle.putParcelable("WORKAREA", currentWorkarea);
        bundle.putParcelableArrayList("FIELDS", fields);
        Intent intent = new Intent(this, ReorderActivity.class);
        intent.putExtra(packageName + ".EXTRA_MESSAGE_DATA", bundle);
        startActivityForResult(intent, R.id.ACTIVITY_REORDER_FIELDS);
    }

    private void openAddRow() {
//        Log.w(this.getClass().getName(), String.format("openAddRow"));
        Bundle bundle = new Bundle();
        bundle.putParcelable("WORKAREA", currentWorkarea);
        bundle.putParcelableArrayList("FIELDS", fields);
        Intent intent = new Intent(this, AddDataActivity.class);
        intent.putExtra(packageName + ".EXTRA_MESSAGE_DATA", bundle);
        startActivityForResult(intent, R.id.ACTIVITY_ADD_DATA);
    }

    private void openNewField() {
//        Log.w(this.getClass().getName(), String.format("openNewField"));
        Bundle bundle = new Bundle();
        bundle.putParcelable("WORKAREA", currentWorkarea);
        Intent intent = new Intent(this, NewNameActivity.class);
        intent.putExtra(packageName + ".EXTRA_MESSAGE_DATA", bundle);
        startActivityForResult(intent, R.id.ACTIVITY_NEW_FIELD);
    }

    private void openNewWorkarea() {
//        Log.w(this.getClass().getName(), String.format("openNewWorkarea"));
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this, NewNameActivity.class);
        intent.putExtra(packageName + ".EXTRA_MESSAGE_DATA", bundle);
        startActivityForResult(intent, R.id.ACTIVITY_NEW_WORKAREA);
    }

    private void openShowWorkArea(long id) {
        currentWorkareaId = id;
        loadDisplay();
    }

    private void openRenameData() {
    }

    private void openRenameIndex() {
        // TODO complete code
    }

    private void openRenameWorkarea() {
        // TODO complete code
//        Log.w(this.getClass().getName(), "openRenameWorkarea");
        Bundle bundle = new Bundle();
        bundle.putParcelable("WORKAREA", currentWorkarea);
        Intent intent = new Intent(this, NewNameActivity.class);
        intent.putExtra(packageName + ".EXTRA_MESSAGE_DATA", bundle);
        startActivityForResult(intent, R.id.ACTIVITY_RENAME_WORKAREA);
    }

    private void openImportFile() {
        // TODO complete code
//        Log.w(this.getClass().getName(), "openImportFile");
    }

    private void openSaveAsFile() {
        // TODO complete code
//        Log.w(this.getClass().getName(), "openSaveAsFile");
    }

    private void openFile() {
        // TODO complete code
//        Log.w(this.getClass().getName(), "openFile");
    }

    private void openHelp() {
        // TODO complete code
//        Log.w(this.getClass().getName(), "openHelp");
    }

    private void openSettings() {
        // TODO complete code
        Log.w(this.getClass().getName(), "openSettings");
        Bundle bundle = new Bundle();
        bundle.putParcelable("WORKAREA", currentWorkarea);
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(packageName + ".EXTRA_MESSAGE_DATA", bundle);
        startActivityForResult(intent, R.id.ACTIVITY_RENAME_WORKAREA);

    }

    private void setColWidth(int index) {
        int width = colWidths[index];
        for (int i = 0, rowslength = rowHeights.length; i < rowslength; i++) {
//            if (data[i][index].getMeasuredWidth() < width) {
            data[i][index].setMinWidth(width);
//            }
        }
    }

    private void setRowHeight(int index) {
        int height = rowHeights[index];
        for (int j = 0, colslength = colWidths.length; j < colslength; j++) {
//            if (data[index][j].getMeasuredHeight() < height) {
            data[index][j].setMinHeight(height);
//            }
        }
    }

    private void Click(View v) { //todo
//        Intent intent = new Intent(this, TestGrid.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("WORKAREA", currentWorkarea);
        bundle.putInt("INDEX1", ((MyTextView) v).getIndex1());
        bundle.putInt("INDEX2", ((MyTextView) v).getIndex2());
        bundle.putString("OLDNAME", ((MyTextView) v).getText().toString());
        Intent intent = new Intent(this, RenameActivity.class);
        intent.putExtra(packageName + ".EXTRA_MESSAGE_DATA", bundle);
        switch (v.getId()) {
            case R.id.tvIndex:
                startActivityForResult(intent, R.id.ACTIVITY_EDIT_INDEX);
                break;
            case R.id.tvRow:
                startActivityForResult(intent, R.id.ACTIVITY_EDIT_ROW);
                break;
            case R.id.tvCol:
                startActivityForResult(intent, R.id.ACTIVITY_EDIT_COL);
                break;
            case R.id.tvBody:
                intent.setClass(this, EditDataActivity.class);
                int index1 = ((MyTextView) v).getIndex1();
                int index2 = ((MyTextView) v).getIndex2();
                long fieldId = fields.get(index2 - 1).getId();
                String index = indices.get(index1 - 1);
//                Log.w(this.getClass().getName(), String.format("Click body %d %d %d %s", index1, index2, fieldId, index));
                try {
                    dataSource.open();
                    ArrayList<Data> history = dataSource.getHistory(fieldId, index);
//                    Log.w(this.getClass().getName(), String.format("Click body history size %d", history.size()));
                    bundle.putParcelableArrayList("HISTORY", history);
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, R.id.ACTIVITY_EDIT_BODY);
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }


    //    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) { //todo
        if (resultCode == Activity.RESULT_OK) {
            Bundle bundle = intent.getBundleExtra(packageName + ".EXTRA_MESSAGE_DATA");
//            Log.w(this.getClass().getName(), String.format("onActivityResult %s", bundle.getString("NEWNAME")));
            switch (requestCode) {
                case R.id.ACTIVITY_EDIT_INDEX:
                    editIndexName(bundle);
                    break;
                case R.id.ACTIVITY_EDIT_ROW:
                    editRowName(bundle);
                    break;
                case R.id.ACTIVITY_EDIT_COL:
                    editFieldName(bundle);
                    break;
                case R.id.ACTIVITY_EDIT_BODY:
                    addData(bundle);
                    break;
                case R.id.ACTIVITY_ADD_DATA:
                    addAllData(bundle);
                    break;
                case R.id.ACTIVITY_NEW_FIELD:
                    addNewField(bundle);
                    break;
                case R.id.ACTIVITY_NEW_WORKAREA:
                    addNewWorkarea(bundle);
                    break;
                case R.id.ACTIVITY_REORDER_FIELDS:
                    editFieldOrder(bundle);
                    break;
                case R.id.ACTIVITY_RENAME_WORKAREA:
                    editWorkarea(bundle);
                    break;
                default:
                    break;

            }
        }
    }

    private void editWorkarea(Bundle bundle) {
        if (bundle != null) {
            String newName = bundle.getString("NEWNAME");
            if (newName != null && !newName.isEmpty()) {
                try {
                    dataSource.open();
                    long insertId = dataSource.updateWorkarea(new Workarea(currentWorkareaId, newName, currentWorkarea.getIndexName()));
                    dataSource.close();
                    if (insertId > 0) {
                        String appName = getString(R.string.app_name);
                        setTitle(getString(R.string.app_title, appName, newName));
                        loadDisplay();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void editFieldOrder(Bundle bundle) {
        if (bundle != null) {
            ArrayList<Field> fields = bundle.getParcelableArrayList("FIELDS");
//            Log.w(this.getClass().getName(), String.format("editFieldOrder #fields: %d", fields.size()));
            if (fields != null && fields.size() > 0) {
                int numberAltered = 0;
                try {
                    dataSource.open();
                    for (Field f : fields) {
                        if (dataSource.updateField(f) > 0) {
                            numberAltered++;
                        }
                    }
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (numberAltered > 0) {
                    loadDisplay();
                }
            }
        }
    }

    private void addNewWorkarea(Bundle bundle) {
        if (bundle != null) {
            String newName = bundle.getString("NEWNAME");
            if (newName != null && !newName.isEmpty()) {
                try {
                    dataSource.open();
                    long insertId = dataSource.addWorkarea(new Workarea(0, newName, getString(R.string.defaultIndexName)));
                    dataSource.close();
                    if (insertId > 0) {
                        currentWorkareaId = insertId;
                        loadDisplay();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addNewField(Bundle bundle) {
        if (bundle != null) {
            String newName = bundle.getString("NEWNAME");
            try {
                dataSource.open();
                long insertId = dataSource.addField(new Field(0, currentWorkareaId, 0, newName));
                dataSource.close();
                if (insertId > 0) {
                    loadDisplay();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void addAllData(Bundle bundle) {
        if (bundle != null) {
            ArrayList<Data> datas = bundle.getParcelableArrayList("DATAS");
            if (datas != null && datas.size() > 0) {
                int numberAdded = 0;
                try {
                    dataSource.open();
                    for (Data d : datas) {
                        if (dataSource.addData(d.getFieldId(), d.getIndex(), user, d.getData()) > 0) {
                            numberAdded++;
                        }
                    }
                    dataSource.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (numberAdded > 0) {
                    loadDisplay();
                }
            }
        }
    }

    private void addData(Bundle bundle) {
        if (bundle != null) {
            int index1 = bundle.getInt("INDEX1", -1);
            int index2 = bundle.getInt("INDEX2", -1);
            String newName = bundle.getString("NEWNAME");
            if (index1 > 0 && index2 > 0 && newName != null && !newName.isEmpty()) {
                long fieldId = fields.get(index2 - 1).getId();
                String index = indices.get(index1 - 1);
                try {
                    dataSource.open();
                    long insertId = dataSource.addData(fieldId, index, user, newName);
                    dataSource.close();
                    if (insertId >= 0) {
                        MyTextView tv = data[index1][index2];
                        tv.setText(newName);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void editRowName(Bundle bundle) {
        if (bundle != null) {
            int index1 = bundle.getInt("INDEX1", -1);
            String newName = bundle.getString("NEWNAME");
            if (index1 > 0 && newName != null && !newName.isEmpty()) {
                try {
                    dataSource.open();
                    long rowsAffected = dataSource.updateRowIndex(currentWorkarea.getId(), (String) data[index1][0].getReference(), newName);
                    dataSource.close();
                    if (rowsAffected > 0) {
                        loadDisplay();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void editIndexName(Bundle bundle) {
        if (bundle != null) {
            String newName = bundle.getString("NEWNAME");
            if (newName != null && !newName.isEmpty()) {
                Workarea workarea = new Workarea(currentWorkarea.getId(), currentWorkarea.getName(), newName);
                try {
                    dataSource.open();
                    long rowsAffected = dataSource.updateWorkarea(workarea);
                    dataSource.close();
                    if (rowsAffected > 0) {
                        MyTextView tv = data[0][0];
                        tv.setText(workarea.getIndexName());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void editFieldName(Bundle bundle) {
        if (bundle != null) {
            int index2 = bundle.getInt("INDEX2", -1);
            String newName = bundle.getString("NEWNAME");
            if (index2 > 0 && newName != null && !newName.isEmpty()) {
                Field field = (Field) data[0][index2].getReference();
                try {
                    dataSource.open();
                    long rowsAffected = dataSource.updateField(
                            new Field(field.getId(), field.getWorkareaId(), field.getOrder(), newName)
                    );
                    dataSource.close();
                    if (rowsAffected > 0) {
                        loadDisplay();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();

                }
            }
        }
    }

    private void populateTable() {
        fields = null;
        indices = null;
        colWidths = null;
        rowHeights = null;
        data = null;
        try {
            dataSource.open();
            fields = dataSource.getAllFieldsForWorkarea(currentWorkareaId);
            indices = dataSource.getAllRowIndices(currentWorkareaId);
            Collections.sort(indices, NumberAwareStringComparator.INSTANCE);

            colWidths = new int[fields.size() + 1];
            rowHeights = new int[indices.size() + 1];
            data = new MyTextView[indices.size() + 1][fields.size() + 1];

            data[0][0] = new MyTextView(this, currentWorkarea.getIndexName());
            data[0][0].setReference(currentWorkarea);
            data[0][0].setId(R.id.tvIndex);
            data[0][0].colHeading = data[0][0];
            data[0][0].rowHeading = data[0][0];

            for (int j = 1, colslength = data[0].length; j < colslength; j++) {
                data[0][j] = new MyTextView(this, ((Field) fields.get(j - 1)).getName());
                data[0][j].setReference(fields.get(j - 1));
                data[0][j].setIndex2(j);
                data[0][j].setId(R.id.tvCol);
                data[0][j].colHeading = data[0][j];
                data[0][j].rowHeading = data[0][0];
                data[0][j - 1].rowNext = data[0][j];
            }

            for (int i = 1, rowslength = data.length; i < rowslength; i++) {
                data[i][0] = new MyTextView(this, (String) indices.get(i - 1));
                data[i][0].setReference(indices.get(i - 1));
                data[i][0].setIndex1(i);
                data[i][0].setId(R.id.tvRow);
                data[i][0].colHeading = data[0][0];
                data[i][0].rowHeading = data[i][0];
                data[i - 1][0].colNext = data[i][0];
                for (int j = 1, colslength = data[0].length; j < colslength; j++) {
                    data[i][j] = new MyTextView(this, dataSource.getData(((Field) fields.get(j - 1)).getId(), (String) indices.get(i - 1)));
                    data[i][j].setIndex1(i);
                    data[i][j].setIndex2(j);
                    data[i][j].setId(R.id.tvBody);
                    data[i][j].colHeading = data[0][j];
                    data[i][j].rowHeading = data[i][0];
                    data[i - 1][j].colNext = data[i][j];
                    data[i][j - 1].rowNext = data[i][j];
                }
            }
            dataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
