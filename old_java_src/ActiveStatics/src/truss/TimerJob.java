package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class TimerJob {

  public boolean done;
  public TimerJob afterJob;
  public G g;

  public TimerJob() {
    g = null;
    done = false;
  }

  public TimerJob(G g) {
    this.g = g;
    done = false;
  }

  public TimerJob(TimerJob afterJob, G g) {
    this.g = g;
    done = false;
    this.afterJob = afterJob;
  }

  public void start() {
  }

  public void step() {
  }
}