package au.com.jlsystems.datawarehouse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Justin Levy on 10/04/14.
 */
public class NewNameActivity extends Activity {
    private String packageName;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = this.getApplication().getPackageName();
        setContentView(R.layout.newname);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(packageName + ".EXTRA_MESSAGE_DATA");
        Workarea wa = bundle.getParcelable("WORKAREA");
        if(wa != null){
            String appName = getString(R.string.app_name);
            setTitle(getString(R.string.app_title, appName, wa.getName()));
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