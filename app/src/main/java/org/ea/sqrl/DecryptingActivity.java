package org.ea.sqrl;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.ea.sqrl.storage.EncryptionUtils;
import org.ea.sqrl.storage.SQRLStorage;

import java.util.Arrays;

public class DecryptingActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    private byte[] qrCodeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decrypting);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] rawQRData = extras.getByteArray(ScanSecretActivity.EXTRA_MESSAGE);
            String hexdata = EncryptionUtils.byte2hex(rawQRData);
            int end = hexdata.indexOf("0ec11ec11");
            qrCodeData = EncryptionUtils.hex2Byte(hexdata.substring(3, end));
            System.out.println(EncryptionUtils.byte2hex(qrCodeData));
        }

        final ProgressBar pbDecrypting = findViewById(R.id.pbDecrypting);
        final EditText txtPassword = findViewById(R.id.txtPassword);
        final Button btnDecryptKey = findViewById(R.id.btnDecryptKey);
        btnDecryptKey.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            SQRLStorage storage = new SQRLStorage(qrCodeData, true);
                            storage.setProgressBar(pbDecrypting);
                            storage.setHandler(handler);
                            storage.decryptIdentityKey(txtPassword.getText().toString());
                        } catch (Exception e) {
                            System.out.println("ERROR: " + e.getMessage());
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });
    }
}
