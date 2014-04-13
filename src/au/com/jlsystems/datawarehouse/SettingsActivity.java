package au.com.jlsystems.datawarehouse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by Justin Levy on 14/04/14.
 */
public class SettingsActivity extends Activity {
    private String packageName;
    private ArrayList<EditText> editTexts;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = this.getApplication().getPackageName();
        setContentView(R.layout.settings);
    }

    public void Click(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                ArrayList<Data> datas = new ArrayList<Data>();
                String index = editTexts.get(0).getText().toString();
                if(index.length() > 0){
                    for(int i = 1, len = editTexts.size(); i < len; i++){
                        EditText et = editTexts.get(i);
                        if(et.getText().length() > 0){
                            Data data = new Data(0, 0, "", index, "", et.getText().toString());
                            datas.add(data);
                        }
                    }

                }
                Intent intent = getIntent();
                Bundle bundle = intent.getBundleExtra(packageName + ".EXTRA_MESSAGE_DATA");
                bundle.putParcelableArrayList("DATAS", datas);
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