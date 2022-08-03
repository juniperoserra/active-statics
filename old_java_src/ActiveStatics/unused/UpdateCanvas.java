package truss;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
/**
 * <p>Title: Truss</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: </p>r
 * @author Simon Greenwold
 * @version 1.0
 */

public class UpdateCanvas extends Canvas implements AdjustmentListener {

  public static final int APPLET_WIDTH = 700;
  public static final int APPLET_HEIGHT = 700;

  public static final int MIN_MIN_COORD = -500;
  public static final int MAX_MAX_COORD = 2000;

  public static final int MIN_OFFSIZE = 500;
  public static final int CANVAS_GROW_CHUNK = 200;

  public BeamLoadApplet mBeamLoadApplet;

  private Image mOffscreen;
  private Graphics mGOff;
  Dimension mOffSize = new Dimension();
  public int W;
  public int H;
  private TButton mouseOverButton;
  private Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
  private Cursor arrow = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

  public int mUpdateTimes = 2;

  private Point tempPoint = new Point();
  private int paintedTwice = 0;
  private ScrollPane mScrollPane;
  private Dimension d = new Dimension();

  int minX = Integer.MAX_VALUE;
  int maxX = Integer.MIN_VALUE;
  int minY = Integer.MAX_VALUE;
  int maxY = Integer.MIN_VALUE;
  Rectangle mExtents = new Rectangle();

  int minXVis, maxXVis;
  int minYVis, maxYVis;
  int xOff, yOff;
  int mOffBuffXMin, mOffBuffXMax;
  int mPrevOffBuffXMin, mPrevOffBuffXMax;
  int mOffBuffYMin, mOffBuffYMax;
  int mPrevOffBuffYMin, mPrevOffBuffYMax;
  int mPrevScrollX, mPrevScrollY;

  int dXMin;
  int dYMin;

  boolean changedSize;
  boolean changedOffset;
  public boolean mGlobalUpdateEveryTime = false;

  public GraphicEntity[] drawList;
  public GraphicEntity[] updateList;

  public G g;

  public void dump() {
    System.out.println("minX, maxX: " + minX + ", " + maxX);
    System.out.println("min, maxXVis: " + minXVis + ", " + maxXVis);
    System.out.println("xOff: " + xOff);
    System.out.println("min, maxOffBuffXMax: " + mOffBuffXMin + ", " + mOffBuffXMax);
    System.out.println("dXMin " + dXMin);
    System.out.println("min, mPrevOffBuffXMax: " + mPrevOffBuffXMin + ", " + mPrevOffBuffXMax);
    System.out.println("mPrevScrollX: " + mPrevScrollX);
    System.out.println("Cur scroll: " + mPrevScrollX);
    System.out.println("W, H: " + W + ", " + H);
  }


  /**Construct the panel*/
  public UpdateCanvas(ScrollPane scrollPane, G aG) {
    mScrollPane = scrollPane;
    setSize(700, 800);
    g = aG;
    init();
    //setLayout(null);
    //setSize(20, 30);
  }

  /**Initialize the applet*/
  public void init() {
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    repaint();
  }

  public void mouseUpHook() {
  }

