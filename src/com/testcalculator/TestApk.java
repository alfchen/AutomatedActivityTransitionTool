
package com.testcalculator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Parcel;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton; 

import com.jayway.android.robotium.solo.Scroller;
import com.jayway.android.robotium.solo.Solo;
import com.jayway.android.robotium.solo.WebElement;




@SuppressWarnings("unchecked")
public class TestApk extends ActivityInstrumentationTestCase2{
	
	//need to change!!
	//appname
	//TARGET_PACKAGE_ID
	//LAUNCHER_ACTIVITY_FULL_CLASSNAME!!
	//SPLASHSCREEN_SLEEPTIME
	//WITHSPLASH
	//manifest!!
	private static final String TAG = "ROBOT_ALF";
	private static final String STATTAG = "ROBOT_ALF_STAT";
	private static final String appname = "actNewEgg"; 
	//actWebMD
	//actChase
	//actTaxCaster
	//actAmazon
	//actGmail
	//actHotel
	//actHRBlock
	//actFacebook
	//actNewEgg
	private static final String activityRecordFileFolder="/sdcard/activityRecord/"+appname+"/";
	private static final String TARGET_PACKAGE_ID="com.newegg.app";
	//com.chase.sig.android
	//com.webmd.android
	//com.intuit.mobile.taxcaster
	//com.amazon.mShop.android
	//com.google.android.gm
	//com.hcom.android
	//com.hrblock.blockmobile
	//com.facebook.katana
	private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME="com.newegg.app.activity.home.Main";
	//com.webmd.android.WebMDMainActivity
	//com.intuit.mobile.taxcaster.activity.MainActivity
	//com.amazon.mShop.home.HomeActivity
	//com.google.android.gm.ConversationListActivityGmail
	//com.hcom.android.modules.menu.presenter.MenuActivity
	//com.hrblock.blockmobile.MainScreen
	//com.chase.sig.android.activity.HomeActivity
	//com.facebook.katana.activity.faceweb.FacewebChromeActivity
	//com.newegg.app.activity.home.HomeStartActivity
	//com.newegg.app.activity.home.Main
	private static int SPLASHSCREEN_SLEEPTIME=4000;
	//8000
	//4000 for newegg
	//2000
	boolean CREATMODE=false;
	boolean ATTACKMODE=true;
	boolean WITHSPLASH=false;

	boolean HUMANMODE=false;
	boolean MANUALGRAPHGENONLY=false;

	//for list testing
	boolean LISTVIEWCLICKMODE=false;
	
	
	//graph generation parameters
	boolean ONLYCONSIDERCLICKABLE=false;
	boolean CONSIDERTRIGGER=false;	
	boolean CONSIDERLISTVIEWCHILD=true;
	private static int CLICK_SLEEPTIME=3000;//2000;
	private static int WEBVIEW_COMPLETE=95;
	private static double DRAWSTATRATIO=0.74; 
	private static double INTERACTSTATRATIO=0.84;
	private static boolean donotConsiderClickAndGoBack=true;
	
	//graph generation parameters && graph traverse
	boolean APPROXIMATEINTERACTCMP=false;
	//true: amazon
	boolean STRONGSIGNATURECMP_INTERACTSTATE=false;
	
	//graph traverse
	private static double GOBACKRARIO=0.2;
	private static double DORESUME=0.1;
	boolean RANDOMNESS=true;
	boolean LAUNCHCONFIRM=false;
	

//	private ArrayList<ArrayList<Integer>> Traces;

	private ArrayList<eventRecord> eventPath;
	private ArrayList<activityNode> activityPath;
	private ArrayList<imViewStateForDFS> imViewState;
	private static int actionCnt;
	private static int RIGHT=720;
	private static int BOTTOM=1280;
	private static int WAIT_LOADING=3000;
	private static int RECTIFY_SCROLL=1500;
	private static int TOLERABLE_LISTLEN=15;
	private static int DRAW_STATE=0;
	private static int INTERACT_STATE=1;
	private static int BACKLIMIT=5;

	private static final String textFillFilename="textFillData";
	private static final String traverseFilename="traverseData";
	private static final String transMapFilename="transitionMap";
	private static final String groundTruthFilename="groundTruthData";
	private static final String reportErrorFilename="reportError";
	private static final String outputFileFoleder="/sdcard/activityTransStat/"+appname+"/";
	private static final String statFilename="statinfo";
	private static final String doneActivitiesListFilename="doneActivitiesList";
	private static Class launcherActivityClass;
	private  HashMap<String,activityRecord> activityData;
	private  HashMap<String,ActivityInfo> actInfo;
	private  ArrayList<textFill> textFillData;
	private ArrayList<activityNode> doneActivities;
	private  HashMap<String,statData> activityStat;
	ArrayList<String> activityWithData;
	Activity lastestActivity;
	ActivityInfo[] acts;
	Activity tabact;
	
	
	long lastmtime=-1;
	private static int PERFIDLETIME=60000;

	
	
	static{
		try
		{
			launcherActivityClass=Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
	//		backupActivityClass=Class.forName(BACKUP_ACTIVITY_FULL_CLASSNAME);
		} catch (ClassNotFoundException e){
			throw new RuntimeException(e);
		}
	}
	public TestApk()throws ClassNotFoundException{
		super(TARGET_PACKAGE_ID,launcherActivityClass);
	}
	private Solo solo;
	@Override
	protected void setUp() throws Exception
	{

		solo = new Solo(getInstrumentation(),getActivity());

		
	//	setTrace();
	}
	
	class statData{
		HashMap<Integer,Integer> onCreate;
//		HashMap<Integer,HashMap<Integer, Integer>> onResume;
		public statData(){
			onCreate=new HashMap<Integer,Integer>();
	//		onResume=new HashMap<Integer,HashMap<Integer, Integer>>();
		}
	}
	class intentSerial implements java.io.Serializable{
		Intent it;
	}
	class event{
		//0-click 1-scroll 2-key 3-input 4-list 5-web 6-clickAndBack 7-autofill 8-startactivity
		int eventid;
		int viewid;
		int keyno;
		//DONOT delete direction-- you will regret it soon
		int direction;
		int listviewline;
		float x1,y1;
		int scrollx, scrolly;
		int fillmode;
		String taract;
//		float x2,y2;
		
		//
		public String toString(){
			if (eventid==0)
			    return "[click on <"+x1+","+y1+">]";
			if (eventid==1)
				return "[scroll to ("+scrollx+","+scrolly+") in <"+viewid+">]";
			if (eventid==2)
				return "[click on key <"+keyno+">]";
			if (eventid==3)
				return "[input text in <"+viewid+">]";
			if (eventid==4){
				if (listviewline!=-1)
				   return "[click on line "+listviewline+" in <"+viewid+">]";
				else 
				   return "[click on random line in <"+viewid+">]";
			}
			if (eventid==5)
				return "[click webview in <"+viewid+">]";
			if (eventid==6)
				return "[do a event(too complicated to clarify:)) and back to last activity instance]";
			if (eventid==7)
				return "[Auto Fill the current view ("+fillmode+")]";
			if (eventid==8)
				return "[start activity "+taract+" with intent]";
			return "[unknown]";
		}
		public String outputEvent(){
			if (eventid==0){
				return eventid+";"+x1+";"+y1;
			}
			if (eventid==1){
				return eventid+";"+scrollx+";"+scrolly+";"+viewid;
			}
			if (eventid==2){
				return eventid+";"+keyno;
			}
			if (eventid==3){
				return eventid+";"+viewid;
			}
			if (eventid==4){
				return eventid+";"+listviewline+";"+viewid;
			}
			if (eventid==5){
				return eventid+";"+viewid;
			}
			if (eventid==6){
				return eventid+";"+direction+";"+x1+";"+y1+";"+scrollx+";"+scrolly+";"+viewid+";"+keyno+";"+listviewline+";"+fillmode;
			}
			if (eventid==7){
				return eventid+";"+fillmode;
			}
			if (eventid==8){
				return eventid+";"+taract;
			}
			return "[unknown]";
		}
        public void inputEvent(String str){
			String[] inp=str.split(";");
			
			this.eventid=Integer.parseInt(inp[0]);
			if (eventid==0){
				x1=Float.parseFloat(inp[1]);
				y1=Float.parseFloat(inp[2]);
			}
			if (eventid==1){
				scrollx=Integer.parseInt(inp[1]);
				scrolly=Integer.parseInt(inp[2]);
				viewid=Integer.parseInt(inp[3]);
			}
			if (eventid==2){
				keyno=Integer.parseInt(inp[1]);
			}
			if (eventid==3){
				viewid=Integer.parseInt(inp[1]);
			}
			if (eventid==4){
				listviewline=Integer.parseInt(inp[1]);
				viewid=Integer.parseInt(inp[2]);
			}
			if (eventid==5){
				viewid=Integer.parseInt(inp[1]);
			}
			if (eventid==6){
				if (inp.length==3){
				//old version
				  x1=Float.parseFloat(inp[1]);
				  y1=Float.parseFloat(inp[2]);
				  direction=0;
				}
				else {
					direction=Integer.parseInt(inp[1]);
					x1=Float.parseFloat(inp[2]);
					y1=Float.parseFloat(inp[3]);
					scrollx=Integer.parseInt(inp[4]);
					scrolly=Integer.parseInt(inp[5]);
					viewid=Integer.parseInt(inp[6]);
					keyno=Integer.parseInt(inp[7]);
					listviewline=Integer.parseInt(inp[8]);
					fillmode=Integer.parseInt(inp[9]);
				}
			}
			if (eventid==7){
				fillmode=Integer.parseInt(inp[1]);
			}
			if (eventid==8){
				taract=inp[1];
			}
		}
        public int compareTo(event e1){
        	if (this.eventid!=e1.eventid) return -1;
        	if (this.eventid==0){
        		if (this.x1!=e1.x1) return -1;
        		if (this.y1!=e1.y1) return -1;
        		return 0;
        	}
        	if (this.eventid==1){
        		if (this.scrollx!=e1.scrollx) return -1;
        		if (this.scrolly!=e1.scrolly) return -1;
        		if (this.viewid!=e1.viewid) return -1;
        		return 0;
        	}
        	if (this.eventid==2){
        		if (this.keyno!=e1.keyno) return -1;
        		return 0;
        	}
        	if (this.eventid==3){
        		if (this.viewid!=e1.viewid) return -1;
        		return 0;
        	}
        	if (this.eventid==4){
        		if (this.viewid!=e1.viewid) return -1;
        		if (this.listviewline!=e1.listviewline) return -1;
        		return 0;
        	}
        	if (this.eventid==5){
        		if (this.viewid!=e1.viewid) return -1;
        		return 0;
        	}
        	if (this.eventid==6){
        		if (this.direction!=e1.direction) return -1;
        		if (this.x1!=e1.x1) return -1;
        		if (this.y1!=e1.y1) return -1;
        		if (this.viewid!=e1.viewid) return -1;
        		if (this.listviewline!=e1.listviewline) return -1;
        		if (this.fillmode!=e1.fillmode) return -1;
        		if (this.keyno!=e1.keyno) return -1;
        		if (this.scrollx!=e1.scrollx) return -1;
        		if (this.scrolly!=e1.scrolly) return -1;
        		return 0;
        	}
        	if (this.eventid==7){
        		if (this.fillmode!=e1.fillmode) return -1;
        		return 0;
        	}
        	if (this.eventid==8){
        		if (!this.taract.equals(e1.taract)) return -1;
        		return 0;
        	}
        	return -1;
        }
	}

	
/*	private void setTrace(){
		
		ActivityTraces=new HashMap<String, ArrayList<ArrayList<Integer>>>();
		//com.webmd.android.WebMDMainActivity
		ArrayList<ArrayList<Integer>> traces=new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> trace1=new ArrayList<Integer>();

		
		trace1=new ArrayList<Integer>();
		//symptomCheckerButton
		trace1.add(0x7f09012b);
		traces.add(trace1);
		
		//contidions
		trace1.add(0x7f09012e);
		traces.add(trace1);
		
		trace1=new ArrayList<Integer>();
		//drugsButton
		trace1.add(0x7f09012f);
		traces.add(trace1);
		
		trace1=new ArrayList<Integer>();
		//firstAidButton
		trace1.add(0x7f090130);
		traces.add(trace1);
		
		trace1=new ArrayList<Integer>();
		//localHealthListingsButton
		trace1.add(0x7f090131);
		traces.add(trace1);
		
		trace1=new ArrayList<Integer>();
		//signupButton
		trace1.add(0x7f09012c);
		traces.add(trace1);
		
		trace1=new ArrayList<Integer>();
		//settingsButton
		trace1.add(0x7f09012d);
		traces.add(trace1);
		
		ActivityTraces.put("com.webmd.android.WebMDMainActivity", traces);
		
		
	}*/
	
	double getRandomDouble(){
		if (RANDOMNESS)
			return Math.random();
		return 0.6;
	}
	
	public boolean isInThisApp(Activity act){
		if (getCurrentViewState().size()!=0 && actInfo.get(act.getClass().getName())!=null)
			return true;

		Log.d(TAG,"not in the current app! size: "+getCurrentViewState().size()+" act:"+act.getClass().getName());
		return false;
	}
	public View getAScrollViewFromCurrentScreen(){
		ArrayList<View> allview=solo.getCurrentViews();
		for (int i=0;i<allview.size();i++){
			View vi=allview.get(i);
			if (vi instanceof ScrollView && vi.getId()==-1){
				return vi;
			}			
		}
		return null;
	}
	public View getAListViewFromCurrentScreen(){
		ArrayList<View> allview=solo.getCurrentViews();
		for (int i=0;i<allview.size();i++){
			View vi=allview.get(i);
			if (vi instanceof ListView && vi.getId()==-1){
				return vi;
			}			
		}
		return null;
	}
	public View getAWebViewFromCurrentScreen(){
		ArrayList<View> allview=solo.getCurrentViews();
		for (int i=0;i<allview.size();i++){
			View vi=allview.get(i);
			if (vi instanceof WebView && vi.getId()==-1){
				return vi;
			}			
		}
		return null;
	}
	public View getViewFromCurrentScreen(int viewid){
		ArrayList<View> allview=solo.getCurrentViews();
		for (int i=0;i<allview.size();i++){
			View vi=allview.get(i);
			if (vi.getId()==viewid && considerView(vi,null)){
				return vi;
			}			
		}
		return null;
	}
	
/*	public void clickOnView(View vi, event evt){
		
		solo.clickOnScreen(x,y);
		evt.x1=x;
		evt.y1=y;
		solo.sleep(CLICK_SLEEPTIME);
	}
	*/
	public event generateClickEvent(View vi){
		event et=new event();
		et.eventid=0;
		
		int[] xy = new int[2];
		vi.getLocationOnScreen(xy);
		int viewWidth = vi.getWidth();
		int viewHeight = vi.getHeight();
		et.x1 = xy[0] + (viewWidth / 2.0f);
		et.y1 = xy[1] + (viewHeight / 2.0f);
		
		return et;		
	}
	public event generateScrollEvent(ScrollView svi, int sx, int sy){
		event et=new event();
		et.eventid=1;
		et.scrollx=sx;
		et.scrolly=sy;
	//	et.direction=direction;
		et.viewid=svi.getId();
		return et;
	}
	public event generateKeyEvent(int key){
		event et=new event();
		et.eventid=2;
		et.keyno=key;
		return et;
	}
	public event generateTextInputEvent(EditText etvi){
		event et=new event();
		et.eventid=3;
		et.viewid=etvi.getId();
		return et;
	}
	public event generateListEvent(ListView lvi, int line){
		event et=new event();
		et.eventid=4;
		et.listviewline=line;
		et.viewid=lvi.getId();
		return et;
	}
	public event generateWebViewEvent(WebView wvi){
		event et=new event();
		et.eventid=5;
		et.viewid=wvi.getId();
		return et;
	}
	public event generateClickAndBackEvent(event evt){	
		event et=new event();
		et.eventid=6;
		et.direction=evt.eventid;		

		et.x1=evt.x1;
		et.y1=evt.y1;
		et.scrollx=evt.scrollx;
		et.scrolly=evt.scrolly;
		et.viewid=evt.viewid;
		et.keyno=evt.keyno;
		et.listviewline=evt.listviewline;
		et.fillmode=evt.fillmode;
		
		
		return et;
	}
	public event generateAutoFillEvent(int fm){
		event et=new event();
		et.eventid=7;
		et.fillmode=fm;
		return et;
	}
	public event generateStartActivityEvent(String tact){
		event et=new event();
		et.eventid=8;
		et.taract=tact;
		return et;
	}
	
	
    int decideListViewType(String viewsig){
    //same: 0, different: 1
    	Log.d(TAG,"view sig: "+viewsig);
    	ListView lvi=(ListView)(findViewBySignature(viewsig,-1));
    	Log.d(TAG,"decide list view type of "+lvi.getId());//+" "+lvi.getCount()+" "+lvi.getChildCount()+" "+lvi.getFirstVisiblePosition());
		int sameThres=5;
    	int trials=sameThres*2;
    	int pt=predeterminedType(lvi);
    	if (pt>=0) return pt;
		if (lvi.getCount()<=TOLERABLE_LISTLEN) return 1;
		if (lvi.getCount()<trials) trials=lvi.getCount();
		ArrayList<Integer> randarr=new ArrayList<Integer>();
		while (randarr.size()<trials){
			int rand=(int) (Math.random()*lvi.getCount());
			Boolean newone=true;
			for (int i=0;i<randarr.size();i++)
				if (randarr.get(i)==rand){
					newone=false;
					break;
				}
			if (newone)
				randarr.add(rand);
		}
		String preActName=null;
		ArrayList<String> preViewState=null;
		int sameEvidenceNum=0;
		boolean thesame=true;
		for (int i=0;i<randarr.size();i++){
			int pos=randarr.get(i);
			Log.d(TAG,pos+"");
			event et=generateListEvent(lvi, pos);
			ArrayList<String> curViewState=getCurrentViewState();
			executeEvent(et,false);
			ArrayList<String> newViewState=getCurrentViewState();
			if (compareInteractState(newViewState, curViewState)==0){
				Log.d(TAG,"no effect, next one!");
				continue;
			}
			String newActName=getCurrentActivity().getClass().getName();
			
			this.goBackToPos(0);
			lvi=(ListView)(findViewBySignature(viewsig,-1));
			Log.d(TAG,"still here");
			
			if (preActName!=null && !(newActName.equals(preActName)/* && compareDrawState(newViewState, preViewState)==0*/)){
				Log.d(TAG,newActName+"!!"+preActName);
		//		Log.d(TAG,newViewState.toString());
		//		Log.d(TAG,preViewState.toString());
				Log.d(TAG,"not the same!");
				thesame=false;
				break;
			}
			sameEvidenceNum++;
			if (sameEvidenceNum>=sameThres) {
				thesame=true;
				break;
			}
			preActName=newActName;
			preViewState=newViewState;
		}

		
	//	getBackToBegining();
		goBackToPos(0);
		if (thesame)
			return 0;
		return 1;
		
	}
    void clickLinkWebView(WebView wvi){
   // 	ArrayList<String> curViewState=getCurrentViewState();
		wvi.reload();		
    }
	
	Activity getCurrentActivity(){
		Activity act=solo.getCurrentActivity();
		if (act.getClass().getName().equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity")){
			tabact=act;
		}
		if (act.getClass().getName().equals("com.hcom.android.modules.hotel.details.presenter.HotelDetailsActivity")
		||	act.getClass().getName().equals("com.hcom.android.modules.hotel.rooms.presenter.HotelRoomsActivity")
		||	act.getClass().getName().equals("com.hcom.android.modules.hotel.ratings.presenter.HotelRatingsActivity")
		||	act.getClass().getName().equals("com.hcom.android.modules.hotel.photos.presenter.HotelPhotosActivity")
		||	act.getClass().getName().equals("com.hcom.android.modules.hotel.map.presenter.HotelMapActivity")
		){
			act=tabact;
		}
		return act;		
	}
	
	
	public String getLocation(View vi){
		int[] xy = new int[2];
		vi.getLocationOnScreen(xy);
		int viewWidth = vi.getWidth();
		int viewHeight = vi.getHeight();
		float x = xy[0] + (viewWidth / 2.0f);
		float y = xy[1] + (viewHeight / 2.0f);
		return "["+x+","+y+"]";
		
	}
	public ArrayList<View> getTopViewGroups(){
		ArrayList<View> res=new ArrayList<View>();
		ArrayList<View> allview=solo.getCurrentViews();
	   
		for (int i=0;i<allview.size();i++){
			View tvi=allview.get(i);
			View ptvi=solo.getTopParent(tvi);
			
			boolean inset=false;
			for (int j=0;j<res.size();j++)
				if (res.get(j)==ptvi){
				   inset=true;
				   break;
				}
			if (!inset)
				res.add(ptvi);
		}
		return res;
	}
	
	String viewFields(View vi){
	  return 	vi.getClass().getName()+" visbile: "+vi.getVisibility()+" location: "+getLocation(vi);
	}
	
/*	Boolean considerViewGroup(ViewGroup tvgi){
		int childcount=tvgi.getChildCount();
    	for (int i=0;i<childcount;i++){
    		if (considerView(ctvi))
    	}
	}
	
	*/
	void printEventPath(){
		Log.d(TAG,"eventPath:");
		for (int i=0;i<eventPath.size();i++){
			Log.d(TAG,i+": activity:"+eventPath.get(i).activity+" initState:"+eventPath.get(i).initStateNo);
			if (eventPath.get(i).action!=null)
		     Log.d(TAG,"  ---action:"+eventPath.get(i).action.toString());
		}
	}
	
	void rectifyScroller(ArrayList<Integer> scrollSet,event newevt){
		
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		
		
		
		HashMap<Integer, Integer> vidSet=new HashMap<Integer, Integer>();
		if (newevt!=null)
			if (newevt.eventid==1 && (vidSet.get(newevt.viewid)==null || vidSet.get(newevt.viewid)!=1)){
				View vi=null;
				if (newevt.viewid==-1)
					vi=getAScrollViewFromCurrentScreen();
				else vi=getViewFromCurrentScreen(newevt.viewid);				
				if (vi!=null){
					vidSet.put(newevt.viewid, 1);
					
					while (!(vi.getScrollX()==newevt.scrollx && vi.getScrollY()==newevt.scrolly)){
					  if (solo.scroller.scrollScrollToView((ScrollView)vi, newevt.scrollx, newevt.scrolly)==false)
						  break;
					  solo.sleep(RECTIFY_SCROLL);
					}
				}
				
			}
		int pos=eventPath.size()-1;
		while (pos>0){
			if (eventPath.get(pos).activity.equals(curAct.toString())){
				event evt=eventPath.get(pos).action;
				if (evt.eventid==1 && (vidSet.get(evt.viewid)==null || vidSet.get(evt.viewid)!=1)){
					View vi=null;
					if (evt.viewid==-1)
						vi=getAScrollViewFromCurrentScreen();
					else vi=getViewFromCurrentScreen(evt.viewid);	
					if (vi!=null){
						vidSet.put(evt.viewid, 1);
						
						while (!(vi.getScrollX()==evt.scrollx && vi.getScrollY()==evt.scrolly)){
						  if (solo.scroller.scrollScrollToView((ScrollView)vi, evt.scrollx, evt.scrolly)==false)
							  break;
						  solo.sleep(RECTIFY_SCROLL);
						}
					}
					
				}
			}			
			pos--;
		}
		//set those without scrolling to 0,0
		for (int i=0;i<scrollSet.size();i++){
			int viewid=scrollSet.get(i);
			if (vidSet.get(viewid)==null || vidSet.get(viewid)!=1){
				View vi=null;
				if (viewid==-1)
					vi=getAScrollViewFromCurrentScreen();
				else vi=getViewFromCurrentScreen(viewid);	
				if (vi!=null){					
					while (!(vi.getScrollX()==0 && vi.getScrollY()==0)){
					  if (solo.scroller.scrollScrollToView((ScrollView)vi, 0, 0)==false)
						  break;
					  solo.sleep(RECTIFY_SCROLL);
					}
				}
				
			}
		}
	}
	
