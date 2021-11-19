package com.example.cryptotracker;

import androidx.annotation.NonNull;

import java.util.Comparator;

public class Coin {
    String mCoin;
    String mCurrency;
    Double mValue;

    public Coin(String Name,String Currency, Double Value){
        mCoin = Name;
        mCurrency = Currency;
        mValue = Value;
    }

    public String CoinName(){ return mCoin;}

    @NonNull
    @Override
    public String toString(){
        return mCoin + " (" + mValue.toString() + ") " + mCurrency;
    }

}

class SortbyCoinName implements Comparator<Coin> {
    // Used for sorting in ascending order of
    // name
    public int compare(Coin a, Coin b)
    {
        return a.CoinName().compareTo(b.CoinName());
    }
}