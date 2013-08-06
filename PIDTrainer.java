import com.ms.wfc.app.*;
import com.ms.wfc.core.*;
import com.ms.wfc.ui.*;
import com.ms.wfc.html.*;

public class PIDTrainer extends Form
{
	String version = "PID Trainer Demo version 0.9a Alpha";
	BufferedControl display = new BufferedControl();
	PIDDisplay pidDisplay;
	EventHandler aeh = new EventHandler(onActivate);
	EventHandler orh = new EventHandler(onResize);
	int x = 0;
	int y = 0;
	
	public PIDTrainer()
	{
		// Required for Visual J++ Form Designer support
		initForm();		
		initEvents();
		setSize(650,425);
		//onResize(null,null);
	}

	public void onResize(Object o, Event e) {
		//Size display then move everything else
		//display.setSize(1000,1000);
		x = this.getWidth();
		y = this.getHeight();
		if (x >= 611) { 
			outputBar.setLeft(x-611+347);
			label1.setLeft(x-611+339);
			groupBox2.setLeft(x-611+409);
			label2.setLeft(x-611+387);
			label3.setLeft(x-611+387);
			outputState.setLeft(x-611+339);
			pidButton.setLeft(x-611+409);
			pidResults.setLeft(x-611+409);
			groupBox3.setLeft(x-611+518);
			display.setW(x-611+300); 
			pidDisplay.setW(185);//x-611+185);
			pidDisplay.setLeft(x-611+411);
			//pidDisplay.setAnchor(ControlAnchor.
		} else { this.setWidth(650);}
		if (y >= 339) { 	
			groupBox4.setTop(y-339+219);
			label19.setTop(y-339+224);
			timeIndexBox.setTop(y-339+222);
			madeSetpointLabel.setTop(y-339+220);
			//outputState.setTop(y-339+218);
			hurtzGroup.setTop(y-339+245);
			powerGroup.setTop(y-339+245);
			styleGroup.setTop(y-339+245);
			int temp = y-339+204;
			labelB.setTop( temp/4);// -16);
			labelC.setTop((temp/2));// -16);
			labelD.setTop( (temp*3)/4);// -16);
			//labelE.setTop(temp);// -16);
			
			display.setH(temp); 	
			pidDisplay.setH(y-339);// + 79);
		}else { this.setHeight(425);}
		//display.update();
		//this.update();
		//this.invalidate();
	}
	
	public void initEvents() {
		//NEW STUFF
		display.setOutputEdit(outputEdit);
		display.setHighEdit(highEdit);
		display.setLowEdit(lowEdit);
		display.setSize(300,204);
		//display.setLocation(75,10);
		display.setLocation(31,11);
		display.setVisible(true);
		display.setTimeIndexBox(timeIndexBox);
		display.setInfoEdit(infoEdit);
		display.setErrEdit(lerrEdit);
		display.setIntGEdit(intgEdit);
		display.setPEdit(pEdit);
		display.setIEdit(iEdit);
		display.setDEdit(dEdit);
		display.setOutputSlider(outputBar);
		display.setMadeSetpointLabel(madeSetpointLabel);
		display.setMeasurementLagEdit(measurementLagEdit);
		display.setDeadTimeEdit(deadTimeEdit);
		//NEW PID Display
		pidDisplay = new PIDDisplay(display.system);
		pidDisplay.setLocation(411,317);
		pidDisplay.setVisible(true);
		//END PID Display
		kpEdit.setText(display.system.getKp()+"");
		kiEdit.setText(display.system.getKi()+"");
		kdEdit.setText(display.system.getKd()+"");
 		measurementLagEdit.setText(display.system.measurementLagSec +"");
		deadTimeEdit.setText(display.system.deadTimeSec +"");

		groupBox1.setVisible(false);
		//infoEdit.setVisible(false);
		pidButton.setEnabled(false);
		this.add(display);
		this.add(pidDisplay);
		//In case program is run on Win2000 Systems
		this.addOnActivate(aeh);
	}
	public void dispose()
	{
		super.dispose();
		components.dispose();
		display.dispose();
	}

