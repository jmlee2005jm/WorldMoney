package com.example.worldmoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.slideup.SlideUp;
import com.mancj.slideup.SlideUpBuilder;

import org.json.JSONException;
import org.json.JSONObject;

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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTAG_";

    private static String API_URL = "https://api.manana.kr/exchange/price/KRW/1/KRW,USD.json";
    private String destCur = "USD";
    private OkHttpClient client = new OkHttpClient();

    private Button confirmBtn;
    private FloatingActionButton fab;
    private Spinner currencySpinner,moneyUsageSpinner;
    private EditText inputMoneyET,descET;
    private RadioButton incomeRadBtn, spentRadBtn, cashBtn, cardBtn;
    private ListView moneyListView;
    private MoneyAdapter adapter;
    private View slideView;
    private FrameLayout dim;
    private TextView sumIncomeTV, sumSpentTV, resultSpentTV, resultLeftCashTV;

    private ArrayList<IncomeItem> moneyItems = new ArrayList<>();
    private IncomeItem tempIncomeItem;

    private String currency,tempCur,moneyUsage,moneyUsageDrawableID;

    private SlideUp slideUp;

    private long leftCash = 100000,usedMoney = 0,incomeMoney = 0;
    private int c = 0,i;
    private double money = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dim = (FrameLayout)findViewById(R.id.dim);

        slideView = (View)findViewById(R.id.slideView);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        confirmBtn = (Button)findViewById(R.id.confirmBtn);

        currencySpinner = (Spinner)findViewById(R.id.inputMoneySpinner);
        moneyUsageSpinner = (Spinner)findViewById(R.id.moneyTypeSpinner);

        inputMoneyET = (EditText)findViewById(R.id.inputMoneyET);
        descET = (EditText)findViewById(R.id.descET);

        incomeRadBtn = (RadioButton)findViewById(R.id.incomeRadioButton);
        spentRadBtn = (RadioButton)findViewById(R.id.spentRadioButton);
        cashBtn = (RadioButton)findViewById(R.id.cashRadioButton);
        cardBtn = (RadioButton)findViewById(R.id.cardRadioButton);

        sumIncomeTV = (TextView)findViewById(R.id.sumIncomeTV);
        sumSpentTV = (TextView)findViewById(R.id.sumSpentTV);
        resultSpentTV = (TextView)findViewById(R.id.resultSpentTV);
        resultLeftCashTV = (TextView)findViewById(R.id.resultLeftCashTV);


        moneyListView = (ListView)findViewById(R.id.moneyListView);

        adapter = new MoneyAdapter(this, moneyItems);

        moneyListView.setAdapter(adapter);

        final FirebaseDatabase prevDB = FirebaseDatabase.getInstance();
        DatabaseReference prevCRef = prevDB.getReference("element_count");
        prevCRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                c = snapshot.getValue(Integer.class);
                if(c != 0) {
                    for (i = 1; i < c + 1; i++) {
                        DatabaseReference prevMoneyRef = prevDB.getReference("" + i);
                        prevMoneyRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tempIncomeItem = snapshot.getValue(IncomeItem.class);
                                moneyItems.add(tempIncomeItem);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.w(TAG, "Failed to read value", error.toException());
                            }
                        });
                    }
                    adapter.setItem(moneyItems);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG,"Failed to read value",error.toException());
            }
        });

        slideUp = new SlideUpBuilder(slideView)
                .withListeners(new SlideUp.Listener.Events() {
                    @Override
                    public void onSlide(float percent) {
                        dim.setAlpha(1 - (percent / 100));
                        if (fab.isShown() && percent < 100) {
                            fab.hide();
                        }
                    }

                    @Override
                    public void onVisibilityChanged(int visibility) {
                        if (visibility == View.GONE){
                            fab.show();
                        }
                    }
                })
                .withStartGravity(Gravity.BOTTOM)
                .withLoggingEnabled(true)
                .withGesturesEnabled(true)
                .withStartState(SlideUp.State.HIDDEN)
                .withSlideFromOtherView(findViewById(R.id.rootView))
                .build();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slideUp.show();
            }
        });

        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempCur = currencySpinner.getItemAtPosition(position).toString();
                switch (tempCur){
                    case "KRW":
                        currency = "KRW";
                        break;
                    case "JPY":
                        currency = "JPY";
                        break;
                    case "CNY":
                        currency = "CNY";
                        break;
                    case "SGD":
                        currency = "SGD";
                        break;
                    case "AUD":
                        currency = "AUD";
                        break;
                    case "USD":
                    default:
                        currency = "USD";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        moneyUsageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                moneyUsage = parent.getItemAtPosition(position).toString();
                switch (moneyUsage){
                    case "식비":
                        moneyUsageDrawableID = "fork";
                        break;
                    case "교통비":
                        moneyUsageDrawableID = "ecocar";
                        break;
                    case "쇼핑":
                        moneyUsageDrawableID = "shoppingcart";
                        break;
                    case "관광":
                        moneyUsageDrawableID = "car";
                        break;
                    case "숙박비":
                        moneyUsageDrawableID = "bed";
                        break;
                    case "항공비":
                        moneyUsageDrawableID = "airplane";
                        break;
                    case "기타":
                        moneyUsageDrawableID = "more";
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IncomeItem item = new IncomeItem();
                String inputMoney = inputMoneyET.getText().toString();
                if(!inputMoney.isEmpty())
                    try {
                        money = Double.parseDouble(inputMoney);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "TYPE ERROR", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        return;
                    }
                item.setDesc(descET.getText().toString());
                int id = getResources().getIdentifier(moneyUsageDrawableID,"drawable",getPackageName());
                item.setImageRes(id);
                item.setCur(currency);
                item.setMoney(money);
                changeURL(currency,money,destCur);
                try{
                    JSONObject jsonObject = new JSONObject(json);
                    money = jsonObject.getDouble(destCur);
                } catch(JSONException e){
                    e.printStackTrace();
                }
                if (incomeRadBtn.isChecked()){
                    item.setAmount("+"+money);
                    item.setPositive(TRUE);
                    incomeMoney += money;
                    if (cashBtn.isChecked()){
                        leftCash += money;
                    }
                } else if (spentRadBtn.isChecked()){
                    item.setAmount("-"+money);
                    item.setPositive(FALSE);
                    usedMoney += money;
                    if (cashBtn.isChecked()){
                        leftCash -= money;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "+/- 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (cashBtn.isChecked()){
                    item.setCash(TRUE);
                } else if (cardBtn.isChecked()){
                    item.setCash(FALSE);
                } else {
                    Toast.makeText(MainActivity.this, "현금/카드 선택해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                moneyItems.add(item);
                adapter.setItem(moneyItems);
                sumSpentTV.setText("$"+String.format("%.2f",usedMoney));
                sumIncomeTV.setText("$"+String.format("%.2f",incomeMoney));
                resultSpentTV.setText("$"+String.format("%.2f",usedMoney));
                resultLeftCashTV.setText("$"+String.format("%.2f",leftCash));

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference moneyRef = database.getReference(""+moneyItems.size());
                moneyRef.setValue(item);

                c += 1;
                DatabaseReference countRef = database.getReference("element_count");
                countRef.setValue(c);
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
                        Log.d(TAG,"json: " + value);
                        json = value;
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

    private static String json;
    private void changeURL(String init, double initMoney, String fin){
        double finMoney = 0;
        API_URL ="https://api.manana.kr/exchange/price/"+init+"/"+initMoney+"/"+fin+".json";
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