/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sam_chordas.android.stockhawk.data;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.sam_chordas.android.stockhawk.ui.DetailActivity;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FetchDetailStockTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchDetailStockTask.class.getSimpleName();
    private String stockName;
    public Context mContext;
    private OkHttpClient client = new OkHttpClient();
    private ArrayList<String> resultJson = new ArrayList<>();
    public static ArrayList<String> aDate = new ArrayList<>();
    public static ArrayList<String> aClose = new ArrayList<>();

    public static String[] mLabels = {"", "", "", "", "", "","", "", "", "","", "", "", "", "", "", "", "", "", ""};
    public static float[] mValues = {0f,2f,1.4f,4.f,3.5f,4.3f,2f,4f,6.f,5.f,3.5f,4.3f,2f,4f,6.f,3f,3f,2f,3f,6.f};
    public static float minValue;
    public static float maxValue;

    public FetchDetailStockTask(Context context) {
        mContext = context;
    }


    private void getDetailDataFromJson(String jsonStr)
            throws JSONException {

        JSONArray resultsArray = null;
        JSONObject dataJson = null;
        JSONObject jsonObject = null;

        try {
            dataJson = new JSONObject(jsonStr);

            jsonObject = dataJson.getJSONObject("query");
            resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");
            aDate.clear();
            aClose.clear();

            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject movie = resultsArray.getJSONObject(i);

                aDate.add(movie.getString("Date").substring(8,10));
                aClose.add(movie.getString("Close"));

                Log.i("Date: ", movie.getString("Date").substring(8, 10));
                Log.i("Close: ", movie.getString("Close"));

            }

        }
        catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        }

    }

    String fetchData(String url) throws IOException{
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected String[] doInBackground(String... params) {


        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        stockName = params[0];

        StringBuilder urlStringBuilder = new StringBuilder();

        try {

            // Base URL for the Yahoo query
            urlStringBuilder.append("https://query.yahooapis.com/v1/public/yql?q=");
            urlStringBuilder.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol "
                    + "in (", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("\""+stockName+"\") ", "UTF-8"));
            urlStringBuilder.append(URLEncoder.encode("and startDate = '"+get30daysTimeStamp()+"' and " +
                    "endDate = '"+getCurrentTimeStamp()+"'", "UTF-8"));

            urlStringBuilder.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables."
                    + "org%2Falltableswithkeys&callback=");

            String urlString;
            String getResponse;

            if (urlStringBuilder != null) {
                urlString = urlStringBuilder.toString();
                Log.i("URL: ", urlString);

                getResponse = fetchData(urlString);
                Log.i("getResponse: ", getResponse);
                getDetailDataFromJson(getResponse);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return null;
    }


    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String strDate = sdfDate.format(now);

        //Log.i("30 DIAS MENOS: ", get30daysTimeStamp());
        return strDate;
    }

    public static String get30daysTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String sToday = sdfDate.format(now);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdfDate.parse(sToday));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, -35);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE

        String output = sdfDate.format(c.getTime());

        return output;
    }



    @Override
    protected void onPostExecute(String[] result) {


        for (int i=0; i<20 ; i++){

            mValues[19-i] = Float.parseFloat(aClose.get(i));
            mLabels[19-i] = aDate.get(i);

            if(i==0){
                minValue = mValues[19-i];
                maxValue = mValues[19-i];
            }else{
                if(mValues[19-i] < minValue)
                    minValue = mValues[19-i];

                if(mValues[19-i] > maxValue)
                    maxValue = mValues[19-i];


            }
        }

        DetailActivity.lineCard.init();
    }
}