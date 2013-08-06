import com.ms.wfc.ui.*;
import com.ms.wfc.core.*;
import com.ms.wfc.util.*;
import com.ms.wfc.ui.Control.ClassInfo;
import java.util.Vector;
import java.util.StringTokenizer;

public class BufferedControl extends com.ms.wfc.ui.Control {
	//NEW STUFF
	private com.ms.wfc.ui.Edit outputEdit;
	private com.ms.wfc.ui.Edit highEdit;
	private com.ms.wfc.ui.Edit lowEdit;
	
	private com.ms.wfc.ui.Label madeStptLabel;
	private com.ms.wfc.ui.ComboBox timeIndexBox;
	private com.ms.wfc.ui.Edit infoEdit;
	private com.ms.wfc.ui.Edit errEdit;
	private com.ms.wfc.ui.Edit intgEdit;
	private com.ms.wfc.ui.Edit measureEdit;
	private com.ms.wfc.ui.Edit deadEdit;	
	private com.ms.wfc.ui.Edit pEdit;
	private com.ms.wfc.ui.Edit iEdit;
	private com.ms.wfc.ui.Edit dEdit;
	private com.ms.wfc.ui.TrackBar outputBar;

	private int count = 0;
	private float lastTimeIndex = 0;
	private long compTime = 0;
	private boolean direction = false;
	private static final float freq = (float)0.1000;
	private static final int scrollSpeed = 1;

	private static final Pen blackPen = new Pen(Color.BLACK);
	private static final Pen redPen = new Pen(Color.RED);
	private static final Pen bluePen = new Pen(Color.BLUE);
	private static final Pen whitePen = new Pen(Color.WHITE);
	private static final Pen grayPen = new Pen(Color.GRAY);
	private static final Brush blackBrush = new Brush(Color.BLACK);
	private static final Brush redBrush = new Brush(Color.RED);
	private static final Brush blueBrush = new Brush(Color.BLUE);
	private static final Brush whiteBrush = new Brush(Color.WHITE);
	private static final Brush grayBrush = new Brush(Color.GRAY);
	protected static int sizeX = 320;
	protected static int sizeY = 200;
	
	private SamPaintTimer timer = new SamPaintTimer(this);
	public SystemDemo system = new SystemDemo();
	
	public void setOutputSlider(com.ms.wfc.ui.TrackBar tb) {outputBar = tb; }

	public void onClick(Object o, MouseEvent e) {
		//TODO: reverse drawRect function to figure out what to set TE__10 to on a click
		//g.drawRect(0,(int)(sizeY -InfinityFunctions.Ratio_Fct(system.getTE__10(), system.oatMin, system.oatMax,4,sizeY )),4,4);
		system.te__10 = InfinityFunctions.Ratio_Fct(e.y, 0, sizeY, system.oatMax, system.oatMin) ;
	}
	
	//GUI STUFF
	private Bitmap buffer = null;
	private static class BufferedPaintEvent extends PaintEvent {
        public final PaintEvent original;
        
        public BufferedPaintEvent(Graphics g, Rectangle clipRect, PaintEvent original) {
            super(g, clipRect);
            this.original = original;
        }
    }

    public BufferedControl() {
		this.addOnMouseUp(new MouseEventHandler(this.onClick));
        setStyle(STYLE_OPAQUE, true);
		//timeOffset = System.currentTimeMillis();
		timer.start();
		system.start();
		if (highEdit != null) highEdit.setText(""+system.oatMax+"");
		if (lowEdit != null) lowEdit.setText(""+system.oatMin);
   }
	
	public void reset() {
		count = 0;
		compTime = 0;
		system.reset();
 		if (measureEdit != null) measureEdit.setText(system.measurementLagSec +"");
		if (deadEdit != null) deadEdit.setText(system.deadTimeSec +"");
	}
	public void dispose() {
		system.stopped = true; 
		system.notifyAll(); 
		timer.stopped = true;
		timer.notifyAll();
		if (timer != null)
			timer.stop();
		if (system != null)
			system.stop();
		//if (timer != null)
		//timer.destroy();
		this.dispose();
	}
	//public void update() {}
	
