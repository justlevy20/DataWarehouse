package au.com.jlsystems.datawarehouse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Justin Levy on 26/03/14.
 */
public class RenameActivity extends Activity {
    public int index1;
    public int index2;
    private String packageName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = this.getApplication().getPackageName();
        setContentView(R.layout.rename);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(packageName + ".EXTRA_MESSAGE_DATA");
        Workarea wa = bundle.getParcelable("WORKAREA");
        String appName = getString(R.string.app_name);
        setTitle(getString(R.string.app_title, appName, wa.getName()));
        String oldName = bundle.getString("OLDNAME");
        ((TextView) findViewById(R.id.etOldName)).setText(oldName);
        ((TextView) findViewById(R.id.etNewName)).setText(oldName);
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