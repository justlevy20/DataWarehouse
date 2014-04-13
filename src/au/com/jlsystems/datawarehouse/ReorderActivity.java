package au.com.jlsystems.datawarehouse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Justin Levy on 10/04/14.
 */
public class ReorderActivity extends Activity {
    private String packageName;
    private ArrayList<EditText> editTexts;
    private ArrayList<Field> fields;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = this.getApplication().getPackageName();
        setContentView(R.layout.reorderfields);
        Intent intent = getIntent();
        TableLayout layout = (TableLayout) findViewById(R.id.reorderLayout);

        Bundle bundle = intent.getBundleExtra(packageName + ".EXTRA_MESSAGE_DATA");
        Workarea wa = bundle.getParcelable("WORKAREA");
        fields = bundle.getParcelableArrayList("FIELDS");
        editTexts = new ArrayList<EditText>();
        String appName = getString(R.string.app_name);
        setTitle(getString(R.string.app_title, appName, wa.getName()));

        TableRow tr = new TableRow(this);
        TextView tv = new TextView(this);
        tv.setText(getString(R.string.column_name));
        tr.addView(tv);
        tv = new TextView(this);
        tv.setText(getString(R.string.column_order, fields.size()));
        tr.addView(tv);
        layout.addView(tr);
        for (Field f : fields) {
            tr = new TableRow(this);
            tv = new TextView(this);
            tv.setText(f.getName());
            EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            if (f.getOrder() > 0) {
                et.setText(String.valueOf(f.getOrder()));
            }
            et.setHint(getString(R.string.hintEnterOrder));
            editTexts.add(et);
            tr.addView(tv);
            tr.addView(et);
            layout.addView(tr);
        }
    }

    public void Click(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                ArrayList<Field> fields = new ArrayList<Field>();
                if (editTexts.size() > 0) {
                    for (int i = 0, len = editTexts.size(); i < len; i++) {
                        EditText et = editTexts.get(i);
                        if (et.getText().length() > 0) {
                            Field f = this.fields.get(i);
                            Field field = new Field(f.getId(), f.getWorkareaId(), Long.parseLong(et.getText().toString()), f.getName());
                            fields.add(field);
                        }
                    }

                }
                Intent intent = getIntent();
                Bundle bundle = intent.getBundleExtra(packageName + ".EXTRA_MESSAGE_DATA");
                bundle.putParcelableArrayList("FIELDS", fields);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }
}