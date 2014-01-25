package com.testcalculator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;

import android.util.Log;


public class Attack {
	private static final String TAG = "ATTACK_ALF";
	String targetApplication;
	String outputFilename_attack="/sdcard/CollectTrace/oTransTrace";
	boolean ContinueCapture;
	long SCREENPAGES=920;//852;//920;//852;
	long MAXTRANSTIME=500;
	long MAXEMPTYUTIME=100;
	long MINFIRSTCPUTIME=1;
	long MININTERTRANSTIME=1500;
	int uid,pid;
	ArrayList<Long> attackRes;
	
	public Attack(String tarapp){
		targetApplication=tarapp;
		
	}
	public void doAttack(){
		ContinueCapture=true;
		ActivityTransDetect atran=new ActivityTransDetect();
		
		updateUIDPID();
		
		if (pid==-1) System.out.println("ERROR!!pid = -1");
		
		atran.start();
	}
	public void stopAttack(){
		ContinueCapture=false;
	}
	public void clearAttackRes(){
		attackRes.clear();
	}
	
    public void outputTrace(String filename){
    	try {
/*			File file = new File(filename);
			
			if (!file.exists()){			
				file.createNewFile();
			}
			
			BufferedWriter fop = new BufferedWriter(new FileWriter(file,true));
			if (attackRes!=null){
				for (int i=0;i<attackRes.size();i++)
					fop.write(attackRes.get(i)+" ");
				fop.write("\n");
			}
			
			fop.close();*/
    	}
      catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
    	attackRes.clear();
    }
    
	class attackVector{
	//	long timestamp;
	//	int canvasnum;
		long cputime;
		long deltatime;
	//	long deltacputime;
		public String toString(){
			return "[cumCPUtime: "+cputime+", deltaTime,"+deltatime+"]";
		}
	}
	