	public void onActivate(Object o, Event e) {
		this.focus();
		this.bringToFront();
		this.focus();
		this.update();
		this.removeOnActivate(aeh);
		this.addOnResize(orh);
		onResize(null,null);
	}
	private void outputBar_scroll(Object source, Event e)
	{
		display.system.setOutputCommand((float)outputBar.getValue()/100);
	}


	private void checkBox1_click(Object source, Event e)
	{		display.system.outputDisabled = outputState.getChecked();	}

	private void pidButton_click(Object source, Event e)
	{
		float t1, t2, t3;
		int i1, i2;
		try {
			t1 =Float.valueOf(kpEdit.getText()).floatValue();
			t2 =Float.valueOf(kiEdit.getText()).floatValue();
			t3 =Float.valueOf(kdEdit.getText()).floatValue();
			i1 = Integer.valueOf(measurementLagEdit.getText()).intValue();
			i2 = Integer.valueOf(deadTimeEdit.getText()).intValue();
			display.system.setPIDTerms(t1, t2,t3);
			display.system.setMeasurementLag(i1);
			display.system.setDeadTime(i2);
			pidButton.setEnabled(false);
		} catch (NumberFormatException excp) {}
	}

	private void kpEdit_textChanged(Object source, Event e)
	{ pidButton.setEnabled(true); }

	private void kiEdit_textChanged(Object source, Event e)
	{ pidButton.setEnabled(true); }

	private void kdEdit_textChanged(Object source, Event e)
	{ pidButton.setEnabled(true); }


	private void timeIndexBox_selectedIndexChanged(Object source, Event e)
	{	display.system.setTimeIndex( timeIndexBox.getSelectedIndex());}

	private void measurementLagEdit_textChanged(Object source, Event e)
	{pidButton.setEnabled(true);}

	private void deadTimeEdit_textChanged(Object source, Event e)
	{pidButton.setEnabled(true);}



	private void hurtzEdit_textChanged(Object source, Event e)
	{	display.system.setLoadHurtz(Float.valueOf(hurtzEdit.getText()).floatValue() ); }


	private void offCheckBox_checkedChanged(Object source, Event e) 	{
		if (offCheckBox.getChecked()) display.system.setLoadStrength(0);
		if (lowCheckBox.getChecked()) display.system.setLoadStrength(1);
		if (medCheckBox.getChecked()) display.system.setLoadStrength(2);
		if (highCheckBox.getChecked()) display.system.setLoadStrength(3);
	}


	private void squareCheckBox_checkedChanged(Object source, Event e) 	{
		if (squareCheckBox.getChecked()) display.system.setLoadType(1);
		if (sineCheckBox.getChecked()) display.system.setLoadType(2);
		if (onCheckBox.getChecked()) display.system.setLoadType(3);
	}

