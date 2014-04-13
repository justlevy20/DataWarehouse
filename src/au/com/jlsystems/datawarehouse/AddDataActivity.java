package au.com.jlsystems.datawarehouse;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Justin Levy on 7/04/14.
 */
public class AddDataActivity extends Activity {
    private String packageName;
    private ArrayList<EditText> editTexts;
    private ArrayList<Field> fields;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        packageName = this.getApplication().getPackageName();
        setContentView(R.layout.adddata);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(packageName + ".EXTRA_MESSAGE_DATA");
        Workarea wa = bundle.getParcelable("WORKAREA");
        String appName = getString(R.string.app_name);
        setTitle(getString(R.string.app_title, appName, wa.getName()));
        LinearLayout layout = (LinearLayout) findViewById(R.id.addNewLayout);
        editTexts = new ArrayList<EditText>();

        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(this);
        String text = wa.getIndexName();
        tv.setText(text);
        EditText et = new EditText(this);
        et.setHint(getString(R.string.hintEnterNew, text));
        et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editTexts.add(et);
        ll.addView(tv);
        ll.addView(et);
        layout.addView(ll);

        fields = bundle.getParcelableArrayList("FIELDS");
        for(Field f : fields){
            ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.HORIZONTAL);
            tv = new TextView(this);
            tv.setText(f.getName());
            et = new EditText(this);
            et.setHint(getString(R.string.hintEnterNew, f.getName()));
            et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            editTexts.add(et);
            ll.addView(tv);
            ll.addView(et);
            layout.addView(ll);
        }
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
                            Data data = new Data(0, fields.get(i - 1).getId(), "", index, "", et.getText().toString());
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