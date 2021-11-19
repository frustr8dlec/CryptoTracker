package com.example.cryptotracker;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptotracker.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TextView mJSON_output;
    private final ArrayList<Coin> Coins = new ArrayList<>();
    private ArrayAdapter<Coin> coinAdapter;
    private Spinner coinSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        Coins.add(new Coin("Loading Crypto", "Currency", 0.0));
        mJSON_output = findViewById(R.id.text_json);
        coinSpinner = findViewById(R.id.coin_spinner);
        coinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Coins);
        coinSpinner.setAdapter(coinAdapter);
        coinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the value selected by the user
                Coin selectedCoin = (Coin) parent.getSelectedItem();
                Log.d("Spinner","Item Selected is " + selectedCoin.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner","No Item Selected");
            }
        });
        Log.d("Spinner", "Listener Set");


        try {
            getHTTPData();
        } catch (IOException e) {
            e.printStackTrace();
        }

   }

    void getHTTPData() throws IOException {
        // https://www.coingecko.com/api/documentations/v3#/
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder =
                Objects.requireNonNull(HttpUrl.parse("https://api.coingecko.com/api/v3/simple/price"))
                        .newBuilder();

        urlBuilder.addQueryParameter("ids", "ampleforth," +
                "ankr," +
                "apollo," +
                "bancor," +
                "binancecoin," +
                "bitcoin," +
                "bitcoin-cash," +
                "cardano," +
                "chainlink," +
                "dash," +
                "ethereum," +
                "tether," +
                "polkadot," +
                "uniswap," +
                "litecoin," +
                "internet-computer," +
                "eos," +
                "the-graph," +
                "maker," +
                "numeraire," +
                "decentraland," +
                "sushi," +
                "filecoin");

        urlBuilder.addQueryParameter("vs_currencies", "gbp");


        String url = urlBuilder.build().toString();
        Log.d("OkHTTP_URL",url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback()
                {

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d("OkHTTPResponse","Error on the call");
                        call.cancel();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        JSONObject json;
                        JSONObject oCoin;

                        double CoinValue;

                        final String myResponse = Objects.requireNonNull(response.body()).string();
                        Log.d("OkHTTPResponse","Call Successful");

                        response.close();

                        try {
                            json = new JSONObject(myResponse);
                            Coins.clear();

                            for (Iterator<String> it = json.keys(); it.hasNext(); ) {
                                String coinName = it.next();
                                CoinValue = json.getJSONObject(coinName).getDouble("gbp");
                                Coins.add(new Coin(coinName, "gbp", CoinValue));
                            }

                            Log.d("OkHTTPResponse","JSON Worked " + coinSpinner.getOnItemSelectedListener());


                        } catch (JSONException e) {
                            Log.d("OkHTTPResponse","JSON Problem");
                            e.printStackTrace();
                        }
                        MainActivity.this.runOnUiThread(() -> {
                            Log.d("OkHTTPResponse","Code running on the UI thread");
                            Log.d("OkHTTPResponse",myResponse);
                            mJSON_output.setText(myResponse);
                            Coins.sort(new SortbyCoinName());
                            coinAdapter.notifyDataSetChanged();

                        });
                    }

                }
        );

    }


    public void refreshCrypto(View view) {
        try {
            getHTTPData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;        }

        return super.onOptionsItemSelected(item);
    }
}