	/**
	 * NOTE: The following code is required by the Visual J++ form
	 * designer.  It can be modified using the form editor.  Do not
	 * modify it using the code editor.
	 */
	Container components = new Container();
	GroupBox groupBox1 = new GroupBox();
	Edit infoEdit = new Edit();
	TrackBar outputBar = new TrackBar();
	Label label1 = new Label();
	Label label2 = new Label();
	Label label3 = new Label();
	CheckBox outputState = new CheckBox();
	GroupBox groupBox2 = new GroupBox();
	Edit kpEdit = new Edit();
	Edit kiEdit = new Edit();
	Edit kdEdit = new Edit();
	Label Kp = new Label();
	Label label4 = new Label();
	Label label5 = new Label();
	Label labelA = new Label();
	Label labelC = new Label();
	Label labelB = new Label();
	Label labelD = new Label();
	Edit lerrEdit = new Edit();
	Edit intgEdit = new Edit();
	Label label11 = new Label();
	Label label12 = new Label();
	ComboBox timeIndexBox = new ComboBox();
	Panel panel1 = new Panel();
	GroupBox groupBox3 = new GroupBox();
	Edit measurementLagEdit = new Edit();
	Label label13 = new Label();
	Edit deadTimeEdit = new Edit();
	Label label14 = new Label();
	Button pidButton = new Button();
	GroupBox groupBox4 = new GroupBox();
	Label label15 = new Label();
	Label label16 = new Label();
	Label label17 = new Label();
	Label label18 = new Label();
	Label label19 = new Label();
	GroupBox styleGroup = new GroupBox();
	GroupBox powerGroup = new GroupBox();
	RadioButton squareCheckBox = new RadioButton();
	RadioButton sineCheckBox = new RadioButton();
	RadioButton offCheckBox = new RadioButton();
	RadioButton lowCheckBox = new RadioButton();
	RadioButton medCheckBox = new RadioButton();
	RadioButton highCheckBox = new RadioButton();
	GroupBox hurtzGroup = new GroupBox();
	Edit hurtzEdit = new Edit();
	MainMenu mainMenu1 = new MainMenu();
	MenuItem menuItem1 = new MenuItem();
	MenuItem menuItem2 = new MenuItem();
	RadioButton onCheckBox = new RadioButton();
	GroupBox pidResults = new GroupBox();
	Edit pEdit = new Edit();
	Label label20 = new Label();
	Edit iEdit = new Edit();
	Edit dEdit = new Edit();
	Label label21 = new Label();
	Label label22 = new Label();
	Label label23 = new Label();
	Label madeSetpointLabel = new Label();
	Edit highEdit = new Edit();
	Edit lowEdit = new Edit();
	Edit outputEdit = new Edit();

