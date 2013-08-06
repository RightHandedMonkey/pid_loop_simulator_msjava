	public class SystemDemo extends Thread {
		//K1 for effect caused by output
		private float K1 = (float)4.71567E-5; //1E-6; //6.94E-7;
		//K2 for effect caused by outside air.
		//One study indicates at 85.5 avg temp oat, 7 deg added to Te__10 in 3 Hrs
		private float K2 = (float)1.388888888E-5; //6.94E-7;
		private float K3 = (float)1.388888888E-5; // For Loading effect
		public boolean outputDisabled = false;
		//Load pid function
		
		public float p=0;
		public float i=0;
		public float d=0;
		private InfinityFunctions fct;
		protected int oatMax = 95;
		protected int oatMin = 55;
		private float ki, kp, kd,bias, lerr, intg;
		private float stpt10 =72;
		private int action;
		//Variables for slowing down scan time if CPU cant keep up
		private long avgTimeToCompute;
		private int tooLongToCompute =0;
		private float oldPeriod = 0;
		public int timeIndexSelection=0;
		public boolean computeTimeError=false;
		//End slowing down variables
		private AverageTimer avgTimer;
		private long compTime;
		private float TIME_INDEX = 1;//10000;  //1 is normal time
		private float oatPeriod = 86400; //86400 is 24 hours. 43200 sec is 12 Hours
		private float output=0;
		private float oat= 65;
		public float te__10 = 70;
		//PERIOD_IN_MILLI is the amount of time the system sleeps
		private float PERIOD_IN_MILLI = 50;
		private ProgTimer sc;
		public String debugString;
		private long counter = 0;
		public boolean stopped = false;
		//Setup lag times.  The time = deadTime * PERIOD_IN_MILLI
		private int deadTime = 0;
		public int deadTimeSec = 0;
		private int measurementLag =0;
		public int measurementLagSec=0;
		private float outputCommand=0;
		private float te__10_Act;
		//Limit how far the output can move per every calculation cycle
		private float outputTravelLimit = 1;
		private float inputTravelLimit = 1;
		//Loading variables
		private int loadType =1; //1 = Square, 2= Sine (+Only)
		private float loadValue =0;
		private float loadStrength=0;
		private float loadHz=0; //Frequency given is per day

		SystemDemo() {
			fct = new InfinityFunctions();
			//TEST CODE
			//fct.timeIndex = TIME_INDEX;
			//END TEST CODE
			oldPeriod = PERIOD_IN_MILLI;
			setScan(PERIOD_IN_MILLI);
			//fct.setScan(PERIOD_IN_MILLI/1000);
			sc = new ProgTimer();
			te__10_Act = te__10;
			bias = (float)0.5;
			action = 1;
			kp = (float)0.01;
			ki = (float)0.01;
			kd = (float)0.01;
			setDeadTime(1);
			setMeasurementLag(30);
			setLoadHurtz(10);
			setTimeIndex(3);
			avgTimer = new AverageTimer( System.currentTimeMillis() );

		}
		public float getKp() {return kp;}
		public float getKi() {return ki;}
		public float getKd() {return kd;}
		public float getErr() {return lerr;}
		public float getIntG() {return intg;}
		
		public void setDeadTime(int i) {
			deadTimeSec =i;
			deadTime = (1000/ (int)PERIOD_IN_MILLI)*i;
			if (deadTime > 0)
				outputTravelLimit = (1/( (1000*i)/PERIOD_IN_MILLI ));
			else
				outputTravelLimit = 1;
		}
		private void setScan(float i) {
			PERIOD_IN_MILLI = i;
			fct.setScan(PERIOD_IN_MILLI/1000);
		}
		public void setLoadStrength(int i) {
			if (i==0) loadStrength = 0;
			if (i==1) loadStrength = (float)K3/2;
			if (i==2) loadStrength = (float)K3;
			if (i==3) loadStrength = (float)K3*2;
		}
		public int getLoadDisplayValue() {
			return ((int)Math.round(((float)30/(K3*2))*loadValue) );
		}
		public void setLoadType(int i) {
			if (i==1) loadType = 1;
			if (i==2) loadType = 2;
			if (i==3) loadType = 3;
		}
		public void setLoadHurtz(float f) {
			//convert f per day to f per sec
			if (f>=0)
				loadHz = f/86400;
		}
		public void setMeasurementLag(int i) {
			measurementLagSec =i;
			measurementLag = (1000/ (int)PERIOD_IN_MILLI)*i;
			if (measurementLag > 0)
				inputTravelLimit = (1/( (1000*i)/PERIOD_IN_MILLI ));
			else
				inputTravelLimit = 1;
		}
		public void setTimeIndex(int i) {
			timeIndexSelection=i;
			if (i == 0) { TIME_INDEX = 1;}
			if (i == 1) { TIME_INDEX = 10;}
			if (i == 2) { TIME_INDEX = 100;}
			if (i == 3) { TIME_INDEX = 1000;}
			if (i == 4) { TIME_INDEX = 2500;}
			if (i == 5) { TIME_INDEX = 5000;}
			if (i == 6) { TIME_INDEX = 1000000;}
			//TEST CODE
			//fct.timeIndex=TIME_INDEX; 
			//END TEST CODE
		}
		public void reset() {
			fct.clearPIDTerms();
			oat = 65;
			te__10 = 70;
			counter = 0;
			//compTime = 0;
		}
		public float getStpt10() {return stpt10;}
		public void setPIDTerms(float t1, float t2, float t3) {
			kp = t1;
			ki = t2;
			kd = t3; 
			lerr=0;
			intg=0;
			fct.clearPIDTerms();
		}
		public long getCompTime() { return compTime; }
		public float getTimeIndex() { 
			return (float)TIME_INDEX;
		}

		private void setOutput(float d) {
			if (d -output > outputTravelLimit)
				output=((float)Math.min((float)Math.min(output+outputTravelLimit, d),1));
			else if (d - output < -outputTravelLimit)
				output=( (float)Math.max((float)Math.max(output-outputTravelLimit, d),0));
			else 
				output=((float)Math.min((float)Math.max(d,0),1));
		}
		private void setTE__10() {
			if (te__10_Act -te__10 > inputTravelLimit)
				te__10=te__10+inputTravelLimit;
			else if (te__10_Act - te__10 < -inputTravelLimit)
				te__10=te__10-inputTravelLimit;
			else 
				te__10=te__10_Act;
		}
		public void setOutputCommand(float d) { 
			outputCommand = d;
		}
		public float getOutputCommand() { 
			return outputCommand;
		}
		//public void setOutput(float d) { 	output = d; 	}
		private void setOat() {
			oat = (float) (76 + 7* (Math.sin((float)((float)2*Math.PI*((float)1/oatPeriod)*(float)((float)PERIOD_IN_MILLI*counter/(float)1000))))+1* Math.pow((Math.sin((float)((float)10*Math.PI*((float)1/oatPeriod)*(float)((float)PERIOD_IN_MILLI*counter/(float)1000)))),2));
		}
		
		private float calcLoadEffect() {
			if (loadType == 1) {
			    if (Math.sin((float)2*Math.PI*loadHz*((float)PERIOD_IN_MILLI*counter/(float)1000))>0)
					
			 	 	return loadStrength;
			    else 
			 	   return 0;
			}
			if (loadType == 2) {
				if (Math.sin((float)2*Math.PI*loadHz*((float)PERIOD_IN_MILLI*counter/(float)1000)) > 0)
					return (float) (loadStrength*(Math.sin((float)2*Math.PI*loadHz*((float)PERIOD_IN_MILLI*counter/(float)1000))));
			}
			if (loadType == 3) 
				return loadStrength;
			return (float) 0;
		}
		private float calcOutputEffect() { 
			return (((float)(PERIOD_IN_MILLI)/(float)1000)*output*(62-te__10)*K1 );
		}
		private float calcAmbientEffect() { 
			//return ((float)20*((float)(PERIOD_IN_MILLI*TIME_INDEX)/(float)1000)*(oat-te__10)*K2 );
			//return ((float)20*((float)(PERIOD_IN_MILLI)/(float)1000)*(oat-te__10)*K2 );
			return (((float)(PERIOD_IN_MILLI)/(float)1000)*(oat-te__10)*K2 );
		}

		private void pcalc() {
			float sgnl10 = fct.PID_Fct(te__10,stpt10,kp,ki,kd,bias,action,lerr, intg);
			p = fct.Filter_Fct((float)fct.p,p,(float)1);
			i = fct.Filter_Fct((float)fct.i,i,(float)1);
			d = fct.Filter_Fct((float)fct.d,d,(float)1);
			if (outputDisabled == false) 
				outputCommand = sgnl10;
			setOutput(outputCommand);
			lerr = InfinityFunctions.getErr;
			intg = InfinityFunctions.getIntG;
			loadValue = calcLoadEffect(); //*20; //20 is in degrees F
			te__10_Act = Math.min( Math.max( te__10 +loadValue+ calcOutputEffect() + calcAmbientEffect(), 55),95);
			setTE__10();
			setOat();
		}
		private void updateInfo() {
			debugString = "";
			debugString = debugString+"Time to calc: " +compTime +"\r\n";
			debugString = debugString+"deadTime = " +deadTime+", measurementLag: " +measurementLag +"\r\n";
			debugString = debugString+"outputTravelTime = " +outputTravelLimit+"\r\n";
			debugString = debugString+"calcOuputEffect = " +calcOutputEffect()+"\r\n";
			debugString = debugString+"calcAmbientEffect = " +calcAmbientEffect()+"\r\n";
			debugString = debugString+"Counter = " + counter + ", getTimeIndex = " +getTimeIndex()+"\r\n";
			debugString = debugString+"Oat = " +oat+"\r\nTE__10 = "+ te__10+ "\r\n";
			debugString = debugString+"Output = " +output+", outputCommand = " +outputCommand+ "\r\n";
			debugString = debugString+"Load = " +loadValue+", LoadStr = " +loadStrength+ ", LoadDisplayValue = "+ getLoadDisplayValue() +"\r\n";
		}
		public float getTE__10() {	return te__10;}
		public float getOat() {	return oat;}
		public float getOutput() {	return Math.abs(outputCommand*100);}
		
		public void run() {
			while(!stopped) {
				//sc.startTimer();
				for (int i = 0; i < TIME_INDEX; i++) {
					counter++;
					pcalc();
				}
				//outputQueue= sc.stopTimer();
				//Now using avg Timer to compute to lower time index
			    avgTimeToCompute = avgTimer.deltaT( System.currentTimeMillis() );
				//comment line below if not debugging
				//updateInfo();
				try {
					long i = (long)(PERIOD_IN_MILLI -avgTimeToCompute);
					if (i>0) {
						sleep(i);
						if (tooLongToCompute > 0)
							tooLongToCompute -= 1;
					} else {
						tooLongToCompute += 2;
						if (tooLongToCompute >20)  {
							setTimeIndex(timeIndexSelection -1);
							tooLongToCompute = 0;
							computeTimeError = true;
						}
					}
					//sleep((long)(Math.max( 0,(PERIOD_IN_MILLI -compTime)) ));
					  } catch (InterruptedException e) {}
			}
		}
	//******************** PROG TIMER CLASS ********************\\
	class ProgTimer {
		private long timer;
		private boolean isRunning = false;
		public ProgTimer() {}
		public void startTimer() { 
			if (isRunning == false) {
				timer = System.currentTimeMillis(); 
				isRunning = true;
			}
		}
		public long stopTimer() {
			if (isRunning = true) {
				isRunning = false;
				return (long) ( System.currentTimeMillis() - timer);
			}
			return 0;
		} 
	}
	//****************** END PROG TIMER CLASS *****************\\
	}
