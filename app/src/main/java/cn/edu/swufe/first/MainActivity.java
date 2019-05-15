package cn.edu.swufe.first;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView out;
    EditText inp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inp = (EditText)findViewById(R.id.inp);
        Button btn = findViewById(R.id.btn);
        out = (TextView)findViewById(R.id.showText);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.i("click","onClick .....");

        String str = inp.getText().toString();
        Double result = Double.parseDouble(str);
        result = result*9/5+32;
        out.setText("结果为：" + result);
    }
}
