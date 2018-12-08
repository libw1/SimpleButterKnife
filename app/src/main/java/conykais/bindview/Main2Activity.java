package conykais.bindview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import conykais.bindview2.BindViewKnife;
import conykais.lib_annotation.BindView;
import conykais.lib_annotation.OnClick;

public class Main2Activity extends AppCompatActivity {

//    @BindView(R.id.tv)
//    TextView textView;
//    @BindView(R.id.tv2)
//    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        BindViewKnife.bind(this);
//        textView.setText("text1");
//        textView2.setText("text2");
    }

    @OnClick(R.id.tv)
    public void onTestClick(View view){
        if (view.getId() == R.id.tv){
            Toast.makeText(Main2Activity.this,"text 1 ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Main2Activity.this,"text 2 ", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.tv2)
    public void onTest1Click(){
//        if (view.getId() == R.id.tv){
//            Toast.makeText(Main2Activity.this,"text 1 ", Toast.LENGTH_SHORT).show();
//        } else {
            Toast.makeText(Main2Activity.this,"text 2 ", Toast.LENGTH_SHORT).show();
//        }
    }
}