	protected BufferedPaintEvent createBufferedPaintEvent(PaintEvent pe) {
        if (buffer == null) {
            buffer = new Bitmap(getClientSize().x, getClientSize().y);
        }
        Graphics g = buffer.getGraphics();
        g.setFont(getFont());
        g.setBackColor(getBackColor());
        g.setTextColor(getForeColor());
        return new BufferedPaintEvent(g, pe.clipRect, pe);
    }
    
	public void setW(int i) {
		sizeX = i;
		super.setWidth(i);
		buffer=null;
		if (highEdit != null) highEdit.setText(""+system.oatMax+"");
		if (lowEdit != null) lowEdit.setText(""+system.oatMin);
	}
	public void setH(int i) {
		sizeY = i;
		super.setHeight(i);
		buffer=null;
		if (highEdit != null) highEdit.setText(""+system.oatMax+"");
		if (lowEdit != null) lowEdit.setText(""+system.oatMin);
	}
	protected void drawValue(Graphics g) {
		//first clear line we are drawing on
		g.setPen(blackPen);
		g.setBrush(blackBrush);
		g.drawRect(0,0,4,sizeY); //204);
		//now draw values
		if (system.getLoadDisplayValue() > 0) {
			g.setPen(redPen);
			g.setBrush(redBrush);
			g.drawRect(0,sizeY+4 -system.getLoadDisplayValue(),4,sizeY);
		}
		g.setPen(grayPen);
		g.setBrush(grayBrush);
		g.drawRect(0,(int)(sizeY -InfinityFunctions.Ratio_Fct(system.getStpt10(), system.oatMin, system.oatMax,4,sizeY )),4,4);
		g.setPen(whitePen);
		g.setBrush(whiteBrush);
		g.drawRect(0,(int)(sizeY -InfinityFunctions.Ratio_Fct(system.getOat(), system.oatMin, system.oatMax,4,sizeY )),4,4);
		g.setPen(bluePen);
		g.setBrush(blueBrush);
		g.drawRect(0,(int)(sizeY -InfinityFunctions.Ratio_Fct(system.getTE__10(), system.oatMin, system.oatMax,4,sizeY )),4,4);
	}
	
	public void setTimeIndexBox(com.ms.wfc.ui.ComboBox cb) {
		if (cb != null) timeIndexBox = cb; }
	public void setInfoEdit(com.ms.wfc.ui.Edit e) {
		if (e != null) 	infoEdit = e; }
	public void setMadeSetpointLabel(com.ms.wfc.ui.Label l) {
		if (l != null) madeStptLabel = l; }
	public void setErrEdit(com.ms.wfc.ui.Edit e) {
		if (e != null) 	errEdit = e;	}
	public void setIntGEdit(com.ms.wfc.ui.Edit e) {
		if (e != null) intgEdit = e;	}
	public void setMeasurementLagEdit(com.ms.wfc.ui.Edit e) {
		if (e != null) 	measureEdit = e;	}
	public void setDeadTimeEdit(com.ms.wfc.ui.Edit e) {
		if (e != null) 	deadEdit = e;	}
	public void setPEdit(com.ms.wfc.ui.Edit e) {
		if (e != null) 	pEdit = e;	}
	public void setIEdit(com.ms.wfc.ui.Edit e) {
		if (e != null)  iEdit = e;	}
	public void setDEdit(com.ms.wfc.ui.Edit e) {
		if (e != null)  dEdit = e;	}
	//NEW STUFF
	public void setOutputEdit(com.ms.wfc.ui.Edit e) {
		if (e != null) 	outputEdit = e;	}
	public void setHighEdit(com.ms.wfc.ui.Edit e) {
		if (e != null)  highEdit = e;	}
	public void setLowEdit(com.ms.wfc.ui.Edit e) {
		if (e != null)  lowEdit = e;	}
	
