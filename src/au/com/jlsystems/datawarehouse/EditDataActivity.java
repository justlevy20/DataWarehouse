package au.com.jlsystems.datawarehouse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by Justin Levy on 11/04/14.
 */
public class EditDataActivity extends Activity {
    private String packageName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = this.getApplication().getPackageName();
        setContentView(R.layout.editdata);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(packageName + ".EXTRA_MESSAGE_DATA");

        TextView tvIndexName = (TextView) findViewById(R.id.textViewIndexName);
        Workarea wa = bundle.getParcelable("WORKAREA");
        String appName = getString(R.string.app_name);
        setTitle(getString(R.string.app_title, appName, wa.getName()));
        tvIndexName.setText(wa.getIndexName());

        TableLayout layout = (TableLayout) findViewById(R.id.historyLayout);
        ArrayList<Data> history = bundle.getParcelableArrayList("HISTORY");
        for(Data d : history){
            TableRow tr = new TableRow(this);
            TextView tv = new TextView(this);
            tv.setText(d.getDate());
            tr.addView(tv);
            tv = new TextView(this);
            tv.setText(d.getIndex());
            tr.addView(tv);
            tv = new TextView(this);
            tv.setText(d.getUser());
            tr.addView(tv);
            tv = new TextView(this);
            tv.setText(d.getData());
            tr.addView(tv);
            layout.addView(tr);
        }
    }

    public void Click(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                Intent intent = getIntent();
                Bundle bundle = intent.getBundleExtra(packageName + ".EXTRA_MESSAGE_DATA");
                bundle.putString("NEWNAME", ((EditText) findViewById(R.id.etNewName)).getText().toString());
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