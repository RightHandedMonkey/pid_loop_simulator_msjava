public class InfinityFunctions
{
	//public static float timeIndex=1;
	private static float err,op,SCAN;
	public static float getErr, getIntG;
	public static float p, i, d;
/*	public static float PID_Fct(float cv, float sp, float kp, float ki, float kd,
	float bias, int actn, float lerr, float intg) {
		if (actn ==1) err=((cv-sp));
		else err=((sp-cv));
		op = Math.max(Math.min(((kp * err) + (ki * intg) + (kd * ((err - lerr) / (SCAN*timeIndex))) + bias), 1), 0);
		if ( ((op < 1) || (err<0)) && ((op > 0) || (err >0))) intg += err*SCAN*timeIndex;
		lerr = err;
		getErr = err;
		getIntG = intg;
		return op;
	}  */
	public static float PID_Fct(float cv, float sp, float kp, float ki, float kd,
	float bias, int actn, float lerr, float intg) {
		if (actn ==1) err=((cv-sp));
		else err=((sp-cv));
		p = kp*err;
		i = ki*intg;
		d = kd*((err-lerr)/(SCAN));
			
		op = Math.max(Math.min((p + i + d + bias), 1), 0);
		if ( ((op < 1) || (err<0)) && ((op > 0) || (err >0))) intg += err*SCAN;
		lerr = err;
		getErr = err;
		getIntG = intg;
		return op;
	}
	public static void clearPIDTerms() {
		err = 0;
		getErr =0;
		getIntG = 0;
	}
	public static void setScan(float sc) {SCAN = sc;}
	
	public static float Ratio_Fct(float inpt, float in1, float in2, float op1, float op2) {
		if ((in1 - in2) == 0)
		  return ((Math.abs(op1 - op2) / 2) + Math.min(op1, op2));
		else
		  return (Math.max(Math.min((((inpt - in1) * ((op1 - op2) / (in1 - in2))) + op1), Math.max(op1, op2)), Math.min(op1, op2)));
	}
	public static float Filter_Fct(float rawVal, float fltVal, float fltTm) {
		float fctr = (float) Math.min(((4.32 / (Math.pow(((Math.max(fltTm, 0.001)) * 60),0.99)) ) * SCAN),1);
		return ((rawVal * fctr) + ((1-fctr)*fltVal));
	}
}
	
/*

' FUNCTION RETURNS FILTERED VALUE - Filter.Fct

Arg 1 RawVal ' Raw input value
Arg 2 FltVal ' Filtered value
Arg 3 FltTm ' Filter time (minutes)

Numeric Fctr

Fctr = minimum(((4.32 / (((maximum(FltTm, 0.001)) * 60) ^ 0.99)) * Sc), 1)
Return ((RawVal * Fctr) + ((1 - Fctr) * FltVal))
END CODE

'FUNCTION RETURNS INTERCEPT OF PLOTTED POINTS BASED ON Inpt VALUE

Arg 1 Inpt '    Input variable
Arg 2 In1 '     Input reference value #1
Arg 3 In2 '     Input reference value #2
Arg 4 Op1 '     Output reference value #1
Arg 5 Op2 '     Output reference value #2

If ((In1 - In2) = 0) then
  Return ((abs(Op1 - Op2) / 2) + minimum(Op1, Op2))
Else
  Return (maximum(minimum((((Inpt - In1) * ((Op1 - Op2) / (In1 - In2))) + Op1), maximum(Op1, Op2)), minimum(Op1, Op2)))
Endif


//***********************************************************************
'FUNCTION RETURNS ON/OFF VALUE FOR RANGE OF Inpt VALUES w/OPTIONAL DELAYS

Arg 1 Inpt '     Input variable
Arg 2 OffVal '   Input value to turn OFF
Arg 3 OnVal '    Input value to turn ON
Arg 4 Stat '     Current status of object calling function
Arg 5 OffDly '   Time delay OFF interval (minutes) - Optional
Arg 6 OnDly '    Time delay ON interval (minutes) - Optional
Arg 7 StrtTm '   Delay timer (DateTime point in seconds) - Optional

Numeric Otpt '  Returned value

If (OnVal > OffVal) then
  If ((Inpt >= OnVal) or ((Stat = ON) and (Inpt > OffVal))) then Otpt = ON Else Otpt = OFF
Else
  If ((Inpt <= OnVal) or ((Stat = ON) and (Inpt < OffVal))) then Otpt = ON Else Otpt = OFF
Endif

If (not passed(5)) or (not passed(6)) or (not passed(7)) then Return (Otpt)

If (Otpt = OFF) then
  If (((Time - StrtTm) > (OffDly * 60)) or (Stat = OFF)) then
    StrtTm = Time
    Return (OFF)
  Else
    Return (ON)
  Endif
Else
  If (((Time - StrtTm) > (OnDly * 60)) or (Stat = ON)) then
    StrtTm = Time
    Return (ON)
  Else
    Return (OFF)
  Endif
Endif

*/


/*	Arg 1 cv ' controlled variable (sensor)
Arg 2 sp ' setpoint
Arg 3 kp ' proportional gain - constant
Arg 4 ki ' integral gain - constant
Arg 5 kd ' derivative gain - constant
Arg 6 bias ' initial output value when no error exists
Arg 7 actn ' directional relationship of cv and output capacity (1=DA 0=RA)
Arg 8 lerr ' error from previous scan - stored in calling program
Arg 9 intg ' integral value - stored in calling program

Numeric err ' difference between sp and cv
Numeric op ' output capacity (0-1) - returned to calling program

err = ((cv - sp) * (actn = 1)) + ((sp - cv) * (actn = 0))
op = maximum(minimum(((kp * err) + (ki * intg) + (kd * ((err - lerr) / Scan)) + bias), 1), 0)
intg = intg + (err * Scan * (((op < 1) or (err < 0)) & ((op > 0) or (err > 0))))
lerr = err

Return op
*/

