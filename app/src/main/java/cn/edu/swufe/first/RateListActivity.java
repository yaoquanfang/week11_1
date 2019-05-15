package cn.edu.swufe.first;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RateListActivity extends ListActivity implements Runnable{
    String data[] = {"one","two","three"};
    Handler handler;
    private final String TAG = "Rate";
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        List<String> list1 = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_ratelist);已经有了布局

        ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        setListAdapter(adapter);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==7){
                    List<String> list2  = (List<String>) msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(RateListActivity.this,android.R.layout.simple_list_item_1,list2);
                    setListAdapter(adapter);
                }
            }
        };

    }

    @Override
    public void run() {
        //获取网络数据,带回到list的主线程
        List<String> retList = new ArrayList<String>();
        Document doc = null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
            Log.i(TAG,"run"+doc.title());
            Elements tables = doc.getElementsByTag("table");

            Element table6 = tables.get(0);
            //Log.i(TAG,"run"+tables);
            //获取td中的数据
            Elements tds = table6.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=6){
                Element td1 = tds.get(i);
                Element td2 = tds.get(i+5);
                Log.i(TAG,"run:"+ td1.text() + "==>" + td2.text());

                String str1 = td1.text();
                String val = td2.text();

                retList.add(str1+"==>" + val);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);

    }
}