	//incomplete
	int getEverythingReadyAfterEvent(event newevt, boolean keepkeyboard){
	//	getCurrentActivity()
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		
		
		//special wait
		if (curActName.equals("com.chase.sig.android.activity.FindBranchActivity")){
			solo.sleep(3000);
		}
				
				
		Date dd=new Date();
		long mtime=dd.getTime();
		
		
	//	solo.getInstrumentation().waitForIdleSync();
		ArrayList<Integer> scrollSet=new ArrayList<Integer>();
		boolean finished=false;
		while (!finished){
	        
			if (curActName.equals("com.hrblock.blockmobile.appointmentmanager.appointments.activities.SelectTimeActivity")){		
				solo.clickOnScreen(360,80);
			}
			
			
			finished=true;
		    ArrayList<View> allview=getTopViewGroups();
		    ArrayList<View> vq=new ArrayList<View>();
		    vq.addAll(allview);
	        if (vq.size()>0){
	    	    int front=0,rear=vq.size();
	    	    while (front<rear){
	    	    	View tvi=vq.get(front);
	    	    	front++;
	    	    	if (tvi instanceof ViewGroup){
	    	    //		Log.d(TAG,"--root:"+viewFields(tvi));
	    	    		ViewGroup tvgi=(ViewGroup)tvi;
	    	    		int childcount=tvgi.getChildCount();
	    		    	for (int i=0;i<childcount;i++){
	    		    		View ctvi=tvgi.getChildAt(i);
	    		    		if (considerView(ctvi,tvgi)) {
	    		  //  			Log.d(TAG,viewFields(ctvi));
	    			    		vq.add(ctvi);
	    			    		rear++;
	    		    		}	    		
	    		    	}
	    	    	}
	    	    	if (considerView(tvi,null)){
	    	    	//view signature    
	    	    		if (tvi instanceof ScrollView){
	    	    			scrollSet.add(tvi.getId());
	    	    			
	    	    		}
	    	    		
	    	    		
	    	    		if (tvi instanceof ProgressBar 
	    	    				&& !(tvi instanceof RatingBar)
	    	    				&& !(tvi instanceof android.widget.SeekBar)
	    	    			    && !(tvi.getId()==2131361941 && curActName.equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity"))
	    	    			    && !(tvi.getId()==2131361944 && curActName.equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity"))
	    	    			    && !(tvi.getId()==2131361947 && curActName.equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity"))
	    	    			    && !(tvi.getId()==2131361950 && curActName.equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity"))
	    	    			    && !(tvi.getId()==2131361953 && curActName.equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity"))
	    	    			    && !(tvi.getId()==2131361956 && curActName.equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity"))
	    	    				){
	    	    			Log.d(TAG,"prograss bar on!"+getViewSignature(tvi));
	    	    			finished=false;
	    	    			solo.sleep(WAIT_LOADING);
	    	    			break;
	    	    		}
	    	    		if (tvi instanceof WebView){
	    	    			WebView wvi=(WebView)tvi;
	    	    			if (wvi.getProgress() < WEBVIEW_COMPLETE){
	    	    				Log.d(TAG,"webview "+wvi.getProgress()+"");
		    	    			finished=false;
		    	    			break;
	    	    			}
	    	    		}
	    		    		
	    	    	}
	    	    	
	    	   }
	        }
	        if (!finished){
	        	solo.sleep(WAIT_LOADING);
	    		this.getInstrumentation().waitForIdleSync();
	        }
	        Date dd1=new Date();
			long mtime1=dd1.getTime();			
			if (mtime1-mtime>10000 && !CREATMODE){
			//go back
				return -1;
			}
		}
		Log.d(TAG,"out!");
		//rectify scroller
		
		Log.d(TAG, "activity: "+curActName);
		if (!(curActName.equals("com.amazon.mShop.search.SearchActivity") 
				||	curActName.equals("com.amazon.mShop.search.image.MShopContinuousScanActivity")
				||	curActName.equals("com.amazon.mShop.android.search.image.SnapItPhotoCaptureActivity")
				||  curActName.equals("com.amazon.mShop.android.search.image.SnapItPhotoPreviewActivity")
				||  curActName.equals("com.hcom.android.modules.web.presenter.OpinionLabEmbeddedBrowserActivity")
				||  curActName.equals("com.chase.sig.android.activity.QuickDepositReviewImageActivity")
				||  curActName.equals("com.chase.sig.android.activity.QuickDepositCaptureActivity"))){
			
			//is soft keyboard on
			if (!keepkeyboard) {
				while (isSoftKeyboardOn()){
				//keyboard is on
					Log.d(TAG, "SOFTKEYBOARD is ON, get rid of it!");
					Log.d(TAG, "go back 1");
					executeGoBack();
				}
			}
		}
		
		
		
		
		
        
		if (newevt!=null)
		 rectifyScroller(scrollSet,newevt);
		return 0;
//		Log.d(TAG, ""+rect);
		//1280-836=
    //    int statusBarHeight = rect.top;
    //    int screenHeight = getCurrentActivity().getWindowManager().getDefaultDisplay().getHeight();
   //     int diff = (screenHeight - statusBarHeight) - height;

	/*	while (imm.isAcceptingText()){
			Log.d(TAG, "SOFTKEYBOARD is OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOON!!");
			executeGoBack();
		}
		*/
//			Log.d(TAG, "SOFTKEYBOARD is OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOON!!");
		//	imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
		
		
	}
	boolean isSoftKeyboardOn(){
		InputMethodManager imm = (InputMethodManager)getCurrentActivity().getSystemService(Context.INPUT_METHOD_SERVICE);			
		Rect rect=new Rect();
		getCurrentActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
		return (BOTTOM-rect.bottom > 128);
	}
	
	void afterEventTrace(){
	//do somethign after executing a event trace.
		if (TARGET_PACKAGE_ID.equals("com.chase.sig.android")){
			//special case for chase
				View vi=getViewFromCurrentScreen(2131296534);
				if (vi!=null && ifViewSignatureSame(getViewSignature(vi),"android.widget.Button,[505.5,770.0],2131296534,negativeButton,Log Off,",0)){
					solo.clickOnScreen((float)505.5,(float)770.0);
					solo.sleep(CLICK_SLEEPTIME);
				}
				else 
				{
					vi=getViewFromCurrentScreen(2131296532);
					if (vi!=null && ifViewSignatureSame(getViewSignature(vi),"android.widget.Button,[214.5,728.0],2131296532,positiveButton,Cancel,",0)){
						solo.clickOnScreen((float)214.5,(float)728.0);
						solo.sleep(CLICK_SLEEPTIME);
					}
					
				}
				
				
				
			}
	}
	
	int autoFillTextForCurrentView(){
		//fill text
		int fillsth=0;
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		ArrayList<View> allview=getTopViewGroups();
	    ArrayList<View> vq=new ArrayList<View>();
	    vq.addAll(allview);
        if (vq.size()>0){
    	    int front=0,rear=vq.size();
    	    while (front<rear){
    	    	View tvi=vq.get(front);
    	    	front++;
    	    	if (tvi instanceof ViewGroup
    		    		){
    	    		ViewGroup tvgi=(ViewGroup)tvi;
    	    		int childcount=tvgi.getChildCount();
    		    	for (int i=0;i<childcount;i++){
    		    		View ctvi=tvgi.getChildAt(i);
    		    		if (considerView(ctvi,tvgi)) {
    			    		vq.add(ctvi);
    			    		rear++;
    		    		}	    		
    		    	}
    	    	}
    	    	if (considerView(tvi,null)){
    	    		if (tvi instanceof EditText){
    	    			fillsth+=fillAppropriateText(curAct, tvi.getId(), (EditText)tvi);
    	    		}
    	    	}
    	    }
        }
        return fillsth;
	}
	
	int autoFillView(int mode){
	//0-fill everywhere 1-fill everywhere and go back 2-fill the current view (no scrolling)
		int totalFillNo=0;
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		ArrayList<Integer> scrollSet=new ArrayList<Integer>();
		//get scroll views
		ArrayList<View> allview=getTopViewGroups();
	    ArrayList<View> vq=new ArrayList<View>();
	    vq.addAll(allview);
        if (vq.size()>0){
    	    int front=0,rear=vq.size();
    	    while (front<rear){
    	    	View tvi=vq.get(front);
    	    	front++;
    	    	if (tvi instanceof ViewGroup){
    	    		ViewGroup tvgi=(ViewGroup)tvi;
    	    		int childcount=tvgi.getChildCount();
    		    	for (int i=0;i<childcount;i++){
    		    		View ctvi=tvgi.getChildAt(i);
    		    		if (considerView(ctvi,tvgi)) {
    			    		vq.add(ctvi);
    			    		rear++;
    		    		}	    		
    		    	}
    		    	if (tvi instanceof ScrollView)
    		    		scrollSet.add(tvi.getId());
    	    	}
    	    }
        }
        if (mode!=2){
        	for (int i=0;i<scrollSet.size();i++){        	
            	ScrollView svi=(ScrollView)getViewFromCurrentScreen(scrollSet.get(i));
            	solo.scroller.scrollScrollViewAllTheWay(svi, Scroller.UP);
        	}
        }        
                	
    	do{
    		totalFillNo+=autoFillTextForCurrentView();
    		//other stuff
    		//if the curActName is ***, do things other than edit text
    		if (curActName.equals("com.webmd.android.activity.healthlisting.search.PhysicianSearchActivity")){
    			View vi;
    			vi=getViewFromCurrentScreen(2131296380);
    		   if (vi!=null){
    			//physician's last name
    				event evt=generateClickEvent(vi);
    				executeEvent(evt,false);
    				autoFillView(1);
    			//	totalFillNo+=1;
    			}
                vi=getViewFromCurrentScreen(2131296381);
    			if (vi!=null){
    			//speciality
    				event evt=generateClickEvent(vi);
    				executeEvent(evt,false);
    				autoFillView(1);
    			//	totalFillNo+=1;
    			}
    		}
    /*		if (curActName.equals("com.webmd.android.activity.healthlisting.search.PharmacySearchActivity")){
    			View vi;
    			vi=getViewFromCurrentScreen(2131296380);
    		   if (vi!=null){
    			//Pharmacy name
    				event evt=generateClickEvent(vi);
    				executeEvent(evt,false);
    				autoFillView(1);
    			}                
    		}
    		if (curActName.equals("com.webmd.android.activity.healthlisting.search.HospitalSearchActivity")){
    			View vi;
    			vi=getViewFromCurrentScreen(2131296380);
    		   if (vi!=null){
    			//Hospital name
    				event evt=generateClickEvent(vi);
    				executeEvent(evt,false);
    				autoFillView(1);
    			}                
    		}
    		*/
    		if (curActName.equals("com.webmd.android.activity.healthlisting.search.PhysicianSpecialtyActivity")){
    			View vi=getViewFromCurrentScreen(2131296373);
    			if (vi!=null){
    			//list
    				event evt=generateListEvent((ListView)vi, 0);
    				executeEvent(evt,false);
    			//	totalFillNo+=1;
    		//		return;
    			}
    		}
    		if (curActName.equals("com.webmd.android.activity.symptom.SymptomCheckerSettingActivity")){
    			View vi=getViewFromCurrentScreen(2131296523);
    			if (vi!=null){
    			//toggleButton
    				while (!((ToggleButton)vi).isChecked()){
    					event evt=generateClickEvent(vi);
        				executeEvent(evt,false);
    				}
    			//	totalFillNo+=1;
    			}
    		}
    		if (curActName.equals("com.hcom.android.modules.search.form.presenter.SearchFormActivity")){
    			View vi=getViewFromCurrentScreen(2131362139);
    			if (vi!=null){
    			//toggleButton
    				event evt=generateClickEvent(vi);
    				executeEvent(evt,false);
    			//	totalFillNo+=1;
    			}
    		}
    		if (curActName.equals("com.chase.sig.android.activity.QuickPaySendMoneyActivity")){
    			View vi=getViewFromCurrentScreen(2131296831);
    			if (vi!=null){
    				event evt=generateClickEvent(vi);
    				executeEvent(evt,false);
    				autoFillView(1);
    			//	totalFillNo+=1;
    			}
    		}
    		if (curActName.equals("com.chase.sig.android.activity.QuickPayRequestMoneyActivity")){
    			View vi=getViewFromCurrentScreen(2131296831);
    			if (vi!=null){
    				event evt=generateClickEvent(vi);
    				executeEvent(evt,false);
    				autoFillView(1);
    			//	totalFillNo+=1;
    			}
    		}
    		if (curActName.equals("com.chase.sig.android.activity.QuickPayChooseRecipientActivity")){
    			View vi=getViewFromCurrentScreen(2131296821);//
    			if (vi!=null){
    				event evt=generateListEvent((ListView)vi, 0);
    				executeEvent(evt,false);
    				vi=getViewFromCurrentScreen(2131296826);//
        			if (vi!=null){
        				evt=generateClickEvent(vi);
        				executeEvent(evt,false);
        			}
    				
    			//	Log.d(TAG,getCurrentActivity().toString());
    			//	totalFillNo+=1;
    			}
    		}
            
    		
    		if (mode==2)
    			break;
    		boolean allstop=true;
    		for (int i=0;i<scrollSet.size();i++){ 
            	ScrollView svi=(ScrollView)getViewFromCurrentScreen(scrollSet.get(i));
            	if (solo.scroller.scrollScrollView(svi, Scroller.DOWN))
            	 allstop=false;
        	}
    		if (allstop)
    			break;
    	}while (true);
    	
    	if (mode==1){
    	//go back here
    		if (curActName.equals("com.webmd.android.activity.healthlisting.search.NameInputActivity")){
    			event evt=generateKeyEvent(solo.ENTER);
				executeEvent(evt,false);
    		//	solo.sendKey(solo.ENTER);
    			return 0;
    		}
    		if (curActName.equals("com.webmd.android.activity.healthlisting.search.PhysicianSpecialtyActivity")){
    			return 0;
    		}
    		
    		if (curActName.equals("com.webmd.android.activity.dob.DateOfBirthActivity")){
    			View vi=getViewFromCurrentScreen(2131296325);
    			if (vi!=null){
    				event evt=generateClickEvent(vi);
    				executeEvent(evt,false);
    				return 0;
    			}
    		}
    		if (curActName.equals("com.webmd.android.activity.drug.ByImprintActivity")){
    			event evt=generateKeyEvent(solo.ENTER);
				executeEvent(evt,false);
    			return 0;    			
    		}
    	}
    	
    	Log.d(TAG, curActName);
    	Log.d(TAG, getCurrentViewState().toString());
    	solo.sleep(5000);
    	return totalFillNo;
	}
	
	int fillAppropriateText(Activity curAct, int viewid, EditText editview){
		String curActName=curAct.getClass().getName();
		if (curActName.equals("com.webmd.android.activity.signup.SignUpActivity")){
			return 0;
		}
		if (curActName.equals("com.webmd.android.activity.signin.SignInActivity")){
			return 0;
		}
		if (curActName.equals("com.webmd.android.activity.email.EmailActivity")){
			return 0;
		}
		if (curActName.equals("com.webmd.android.activity.info.AppSettingsActivity")){
			return 0;
		}
		if (curActName.equals("com.google.android.gm.ComposeActivityGmail")){
			return 0;
		}
		if (curActName.contains("com.amazon.mShop")){
			if (viewid==2131624605)
				return 0;
		}
		if (curActName.contains("com.amazon.mShop.android.account.LoginActivity")){
			return 0;
		}
		if (curActName.equals("com.amazon.mShop.android.cart.CartActivity")){
			return 0;
		}
		if (curActName.equals("com.hcom.android.modules.authentication.signin.presenter.SignInActivity")){
			return 0;
		}
		if (curActName.equals("com.hcom.android.modules.reservation.form.presenter.ReservationFormActivity")){
			return 0;
		}
		if (curActName.equals("com.hrblock.blockmobile.gua.BlockMobileSpecificLoginActivity")){
			return 0;
		}
		if (curActName.equals("com.hrblock.blockmobile.gua.BlockMobileSpecificLoginActivity")){
			return 0;
		}
		if (curActName.equals("com.hrblock.blockmobile.taxestimator.activities.TaxEstimatorActivity")){
			return 0;
		}
		if (curActName.equals("com.hcom.android.modules.search.form.presenter.SearchFormActivity")){
			return 0;
		}
		if (curActName.equals("com.hcom.android.modules.authentication.signin.presenter.SignInActivity")){
			return 0;
		}
		if (curActName.equals("com.hcom.android.modules.reservation.form.presenter.ReservationFormActivity")){
			return 0;
		}
		if (curActName.equals("com.hcom.android.modules.register.step2.presenter.RegistrationStep2Activity")){
			return 0;
		}
		if (curActName.equals("com.newegg.app.activity.more.AppFeedBackActivity")){
			return 0;
		}
		if (curActName.equals("com.newegg.app.activity.product.ProductWriteReviewActivity")){
			return 0;
		}
		if (curActName.equals("com.newegg.app.activity.product.ProductPriceAlertActivity")){
			return 0;
		}
		if (curActName.equals("com.newegg.app.activity.login.ShoppingLoginActivity")){
			return 0;
		}
		if (curActName.equals("com.newegg.app.activity.checkout.CheckoutActivity")){
			return 0;
		}
		if (curActName.equals("com.newegg.app.activity.myaccount.ShippingAddressEditActivity")){
			return 0;
		}
		if (curActName.equals("com.newegg.app.activity.myaccount.BillingAddressEditActivity")){
			return 0;
		}
		if (curActName.equals("com.newegg.app.activity.myaccount.PaymentOptionModifyActivity")){
			return 0;
		}
		
	/*	if (curActName.equals("com.hcom.android.modules.search.filter.presenter.SearchHotelFiltersActivity")){
			return;
		}
		*/
		
		String appText=null;
		Log.d(TAG,"textfill size:"+textFillData.size());
		for (int i=0;i<textFillData.size();i++){
	/*		Log.d(TAG,textFillData.get(i).actName+" "+curActName+" "+textFillData.get(i).viewsignature+" "+getViewSignature(editview));
			if (editview.getHint()!=null){
				Log.d(TAG,textFillData.get(i).hint+" "+editview.getHint());
			}*/
			if (textFillData.get(i).actName.equals(curActName) && ifViewSignatureSame(textFillData.get(i).viewsignature, getViewSignature(editview),2) && (editview.getHint()==null || textFillData.get(i).hint.equals(editview.getHint().toString()))){
				
				appText=textFillData.get(i).theText;
		        break;
			}
		}
		if (appText!=null){
			String before=editview.getText().toString();
			solo.textEnterer.setEditTextNoAppend(editview, appText);
			if (!before.equals(editview.getText().toString()))				
			  return 1;
			return 0;
		}
	/*	if (curActName.equals("com.webmd.android.activity.healthlisting.search.NameInputActivity")){
			solo.enterText(editview, "Chen");
			solo.sendKey(solo.ENTER);
		}
		*/
	/*	if (curActName.equals("")){
			if (viewid==){
				return "";
			}
		}*/
		String before=editview.getText().toString();
		try {
			String visign=getViewSignature(editview);
			String hint="";
			if (editview.getHint()!=null)
				hint=editview.getHint().toString();
			Log.d(TAG,"don't know how to input! "+visign+" in "+curActName+" "+hint);		
			event cevt=this.generateClickEvent(editview);
			executeEvent(cevt, true);
		//	this.tearDown();
			while (isSoftKeyboardOn()) ;
			Log.d(TAG,"get a "+editview.getText().toString()+"---");
		//	while (true);
			textFillData.add(new textFill(curActName,visign,editview.getText().toString(),hint));
			Log.d(TAG,"testFillData size: "+textFillData.size());
		} catch (Exception e) {
			Log.d(TAG,"testFillData exception? "+e.toString());
			e.printStackTrace();
		}
		if (!before.equals(editview.getText().toString()))				
			  return 1;
		return 0;
		
	}
	boolean blockedActivities(String actName){
		if (actName.equals("com.webmd.android.activity.symptom.SymptomCheckerSettingActivity"))
			return true;
		if (actName.equals("com.amazon.mShop.android.AmazonChooserActivity"))
			return true;
		return false;
	}
	
	Boolean specialBlock(View vi){
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		
		//application specific block list
		if (curActName.equals("com.webmd.android.activity.dob.DateOfBirthActivity")){
			if (vi.getId()==16909109)
			//increase
				return false;
			if (vi.getId()==16909111)
			//decrease
				return false;
		}
		if (curActName.equals("com.webmd.android.activity.symptom.SymptomCheckerSettingActivity")){
			if (vi.getId()==2131296522)
			//female button
				return false;
			if (vi.getId()==2131296523)
			//male button
				return false;
		}
		if (curActName.equals("com.webmd.android.activity.info.AppSettingsActivity")){
			if (vi.getId()==2131296280)
				return false;
			if (vi.getId()==2131296281)
				return false;
		}
		if (curActName.equals("com.google.android.gm.ui.MailActivityGmail")){
			if (vi.getId()==2131231000)
			//archive
				return false;
			if (vi.getId()==2131231002)
				//delete email
			    return false;
			if (vi.getId()==2131231005)
				//mark unread email
			    return false;
			if (vi.getId()==2131231013)
			//refresh
				return false;
			if (vi.getId()==2131230884)
			//webview
				return false;
			if (vi.getId()==2131230868)
			//contact photo
				return false;
			if (vi.getId()==2131230869)
			//photo_spacer
				return false;
			if (vi.getId()==2131230807)
			//star,
				return false;
			if (vi.getId()==2131230808)
			//folders,.Inbox,
				return false;
			if (vi.getId()==2131230840)
			//	to_heading,To:
				return false;
			if (vi.getId()==2131230879)
			//sender_name
				return false;
			if (vi.getId()==2131230877)
			//upper_date
				return false;
			if (vi.getId()==2131230832)
			//date_value,
				return false;
			if (vi.getId()==2131230834)
			//from_heading
				return false;
			if (vi.getId()==2131230841)
			//to_value
				return false;
			if (vi.getId()==2131230835)
			//from_value
			    return false;
			if (vi.getId()==2131230881)
			//sender_email,
				return false;
			if (vi.getId()==2131230829)
			//date_summary
				return false;
			if (vi.getId()==2131230828)
			//recipients_summary
				return false;
			if (vi instanceof android.widget.ImageView && getLocation(vi).equals("[672.0,354.0]") && vi.getId()==-1)
				return false;
			if (vi.getId()==16908332)
			//home
				return false;
			if (vi.getId()==2131230838)
			//replyto_value,
				return false;
			if (vi.getId()==2131230892)
			//scroll_indicators
				return false;
			if (vi.getId()==2131230831)
			//date_heading
				return false;
			if (vi.getId()==2131230824 || vi.getId()==2131230818 || vi.getId()==2131230820  || vi.getId()==2131230819)
			//attachment
			return false;

		}
		if (curActName.equals("com.google.android.gm.ComposeActivityGmail")){
			if (vi.getId()==16908929)
				return true;
			return false;
		//	return false;
		}
		if (curActName.equals("com.google.android.gm.preference.GmailPreferenceActivity")){
			if (vi.getId()==16908870 || vi.getId()==16908332)
				//,up, home
					return false;
		}
		if (curActName.contains("com.amazon.mShop")){
			if (vi.getId()==2131624037 && !getViewSignature(vi).equals("com.amazon.mShop.android.details.BuyButtonView,[360.0,970.5],2131624037,buy_button,Buy Now,"))
				//buy button
					return false;
		}
		if (curActName.equals("com.amazon.mShop.android.cart.CartActivity")){
			if (vi.getId()==2131624077 || vi.getId()==2131624076)
				return false;
			
		}
		if (curActName.equals("com.amazon.mShop.history.HistoryActivity")){
			if (vi.getId()==2131623976)
				return false;
			
		}
		if (curActName.equals("com.hcom.android.modules.settings.common.presenter.SettingsActivity")){
			if (vi.getId()==2131362225)
				return false;
			if (vi.getId()==2131362226)
				return false;
			if (vi.getId()==2131362227)
				return false;
			if (vi.getId()==2131362228)
				return false;
		}
		if (curActName.equals("com.hcom.android.modules.info.rateapp.presenter.RateAppActivity")){
			if (vi.getId()==2131361987)
				return false;
		}
		if (curActName.equals("com.amazon.mShop.remembers.RemembersActivity")){
			if (vi.getId()==2131624578)
				return false;
				
		}
		if (curActName.equals("com.hrblock.blockmobile.gua.BlockMobileSpecificLoginActivity")){
			if (vi.getId()==2131034390)
				return false;
		}
		if (curActName.equals("com.hrblock.blockmobile.taxestimator.activities.TaxEstimatorActivity")){
			if (vi.getClass().getName().equals("com.viewpagerindicator.TabPageIndicator$TabView"))
			  return false;
			if (vi.getClass().getName().equals("android.widget.TextView"))
				return false;
			if (vi.getClass().getName().equals("android.widget.EditText"))
				return false;
			if (vi.getId()==2131034135)
				return false;
			if (vi.getId()==2131034431)
				return false;			
		}
				
		if (curActName.equals("com.hrblock.blockmobile.appointmentmanager.findoffice.activities.FindOfficeActivity")){
			if (getViewSignature(vi).equals("com.android.internal.view.menu.ActionMenuItemView,[406.0,98.0],0,,"))
			  return false;
		}
		if (curActName.equals("com.hrblock.blockmobile.wheresmyrefund.activities.AcquireRefundInfoActivity")){
			if (vi.getId()==2131034431)
				return false;
		}
		if (curActName.equals("com.hrblock.blockmobile.appointmentmanager.appointments.activities.SelectTimeActivity")){
			if (vi.getId()==2131034426 || vi.getId()==2131034428)
				return false;
		}
		
		if (curActName.equals("com.hrblock.blockmobile.appointmentmanager.taxpro.activities.TaxProListActivity")){
			if (vi.getId()==16908298)
			    return false;			
			
		}
		
        if (curActName.equals("com.hrblock.blockmobile.checklist.activities.ChecklistActivity")){
        	if (vi.getId()==2131034431)
        		return false;
        	if (vi.getClass().getName().equals("android.widget.CheckedTextView"))
				return false;
			if (vi.getClass().getName().equals("android.widget.TextView"))
				return false;
			if (getViewSignature(vi).equals("android.widget.Button,[88.5,536.0],-1,Email,"))
				  return false;
		}
        if (curActName.equals("com.hrblock.blockmobile.helpcenter.instantanswers.activities.QAListActivity")){
			if (getViewSignature(vi).equals("com.android.internal.view.menu.ActionMenuItemView,[518.0,98.0],0,,"))
			    return false;			
			
		}
        if (curActName.equals("com.hrblock.blockmobile.helpcenter.instantanswers.activities.CategoryListActivity")){
			if (getViewSignature(vi).equals("com.android.internal.view.menu.ActionMenuItemView,[518.0,98.0],0,,"))
			    return false;			
			
		}
		if (curActName.equals("com.hrblock.blockmobile.helpcenter.glossary.activities.GlossaryListActivity")){
			if (getViewSignature(vi).equals("com.android.internal.view.menu.ActionMenuItemView,[518.0,98.0],0,,"))
			    return false;			
			
		}
		if (curActName.equals("com.hcom.android.modules.search.filter.presenter.SearchHotelFiltersActivity")){
			if (vi.getId()==2131362120)
        		return false;
			if (vi.getId()==2131362122)
				return false;
			if (vi.getClass().getName().equals("com.hcom.android.common.widget.CheckBoxCenter"))
				return false;
			if (vi.getClass().getName().equals("android.widget.CheckBox"))
				return false;
		}
		if (curActName.equals("com.hcom.android.modules.search.landmarks.presenter.LandmarkFilterActivity")){
			if (vi.getClass().getName().equals("android.widget.CheckedTextView"))
				return false;
			if (vi.getClass().getName().equals("android.widget.TextView"))
				return false;
			
			
		}
		if (curActName.equals("com.hcom.android.modules.search.morefilters.presenter.MoreFiltersActivity")){
			if (vi.getClass().getName().equals("android.widget.CheckedTextView"))
				return false;
			if (vi.getClass().getName().equals("android.widget.TextView"))
				return false;
		}
		
		if (curActName.equals("com.hcom.android.modules.search.form.presenter.SearchFormActivity")){
			if (vi.getId()==2131362150)
				return false;
			if (vi.getId()==2131362149 || vi.getId()==2131362144 || vi.getId()==2131362145)
				return false;			
			if (vi.getId()==2131362139)
				return false;
		}
		if (curActName.equals("com.hcom.android.modules.register.step1.presenter.RegistrationStep1Activity")){
			if (vi.getId()==2131362019)
				return false;
			if (vi.getId()==16908308)
				return false;
			if (vi.getId()==2131362027)
				return false;
			if (vi.getId()==2131362028)
				return false;
			if (vi.getClass().getName().equals("android.widget.Spinner"))
				return false;
		}
/*		if (vi.getId()==2131361831){
			return false;
		}
	*/	
		if (curActName.equals("com.hcom.android.modules.search.form.presenter.SearchFormActivity")){
			if (vi.getId()==2131362140)
				return false;
		}
		if (curActName.equals("com.chase.sig.android.activity.LoginActivity")){
			if (vi.getId()==2131296598)
				return false;
		}
		
		if (curActName.equals("com.chase.sig.android.activity.BillPayAddStartActivityR2")){
			if (vi.getId()==2131296977)
				return false;
		}
		
		if (curActName.equals("com.chase.sig.android.activity.BillPayAddVerifyActivityR2")){
				return false;
		}
		
		
		if (curActName.equals("com.chase.sig.android.activity.QuickDepositReviewImageActivity")){
			if (vi.getId()==2131296312)
				return false;
		}
		if (curActName.equals("com.chase.sig.android.activity.BillPayPayeeAddVerifyActivity")){
			if (vi.getId()==2131296339)
				//add payee
				return false;
		}
		if (curActName.equals("com.chase.sig.android.activity.BillPayPayeeEditActivity")){
			if (vi.getId()==2131296340)
				//delete payee
				return false;
		}
		if (curActName.equals("com.chase.sig.android.activity.BillPayPayeeEditVerifyActivity")){
			if (vi.getId()==2131296342)
				//done
				return false;
		}
		if (curActName.equals("com.chase.sig.android.activity.QuickPaySendMoneyActivity")){
			if (vi.getId()==2131296850)
				//clear
				return false;
		}

		if (curActName.equals("com.chase.sig.android.activity.QuickPaySendMoneyVerifyActivity")){
			if (vi.getId()==2131296854)//2131296854
				return false;
		}
		if (curActName.equals("com.chase.sig.android.activity.QuickPayRequestMoneyActivity")){
			if (vi.getId()==2131296850)//2131296854
				return false;
		}
		if (curActName.equals("com.chase.sig.android.activity.QuickPayAddRecipientActivity")){
			if (vi.getId()==2131296779)	
				//clear
				return false;
			if (vi.getId()==2131296780)			
			    //add
				return false;
		}
		
		if (curActName.equals("com.chase.sig.android.activity.QuickPayAddRecipientActivity")){
			if (vi.getId()==2131296779)	
				//clear
				return false;
			if (vi.getId()==2131296780)			
			    //add
				return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.home.Main")){
			if (vi.getId()==2131166396)	
				//search menu
				return false;
			
		}
		
		if (curActName.equals("com.newegg.app.activity.myaccount.MyAccountActvity")){
			if (vi.getId()==2131165736)	
				//log out button
				return false;
			
		}
		
		if (curActName.contains("com.newegg.app")){
			if (vi.getId()==2131166145)	{
			//voice search
				return false;				
			}
			if (vi.getId()==2131166146)	{
			//scan search
				return false;
				
			}
			if (vi.getId()==2131166396)	
				//search menu
				return false;
			
			if (vi.getId()==16908870)	
				//up
				return false;
			if (vi.getId()==16908332)	
				//home
				return false;
			if (vi.getId()==16908877)	
				//title
				return false;
			
			if (getViewSignature(vi).contains("android.widget.TextView,[567.0,81.0],-1,1,"))
				//cart
				return false;
			
			
		}
		
		if (curActName.equals("com.newegg.app.activity.cart.ShoppingCartActivity")){
			if (vi.getId()==2131166294)	
				//quantity
				return false;
			
			if (vi.getId()==2131166201)	
				//update button
				return false;
			if (vi.getId()==2131166295)	
				//remove button
				return false;
			
			if (vi.getId()==2131166296)	
				//add wish list button
				return false;
			
			
		}
		if (curActName.equals("com.newegg.app.activity.product.ProductActivity")){
			if (vi.getId()==2131165993)	
				//return policy
				return false;
			
			if (vi.getId()==2131166002)	
				//add to wish list
				return false;
			
			if (vi.getId()==2131165999)	
				//add to cart
				return false;
			if (vi.getId()==2131166007)	
				//add to personal homepage
				return false;
			
			if (getViewSignature(vi).contains("android.widget.TextView,[643.5,176.5],-1,OVERVIEW,"))
				//back to overview
				return false;
			
			
		}
		
		if (curActName.equals("com.newegg.app.activity.setting.SettingActivity")){
			if (vi.getId()==2131166163)	
				//search google tv
				return false;
			if (vi.getId()==2131166153)	
				//notification
				return false;
			if (vi.getId()==2131166161)	
				//confirm app exit
				return false;
			
			
		}
		
		if (curActName.equals("com.newegg.app.activity.more.AppFeedBackActivity")){
			if (vi.getId()==2131165343)	
				//send
				return false;
			if (vi.getId()==2131165342)	
				//send
				return false;
			
			if (vi.getId()==2131165722)
				//anonymous send
				return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.setting.ChangeCountryActivity")){
			if (vi.getId()==2131166165)	
				//canada
				return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.wishlist.WishListSettingActivity")){
			if (vi.getId()==2131166395)	
				//canada
				return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.wishlist.EditWishListActivity")){
			if (vi.getId()==2131166406)	
				//delete
				return false;
			
			if (vi.getId()==2131165341)	
				//cancel
				return false;
			
			if (getViewSignature(vi).contains("android.widget.TextView,[180.0,1232.0],-1,Cancel,"))
			    //cancel
			    return false;
			
			
		}
		if (curActName.equals("com.newegg.app.activity.product.ProductWriteReviewActivity")){
			//nothing to do!
			return false;
			
		}
		
		
		if (curActName.equals("com.newegg.app.activity.product.ProductPriceAlertActivity")){
			//nothing to do!
			return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.cart.ShoppingCartCalculateShippingActivity")){
			//nothing to do!
			return false;
		}
		
		
		if (curActName.equals("com.newegg.app.activity.login.ShoppingLoginActivity")){
			if (vi.getId()==2131165700 
					|| getViewSignature(vi).contains("baa,[360.0,689.0],-1,Sign in with Google")
					|| getViewSignature(vi).contains("android.view.View,[360.0,665.0],-1,")
					|| getViewSignature(vi).contains("android.view.View,[360.0,689.0],-1,"))
				//sign in with google
				return false;
			
			
			if (vi.getId()==2131165341
					|| getViewSignature(vi).contains("android.widget.TextView,[180.0,1232.0],-1,Cancel,"))
				//cancel
				return false;
			
			if (vi.getId()==2131165698)
				//remember password
				return false;
			
			if (vi.getId()==2131165694)
				//sign up
			    return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.checkout.CheckoutActivity")){
			if (getViewSignature(vi).contains("android.widget.TextView,[334.5,1210.0],-1,SUBMIT ORDER,")
			  || getViewSignature(vi).contains("android.view.View,[512.0,1210.0],-1,"))
				//submit
				return false;
			if (vi.getId()==2131165376)
				//rushbox
			    return false;
			
		}
		
		if (curActName.equals("com.newegg.app.activity.myaccount.AddressBookForCheckoutActivity")){
			if (vi.getId()==2131166395)
				//plus
			    return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.checkout.CheckoutCalculateShippingActivity")){
			return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.myaccount.ShippingAddressEditActivity")){
			return false;
		}
		if (curActName.equals("com.newegg.app.activity.myaccount.BillingAddressEditActivity")){
			return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.myaccount.PaymentOptionsActivity")){
			if (vi.getId()==2131166395)
			//plus
		    return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.myaccount.PaymentOptionModifyActivity")){
			return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.masterpass.MasterPassForShoppingCartActivity")){
			return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.paypal.PayPalForMyCartActivity")){
			return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.eblast.LastestEmailDealsActivity")){
			if (vi.getId()==2131165683)
				//sort by
			    return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.browse.SearchResultActivity")){
			if (vi.getId()==2131165329)
				//sort by
			    return false;
			if (vi.getId()==2131165325)
				//compare
			    return false;
			
		}
		if (curActName.equals("com.newegg.app.activity.order.OrderHistoryActivity")){
			if (vi.getId()==2131165589)
				//find record
			    return false;
			if (vi.getId()==2131165587)
				//search
			    return false;
			
		}
		
		if (curActName.equals("com.newegg.app.activity.home.MyPersonalHomeCustomization")){
			if (getViewSignature(vi).contains("android.widget.CheckBox")
			  || (getViewSignature(vi).contains("android.view.View")
					  && getViewSignature(vi).contains("],-1")))
			//all config
					return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.home.MyPersonalHomeRecentHistoryActivity")){
			if (getViewSignature(vi).contains("android.widget.CheckBox")
					  || (getViewSignature(vi).contains("android.view.View")
							  && getViewSignature(vi).contains("],-1")))
			//all choices
					return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.home.MyPersonalHomeSearchHistory")){
			if (getViewSignature(vi).contains("android.widget.CheckBox")
					  || (getViewSignature(vi).contains("android.view.View")
							  && getViewSignature(vi).contains("],-1")))
			//all choices
					return false;
		}
		if (curActName.equals("com.newegg.app.activity.home.MyPersonalHomeCategory")){
			if (getViewSignature(vi).contains("android.widget.CheckBox")
					  || (getViewSignature(vi).contains("android.view.View")
							  && getViewSignature(vi).contains("],-1")))
			//all choices
					return false;
		}
		if (curActName.equals("com.newegg.app.activity.home.MyPersonalHomeSubCategory")){
			if (getViewSignature(vi).contains("android.widget.CheckBox")
					  || (getViewSignature(vi).contains("android.view.View")
							  && getViewSignature(vi).contains("],-1")))
			//all choices
					return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.myaccount.AddressBookForMyAccountActivity")){
			if (vi.getId()==2131166395)
				//plus
			    return false;
		}
		
		if (curActName.equals("com.newegg.app.activity.rma.RmaHistoryActivity")){
			if (vi.getId()==2131165589)
				//find order
			    return false;
			if (vi.getId()==2131165587)
				//search
			    return false;
		}
		
		
		
		return true;
	}
	
	Boolean considerView(View vi,ViewGroup vgi){
		if (vi.getVisibility()!=View.VISIBLE)
		  return false;
	/*	if (vgi!=null && vi!=null && getViewSignature(vgi).equals(getViewSignature(vi)))
		//no circle
			return false;
		*/
		Rect outRect=new Rect(0,0,RIGHT,BOTTOM);
		if (vgi!=null)
			vgi.getHitRect(outRect);
		
		return vi.getLocalVisibleRect(outRect);
	}

	Boolean considerListView(int viewid, int pos, int childcnt){
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		if (curActName.equals("com.google.android.gm.ui.MailActivityGmail")){
		  if (viewid==16908929){
			  if (pos==0 || pos==1 || pos==2 || pos==3)
				  return false;			  
		  }
		}
		if (curActName.equals("com.google.android.gm.ComposeActivityGmail")){
			if (viewid==16908929){
				  if (pos==0 || pos==1 || pos==2 || pos==3 || pos==4)
					  return false;			  
			  }
		}
		if (curActName.equals("com.google.android.gm.LabelsActivity")){
			if (viewid==16908298){
					  return false;			  
			  }
		}
		if (curActName.equals("com.google.android.gm.preference.GmailPreferenceActivity")){
			
				if (viewid==16908298){
					if (childcnt==3){
						if (pos==0)
							return false;
					}
					
				  if (childcnt==9){
					  if (pos==0 || pos==1 || pos==3 || pos==4 || pos==5 || pos==6 || pos==8)
						  return false;			
				  }
				}
				if (viewid==16908929){
					
						  return false;			
				}
			}
		if (curActName.equals("com.amazon.mShop.search.SearchActivity")){
			if (viewid==16909165)
				return false;
		}
		if (curActName.equals("com.amazon.mShop.localeswitch.MShopLocaleSwitchActivity")){
		//com.amazon.mShop.android.localeswitch.LocaleSwitchView,[360.0,736.0],-1,10,
			return false;
		}
		if (curActName.equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity")){
			if (viewid==16909165)
			return false;
		}
		if (curActName.equals("com.hcom.android.modules.search.landmarks.presenter.LandmarkFilterActivity")){
			if (viewid==2131362163)
			return false;
		}
		if (curActName.equals("com.hcom.android.modules.search.morefilters.presenter.MoreFiltersActivity")){
			if (viewid==2131362153)
			return false;
		}
		if (curActName.equals("com.hcom.android.modules.search.form.presenter.SearchFormActivity")){
			if (viewid==131362140)
			return false;
		}
		if (curActName.equals("com.hcom.android.modules.settings.country.presenter.CountrySelectActivity")){
			if (viewid==16908298)
			return false;
		}

		
		
		
		
		
		return true;		
	}
	int predeterminedType(ListView lvi){
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		if (curActName.equals("com.amazon.mShop.search.SearchActivity")){
			if (lvi.getId()==2131624252)
			return 0;
		}
		if (curActName.equals("com.hrblock.blockmobile.appointmentmanager.findoffice.activities.FindOfficeActivity")){
			if (lvi.getId()==16908298)
			return 0;
		}
		
		if (curActName.equals("com.hrblock.blockmobile.appointmentmanager.taxpro.activities.TaxProListActivity")){
			if (lvi.getId()==16908298)
			return 0;
		}
		if (curActName.equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity")){
			if (lvi.getId()==16908298)
			return 0;
		}
		
		if (curActName.equals("com.newegg.app.activity.eblast.LastestEmailDealsActivity")){
			if (lvi.getId()==2131165684)
			return 0;
		}
		
         if (curActName.equals("com.newegg.app.activity.eblast.SubEblastFilterActivity")){
        	 if (lvi.getId()==2131165524)
     			return 0;
		}
         
        if (curActName.equals("com.newegg.app.activity.home.ShellShockerActivity")){
        	 if (lvi.getId()==2131165656)
     			return 0;
		}
        
        if (curActName.equals("com.newegg.app.activity.browse.SearchResultActivity")){
       	 if (lvi.getId()==2131165330)
    			return 0;      	
       
		}
        
        if (curActName.equals("com.newegg.app.activity.home.EggExtraActivity")){
       	 if (lvi.getId()==2131165656)
    			return 0;
		}
        if (curActName.equals("com.newegg.app.activity.browse.BrowseSearchFilterActivity")){
       	 if (lvi.getId()==2131165302)
    			return 0;
		}
        
        if (curActName.equals("com.newegg.app.activity.browse.SubCategoryActivity")){
          	 if (lvi.getId()==2131165339)
       			return 0;
          	 
          	 if (lvi.getId()==2131165337)
        			return 0;
          	 
          	
   		}
        
        if (curActName.equals("com.newegg.app.activity.browse.MoreCategoryActivity")){
        	if (lvi.getId()==2131165289)
        		return 1;
        }
        
        if (curActName.equals("com.newegg.app.activity.home.MyPersonalHomeCategory")){
        	if (lvi.getId()==2131165619)
        		return 0;
        }
        
        if (curActName.equals("com.newegg.app.activity.home.MyPersonalHomeSubCategory")){
        	if (lvi.getId()==2131165619)
        		return 0;
        }
        
        
		return -1;
	}
	boolean blockedScroll(ScrollView svi){
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		if (curActName.equals("com.hrblock.blockmobile.MainScreen")){
		  if (getViewSignature(svi).equals("android.widget.ScrollView,[360.0,713.0],-1,")){
			return true;
		  }
		}
		if (curActName.equals("com.hcom.android.modules.hotel.tabs.presenter.HotelDetailsTabsActivity")){
			return true;
		}
		if (curActName.equals("com.hcom.android.modules.search.landmarks.presenter.LandmarkFilterActivity")){
			return true;
		}
		if (curActName.equals("com.hcom.android.modules.search.morefilters.presenter.MoreFiltersActivity")){
			return true;
		}
		if (curActName.equals("com.newegg.app.activity.product.ProductActivity")){
			return true;
		}
		return false;
	}
	
	boolean forwardLandingBlockResume(Activity act){
		if (act.getClass().getName().equals("com.newegg.app.activity.home.Main")){
			return true;
		}
		return false;
	}
	
	ArrayList<String> getFullCurrentViewState(){
		ArrayList<String> resviewstate=new ArrayList<String>();
	    ArrayList<View> allview=getTopViewGroups();
	    ArrayList<View> vq=new ArrayList<View>();
	    vq.addAll(allview);
        if (vq.size()>0){
    	    int front=0,rear=vq.size();
    	    while (front<rear){
    	    	View tvi=vq.get(front);
    	    	front++;
    	    	if (tvi instanceof ViewGroup){
    	    		Log.d(TAG,"--root:"+viewFields(tvi));
    	    		ViewGroup tvgi=(ViewGroup)tvi;
    	    		int childcount=tvgi.getChildCount();
    		    	for (int i=0;i<childcount;i++){
    		    		View ctvi=tvgi.getChildAt(i);
    		    		if (considerView(ctvi,tvgi)) {
    		    			Log.d(TAG,viewFields(ctvi));
    			    		vq.add(ctvi);
    			    		rear++;
    		    		}	    		
    		    	}
    	    	}
    	    	if (considerView(tvi,null)){
    	    	//view signature    			
    		    		resviewstate.add(getViewSignature(tvi));
    	    	}
    	    	
    	   }
        }

	    return resviewstate;
	}
	ArrayList<String> getCurrentViewState(){
		ArrayList<String> resviewstate=new ArrayList<String>();
	    ArrayList<View> allview=getTopViewGroups();
	    ArrayList<View> vq=new ArrayList<View>();
	    vq.addAll(allview);
        if (vq.size()>0){
    	    int front=0,rear=vq.size();
    	    while (front<rear){
    	    	View tvi=vq.get(front);
    	    	front++;
    	    	if (tvi instanceof ViewGroup
    		    	&& !(tvi instanceof ListView)
    		    		){
    	    //		Log.d(TAG,"--root:"+viewFields(tvi));
    	    		ViewGroup tvgi=(ViewGroup)tvi;
    	    		int childcount=tvgi.getChildCount();
    		    	for (int i=0;i<childcount;i++){
    		    		View ctvi=tvgi.getChildAt(i);
    		    		if (considerView(ctvi,tvgi)) {
    		  //  			Log.d(TAG,viewFields(ctvi));
    			    		vq.add(ctvi);
    			    		rear++;
    		    		}	    		
    		    	}
    	    	}
    	    	if (considerView(tvi,null)){
    	    	//view signature    			
    	    		
    		    		
    		    	/*	if (tvi instanceof WebView){
    	    				StringBuilder sb=new StringBuilder();
    			    		sb.append(tvi.getClass().getName()+",");
    			    		sb.append(getLocation(tvi)+",");
    			    		resviewstate.add(sb.toString());
    			    		
    	    				ArrayList<WebElement> wal=solo.getCurrentWebElements();
    	    				Log.d(TAG,"#: "+wal.size());
    	    				WebElement we=null;
    	    				for (int i=0;i<wal.size();i++){
    	    				//	if (wal.get(i).getTagName().equals("A")) {
    	    				    //link
    	    					Log.d(TAG,wal.get(i).getText()+" "+wal.get(i).getLocationX()+","+wal.get(i).getLocationY());
    	    					if (we==null)
    	    					   we=wal.get(i);
    	    					//}
    	    				}
    	    				Log.d(TAG,"click:"+we.getText());
    	    				solo.clickOnWebElement(we);
    	    				
    	    			}*/
    		    		resviewstate.add(getViewSignature(tvi));
    	    	}
    	    	
    	   }
        }

	    return resviewstate;
	}
	
/*	public ArrayList<View> getCurrentViewState(){
	    ArrayList<View> allview=getTopViewGroups();
	    ArrayList<View> vq=new ArrayList<View>();
	    vq.addAll(allview);
        if (vq.size()>0){
    	    int front=0,rear=vq.size();
    	    while (front<rear){
    	    	View tvi=vq.get(front);
    	    	front++;
    	    	if (tvi instanceof ViewGroup
    		  //  	&& !(tvi instanceof ListView)
    		    		){
    	    //		Log.d(TAG,"--root:"+viewFields(tvi));
    	    		ViewGroup tvgi=(ViewGroup)tvi;
    	    		int childcount=tvgi.getChildCount();
    		    	for (int i=0;i<childcount;i++){
    		    		View ctvi=tvgi.getChildAt(i);
    		    		if (considerView(ctvi,tvgi)) {
    		  //  			Log.d(TAG,viewFields(ctvi));
    			    		vq.add(ctvi);
    			    		rear++;
    		    		}	    		
    		    	}
    	    	}
    	    	if (considerView(tvi,null)){
    	    	//view signature    			
    	    		
    		    		
    		    		resviewstate.add(getViewSignature(tvi));
    	    	}
    	    	
    	   }
        }

	    return vq;
	}
	*/
	
	
	public ArrayList<View> getCurrentViewStateInBranch(View vi){
	    ArrayList<View> allview=getTopViewGroups();
	    ArrayList<View> vq=new ArrayList<View>();
	    if (vi==null) vq.addAll(allview);
	    else vq.add(vi);
        if (vq.size()>0){
    	    int front=0,rear=vq.size();
    	    while (front<rear){
    	    	View tvi=vq.get(front);
    	    	front++;
    	    	if (tvi instanceof ViewGroup
    		    	&& !(tvi instanceof ListView)
    		    		){
    	    //		Log.d(TAG,"--root:"+viewFields(tvi));
    	    		ViewGroup tvgi=(ViewGroup)tvi;
    	    		int childcount=tvgi.getChildCount();
    		    	for (int i=0;i<childcount;i++){
    		    		View ctvi=tvgi.getChildAt(i);
    		    		if (considerView(ctvi,tvgi)) {
    			    		vq.add(ctvi);
    			    		rear++;
    		    		}	    		
    		    	}
    	    	}

    	    	
    	   }
        }

	    return vq;
	}
	
	public ArrayList<String> getCurrentViewStateStringInBranch(View vi){
		ArrayList<String> resviewstate=new ArrayList<String>();
	    ArrayList<View> allview=getTopViewGroups();
	    ArrayList<View> vq=new ArrayList<View>();
	    if (vi==null) vq.addAll(allview);
	    else vq.add(vi);
        if (vq.size()>0){
    	    int front=0,rear=vq.size();
    	    while (front<rear){
    	    	View tvi=vq.get(front);
    	    	front++;
    	    	if (tvi instanceof ViewGroup
    		    	&& !(tvi instanceof ListView)
    		    		){
    	    //		Log.d(TAG,"--root:"+viewFields(tvi));
    	    		ViewGroup tvgi=(ViewGroup)tvi;
    	    		int childcount=tvgi.getChildCount();
    		    	for (int i=0;i<childcount;i++){
    		    		View ctvi=tvgi.getChildAt(i);
    		    		if (considerView(ctvi,tvgi)) {
    			    		vq.add(ctvi);
    			    		resviewstate.add(getViewSignature(ctvi));
    			    		rear++;
    		    		}	    		
    		    	}
    	    	}

    	    	
    	   }
        }

	    return resviewstate;
	}
	
	boolean compareViewSignature(String sig1, String sig2){
	//used for interact state only
		String[] segsig1=sig1.split(",");
		String[] segsig2=sig2.split(",");


		if (segsig1.length!=segsig2.length)
			return false;
		

		if (segsig1.length>0){
			if (!segsig1[0].equals(segsig2[0]))
			//class name
				return false;

			if (!segsig1[1].equals(segsig2[1]))
			//location x
				return false;

			if (!segsig1[2].equals(segsig2[2]))
			//location y
				return false;
			
			if (!segsig1[3].equals(segsig2[3]))
			//id
				return false;

			if (STRONGSIGNATURECMP_INTERACTSTATE){
				for (int i=4;i<segsig1.length;i++)
					if (!segsig1[i].equals(segsig2[i]))
							return false;
			}
			else {
			   if (!segsig1[0].equals("android.widget.ListView")){
				   for (int i=4;i<segsig1.length;i++)
						if (!segsig1[i].equals(segsig2[i]))
								return false;
			   }
				
			}
		}
		
		return true;
	}
	
	String getViewSignature(View tvi){
		if (tvi==null)
			return "";
		StringBuilder sb=new StringBuilder();
		sb.append(tvi.getClass().getName()+",");
		sb.append(getLocation(tvi)+",");
		sb.append(tvi.getId()+",");
	//	sb.append(tvi.isScrollContainer()+",");
		String resourcename=null;
		try{
			resourcename=tvi.getResources().getResourceEntryName(tvi.getId());		   
		}
		catch (Resources.NotFoundException rf){
		   //no big deal
		}
		if (resourcename!=null){
			sb.append(resourcename+",");
		}
		if (tvi instanceof ListView){
			sb.append(((ListView)tvi).getCount()+",");
		}
		if (tvi instanceof TextView){
			if (tvi instanceof EditText){
				
			}
			else 
			sb.append(((TextView)tvi).getText()+",");
		}
		if (tvi instanceof ToggleButton)
			sb.append(((ToggleButton)tvi).isChecked()+",");
		if (tvi instanceof RadioButton)
			sb.append(((RadioButton)tvi).isChecked()+",");
		if (tvi instanceof Spinner){
			sb.append(((Spinner)tvi).getCount()+",");
			sb.append(((Spinner)tvi).getSelectedItemId()+",");
		}
		return sb.toString().replaceAll("(\\r|\\n)", "");
	//	return tvi.toString();
	}
	boolean ifViewSignatureSame(String sig1, String sig2, int cmplevel){
	//cmplevel: 0 -- totally same, x -- first x parts are the same
		if (cmplevel==0)
			return sig1.equals(sig2);
		String[] segsig1=sig1.split(",");
		String[] segsig2=sig2.split(",");
		int cmplen=cmplevel;
		if (segsig1.length<cmplevel) cmplevel=segsig1.length;
		if (segsig2.length<cmplevel) cmplevel=segsig2.length;
		for (int i=0;i<cmplevel;i++){
			if (!segsig1[i].equals(segsig2[i]))
					return false;
		}
		return true;
		
	}
	
	class eventTrace{
	//structure for recording the event path leading to another activity
		String targetActivity;
		int maybeResume; //for outInit edge
		int targetInitStateID;
		int targetLandingStateID;
		
		//for backInit edge
		int isCreate;
		String middleActivity;
		int middleInitStateID;
		
		ArrayList<event> eventtrace;
		public eventTrace(){
			eventtrace=new ArrayList<event>();
		}
		public String toString(){			
			StringBuilder sb=new StringBuilder("{");
			sb.append(" maybeResume: "+maybeResume);
			sb.append(" isCreate: "+isCreate);
			sb.append(" targetActivity: "+targetActivity);
			sb.append(" targetInitStateID: "+targetInitStateID);
			sb.append(" targetLandingStateID: "+targetLandingStateID);
			sb.append(" middleActivity: "+middleActivity);
			sb.append(" middleInitStateID: "+middleInitStateID);
			sb.append(" trace: ");
			for (int i=0;i<eventtrace.size();i++)
				sb.append(eventtrace.get(i).toString()+",");
			sb.append("}");
			return sb.toString();
		}
		
	}
	class activityRecord{
		String name;
		ArrayList<ArrayList<String>> initState;
//		ArrayList<ArrayList<String>> interactState;
//		ArrayList<Integer> interactStateProceedings;
		ArrayList<ArrayList<ArrayList<String>>> landingState;
		ArrayList<ArrayList<eventTrace>> outInitStateOptions;
		//need careful handling with back option
		ArrayList<ArrayList<eventTrace>> backInitStateOptions;
		
		public activityRecord(String actname){
			name=actname;
			initState=new ArrayList<ArrayList<String>>();
			landingState=new ArrayList<ArrayList<ArrayList<String>>>();
	//		interactState=new ArrayList<ArrayList<String>>();
	//		interactStateProceedings=new ArrayList<Integer>();
			outInitStateOptions=new ArrayList<ArrayList<eventTrace>>();		
			backInitStateOptions=new ArrayList<ArrayList<eventTrace>>();		
		}
		public activityRecord(BufferedReader br){
			try {
				this.name=br.readLine();
				initState=new ArrayList<ArrayList<String>>();
				landingState=new ArrayList<ArrayList<ArrayList<String>>>();
				outInitStateOptions=new ArrayList<ArrayList<eventTrace>>();		
				backInitStateOptions=new ArrayList<ArrayList<eventTrace>>();		
				
				int sizei=Integer.parseInt(br.readLine());
				for (int i=0;i<sizei;i++){
					int sizej=Integer.parseInt(br.readLine());
					ArrayList<String> ds=new ArrayList<String>();
					for (int j=0;j<sizej;j++)
						ds.add(br.readLine());
					initState.add(ds);
				}
				sizei=Integer.parseInt(br.readLine());
				for (int i=0;i<sizei;i++){
					int sizej=Integer.parseInt(br.readLine());
					ArrayList<ArrayList<String>> aas=new ArrayList<ArrayList<String>>();
					for (int j=0;j<sizej;j++){
						int sizek=Integer.parseInt(br.readLine());
						ArrayList<String> as=new ArrayList<String>();
						for (int k=0;k<sizek;k++)
							as.add(br.readLine());
						aas.add(as);
					}
					landingState.add(aas);
				}
				sizei=Integer.parseInt(br.readLine());
				for (int i=0;i<sizei;i++){
					int sizej=Integer.parseInt(br.readLine());
					ArrayList<eventTrace> aet=new ArrayList<eventTrace>();
					for (int j=0;j<sizej;j++){
						eventTrace et=new eventTrace();
						et.targetActivity=br.readLine();
						et.maybeResume=Integer.parseInt(br.readLine());
						et.targetInitStateID=Integer.parseInt(br.readLine());
						ArrayList<event> ae=new ArrayList<event>();
						int sizek=Integer.parseInt(br.readLine());
						for (int k=0;k<sizek;k++){
							event e=new event();
							e.inputEvent(br.readLine());
							ae.add(e);
						}
						et.eventtrace=ae;
						aet.add(et);
					}
					outInitStateOptions.add(aet);
				}
				
				sizei=Integer.parseInt(br.readLine());
				for (int i=0;i<sizei;i++){
					int sizej=Integer.parseInt(br.readLine());
					ArrayList<eventTrace> aet=new ArrayList<eventTrace>();
					for (int j=0;j<sizej;j++){
						eventTrace et=new eventTrace();
						et.targetActivity=br.readLine();
						et.targetInitStateID=Integer.parseInt(br.readLine());
						et.targetLandingStateID=Integer.parseInt(br.readLine());
						ArrayList<event> ae=new ArrayList<event>();
						int sizek=Integer.parseInt(br.readLine());
						for (int k=0;k<sizek;k++){
							event e=new event();
							e.inputEvent(br.readLine());
							ae.add(e);
						}
						et.eventtrace=ae;
						aet.add(et);
					}
					backInitStateOptions.add(aet);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		public String toString(){
			StringBuilder sb=new StringBuilder(this.name+"\n");
			sb.append(this.initState.size()+"\n");
			for (int i=0;i<initState.size();i++){
				sb.append(this.initState.get(i).size()+"\n");
				for (int j=0;j<initState.get(i).size();j++)
					sb.append(this.initState.get(i).get(j)+"\n");				
			}
			sb.append(this.landingState.size()+"\n");
			for (int i=0;i<landingState.size();i++){
				sb.append(this.landingState.get(i).size()+"\n");
				for (int j=0;j<landingState.get(i).size();j++){
					sb.append(this.landingState.get(i).get(j).size()+"\n");
         			for (int k=0;k<landingState.get(i).get(j).size();k++){
						sb.append(landingState.get(i).get(j).get(k)+"\n");
					}
				}							
			}
			
			sb.append(this.outInitStateOptions.size()+"\n");
			for (int i=0;i<outInitStateOptions.size();i++){
				sb.append(this.outInitStateOptions.get(i).size()+"\n");
				for (int j=0;j<outInitStateOptions.get(i).size();j++){
					eventTrace et=outInitStateOptions.get(i).get(j);
					sb.append(this.outInitStateOptions.get(i).get(j).targetActivity+"\n");
					sb.append(this.outInitStateOptions.get(i).get(j).maybeResume+"\n");
					sb.append(this.outInitStateOptions.get(i).get(j).targetInitStateID+"\n");
					sb.append(this.outInitStateOptions.get(i).get(j).eventtrace.size()+"\n");
					for (int k=0;k<outInitStateOptions.get(i).get(j).eventtrace.size();k++){
						event e=outInitStateOptions.get(i).get(j).eventtrace.get(k);
						sb.append(e.outputEvent()+"\n");
					}
				}							
			}
			sb.append(this.backInitStateOptions.size()+"\n");
			for (int i=0;i<backInitStateOptions.size();i++){
				sb.append(this.backInitStateOptions.get(i).size()+"\n");
				for (int j=0;j<backInitStateOptions.get(i).size();j++){
					eventTrace et=backInitStateOptions.get(i).get(j);
					sb.append(this.backInitStateOptions.get(i).get(j).targetActivity+"\n");
					sb.append(this.backInitStateOptions.get(i).get(j).targetInitStateID+"\n");
					sb.append(this.backInitStateOptions.get(i).get(j).targetLandingStateID+"\n");
					sb.append(this.backInitStateOptions.get(i).get(j).eventtrace.size()+"\n");
					for (int k=0;k<backInitStateOptions.get(i).get(j).eventtrace.size();k++){
						event e=backInitStateOptions.get(i).get(j).eventtrace.get(k);
						sb.append(e.outputEvent()+"\n");
					}
				}							
			}
			return sb.toString();
			
		}
	}
	class eventRecord{
		String activity;
        event action;
        int initStateNo;
		int location;
		//-1:unknown 0:allsame 1:maydifferent
		int listViewType;
	
        ArrayList<String> viewState;
        public eventRecord(String act, event action, ArrayList<String> viewState, int loca){
    		this.activity=act;
    		this.action=action;
    		this.initStateNo=-1;
    		this.viewState=viewState;
    		this.location=loca;
    		this.listViewType=-1;
        }
	}
	class activityNode{
		String activity;
		int initState;
		int actLocation;
		public activityNode(String act, int is, int al){
    		this.activity=act;
    		this.initState=is;
    		this.actLocation=al;
        }
	}
	
	class imViewStateForDFS{
		ArrayList<String> viewState;
		event evt;
		String triggerViewSignature;
		public imViewStateForDFS(ArrayList<String> vs, event et, View vi){
			viewState=vs;
			evt=et;
			triggerViewSignature="";
			if (vi!=null)
				triggerViewSignature=getViewSignature(vi);
		}
		public int compareTo(imViewStateForDFS ivsfd){
			if (ivsfd==null) return -1;
			if (compareInteractState(ivsfd.viewState, this.viewState)!=0)
				return -1;
			if (CONSIDERTRIGGER){
			if (this.evt==null || ivsfd.evt==null){
				if (!(this.evt==null && ivsfd.evt==null))
					return -1;
			}
			else {
			    if (ivsfd.evt.compareTo(this.evt)==-1)
				  return -1;
			}
			if (!ivsfd.triggerViewSignature.equals(this.triggerViewSignature))
				return -1;
			}
			return 0;			
		}
	}
	class textFill{
		String actName;
		String viewsignature;
		String theText;
	    String hint;
		public textFill(String an, String visig, String txt, String ht){
			actName=an;
			viewsignature=visig;
			theText=txt;
			if (ht==null) hint="";
			else hint=ht;
			//needenter=ne;
		}
		public textFill(String input){
			String[] cols=input.split(";");
			actName=cols[0];
			viewsignature=cols[1];
			theText=cols[2];
			if (cols.length<=3)
				hint="";
			else hint=cols[3];
		//	needenter=Integer.parseInt(cols[3]);
		}
		public String toString(){
			return actName+";"+viewsignature+";"+theText+";"+hint;
		}
	}
	
/*	int findActivityLandingState(String Act, ArrayList<String> ViewState, int initLoc){
	//0: drawState
		if (activityData.get(Act)==null) return -1;
		
		activityRecord ar=activityData.get(Act);
		for (int j=0;j<ar.landingState.size();j++)
				if (compareDrawState(ar.landingState.get(initLoc).get(j),ViewState)==0)
					return j;
		return -1;
	}
	*/
	String getCurrentTime(){
		Date dd=new Date();
		long mtime=dd.getTime();
		double ct0=mtime/1000.0;
		java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");
		String ct=df.format(ct0);
		return ct;
	}
	void recordRelaunch(){
		Date dd=new Date();
		long mtime=dd.getTime();
		
		
		 try {
	 			File file = new File(outputFileFoleder+groundTruthFilename);
	 			
	 			if (!file.exists()){			
	 				file.createNewFile();
	 			}
	 			
	 			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
	 			fop.write(mtime+" relaunch\n");
	 			
	 			fop.close();
	     	}
	       catch (Exception e) {
	 		// TODO Auto-generated catch block
	 		e.printStackTrace();
	 	  }
	}
	
	void addOnCreateStat(String actName, int initStateID){
		Date dd=new Date();
		long mtime=dd.getTime();
		
		 Log.d(STATTAG,"[Statistics]"+getCurrentTime()+"[onCreate]"+actName+"("+initStateID+")");
		 try {
	 			File file = new File(outputFileFoleder+groundTruthFilename);
	 			
	 			if (!file.exists()){			
	 				file.createNewFile();
	 			}
	 			
	 			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
	 			fop.write(mtime+" "+actName+" 0 "+initStateID+"\n");
	 			
	 			fop.close();
	     	}
	       catch (Exception e) {
	 		// TODO Auto-generated catch block
	 		e.printStackTrace();
	 	  }
/*		if (!CREATMODE){
			int cnt=0;
			if (activityStat.get(actName).onCreate.get(initStateID)!=null)
				cnt=activityStat.get(actName).onCreate.get(initStateID);
			activityStat.get(actName).onCreate.put(initStateID,cnt+1);

		}*/
		
	}
	
	void addOnListViewStat(int lineno){
		Date dd=new Date();
		long mtime=dd.getTime();
		
		 Log.d(STATTAG,"[Statistics]"+getCurrentTime()+"[line click]"+lineno);
		 try {
	 			File file = new File(outputFileFoleder+groundTruthFilename);
	 			
	 			if (!file.exists()){			
	 				file.createNewFile();
	 			}
	 			
	 			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
	 			fop.write(mtime+" "+lineno+"\n");
	 			
	 			fop.close();
	     	}
	       catch (Exception e) {
	 		// TODO Auto-generated catch block
	 		e.printStackTrace();
	 	  }
		
	}
	
	void addOnResumeStat(String actName, int initStateID, int landingStateID){
		Date dd=new Date();
		long mtime=dd.getTime();
		 Log.d(STATTAG,"[Statistics]"+getCurrentTime()+" [onResume]"+actName+"("+initStateID+")"+"("+landingStateID+")");
		 try {
	 			File file = new File(outputFileFoleder+groundTruthFilename);
	 			
	 			if (!file.exists()){			
	 				file.createNewFile();
	 			}
	 			
	 			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
	 			fop.write(mtime+" "+actName+" 1 "+initStateID+" "+landingStateID+"\n");
	 			
	 			fop.close();
	     	}
	       catch (Exception e) {
	 		// TODO Auto-generated catch block
	 		e.printStackTrace();
	 	  }
	/*	 if (!CREATMODE){
			 int cnt=0;
				if (activityStat.get(actName).onResume.get(initStateID).get(landingStateID)!=null)
					cnt=activityStat.get(actName).onResume.get(initStateID).get(landingStateID);
				activityStat.get(actName).onResume.get(initStateID).put(landingStateID,cnt+1);

		 }
		 */
		
		
	}
	
	void reportError(String report){
		Date dd=new Date();
		long mtime=dd.getTime();
		try {
	 			File file = new File(outputFileFoleder+reportErrorFilename);
	 			
	 			if (!file.exists()){			
	 				file.createNewFile();
	 			}
	 			
	 			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
	 			fop.write(mtime+" "+report+"\n");
	 			
	 			fop.close();
	     	}
	       catch (Exception e) {
	 		// TODO Auto-generated catch block
	 		e.printStackTrace();
	 	  }
	}
	
	
	int findEventRecord(Activity curAct, ArrayList<String> newViewState){
		int pos=eventPath.size()-1;
		while (pos>=0 && (!(eventPath.get(pos).activity.equals(curAct.toString()) && compareInteractState(eventPath.get(pos).viewState, newViewState)==0))) {
		//	Log.d(TAG,""+pos);
		//	Log.d(TAG,eventPath.get(pos).viewState.toString());
		//	Log.d(TAG,newViewState.toString());
			pos--;
		}
		
		return pos;		
	}
	int findInImViewState(imViewStateForDFS ivsfd){
		for (int i=0;i<imViewState.size();i++){
			if (imViewState.get(i).compareTo(ivsfd)==0)
				return i;
		}
		return -1;
	}
	
/*	Boolean isNewInitState(String curAct, ArrayList<String> viewState){
		int pos=eventPath.size()-1;
		if (eventPath.get(pos).activity.equals(curAct)) return false;
		
		while (pos>=0 
				&& (!(eventPath.get(pos).activity.equals(curAct) && compareInteractState(eventPath.get(pos).viewState, viewState)==0))) 
			pos--;
		if (pos<0) return true;
		//pos>=0
		return false;
	}
	*/
	int findNearestActivityInitState(Activity curAct, ArrayList<String> viewState){
		String curActName=curAct.getClass().getName();
		if (activityData.get(curActName)==null)
			return -1;
		activityRecord ar=activityData.get(curActName);
		int vsno=-1;
		double maxratio=0;
		for (int j=0;j<ar.initState.size();j++){
			double ratio=compareDrawStateWithRatio(ar.initState.get(j),viewState);
			if (ratio > maxratio){
				maxratio=ratio;
				vsno=j;
			}
		}
		return vsno;
    }
	int addActivityInitState(Activity curAct, ArrayList<String> viewState, int addORnot){
	//addORnot: 0- add, 1-not
	//used after addActivityLandingState()
	//make sure that viewState has not been included in eventPath yet
	//add any landing state is ok, we have handled that!
		String curActName=curAct.getClass().getName();
		if (activityData.get(curActName)==null){
			if (addORnot==1)
				return -1;			
			activityRecord ar=new activityRecord(curActName);
    		activityData.put(curActName, ar);
    		
		}

		int ispos=getInitStatePos(curAct);
		ArrayList<String> is;
		if (ispos==-1)
			is=viewState;
		else {			
			is=eventPath.get(ispos).viewState;
	/*		int tpos=ispos;
			while (tpos<eventPath.size() && (!(eventPath.get(tpos).activity.equals(curAct) 
    				&& compareInteractState(eventPath.get(tpos).viewState,viewState)==0))) tpos++;
    		if (tpos==ispos || tpos==eventPath.size()){
    		//not found, new init stat!
    			is=viewState;
    		}*/			
		}
		
		
		activityRecord ar=activityData.get(curActName);
		int vsno=-1;
		for (int j=0;j<ar.initState.size();j++){
			if (compareDrawState(ar.initState.get(j),is)==0){
				vsno=j;
				break;
			}
		}
		if (vsno>=0)
		  return vsno;
		if (addORnot==1)
		  return -1;
    	ar.initState.add(is);
        ar.landingState.add(new ArrayList<ArrayList<String>>());
	    ar.outInitStateOptions.add(new ArrayList<eventTrace>());
	    ar.backInitStateOptions.add(new ArrayList<eventTrace>());
	    writeRecord(curActName);
	    return ar.initState.size()-1; 

	}
	
	int findNearestActivityLandingState(Activity curAct, ArrayList<String> viewState, int initLoc){
		String curActName=curAct.getClass().getName();
		if (activityData.get(curActName)==null)
			return -1;
		activityRecord ar=activityData.get(curActName);
		int vsno=-1;
		double maxratio=0;
		for (int j=0;j<ar.initState.size();j++){
			double ratio=compareDrawStateWithRatio(ar.landingState.get(initLoc).get(j),viewState);
			if (ratio > maxratio){
				maxratio=ratio;
				vsno=j;
			}
		}
		return vsno;
    }
	
	int addActivityLandingState(Activity curAct, ArrayList<String> ViewState, int initLoc, int addORnot){
		//addORnot: 0- add, 1-not
	//0: drawState
		String curActName=curAct.getClass().getName();
		activityRecord ar=activityData.get(curActName);
		int vsno=-1;

		
		for (int j=0;j<ar.landingState.get(initLoc).size();j++){
	//		Log.d(TAG, ar.drawState.get(j).toString());
		//	Log.d(TAG, ViewState.toString());
			if (compareDrawState(ar.landingState.get(initLoc).get(j),ViewState)==0){
		//		Log.d(TAG,ar.landingState.get(initLoc).get(j).toString());
	    //		Log.d(TAG,ViewState.toString());
				vsno=j;
				break;
			}
		}
		if (vsno>=0){
			Log.d(TAG,"exist landing state "+vsno);
			return vsno;
		}
		if (addORnot==1)
			  return -1;
		Log.d(TAG,"new landing state!");
		ar.landingState.get(initLoc).add(ViewState);
	    writeRecord(curActName);
		return ar.landingState.get(initLoc).size()-1;
	}
	
	
	double compareDrawStateWithRatio(ArrayList<String> ViewState1, ArrayList<String> ViewState2){
		boolean thesame=true;
		if (ViewState1.size()!=ViewState2.size()){
			thesame=false;
		}
		if (thesame){		
			for (int i=0;i<ViewState1.size();i++){
				String[] vs1s=ViewState1.get(i).split(",");
				String[] vs2s=ViewState2.get(i).split(",");
				for (int j=0;j<2;j++)
					if (!vs1s[j].equals(vs2s[j])){
						thesame=false;
						break;
					}
				if (!thesame)
					break;					
			}
		}
		if (thesame)
		 return 1;
		//need to measure the samilarity
		ArrayList<String> avs1,avs2;
		int size=ViewState1.size(),sizeBig=ViewState2.size();
		
		avs1=ViewState1;
		avs2=ViewState2;
		if (ViewState1.size() > ViewState2.size()) {
			size=ViewState2.size();
			sizeBig=ViewState1.size();
			avs1=ViewState2;
			avs2=ViewState1;
		}
		double samenum=0;
		for (int i=0;i<size;i++){
			for (int j=0;j<avs2.size();j++){
				String[] vs1s=avs1.get(i).split(",");
				String[] vs2s=avs2.get(j).split(",");
				boolean itemthesame=true;
				for (int k=0;k<2;k++)
					if (!vs1s[k].equals(vs2s[k])){
						itemthesame=false;
						break;
					}
				if (itemthesame){
					samenum++;
					break;
				}
			}
		}
		double perc=(samenum/size+samenum/sizeBig)/2;
		
		//listview
		for (int i=0;i<size;i++){
			String[] vs1s=avs1.get(i).split(",");
			if (vs1s[0].equals("android.widget.ListView")){
				boolean inavs2=false;
				for (int j=0;j<avs2.size();j++){					
					String[] vs2s=avs2.get(j).split(",");
					if (vs2s[0].equals("android.widget.ListView")){
						boolean itemthesame=true;
						for (int k=0;k<2;k++)
							if (!vs1s[k].equals(vs2s[k])){
								itemthesame=false;
								break;
							}
						if (itemthesame){
							inavs2=true;
							break;
						}
					}					
				}
				if (!inavs2){
					perc=0;
					break;
				}
			}			
		}
		for (int j=0;j<avs2.size();j++){
			String[] vs2s=avs2.get(j).split(",");
			if (vs2s[0].equals("android.widget.ListView")){
				boolean inavs1=false;
				for (int i=0;i<size;i++){					
					String[] vs1s=avs1.get(i).split(",");
					if (vs1s[0].equals("android.widget.ListView")){
						boolean itemthesame=true;
						for (int k=0;k<2;k++)
							if (!vs1s[k].equals(vs2s[k])){
								itemthesame=false;
								break;
							}
						if (itemthesame){
							inavs1=true;
							break;
						}
					}					
				}
				if (!inavs1){
					perc=0;
					break;
				}
			}			
		}
		
		
		
		
		return perc;
	}
	int compareDrawState(ArrayList<String> ViewState1, ArrayList<String> ViewState2){
		boolean thesame=true;
		if (ViewState1.size()!=ViewState2.size()){
			Log.d(TAG,"draw state differ in size: "+ViewState1.size()+"--"+ViewState2.size());
			Log.d(TAG," "+ViewState1.toString());
			Log.d(TAG," "+ViewState2.toString());
			thesame=false;
		}
		if (thesame){		
			for (int i=0;i<ViewState1.size();i++){
				String[] vs1s=ViewState1.get(i).split(",");
				String[] vs2s=ViewState2.get(i).split(",");
				for (int j=0;j<2;j++)
					if (!vs1s[j].equals(vs2s[j])){
						Log.d(TAG,"draw state differ in signature: "+ViewState1.get(i)+"--"+ViewState2.get(i));
						thesame=false;
						break;
					}
				if (!thesame)
					break;					
			}
		}
		if (thesame)
		 return 0;
		
		//need to measure the samilarity
		double perc=compareDrawStateWithRatio(ViewState1, ViewState2);
		Log.d(TAG,"similarity score (draw state): "+perc);
		//0.80 magic number
		if (perc > DRAWSTATRATIO) return 0;
		
		return -1;		
	}
	int compareInteractState(ArrayList<String> ViewState1, ArrayList<String> ViewState2){
    	boolean thesame=true;
		if (ViewState1.size()!=ViewState2.size()){
			thesame=false;
		}
		if (thesame){		
			for (int i=0;i<ViewState1.size();i++){
				String vs1=ViewState1.get(i);
				String vs2=ViewState2.get(i);
				if (!compareViewSignature(vs1,vs2))
					thesame=false;
				if (!thesame)
					break;					
			}
		}
		if (thesame){
			Log.d(TAG,"interact state compare: the same 0");
		 return 0;
		}
		if (!APPROXIMATEINTERACTCMP){
			Log.d(TAG,"interact state compare no approximate: different -1");
		  return -1;
		}
		
		//need to measure the samilarity
		ArrayList<String> avs1,avs2;
		int size=ViewState1.size();
		avs1=ViewState1;
		avs2=ViewState2;
		if (ViewState1.size() > ViewState2.size()) {
			size=ViewState2.size();
			avs1=ViewState2;
			avs2=ViewState1;
		}
		double samenum=0;
		for (int i=0;i<size;i++){
			for (int j=0;j<avs2.size();j++){
				String vs1s=avs1.get(i);
				String vs2s=avs2.get(j);
				boolean itemthesame=true;
				if (!compareViewSignature(vs1s,vs2s))
					itemthesame=false;
				if (itemthesame){
					samenum++;
					break;
				}
			}
		}
		double perc=samenum/size;
		
		//listview
		for (int i=0;i<size;i++){
			String[] vs1s=avs1.get(i).split(",");
			if (vs1s[0].equals("android.widget.ListView")){
				boolean inavs2=false;
				for (int j=0;j<avs2.size();j++){					
					String[] vs2s=avs2.get(j).split(",");
					if (vs2s[0].equals("android.widget.ListView")){
						boolean itemthesame=true;
						for (int k=0;k<2;k++)
							if (!vs1s[k].equals(vs2s[k])){
								itemthesame=false;
								break;
							}
						if (itemthesame){
							inavs2=true;
							break;
						}
					}					
				}
				if (!inavs2){
					perc=0;
					break;
				}
			}			
		}
		for (int j=0;j<avs2.size();j++){
			String[] vs2s=avs2.get(j).split(",");
			if (vs2s[0].equals("android.widget.ListView")){
				boolean inavs1=false;
				for (int i=0;i<size;i++){					
					String[] vs1s=avs1.get(i).split(",");
					if (vs1s[0].equals("android.widget.ListView")){
						boolean itemthesame=true;
						for (int k=0;k<2;k++)
							if (!vs1s[k].equals(vs2s[k])){
								itemthesame=false;
								break;
							}
						if (itemthesame){
							inavs1=true;
							break;
						}
					}					
				}
				if (!inavs1){
					perc=0;
					break;
				}
			}			
		}
		
		
		Log.d(TAG,"similarity score (interact state): "+perc);
		//0.85 magic number
		if (perc > INTERACTSTATRATIO) return 0;
		
		return -1;		
	}
	
/*	void addEventRecord(String act, event action, ArrayList<String> viewState, int loca){
		eventRecord er=new eventRecord();
		er.activity=act;
		er.action=action;
		er.viewState=viewState;
		er.location=loca;
		
		eventPath.add(er);
	}
	*/
	View findViewBySignature(String signature, int no){
	//if no==-1, no number information here
		ArrayList<String> resviewstate=new ArrayList<String>();
	    ArrayList<View> allview=getTopViewGroups();
	    ArrayList<View> vq=new ArrayList<View>();
	    vq.addAll(allview);

	    Boolean frontHasNoHope=false;
	    if (no<0) frontHasNoHope=true;
	    
	    View candidate=null;
	    int front=0,rear=vq.size();
	    while (front<rear){
	    	View tvi=vq.get(front);
	    	if (tvi instanceof ViewGroup){
	    //		Log.d(TAG,"--root:"+viewFields(tvi));
	    		ViewGroup tvgi=(ViewGroup)tvi;
	    		int childcount=tvgi.getChildCount();
		    	for (int i=0;i<childcount;i++){
		    		View ctvi=tvgi.getChildAt(i);
		    		if (considerView(ctvi,tvgi)) {
		  //  			Log.d(TAG,viewFields(ctvi));
			    		vq.add(ctvi);
			    		rear++;
		    		}	    		
		    	}
	    	}
	 //   	Log.d(TAG,getViewSignature(tvi));
	    	if (considerView(tvi,null)){
	    	//view signature    	
	    //		Log.d(TAG, getViewSigature(tvi)+"->"+signature);
	    		if (compareViewSignature(getViewSignature(tvi),signature))
	    			candidate=tvi;
	    		if (front==no){
	    			if (compareViewSignature(getViewSignature(tvi), signature)){
	    				return tvi;
	    			}
	    			frontHasNoHope=true;
	    		}
	    		if (frontHasNoHope && candidate!=null)
	    			return candidate;
	    	}	 

	    	front++;
	   }
	    
	    Log.d(TAG, "OMG, cannot find view for signature:"+signature);
	    Log.d(TAG, getCurrentViewState().toString());
	    return null;
	}
	
	void writeAccesoryData(){
		Log.d(TAG,"text fill data: "+textFillData.size());
		File datafile = new File(activityRecordFileFolder+textFillFilename);
		Log.d(TAG,"saving text fill data");
		try {
			datafile.createNewFile();
			BufferedWriter fop = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(datafile)));
			fop.write(textFillData.size()+"\n");
			for (int i=0;i<textFillData.size();i++)    				
              fop.write(textFillData.get(i).toString().replaceAll("(\\r|\\n)", "")+"\n");
			fop.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		datafile = new File(activityRecordFileFolder+doneActivitiesListFilename);
		Log.d(TAG,"saving done activities data");
		try {
			datafile.createNewFile();
			BufferedWriter fop = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(datafile)));
			fop.write(doneActivities.size()+"\n");
			for (int i=0;i<doneActivities.size();i++)    				
              fop.write(doneActivities.get(i).activity+";"+doneActivities.get(i).initState+"\n");
			fop.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!CREATMODE){
			//saving traverse data
	    	datafile = new File(outputFileFoleder+traverseFilename);
			Log.d(TAG,"saving traverse data");
			try {
				datafile.createNewFile();
				BufferedWriter fop = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(datafile)));
			    for (int i=0;i<acts.length;i++){
		    		statData sd=activityStat.get(acts[i].name);
		    		activityRecord temparcur=activityData.get(acts[i].name);
		    		if (temparcur==null) continue;
	
		    //		Log.d(TAG,"saving here");
		    		int initStateNum=temparcur.initState.size();
		    		fop.write(acts[i].name+"\n");
		    		for (int j=0;j<initStateNum;j++){
		    			int cnt=0;
		        		if (sd.onCreate.get(j)!=null)
		        			cnt=sd.onCreate.get(j);
			    		fop.write(j+" "+cnt+" ");
		    		}   
		    		fop.write("\n");
			    }
				fop.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(TAG,"error..!!");
				e.printStackTrace();
			}
		}
	}

	void getBackToBegining(){
		
		writeAccesoryData();
	
		//rescue time!
		Log.d(TAG, "rescue time (5s)!");		solo.sleep(5000); Log.d(TAG, "rescue end!");	
		
		
	//		Log.d(TAG,"size: "+getCurrentViewState().size());
	//	if (att!=null)
     //   	att.stopAttack();
		if (getCurrentActivity().getClass().getName().equals("com.newegg.app.activity.home.Main")){
			//do not go back!
			solo.finishOpenedActivities();
		}
		else 
		if (TARGET_PACKAGE_ID.equals("com.chase.sig.android")){
			executeGoBack();
			while (isInThisApp(getCurrentActivity()))
				executeGoBack();
			
		}
		else {
			solo.finishOpenedActivities();		   
			while (isInThisApp(getCurrentActivity())){
				 executeGoBack();
			   solo.finishOpenedActivities();	
			   Log.d(TAG,getCurrentViewState().toString());
			   if (getCurrentActivity().getClass().getName().equals(LAUNCHER_ACTIVITY_FULL_CLASSNAME)){
				   break;
			   }
			}
		}
		
		
	/*	if (getCurrentActivity().getClass().getName().equals("com.chase.sig.android.activity.AccountsActivity")){
			solo.clickOnScreen((float)505.5,(float)770.0);
			solo.sleep(CLICK_SLEEPTIME);
		}*/
		
		if (isInThisApp(getCurrentActivity()) 
				&& getCurrentActivity().getClass().getName().equals(LAUNCHER_ACTIVITY_FULL_CLASSNAME)){
			recordRelaunch();
	    }else {
		  solo.activityUtils.restartActivityUtils();
          this.launchActivity(TARGET_PACKAGE_ID,launcherActivityClass,null);
          if (WITHSPLASH)
          	solo.activityUtils.restartActivityUtils();	
          recordRelaunch();
          if (WITHSPLASH)
            solo.sleep(SPLASHSCREEN_SLEEPTIME);
          
		}
		

		Log.d(TAG,"finished");
		
		getEverythingReadyAfterEvent(null,false);
		
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		activityRecord firstarcur=activityData.get(curActName);	
	    if (firstarcur!=null && firstarcur.outInitStateOptions.size()>0 && firstarcur.outInitStateOptions.get(0).size()!=0){
	    //valid
	    	int initState=-1;
	    	initState=addActivityInitState(curAct,getCurrentViewState(),1);
	    	while (initState!=0){	    	  
	    		if (TARGET_PACKAGE_ID.equals("com.chase.sig.android")){
	    			while (isInThisApp(getCurrentActivity()))
	    				executeGoBack();	    			
	    		}
	    		else {
		    		solo.finishOpenedActivities();		   
		    		while (isInThisApp(getCurrentActivity())){
		    			executeGoBack();
		    		   solo.finishOpenedActivities();	
		    		   Log.d(TAG,getCurrentViewState().toString());
		    		   if (getCurrentActivity().getClass().getName().equals(LAUNCHER_ACTIVITY_FULL_CLASSNAME)){
		    			   break;
		    		   }
		    		}
	    		}
	    		if (isInThisApp(getCurrentActivity()) && getCurrentActivity().getClass().getName().equals(LAUNCHER_ACTIVITY_FULL_CLASSNAME))
	    			recordRelaunch();
	    		else {
	    		  solo.activityUtils.restartActivityUtils();
	              this.launchActivity(TARGET_PACKAGE_ID,launcherActivityClass,null);
	              if (WITHSPLASH)
	              	solo.activityUtils.restartActivityUtils();	
	              recordRelaunch();
	              if (WITHSPLASH)
	                solo.sleep(SPLASHSCREEN_SLEEPTIME);
	              
	    		}
	    		
	    		getEverythingReadyAfterEvent(null,false);
	    		if (!LAUNCHCONFIRM)
	    			break;
	    	  initState=addActivityInitState(curAct,getCurrentViewState(),1);
	    	}
	    }
		
		if (WITHSPLASH){
		  addOnCreateStat(LAUNCHER_ACTIVITY_FULL_CLASSNAME,0);
		  statData sd=activityStat.get(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
    	  int cnt=0;
		  if (sd.onCreate.get(0)!=null)
			  cnt=sd.onCreate.get(0);
		  if (cnt!=-1){
    		  sd.onCreate.put(0,cnt+1);
		  }
		}

		eventPath.get(0).activity=getCurrentActivity().toString();

		eventPath.get(0).viewState=getCurrentViewState();
		if (eventPath.get(0).initStateNo!=-1){
			activityData.get(getCurrentActivity().getClass().getName()).initState.set(eventPath.get(0).initStateNo,eventPath.get(0).viewState);
		}
		solo.sleep(CLICK_SLEEPTIME);
	}
	
	void goBackToPos(int backposition){
	//go back to view state position
    //caution! the current position and the target position should be ensured to be in the same activity
	   int position=eventPath.size()-1-backposition;
	//   String curAct=getCurrentActivity().toString();
	   
   	   //go back to position!
	   Log.d(TAG, "go back to position: "+position);
	   int pos=-1;
	   ArrayList<String> before=getCurrentViewState();
    	int backnum=-1;
	   while (true){
	   //go back to somewhere we know			   
		   String tempAct=getCurrentActivity().toString();
		   String tempActName=getCurrentActivity().getClass().getName();
		   ArrayList<String> tempViewState=getCurrentViewState();
		   boolean toomanybackwithnoeffect=false;
		   if (compareInteractState(before, tempViewState)==0){
			   if (backnum>BACKLIMIT)
				   toomanybackwithnoeffect=true;			   
		   }
		   else {
			   before=tempViewState;
		       backnum=0;
		   }
		   if (tempViewState.size()==0 || toomanybackwithnoeffect || !isInThisApp(getCurrentActivity())){
			   Log.d(TAG, "out of current activity! 4");
			   getBackToBegining();
			   pos=0;
			   break;
		   }
		   //pos=eventPath.size()-1;
		   pos=position;
	/*	   if (pos==3){
			   Log.d(TAG, ""+tempViewState.toString());
			   Log.d(TAG, ""+eventPath.get(pos).viewState.toString());
			   for (int ii=0;ii<eventPath.size();ii++){
				   Log.d(TAG, ii+":"+eventPath.get(ii).viewState.toString());
				   
			   }
		   }*/
		   boolean crossAct=false;
		   while (pos>=0 && !(compareInteractState(tempViewState,eventPath.get(pos).viewState)==0
				   && tempAct.equals(eventPath.get(pos).activity))
			//	 && eventPath.get(pos).activity.equals(curAct)
				 ) {
			   if (!crossAct 
					&& pos-1>=0 
					&& !eventPath.get(pos).activity.equals(eventPath.get(pos-1).activity))
				   crossAct=true;
			   pos--;
			}
		   
		   if (pos >=0 && position!=pos 
				   && tempAct.equals(eventPath.get(pos).activity)
				   && !crossAct
		//		   && tempAct.getClass().getName().equals("com.newegg.app.activity.home.Main")
				   ){
		//XX[if we come to an old place which maintains app state, we keep going back]
	    //
		   //same activity go back, may store some states
			   pos=-1;
		   }
	//	   Log.d(TAG, tempViewState.toString());
	//	   Log.d(TAG, eventPath.get(0).viewState.toString());
		   if (pos >=0 && 
				   (pos+1>=eventPath.size() || 
				   !(eventPath.get(pos+1).activity.contains("com.webmd.android")
				    && eventPath.get(pos+1).action.eventid==4 && eventPath.get(pos+1).action.listviewline!=-1))			   
				//   && eventPath.get(pos).activity.equals(curAct)
				   ){
		//	   if (lastestActivity!=null)
			//    Log.d(TAG,lastestActivity.getClass().getName()+"--"+getCurrentActivity().getClass().getName());
			 break;
		   }
		   Log.d(TAG, "go back 2");
		   if (tempActName.equals("com.newegg.app.activity.home.Main")){
			   Log.d(TAG, "about to leave New Egg app, restart now");
			   getBackToBegining();
			   pos=0;
			   break;
		   }
		   else executeGoBack();
    	   backnum++;
	   }
	   Log.d(TAG, "out1! "+"pos: "+pos);
	   if (pos<0){			
			 try {
				Log.d(TAG,"[OMG]no current state after go back found in event path!");
				this.tearDown();
			} catch (Exception e) {
				e.printStackTrace();
			}
	   }
	   Log.d(TAG, "out2! "+"pos: "+pos);
	   if (pos>=0){		   
		   //replay to position
		   for (pos++;pos <= position && pos < eventPath.size();pos++){			   
				executeEvent(eventPath.get(pos).action,false);
				eventPath.get(pos).activity=getCurrentActivity().toString();
				eventPath.get(pos).viewState=getCurrentViewState();
				if (eventPath.get(pos).initStateNo!=-1){
					Log.d(TAG,"set "+getCurrentActivity().getClass().getName()+" initState: "+eventPath.get(pos).initStateNo+" at pos "+pos);
			//		Log.d(TAG,eventPath.get(pos).viewState.toString());
					activityData.get(getCurrentActivity().getClass().getName()).initState.set(eventPath.get(pos).initStateNo,eventPath.get(pos).viewState);
			//		Log.d(TAG, activityData.get(getCurrentActivity().getClass().getName()).initState.get(eventPath.get(pos).initStateNo).toString());
				}
			}
		//   Log.d(TAG, pos+" before SIZEEEE: "+eventPath.size());
		   while (pos<eventPath.size())
				eventPath.remove(eventPath.size()-1);
		//   Log.d(TAG, "SIZEEEE: "+eventPath.size());
	   }	   
	   Log.d(TAG, "out3! "+"pos: "+pos);
		
	}
	
	int getInitStatePos(String curAct){
	/*	ArrayList<String> is=null;
        int pos=0;
        int lastpos=-1;
        while (pos<eventPath.size()){
        	//find the first guy
        	while (pos<eventPath.size() && !eventPath.get(pos).activity.equals(curAct)) pos++;
        	if (pos>=eventPath.size())
        		break;
        	if (lastpos==-1){
        		is=eventPath.get(pos).viewState;
        		lastpos=pos;
        	}
        	else {
        		ArrayList<String> tis=eventPath.get(pos).viewState;
        		int tpos=lastpos;
        		while (tpos<pos && (!(eventPath.get(tpos).activity.equals(curAct) 
        				&& compareInteractState(eventPath.get(tpos).viewState,tis)==0))) tpos++;
        		if (tpos==pos || tpos==lastpos){
        		//not found, new init stat!
        			is=tis;
        			lastpos=pos;
        		}
        	}    		
    		while (pos<eventPath.size() && eventPath.get(pos).activity.equals(curAct)) pos++;
        }
        return lastpos;
        */
		int pos=0;
		while (pos<eventPath.size() && !eventPath.get(pos).activity.equals(curAct)) pos++;
		if (pos>=eventPath.size()) return -1;
		
		return pos;
		
	}
	int getInitStatePos(Activity curAct){
		return getInitStatePos(curAct.toString());
	}
	
	int getInitStateID(Activity curAct, int ispos){
		String curActName=curAct.getClass().getName();
//		int ispos=getInitStatePos(curAct);
		if (ispos!=-1){
			ArrayList<String> is=eventPath.get(ispos).viewState;
			ArrayList<ArrayList<String>> ds=activityData.get(curActName).initState;
			for (int i=0;i<ds.size();i++)
				if (compareDrawState(ds.get(i),is)==0)
				  return i;		
			Log.d(TAG,"still cannot find init state! "+curActName);
	//		return -1;
			int initpos=-1;
			int maxsim=0;
			for (int i=0;i<ds.size();i++){
				int sim=compareDrawState(ds.get(i),is)*(-1);
				if (sim>maxsim){
					maxsim=sim;
					initpos=i;
				}					
			}
			if (initpos!=-1)
				return initpos;
		}
		
    	try {
    		Log.d(TAG,"[OMG]cannot find init state!");
			tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	ArrayList<event> getEventTrace(Activity curAct){
		int ispos=getInitStatePos(curAct);
		if (ispos==-1) return null;
		ArrayList<String> is=eventPath.get(ispos).viewState;
		ArrayList<event> et=new ArrayList<event>();
		ArrayList<event> backet=new ArrayList<event>();
		int pos=eventPath.size()-1;
		ArrayList<String> tempState=null;
		while (pos>=0){
			while (pos>=0 && eventPath.get(pos).activity.equals(curAct.toString())) {
				backet.add(eventPath.get(pos).action);
				pos--;
			}
			backet.remove(backet.size()-1);
			tempState=eventPath.get(pos+1).viewState;
			if (compareInteractState(tempState,is)==0)
				break;
			
			while (pos>=0 && !(eventPath.get(pos).activity.equals(curAct.toString()) 
					&& compareInteractState(eventPath.get(pos).viewState,tempState)==0)) 
				pos--;
		}
		
		for (int i=backet.size()-1;i>=0;i--)
			et.add(backet.get(i));
		return et;		
	}
	
	int interactEffect(Activity curAct, ArrayList<String> curViewState, event evt, View viewinst){
		//viewinst may be null!!
		String curActName=curAct.getClass().getName();
		lastestActivity=curAct;
		ArrayList<String> newViewState=getCurrentViewState();
		if (newViewState.size()==0 || !isInThisApp(getCurrentActivity())){
		   Log.d(TAG, "out of current application! 1");
		   getBackToBegining();
			this.goBackToPos(0);
			return 0;
		}
		Activity newAct=getCurrentActivity();
		String newActName=newAct.getClass().getName();
		Log.d(TAG, "activity: "+newAct);
		if (!curAct.toString().equals(newAct.toString()) || compareInteractState(curViewState,newViewState)!=0){
		//view changed
		//	solo.sleep(3000);
			
			
			boolean newlandingState=false;
			if (!curAct.toString().equals(newAct.toString())){
		    //new activity!
				lastestActivity=newAct;
			//	Log.d(TAG,"SIZESIZE1: "+activityData.get(curActName).outInitStateOptions.get(0).size());
				Log.d(TAG, "enter new activity: "+newAct);
                ActivityInfo ai=actInfo.get(newActName);
                if (ai.launchMode==ActivityInfo.LAUNCH_MULTIPLE)
                	Log.d(TAG, "LAUNCH_MULTIPLE");
                if (ai.launchMode==ActivityInfo.LAUNCH_SINGLE_INSTANCE)
                	Log.d(TAG, "LAUNCH_SINGLE_INSTANCE");
                if (ai.launchMode==ActivityInfo.LAUNCH_SINGLE_TASK)
                	Log.d(TAG, "LAUNCH_SINGLE_TASK");
                if (ai.launchMode==ActivityInfo.LAUNCH_SINGLE_TOP)
                	Log.d(TAG, "LAUNCH_SINGLE_TOP");
				int intentflags=newAct.getIntent().getFlags();
				if ((intentflags|Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_BROUGHT_TO_FRONT");
				if ((intentflags|Intent.FLAG_ACTIVITY_CLEAR_TASK) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_CLEAR_TASK");
				if ((intentflags|Intent.FLAG_ACTIVITY_CLEAR_TOP) == intentflags)
						Log.d(TAG, "FLAG_ACTIVITY_CLEAR_TOP");
				if ((intentflags|Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET");
				if ((intentflags|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS");
				if ((intentflags|Intent.FLAG_ACTIVITY_FORWARD_RESULT) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_FORWARD_RESULT");
				if ((intentflags|Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY");
				if ((intentflags|Intent.FLAG_ACTIVITY_MULTIPLE_TASK) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_MULTIPLE_TASK");
				if ((intentflags|Intent.FLAG_ACTIVITY_NEW_TASK) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_NEW_TASK");
				if ((intentflags|Intent.FLAG_ACTIVITY_NO_ANIMATION) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_NO_ANIMATION");
				if ((intentflags|Intent.FLAG_ACTIVITY_NO_HISTORY) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_NO_HISTORY");
				if ((intentflags|Intent.FLAG_ACTIVITY_NO_USER_ACTION) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_NO_USER_ACTION");
				if ((intentflags|Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_PREVIOUS_IS_TOP");
				if ((intentflags|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_REORDER_TO_FRONT");
				if ((intentflags|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_RESET_TASK_IF_NEEDED");
				if ((intentflags|Intent.FLAG_ACTIVITY_SINGLE_TOP) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_SINGLE_TOP");
				if ((intentflags|Intent.FLAG_ACTIVITY_TASK_ON_HOME) == intentflags)
					Log.d(TAG, "FLAG_ACTIVITY_TASK_ON_HOME");
				
				//add new init state to new activity
				int newInitStateID=addActivityInitState(newAct,newViewState,0);
				//for out edge, target landing state id is not important
		//		addActivityLandingState(newAct,newViewState,newinitloca);
		//		int newloca=addActivityInitState(newAct,newViewState);

		
				//get event trace
				int initStatePos=getInitStatePos(curAct);
				Log.d(TAG,curAct.toString()+" "+initStatePos);
				int initStateID=getInitStateID(curAct,initStatePos);
				ArrayList<String> curActInitState=eventPath.get(initStatePos).viewState;
				ArrayList<event> etrace=getEventTrace(curAct);
				eventTrace et=new eventTrace();
				et.targetActivity=newActName;
				et.maybeResume=0;
				if (getInitStatePos(newAct)!=-1)
				  et.maybeResume=1;
				et.targetInitStateID=newInitStateID;						
				et.eventtrace=etrace;		
				et.eventtrace.add(evt);
				Log.d(TAG, "a trace:"+et.toString());
				
				
				if (et!=null){
				//trace exist
					//check if this is a new trace for outInit
					
					ArrayList<eventTrace> aets=activityData.get(curActName).outInitStateOptions.get(initStateID);
				//	Log.d(TAG,"CURRENT:"+curActName+" #: "+aets.size());
					int etid=-1;
					for (int i=0;i<aets.size();i++){
						eventTrace ett=aets.get(i);
				//		Log.d(TAG,ett.targetActivity+"--"+et.targetActivity);
				//		Log.d(TAG,ett.targetInitStateID+"--"+et.targetInitStateID);
						if (ett.targetActivity.equals(et.targetActivity) && ett.targetInitStateID==et.targetInitStateID){
							etid=i;
							break;
						}					
					}
					
					//add new trace if proper
					if (etid==-1){
						//new trace
						Log.d(TAG, "new trace!");
						 aets.add(et);
						 writeRecord(curActName);
					}
					else {
						if (et.eventtrace.size() < aets.get(etid).eventtrace.size()){
					    //old trace but shorter

							Log.d(TAG, "old trace but shorter!");
							aets.set(etid, et);
							writeRecord(curActName);
					    }						
					}

					Log.d(TAG, "go back to last activity");
					//go back to last activity
					//may have problem: cannot go back
					Boolean outofArea=false;
					ArrayList<String> before=getCurrentViewState();
			    	int backnum=-1;
					while (getCurrentActivity().toString().equals(newAct.toString())) {
						
						ArrayList<String> tempViewState=getCurrentViewState();
						boolean toomanybackwithnoeffect=false;
						   if (compareInteractState(before, tempViewState)==0){
							   if (backnum>BACKLIMIT)
								   toomanybackwithnoeffect=true;			   
						   }
						   else {
							   before=tempViewState;
						       backnum=0;
						   }
					   if (tempViewState.size()==0 || toomanybackwithnoeffect || !isInThisApp(getCurrentActivity())){
						   Log.d(TAG, "out of current activity! 2");
						   outofArea=true;
						   getBackToBegining();
						   break;
					   }
					       Log.d(TAG, "go back 3");
					       if (getCurrentActivity().getClass().getName().equals("com.newegg.app.activity.home.Main")){
							   Log.d(TAG, "about to leave New Egg app, restart now");
							   outofArea=true;
							   getBackToBegining();
							   break;
						   }
						   else
						     executeGoBack();
						   backnum++;
					//	   Log.d(TAG, getCurrentActivity().toString()+":"+curAct.toString());
					}
					
					if (!outofArea){
						if (!getCurrentActivity().toString().equals(curAct.toString())){
						//do not jump back to the caller activity
							Log.d(TAG,getCurrentActivity().toString()+"--"+curAct.toString());
							//case 1: the jump skips the caller activity and back to more primitive one
						/*	for (int pp=eventPath.size()-1;pp>=0;pp--)
								if (eventPath.get(pp).activity.equals(getCurrentActivity().toString())){
									outofArea=true;
									break;
								}*/
							//case 2: the back jump create a new instance, which is handled below in the branch of outofArea==false											
						}
						else {
						//do jump back to the caller activity
							if (!donotConsiderClickAndGoBack){
								newlandingState=true;
								ArrayList<String> vs=getCurrentViewState();
								for (int pp=eventPath.size()-1;pp>=0 && eventPath.get(pp).activity.equals(curAct.toString());pp--)
									if (compareInteractState(eventPath.get(pp).viewState, vs)==0){
										newlandingState=false;
										break;
									}					
								if (newlandingState)
									Log.d(TAG,"new landing state by going back!");
							}
							
						}
					}
					Log.d(TAG,"outofArea=="+outofArea);
			//		if (!newlandingState){
					if (!outofArea){
						
						//deal with goback drawstate
						eventTrace etback=null;
						if (et!=null){
							etback=new eventTrace();
							etback.eventtrace=et.eventtrace;
						}
						ArrayList<String> backViewState=getCurrentViewState();
						Activity backAct=getCurrentActivity();
						String backActName=backAct.getClass().getName();
						int backInitStatePos=getInitStatePos(backAct);
						int backInitStateID=-1;
						etback.isCreate=0;
						if (backInitStatePos==-1){
						//a new instance! this one is create!
							etback.isCreate=1;
							backInitStateID=addActivityInitState(backAct,backViewState,0);
						}
						else  backInitStateID=getInitStateID(backAct,backInitStatePos);
						//add back draw state to the current activity
						etback.targetActivity=backActName;
						etback.targetInitStateID=backInitStateID;
						etback.middleActivity=newActName;
						etback.middleInitStateID=newInitStateID;
						etback.targetLandingStateID=addActivityLandingState(backAct,backViewState,backInitStateID,0);	

						Log.d(TAG, "a back trace:"+etback.toString());
						
						//check if this is a new trace for outDraw
						ArrayList<eventTrace> aetsback=activityData.get(curActName).backInitStateOptions.get(initStateID);
						int etidback=-1;
						for (int i=0;i<aetsback.size();i++){
							eventTrace ett=aetsback.get(i);
							if (ett.targetActivity.equals(etback.targetActivity) && ett.targetInitStateID==etback.targetInitStateID
									&& ett.targetLandingStateID==etback.targetLandingStateID
								//	&& ett.middleInitStateID==etback.middleInitStateID
								//	&& ett.middleActivity.equals(etback.middleActivity)
									){
								etidback=i;
								break;
							}					
						}
						
						//add new back trace if proper
						if (etidback==-1){
							//new back trace
							Log.d(TAG, "new back trace");
							 aetsback.add(etback);
							 writeRecord(curActName);
						}
						else {
							if (etback.eventtrace.size() < aetsback.get(etidback).eventtrace.size()){
						    //old trace but shorter

								Log.d(TAG, "old back trace but shorter");
								aetsback.set(etidback, etback);
								writeRecord(curActName);
						    }						
						}
					}

			//		 Log.d(TAG,"aet size: "+aets.size());
														
					
				}
				if (!newlandingState){
					goBackToPos(0);				
					return 0;	
				}
								
			}
			
		    //still in the same activity

			    if (newlandingState){
				  newViewState=getCurrentViewState();
				  Log.d(TAG, "before:"+evt.toString());
				  evt=generateClickAndBackEvent(evt);
				  Log.d(TAG, "after:"+evt.toString());
			    }
			
			    
				Log.d(TAG, "still in the same activity!");
				imViewStateForDFS ivsfdfs=new imViewStateForDFS(newViewState, evt, viewinst);
				if (findInImViewState(ivsfdfs)>=0 || findEventRecord(curAct, newViewState) >= 0){
				//existed interact state!
					Log.d(TAG, "existed interact state!");
					goBackToPos(0);
					return 0;
				}
				else {
				//new interact state
					Log.d(TAG, "new interact state!");
					
					//ALF:
					//we get the risk: if a landing state is simple enough and can only reach other activity by a existed window, we may miss this landing state
					actionCnt=0;
					
					imViewState.add(ivsfdfs);
    		    	eventPath.add(new eventRecord(curAct.toString(),evt,newViewState,0));
    		   /* 	for (int ii=0;ii<eventPath.size();ii++){
					   Log.d(TAG, ii+":"+eventPath.get(ii).viewState.toString());
					   
				   }*/
					return 1;
				}
			
		}
	//	Log.d(TAG,curViewState.toString());

	//	Log.d(TAG,newViewState.toString());
		return 0;
	}
	

    
    
	void constructTransitionMap(){
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		ArrayList<String> curViewState=getCurrentViewState();
		imViewState.clear();
		actionCnt=0;
	//	int loca=addActivityState(curAct,curViewState, INTERACT_STATE);
	//	(activityData.get(curAct).interactStateProceedings).set(loca, 0);
		//remember to change drawStateID!!
		Log.d(TAG,curViewState.toString());
		int initStateID=addActivityInitState(curAct,curViewState,0);
		addActivityLandingState(curAct,curViewState,initStateID,0);
		eventPath.get(eventPath.size()-1).location=0;
		eventPath.get(eventPath.size()-1).initStateNo=initStateID;
	//	int eventPathPos=eventPath.size()-1;
		int startPathPos=eventPath.size()-1;
	//	addEventRecord(curAct,null,curViewState,0);
		imViewState.add(new imViewStateForDFS(curViewState, null, null));
		
		while (true){
			
			curViewState=getCurrentViewState();
			if (curViewState.size()==0){
				goBackToPos(0);
				continue;
			}
			Log.d(TAG, curViewState.toString());
			int lastpos=eventPath.get(eventPath.size()-1).location;
			int lastlistviewtype=eventPath.get(eventPath.size()-1).listViewType;
		    ArrayList<View> alltopview=getTopViewGroups();
		    ArrayList<String> vq=new ArrayList<String>();
		    for (int i=0;i<alltopview.size();i++)
		      vq.add(getViewSignature(alltopview.get(i)));

	//		Log.d(TAG, "here1");
	
		    //start to interact
		    boolean allInteractionAreDone=false;
	//	    View targetView=null;
		    int cntpos=0;
		    int front=0,rear=vq.size();
		    ArrayList<String> scrollViewList=new ArrayList<String>();
		    while (front<rear){

		//    	View tvi=vq.get(front);
		    	curAct=getCurrentActivity();
		    	View tvi=findViewBySignature(vq.get(front),front);
			//	Log.d(TAG, "front "+front+" is "+getViewSignature(tvi));
		    	if (tvi==null) {front++;continue;}
		    	if (tvi instanceof ViewGroup
		    		&& !(!CONSIDERLISTVIEWCHILD && tvi instanceof ListView)
		    		&& !(tvi instanceof WebView)
		    		//&& !(tvi instanceof ScrollView)
		    		){
			    		ViewGroup tvgi=(ViewGroup)tvi;
			    		int childcount=tvgi.getChildCount();
				    	for (int i=0;i<childcount;i++){
				    		View ctvi=tvgi.getChildAt(i);
				    		if (considerView(ctvi,tvgi) && specialBlock(ctvi)) {
					    		vq.add(getViewSignature(ctvi));
						//		Log.d(TAG, "add "+ctvi+" father:"+tvgi);
					    		rear++;
				    		}	    		
				    	}
				/*    	Log.d(TAG,"the queue:");
				    	for (int k=0;k<vq.size();k++)
				    		Log.d(TAG,vq.get(k));
				  */  	
				    	if (tvi instanceof ScrollView)
				    		scrollViewList.add(getViewSignature(tvi));
		    	}
		    	else if (considerView(tvi,null) && specialBlock(tvi)){
		    	    //conduct event
		    	//	Log.d(TAG, "consider "+getViewSignature(tvi));
		    		    if (!(tvi instanceof ViewGroup)){
		    		    	
		    			//	Log.d(TAG, getCurrentViewState().toString());
		    		    	if (tvi instanceof EditText){
		    		    		EditText etvi=(EditText)tvi;
		    		    		if (cntpos==lastpos){
		    		    			lastpos++;
		    		    			event evt=generateTextInputEvent(etvi);
    		    		    	/*	int didEffectiveTextInput=executeEvent(evt,false);
    		    		    		//do nothing here, rely on auto fill
    		    		    		if (interactEffect(curAct,curViewState, evt, tvi)==1) {
        		    		    		//new event is added into eventPath
        		    		    			Log.d(TAG, "new view state, go deeper!");
        			    		          break;
        		    		    	}    
    		    		    		if (didEffectiveTextInput>0){
    		    		    			eventPath.add(new eventRecord(curAct.toString(),evt,getCurrentViewState(),0));
    		    		    			Log.d(TAG, "effective text input, go deeper!");
      			    		            break;
    		    		    		}
    		    		    		*/
        		    		    	Log.d(TAG, "next move!");
		    		    		}
		    		    		cntpos++;
		    		    	}
		    		    	else {
		    		    		
		    		    		if (!ONLYCONSIDERCLICKABLE || tvi.isClickable()){
		    		    		//	Log.d(TAG, cntpos+" "+lastpos+"1 consider "+getViewSignature(tvi));
		    		    			if (cntpos==lastpos){
			    		    	//		Log.d(TAG, "2 consider "+getViewSignature(tvi));
			    		    		    //		(activityData.get(curAct).interactStateProceedings).set(loca,lastpos+1);
			    		    			lastpos++;
	    		    		    		Log.d(TAG, "click on "+getViewSignature(tvi));
	    		    		    		event evt=generateClickEvent(tvi);
	    		    		    		executeEvent(evt,false);
	    		    		    	//	evt.eventid=0;
	    		    		    	//	clickOnView(tvi,evt);	    	
	    		    		    	//	Log.d(TAG, tvi.toString());
	    		    		    		if (interactEffect(curAct,curViewState, evt, tvi)==1) {
	    		    		    		//new event is added into eventPath
	    		    		    			Log.d(TAG, "new view state, go deeper!");
	    			    		          break;
	    		    		    		}
	    		    		        	
	    		    		    		
	    		    		    		Log.d(TAG, "next move!");
			    			        }
			    		    		cntpos++;
		    		    		}
		    		    		
		    		    	}
		    		    	
				    		
		    		    }
		    		    else {
		    		    //viewgroup
		    		    	if (tvi instanceof ListView){
		    		    
		    		    		ListView lvi=(ListView)tvi;
			    		    	if (lastlistviewtype==-1){
			    		    		if (cntpos==lastpos){
			    		    			lastpos++;
			    		    			lastlistviewtype=decideListViewType(getViewSignature(lvi));  
			    		    			Log.d(TAG, "The list view type is "+lastlistviewtype);
			    		    		}
			    		    	}
			    		    	//for decide
		    		    		cntpos++;
		    		    		
			    		    	curAct=getCurrentActivity();
			    		    	tvi=findViewBySignature(vq.get(front),front);
			    		    	if (tvi==null) {front++;continue;}
			    		    	lvi=(ListView)tvi;
			    		    	if (lastlistviewtype==0){
			    		    	//all the same!	
			    		    		Boolean isbreak=false;
			    		    		if (cntpos==lastpos){
			    		    			lastpos++;
			    		    			event evt=generateListEvent(lvi,-1);
	    		    		    		executeEvent(evt,false);		  
	    		    		    		if (interactEffect(curAct,curViewState, evt, tvi)==1) {
	        		    		    		//new event is added into eventPath
	        		    		    			Log.d(TAG, "new view state, go deeper!");
	        			    		          break;
	        		    		    	}       		    		        	
	        		    		    	Log.d(TAG, "next move!");
			    		    		}
			    		    		cntpos++;
			    		    		lastlistviewtype=-1;
			    		    		lastpos-=1;
			    		    		cntpos-=1;
			    		    	}
			    		    	if (lastlistviewtype==1){
				    		    //may be different
			    		    		Boolean isbreak=false;
			    		    		for (int li=0;li<lvi.getCount();li++){
			    		    			if (cntpos==lastpos){
				    		    			lastpos++;
				    		    			event evt=generateListEvent(lvi,li);
		    		    		    		executeEvent(evt,false);		    		
		    		    		    		if (interactEffect(curAct,curViewState, evt, tvi)==1) {
		        		    		    		//new event is added into eventPath
		        		    		    			Log.d(TAG, "new view state, go deeper!");
		        		    		    			isbreak=true;
		        			    		          break;
		        		    		    	}       		    		        	
		        		    		    	Log.d(TAG, "next move!");
		    			    		    	curAct=getCurrentActivity();
		        		    		    	tvi=findViewBySignature(vq.get(front),front);		        		    		    	
		        		    		    	lvi=(ListView)tvi;
		        		    		    	if (tvi==null) {break;}
				    		    		}
				    		    		cntpos++;
			    		    		}
			    		    		if (isbreak) break;
			    		    		else {lastlistviewtype=-1;
			    		    		 if (lvi==null) {front++; continue;}
			    		    		  lastpos-=lvi.getCount();
			    		    		  cntpos-=lvi.getCount();
			    		    		}
			    		    		
				    		    }
			    		    	
			    		    }
			    		    else {
			    		    	if (tvi instanceof WebView){
				    		    	WebView wvi=(WebView)tvi;
				    		    	if (cntpos==lastpos){
			    		    			lastpos++;
			    		    			event evt=generateWebViewEvent(wvi);
	    		    		    		executeEvent(evt,false);		    		
	    		    		    		if (interactEffect(curAct,curViewState, evt, tvi)==1) {
	        		    		    		//new event is added into eventPath
	        		    		    			Log.d(TAG, "new view state, go deeper!");
	        			    		          break;
	        		    		    	}       		    		        	
	        		    		    	Log.d(TAG, "next move!");
			    		    		}
			    		    		cntpos++;
			    		       }
			    		    	else {
			    		    		//scroll
			    		    	//	if () 		solo.scrollDown()
			    		    	/*	if (tvi instanceof ScrollView){
			    		    			ScrollView svi=(ScrollView)tvi;
			    		    			
			    		    		}
			    		    		*/
			    		    	}
			    		    }
		    		    }
		    	}	    	
		    	front++;
		   }
		    boolean donescroll=false;
		    if (front >= rear){
		    	//scroll
		    	Log.d(TAG, "do scroll!");
	    		Boolean isbreak=false;
	    		
		    	for (int si=0;si<scrollViewList.size();si++){
		    		curAct=getCurrentActivity();
		    		curViewState=getCurrentViewState();
		    		ScrollView svi=(ScrollView)findViewBySignature(scrollViewList.get(si),-1);
		    		if (svi==null) {cntpos++; continue;}
		    		if (cntpos==lastpos){
		    			lastpos++;
		    			//scroll down
		    			int sx=svi.getScrollX();
		    			int sy=svi.getScrollY();
		    			sy+=svi.getHeight()-1;
		    			event evt=generateScrollEvent(svi,sx,sy);
		    			if (!blockedScroll(svi)){
    		    		  executeEvent(evt,false);
		    			}
    		    		if (interactEffect(curAct,curViewState, evt, svi)==1) {
	    		    		//new event is added into eventPath
	    		    			Log.d(TAG, "new view state, go deeper!");
	    		    			isbreak=true;
		    		          break;
	    		    	}       		    		        	
	    		    	Log.d(TAG, "next move!");
		    		}
		    		cntpos++;		    		
		    	}
	    		
		    	if (!isbreak)
		    		donescroll=true;
		    }
		    boolean doneAutoFill=false;
		    
		    if (donescroll){
	    		curAct=getCurrentActivity();
	    		curViewState=getCurrentViewState();
		    	Log.d(TAG, "do auto fill!");
	    		Boolean isbreak=false;
		    	if (cntpos==lastpos){
	    			lastpos++;
	    			//fill the current view
	    			event evt=generateAutoFillEvent(2);
		    		int didEffectiveAutoFill=executeEvent(evt,false);		    		
		    		if (interactEffect(curAct,curViewState, evt, null)==1) {
    		    		//new event is added into eventPath
    		    			Log.d(TAG, "new view state, go deeper!");
    		    			isbreak=true;
    		    	}       		
		    		else {
		    			if (didEffectiveAutoFill>0){
    		    			eventPath.add(new eventRecord(curAct.toString(),evt,getCurrentViewState(),0));
    		    			Log.d(TAG, "effective auto fill, go deeper!");
    		    			isbreak=true;
    		    		}
		    			
		    		}
    		    	Log.d(TAG, "next move!");
	    		}
	    		cntpos++;
	    		if (!isbreak)
	    			doneAutoFill=true;
		    }
		    if (doneAutoFill){
		    //getout because all actions are down
		    	//do menu		
	    		curAct=getCurrentActivity();
	    		curViewState=getCurrentViewState();
		    	Log.d(TAG, "do menu!");
		    	boolean noeffect=true;
		    	if (cntpos==lastpos){
		 //   		Log.d(TAG, "in here1");
	    			lastpos++;
	    			event evt=generateKeyEvent(solo.MENU);
		    		executeEvent(evt,false);		    		
		    		if (interactEffect(curAct,curViewState, evt, null)==1) {
    		    		//new event is added into eventPath
		    			noeffect=false;
		    			Log.d(TAG, "new view state, go deeper!");
	    		    }	
		    		else 
			    		allInteractionAreDone=true;
	    		}    
		    	cntpos++;

	 //   		Log.d(TAG, "in here2 "+cntpos+" "+lastpos);
		    	if (noeffect){
		//    		if (cntpos>=lastpos)
		    	//listview should only have the decide time, if u have time, please improve this
		    			allInteractionAreDone=true;
		    	}
		    	
		    }
		    
		    Log.d(TAG, "allInteractionAreDone=="+allInteractionAreDone);
		    
		    if (!allInteractionAreDone){
		    //out because we go deeper in dfs
		    	//store lastpos
		    	eventPath.get(eventPath.size()-2).location=lastpos;
		    	eventPath.get(eventPath.size()-2).listViewType=lastlistviewtype;
		    }
		    else { 
		    //out because we are done with this view state
		    	if (eventPath.size()-1 == startPathPos){
		    		Log.d(TAG, "done for this activity!");
		    		break;
		    	}
		    	Log.d(TAG, "done for this view!");
		    	goBackToPos(1);
		    }
		    
		}

	}
	void writeRecord(String curActName){
		File datafile = new File(activityRecordFileFolder+curActName);
		Log.d(TAG, "start to write!");				
		try {
			datafile.createNewFile();
			BufferedWriter fop = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(datafile)));
	        fop.write(activityData.get(curActName).toString());
	        fop.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.d(TAG, e.toString());//.printStackTrace());
			;
		}	
		Log.d(TAG, "done write!");
	}
	
	
	boolean needUpdate(String curActName){
		if (curActName.equals("")){
			return true;
		};
		return false;
	}

	public void traverseTransitionMapForCreation(){
		Activity curAct=getCurrentActivity();
	    String curActName=getCurrentActivity().getClass().getName();
		eventPath.add(new eventRecord(curAct.toString(),null,getCurrentViewState(),0));
		activityPath.add(new activityNode(curAct.toString(),0,0));
		eventPath.get(eventPath.size()-1).initStateNo=0;
		
	    activityRecord firstarcur=activityData.get(curActName);	
	    if (firstarcur!=null && firstarcur.outInitStateOptions.size()>0 && firstarcur.outInitStateOptions.get(0).size()!=0){
	    //valid
	    	int initState=-1;
	    	initState=addActivityInitState(curAct,getCurrentViewState(),1);
	    	while (initState!=0 && LAUNCHCONFIRM){	    	  
	    	  getBackToBegining();
	    	  initState=addActivityInitState(curAct,getCurrentViewState(),1);
	    	}
	    }
	    
		if (HUMANMODE){
			if (MANUALGRAPHGENONLY){
				recordRelaunch();
				addOnCreateStat(curActName,0);
			}
			humantraverse();
			return;
		}
		
	//	int initState=0;
		while (true) {
			curAct=getCurrentActivity();
		    curActName=getCurrentActivity().getClass().getName();
		    int initState=activityPath.get(activityPath.size()-1).initState;
		    int actLoc=activityPath.get(activityPath.size()-1).actLocation;
		    Log.d(TAG,"initState: "+initState+"  actLoc: "+actLoc);
		    activityRecord arcur=activityData.get(curActName);	
		    if (arcur==null || arcur.outInitStateOptions.size()<=initState 
		    		|| arcur.outInitStateOptions.get(initState).size()==0
		    		|| needUpdate(curActName)){
		    	if (!needUpdate(curActName))
		    	  Log.d(TAG, "no valid record! go contruct one!");
		    	else
		    		Log.d(TAG, "go update it!");
		    	constructTransitionMap();
		    	writeRecord(curActName);
		    	
				curAct=getCurrentActivity();
			    curActName=curAct.getClass().getName();
				if (arcur==null)
					arcur=activityData.get(curActName);
			//	activityPath.get(activityPath.size()-1).actLocation=0;
			//	activityPath.get(activityPath.size()-1).initState=0;
		    }
		    
		    ArrayList<eventTrace> aet=arcur.outInitStateOptions.get(initState);
		    while (actLoc<aet.size()){
		    	curAct=getCurrentActivity();
		    	curActName=curAct.getClass().getName();
		    	eventTrace et=aet.get(actLoc);
		    	String newActName=et.targetActivity;
		    	int newActInitState=et.targetInitStateID;

		    	Log.d(TAG, "[activity transition] try actLoc "+actLoc+" of "+curActName+"("+initState+")"+" target: "+newActName+"("+newActInitState+")");
		 //   	Boolean newAct=true;
		 //   	activityRecord nextAct=activityData.get(newActName);
		//	    if (true || nextAct==null || nextAct.outInitStateOptions.get(newActInitState).size()==0){
		    	boolean nocircle=true;
		    	for (int k=0;k<activityPath.size();k++)
		    		if (activityPath.get(k).activity.equals(newActName) && activityPath.get(k).initState==newActInitState){
		    			nocircle=false;
		    			break;
		    		}
		    	boolean notdone=true;
		    	for (int k=0;k<doneActivities.size();k++)
		    		if (doneActivities.get(k).activity.equals(newActName) && doneActivities.get(k).initState==newActInitState){
		    			notdone=false;
		    			break;
		    		}
		    	
		    	if (nocircle && notdone){
			    //make the move!
		    		//do{
		    		//ensure we are at curAct
		    		
		    		
		    		
		    		
			    	int eppos=eventPath.size()-1;
		/*	    	for (int ii=0;ii<eventPath.size();ii++){
			    		Log.d(TAG, ii+":"+eventPath.get(ii).activity);
			    		if (eventPath.get(ii).action!=null)
			    			Log.d(TAG, "--" +
			    					""+eventPath.get(ii).action.toString());
			    	}*/
			    	Log.d(TAG,"curAct: "+curAct.toString());
			    	Log.d(TAG,eppos+"");
			    	while (eppos>=0 && eventPath.get(eppos).activity.equals(curAct.toString())) eppos--;
			    	Log.d(TAG,eppos+"");
			    	eppos++;
			    	eppos++;
			    	int j=0;
			    	while (eppos<eventPath.size() && eventPath.get(eppos).action.compareTo(et.eventtrace.get(j))==0){
			    		j++;
			    		eppos++;
			    	}
			    	eppos--;
			    	Log.d(TAG,eppos+"");
			    	
		    			
			    	int bj=j;
			    	int waittime=0;
			    	while (true){
			    		goBackToPos(eventPath.size()-1-eppos);
			    		solo.sleep(2000*waittime);
				    	for (j=bj;j<et.eventtrace.size();j++){
				    		event e=et.eventtrace.get(j);
				    		Log.d(TAG, e.toString());
				    		executeEvent(e,false);				    		
				    		eventPath.add(new eventRecord(getCurrentActivity().toString(),e,getCurrentViewState(),-1));
				    	}
				    	afterEventTrace();
				   // 	Log.d(TAG,getCurrentActivity().getClass().getName()+"--"+newActName);
				    	if (getCurrentActivity().getClass().getName().equals(newActName))
				    		break;
				    	Log.d(TAG,"BAD landing! Redo it!");		
				    	waittime++;
				    	if (waittime>=5)
				    		break;
			    	}	
			    	//Log.d(TAG,getCurrentActivity().getClass().getName()+"--"+newActName);
		    		//}while (!getCurrentActivity().getClass().getName().equals(newActName));
					
		    		
			//    	Activity newAct=getCurrentActivity();
			    	
			    	if (getCurrentActivity().getClass().getName().equals(newActName)){
			    		Log.d(TAG,"successfully landing!");
			    		activityPath.add(new activityNode(newActName,newActInitState,0));
						eventPath.get(eventPath.size()-1).initStateNo=newActInitState;
						break;
			   	    }
			    	else {
                      Log.d(TAG,"skip bad landing!");
			    	  goBackToPos(eventPath.size()-1-eppos);
			    	}
			    //	Log.d(TAG,"BAD landing!");
			   /* 	else {
			    		try {
							Log.d(TAG,"[OMG]the current activity is not the one in the data!");
							this.tearDown();
						} catch (Exception e) {
							e.printStackTrace();
						}
			    	}*/
			    	
			    }
			    actLoc++;		    	
		    }
		    if (actLoc<aet.size()){
		    //already made the next move	
			    actLoc++;	
		    	activityPath.get(activityPath.size()-2).actLocation=actLoc;
		    	Log.d(TAG,"set "+activityPath.get(activityPath.size()-2).activity+" to "+activityPath.get(activityPath.size()-2).actLocation);
		    }
		    else {
		    //nothing to do, so go back
		    	doneActivities.add(activityPath.get(activityPath.size()-1));
		    	activityPath.remove(activityPath.size()-1);
		    	if (activityPath.size()==0)
		    		break;
		    	else {
		    	//go back to last activity
		    		String lastAct=null;
		    		int lastloc=-1;
		    		for (int j=eventPath.size()-2;j>=0;j--)
		    			if (!eventPath.get(j).activity.equals(eventPath.get(j+1).activity)){
		    				lastAct=eventPath.get(j).activity;
		    				lastloc=j;
		    				break;
		    			}	    	
		    		
		    		if (lastAct==null || lastloc==-1){
		    			try {
							Log.d(TAG,"[OMG]cannot find lastAct!");
							this.tearDown();
						} catch (Exception e) {
							e.printStackTrace();
						}
		    		}
		    		
		    		
		    		
					
		    		
		    		Boolean outofArea=false;
			    	int backnum=-1;
			    	ArrayList<String> before=getCurrentViewState();
			    	while (!getCurrentActivity().toString().equals(lastAct)) {					
						ArrayList<String> tempViewState=getCurrentViewState();
						boolean toomanybackwithnoeffect=false;
						   if (compareInteractState(before, tempViewState)==0){
							   if (backnum>BACKLIMIT)
								   toomanybackwithnoeffect=true;			   
						   }
						   else {
							   before=tempViewState;
						       backnum=0;
						   }
					   if (tempViewState.size()==0 || toomanybackwithnoeffect || !isInThisApp(getCurrentActivity())){
						   Log.d(TAG, "out of current activity! 3");
						   getBackToBegining();
						   outofArea=true;
						   break;
					   }
					       Log.d(TAG, "go back 4");
					       if (getCurrentActivity().getClass().getName().equals("com.newegg.app.activity.home.Main")){
							   Log.d(TAG, "about to leave New Egg app, restart now");
							   outofArea=true;
							   getBackToBegining();
							   break;
						   }
						   else
						     executeGoBack();
						   backnum++;
					}
			   // 	if (outofArea)
			    		this.goBackToPos(eventPath.size()-1-lastloc);
		    	}
		    	
		    }

			
			
		}
		
	}
	int getEventPost(event evt){
		for (int i=0;i<eventPath.size();i++)
			if (eventPath.get(i).action==evt)
				return i; 
		return -1;
	}
	int executeEvent(event evt, boolean keepkeyboard){
		int ret=0;
		String beforeAct=getCurrentActivity().toString();
	    String beforeActName=getCurrentActivity().getClass().getName();
//		Log.d(STATTAG,"before execute event");
//		if (ATTACKMODE)
	//		att.clearAttackRes();
		if (evt.eventid==0){
		//click event
			Log.d(TAG,"EVENT: click on ("+evt.x1+","+evt.y1+")");
//			Log.d(STATTAG,"right before execute event");
			solo.clickOnScreen(evt.x1,evt.y1);
//			Log.d(STATTAG,"right after execute event");
			solo.sleep(CLICK_SLEEPTIME);
		//	solo.getClass().getMethod(name, parameterTypes)
		}
		if (evt.eventid==1){
		//scroll event
			Log.d(TAG,"EVENT: scroll ("+evt.scrollx+","+evt.scrolly+") in <"+evt.viewid+">");
			View sv=null;
			if (evt.viewid==-1)
				sv=getAScrollViewFromCurrentScreen();
			else sv=getViewFromCurrentScreen(evt.viewid);	
			
		//	Log.d(TAG,""+(sv instanceof ScrollView));
			ScrollView scrollview=(ScrollView)(sv);
			Log.d(TAG,""+solo.scroller.scrollScrollToView(scrollview, evt.scrollx,evt.scrolly));//evt.direction);
			solo.sleep(CLICK_SLEEPTIME);
		}
		if (evt.eventid==2){
		//key event
			Log.d(TAG,"EVENT: click on key <"+evt.keyno+">");
			solo.sendKey(evt.keyno);	
			solo.sleep(CLICK_SLEEPTIME);	
		}
		if (evt.eventid==3){
			//input event
			Log.d(TAG,"EVENT: input text in <"+evt.viewid+">");
			int viewid=evt.viewid;
			EditText inputview=(EditText)(getViewFromCurrentScreen(viewid));
			ret+=fillAppropriateText(getCurrentActivity(),viewid,inputview);
			solo.sleep(CLICK_SLEEPTIME);	
		/*	if (input!=null){
			  inputview.setText(input);;
			  Log.d(TAG,"EVENT: enter "+input+" in view "+getViewSignature(inputview));
			}
			else 
			  Log.d(TAG,"skip the one");
			  */
		}
		if (evt.eventid==4){
			//list event
			
			int viewid=evt.viewid;
			View lv=null;
			if (evt.viewid==-1)
				lv=getAListViewFromCurrentScreen();
			else lv=getViewFromCurrentScreen(evt.viewid);	
			//block some part of the list
			ListView listview=(ListView)(lv);
			int pos=evt.listviewline;
			if (listview!=null){
			if (evt.listviewline==-1){
				Log.d(TAG,"EVENT: click random line in <"+evt.viewid+">");		
				do {
				pos=(int) (getRandomDouble()*(listview.getChildCount()));
				if (!considerListView(viewid, pos,listview.getChildCount())) break;
		//0		solo.scrollListToLine(listview, pos);
				ArrayList<String> curViewState=getCurrentViewState();
				executeEvent(generateClickEvent(listview.getChildAt(pos)),false);
				ArrayList<String> newViewState=getCurrentViewState();
				if (compareInteractState(curViewState, newViewState)!=0)
					break;
				}while (true);	
				evt.listviewline=pos;
			}
			else {
			   Log.d(TAG,"EVENT: click on line "+pos+" in <"+evt.viewid+">");
			   if (considerListView(viewid, pos,listview.getCount())) {
			   while (!(listview.getLastVisiblePosition() >= pos && listview.getFirstVisiblePosition() <= pos)){
					if (listview.getLastVisiblePosition() < pos)
						solo.scrollDownList(listview);
					if (listview.getFirstVisiblePosition() > pos)
						solo.scrollUpList(listview);
				}
		//	   solo.scrollListToLine(listview, pos);
				int childno=pos-listview.getFirstVisiblePosition();				
    			executeEvent(generateClickEvent(listview.getChildAt(childno)),false);
			   }

		//       solo.scrollListToLine(listview, pos);
		//	   executeEvent(generateClickEvent(listview.getChildAt(pos)));			
			}
			}
		}
		if (evt.eventid==5){
		//webview event
			Log.d(TAG,"EVENT: click webview in <"+evt.viewid+">");
			WebView webview=null;
			if (evt.viewid==-1)
				webview=(WebView) getAWebViewFromCurrentScreen();
			else webview=(WebView)getViewFromCurrentScreen(evt.viewid);	

			clickLinkWebView(webview);
			solo.sleep(CLICK_SLEEPTIME);
		}
		if (evt.eventid==6){
			//click and go back to last activity instance
			//generate a click event first
			event et=new event();
			
			
			et.eventid=evt.direction;
			if (et.eventid==0){
				et.x1=evt.x1;
				et.y1=evt.y1;		
			}
			if (et.eventid==1){
				et.scrollx=evt.scrollx;
				et.scrolly=evt.scrolly;
				et.viewid=evt.viewid;
			}
			if (et.eventid==2){
				et.keyno=evt.keyno;
			}
			if (et.eventid==3){
				et.viewid=evt.viewid;	
			}
			if (et.eventid==4){
                et.listviewline=evt.listviewline;
				et.viewid=evt.viewid;	
			}
			if (et.eventid==5){
				et.viewid=evt.viewid;		
			}
			if (et.eventid==7){
				et.fillmode=evt.fillmode;		
			}

			//execute it
			executeEvent(et,false);
			//then go back			
			Activity curAct=getCurrentActivity();

			ArrayList<String> before=getCurrentViewState();
			int backnum=0;
			while (getCurrentActivity().toString().equals(curAct.toString())) {
				   Log.d(TAG, "go back 5");
				   if (getCurrentActivity().getClass().getName().equals("com.newegg.app.activity.home.Main")){
					   Log.d(TAG, "about to leave New Egg app, restart now");
					   getBackToBegining();
					   break;
				   }
				   else
				     executeGoBack();
				   if (backnum>BACKLIMIT && compareInteractState(before, getCurrentViewState())==0){
						solo.finishOpenedActivities();
						break;
				   }
				   backnum++;
			}
		}
		if (evt.eventid==7){
			//auto fill the current view
			ret=autoFillView(evt.fillmode);
			
		}
//		Log.d(STATTAG,"after execute event");
		writeAccesoryData();
		
		   Log.d(TAG,"here2");
		if (getEverythingReadyAfterEvent(evt,keepkeyboard)==-1)
			return -1;

		
		if (evt.eventid==4)
			return ret;
		
		//updating action stats
		solo.sleep(1000);
		
		if (LISTVIEWCLICKMODE) return 0;
		Activity afterAct=getCurrentActivity();
		if (!afterAct.toString().equals(beforeAct.toString())){
			
			int initpos=getInitStatePos(afterAct);
			int eventpos=getEventPost(evt);
			//eventpos!=-1: replaying 
			 if ((eventpos==-1 && initpos==-1) || (eventpos!=-1 && (initpos==-1 || initpos>=eventpos))){
		     //onCreate
				 int initID=addActivityInitState(afterAct, getCurrentViewState(), 1);
				 if (initID==-1)
					 initID=findNearestActivityInitState(afterAct, getCurrentViewState());
				 if (initID!=-1){
					 Log.d(TAG,"[onCreate]"+afterAct.getClass().getName()+"("+initID+")");
					 addOnCreateStat(afterAct.getClass().getName(), initID);	
				 }
				 else {
					 Log.d(TAG,"on create, but cannot find the init state record");
					 reportError("on create in "+afterAct.getClass().getName());
				 }
			 }
			 if ((eventpos==-1 && initpos!=-1) || (eventpos!=-1 && initpos!=-1 && initpos<eventpos)) {
				 //onResume
				 int initID=getInitStateID(afterAct, initpos);
				 int landingID=addActivityLandingState(afterAct, getCurrentViewState(), initID, 1);
				 if (landingID==-1)
					 landingID=findNearestActivityLandingState(afterAct, getCurrentViewState(), initID);
				 if (landingID!=-1){
					 Log.d(TAG,"[onResume]"+afterAct.getClass().getName()+"("+initID+")"+"("+landingID+")");
					 addOnResumeStat(afterAct.getClass().getName(), initID, landingID);	
				 }
				 else {
					 Log.d(TAG,"on resume, but cannot find the landing state record");
					 reportError("on resume in "+afterAct.getClass().getName());
				 }
			 }
			 solo.sleep(CLICK_SLEEPTIME);
		}
		return ret;
		
	}
	void executeGoBack(){
		String beforeAct=getCurrentActivity().toString();
		Log.d(TAG,"EVENT: go Back");
		Log.d(TAG,"here1!");
		solo.goBack();	
		Log.d(TAG,"here2!");
		solo.sleep(CLICK_SLEEPTIME);
		Log.d(TAG,"here3!");
		if (TARGET_PACKAGE_ID.equals("com.chase.sig.android")){
		//special case for chase
			View vi=getViewFromCurrentScreen(2131296534);
			if (vi!=null && ifViewSignatureSame(getViewSignature(vi),"android.widget.Button,[505.5,770.0],2131296534,negativeButton,Log Off,",0)){
				solo.clickOnScreen((float)505.5,(float)770.0);
				solo.sleep(CLICK_SLEEPTIME);
			}
			else 
			{
				vi=getViewFromCurrentScreen(2131296532);
				if (vi!=null && ifViewSignatureSame(getViewSignature(vi),"android.widget.Button,[214.5,728.0],2131296532,positiveButton,Cancel,",0)){
					solo.clickOnScreen((float)214.5,(float)728.0);
					solo.sleep(CLICK_SLEEPTIME);
				}
				
			}
			
			
			
		}
		Log.d(TAG,"here!");
		getEverythingReadyAfterEvent(null,false);
		Log.d(TAG," "+getCurrentViewState().toString());
		writeAccesoryData();
		solo.sleep(1000);
		if (LISTVIEWCLICKMODE) return ;
		//updating action stats
		Activity afterAct=getCurrentActivity();
		if (!afterAct.toString().equals(beforeAct.toString())){
			int beforeinitpos=getInitStatePos(beforeAct);
			int afterinitpos=getInitStatePos(afterAct);
			 if (afterinitpos==-1 || (afterinitpos!=-1 && beforeinitpos!=-1 && afterinitpos >= beforeinitpos)){
		     //onCreate
				 int initID=addActivityInitState(afterAct, getCurrentViewState(), 1);
				 if (initID==-1)
					 initID=findNearestActivityInitState(afterAct, getCurrentViewState());
				 
				 if (initID!=-1){
					 addOnCreateStat(afterAct.getClass().getName(), initID);	
				 }
				 else {
					 Log.d(TAG,"on create, but cannot find the init state record");
					 reportError("on create in "+afterAct.getClass().getName());
				 }
			 }
			 if (afterinitpos!=-1 && (beforeinitpos==-1 || afterinitpos < beforeinitpos)){
				 //onResume
				 int initID=getInitStateID(afterAct, afterinitpos);
				 int landingID=addActivityLandingState(afterAct, getCurrentViewState(), initID, 1);
				 if (landingID==-1)
					 landingID=findNearestActivityLandingState(afterAct, getCurrentViewState(), initID);
				 
				 if (landingID!=-1){
					 addOnResumeStat(afterAct.getClass().getName(), initID, landingID);	
				 }
				 else {
					 Log.d(TAG,"on resume, but cannot find the landing state record");
					 reportError("on resume in "+afterAct.getClass().getName());
				 }
			 }
			 solo.sleep(CLICK_SLEEPTIME);
		}
	}
	
	void generateTranMap(){
		File datafile = new File(activityRecordFileFolder+transMapFilename);
		Log.d(TAG,"generate transition map "+activityRecordFileFolder+transMapFilename);
		try {
			Log.d(TAG,""+datafile.exists());
			datafile.createNewFile();
			BufferedWriter fop = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(datafile)));
	//		for (int i=0;i<textFillData.size();i++)    				
      //        fop.write(textFillData.get(i).toString().replaceAll("(\\r|\\n)", "")+"\n");
			
			for (int i=0;i<acts.length;i++){
				activityRecord arcur=activityData.get(acts[i].name);
				if (arcur==null)
					continue;
				for (int j=0;j<arcur.initState.size();j++){
					String fromAct=new String(acts[i].name+"-onCreate-"+j);
					
					ArrayList<eventTrace> aet=arcur.outInitStateOptions.get(j);
					for (int k=0;k<aet.size();k++){
						String newActName=aet.get(k).targetActivity;
				    	int newActInitState=aet.get(k).targetInitStateID;
				    	String toAct=new String(newActName+"-onCreate-"+newActInitState);
				    	fop.write("1\n");
				    	fop.write(fromAct+"\n");
			    		fop.write(toAct+"\n");
				    	if (aet.get(k).maybeResume==1){
				    		fop.write("-1\n");
				    	    fop.write(fromAct+"\n");
			    		    fop.write(toAct+"\n");
				    	}

			    						
					}
					
	/*				ArrayList<eventTrace> aetBack=arcur.backInitStateOptions.get(j);
					for (int k=0;k<aetBack.size();k++){
						String newActName=aet.get(k).targetActivity;
				    	int newActInitState=aet.get(k).targetInitStateID;
				    	String toAct=new String(acts[i].name+"-onCreate-"+newActInitState);
				    	if (aet.get(k).maybeResume==0)
				    		fop.write("1\n");
				    	else 
				    		fop.write("-1\n");

			    		fop.write(fromAct+"\n");
			    		fop.write(toAct+"\n");				
					}*/
				    
				}
				
			}
		
		fop.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void getActivityInfo(){
	//get information, update with existed data
		try {
			activityWithData=new ArrayList<String>();
			
			acts = (getCurrentActivity().getPackageManager().getPackageInfo(TARGET_PACKAGE_ID,PackageManager.GET_ACTIVITIES)).activities;
	//		Log.d(TAG, acts.length+"\n");
			for (int i=0;i<acts.length;i++){
		//		Log.d(TAG, acts[i].name+"\n");
		    	actInfo.put(acts[i].name, acts[i]);
			}
			for (int i=0;i<acts.length;i++){		    	
		    	File datafile = new File(activityRecordFileFolder+acts[i].name);
		    	if (datafile.exists()){
		    		Log.d(TAG,"loading activity data of "+acts[i].name);
		    		try {
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(datafile)));
						activityRecord ar=new activityRecord(br);
				//		Log.d(TAG,"here1");
						activityData.put(acts[i].name, ar);
						activityWithData.add(acts[i].name);
			//			Log.d(TAG,"here2");
			//			Log.d(TAG,acts[i].name+";"+activityData.get(acts[i].name));
						br.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.d(TAG,e.toString());
						e.printStackTrace();
					}
		    	}
			}
			generateTranMap();
			File datafile = new File(activityRecordFileFolder+textFillFilename);
			if (datafile.exists()){
	    		Log.d(TAG,"loading text fill data from "+datafile.getName());
	    		try {
					BufferedReader br1 = new BufferedReader(new InputStreamReader(new FileInputStream(datafile)));
					int linenum=Integer.parseInt(br1.readLine());
					for (int i=0;i<linenum;i++)
					  textFillData.add(new textFill(br1.readLine()));
					Log.d(TAG,"text fill data: "+textFillData.size());
					br1.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(TAG, e.toString());
					e.printStackTrace();
				}
	    		
			}
			
			datafile = new File(activityRecordFileFolder+doneActivitiesListFilename);
			if (datafile.exists()){
	    		Log.d(TAG,"loading done activities data from "+datafile.getName());
	    		try {
					BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(datafile)));
					int linenum=Integer.parseInt(br2.readLine());
					for (int i=0;i<linenum;i++){
						String[] cols=br2.readLine().split(";");
						doneActivities.add(new activityNode(cols[0],Integer.parseInt(cols[1]),-1));
					}
					br2.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(TAG, e.toString());
					e.printStackTrace();
				}
	    		
			}
			
				for (int i=0;i<acts.length;i++){
					statData sd=new statData();
					activityStat.put(acts[i].name, sd);
				}

			datafile = new File(outputFileFoleder+traverseFilename);
			if (datafile.exists()){
	    		Log.d(TAG,"loading traverse data from "+datafile.getName());
	    		try {
					BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(datafile)));
					String line=null;
					while ((line=br2.readLine())!=null){
						String actName=line;
						statData sd=activityStat.get(actName);
						line=br2.readLine();
						String[] cols=line.split("[ \t]+");
						int i=0;
						while (i<cols.length){
							int id=-1,cnt=-1;
							for (;i<cols.length && cols[i].length()==0;i++) ;
							if (i<cols.length)
								id=Integer.parseInt(cols[i]);
							i++;
							for (;i<cols.length && cols[i].length()==0;i++) ;
							if (i<cols.length)
								cnt=Integer.parseInt(cols[i]);
							if (id!=-1 && cnt!=-1)
							 sd.onCreate.put(id, cnt);
						}
						
					}
					
					br2.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(TAG, e.toString());
					e.printStackTrace();
				}
	    		
			}
			datafile = new File(outputFileFoleder+statFilename);
			if (datafile.exists()){
				Log.d(TAG,"loading mtime data from "+datafile.getName());
	    		try {
					BufferedReader br2 = new BufferedReader(new InputStreamReader(new FileInputStream(datafile)));
					String line=null,lline=null;
					while ((line=br2.readLine())!=null){
						lline=line;						
					}					
					br2.close();
					if (lline!=null){
						String tt=lline.split(" ")[0];
						lastmtime=Long.parseLong(tt);
					}
					else
						lastmtime=-1;
					Log.d(TAG, "lastmtime: "+lastmtime);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.d(TAG, e.toString());
					e.printStackTrace();
				}
	    		
			}
			
			
			
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    int findTheMinTrial(){
    	int minnum=-1;
    	String actName=null;
    	for (int i=0;i<activityWithData.size();i++){
    		if (blockedActivities(activityWithData.get(i)))
	    		continue;
    		statData sd=activityStat.get(activityWithData.get(i));
    		activityRecord arcur=activityData.get(activityWithData.get(i));
    		int initStateNum=arcur.initState.size();
    		for (int j=0;j<initStateNum;j++){
    			int cnt=0;
        		if (sd.onCreate.get(j)!=null)
        			cnt=sd.onCreate.get(j);
        		if (cnt!=-1 && (minnum==-1 || cnt<minnum)){
        			minnum=cnt;
        			actName=activityWithData.get(i);
        		}
    		}   		
    	}
    	Log.d(TAG,actName+" needs rescue: "+minnum);
    	return minnum;
    }
    
    double findTheAverageTrial(){
    	double sum=0;
    	int sumnum=0;
    	for (int i=0;i<activityWithData.size();i++){
    		if (blockedActivities(activityWithData.get(i)))
	    		continue;
    		statData sd=activityStat.get(activityWithData.get(i));
    		activityRecord arcur=activityData.get(activityWithData.get(i));
    		int initStateNum=arcur.initState.size();
    		for (int j=0;j<initStateNum;j++){
    			int cnt=0;
        		if (sd.onCreate.get(j)!=null)
        			cnt=sd.onCreate.get(j);
        		sum+=cnt;
        		sumnum++;
    		}   		
    	}
    	Log.d(TAG,"average trial: "+sum/sumnum);
    	return sum/sumnum;
    }
    
    void disableTheMinTrial(int minnum){
    	for (int i=0;i<activityWithData.size();i++){
    		statData sd=activityStat.get(activityWithData.get(i));
    		activityRecord arcur=activityData.get(activityWithData.get(i));
    		int initStateNum=arcur.initState.size();
    		for (int j=0;j<initStateNum;j++){
    			int cnt=0;
        		if (sd.onCreate.get(j)!=null)
        			cnt=sd.onCreate.get(j);
        		if (cnt!=-1 && (cnt==minnum)){
        			sd.onCreate.put(j, -1);
        		}
    		}   		
    	}
    }
    class actCreateUnit{
    	String actName;
    	int initID;
    	public actCreateUnit(String an, int ii){
    		actName=an;
    		initID=ii;
    	}
    }
    int bfsForThePoor(String startActName, int initID, int minOverall, HashMap<Integer, Integer> badDecisions){    	
    	ArrayList<actCreateUnit> aq=new ArrayList<actCreateUnit>();
    	ArrayList<Integer> sig=new ArrayList<Integer>();
    	aq.add(new actCreateUnit(startActName,initID)); 
    	sig.add(-1);
    	int front=0,rear=aq.size();
 	    while (front<rear){
 	    	actCreateUnit ai=aq.get(front);
 	    	activityRecord arcur=activityData.get(ai.actName);
 	    	Log.d(TAG, "!1!"+ai.actName);
 	    	Log.d(TAG, "!2!"+arcur);
 	    	Log.d(TAG, "!3!"+arcur.outInitStateOptions);
 	    	ArrayList<eventTrace> aet=arcur.outInitStateOptions.get(ai.initID);
 	    	for (int i=0;i<aet.size();i++){
 	    		String tarAct=aet.get(i).targetActivity;
 	    		int tarID=aet.get(i).targetInitStateID;
 	    		if (blockedActivities(tarAct))
 		    		continue;
 	    		if (front==0 && badDecisions.get(i*10+0)!=null)
 	    			continue;
 	    		if (aet.get(i).maybeResume!=0 && tarAct.equals(ai.actName)) continue;
 	    		boolean existed=false;
 	    		for (int j=0;j<rear;j++)
 	    			if (aq.get(j).actName.equals(tarAct) && aq.get(j).initID==tarID){
 	    				existed=true;
 	    				break;
 	    			}
 	    		if (!existed){
 	    			aq.add(new actCreateUnit(tarAct,tarID));
 	    	//		Log.d(TAG,"enqueue:"+tarAct+"-"+tarID);
 	    			if (front==0)
 	    				sig.add(i);
 	    			else {
 	    				sig.add(sig.get(front));
 	    			}
 	    			rear++;
 	    		}
 	    	}
 	    	
 	    	statData sd=activityStat.get(ai.actName);
 	    	int cnt=0;
    		if (sd.onCreate.get(ai.initID)!=null)
    			cnt=sd.onCreate.get(ai.initID);
    		if (cnt==minOverall){
    			return sig.get(front);
    		}
 	    	
 	    	front++;
 	    }
    	return -1;
    }
    
    void toolPerf(){
    	Date dd=new Date();
		long mtime=dd.getTime();	
		if (lastmtime==-1 || mtime-lastmtime>PERFIDLETIME){
    	ArrayList<Integer> dataCollect=new ArrayList<Integer>();
    	for (int i=0;i<activityWithData.size();i++){
    		if (blockedActivities(activityWithData.get(i)))
	    		continue;
    		statData sd=activityStat.get(activityWithData.get(i));
    		activityRecord arcur=activityData.get(activityWithData.get(i));
    		int initStateNum=arcur.initState.size();
    		for (int j=0;j<initStateNum;j++){
    			int cnt=0;
        		if (sd.onCreate.get(j)!=null)
        			cnt=sd.onCreate.get(j);
        		if (cnt!=-1){
        			dataCollect.add(cnt);
        		}
    		}   		
    	}
    	Collections.sort(dataCollect);
    	lastmtime=mtime;
		
		
		int dcsize=dataCollect.size();
		 try {
	 			File file = new File(outputFileFoleder+statFilename);
	 			
	 			if (!file.exists()){			
	 				file.createNewFile();
	 			}
	 			
	 			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
	 			fop.write(mtime+" "+dataCollect.get(0)+" "+dataCollect.get(dcsize*1/20)+" "+dataCollect.get(dcsize*1/4-1)+" "+dataCollect.get(dcsize*2/4-1)
	 					+" "+dataCollect.get(dcsize*3/4-1)+" "+dataCollect.get(dcsize*19/20-1)+" "+dataCollect.get(dcsize-1)+"\n");
	 			
	 			fop.close();
	     	}
	       catch (Exception e) {
	 		// TODO Auto-generated catch block
	 		e.printStackTrace();
	 	  }
		}
    }
	
	public void traverseTransitionMap(){
		Activity curAct=getCurrentActivity();
	    String curActName=getCurrentActivity().getClass().getName();
		eventPath.add(new eventRecord(curAct.toString(),null,getCurrentViewState(),0));
		activityPath.add(new activityNode(curAct.toString(),0,0));
		eventPath.get(eventPath.size()-1).initStateNo=0;

	
		activityRecord firstarcur=activityData.get(curActName);	
	    if (firstarcur!=null && firstarcur.outInitStateOptions.size()>0 && firstarcur.outInitStateOptions.get(0).size()!=0){
	    //valid
	    	int initState=-1;
	    	initState=addActivityInitState(curAct,getCurrentViewState(),1);
	    	while (initState!=0 && LAUNCHCONFIRM){	    	  
	    	  getBackToBegining();
	    	  initState=addActivityInitState(curAct,getCurrentViewState(),1);
	    	}
	    }
	    if (HUMANMODE) {
	    	if (MANUALGRAPHGENONLY){
				recordRelaunch();
				addOnCreateStat(curActName,0);
			}
	      humantraverse();
		  return;
	    }
	    
	//	int initState=0;
		boolean forwardLanding=true;
		while (true) {
			curAct=getCurrentActivity();
		    curActName=getCurrentActivity().getClass().getName();
		    int initState=activityPath.get(activityPath.size()-1).initState;
		    Log.d(TAG,"initState: "+initState);
		    activityRecord arcur=activityData.get(curActName);
		//    Log.d(TAG, curActName+" "+arcur);		
		    boolean nogoodrecord=false;
		    if (arcur==null || arcur.outInitStateOptions.size()<=initState || arcur.outInitStateOptions.get(initState).size()==0){
		    	Log.d(TAG, "!!What,,,"+curAct+"("+initState+")"+" do not have proper record..."
		            +"arcur=="+arcur+" and the size is "+arcur.outInitStateOptions.get(initState).size());
		    	 nogoodrecord=true;
		    }
		    boolean decideToGoBack=false;
		    if (!nogoodrecord){
		    ArrayList<eventTrace> aet=arcur.outInitStateOptions.get(initState);
		    ArrayList<eventTrace> aetBack=arcur.backInitStateOptions.get(initState);
		    
		    HashMap<Integer, Integer> badDecisions=new HashMap<Integer, Integer>();
		    int backWardCounter=0;
		    HashMap<Integer, Integer> triedLandingState=new HashMap<Integer, Integer>();
		    boolean doresume=false;
		    if (forwardLanding && !forwardLandingBlockResume(getCurrentActivity()))
		    	doresume=true;
		    else {
		       double rand=getRandomDouble();
		       if (rand<DORESUME)
		    	   doresume=true;
		    }
		    
		    while (true){
		    	//decide what actLoc to go
			    //remember: we have 2 options: forward and back
			    int minDirect=-1; // 0-forward, 1-backward
			    int minNo=-1;
			    curAct=getCurrentActivity();
		    	curActName=curAct.getClass().getName();
		    	
		    	toolPerf();
		    	
		    	if (backWardCounter<aetBack.size() && doresume){
		    	//first get data from backward
		//    		String newActName=aetBack.get(backWardCounter).targetActivity;
		//	    	int newActInitState=aetBack.get(backWardCounter).targetInitStateID;
		//	    	int newActLandingState=aetBack.get(backWardCounter).targetLandingStateID;
		//	    	statData sd=activityStat.get(newActName);
			    	//ALF:
			    	//did not consider create here
		/*	    	if ( sd.onResume.get(newActInitState)==null){
			    		HashMap<Integer, Integer> ii=new HashMap<Integer, Integer>();
			    		sd.onResume.put(newActInitState, ii);
			    	}
		    		int cnt=0;
		    		if (sd.onResume.get(newActInitState).get(newActLandingState)!=null)
		    			cnt=sd.onResume.get(newActInitState).get(newActLandingState);
		    		
		  */ // 	    Log.d(TAG, "here "+backWardCounter+" "+aetBack.size());
		    		int k=backWardCounter;
		    		for (;k<aetBack.size();k++)
		    		  if (badDecisions.get(k*10+1)==null && aetBack.get(k).targetActivity.equals(curActName) 
		    				  && !blockedActivities(curActName) 
		    		  && triedLandingState.get(aetBack.get(k).targetLandingStateID)==null){
		    //			minOne=cnt;
		    			 minDirect=1;
		    			 minNo=k;
		    			 triedLandingState.put(aetBack.get(k).targetLandingStateID, 1);
		    			 break;
		    		  }
		            backWardCounter=k+1;
		    		
		    	}
		    	
		    	statData sdt=activityStat.get(curActName);
		    	int cntt=0;
		    	if (sdt.onCreate.get(initState)!=null)
		    			cntt=sdt.onCreate.get(initState);
		    	if (cntt<findTheAverageTrial() && (WITHSPLASH || activityPath.size()>1)){
		    		decideToGoBack=true;
		    		Log.d(TAG,cntt+" less than average trial!");
		    		break;
		    	}
		    	
		    	
		    	
		    	if (minDirect==-1 && minNo==-1) {
		    	//after backward are all done
		    /*		Double randnum=getRandomDouble();
			    	if ((activityPath.size()>1) && randnum<GOBACKRARIO){
			    		Log.d(TAG,"dice indicates "+randnum+", so go back!");		    		
			    		decideToGoBack=true;
			  //  		break;
			    	}
			  */  		    	

				    int minOne=-1;
				    ArrayList<Integer> sameMins=new ArrayList<Integer>();
				    //forward!
				    for (int k=0;k<aet.size();k++){
				    	String newActName=aet.get(k).targetActivity;
				    	int newActInitState=aet.get(k).targetInitStateID;
				    	statData sd=activityStat.get(newActName);
				    	if (blockedActivities(newActName))
				    		continue;
				    		
				    	int nocircle=0;
				    	for (int kk=0;kk<activityPath.size();kk++)
				    		if (activityPath.get(kk).activity.equals(newActName) 
				    				&& activityPath.get(kk).initState==newActInitState){
				    			nocircle++;
				    			if (nocircle>2)
				    			  break;
				    		}
				    	if (nocircle>2)
				    		continue;
				    	
				    	
				    	if (!(aet.get(k).maybeResume!=0 && (activityPath.size()-2<0 || activityPath.get(activityPath.size()-2).activity.equals(newActName)))){
				    	//no resume!!
				    		int cnt=0;
				    		if (sd.onCreate.get(newActInitState)!=null)
				    			cnt=sd.onCreate.get(newActInitState);
				    		if (badDecisions.get(k*10+0)==null && cnt!=-1 && (minOne==-1 || cnt<=minOne)){
				    			if (minOne==-1 || cnt<minOne)				    				
				    		      sameMins.clear();
				    		    sameMins.add(k);	    		   
				    		    	
				    			minOne=cnt;
				    			
				    		}
				    	}
				    }
				    
			    	Log.d(TAG,"min past trials: "+minOne);
			    	if (minOne==-1){
				    	decideToGoBack=true;
				    }
			    	else {
			    	//choose one from sameMins
		    			minDirect=0;
		    			int rand=(int)(getRandomDouble()*sameMins.size());
		    			minNo=sameMins.get(rand);
			    	}
			    	
			    	
			    	int minOverall=findTheMinTrial();
			    	if (minOverall!=-1 && minOne-minOverall>=2){
			    	//rescue the poor ones!
			    		Log.d(TAG,"needs rescue: "+minOverall);
			    	     int rescueDir=bfsForThePoor(curActName,initState,minOverall, badDecisions);
			    	     if (rescueDir!=-1){
			    	    	 minDirect=0;
				    	     minNo=rescueDir;
				    	     Log.d(TAG,"rescue direction is: forward");
				    	     
				    	     //more go back
				    	     Double randnum=getRandomDouble();
				    	     if ((activityPath.size()>1) && randnum<GOBACKRARIO){
					    		Log.d(TAG,"dice indicates "+randnum+", so go back!");		    		
					    		decideToGoBack=true;
						     }
				    	     
				    	     if ((activityPath.size()>1) && badDecisions.get(rescueDir*10+0)!=null){
				    	    	 Log.d(TAG,"bad rescue direction! go back!");		    		
						    		decideToGoBack=true;
				    	     }
			    	     }
			    	     else {
			    	       if (activityPath.size()>1)
			    	    	 decideToGoBack=true;
			    	       else {
			    	       //no one knows how to reach the poor one
			    	    	   Log.d(TAG,"no one knows how to reach the poor one....");
			    	    	   disableTheMinTrial(minOverall);			    	    	   
			    	       }
			    	     }
			    	}
			    	
			    	
				    
				    if (!decideToGoBack){
				    	String newActName=aet.get(minNo).targetActivity;
				    	int newActInitState=aet.get(minNo).targetInitStateID;
				    	Log.d(TAG,"forward:"+newActName+" "+newActInitState);
				    	statData sd=activityStat.get(newActName);
				    	int cnt=0;
			    		if (sd.onCreate.get(newActInitState)!=null)
			    			cnt=sd.onCreate.get(newActInitState);
			    		if (cnt!=-1){
				    		Log.d(TAG,"past cnt:"+cnt);
				    		sd.onCreate.put(newActInitState,cnt+1);
				    		Log.d(TAG,"after cnt:"+sd.onCreate.get(newActInitState));
			    		}
			    		else 
			    			decideToGoBack=true;
				    }
				    
		    	}
		    	
		    	
		    	
		    	
			    //backward!
	/*		    for (int k=0;k<aetBack.size();k++){
			    	String newActName=aetBack.get(k).targetActivity;
			    	int newActInitState=aetBack.get(k).targetInitStateID;
			    	int newActLandingState=aetBack.get(k).targetLandingStateID;
			    	statData sd=activityStat.get(newActName);
			    	//ALF:
			    	//did not consider create here
			    	if ( sd.onResume.get(newActInitState)==null){
			    		HashMap<Integer, Integer> ii=new HashMap<Integer, Integer>();
			    		sd.onResume.put(newActInitState, ii);
			    	}
			    		int cnt=0;
			    		if (sd.onResume.get(newActInitState).get(newActLandingState)!=null)
			    			cnt=sd.onResume.get(newActInitState).get(newActLandingState);
			    	    if (badDecisions.get(minNo*10+minDirect)==null && (minOne==-1 || cnt<minOne)){
			    			minOne=cnt;
			    			minDirect=1;
			    			minNo=k;
			    		}
			    }*/
			    //go back option
		/*	    if (activityPath.size()>1){
			    	String lastActName=curAct2curActName(activityPath.get(activityPath.size()-2).activity);
			    	statData sd=activityStat.get(lastActName);
				    activityRecord arcur1=activityData.get(curActName);
				    for (int ii=0;ii<arcur1.initState.size();ii++){
				    	if ( sd.onResume.get(ii)==null){
				    		HashMap<Integer, Integer> hashii=new HashMap<Integer, Integer>();
				    		sd.onResume.put(ii, hashii);
				    	}
					    for (int jj=0;jj<arcur1.landingState.get(ii).size();jj++){
					    	int cnt=0;
					    	if (sd.onResume.get(ii).get(jj)!=null)
					    		cnt=sd.onResume.get(ii).get(jj);
					    	if (minOne==-1 || cnt<minOne){
					    		minOne=cnt;
					    		decideToGoBack=true;
					    		break;
					    	}
					    }
					    if (decideToGoBack)
					    	break;
				    }
				    			    	
			    }
			*/    
			    
		    	if (decideToGoBack){
		    		Log.d(TAG,"decide to go back!");
		    		break;
		    	}
		    	//after decision
		    	
		    	
		    	
		    	eventTrace et=null;
		    	if (minDirect==0){		    		
			    	et=aet.get(minNo);
			    	String newActName=et.targetActivity;
			    	int newActInitState=et.targetInitStateID;
			    	Log.d(TAG, "[activity transition: Move FORWARD] try # "+minNo+" of "+curActName+"("+initState+")"+" target: "+newActName+"("+newActInitState+")");
		    	}
		    	if (minDirect==1){		    		
			    	et=aetBack.get(minNo);
			    	String newActName=et.targetActivity;
			    	int newActInitState=et.targetInitStateID;
			    	int newActLandingState=et.targetLandingStateID;
			    	Log.d(TAG, "[activity transition: Move BACKWARD] try # "+minNo+" of "+curActName+"("+initState+")"+" target: "+newActName+"("+newActInitState+")"
			    			+"("+newActLandingState+")");
		    	}

    	
		    	if (true){
			    //make the move!
			 /*   	int eppos=eventPath.size()-1;
			    	Log.d(TAG,"curAct: "+curAct.toString());
			    	Log.d(TAG,eppos+"");
			    	while (eppos>=0 && eventPath.get(eppos).activity.equals(curAct.toString())) eppos--;
			    	Log.d(TAG,eppos+"");
			    	eppos++;
			    	eppos++;
			    	int j=0;
			    	while (eppos<eventPath.size() && eventPath.get(eppos).action.compareTo(et.eventtrace.get(j))==0){
			    		j++;
			    		eppos++;
			    	}
			    	eppos--;
			    	Log.d(TAG,eppos+"");*/
		    		int eppos=eventPath.size()-1;
		    		int j=0;	
			    	int bj=j;
			    	int waittime=0;
			    	boolean rightLand=true;
			    	while (true){
			    		goBackToPos(eventPath.size()-1-eppos);
			    		solo.sleep(2000*waittime);
			    		rightLand=true;
				    	for (j=bj;j<et.eventtrace.size();j++){
				    		event e=et.eventtrace.get(j);
				    		Log.d(TAG, e.toString());
				    		if (j==et.eventtrace.size()-1){
				    		//last step
				    			solo.sleep(CLICK_SLEEPTIME);
				    		}
				    		if (executeEvent(e,false)==-1){
				    			reportError("false landing in transiting to "+et.targetActivity);
				    			rightLand=false;
				    			break;
				    		}
				    		eventPath.add(new eventRecord(getCurrentActivity().toString(),e,getCurrentViewState(),-1));
				    	}
				    	afterEventTrace();
				    	

				//    	Log.d(TAG,"herehahaha "+minDirect);
				    	if (rightLand && minDirect==1){
				    		//goback
				    		Activity ca=getCurrentActivity();
							ArrayList<String> before=getCurrentViewState();
							int backnum=0;
							while (getCurrentActivity().toString().equals(ca.toString())) {
								   Log.d(TAG, "go back 6");
								   executeGoBack();
								   if (backnum>BACKLIMIT && compareInteractState(before, getCurrentViewState())==0){
										solo.finishOpenedActivities();
						    			reportError("false landing in transiting to "+et.targetActivity);
										rightLand=false;
										break;
								   }
								   backnum++;
							}
				    		
				    	}
				    	// 	Log.d(TAG,getCurrentActivity().getClass().getName()+"--"+newActName);
				    	if (rightLand && !getCurrentActivity().getClass().getName().equals(et.targetActivity)){
			    			Log.d(TAG,"The landing activity is different from expected! should be: "+et.targetActivity+" but we get:"+getCurrentActivity().getClass().getName());		

			    			reportError("false landing in transiting to "+et.targetActivity+" but we get "+getCurrentActivity().getClass().getName());
			    			rightLand=false;
				    	}
				    	if (rightLand && minDirect==0){
				    		activityRecord nextAct=activityData.get(et.targetActivity);
				    		if (compareDrawState(getCurrentViewState(), nextAct.initState.get(et.targetInitStateID))!=0){
				    			Log.d(TAG,"The init state is different from expected!");		
			    			  rightLand=false;
				    		}
				    	}
				    	if (rightLand && minDirect==1){
				    		activityRecord nextAct=activityData.get(et.targetActivity);
				    		if (compareDrawState(getCurrentViewState(), nextAct.landingState.get(et.targetInitStateID).get(et.targetLandingStateID))!=0){
				    			Log.d(TAG,"The landing state is different from expected!");		
			    			  rightLand=false;
				    		}
				    	}
				    	if (rightLand)
				    		break;
				    	Log.d(TAG,"BAD landing! Redo it!");	
		    			//make use of these bad landings
		    			
				    	
				    	
				    	waittime++;
				    	if (waittime>=3)
				    		break;
			    	}
			    	
			    	if (rightLand){
			    		Log.d(TAG,"successfully landing!");
			    		if (minDirect==0){
			    			    int initpos=getInitStatePos(getCurrentActivity());
			    			    if (initpos==eventPath.size()-1){
					    		//onCreate				    			
					    			
					    			activityPath.add(new activityNode(et.targetActivity,et.targetInitStateID,0));
									eventPath.get(eventPath.size()-1).initStateNo=et.targetInitStateID;
									break;
					    		}
					    		else {		    			
						    		Log.d(TAG,"oh...it is actually not a new instance...no need to proceed");		
				                      badDecisions.put(minNo*10+minDirect, 1);   	
					    		}
			    			
			    		}
			    		if (minDirect==1){

		    			    int initpos=getInitStatePos(getCurrentActivity());
			    			if (initpos==-1){
					    		//onCreate
			    				Log.d(TAG,"oh...it creates a new instance when goes back...");			
			                      badDecisions.put(minNo*10+minDirect, 1);   	
			    			}
			    			else {
			    			//onResume
			    		//		addOnResumeStat(getCurrentActivity().getClass().getName(), et.targetInitStateID,et.targetLandingStateID);			    				
			    			}
			    			
			    		}
			    							
			   	    }
			    	else {
                      Log.d(TAG,"bad idea...skip bad landing!");	
                      badDecisions.put(minNo*10+minDirect, 1);                      
                      
			    	}
			    	Log.d(TAG,"Go back to the begining for next move!");	
			    	goBackToPos(eventPath.size()-1-eppos);
			    }	    	
		    }
		    }
		    forwardLanding=true;
		    if (decideToGoBack || nogoodrecord){
		    //decide to go back
		    	forwardLanding=false;
		    	if (activityPath.size()==1){
		    		getBackToBegining();
		    	}
		    	if (activityPath.size()>1){
		    	//go back to last activity
		    		activityPath.remove(activityPath.size()-1);
		    		String lastAct=null;
		    		int lastloc=-1;
		    		for (int j=eventPath.size()-2;j>=0;j--)
		    			if (!eventPath.get(j).activity.equals(eventPath.get(j+1).activity)){
		    				lastAct=eventPath.get(j).activity;
		    				break;
		    			}	    	
		    		
		    		if (lastAct==null){
		    			try {
							Log.d(TAG,"[OMG]cannot find lastAct!");
							this.tearDown();
						} catch (Exception e) {
							e.printStackTrace();
						}
		    		}
		    		lastloc=getInitStatePos(lastAct);
		    		
		    		
					
		    		
		  /*  		Boolean outofArea=false;
			    	int backnum=-1;
			    	ArrayList<String> before=getCurrentViewState();
			    	while (!getCurrentActivity().toString().equals(lastAct)) {					
						ArrayList<String> tempViewState=getCurrentViewState();
						boolean toomanybackwithnoeffect=false;
						   if (compareInteractState(before, tempViewState)==0){
							   if (backnum>BACKLIMIT)
								   toomanybackwithnoeffect=true;			   
						   }
						   else {
							   before=tempViewState;
						       backnum=0;
						   }
					   if (tempViewState.size()==0 || toomanybackwithnoeffect || !isInThisApp(getCurrentActivity())){
						   Log.d(TAG, "out of current activity!");
						   getBackToBegining();
						   outofArea=true;
						   break;
					   }
						   executeGoBack();
						   backnum++;
					}*/
		    		
			   // 	if (outofArea)
			    	this.goBackToPos(eventPath.size()-1-lastloc);
		    	}
		    	
		    	
		    	
		    	
		    	
		    }

			
			
		}
		
	}
	void humantraverse(){
		Activity curAct=getCurrentActivity();
		String curActName=curAct.getClass().getName();
		ArrayList<String> viewState=getCurrentViewState();
		while (viewState.size()>0){
			Log.d(TAG, curAct.toString());
			Activity newAct=getCurrentActivity();
			String newActName=newAct.getClass().getName();
			while (curAct.toString().equals(newAct.toString())){
				newAct=getCurrentActivity();
			}
			solo.sleep(1000);
			getEverythingReadyAfterEvent(null, false);
			
			Log.d(TAG, newAct.toString());
			newAct=getCurrentActivity();
			newActName=newAct.getClass().getName();
			
			int initpos=getInitStatePos(newAct);
			//eventpos!=-1: replaying 
			 if (initpos==-1){
		     //onCreate
				 if (MANUALGRAPHGENONLY){
					Log.d(TAG,"[onCreate]"+newActName);
					addOnCreateStat(newActName,0);
					ArrayList<String> viewstate=getFullCurrentViewState();
					Log.d(TAG,viewstate.toString());
				 }
				 else {
				 int initID=addActivityInitState(newAct, getCurrentViewState(), 1);
				 
				 if (initID!=-1){
					 Log.d(TAG,"[onCreate]"+newActName+"("+initID+")");
					 addOnCreateStat(newActName, initID);	
					 ArrayList<String> viewstate=getFullCurrentViewState();
					 Log.d(TAG,viewstate.toString());
					 /*	 Intent it=newAct.getIntent();			
					 event evt=generateStartActivityEvent(it);
					 Intent itt=new Intent(getCurrentActivity(),newAct.getClass());
					 if (it.getExtras()!=null){
					 Set<String> sstr=it.getExtras().keySet();
					 for (Iterator<String> istr=sstr.iterator();istr.hasNext();){
						 String str=istr.next();
						 Log.d(TAG,str+" "+it.getExtras().get(str));
						 itt.put
					 }
					 }
					 Intent itt=new Intent(it.getAction(),Uri.parse(it.toURI()));
					 Log.d(TAG,"sleep begin (5s)!");
					 solo.sleep(5000);
					 Log.d(TAG,"sleep end!");
					 curAct.startActivity(itt);
					 solo.sleep(2000);
					 newAct=getCurrentActivity();
						newActName=newAct.getClass().getName();
						*/
				 }
				 else {
					 Log.d(TAG,"on create, but cannot find the init state record");
					 
					 
					 if (CREATMODE){
						 if (activityData.get(curActName)==null){
						 //no record before
							 int newinitID=addActivityInitState(newAct, getCurrentViewState(), 0);
							 int initStatePos=getInitStatePos(curAct);
							 int initStateID=getInitStateID(curAct,initStatePos);
							 ArrayList<String> curActInitState=eventPath.get(initStatePos).viewState;
						
							 eventTrace et=new eventTrace();
							 et.targetActivity=newActName;
							 et.maybeResume=0;
							 et.targetInitStateID=newinitID;						
							 et.eventtrace=new ArrayList<event>();		
							 event evt=generateStartActivityEvent(newActName);
							 et.eventtrace.add(evt);
							 Log.d(TAG, "a trace:"+et.toString());
							 
							 if (et!=null){
								 //trace exist
								 //check if this is a new trace for outInit
										
								   ArrayList<eventTrace> aets=activityData.get(curActName).outInitStateOptions.get(initStateID);
								   int etid=-1;
								   for (int i=0;i<aets.size();i++){
								     eventTrace ett=aets.get(i);
									 if (ett.targetActivity.equals(et.targetActivity) && ett.targetInitStateID==et.targetInitStateID){
									   etid=i;
									   break;
									 }					
								   }
										
								   //add new trace if proper
								   if (etid==-1){
								   //new trace
								     Log.d(TAG, "new trace!");
									 aets.add(et);
									 writeRecord(curActName);
								   }	
								 }
							 
						 }
						 
					 }
					 
					 
					 ArrayList<String> viewstate=getFullCurrentViewState();
					 Log.d(TAG,viewstate.toString());
					 
				 }
				 }
			 }
			 else{
				 //onResume
				 if (MANUALGRAPHGENONLY){
						Log.d(TAG,"[onCreate]"+newActName);
						addOnResumeStat(newActName,0,0);
				 }
				 else {
				 int initID=getInitStateID(newAct, initpos);
				 ArrayList<String> viewstate=getFullCurrentViewState();
				 Log.d(TAG,viewstate.toString());
				 
				 int landingID=addActivityLandingState(newAct, getCurrentViewState(), initID, 1);
				 if (landingID==-1)
					 landingID=findNearestActivityLandingState(newAct, getCurrentViewState(), initID);
				 if (landingID!=-1){
					 Log.d(TAG,"[onResume]"+newActName+"("+initID+")"+"("+landingID+")");
					 addOnResumeStat(newActName, initID, landingID);	
				 }
				 else {
					 Log.d(TAG,"on resume, but cannot find the landing state record");
				 }
				 }
			 }
			 curAct=newAct;
			 curActName=newActName;
			 eventPath.add(new eventRecord(curAct.toString(),null,getCurrentViewState(),0));
			
		}
	}
	
	void listviewinteract(){
		
		event e=new event();
		
		if (LAUNCHER_ACTIVITY_FULL_CLASSNAME.equals("com.webmd.android.WebMDMainActivity")){
		e.eventid=0;
		e.x1=(float) 360.0;
		e.y1=(float) 712.0;
		executeEvent(e,false);
/*		e.eventid=0;
		e.x1=(float) 450.0;
		e.y1=(float) 1226.5;
		executeEvent(e,false);
	*/	}
		if (LAUNCHER_ACTIVITY_FULL_CLASSNAME.equals("com.hrblock.blockmobile.MainScreen")){
		/*	e.eventid=0;
			e.x1=(float) 360.0;
			e.y1=(float) 1224.5;
			executeEvent(e,false);
			e.eventid=0;
			e.x1=(float) 360.0;
			e.y1=(float) 484.0;
			executeEvent(e,false);
			e.eventid=4;
			e.listviewline=1;
			e.viewid=16908298;
			executeEvent(e,false);*/
while (true){
				
				e.eventid=0;
				e.x1=(float) 518.0;
				e.y1=(float) 98.0;
				executeEvent(e,false);
}
		}
		
		
		ArrayList<View> allview=solo.getCurrentViews();
		ListView lvi=null;
		for (int i=0;i<allview.size();i++){
			View vi=allview.get(i);
			if (vi instanceof ListView){
				lvi=(ListView)vi;
			}			
		}
		if (lvi!=null){
			Activity curAct=getCurrentActivity();
			String curActName=curAct.getClass().getName();
			String lviewsig=getViewSignature(lvi);
			
	//		while (true){
				
		/*		e.eventid=0;
				e.x1=(float) 518.0;
				e.y1=(float) 98.0;
				executeEvent(e,false);
				
	*/ 	String outputstr=new String("");
			for (int li=0;li<lvi.getCount();li++){
		
					while (!(lvi.getLastVisiblePosition() >= li && lvi.getFirstVisiblePosition() <= li)){
						if (lvi.getLastVisiblePosition() < li)
							solo.scrollDownList(lvi);
						if (lvi.getFirstVisiblePosition() > li)
							solo.scrollUpList(lvi);
					}
			//	   solo.scrollListToLine(listview, pos);
					int childno=li-lvi.getFirstVisiblePosition();	
					View cvi=lvi.getChildAt(childno);
					
					ViewGroup tvgi=(ViewGroup)cvi;
					int childcount=tvgi.getChildCount();
					
			    	for (int i=0;i<childcount;i++){
			    		View ctvi=tvgi.getChildAt(i);
			    		if (ctvi.getClass().getName().equals("android.widget.TextView")){    			
			    			String condstr=(String) ((TextView)ctvi).getText();
			    			outputstr+=condstr+"\n";
			    			Log.d(TAG,condstr);    
			    			
			    		} 		
			    	}
    	
				
	   	/*		event evt=generateListEvent(lvi,li);
		    		executeEvent(evt,false);		    		
    		    	Log.d(TAG, "next move!");
    		    	if (getCurrentActivity().getClass().getName().equals(curActName)) continue;
    		    	
    		    	addOnListViewStat(li+1);
    		    	
    		    	executeGoBack();
    		    	
    		    	curAct=getCurrentActivity();
    		    	lvi=(ListView)findViewBySignature(lviewsig,-1);	
    		*/    	
    		    	
	    		}
	try {
			File file = new File(outputFileFoleder+"tmp");
			
			if (!file.exists()){			
				file.createNewFile();
			}
			
			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
			fop.write(outputstr);
			
			fop.close();
 	}
   catch (Exception e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	  }
		//	}
		}
	}
	
	
	private String curAct2curActName(String act) {
		int atPos=-1;
		for (int i=0;i<act.length();i++)
			if (act.charAt(i)=='@'){
				atPos=i;
				break;
			}
		
		if (atPos==-1) return act;
		return act.substring(0, atPos);
	}
	
	public void testDisplayBlackBox() {
	//Enter any integer/decimal value for first editfield, we are writing 10
    solo.sleep(SPLASHSCREEN_SLEEPTIME);
    getEverythingReadyAfterEvent(null,false);
    Log.d(TAG,"**************NEW TEST**************");
    int id= android.os.Process.myPid();
    Log.d(TAG,"myid:"+id);
    lastestActivity=null;
    doneActivities=new ArrayList<activityNode>();
    textFillData=new ArrayList<textFill>();
    imViewState=new ArrayList<imViewStateForDFS>();
    eventPath=new ArrayList<eventRecord>();
    activityPath=new ArrayList<activityNode>();
    activityData=new HashMap<String,activityRecord>();
    actInfo=new HashMap<String,ActivityInfo>();
    activityStat=new HashMap<String, statData>();
	getActivityInfo();
	
	
	if (ATTACKMODE){
		
		solo.finishOpenedActivities();
		Log.d(STATTAG,"please launch the attack! (5 seconds)");
		solo.sleep(5000);
		solo.activityUtils.restartActivityUtils();	
		this.launchActivity(TARGET_PACKAGE_ID,launcherActivityClass,null);
		if (WITHSPLASH)
		  solo.activityUtils.restartActivityUtils();	
		getEverythingReadyAfterEvent(null,false);
	}

	
	if (LISTVIEWCLICKMODE){
	   listviewinteract();
	}
	else {
     	if (CREATMODE)
          traverseTransitionMapForCreation();
        else  traverseTransitionMap();
    }

	/*  
 //   solo.sendKey(solo.MENU);
 //   solo.sleep(1000);
    View vi=this.getViewFromCurrentScreen(0x7f09012e);
    this.clickOnView(vi);
    
	
    Log.d(TAG,getCurrentActivity().toString());
    ArrayList<String> viewstate=getCurrentViewState();
    for (int i=0;i<viewstate.size();i++)
    	Log.d(TAG,viewstate.get(i));
    
    solo.clickOnScreen((float)370 , (float) 419.5);
	solo.sleep(8000);
	viewstate=getCurrentViewState();
    for (int i=0;i<viewstate.size();i++)
    	Log.d(TAG,viewstate.get(i));
    
    
    ArrayList<View> allview=getTopViewGroups();
    ArrayList<View> vq=new ArrayList<View>();
    vq.addAll(allview);
    ListView thelist=null;
    int front=0,rear=0;
    while (front<=rear){
    	View tvi=vq.get(front);
    	front++;
    	if (tvi instanceof ListView)
    		thelist=(ListView) tvi;
    	if (tvi instanceof ViewGroup){
    		Log.d(TAG,"--root:"+viewFields(tvi));
    		ViewGroup tvgi=(ViewGroup)tvi;
    		int childcount=tvgi.getChildCount();
	    	for (int i=0;i<childcount;i++){
	    		View ctvi=tvgi.getChildAt(i);
	    		if (considerView(ctvi)) {
	    			Log.d(TAG,viewFields(ctvi));
		    		vq.add(ctvi);
		    		rear++;
	    		}	    		
	    	}
    	}
   }
    
   while (thelist!=null && solo.scrollDownList(thelist)) solo.sleep(1000);
	*/
	solo.sleep(3000);
	//Enter any interger/decimal value for first editfield, we are writing 20
	//solo.enterText(1, "20");
	//Click on Multiply button
	//solo.clickOnButton("Multiply");
	//Verify that resultant of 10 x 20
	//assertTrue(solo.searchText("200"));
	}
	@Override
	public void tearDown() throws Exception {
		
 //       if (att!=null)
 //      	att.stopAttack();
		solo.finishOpenedActivities();
	}
}
