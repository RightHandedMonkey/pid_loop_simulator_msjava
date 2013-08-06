public class AverageTimer
{
  final static int  NUM_SAMPLES = 6;      //  # of samples to use for an average
  final static long INIT_MSPF = 30;       //  Initial assumed millis per frame (30ms is approx. 30fps)
  final static long MAX_DT = 100;         //  Max time for 1 frame.  This weeds out "spikes"
                                          //  that would advance the sim too much for 1 frame
  long  m_tick[] = new long[NUM_SAMPLES]; //  Array to remember samples
  int   m_index = 0;                      //  Current index to "circular" array

  public AverageTimer( long t )
  {
    int  i;

    //  Fill array with an assumed rate so far.. (INIT_MSPF)
    //  The actual times will quickly fix this, after NUM_SAMPLES samples.
    //
    for( i = NUM_SAMPLES - 1; i >= 0; i-- )
      m_tick[i] = t - ((NUM_SAMPLES - i) * INIT_MSPF);

    m_index = 0;
  }

  //  By supplying the current time (by using System.currentTimeMillis())
  //  we calculate the average time over the past NUM_SAMPLES frames, and record
  //  this current time to continue keeping the average accurate as possible.
  //
  public long deltaT( long cur_t )
  {
    int id = m_index-1;                       //  Calc index to previous sample
    if( id < 0 )                              //  Wrap if necessary..
      id += NUM_SAMPLES;

    long dt = cur_t - m_tick[id];             //  Dif time since prev sample

    if( dt > MAX_DT )                         //  If the slice was too big,
    {                                         //  we have to advance all the previous
      long ct = dt - MAX_DT;                  //  times by the diff..
      for( int i = 0; i < NUM_SAMPLES; i++ )
        m_tick[i] += ct;
    }

    long t = cur_t - m_tick[m_index];         //  Time dif since oldest time recorded
    m_tick[m_index] = cur_t;                  //  Save the current one now
    m_index = (m_index + 1) % NUM_SAMPLES;    //  Advance the index

    dt = t / (long)NUM_SAMPLES;               //  Calculate the final average time

    return dt;
  }
}