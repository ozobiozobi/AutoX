package org.autojs.autojs.external.open;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.widget.Toast;

import com.stardust.autojs.script.StringScriptSource;
import com.stardust.autojs.servicecomponents.EngineController;
import com.stardust.pio.PFiles;

import org.autojs.autojs.external.ScriptIntents;
import org.autojs.autoxjs.R;
import org.autojs.autojs.model.script.Scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Stardust on 2017/2/22.
 */

public class RunIntentActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            handleIntent(getIntent());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.edit_and_run_handle_intent_error, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    private void handleIntent(Intent intent) {
        Uri uri = intent.getData();
        if (uri == null) {
            Toast.makeText(this, "uri is null", Toast.LENGTH_LONG).show();
            return;
        }
        String path = uri.getPath();
        EngineController.INSTANCE.runScript(new File(path), null, null);
    }
}
