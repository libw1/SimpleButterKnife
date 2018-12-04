package conykais.bindview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import conykais.bindview2.BindViewKnife;
import conykais.lib_annotation.BindView;
import conykais.lib_annotation.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.text)
    TextView textView;
    @BindView(R.id.button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BindViewKnife.bind(this);
        textView.setText("hello!");
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"onClick " , Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @OnClick({R.id.button, R.id.text})
    public void onClick(View view){
        if (view.getId() == R.id.button) {
            Toast.makeText(MainActivity.this, "onClick ", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,Main2Activity.class));
        } else if (R.id.text == view.getId()){
            textView.setText("click!");
        }
    }
}
