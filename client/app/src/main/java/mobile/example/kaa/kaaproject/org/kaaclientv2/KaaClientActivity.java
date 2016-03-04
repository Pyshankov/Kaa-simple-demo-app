package mobile.example.kaa.kaaproject.org.kaaclientv2;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.GetChars;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.kaaproject.kaa.client.AndroidKaaPlatformContext;
import org.kaaproject.kaa.client.Kaa;
import org.kaaproject.kaa.client.KaaClient;
import org.kaaproject.kaa.client.logging.DefaultLogUploadStrategy;
import org.kaaproject.kaa.client.logging.LogStorageStatus;
import org.kaaproject.kaa.client.logging.LogUploadStrategyDecision;
import org.kaaproject.kaa.client.logging.memory.MemLogStorage;
import org.kaaproject.kaa.client.notification.NotificationListener;
import org.kaaproject.kaa.client.notification.NotificationTopicListListener;
import org.kaaproject.kaa.client.notification.UnavailableTopicException;
import org.kaaproject.kaa.common.endpoint.gen.SubscriptionType;
import org.kaaproject.kaa.common.endpoint.gen.Topic;
import org.kaaproject.kaa.example.mobile.log.Data;
import static org.kaaproject.kaa.client.logging.LogUploadStrategyDecision.UPLOAD;
import static org.kaaproject.kaa.client.logging.LogUploadStrategyDecision.NOOP;
import java.io.File;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class KaaClientActivity extends AppCompatActivity {

    private static final int FILE_SELECT_CODE = 0;

    private static String TAG = "KaaClientActivity";

    private KaaClient client;
    private NotificationManager mNotificationManager;
    private TextView textView;
    private RadioGroup radioHashFunctionGroup;
    private RadioButton radioCheckedHashFunctionButton;
    private FloatingActionButton floatingActionButtonAddFile;
    private FloatingActionButton floatingActionButtonSendFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kaa_client);

        client = Kaa.newClient(new AndroidKaaPlatformContext(this.getApplicationContext()));
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        textView=(TextView) findViewById(R.id.path_to_file);
        radioHashFunctionGroup = (RadioGroup) findViewById(R.id.radioGroup);
        floatingActionButtonAddFile = (FloatingActionButton) findViewById(R.id.btn_choose_file);
        floatingActionButtonSendFile = (FloatingActionButton) findViewById(R.id.btn_send_to_kaa);

        client.setLogStorage(new MemLogStorage(Long.MAX_VALUE,Long.MAX_VALUE,256));

        client.setLogUploadStrategy(new DefaultLogUploadStrategy() {
            @Override
            public LogUploadStrategyDecision isUploadNeeded(LogStorageStatus status) {
                return status.getRecordCount() >= 1 ? UPLOAD : NOOP;
            }
        });

        client.addTopicListListener(new NotificationTopicListListener() {
            public void onListUpdated(List<Topic> list) {
                for (Topic t : list) {
                    try {
                        if (!t.getSubscriptionType().equals(SubscriptionType.MANDATORY_SUBSCRIPTION)) {
                            client.subscribeToTopic(t.getId());
                        }
                    } catch (UnavailableTopicException e) {
                    }
                }
            }
        });

        client.addNotificationListener(new NotificationListener() {
            public void onNotification(String s, org.kaaproject.kaa.example.mobile.notification.Data data) {

                SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");

//                String processingResult = new String((data.getData().array()));
                String processingResult = new BigInteger(1, data.getData().array()).toString(16);

                String title = "Time: " + DATE_FORMAT.format(new Date(data.getTimestamp()));
                Log.e(TAG, "P: " + processingResult);

                Intent resultIntent = new Intent(getApplicationContext(), DetailActivity.class);
                resultIntent.putExtra("title",title);
                resultIntent.putExtra("result","result: "+ processingResult);
                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                getApplicationContext(),
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(KaaClientActivity.this)
                        .setSmallIcon(R.mipmap.kaa)
                        .setContentTitle(title)
                        .setContentText(processingResult)
                        .setContentIntent(resultPendingIntent);
                mNotificationManager.notify(
                        1,
                        mBuilder.build());



            }
        });
        floatingActionButtonAddFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        floatingActionButtonSendFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getText().equals("")) {
                    Toast.makeText(KaaClientActivity.this, "Please,choose a file.", Toast.LENGTH_SHORT).show();
                } else {
                    radioCheckedHashFunctionButton = (RadioButton) findViewById(radioHashFunctionGroup.getCheckedRadioButtonId());

                    String filename = textView.getText().toString();

                    File file = new File(filename);

                    ByteBuffer byteBuffer = ByteBuffer.wrap(FileUtil.fromFileToBytes(file));

                    Data data = new Data(new Date().getTime(), byteBuffer,
                            client.getEndpointKeyHash(), radioCheckedHashFunctionButton.getText().toString().toUpperCase());

                    Log.d(TAG, data.toString());

                    client.addLogRecord(data);

                }
            }
        });

        client.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    String path  = FileUtil.getPath(this, uri);
                    Log.d(TAG, "File Path: " + path);
                    textView.setText(path);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("file_path",textView.getText().toString());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        textView.setText(savedInstanceState.getString("file_path"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.stop();
    }

}