    class ActivityTransDetect extends Thread{
		ArrayList<attackVector> transRec;
    	public void run(){
    		transRec=new ArrayList<attackVector>();
    		
    		boolean mmapPage=false,munmapPage=false;//,dropcomplete=false;
    		int lastTransCanvasNum=-1;
    		long cumutime=0,transcumutime=0,lastEffectTime=-1,beginTime=-1,lastEndTime=-1,drawstarttime=0,drawdeposittime=0;
    		long lastMMtime=-1,ldrs=-1,lasttranstime=-1;
    		boolean addDrop=false;
    		boolean inTransition=false;
    		int draw=0;
    		attackRes=new ArrayList<Long>();
    		int bothSatisify=0;
    		try {
	    		long lvmsizedrs=-1,lutime=-1;
	    		RandomAccessFile rdstatm=new RandomAccessFile("/proc/"+pid+"/statm","r");
	    		RandomAccessFile rdstat = new RandomAccessFile("/proc/"+pid+"/stat","r");	
	    		while (ContinueCapture){
	    		
	    			if (uid==-1 || pid==-1) {
						updateUIDPID();
						rdstatm=new RandomAccessFile("/proc/"+pid+"/statm","r");
						rdstat = new RandomAccessFile("/proc/"+pid+"/stat","r");	
						continue;
					}
	    			
	    			Date dd=new Date();
					long mtime=dd.getTime();
				//	dd.get
					
					if (lasttranstime!=-1 && (mtime - lasttranstime) > MININTERTRANSTIME){
				//		Log.d(TAG, "wait for next transistion!");
					}
					
	    			rdstatm.seek(0);
	    			String strstatm=rdstatm.readLine();
	    		
					String[] cols = strstatm.split("[ \t]+");
	    			long vmsizedrs=(Long.parseLong(cols[0])-Long.parseLong(cols[5]));
					long printvmsizedrs=0;
					if (lvmsizedrs!=-1){
						printvmsizedrs=vmsizedrs-lvmsizedrs;
					}
					lvmsizedrs=vmsizedrs;
					
					long drs=Long.parseLong(cols[5]);
					long printdrs=0;
					if (ldrs!=-1){
						printdrs=drs-ldrs;
					}
					ldrs=drs;
					
			
					rdstat.seek(0);	
					String strstat=rdstat.readLine();
	    			
					String[] colsstat = strstat.split("[ \t]+");
					long utime=Long.parseLong(colsstat[13]);//+Long.parseLong(colsstat[14]);
				//	long stime=Long.parseLong(colsstat[14]);
					long printutime=0;
					if (lutime!=-1){
						printutime=utime-lutime;
					}
					lutime=utime;
					
				
		/*			if (lastEffectTime==-1 || (printutime==0 && (mtime-lastEffectTime > MAXEMPTYUTIME))){
						cumutime=0;
					}
					if (printutime!=0) {
						cumutime+=printutime;
						lastEffectTime=mtime;
					}*/
					if (printutime==0){
				       if (mtime-lastEffectTime>=50 && cumutime!=0){
						  if (cumutime>2){
							  if (inTransition){
								  attackVector av=new attackVector();
								  av.cputime=cumutime;
								  av.deltatime=-1;
								  if (lastEndTime!=-1)
								     av.deltatime=beginTime-lastEndTime;
								  transRec.add(av);
									Log.d(TAG,"add "+av.toString());
							  }                             
							  lastEndTime=lastEffectTime;
						  }
						  
						  cumutime=0;
						}
					}
					else {
						if (cumutime==0)
							beginTime=mtime;
						cumutime+=printutime;
						if (cumutime>2){
						  lastEffectTime=mtime;
						}
					//	Log.d(TAG, mtime+" cumutime:"+cumutime);
					}
					Log.d(TAG, mtime+" utime:"+printutime);

					if ((printvmsizedrs/SCREENPAGES>=1 && printvmsizedrs%SCREENPAGES==0))
						mmapPage=true;
					if (printvmsizedrs < 0 && (printvmsizedrs*(-1) % SCREENPAGES)==0)
						munmapPage=true;
					
					if (printvmsizedrs!=0){
						Log.d(TAG, mtime+",vmsize-DRS: "+printvmsizedrs);
					}
					
					if (mmapPage || munmapPage) {
						    //mmap or munmap a full screen canvas
						inTransition=true;
						lastMMtime=mtime;
						mmapPage=false;
						munmapPage=false;
					}
			/*		
					    if (lastMMtime!=-1 && (mtime-lastMMtime > MAXTRANSTIME) && !dropcomplete){
					    //no drop observed because we missed it					    	
					    	if (lastTransCanvasNum==1) {
					    		Log.d(TAG, "missed the drop, add 1 mmap/munmap pair!");
					    		addDrop=true;
					    		//add mmap
						    	attackVector av=new attackVector();
								av.timestamp=mtime;
								av.canvasnum=1;
								av.cputime=utime;							
								av.deltacputime=av.cputime-transRec.get(transRec.size()-1).cputime;
								transRec.add(av);
						    	
						    	//add munmap
						    	av=new attackVector();
								av.timestamp=mtime;
								av.canvasnum=-1;
								av.cputime=utime;
								dropcomplete=true;
								
								av.deltacputime=0;
								transRec.add(av);
					    	}
					    	else {
					    		if (lastTransCanvasNum!=-1){
					    			for (int i=0;i<transRec.size();i++) 
							    		if (transRec.get(i).canvasnum>0) {
							    		  lastTransCanvasNum+=transRec.get(i).canvasnum;
							    		}
						    	}

					    		transRec.clear();
					    		lastMMtime=-1;
					    	}
					    		
					    	
					    }
					    */
					
				/*	     if (lastTransCanvasNum!=-1 && (cumDropPages >= lastTransCanvasNum)){
								dropcomplete=true;
							}
				*/			
					
				/*			if (draw==0 && transcumutime>0 && (mtime-lastEffectTime > MAXEMPTYUTIME)
									&& (transRec.size()!=0 || hasdrop)){
								draw=1;		
								drawdeposittime=1000;
								drawstarttime=mtime;
								Log.d(TAG, transcumutime+"");
							}
							
						
*/
					    
					    


					if (/*draw!=0 && (mtime-drawstarttime)>=drawdeposittime*/
				//			mtime-lastEffectTime > MAXEMPTYUTIME
							
						lastMMtime!=-1 &&	(mtime-lastMMtime > MAXTRANSTIME)
						&& (transRec.size()!=0)){
					//a transition!
						Log.d(TAG,"a transition!");
						//check if it is a fake transition
						boolean fakeTrans=false;
				/*	    if (transRec.get(0).deltacputime<=MINFIRSTCPUTIME){
					    	Log.d(TAG, "fake1!");
							  fakeTrans=true;
					    }
						if (((transRec.get(0).timestamp - lasttranstime) <= MININTERTRANSTIME)) {
					    	Log.d(TAG, "fake2!");
							fakeTrans=true;
						}*/
			//		    Log.d(TAG, "trans!");
					    

						if (!fakeTrans) {	
						//real transition!
							

							for (int i=0;i<transRec.size();i++) {
								Log.d(TAG, transRec.get(i).toString());	

								attackRes.add(transRec.get(i).cputime);
							}
							
							
								
					
     					}
						else {
							//fake!
							Log.d(TAG, "Fake Transition!");
						/*	if (addDrop && lastTransCanvasNum!=-1) {
								for (int i=0;i<transRec.size();i++) 
						    		if (transRec.get(i).canvasnum>0) {
						    		  lastTransCanvasNum+=transRec.get(i).canvasnum;
						    		}
						    	
						    		lastTransCanvasNum-=1;
							}*/
						    	
						    	
						    	
		
						}
						
						
				//		draw=0;
				
						lastMMtime=-1;
			//			cumDropPages=0;
						cumutime=0;
			//			transcumutime=0;
						addDrop=false;
						inTransition=false;
					//	dropcomplete=false;						
						transRec.clear();
					}
					
					
					
					Thread.sleep(40);
	    		}
	    		rdstat.close();
				rdstatm.close();
				Log.d(TAG, "OUTHERE!!!!!!!!!!");
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pid=-1;
	    	uid=-1;
    		
    	}
    }
    
    