  /**Component initialization*/
  private void jbInit() throws Exception {
    setBackground(G.mBackground);
    W = getSize().width;
    H = getSize().height;
//    mOffscreen = createImage(W, H);
//    mGOff = mOffscreen.getGraphics();
//    mOffSize = getSize();

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        g.mouseDown = true;
        tempPoint.x = e.getX() + xOff;
        tempPoint.y = e.getY() + yOff;
        for (int i = drawList.length - 1; i >= 0 ; i--) {
          if (drawList[i].hit(tempPoint)) {
            saveScrollPos();
            g.selectedEntity = drawList[i];
            if (g.selectedEntity.isButton()) {
              ((TButton)g.selectedEntity).mHighlight = true;
              repaint();
            }
            return;
          }
        }
        g.selectedEntity = null;
      }
      public void mouseReleased(MouseEvent e) {
        g.mouseDown = false;
        mouseUpHook();
        if (g.selectedEntity == null)
          return;
        if (!g.selectedEntity.isButton()) {
          recalcOffset();
          return;
        }


        ((TButton)g.selectedEntity).mHighlight = false;
        tempPoint.x = e.getX() + xOff;
        tempPoint.y = e.getY() + yOff;
        boolean releasedIn = ((TButton)g.selectedEntity).hit(tempPoint);
        if (releasedIn)
          ((TButton)g.selectedEntity).run();
        repaint();
      }

    });
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {
        tempPoint.x = e.getX() + xOff;
        tempPoint.y = e.getY() + yOff;
        for (int i = drawList.length - 1; i >= 0 ; i--) {
          if (drawList[i].isButton() && !drawList[i].mInvisible) {
            if (drawList[i].hit(tempPoint)) {
              setCursor(hand);
              return;
            }
          }
        }
        setCursor(arrow);
      }

      public void mouseDragged(MouseEvent e) {
        if (g.selectedEntity != null) {
          if (g.selectedEntity.isButton()) {
            tempPoint.x = e.getX() + xOff;
            tempPoint.y = e.getY() + yOff;

            boolean stillIn = ((TButton)g.selectedEntity).hit(tempPoint);
            ((TButton)g.selectedEntity).mHighlight = stillIn;
          }
          else {
            tempPoint.x = e.getX() + xOff;
            tempPoint.y = e.getY() + yOff;
            g.selectedEntity.dragged(tempPoint);
          }
          repaint();
        }
      }
    });
