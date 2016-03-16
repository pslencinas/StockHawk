

package com.sam_chordas.android.stockhawk.ui;

import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.sam_chordas.android.stockhawk.R;


public class CardController {




	private final Runnable showAction = new Runnable() {
		@Override
		public void run() {
			new Handler().postDelayed(new Runnable() {
				public void run() {
					show(unlockAction);
				}
			}, 500);
		}
	};

	private final Runnable unlockAction =  new Runnable() {
		@Override
		public void run() {
			new Handler().postDelayed(new Runnable() {
				public void run() {

				}
			}, 500);
		}
	};


	protected boolean firstStage;


	protected CardController(CardView card){
		super();

		//RelativeLayout toolbar = (RelativeLayout) card.findViewById(R.id.chart_toolbar);

    }


	public void init(){
		show(unlockAction);
	}


	protected void show(Runnable action){

		firstStage = false;
	}

	protected void update(){

		firstStage = !firstStage;
	}

	protected void dismiss(Runnable action){

	}



}
