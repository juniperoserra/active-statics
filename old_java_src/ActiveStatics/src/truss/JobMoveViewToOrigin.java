package truss;

/**
 * <p>Title: Truss</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author Simon Greenwold
 * @version 1.0
 */

public class JobMoveViewToOrigin  extends TimerJob {
  public NoScrollUpdateCanvas mView;

  private static final float moveProportion = 0.1f;
  private float dx, dy;
  private boolean xDone = false;
  private boolean yDone = false;

  public JobMoveViewToOrigin(G g) {
    super(g);
  }

  public JobMoveViewToOrigin(TimerJob afterJob, G g) {
    super(afterJob, g);
  }

  public void step() {
    if (afterJob != null && !afterJob.done)
      return;

    if (g.selectedEntity == null && g.mouseDown)
      return;

    if (!xDone) {
      dx = mView.viewXOffset;
      if (Math.abs(dx) <= 1.0) {
        mView.viewXOffset = 0;
        xDone = true;
      }
      else {
        mView.viewXOffset -= dx * moveProportion;
      }
    }

    if (!yDone) {
      dy = mView.viewYOffset;
      if (Math.abs(dy) <= 1.0) {
        mView.viewYOffset = 0;
        yDone = true;
      }
      else {
        mView.viewYOffset -= dy * moveProportion;
      }
    }

//    System.out.println("Dx, Dy: " + dx + ", " + dy);

    if (xDone && yDone) {
//      System.out.println("Move point to start done.");
      done = true;
    }
  }


}