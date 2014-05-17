package com.example.genji;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.example.genji.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;

public class Genji extends Activity {
private static int ROW_COUNT = -1;
private static int COL_COUNT = -1;
private Context context;
private Drawable backImage;
private int [] [] cards;
private List<Drawable> images;
private Card firstCard;
private Card seconedCard;
private ButtonListener buttonListener;
private static Object lock = new Object();	
private TableLayout mainTable;
private UpdateCardsHandler handler;
private static final int NEW_MENU_ID = Menu.FIRST;
private static final int EXIT_MENU_ID = Menu.FIRST+1;
private int count =0;
private MediaPlayer mp;

@Override
public void onCreate(Bundle savedInstanceState) {
	DisplayMetrics displaymetrics = new DisplayMetrics();
	getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	int height = displaymetrics.heightPixels;
	int width = displaymetrics.widthPixels;
	Log.i("width height ", "w: " + width + " h: " + height);
	
	super.onCreate(savedInstanceState); 
	progressDialog(); 
	handler = new UpdateCardsHandler(); 
	loadImages();
	setContentView(R.layout.activity_bubble_smile); 
	Button btnnew = (Button)findViewById(R.id.btnnew);
	btnnew.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			clickSound();
			singleChoiceDialog();
		}
	});
	
	backImage = getResources().getDrawable(R.drawable.iconfruit); 
	buttonListener = new ButtonListener(); 
	mainTable = (TableLayout)findViewById(R.id.TableLayout03); 
	context = mainTable.getContext();
	newGame(4,4);
}

public void progressDialog(){
	final ProgressDialog progressDialog = ProgressDialog.show(Genji.this, "", "Loading...", true);
	
	new Thread(new Runnable(){
	
	public void run(){
		try {
			Thread.sleep(100);
			progressDialog.dismiss();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
  }).start();
}

private void newGame(int c, int r) {
	ROW_COUNT = r;
	COL_COUNT = c;
	count =0;
	
	cards = new int [COL_COUNT] [ROW_COUNT];
	
	mainTable.removeView(findViewById(R.id.TableRow01) );
	mainTable.removeView(findViewById(R.id.TableRow02) );
	
	TableRow tr = ((TableRow)findViewById(R.id.TableRow03));
	tr.removeAllViews();
	
	mainTable = new TableLayout(context);
	tr.addView(mainTable);
	
	for (int y = 0; y < ROW_COUNT; y++) {
		mainTable.addView(createRow(y));
	}
	
	firstCard=null;
	loadCards();
}

private void loadImages() {
	images = new ArrayList<Drawable>();
	
	images.add(getResources().getDrawable(R.drawable.card1));
	images.add(getResources().getDrawable(R.drawable.card2));
	images.add(getResources().getDrawable(R.drawable.card3));
	images.add(getResources().getDrawable(R.drawable.card4));
	images.add(getResources().getDrawable(R.drawable.card5));
	images.add(getResources().getDrawable(R.drawable.card6));
	images.add(getResources().getDrawable(R.drawable.card7));
	images.add(getResources().getDrawable(R.drawable.card8));
	images.add(getResources().getDrawable(R.drawable.card9));
	images.add(getResources().getDrawable(R.drawable.card10));
	images.add(getResources().getDrawable(R.drawable.card11));
	images.add(getResources().getDrawable(R.drawable.card12));
	images.add(getResources().getDrawable(R.drawable.card13));
	images.add(getResources().getDrawable(R.drawable.card14));
	images.add(getResources().getDrawable(R.drawable.card15));
	images.add(getResources().getDrawable(R.drawable.card16));
	images.add(getResources().getDrawable(R.drawable.card17));
	images.add(getResources().getDrawable(R.drawable.card18));
	images.add(getResources().getDrawable(R.drawable.card19));
	images.add(getResources().getDrawable(R.drawable.card20));
	images.add(getResources().getDrawable(R.drawable.card21));
}

private void loadCards(){
	int size = ROW_COUNT*COL_COUNT;
	
	Log.i("loadCards()","size=" + size);
	
	ArrayList<Integer> list = new ArrayList<Integer>();
	
	for(int i=0;i<size;i++){
		list.add(new Integer(i));
	}	
	Random r = new Random();
	
	for(int i=size-1;i>=0;i--){
		int t=0;
	
		if(i>0){
			t = r.nextInt(i);
		}
		
		t=list.remove(t).intValue();
		cards[i%COL_COUNT][i/COL_COUNT]=t%(size/2);
		
		Log.i("loadCards()", "card["+(i%COL_COUNT)+
		"]["+(i/COL_COUNT)+"]=" + cards[i%COL_COUNT][i/COL_COUNT]);
	}

}

private TableRow createRow(int y){
	TableRow row = new TableRow(context);
	row.setHorizontalGravity(Gravity.CENTER);
	
	for (int x = 0; x < COL_COUNT; x++) {
		row.addView(createImageButton(x,y));
	}
	return row;
}

private View createImageButton(int x, int y){
	Button button = new Button(context);
	button.setBackgroundDrawable(backImage);
	button.setId(100*x+y);
	button.setOnClickListener(buttonListener);
	return button;
}

class ButtonListener implements OnClickListener {

int doubleTap = 0;        // wrap imag when re-tap
int currentCard = -1;      // current position of Card select

@Override
public void onClick(View v) {
	if(currentCard != v.getId()) {      
		currentCard = v.getId();
		doubleTap = 0;
	Log.i("doubleTap: ", " tap in another card n = " + doubleTap + " other  card = " + currentCard);
	} else {     // Tap dung card 2,3 ..n lan
		doubleTap++;
	}
	synchronized (lock) {
		if(firstCard!=null && seconedCard != null){  // ???
			Log.i("2 card", "card 1 = " + firstCard.x + firstCard.y + " 2nd card = " + seconedCard.x + seconedCard.y);
			return;
		}
		int id = v.getId();
		int x = id/100;
		int y = id%100;
		turnCard((Button)v,x,y);
	}
	Log.i("doubleTap: ", " tap n = " + doubleTap + " cur card = " + currentCard);
}

private void turnCard(Button button,int x, int y) {
	if((doubleTap%2) == 0) {
		button.setBackgroundDrawable(images.get(cards[x][y]));
	} else {
		button.setBackgroundDrawable(backImage);
		firstCard = null;
		return;
	}
	//playSelectedSound();
	if(firstCard==null){
		firstCard = new Card(button,x,y);
	}
	else{ 	
		if(firstCard.x == x && firstCard.y == y){
			return;
		}
		Log.i("doubleTap: ", " tap n = " + doubleTap + " cur card = " + currentCard);
		seconedCard = new Card(button,x,y);	
		
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
			//try{
			synchronized (lock) {
				handler.sendEmptyMessage(0);
			}
			//}
			//catch (Exception e) {
			//	Log.e("E1", e.getMessage());
			//}
			}
		};
			
		Timer t = new Timer(false);
		t.schedule(tt, 300); // delay when swap icon
	}
  }
}

