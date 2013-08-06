import com.ms.wfc.ui.*;
import com.ms.wfc.core.*;
import com.ms.wfc.util.*;
import com.ms.wfc.ui.Control.ClassInfo;
import java.util.Vector;
import java.util.StringTokenizer;

public class PIDDisplay extends com.ms.wfc.ui.Control {
	//NEW STUFF

	private int count = 0;
	private float lastTimeIndex = 0;
	private long compTime = 0;
	private boolean direction = false;
	private static final float freq = (float)0.1000;
	private static final int scrollSpeed = 1;
	private InfinityFunctions function = new InfinityFunctions();
	private int iValue = 0;
	private int pValue = 0;
	private int dValue = 0;
	
	private static final Pen blackPen = new Pen(Color.BLACK);
	private static final Pen redPen = new Pen(Color.RED);
	private static final Pen bluePen = new Pen(Color.BLUE);
	private static final Pen whitePen = new Pen(Color.WHITE);
	private static final Pen grayPen = new Pen(Color.GRAY);
	private static final Pen yellowPen = new Pen(Color.YELLOW);
	private static final Brush blackBrush = new Brush(Color.BLACK);
	private static final Brush redBrush = new Brush(Color.RED);
	private static final Brush blueBrush = new Brush(Color.BLUE);
	private static final Brush whiteBrush = new Brush(Color.WHITE);
	private static final Brush grayBrush = new Brush(Color.GRAY);
	private static final Brush greenBrush = new Brush(Color.GREEN);
	private static final Brush yellowBrush = new Brush(Color.YELLOW);
	protected static int sizeX = 320;
	protected static int sizeY = 200;

	private SamPaintTimer timer = new SamPaintTimer(this);
	public SystemDemo system;


	//GUI STUFF
	private Bitmap buffer = null;
	private static class BufferedPaintEvent extends PaintEvent {
        public final PaintEvent original;

        public BufferedPaintEvent(Graphics g, Rectangle clipRect, PaintEvent original) {
            super(g, clipRect);
            this.original = original;
        }
    }

    public PIDDisplay(SystemDemo s) {
        setStyle(STYLE_OPAQUE, true);
		//timeOffset = System.currentTimeMillis();
		timer.start();
		system= s;
   }

	public void reset() {
		count = 0;
		compTime = 0;
	}
	public void dispose() {
		timer.stopped = true;
		timer.notifyAll();
		if (timer != null)
			timer.stop();
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
	}
	public void setH(int i) {
		sizeY = i;
		super.setHeight(i);
		buffer=null;
	}
	protected void drawValue(Graphics g) {
		//first clear line we are drawing on
		//g.setPen(whitePen);
		//g.setBrush(blackBrush);
		//g.drawRect(0,sizeX,0,sizeY); //204);
		//g.setPen(blackPen);
		g.fill(0, 0, sizeX, sizeY, blackBrush);
		g.setTextColor(Color.WHITE);
		g.setBackColor(Color.BLACK);
		g.drawString( "P", sizeX/4, 5);
		g.setTextColor(Color.YELLOW);
		g.drawString( "I", sizeX/2, 5);
		g.setTextColor(Color.GREEN);
		g.drawString( "D", (3*sizeX)/4, 5);
		g.setPen(whitePen);
		g.drawLine(sizeX/4 - sizeX/8,sizeY/2 ,3*(sizeX)/4 + sizeX/8, sizeY/2);
		g.fill(sizeX/4 -sizeX/32,sizeY/2, sizeX/16,pValue,whiteBrush );
		g.fill(sizeX/2 -sizeX/32,sizeY/2, sizeX/16,iValue, yellowBrush );
		g.fill(3*(sizeX)/4 -sizeX/32,sizeY/2, sizeX/16,dValue, greenBrush );
			
	}

	private void updateInfo() {
		String toSet = "time index = " +timer.getTimeIndex() + "\r\n";
		toSet = toSet + "calculation time = " +timer.getCompTime() + "\r\n";
		pValue = (int) function.Ratio_Fct(system.p, 1,-1, -(sizeY-10)/2, (sizeY-10)/2);
		iValue = (int) function.Ratio_Fct(system.i, 1,-1, -(sizeY-10)/2, (sizeY-10)/2);
		dValue = (int) function.Ratio_Fct(system.d, 1,-1, -(sizeY-10)/2, (sizeY-10)/2);
		//iValue = function.Filter_Fct
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
        pe.graphics.drawImage(buffer, 0, 0);
        timer.updateCompTimer();

        if (created) {
            bpe.graphics.dispose();
        }
    }
	private void samPaint(Graphics g) {
		//Custom drawing code goes here
		if (g == null) return;
		drawValue(g);
		updateInfo();
		//g.scroll(scrollSpeed,0,new Rectangle(0,0,sizeX,sizeY),new Rectangle(0,0,sizeX ,sizeY));
	}

	class SamPaintTimer extends Thread {
		private int PERIOD_IN_MILLI = 30;
		private ProgTimer sc;
		private long counter = 0;
		PIDDisplay bc;
		public boolean stopped = false;

		SamPaintTimer(PIDDisplay bc) {
			this.bc = bc;
			sc = new ProgTimer();
		}
		public long getCompTime() { return compTime; }
		public float getTimeIndex() {
			return (float) ((float)(counter*PERIOD_IN_MILLI)/1000);
		}
		public void updateCompTimer() {compTime += sc.stopTimer();}
		public void run() {
			while(!stopped) {
				counter++;
				sc.startTimer();
				bc.invalidate();

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