/*    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        System.out.println("Resize to: " + getSize().width + ", " + getSize().height);
        recalcOffset();
        update(getGraphics());
      }
    });*/
  }

  public void appletResized() {
//    System.out.println("Resize to: " + getSize().width + ", " + getSize().height);
    postScroll();
    recalcOffset();
    update(getGraphics());
  }

  public void globalUpdate() {    // Apply contstraints
    // Override this anonymously.
  }

  public void update(Graphics graphics) {
    //System.out.println("In update.");
    if (!mGlobalUpdateEveryTime)
      globalUpdate();

    //System.out.println("In update. W: " + W);

    for (int j = 0; j < mUpdateTimes; j++) {  // We update j times to filter through the changes
      if (mGlobalUpdateEveryTime)
        globalUpdate();
      for (int i = 0; i < updateList.length; i++) {
        updateList[i].update();
      }
    }


    if (mGOff == null)
      recalcOffset();

    mGOff.clearRect(0, 0, W, H);


    mGOff.translate(-xOff, -yOff);

    if (mBeamLoadApplet != null)
      mBeamLoadApplet.drawStuff(mGOff);

    for (int i = 0; i < drawList.length; i++) {
      drawList[i].draw(mGOff);
    }


    mGOff.translate(xOff, yOff);

    if (changedSize) {
      changedSize = false;
      setSize(W, H);
      //repaint();
      mScrollPane.validate();
      //mScrollPane.setScrollPosition(mPrevScrollX - xOff, mPrevScrollY - yOff);
      mScrollPane.setScrollPosition(mPrevScrollX - dXMin, mPrevScrollY - dYMin);
    }
    else if (changedOffset) {
      //mScrollPane.setScrollPosition(mPrevScrollX - xOff, mPrevScrollY - yOff);
      mScrollPane.setScrollPosition(mPrevScrollX - dXMin, mPrevScrollY - dYMin);
    }

    paint(graphics);

  }

  public void adjustmentValueChanged(AdjustmentEvent e) {
    postScroll();
    //repaint();        // Can be removed (only for debugging);
  }

  public void postScroll() {
    minXVis = mScrollPane.getScrollPosition().x + xOff;
    minYVis = mScrollPane.getScrollPosition().y + yOff;
    maxXVis = minXVis + mScrollPane.getViewportSize().width;
    maxYVis = minYVis + mScrollPane.getViewportSize().height;
  }

  public void saveScrollPos() {
    mPrevScrollX = mScrollPane.getScrollPosition().x;
    mPrevScrollY = mScrollPane.getScrollPosition().y;

    dXMin = mOffBuffXMin;
    dYMin = mOffBuffYMin;
  }

  public void recalcOffset() {
    minX = Integer.MAX_VALUE;
    maxX = Integer.MIN_VALUE;
    minY = Integer.MAX_VALUE;
    maxY = Integer.MIN_VALUE;

    int testCoord;
    changedOffset = false;

    //Dimension d;// = getSize();
    for (int i = 0; i < drawList.length; i++) {
      if (drawList[i].mConsiderExtents)
        drawList[i].getExtents(mExtents);
      testCoord = Math.max(mExtents.x, MIN_MIN_COORD);
      if (testCoord < minX) minX = testCoord;

      testCoord = Math.min(mExtents.x + mExtents.width, MAX_MAX_COORD);
      if (testCoord > maxX) maxX = testCoord;

      testCoord = Math.max(mExtents.y, MIN_MIN_COORD);
      if (testCoord < minY) minY = testCoord;

      testCoord = Math.min(mExtents.y + mExtents.height, MAX_MAX_COORD);
      if (testCoord > maxY) maxY = testCoord;
    }

    // Resize only sometimes.
    minX = ((int)(minX / CANVAS_GROW_CHUNK)) * CANVAS_GROW_CHUNK;
    minY = ((int)(minY / CANVAS_GROW_CHUNK)) * CANVAS_GROW_CHUNK;
    maxX = ((int)(maxX / CANVAS_GROW_CHUNK) + 1) * CANVAS_GROW_CHUNK;
    maxY = ((int)(maxY / CANVAS_GROW_CHUNK) + 1) * CANVAS_GROW_CHUNK;


    mOffBuffXMin = Math.min(minX, minXVis);
    mOffBuffXMax = Math.max(maxX, maxXVis);// + 16;
    mOffBuffYMin = Math.min(minY, minYVis);
    mOffBuffYMax = Math.max(maxY, maxYVis);// + 16;

    dXMin = mOffBuffXMin - dXMin;
    dYMin = mOffBuffYMin - dYMin;

    boolean repaint = false;
    if (mOffBuffXMin != mPrevOffBuffXMin || mOffBuffXMax != mPrevOffBuffXMax ||
        mOffBuffYMin != mPrevOffBuffYMin || mOffBuffYMax != mPrevOffBuffYMax) {

      xOff += mOffBuffXMin - mPrevOffBuffXMin;
      yOff += mOffBuffYMin - mPrevOffBuffYMin;

      changedOffset = true;
      repaint = true;
    }

    d.width = mOffBuffXMax - mOffBuffXMin;
    d.height = mOffBuffYMax - mOffBuffYMin;
//    d.width = Math.max(MIN_OFFSIZE, (((mOffBuffXMax - mOffBuffXMin) / CANVAS_GROW_CHUNK)+1)*CANVAS_GROW_CHUNK);
//    d.height = Math.max(MIN_OFFSIZE, (((mOffBuffYMax - mOffBuffYMin) / CANVAS_GROW_CHUNK)+1)*CANVAS_GROW_CHUNK);

    if ((mGOff == null) || (d.width != mOffSize.width) || (d.height != mOffSize.height)) {
	    //System.out.println("Creating offscreen " + d.width +", " + d.height);
      //	clean up the previous image
      if(mGOff!=null){
        mGOff.dispose();
        mGOff=null;
      }
      if(mOffscreen!=null){
        mOffscreen.flush();
        mOffscreen=null;
      }
      System.gc();

      mOffscreen = createImage(d.width, d.height);
	    mOffSize.width = d.width;
      mOffSize.height = d.height;
	    mGOff = mOffscreen.getGraphics();
	    mGOff.setFont(getFont());

      W = d.width;
      H = d.height;
      //setSize(W, H);
      changedSize = true;
      update(getGraphics());
	  }

    //mScrollPane.validate();
    if (repaint) {
      repaint();
    }
    changedOffset = false;

    mPrevOffBuffXMax = mOffBuffXMax;
    mPrevOffBuffXMin = mOffBuffXMin;
    mPrevOffBuffYMax = mOffBuffYMax;
    mPrevOffBuffYMin = mOffBuffYMin;
  }

  public void paint(Graphics graphics) {
    if (paintedTwice < 2) {
      for (int i = 0; i < updateList.length; i++) {
        updateList[i].update();
      }
      for (int i = 0; i < updateList.length; i++) {
        updateList[i].update();
      }
      for (int i = 0; i < drawList.length; i++) {
        drawList[i].draw(graphics);
      }
      paintedTwice++;
    }
    else {
      //System.out.println("Offscreen " + mOffscreen);
      graphics.drawImage(mOffscreen, 0, 0, this);
    }
  }
}