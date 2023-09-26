package com.example.adroid_thuc_hanh1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import  android.view.View;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView tvListAccount;
    private Button buttonGetList;
    private Button buttonCreate;

    private EditText edtName;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText editTextDate;

    private RadioButton radioButtonNam, radioButtonNu;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextDate= findViewById(R.id.editTextDate);
        edtPassword=findViewById(R.id.edtPassword);
        edtEmail=findViewById(R.id.edtEmail);
        edtName=findViewById(R.id.edtName);
        buttonCreate=findViewById(R.id.buttonCreate);
        tvListAccount= findViewById(R.id.tvListAccount);
        buttonGetList= findViewById(R.id.buttonGetAccount);
        radioButtonNam= findViewById(R.id.radioButtonNam);
        radioButtonNu= findViewById(R.id.radioButtonNu);
    }
    public void getData(View v){
        OkHttpClient client= new OkHttpClient();
        String url= "https://be-androidth1.onrender.com/account";
        Request req= new Request.Builder().url(url).build();
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    final String resuilt=response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvListAccount.setText(resuilt);
                        }
                    });
                }
            }
        });
    }


public void createAccount(View v) throws ParseException, IOException {
    OkHttpClient client= new OkHttpClient();
    String name="";
    String email="";
    String password="";
    String dateOfBirth="";
    if(!edtName.getText().toString().trim().isEmpty()){
       name =edtName.getText().toString().trim();
    }
    if(!edtEmail.getText().toString().trim().isEmpty()){
        email=edtEmail.getText().toString().trim();
    }
    if(!edtPassword.getText().toString().trim().isEmpty()){
        password=edtPassword.getText().toString().trim();
    }
    if(!editTextDate.getText().toString().trim().isEmpty()){
       String dateOfBirth1= editTextDate.getText().toString();

        Date date= new Date(dateOfBirth1);
        date= getConvertedDate(date,"UTC");

        dateOfBirth=date.toString();
    }



    Boolean gender;
    if(radioButtonNam.isChecked()){
        gender= true;
    }
    else{
        gender= false;
    }
//    Boolean name=edtName.getText().toString()

    new PostToServer(name,email,password,dateOfBirth,gender).execute();
}

    public static java.util.Date getConvertedDate(java.util.Date date,String newTimeZone) {

        if(date != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy hh:mm a");
            df.setTimeZone(TimeZone.getTimeZone(newTimeZone));

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String newdt = df.format(calendar.getTime());
            try {
                date = df.parse(newdt);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return date;
    }

class PostToServer extends AsyncTask<String, Void,String>{
    OkHttpClient client= new OkHttpClient.Builder().build();
    String name, email, password,dateOfBirth;
    Boolean gender;
    String url= "https://be-androidth1.onrender.com/account/createAccount";

    public PostToServer(String name, String email, String password, String dateOfBirth, Boolean gender) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    @Override
    protected String doInBackground(String... strings) {
        RequestBody requestBody= new FormBody.Builder()
                .add("name",name)
                .add("email",email)
                .add("password",password)
                .add("dateOfBirth",dateOfBirth)
                .add("gender",gender.toString())

                .build();
        Request request= new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        try {
            Response response=client.newCall(request).execute();
            return  response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if(s.equals("Success")) {
            edtName.setText("");
            edtEmail.setText("");
            edtPassword.setText("");
            editTextDate.setText("");
            radioButtonNam.setChecked(false);
            radioButtonNu.setChecked(false);
        }
        Toast.makeText(MainActivity.this,s, Toast.LENGTH_LONG).show();

        super.onPostExecute(s);

    }

}

}