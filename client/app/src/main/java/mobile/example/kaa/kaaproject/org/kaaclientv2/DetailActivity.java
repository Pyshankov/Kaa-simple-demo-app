package mobile.example.kaa.kaaproject.org.kaaclientv2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView title;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title=(TextView) findViewById(R.id.title);
        result=(TextView)findViewById(R.id.result);

        title.setText(getIntent().getStringExtra("title"));
        result.setText(getIntent().getStringExtra("result"));

    }
}
