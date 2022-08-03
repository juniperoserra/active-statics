package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class Timer extends Thread {

  private TimerJob[] mTimerJobs = new TimerJob[0];
  private TimerJob[] mTimerJobsCopy = new TimerJob[0];
  private boolean mLocked = false;
  int mLastSize = 0;

  public G g;

  public Timer(G aG) {
    g = aG;
  }

  public int numJobs() {
    return mTimerJobs.length;
  }

  public void addJob(TimerJob job) {
    while(mLocked) {
      Util.sleep(1);
    }
    mTimerJobs = (TimerJob[])Util.append(mTimerJobs, Types.TIMER_JOB, job);
    job.start();
  }

  public void clearJobs() {
    for (int i = 0; i < mTimerJobs.length; i++) {
      mTimerJobs[i].done = true;
    }
    mTimerJobs = new TimerJob[0];
  }

  public void removeJob(TimerJob job) {
    while(mLocked) {
      Util.sleep(1);
    }
    job.done = true;
    mTimerJobs = (TimerJob[])Util.remove(mTimerJobs, Types.TIMER_JOB, job);
  }

  public void run() {
    while(true) {
      mLocked = true;
      if (mTimerJobs.length != mLastSize) {
        mLastSize = mTimerJobs.length;
        mTimerJobsCopy = new TimerJob[mLastSize];
      }
      System.arraycopy(mTimerJobs, 0, mTimerJobsCopy, 0, mLastSize);
      mLocked = false;

//      System.out.println("nJobs: " + mTimerJobsCopy.length);

      for (int i = 0; i < mTimerJobsCopy.length; i++) {
        mTimerJobsCopy[i].step();
        if (mTimerJobsCopy[i].done) {
          removeJob(mTimerJobsCopy[i]);
        }
      }
      if (mLastSize > 0)
        g.mFrame.repaint();
      Util.sleep(20);
    }
  }
}