class UpdateCardsHandler extends Handler{

@Override
public void handleMessage(Message msg) {
	synchronized (lock) {
		checkCards();
	}
}
@SuppressWarnings("deprecation")
public void checkCards(){
	
	if(cards[seconedCard.x][seconedCard.y] == cards[firstCard.x][firstCard.y]){
		playCorrectSound();
		firstCard.button.setVisibility(View.INVISIBLE);
		seconedCard.button.setVisibility(View.INVISIBLE);
		
		count ++;
		if(count == (ROW_COUNT*COL_COUNT)/2){
		winDialog();
		}
	}
	else {
		seconedCard.button.setBackgroundDrawable(backImage );
		firstCard.button.setBackgroundDrawable(backImage);
	}
	
	firstCard=null;
	seconedCard=null;	
	}
}

public void playSelectedSound(){
	//mp = MediaPlayer.create(this, R.raw.click2);
	//mp.start();
}
public void playCorrectSound(){
	//mp = MediaPlayer.create(this, R.raw.correct);
	//mp.start();
}
public void playInCorrectSound(){
	//mp.start();
}
public void stopSound(){
	if(mp != null){
	mp.stop();
	mp.release();
	mp = null;
}
}
public void clickSound(){
	stopSound();
	//mp = MediaPlayer.create(this, R.raw.brickogg);
	//mp.start();
}
private void winDialog() {
	AlertDialog.Builder alert = new AlertDialog.Builder(this);
	alert.setTitle("Continue ?");
	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
		singleChoiceDialog();
	}
	}).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
			dialog.cancel();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
	})
	.show();
}

public void singleChoiceDialog(){
	final CharSequence[] items = {"Easy", "Medium", "Hard", "Exelent"};
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Select level");
	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
	
	@Override
	public void onClick(DialogInterface dialog, int item) {
		// TODO Auto-generated method stub
		//Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
		switch(item){     // Ngoai item ra thi nen set ca time khi xoay hinh, hard --> xoay nhanh
		case 0:
			newGame(4,4);	
			break;
		case 1:
			newGame(4,5);
			break;
		case 2:
			newGame(4,6);
			break;
		case 3:
			newGame(4,7);
		}
	}
	}).setPositiveButton("OK", new DialogInterface.OnClickListener() {	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
		}	
	});
	AlertDialog alert = builder.create();
	alert.show();
  }
}