	private void updateInfo() {
		String toSet = "time index = " +timer.getTimeIndex() + "\r\n";
		toSet = toSet + "calculation time = " +timer.getCompTime() + "\r\n";
		toSet = toSet + system.debugString;
		if (outputBar != null) outputBar.setValue((int)(system.getOutput())); 
		if (madeStptLabel != null) {
			if (Math.abs(system.getTE__10() - system.getStpt10()) < 0.2)
				madeStptLabel.setVisible(true);
			else
				madeStptLabel.setVisible(false);
		}
		if ((lastTimeIndex) < (timer.getTimeIndex())) {
			if (system.computeTimeError == true) 
				if (timeIndexBox != null) {
					timeIndexBox.setSelectedIndex(system.timeIndexSelection +1);
					system.computeTimeError = false;
				}
			if (outputEdit != null) outputEdit.setText(""+system.getOutputCommand()+"");
			//if (highEdit != null) highEdit.setText(""+system.oatMax+"");
			//if (lowEdit != null) lowEdit.setText(""+system.oatMin);

			if (errEdit != null) errEdit.setText((float)system.getErr()+"");
			if (intgEdit != null) intgEdit.setText((float)system.getIntG()+"");
			if (infoEdit != null) infoEdit.setText(toSet);
			if (pEdit != null) {
				if (Math.abs(system.p) < (0.001)) 
					pEdit.setText(" < 0.001");
				else
					pEdit.setText((float)system.p+"");
			}
			if (iEdit != null) {
				if (Math.abs(system.i) < (0.001)) 
					iEdit.setText(" < 0.001");
				else
					iEdit.setText((float)system.i+"");
			}
			if (dEdit != null) {
				if (Math.abs(system.d) < (0.001)) 
					dEdit.setText(" < 0.001");
				else
					dEdit.setText((float)system.d+"");
			}
			lastTimeIndex = (float)(timer.getTimeIndex()+.3);
		}
	}
	
    protected void onPaint(PaintEvent pe) {
        boolean created = false;
        BufferedPaintEvent bpe;
        
        if (pe instanceof BufferedPaintEvent) {
            bpe = (BufferedPaintEvent)pe;
        }
        else {
            bpe = createBufferedPaintEvent(pe);
            created = true;
        }
        
        // Paint to the off-screen buffer.
        super.onPaint(bpe);
        samPaint(buffer.getGraphics());
        // This call actually does the on-screen painting
        pe.graphics.drawImage(buffer, 0, 0);
		//tell timer we got the image
        timer.updateCompTimer();
		
        if (created) {
            bpe.graphics.dispose();
        }
    }
	private int sineValue() {
		   return (int)(100*Math.sin(2*(Math.PI)*freq*(timer.getTimeIndex() )));
	}
	private void samPaint(Graphics g) {
		//Custom drawing code goes here
		if (g == null) return;
		//g.setTextColor(Color.BLACK);
		drawValue(g);
		updateInfo();
		g.scroll(scrollSpeed,0,new Rectangle(0,0,sizeX,sizeY),new Rectangle(0,0,sizeX ,sizeY));
	}
	
	class SamPaintTimer extends Thread {
		private int PERIOD_IN_MILLI = 30;
		private ProgTimer sc;
		private long counter = 0;
		BufferedControl bc;
		public boolean stopped = false;

		SamPaintTimer(BufferedControl bc) {
			this.bc = bc;
			sc = new ProgTimer();
		}
		public long getCompTime() { return compTime; }
		public float getTimeIndex() { 
			//compTime += sc.stopTimer();
			return (float) ((float)(counter*PERIOD_IN_MILLI)/1000);
			//long temp = sc.stopTimer();
			//sc.startTimer();
			//return ((float)temp/(float)1000);
		}
		public void updateCompTimer() {compTime += sc.stopTimer();}
		public void run() {
			while(!stopped) {
				counter++;
				sc.startTimer();
				bc.invalidate();
				//compTime += sc.stopTimer();
					
				try {
					sleep(PERIOD_IN_MILLI);
					  } catch (InterruptedException e) {}
			}
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

	