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

    /* UI Elements */
    private ActivityMainBinding binding;
    private Spinner mCoinSpinner;
    private TextView mScrollText;

    /* Crypto Coin List */
    private final ArrayList<Coin> mCoinList = new ArrayList<>();
    private ArrayAdapter<Coin> mCoinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* UI */
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        mScrollText = findViewById(R.id.text_json);

        /* Coin List Spinner Setup */
        mCoinList.add(new Coin("Loading Crypto", "Currency", 0.0));
        mCoinSpinner = findViewById(R.id.coin_spinner);
        mCoinAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mCoinList);
        mCoinSpinner.setAdapter(mCoinAdapter);
        mCoinSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the value selected by the user
                Coin selectedCoin = (Coin) parent.getSelectedItem();

                mScrollText.append(selectedCoin.toString() + "\n");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner","No Item Selected");
            }
        });

        /* Retrieve coin list for spinner */
        try {
            getHTTPData();
        } catch (IOException e) {
            e.printStackTrace();
            mCoinList.add(new Coin("Load Error", "Currency", 0.0));
        }

   }

   void getHTTPData() throws IOException {
        /* https://www.coingecko.com/api/documentations/v3#/ */
        OkHttpClient client = new OkHttpClient();
        /* Set API URL */
        HttpUrl.Builder urlBuilder =
                Objects.requireNonNull(HttpUrl.parse("https://api.coingecko.com/api/v3/simple/price"))
                        .newBuilder();
       /* Add coin list to be fetched could be a string resource */
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

        /* Add the returned currency parameter */
        urlBuilder.addQueryParameter("vs_currencies", "gbp");

        /* Build the URL with params */
        String url = urlBuilder.build().toString();

        /* Create an OkHTTP request object */
        Request request = new Request.Builder()
                .url(url)
                .build();

        /* Add the request to the call queue for sending */
        client.newCall(request).enqueue(new Callback()
                {

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        Log.d("OkHTTPResponse","The API call for the coins failed: "
                                + e.getMessage());
                        call.cancel();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                        JSONObject oCoin;

                        final String myResponse = Objects.requireNonNull(response.body(),"Invalid Null API Response Received").string();
                        Log.d("OkHTTPResponse","API Call Successful");
                        response.close();

                        try {
                            JSONObject oJSON = new JSONObject(myResponse);
                            mCoinList.clear();

                            double CoinValue;
                            /* Build the list of coins from API Data */
                            for (Iterator<String> it = oJSON.keys(); it.hasNext(); ) {
                                String coinName = it.next();
                                CoinValue = oJSON.getJSONObject(coinName).getDouble("gbp");
                                mCoinList.add(new Coin(coinName, "gbp", CoinValue));
                            }

                        } catch (JSONException e) {
                            Log.d("OkHTTPResponse","JSON Format Problem");
                            e.printStackTrace();
                        }

                        MainActivity.this.runOnUiThread(() -> {
                            Log.d("OkHTTPResponse",myResponse);
                            /* Update spinner with new coin data */
                            mCoinList.sort(new SortByCoinName());
                            mCoinAdapter.notifyDataSetChanged();

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