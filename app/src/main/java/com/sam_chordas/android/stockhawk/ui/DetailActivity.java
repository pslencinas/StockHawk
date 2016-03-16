package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.animation.Animation;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.FetchDetailStockTask;

public class DetailActivity extends AppCompatActivity {

    private Intent mServiceIntent;
    private String mSymbol;
    public static LineCard lineCard;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                mSymbol= null;
            } else {
                mSymbol= extras.getString("symbol");
            }
        } else {
            mSymbol= (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }

        new FetchDetailStockTask(this).execute(mSymbol);

        lineCard = (new LineCard((CardView) findViewById(R.id.card2), getApplication()));
        //lineCard.init();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public class LineCard extends CardController {


        private final LineChartView mChart;
        private final TextView name;

        public LineCard(CardView card, Context context){
            super(card);

            mChart = (LineChartView) card.findViewById(R.id.chart2);
            name = (TextView) card.findViewById(R.id.chart2_legend);

            name.setText(mSymbol);

        }


        @Override
        public void show(Runnable action){
            super.show(action);

            LineSet dataset = new LineSet(FetchDetailStockTask.mLabels, FetchDetailStockTask.mValues);

            dataset.setColor(Color.parseColor("#e11a2e"))
                    .setFill(Color.parseColor("#3d6c73"))
                    .setGradientFill(new int[]{Color.parseColor("#364d5a"), Color.parseColor("#3f7178")}, null);
            mChart.addData(dataset);

            mChart.setBorderSpacing(1)
                    .setLabelsColor(Color.parseColor("#ffffff"))
                    .setAxisBorderValues((int)FetchDetailStockTask.minValue,(int)FetchDetailStockTask.maxValue + 1)
                    .setXAxis(false)
                    .setYAxis(false)
                    .setBorderSpacing(Tools.fromDpToPx(5));

            Animation anim = new Animation().setEndAction(action);

            mChart.show(anim);
        }

        @Override
        public void update(){
            super.update();

            mChart.dismissAllTooltips();
            mChart.updateValues(0, FetchDetailStockTask.mValues);
            mChart.notifyDataUpdate();
        }

        @Override
        public void dismiss(Runnable action){
            super.dismiss(action);

            mChart.dismissAllTooltips();
            mChart.dismiss(new Animation().setEndAction(action));
        }
    }
}