	public void updateUIDPID(){
		ArrayList<String> lines=getPlinesfromPS(targetApplication);
		if (lines.size() < 1){
			Log.d(TAG, "The target app is not running now!");
			uid=-1;
			pid=-1;
			return;
		}
		String[] words = (lines.get(0)).split("[ \t]+"); 
			
		String pidstr = getParameter(words,2);
		pid=Integer.parseInt(pidstr);
		uid=getUidForPid(pid);
		Log.d(TAG, "pid: "+pid+" uid:"+uid);
		
	}
	public static String getParameter(String[] line, int no){
		int j=0;
		for (int i=0;i<line.length;i++)
			if (line[i].length()!=0){
				j++;
				if (j==no)
					return line[i];
			}
				
		return null;
	}
	public static int getUidForPid(int pid) {
	    try {
	      BufferedReader rdr = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + pid + "/status")));
	      for(String line = rdr.readLine(); line != null; line = rdr.readLine()) {
	        if(line.startsWith("Uid:")) {
	          String tokens[] = line.substring(4).split("[ \t]+"); 
	          String realUidToken = tokens[tokens[0].length() == 0 ? 1 : 0];
	          try {
	            return Integer.parseInt(realUidToken);
	          } catch(NumberFormatException e) {
	            return -1;
	          }
	        }
	      }
	    } catch(IOException e) {
	    	e.printStackTrace();
	    }
	    return -1;
	  }
	protected static ArrayList<String> getPlinesfromPS(String processName){
		String resps=executePS();
		String[] lines = resps.split("\\n");
		ArrayList<String> reslines=new ArrayList<String>();
		for (int i=0;i<lines.length;i++){
			if (lines[i].contains(processName)){
				reslines.add(lines[i]);
			}
		}
		return reslines;
		
	}
	protected static String executePS() {
		String line = null;
		try {
		Process process = Runtime.getRuntime().exec("ps");
		InputStreamReader inputStream = new InputStreamReader(process.getInputStream());
		BufferedReader reader = new BufferedReader(inputStream);
			

			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			process.waitFor();

			line = output.toString();
			reader.close();
			inputStream.close();
			reader.close();			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}

}
