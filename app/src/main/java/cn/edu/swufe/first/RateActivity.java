package cn.edu.swufe.first;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RateActivity extends AppCompatActivity implements Runnable {

    private final String TAG = "Rate";
    private float dollarRate = 0.1f;
    private float euroRate = 0.2f;
    private float wonRate = 0.3f;

    EditText rmb;
    TextView show;
    Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb = (EditText) findViewById(R.id.rmb);
        show = (TextView) findViewById(R.id.showOut);

        //获取SP保存的数据
        SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        dollarRate = sharedPreferences.getFloat("dollarRate",0.0f);
        euroRate = sharedPreferences.getFloat("euroRate",0.0f);
        wonRate = sharedPreferences.getFloat("wonRate",0.0f);

        Log.i(TAG,"onCreate: sp dollarRate=" + dollarRate);
        Log.i(TAG,"onCreate: sp euroRate=" + euroRate);
        Log.i(TAG,"onCreate: sp wonRate=" + wonRate);

        //开启子线程
        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    Bundle bd1 = (Bundle) msg.obj;
                    dollarRate = bd1.getFloat("dollar-rate");
                    euroRate = bd1.getFloat("euro-rate");
                    wonRate = bd1.getFloat("won-rate");

                    Log.i(TAG,"handMessage: dollarRate:" + dollarRate);
                    Log.i(TAG,"handMessage: euroRate:" + euroRate);
                    Log.i(TAG,"handMessage: wonRate:" + wonRate);

                    Toast.makeText(RateActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();

                }
                super.handleMessage(msg);
            }

        };
    }

    public void onClik(View btn){
        Log.i(TAG,"onClick:");
        String str = rmb.getText().toString();
        Log.i(TAG,"onClick: get str=" + str);

        float r = 0;
        if(str.length()>0){
            r = Float.parseFloat(str);
        }else{
            Toast.makeText(this,"请输入金额",Toast.LENGTH_SHORT).show();
        }

        if (btn.getId()==R.id.btn_dollar){
            show.setText(String.format("%.2f",r*dollarRate));
        }else if(btn.getId()==R.id.btn_euro){
            show.setText(String.format("%.2f",r*euroRate));
        }else{
            show.setText(String.format("%.2f",r*wonRate));
        }
    }

    public void openOne(View btn){
        openConfig();
    }

    private void openConfig() {
        Intent config = new Intent(this,ConfigActivity.class);
        config.putExtra("dollar_rate_key",dollarRate);
        config.putExtra("euro_rate_key",euroRate);
        config.putExtra("won_rate_key",wonRate);

        Log.i(TAG,"openOne:dollarRate=" + dollarRate);
        Log.i(TAG,"openOne:euroRate=" + euroRate);
        Log.i(TAG,"openOne:wonRate=" + wonRate);

//        startActivity(config);
        startActivityForResult(config,1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.menu_set){

            openConfig();
        }else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list = new Intent(this,MyList2Activity.class);
            startActivity(list);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==1 && requestCode==2){
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar",0.1f);
            euroRate = bundle.getFloat("key_euro",0.1f);
            wonRate = bundle.getFloat("key_won",0.1f);
            Log.i(TAG,"onActivityResult:dollarRate=" + dollarRate);
            Log.i(TAG,"onActivityResult:euroRate=" + euroRate);
            Log.i(TAG,"onActivityResult:wonRate=" + wonRate);

            //将新设置的汇率写道SP里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate",dollarRate);
            editor.putFloat("euro_rate",euroRate);
            editor.putFloat("won_rate",wonRate);
            editor.commit();
            Log.i(TAG,"onActivityResult: 数据已保存到sharePreference");
        }
        super.onActivityResult(requestCode,resultCode,data);

    }

    @Override
    public void run() {
        Log.i(TAG,"run: run()......");
        for(int i=1;i<6;i++){
            Log.i(TAG,"run: i=" + i);
            try{
                Thread.sleep(2000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

        //用于保存获取的汇率
        Bundle bundle = new Bundle();



        //获取网络数据
//        URL url = null;
//        try {
//            url = new URL("www.usd-cny.com/bankofchina.htm");
//            HttpURLConnection http = (HttpURLConnection) url.openConnection();
//            InputStream in = http.getInputStream();
//
//            String html = inputSream25tring(in);
//            Log.i(TAG,"run: html=" + html);
//            Document doc = Jsoup.parse(html);
//
//        }catch (MalformedURLException e){
//            e.printStackTrace();
//        }catch (IOException e){
//            e.printStackTrace();
//        }

        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG, doc.title());
            Elements tables = doc.getElementsByTag("table");
//            for(Element table : tables){
//                Log.i(TAG,"run: table["+i+"]=" + table);
//            }
            Element table6 = tables.get(0);
            //获取TD中的元素
            Elements tds = table6.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                Log.i(TAG,"run: " + td1.text() + "==>" + td2.text());
                String str1 = td1.text();
                String val = td2.text();

            if("美元".equals(str1)) {
                bundle.putFloat("dollar-rate", 100f / Float.parseFloat(val));
            }else if("欧元".equals(str1)) {
                bundle.putFloat("euro-rate", 100f / Float.parseFloat(val));
            }else if("韩国元".equals(str1)) {
                bundle.putFloat("won-rate", 100f / Float.parseFloat(val));
            }

            }

        }catch(IOException e){
            e.printStackTrace();
        }

        //bundle中保存所获取的汇率

        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
//        msg.obj = "Hello from run()";
        msg.obj = bundle;
        handler.sendMessage(msg);

    }

    private String inputSream25tring(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream,"UTF-8");
        for(;;){
            int rsz = in.read(buffer,0,buffer.length);
            if(rsz < 0)
                break;
            out.append(buffer,0,rsz);
        }
        return out.toString();
    }
}
