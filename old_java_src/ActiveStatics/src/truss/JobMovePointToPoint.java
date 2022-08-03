package truss;

/**
 * <p>Title: Truss</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>
 * @author Simon Greenwold
 * @version 1.0
 */

public class JobMovePointToPoint extends TimerJob {

  public TPoint mMovePoint;
  public float xDst, yDst;
  public TPoint  toPoint;
  public boolean forceSelectMovingNode = false;

  private static final float moveProportion = 0.1f;
  private float dx, dy;
  private boolean xDone = false;
  private boolean yDone = false;

  public JobMovePointToPoint(G g) {
    super(g);
  }

  public JobMovePointToPoint(TimerJob afterJob, G g) {
    super(afterJob, g);
  }

  public JobMovePointToPoint(G g, TPoint movePoint, TPoint toPoint) {
    super(g);
    mMovePoint = movePoint;
    this.toPoint = toPoint;
  }

  public JobMovePointToPoint(TimerJob afterJob, G g, TPoint movePoint, TPoint toPoint) {
    super(afterJob, g);
    mMovePoint = movePoint;
    this.toPoint = toPoint;
  }

  public void step() {
    if (afterJob != null && !afterJob.done)
      return;

    if (g.selectedEntity == mMovePoint && g.mouseDown)
      return;

    if (forceSelectMovingNode || (g.selectedEntity == null || !g.selectedEntity.isButton()))
      g.selectedEntity = mMovePoint;

    xDst = toPoint.x;
    yDst = toPoint.y;

    if (!xDone) {
      dx = mMovePoint.x - xDst;
      if (Math.abs(dx) <= 1.0) {
        mMovePoint.x = xDst;
        xDone = true;
      }
      else {
        mMovePoint.x -= dx * moveProportion;
      }
    }

    if (!yDone) {
      dy = mMovePoint.y - yDst;
      if (Math.abs(dy) <= 1.0) {
        mMovePoint.y = yDst;
        yDone = true;
      }
      else {
        mMovePoint.y -= dy * moveProportion;
      }
    }

//    System.out.println("Point to point dx, dy: " + dx + ", " + dy);


    if (xDone && yDone) {
      done = true;
//      System.out.println("Move point to point done.");
    }
  }


}