	private void initForm()
	{
		// NOTE:  This form is storing resource information in an
		// external file.  Do not modify the string parameter to any
		// resources.getObject() function call. For example, do not
		// modify "foo1_location" in the following line of code
		// even if the name of the Foo object changes: 
		//   foo1.setLocation((Point)resources.getObject("foo1_location"));

		IResourceManager resources = new ResourceManager(this, "PIDTrainer");
		groupBox1.setAnchor(ControlAnchor.LEFT);
		groupBox1.setLocation(new Point(8, 327));
		groupBox1.setSize(new Point(245, 216));
		groupBox1.setTabIndex(0);
		groupBox1.setTabStop(false);
		groupBox1.setText("Information");

		infoEdit.setAnchor(ControlAnchor.ALL);
		infoEdit.setCursor(Cursor.NO);
		infoEdit.setLocation(new Point(8, 16));
		infoEdit.setSize(new Point(228, 192));
		infoEdit.setTabIndex(0);
		infoEdit.setTabStop(false);
		infoEdit.setText("");
		infoEdit.setMultiline(true);
		infoEdit.setReadOnly(true);

		outputBar.setLocation(new Point(347, 16));
		outputBar.setSize(new Point(42, 199));
		outputBar.setTabIndex(1);
		outputBar.setTabStop(false);
		outputBar.setText("trackBar1");
		outputBar.setAutoSize(false);
		outputBar.setLargeChange(20);
		outputBar.setMaximum(100);
		outputBar.setOrientation(Orientation.VERTICAL);
		outputBar.setTickStyle(TickStyle.BOTH);
		outputBar.setTickFrequency(10);
		outputBar.addOnScroll(new EventHandler(this.outputBar_scroll));

		label1.setLocation(new Point(339, 0));
		label1.setSize(new Point(56, 16));
		label1.setTabIndex(2);
		label1.setTabStop(false);
		label1.setText("Output");
		label1.setTextAlign(HorizontalAlignment.CENTER);

		label2.setLocation(new Point(387, 24));
		label2.setSize(new Point(16, 16));
		label2.setTabIndex(4);
		label2.setTabStop(false);
		label2.setText("0");

		label3.setLocation(new Point(387, 199));
		label3.setSize(new Point(16, 16));
		label3.setTabIndex(3);
		label3.setTabStop(false);
		label3.setText("1");

		outputState.setBackColor(Color.YELLOW);
		outputState.setForeColor(Color.RED);
		outputState.setLocation(new Point(339, 218));
		outputState.setSize(new Point(64, 23));
		outputState.setTabIndex(5);
		outputState.setText("Disable");
		outputState.setAppearance(Appearance.BUTTON);
		outputState.addOnClick(new EventHandler(this.checkBox1_click));

		groupBox2.setBackColor(Color.CONTROL);
		groupBox2.setForeColor(Color.WINDOWTEXT);
		groupBox2.setLocation(new Point(409, 3));
		groupBox2.setSize(new Point(103, 141));
		groupBox2.setTabIndex(6);
		groupBox2.setTabStop(false);
		groupBox2.setText("PID Terms");

		kpEdit.setLocation(new Point(32, 16));
		kpEdit.setSize(new Point(58, 20));
		kpEdit.setTabIndex(0);
		kpEdit.setText("");
		kpEdit.addOnTextChanged(new EventHandler(this.kpEdit_textChanged));

		kiEdit.setLocation(new Point(32, 40));
		kiEdit.setSize(new Point(58, 20));
		kiEdit.setTabIndex(1);
		kiEdit.setText("");
		kiEdit.addOnTextChanged(new EventHandler(this.kiEdit_textChanged));

		kdEdit.setLocation(new Point(33, 64));
		kdEdit.setSize(new Point(58, 20));
		kdEdit.setTabIndex(2);
		kdEdit.setText("");
		kdEdit.addOnTextChanged(new EventHandler(this.kdEdit_textChanged));

		Kp.setLocation(new Point(8, 16));
		Kp.setSize(new Point(24, 16));
		Kp.setTabIndex(7);
		Kp.setTabStop(false);
		Kp.setText("Kp");

		label4.setLocation(new Point(8, 40));
		label4.setSize(new Point(24, 16));
		label4.setTabIndex(6);
		label4.setTabStop(false);
		label4.setText("Ki");

		label5.setLocation(new Point(8, 64));
		label5.setSize(new Point(24, 16));
		label5.setTabIndex(5);
		label5.setTabStop(false);
		label5.setText("Kd");

		labelA.setForeColor(Color.BLUE);
		labelA.setLocation(new Point(11, 8));
		labelA.setSize(new Point(16, 16));
		labelA.setTabIndex(11);
		labelA.setTabStop(false);
		labelA.setText("95");

		labelC.setForeColor(Color.BLUE);
		labelC.setLocation(new Point(11, 96));
		labelC.setSize(new Point(16, 16));
		labelC.setTabIndex(10);
		labelC.setTabStop(false);
		labelC.setText("75");

		labelB.setForeColor(Color.BLUE);
		labelB.setLocation(new Point(11, 48));
		labelB.setSize(new Point(16, 16));
		labelB.setTabIndex(9);
		labelB.setTabStop(false);
		labelB.setText("85");

		labelD.setForeColor(Color.BLUE);
		labelD.setLocation(new Point(11, 144));
		labelD.setSize(new Point(16, 16));
		labelD.setTabIndex(8);
		labelD.setTabStop(false);
		labelD.setText("65");

		lerrEdit.setBackColor(Color.INACTIVECAPTIONTEXT);
		lerrEdit.setLocation(new Point(32, 88));
		lerrEdit.setSize(new Point(58, 20));
		lerrEdit.setTabIndex(8);
		lerrEdit.setText("");
		lerrEdit.setReadOnly(true);

		intgEdit.setBackColor(Color.INACTIVECAPTIONTEXT);
		intgEdit.setLocation(new Point(32, 112));
		intgEdit.setSize(new Point(58, 20));
		intgEdit.setTabIndex(9);
		intgEdit.setText("");
		intgEdit.setReadOnly(true);

		label11.setLocation(new Point(8, 88));
		label11.setSize(new Point(24, 16));
		label11.setTabIndex(4);
		label11.setTabStop(false);
		label11.setText("lerr");

		label12.setLocation(new Point(8, 112));
		label12.setSize(new Point(24, 16));
		label12.setTabIndex(3);
		label12.setTabStop(false);
		label12.setText("intg");

		timeIndexBox.setLocation(new Point(134, 250)); //236
		timeIndexBox.setSize(new Point(131, 21));
		timeIndexBox.setTabIndex(12);
		timeIndexBox.setText("Select Speed");
		timeIndexBox.setItems(new Object[] {
							  "Slowest (Normal Time)", 
							  "Slower (10 X Time)", 
							  "Slow (100 X Time)", 
							  "Fast (1000 X Time)", 
							  "Faster (2500 X Time)", 
							  "Fastest (5000 X Time)"});
		timeIndexBox.addOnSelectedIndexChanged(new EventHandler(this.timeIndexBox_selectedIndexChanged));

		panel1.setEnabled(false);
		panel1.setLocation(new Point(31, 11));
		panel1.setSize(new Point(308, 204));
		panel1.setTabIndex(13);
		panel1.setText("panel1");
		panel1.setVisible(false);

		groupBox3.setLocation(new Point(518, 3));
		groupBox3.setSize(new Point(81, 141));
		groupBox3.setTabIndex(14);
		groupBox3.setTabStop(false);
		groupBox3.setText("I/O Lag (sec)");

		measurementLagEdit.setLocation(new Point(21, 32));
		measurementLagEdit.setSize(new Point(35, 20));
		measurementLagEdit.setTabIndex(2);
		measurementLagEdit.setText("0");
		measurementLagEdit.addOnTextChanged(new EventHandler(this.measurementLagEdit_textChanged));

		label13.setLocation(new Point(6, 15));
		label13.setSize(new Point(71, 15));
		label13.setTabIndex(3);
		label13.setTabStop(false);
		label13.setText("Measurement");

		deadTimeEdit.setLocation(new Point(21, 76));
		deadTimeEdit.setSize(new Point(35, 20));
		deadTimeEdit.setTabIndex(0);
		deadTimeEdit.setText("0");
		deadTimeEdit.addOnTextChanged(new EventHandler(this.deadTimeEdit_textChanged));

		label14.setLocation(new Point(6, 61));
		label14.setSize(new Point(60, 15));
		label14.setTabIndex(1);
		label14.setTabStop(false);
		label14.setText("Dead Time");

		pidButton.setFont(new Font("MS Sans Serif", 16.0f, FontSize.CHARACTERHEIGHT, FontWeight.BOLD, false, false, false, CharacterSet.DEFAULT, 0));
		pidButton.setLocation(new Point(409, 147));
		pidButton.setSize(new Point(190, 40));
		pidButton.setTabIndex(7);
		pidButton.setText("Apply");
		pidButton.addOnClick(new EventHandler(this.pidButton_click));

		groupBox4.setBackColor(Color.CONTROL);
		groupBox4.setForeColor(Color.CONTROLTEXT);
		groupBox4.setLocation(new Point(3, 250)); //234
		groupBox4.setSize(new Point(58, 88));
		groupBox4.setTabIndex(15);
		groupBox4.setTabStop(false);
		groupBox4.setText("Legend");

		label15.setBackColor(Color.CONTROLDARKDARK);
		label15.setFont(new Font("MS Sans Serif", 8.0f, FontSize.POINTS, FontWeight.BOLD, false, false, false, CharacterSet.DEFAULT, 0));
		label15.setForeColor(Color.BLUE);
		label15.setLocation(new Point(9, 31));
		label15.setSize(new Point(40, 16));
		label15.setTabIndex(0);
		label15.setTabStop(false);
		label15.setText("TE_10");

		label16.setBackColor(Color.CONTROLDARKDARK);
		label16.setFont(new Font("MS Sans Serif", 8.0f, FontSize.POINTS, FontWeight.BOLD, false, false, false, CharacterSet.DEFAULT, 0));
		label16.setForeColor(Color.WHITE);
		label16.setLocation(new Point(9, 15));
		label16.setSize(new Point(40, 16));
		label16.setTabIndex(1);
		label16.setTabStop(false);
		label16.setText("OAT");

		label17.setBackColor(Color.CONTROLDARKDARK);
		label17.setFont(new Font("MS Sans Serif", 8.0f, FontSize.POINTS, FontWeight.BOLD, false, false, false, CharacterSet.DEFAULT, 0));
		label17.setForeColor(Color.GRAY);
		label17.setLocation(new Point(9, 47));
		label17.setSize(new Point(40, 16));
		label17.setTabIndex(3);
		label17.setTabStop(false);
		label17.setText("Stpt10");

		label18.setBackColor(Color.CONTROLDARKDARK);
		label18.setFont(new Font("MS Sans Serif", 8.0f, FontSize.POINTS, FontWeight.BOLD, false, false, false, CharacterSet.DEFAULT, 0));
		label18.setForeColor(Color.RED);
		label18.setLocation(new Point(9, 62));
		label18.setSize(new Point(40, 16));
		label18.setTabIndex(2);
		label18.setTabStop(false);
		label18.setText("Load");

		label19.setLocation(new Point(66, 238));
		label19.setSize(new Point(61, 18));
		label19.setTabIndex(16);
		label19.setTabStop(false);
		label19.setText("Time Factor");
		label19.setTextAlign(HorizontalAlignment.RIGHT);

		styleGroup.setLocation(new Point(67, 259));
		styleGroup.setSize(new Point(120, 62));
		styleGroup.setTabIndex(17);
		styleGroup.setTabStop(false);
		styleGroup.setText("Load Signal");

		powerGroup.setLocation(new Point(193, 259));
		powerGroup.setSize(new Point(115, 63));
		powerGroup.setTabIndex(18);
		powerGroup.setTabStop(false);
		powerGroup.setText("Load Power");

		squareCheckBox.setLocation(new Point(10, 16));
		squareCheckBox.setSize(new Point(58, 21));
		squareCheckBox.setTabIndex(1);
		squareCheckBox.setTabStop(true);
		squareCheckBox.setText("Square");
		squareCheckBox.setChecked(true);
		squareCheckBox.addOnCheckedChanged(new EventHandler(this.squareCheckBox_checkedChanged));

		sineCheckBox.setLocation(new Point(9, 39));
		sineCheckBox.setSize(new Point(61, 21));
		sineCheckBox.setTabIndex(0);
		sineCheckBox.setText("Sine (+)");
		sineCheckBox.addOnCheckedChanged(new EventHandler(this.squareCheckBox_checkedChanged));

		offCheckBox.setForeColor(Color.RED);
		offCheckBox.setLocation(new Point(12, 17));
		offCheckBox.setSize(new Point(41, 21));
		offCheckBox.setTabIndex(3);
		offCheckBox.setTabStop(true);
		offCheckBox.setText("Off");
		offCheckBox.setChecked(true);
		offCheckBox.addOnCheckedChanged(new EventHandler(this.offCheckBox_checkedChanged));

		lowCheckBox.setLocation(new Point(12, 36));
		lowCheckBox.setSize(new Point(41, 21));
		lowCheckBox.setTabIndex(2);
		lowCheckBox.setText("Low");
		lowCheckBox.addOnCheckedChanged(new EventHandler(this.offCheckBox_checkedChanged));

		medCheckBox.setLocation(new Point(62, 17));
		medCheckBox.setSize(new Point(41, 21));
		medCheckBox.setTabIndex(1);
		medCheckBox.setText("Med");
		medCheckBox.addOnCheckedChanged(new EventHandler(this.offCheckBox_checkedChanged));

		highCheckBox.setLocation(new Point(62, 36));
		highCheckBox.setSize(new Point(41, 21));
		highCheckBox.setTabIndex(0);
		highCheckBox.setText("High");
		highCheckBox.addOnCheckedChanged(new EventHandler(this.offCheckBox_checkedChanged));

		hurtzGroup.setLocation(new Point(316, 259));
		hurtzGroup.setSize(new Point(88, 62));
		hurtzGroup.setTabIndex(19);
		hurtzGroup.setTabStop(false);
		hurtzGroup.setText("Load Hz / Day");

		hurtzEdit.setLocation(new Point(11, 23));
		hurtzEdit.setSize(new Point(67, 20));
		hurtzEdit.setTabIndex(0);
		hurtzEdit.setText("10");
		hurtzEdit.addOnTextChanged(new EventHandler(this.hurtzEdit_textChanged));

		menuItem1.setDefault(true);
		menuItem1.setEnabled(false);
		menuItem1.setText("File");
		menuItem1.setVisible(false);

		menuItem2.setDefault(true);
		menuItem2.setEnabled(false);
		menuItem2.setText("About");
		menuItem2.setVisible(false);

		mainMenu1.setMenuItems(new MenuItem[] {
							   menuItem1, 
							   menuItem2});
		/* @designTimeOnly mainMenu1.setLocation(new Point(156, 20)); */

		this.setText("PID - Trainer");
		this.setAutoScaleBaseSize(new Point(5, 13));
		this.setClientSize(new Point(600, 403));
		this.setIcon((Icon)resources.getObject("this_icon"));
		this.setMenu(mainMenu1);

		onCheckBox.setLocation(new Point(80, 16));
		onCheckBox.setSize(new Point(32, 23));
		onCheckBox.setTabIndex(2);
		onCheckBox.setText("On");
		onCheckBox.addOnCheckedChanged(new EventHandler(this.squareCheckBox_checkedChanged));

		pidResults.setFont(new Font("MS Sans Serif", 11.0f, FontSize.CHARACTERHEIGHT, FontWeight.BOLD, false, false, false, CharacterSet.DEFAULT, 0));
		pidResults.setLocation(new Point(410, 196));
		pidResults.setSize(new Point(189, 111));
		pidResults.setTabIndex(20);
		pidResults.setTabStop(false);
		pidResults.setText("PID RESULTS (Filtered)");

		pEdit.setBackColor(Color.ACTIVECAPTION);
		pEdit.setFont(Font.DEFAULT_GUI);
		pEdit.setForeColor(Color.ACTIVECAPTIONTEXT);
		pEdit.setLocation(new Point(78, 40));
		pEdit.setSize(new Point(100, 20));
		pEdit.setTabIndex(2);
		pEdit.setText("");
		pEdit.setReadOnly(true);

		label20.setFont(Font.DEFAULT_GUI);
		label20.setLocation(new Point(13, 18));
		label20.setSize(new Point(170, 16));
		label20.setTabIndex(6);
		label20.setTabStop(false);
		label20.setText("Contributions from PID Terms");

		iEdit.setBackColor(Color.ACTIVECAPTION);
		iEdit.setFont(Font.DEFAULT_GUI);
		iEdit.setForeColor(Color.ACTIVECAPTIONTEXT);
		iEdit.setLocation(new Point(78, 64));
		iEdit.setSize(new Point(100, 20));
		iEdit.setTabIndex(1);
		iEdit.setText("");
		iEdit.setReadOnly(true);

		dEdit.setBackColor(Color.ACTIVECAPTION);
		dEdit.setFont(Font.DEFAULT_GUI);
		dEdit.setForeColor(Color.ACTIVECAPTIONTEXT);
		dEdit.setLocation(new Point(78, 86));
		dEdit.setSize(new Point(100, 20));
		dEdit.setTabIndex(0);
		dEdit.setText("");
		dEdit.setReadOnly(true);

		label21.setFont(Font.DEFAULT_GUI);
		label21.setLocation(new Point(6, 43));
		label21.setSize(new Point(68, 16));
		label21.setTabIndex(5);
		label21.setTabStop(false);
		label21.setText("P term adds:");
		label21.setTextAlign(HorizontalAlignment.RIGHT);

		label22.setFont(Font.DEFAULT_GUI);
		label22.setLocation(new Point(6, 64));
		label22.setSize(new Point(68, 16));
		label22.setTabIndex(4);
		label22.setTabStop(false);
		label22.setText("I term adds:");
		label22.setTextAlign(HorizontalAlignment.RIGHT);

		label23.setFont(Font.DEFAULT_GUI);
		label23.setLocation(new Point(6, 89));
		label23.setSize(new Point(68, 16));
		label23.setTabIndex(3);
		label23.setTabStop(false);
		label23.setText("D term adds:");
		label23.setTextAlign(HorizontalAlignment.RIGHT);

		madeSetpointLabel.setFont(new Font("MS Sans Serif", 11.0f, FontSize.CHARACTERHEIGHT, FontWeight.BOLD, false, false, false, CharacterSet.DEFAULT, 0));
		madeSetpointLabel.setForeColor(new Color(0, 192, 0));
		madeSetpointLabel.setLocation(new Point(269, 233));
		madeSetpointLabel.setSize(new Point(62, 28));
		madeSetpointLabel.setTabIndex(21);
		madeSetpointLabel.setTabStop(false);
		madeSetpointLabel.setText("Made Setpoint");
		madeSetpointLabel.setVisible(false);
		madeSetpointLabel.setTextAlign(HorizontalAlignment.CENTER);

		highEdit.setLocation(new Point(6, 10));
		highEdit.setSize(new Point(24, 20));
		highEdit.setTabIndex(24);
		highEdit.setText("");

		lowEdit.setAnchor(ControlAnchor.BOTTOMLEFT);
		lowEdit.setLocation(new Point(6, 284));
		lowEdit.setSize(new Point(24, 20));
		lowEdit.setTabIndex(23);
		lowEdit.setText("");

		outputEdit.setAnchor(ControlAnchor.TOPRIGHT);
		outputEdit.setBackColor(Color.INACTIVECAPTIONTEXT);
		outputEdit.setLocation(new Point(339, 248));
		outputEdit.setSize(new Point(63, 20));
		outputEdit.setTabIndex(22);
		outputEdit.setText("");
		outputEdit.setReadOnly(true);

		this.setNewControls(new Control[] {
							outputEdit, 
							lowEdit, 
							highEdit, 
							madeSetpointLabel, 
							pidResults, 
							hurtzGroup, 
							powerGroup, 
							styleGroup, 
							label19, 
							groupBox4, 
							pidButton, 
							groupBox3, 
							panel1, 
							timeIndexBox, 
							labelD, 
							labelB, 
							labelC, 
							labelA, 
							groupBox2, 
							outputState, 
							label3, 
							label2, 
							label1, 
							outputBar, 
							groupBox1});
		groupBox1.setNewControls(new Control[] {
								 infoEdit});
		groupBox2.setNewControls(new Control[] {
								 label12, 
								 label11, 
								 intgEdit, 
								 lerrEdit, 
								 label5, 
								 label4, 
								 Kp, 
								 kdEdit, 
								 kiEdit, 
								 kpEdit});
		groupBox3.setNewControls(new Control[] {
								 label14, 
								 deadTimeEdit, 
								 label13, 
								 measurementLagEdit});
		groupBox4.setNewControls(new Control[] {
								 label18, 
								 label17, 
								 label16, 
								 label15});
		styleGroup.setNewControls(new Control[] {
								  onCheckBox, 
								  sineCheckBox, 
								  squareCheckBox});
		powerGroup.setNewControls(new Control[] {
								  highCheckBox, 
								  medCheckBox, 
								  lowCheckBox, 
								  offCheckBox});
		hurtzGroup.setNewControls(new Control[] {
								  hurtzEdit});
		pidResults.setNewControls(new Control[] {
								  label23, 
								  label22, 
								  label21, 
								  dEdit, 
								  iEdit, 
								  label20, 
								  pEdit});
	}

	/**
	 * The main entry point for the application. 
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.
	 */
	public static void main(String args[])
	{
		Application.run(new PIDTrainer());
	}
}
