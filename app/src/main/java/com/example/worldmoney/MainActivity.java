package com.example.worldmoney;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTAG_";
    private static final String API_URL = "https://api.manana.kr/exchange/price/KRW/1/KRW,USD,JPY.json";
    private OkHttpClient client = new OkHttpClient();

    private Button confirmBtn,testBtn;
    private Spinner spinner;
    private EditText inputMoneyET,titleET,descET;
    private RadioButton incomeRadBtn, spentRadBtn;
    private ListView moneyListView;
    private MoneyAdapter adapter;
    private View slideView;

    private ArrayList<IncomeItem> moneyItems = new ArrayList<>();

    private String currency;

    private SlideUp slideUp;

    private long leftMoney = 100000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testBtn = findViewById(R.id.testBtn);

        slideView = (View)findViewById(R.id.slideView);
        confirmBtn = (Button)findViewById(R.id.confirmBtn);
        spinner = (Spinner)findViewById(R.id.inputMoneySpinner);
        inputMoneyET = (EditText)findViewById(R.id.inputMoneyET);
        titleET = (EditText)findViewById(R.id.titleMoneyET);
        descET = (EditText)findViewById(R.id.descET);
        incomeRadBtn = (RadioButton)findViewById(R.id.incomeRadioButton);
        spentRadBtn = (RadioButton)findViewById(R.id.spentRadioButton);

        moneyListView = (ListView)findViewById(R.id.moneyListView);

        adapter = new MoneyAdapter(this, moneyItems);

        moneyListView.setAdapter(adapter);

        slideUp = new SlideUpBuilder(slideView).build();

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideUp.show();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currency = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncomeItem item = new IncomeItem();
                double money = Double.parseDouble(inputMoneyET.getText().toString());
                item.setTitle(titleET.getText().toString());
                item.setDesc(descET.getText().toString());
                if (incomeRadBtn.isChecked()){
                    item.setAmount("+"+inputMoneyET.getText().toString());
                    leftMoney += money;
                    item.setLeftMoney(leftMoney);
                    item.setMoneySignColor(0xe0ebc3);
                } else if (spentRadBtn.isChecked()){
                    item.setAmount("-"+inputMoneyET.getText().toString());
                    leftMoney -= money;
                    item.setLeftMoney(leftMoney);
                    item.setMoneySignColor(0xf3bfbf);
                } else {
                    Toast.makeText(MainActivity.this, "NOT SELECTED", Toast.LENGTH_SHORT).show();
                    return;
                }
                moneyItems.add(item);
                adapter.setItem(moneyItems);
            }
        });



        createObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "onSubscribe: " + d);
                    }

                    @Override
                    public void onNext(String value) {
                        Log.d(TAG, "onNext: " + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                    }
                });
    }

    private String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private Observable<String> createObservable() {
        //Could use fromCallable
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just(run(API_URL));
            }
        });
    }
}