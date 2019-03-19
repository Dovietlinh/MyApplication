package com.example.myapplication;
import java.util.ArrayList;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalFloat;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {
    Button btngetlist;
    ListView lvcatalog;
    final String URL="http://linhdv106.somee.com/WebService.asmx?WSDL";
    ArrayList<String> arrCate=new ArrayList<String>();
    ArrayAdapter<String>adapter=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        lvcatalog=findViewById(R.id.lvcatalog);
        btngetlist=findViewById(R.id.btnlistcatalog);
        btngetlist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0)
            {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                            Manifest.permission.INTERNET)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.INTERNET},
                                1);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    doGetList();
                }


            }
        });
        adapter=new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, arrCate);
        lvcatalog.setAdapter(adapter);
    }
    public void doGetList() {
        try{final String NAMESPACE="http://tempuri.org/";
            final String METHOD_NAME="getListProduct";
            final String SOAP_ACTION=NAMESPACE+METHOD_NAME;
            SoapObject request=new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope=new SoapSerializationEnvelope(SoapEnvelope.VER10);
            envelope.setOutputSoapObject(request);
            envelope.dotNet=true;

            //Nếu truyền số thực trên mạng bắt buộc phải đăng ký MarshalFloat
            //không có nó thì bị báo lỗi
            MarshalFloat marshal=new MarshalFloat();
            marshal.register(envelope);

            HttpTransportSE androidHttpTransport=
                    new HttpTransportSE(URL);
            try{
                androidHttpTransport.call(SOAP_ACTION, envelope);
            }catch (Exception e){
                Log.d("loi",""+e.toString());
            }


            //Get Array Catalog into soapArray
            SoapObject soapArray=(SoapObject) envelope.getResponse();
            arrCate.clear();
            //soapArray.getPropertyCount() return number of
            //element in soapArray
            //vòng lặp duyệt qua từng dòng dữ liệu
            for(int i=0; i<soapArray.getPropertyCount(); i++)
            {
                //(SoapObject) soapArray.getProperty(i) get item at position i
                SoapObject soapItem =(SoapObject) soapArray.getProperty(i);
                //soapItem.getProperty("CateId") get value of CateId property
                //phải mapp đúng tên cột:
                String cateId=soapItem.getProperty("ProductID").toString();
                String cateName=soapItem.getProperty("Name").toString();
                //đẩy vào array
                arrCate.add(cateId+" - "+cateName);
            }
            //xác nhận cập nhật giao diện
            adapter.notifyDataSetChanged();
        }
        catch(Exception e){}}
}