package truss;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;

public class HangingCableApplet extends Applet {
  public static final int APPLET_WIDTH = 820;
  public static final int APPLET_HEIGHT = 620;

  public static final int PANEL_SIZE = 45;
  public static final int CABLE_X_START = 160;
  public static final int CABLE_Y_START = 350;
  public final int START_FORCE_LENGTH = 60;
  public static final int LOAD_LINE_START_X = 710;
  public static final int LOAD_LINE_START_Y = 40;

  public static final float MAX_WIDTH = 60.0f;
  public static final float MIN_WIDTH = 2.0f;
  public static final float WIDTH_MULT = 0.1f;

  public GraphicEntity[] drawList = new GraphicEntity[0];
  public GraphicEntity[] updateList = new GraphicEntity[0];
  private Image mOffscreen;
  private Graphics mGOff;
  Dimension mOffSize;
  public int W;
  public int H;

  public TPoint mResultantStartNode;
  public TPoint mResultantEndNode;
  public TPointIntersect mActionIntersect;
  public TLine mRaLineOfAction;
  public TLine mRbLineOfAction;
  public TArrow mLoadLineOfAction;

  private Point tempPoint = new Point();
  private int paintedTwice = 0;
  private TButton mLinesOfActionCheck;

  private float[] mForceDy = new float[7];
  private boolean mIsArch = false;
  private boolean mUpdatedOnce = false;

  public boolean mSupportsHoriz;
  public boolean mLinesOfAction;

  private NoScrollUpdateCanvas mUpdateCanvas;

  private TButton mHorizButton;

  public TPoint[] mCableNodes = new TPoint[9];
  public TPoint[] mForceTails = new TPoint[7];
  public TPoint[] mForceTailStarts = new TPoint[7];
  public TPoint[] mEqualTails = new TPoint[7];
  public TArrow[] mLoads = new TArrow[7];
  public TLine[] mMembers = new TLine[8];
  public TTextPointLength RbMag, RaMag;
  public TTextPointLength mRaXMag, mRaYMag, mRbXMag, mRbYMag;
  public TPoint mRbTail;
  public TReaction mRb;
  public TPoint mRaTail;
  public TReaction mRa;
  public TPoint mRaX;
  public TPoint mRaY;
  public TPoint mRbX;
  public TPoint mRbY;
  public TPoint[] mLoadLine = new TPoint[8];
  public TPoint mForcePolyNode;
  public TLine mForcePolyLines[] = new TLine[8];
  public TPoint mOPrime;

  public TPoint mHorizO;

  public boolean mLoadsVertical = false;

  public G g;

  boolean isStandalone = false;
  /**Get a parameter value*/
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }


  public void addToDrawList(GraphicEntity entity) {
    drawList = (GraphicEntity[])Util.append(drawList, Types.GRAPHIC_ENTITY, entity);
    updateList = (GraphicEntity[])Util.append(updateList, Types.GRAPHIC_ENTITY, entity);
  }

  public void addToDrawListOnly(GraphicEntity entity) {
    drawList = (GraphicEntity[])Util.append(drawList, Types.GRAPHIC_ENTITY, entity);
  }

  public void addToUpdateList(GraphicEntity entity) {
    updateList = (GraphicEntity[])Util.append(updateList, Types.GRAPHIC_ENTITY, entity);
  }

  public void repaint() {
    if (mUpdateCanvas != null)
      mUpdateCanvas.repaint();
  }

  /**Initialize the applet*/
  public void init() {
    g = new G(this);
    new Types();
    setLayout(null);
    g.mFrame = this;
    g.selectedEntity = null;
    g.mLengthDivisor = 1.0f;

    makeNodes();
    makeRb();
    makeLoadLine();
    makeRa();
    makeForcePolygon();
    makeMembers();
    addNodes();
    makeText();
    makeSupports();
    makeReport();
    makeLinesOfAction();

    makeButtons();

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    repaint();
  }
  /**Component initialization*/
  private void jbInit() throws Exception {
    setBackground(g.mBackground);
    W = getSize().width;
    H = getSize().height;

    mUpdateCanvas = new NoScrollUpdateCanvas(this, g) {
      public void mouseUpHook() {
        if (!mSupportsHoriz)
          return;
        for (int i = 0; i < 7; i++) {
          if (g.selectedEntity == mForceTails[i]) {
            g.mTimer.addJob(new JobMovePointToPoint(g, mForcePolyNode, mHorizO));
            return;
          }
        }
      }

      public void globalUpdate() {    // Apply contstraints
      // Cable start/end move: OPrime stays put. O moves.
      // Forces move: O moves. OPrime moves.

        if (mForcePolyNode.x == mLoadLine[0].x)
          return;

        if (mLinesOfAction) {
          mRaLineOfAction.mInvisible = false;
          mRbLineOfAction.mInvisible = false;
          mLoadLineOfAction.mInvisible = false;
          mActionIntersect.mInvisible = false;
        }
        else {
          mRaLineOfAction.mInvisible = true;
          mRbLineOfAction.mInvisible = true;
          mLoadLineOfAction.mInvisible = true;
          mActionIntersect.mInvisible = true;
        }

        if (mSupportsHoriz && g.mTimer.numJobs() == 0) {
          if (g.selectedEntity == mCableNodes[0])
            mCableNodes[8].y = mCableNodes[0].y;
          else if (g.selectedEntity == mCableNodes[8])
            mCableNodes[0].y = mCableNodes[8].y;

          if (g.selectedEntity == mForcePolyNode) {
            mForcePolyNode.y = mOPrime.y;
          }
        }

        isArch();
        if (mIsArch) {
          RbMag.mXOffset = -20;
          RbMag.mYOffset = 20;
          RaMag.mXOffset = -20;
          RaMag.mYOffset = 20;
          mRaYMag.mXOffset = -50;
          mRbYMag.mXOffset = 10;

          for (int i = 0; i < 8; i++) {
            mMembers[i].mColor = G.mRed;
            mForcePolyLines[i].mColor = G.mRed;
          }
        }
        else {
          RbMag.mXOffset = 10;
          RbMag.mYOffset = -10;
          RaMag.mXOffset = -40;
          RaMag.mYOffset = -10;
          mRaYMag.mXOffset = 10;
          mRbYMag.mXOffset = -50;

          for (int i = 0; i < 8; i++) {
            mMembers[i].mColor = G.mBlue;
            mForcePolyLines[i].mColor = G.mBlue;
          }
        }

        if (mUpdatedOnce) {
          for (int i = 0; i < 7; i++) {
            if (mForceTails[i].y > mLoads[i].mArrowHead.y)
              mForceTails[i].y = mLoads[i].mArrowHead.y;
          }
        }
        else
          mUpdatedOnce = true;

        if (mCableNodes[8].x < mCableNodes[0].x + 8)
          mCableNodes[8].x = mCableNodes[0].x + 8;

        if (this.g.selectedEntity == mCableNodes[0] || this.g.selectedEntity == mCableNodes[8]) {
          findO();
        }
        else {
          findOPrime();
        }

        if (mSupportsHoriz && g.mTimer.numJobs() == 0) {
          if (g.selectedEntity == mForcePolyNode) {
            mForcePolyNode.y = mOPrime.y;
          }
        }


        distributeCableNodes();
        for (int i = 0; i < 7; i++) {
          mForceTails[i].x = mCableNodes[i+1].x;
          mForceDy[i] = mForceTails[i].y - mCableNodes[i+1].y;
        }
        findCableYs();
        findReactions();
        for (int i = 0; i < 7; i++) {
          mForceTails[i].y = mForceDy[i] + mCableNodes[i+1].y;
        }
        for (int i = 0; i < 8; i++) {
          mMembers[i].mSize = (int)Util.bound(mForcePolyLines[i].length() *
                                        WIDTH_MULT, MIN_WIDTH, MAX_WIDTH);
        }

        for (int i = 0; i < 7; i++) {
          mForceTailStarts[i].x = mCableNodes[i+1].x;
          mForceTailStarts[i].y = mCableNodes[i+1].y - START_FORCE_LENGTH;

          mEqualTails[i].x = mCableNodes[i+1].x;
          mEqualTails[i].y = mCableNodes[i+1].y - (mCableNodes[1].y - mForceTails[0].y);
        }

        mHorizO.x = mForcePolyNode.x;
        mHorizO.y = mOPrime.y;


        mResultantStartNode.x = mActionIntersect.x;
        mResultantStartNode.y = mActionIntersect.y;
        mResultantEndNode.x = mActionIntersect.x;
        mResultantEndNode.y = mActionIntersect.y + (mLoadLine[7].y - mLoadLine[0].y);

      }

    };
    mUpdateCanvas.drawList = drawList;
    mUpdateCanvas.updateList = updateList;
    mUpdateCanvas.mUpdateTimes = 3;
    mUpdateCanvas.mGlobalUpdateEveryTime = true;

    add(mUpdateCanvas);

    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        mUpdateCanvas.appletResized();
      }
    });
  }


/*  private void jbInit() throws Exception {
    setBackground(g.mBackground);
    W = getSize().width;
    H = getSize().height;
    mOffscreen = createImage(W, H);
    if (mOffscreen != null)
      mGOff = mOffscreen.getGraphics();
    mOffSize = getSize();

    addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        tempPoint.x = e.getX();
        tempPoint.y = e.getY();
        for (int i = drawList.length - 1; i >= 0 ; i--) {
          if (drawList[i].hit(tempPoint)) {
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
        if (g.selectedEntity == null || !g.selectedEntity.isButton())
          return;
        ((TButton)g.selectedEntity).mHighlight = false;
        tempPoint.x = e.getX();
        tempPoint.y = e.getY();
        boolean releasedIn = ((TButton)g.selectedEntity).hit(tempPoint);
        if (releasedIn)
          ((TButton)g.selectedEntity).run();
        repaint();
      }

    });
    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        if (g.selectedEntity != null) {
          if (g.selectedEntity.isButton()) {
            tempPoint.x = e.getX();
            tempPoint.y = e.getY();

            boolean stillIn = ((TButton)g.selectedEntity).hit(tempPoint);
            ((TButton)g.selectedEntity).mHighlight = stillIn;
          }
          else {
            tempPoint.x = e.getX();
            tempPoint.y = e.getY();
            g.selectedEntity.dragged(tempPoint);
          }
          repaint();
        }
      }
    });
    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        update(getGraphics());
      }
    });
  }*/
  /**Get Applet information*/
  public String getAppletInfo() {
    return "Applet Information";
  }
  /**Get parameter info*/
  public String[][] getParameterInfo() {
    return null;
  }
  /**Main method*/
  public static void main(String[] args) {
    TrussApplet applet = new TrussApplet();
    applet.isStandalone = true;
    Frame frame;
    frame = new Frame() {
      protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
          System.exit(0);
        }
      }
      public synchronized void setTitle(String title) {
        super.setTitle(title);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      }
    };
    frame.setTitle("Applet Frame");
    frame.add(applet, BorderLayout.CENTER);
    applet.init();
    applet.start();
    frame.setSize(APPLET_WIDTH,APPLET_HEIGHT + 20);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
  }

  private void findO() {
    float len = Util.distance(mForcePolyNode.x, mForcePolyNode.y,
                              mOPrime.x, mOPrime.y);
    float dir = Util.direction(mCableNodes[8].x, mCableNodes[8].y,
                              mCableNodes[0].x, mCableNodes[0].y);
    if (!mIsArch) {
      len = -len;
    }

    mForcePolyNode.x = mOPrime.x + len * (float)Math.cos(dir);
    mForcePolyNode.y = mOPrime.y + len * (float)Math.sin(dir);
  }

/*  public void globalUpdate() {    // Apply contstraints
  // Cable start/end move: OPrime stays put. O moves.
  // Forces move: O moves. OPrime moves.

    isArch();
    if (mIsArch) {
      RbMag.mXOffset = -20;
      RbMag.mYOffset = 20;
      RaMag.mXOffset = -20;
      RaMag.mYOffset = 20;
      mRaYMag.mXOffset = -50;
      mRbYMag.mXOffset = 10;

      for (int i = 0; i < 8; i++) {
        mMembers[i].mColor = g.mRed;
        mForcePolyLines[i].mColor = g.mRed;
      }
    }
    else {
      RbMag.mXOffset = 10;
      RbMag.mYOffset = -10;
      RaMag.mXOffset = -40;
      RaMag.mYOffset = -10;
      mRaYMag.mXOffset = 10;
      mRbYMag.mXOffset = -50;

      for (int i = 0; i < 8; i++) {
        mMembers[i].mColor = g.mBlue;
        mForcePolyLines[i].mColor = g.mBlue;
      }
    }

    if (mUpdatedOnce) {
      for (int i = 0; i < 7; i++) {
        if (mForceTails[i].y > mLoads[i].mArrowHead.y)
          mForceTails[i].y = mLoads[i].mArrowHead.y;
      }
    }
    else
      mUpdatedOnce = true;

    if (mCableNodes[8].x < mCableNodes[0].x + 8)
      mCableNodes[8].x = mCableNodes[0].x + 8;

    if (g.selectedEntity == mCableNodes[0] || g.selectedEntity == mCableNodes[8]) {
      findO();
    }
    else {
      findOPrime();
    }


    distributeCableNodes();
    for (int i = 0; i < 7; i++) {
      mForceTails[i].x = mCableNodes[i+1].x;
      mForceDy[i] = mForceTails[i].y - mCableNodes[i+1].y;
    }
    findCableYs();
    findReactions();
    for (int i = 0; i < 7; i++) {
      mForceTails[i].y = mForceDy[i] + mCableNodes[i+1].y;
    }
    for (int i = 0; i < 8; i++) {
      mMembers[i].mSize = (int)Util.bound(mForcePolyLines[i].length() *
                                    WIDTH_MULT, MIN_WIDTH, MAX_WIDTH);
    }
/*    if (mLoadsVertical) {
      mForceTail.x = mLoad.mEndPoint.x;
    }

    if (mVerticalsVertical) {
      mTrussNodes[2].x = mTrussNodes[1].x;
      mTrussNodes[4].x = mTrussNodes[3].x;
      mTrussNodes[6].x = mTrussNodes[5].x;
      mTrussNodes[8].x = mTrussNodes[7].x;
      mTrussNodes[10].x = mTrussNodes[9].x;
    }

  }*/

  private void findOPrime() {
/*    mOPrime.x = mLoadLine[0].x;
    mOPrime.y = mLoadLine[0].y +
                         (mRb.mStartPoint.y - mRb.mEndPoint.y);*/
//    float len = Util.distance(mForcePolyNode.x, mForcePolyNode.y,
//                              mOPrime.x, mOPrime.y);
    float slope = Util.slope(mCableNodes[0].x, mCableNodes[0].y,
                              mCableNodes[8].x, mCableNodes[8].y);

    mOPrime.x = mLoadLine[0].x;
    mOPrime.y = mForcePolyNode.y - slope *
                         (mForcePolyNode.x - mOPrime.x);
  }


  private void isArch() {
    /*float downSum = 0.0f;
    for (int i = 0; i < 7; i++) {
      downSum += mForceTails[i].y - mCableNodes[i+1].y;
    }
    downSum *= mForcePolyNode.x - mLoadLine[0].x;
    mIsArch = downSum >= 0;*/

    mIsArch = mForcePolyNode.x < mLoadLine[0].x;
  }

  private void findReactions() {
    float len = mForcePolyLines[0].length() + mRa.ARROW_OFFSET;
    float dir = mForcePolyLines[0].direction();
    if (! mIsArch) {
      len = - len;
      mRa.mReverse = -1;
    }
    else {
      mRa.mReverse = 1;
    }

    mRaTail.x = mCableNodes[0].x + len * (float)Math.cos(dir);
    mRaTail.y = mCableNodes[0].y + len * (float)Math.sin(dir);

    if (mUpdatedOnce) {
      mRaX.x = mRa.mArrowHead.x;
      mRaX.y = mRa.mArrowTail.y;
      mRaY.x = mRa.mArrowTail.x;
      mRaY.y = mRa.mArrowHead.y;
    }

    len = mForcePolyLines[7].length() + mRb.ARROW_OFFSET;
    dir = mForcePolyLines[7].direction();
    if (! mIsArch) {
      len = - len;
      mRb.mReverse = -1;
    }
    else {
      mRb.mReverse = 1;
    }

    mRbTail.x = mCableNodes[8].x - len * (float)Math.cos(dir);
    mRbTail.y = mCableNodes[8].y - len * (float)Math.sin(dir);

    if (mUpdatedOnce) {
      mRbX.x = mRb.mArrowHead.x;
      mRbX.y = mRb.mArrowTail.y;
      mRbY.x = mRb.mArrowTail.x;
      mRbY.y = mRb.mArrowHead.y;
    }
  }

  private void findCableYs() {
    float y = mCableNodes[0].y;
    float xOver = (mCableNodes[8].x - mCableNodes[0].x) / 8.0f;
    for (int i = 0; i < mLoadLine.length - 1; i++) {
      y += Util.slope(mForcePolyNode.x, mForcePolyNode.y,
                      mLoadLine[i].x, mLoadLine[i].y) * xOver;
      mCableNodes[i + 1].y = y;
    }
    y += Util.slope(mForcePolyNode.x, mForcePolyNode.y,
                    mLoadLine[mLoadLine.length - 1].x,
                    mLoadLine[mLoadLine.length - 1].y) * xOver;
    mCableNodes[8].y = y;
  }

  private void distributeCableNodes() {
    float increment = (mCableNodes[8].x - mCableNodes[0].x) / 8.0f;
    float x = mCableNodes[0].x + increment;
    for (int i = 1; i < 8; i++) {
      mCableNodes[i].x = x;
      x += increment;
    }
  }

/*  public void update(Graphics graphics) {
    globalUpdate();

    Dimension d = getSize();
    if ((mGOff == null) || (d.width != mOffSize.width) || (d.height != mOffSize.height)) {
	    mOffscreen = createImage(d.width, d.height);
	    mOffSize = d;
	    mGOff = mOffscreen.getGraphics();
	    mGOff.setFont(getFont());
      W = d.width;
      H = d.height;
	  }

    mGOff.clearRect(0, 0, W, H);
    for (int i = 0; i < updateList.length; i++) {   // We update twice to filter through the changes
      updateList[i].update();
    }
    globalUpdate();
    for (int i = 0; i < updateList.length; i++) {
      updateList[i].update();
    }
    globalUpdate();
    mRbXMag.update();
    mRbYMag.update();
//    Util.tr("Is arch: " + mIsArch);
    for (int i = 0; i < drawList.length; i++) {
      drawList[i].draw(mGOff);
    }
    paint(mGOff);
    paint(graphics);
  }

  public void paint(Graphics graphics) {
    super.paint(graphics);
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
    else
      graphics.drawImage(mOffscreen, 0, 0, this);
  }*/

  public void start() {
    g.mTimer = new Timer(g);
    g.mTimer.start();
  }

  public void stop() {
    g.mTimer.stop();
  }

  //------------------------  SCENE CREATION

  private void makeText() {
    TText title = new TText();
    title.mText = "Hanging Cable/Arch";
    title.mSize = 24;
    title.x = 20;
    title.y = 50;
    title.mPosRelativeTo = GraphicEntity.VIEW_RELATIVE;
    addToDrawList(title);

    TTextPoint forcePoly = new TTextPoint();
    forcePoly.mBasePoint = mLoadLine[0];
    forcePoly.mXOffset = -160;
    forcePoly.mYOffset = 0;
    forcePoly.mSize = 20;
    forcePoly.mText = "Force Polygon";
    addToDrawList(forcePoly);

    TTextPoint formDiag = new TTextPoint();
    formDiag.mBasePoint = mCableNodes[1];
    formDiag.mXOffset = -180;
    formDiag.mYOffset = 0;
    formDiag.mSize = 20;
    formDiag.mText = "Form Diagram";
    addToDrawList(formDiag);
  }

  private void makeSupports() {
    TPin pin = new TPin(mCableNodes[0]);
    addToDrawList(pin);
    TPin pin2 = new TPin(mCableNodes[8]);
    addToDrawList(pin2);
  }

  private void makeNodes() {
    int x = CABLE_X_START;

    mCableNodes[0] = new TPoint(x, CABLE_Y_START);
    mCableNodes[8] = new TPoint(x + 8 * PANEL_SIZE, CABLE_Y_START);

    for (int i = 0; i < 7; i++) {
      mCableNodes[i+1] = new TPoint(x + (i + 1) * PANEL_SIZE, CABLE_Y_START);
      mCableNodes[i+1].mSelectable = false;
      mCableNodes[i+1].mControlPoint = false;
      mForceTails[i] = new TPoint(x + (i + 1) * PANEL_SIZE, CABLE_Y_START - START_FORCE_LENGTH);
      mForceTailStarts[i] = new TPoint(x + (i + 1) * PANEL_SIZE, CABLE_Y_START - START_FORCE_LENGTH);
      mEqualTails[i] = new TPoint(x + (i + 1) * PANEL_SIZE, CABLE_Y_START - START_FORCE_LENGTH);
      addToUpdateList(mForceTails[i]);

      mLoads[i] = new TLoad(g);
      addToUpdateList(mLoads[i]);
      mLoads[i].mStartPoint = mForceTails[i];
      mLoads[i].mEndPoint = mCableNodes[i+1];
    }
    mCableNodes[4].mLabelXOff = 0;
    mCableNodes[4].mLabelYOff = 24;
    mCableNodes[4].mLabel = "O";

    mHorizO = new TPoint();
  }

  private void addNodes() {
    addToDrawList(mCableNodes[0]);
    addToDrawList(mCableNodes[8]);
    for (int i = 1; i < 8; i++) {
      addToDrawList(mCableNodes[i]);
    }
    for (int i = 0; i < 7; i++) {
      addToDrawList(mForceTails[i]);
    }
  }


  private void makeMembers() {

    TLine groundLine = new TLine();
    groundLine.mStartPoint = mCableNodes[0];
    groundLine.mEndPoint = mCableNodes[8];
    groundLine.mColor = g.mYellow;
    groundLine.mSize = 3;
    groundLine.mDashed = true;
    groundLine.mDashLength = 7;
    groundLine.mGapLength = 5;
    addToDrawList(groundLine);

    for (int i = 0; i < 8; i++) {
      mMembers[i] = new TLine();
      mMembers[i].mStartPoint = mCableNodes[i];
      mMembers[i].mEndPoint = mCableNodes[i + 1];
      mMembers[i].dragAlso(mCableNodes[0]);
      mMembers[i].dragAlso(mCableNodes[8]);
      mMembers[i].mLabelXOff = 0;
      mMembers[i].mLabelYOff = -20;
      mMembers[i].mLabel = String.valueOf((char)('A' + i));
      addToDrawList(mMembers[i]);
    }
    for (int i = 0; i < 7; i++) {
      addToDrawListOnly(mLoads[i]);
    }

  }

  private void makeRb() {
    mRbTail = new TPoint();
    mRb = new TReaction();
    mRb.ARROW_OFFSET = 45;
    mRb.mArrowOffset = 45;
    mRb.mStartPoint = mRbTail;
    mRb.mEndPoint = mCableNodes[8];
    mRb.mColor = g.mGreen;
    mRb.mLabel = "Rb";
    mRb.mLabelXOff = 14;
    mRb.mLabelYOff = 0;
    addToDrawList(mRb);

    RbMag = new TTextPointLength(g);
    RbMag.mBasePoint = mRbTail;
    RbMag.mXOffset = -20;
    RbMag.mYOffset = 20;
    RbMag.mLine = mRb;
    addToDrawList(RbMag);


    mRbX = new TPoint();
    TLine newLine = new TLine();
    newLine.mStartPoint = mRb.mArrowHead;
    newLine.mEndPoint = mRbX;
    newLine.mColor = g.mGreen;
    newLine.mSize = 2;
    newLine.mDashed = true;
    newLine.mDashLength = 7;
    newLine.mGapLength = 5;
    newLine.mLabel = "Rbx";
    newLine.mLabelYOff = 0;
    newLine.mLabelXOff = 10;
    addToDrawList(newLine);

    mRbXMag = new TTextPointLength(g);
    mRbXMag.mBasePoint = mRbX;
    mRbXMag.mXOffset = 0;
    mRbXMag.mYOffset = 20;
    mRbXMag.mLine = newLine;
    addToDrawList(mRbXMag);

    mRbY = new TPoint();
    newLine = new TLine();
    newLine.mStartPoint = mRb.mArrowHead;
    newLine.mEndPoint = mRbY;
    newLine.mColor = g.mGreen;
    newLine.mSize = 2;
    newLine.mDashed = true;
    newLine.mDashLength = 7;
    newLine.mGapLength = 5;
    newLine.mLabel = "Rby";
    newLine.mLabelYOff = -10;
    newLine.mLabelXOff = 0;
    addToDrawList(newLine);

    mRbYMag = new TTextPointLength(g);
    mRbYMag.mBasePoint = mRbY;
    mRbYMag.mXOffset = -50;
    mRbYMag.mYOffset = 0;
    mRbYMag.mLine = newLine;
    addToDrawList(mRbYMag);
  }

  private void makeRa() {
    mRaTail = new TPoint();
    mRa = new TReaction();
    mRa.ARROW_OFFSET = 45;
    mRa.mArrowOffset = 45;
    mRa.mStartPoint = mRaTail;
    mRa.mEndPoint = mCableNodes[0];
    mRa.mColor = g.mGreen;
    mRa.mLabel = "Ra";
    mRa.mLabelXOff = -24;
    mRa.mLabelYOff = 0;
    addToDrawList(mRa);

    RaMag = new TTextPointLength(g);
    RaMag.mBasePoint = mRaTail;
    RaMag.mXOffset = -20;
    RaMag.mYOffset = 20;
    RaMag.mLine = mRa;
    addToDrawList(RaMag);

    mRaX = new TPoint();
    TLine newLine = new TLine();
    newLine.mStartPoint = mRa.mArrowHead;
    newLine.mEndPoint = mRaX;
    newLine.mColor = g.mGreen;
    newLine.mSize = 2;
    newLine.mDashed = true;
    newLine.mDashLength = 7;
    newLine.mGapLength = 5;
    newLine.mLabel = "Ray";
    newLine.mLabelYOff = 0;
    newLine.mLabelXOff = 10;
    addToDrawList(newLine);

    mRaXMag = new TTextPointLength(g);
    mRaXMag.mBasePoint = mRaX;
    mRaXMag.mXOffset = 0;
    mRaXMag.mYOffset = 20;
    mRaXMag.mLine = newLine;
    addToDrawList(mRaXMag);

    mRaY = new TPoint();
    newLine = new TLine();
    newLine.mStartPoint = mRa.mArrowHead;
    newLine.mEndPoint = mRaY;
    newLine.mColor = g.mGreen;
    newLine.mSize = 2;
    newLine.mDashed = true;
    newLine.mDashLength = 7;
    newLine.mGapLength = 5;
    newLine.mLabel = "Rax";
    newLine.mLabelYOff = -10;
    newLine.mLabelXOff = 0;
    addToDrawList(newLine);

    mRaYMag = new TTextPointLength(g);
    mRaYMag.mBasePoint = mRaY;
    mRaYMag.mXOffset = -50;
    mRaYMag.mYOffset = 0;
    mRaYMag.mLine = newLine;
    addToDrawList(mRaYMag);
  }

  private TLine mLoadLineLines[] = new TLine[3];
  private void makeLoadLine() {

    TPointTranslate newPoint;

    mLoadLine[0] = new TPoint(LOAD_LINE_START_X, LOAD_LINE_START_Y);
    //mLoadLine[0].x = LOAD_LINE_START_X;
    //mLoadLine[0].y = LOAD_LINE_START_Y;
    mLoadLine[0].mLabel = "a";
    mLoadLine[0].mLabelXOff = 14;
    mLoadLine[0].mLabelYOff = 0;
    addToUpdateList(mLoadLine[0]);

    for (int i = 0; i < 7; i++) {
      newPoint = new TPointTranslate();
      mLoadLine[i + 1] = newPoint;
      newPoint.mBasePoint = mLoadLine[i];
      newPoint.mVectorStart = mForceTails[i];
      newPoint.mVectorEnd = mLoads[i].mArrowHead;
      newPoint.mLabel = String.valueOf((char)('b' + i));
      newPoint.mLabelXOff = 14;
      newPoint.mLabelYOff = 0;
      newPoint.mSize = 7;
      newPoint.dragAlso(mLoadLine[0]);
      addToUpdateList(newPoint);

      TLine newLine = new TLine();
      newLine.mStartPoint = mLoadLine[i];
      newLine.mEndPoint = mLoadLine[i+1];
      newLine.mColor = mLoads[0].mColor;
      newLine.mSize = 4;
      newLine.dragAlso(mLoadLine[0]);
      addToDrawList(newLine);
    }

    mOPrime = new TPoint();
    mOPrime.mSize = 7;
    mOPrime.mControlPoint = false;
    mOPrime.mSelectable = false;
    addToDrawList(mOPrime);
  }

  private void makeForcePolygon() {
    TPoint newNode;

    newNode = new TPoint(LOAD_LINE_START_X - 100, LOAD_LINE_START_Y + 157);
    mForcePolyNode = newNode;
    newNode.mLabel = "O";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    addToUpdateList(newNode);
    mLoadLine[0].dragAlso(newNode);

// Force poly lines

    TLine groundLine = new TLine();
    groundLine.mStartPoint = mForcePolyNode;
    groundLine.mEndPoint = mOPrime;
    groundLine.mColor = g.mYellow;
    groundLine.mSize = 3;
    groundLine.mDashed = true;
    groundLine.mDashLength = 7;
    groundLine.mGapLength = 5;
    groundLine.dragAlso(mLoadLine[0]);
    addToDrawList(groundLine);

    TLineForcePoly newLine;

    for (int i = 0; i < mLoadLine.length; i++) {
      mForcePolyLines[i] = new TLine();
      mForcePolyLines[i].mStartPoint = mLoadLine[i];
      mForcePolyLines[i].mEndPoint = newNode;
      addToDrawList(mForcePolyLines[i]);
      mForcePolyLines[i].mSize = 2;
      mForcePolyLines[i].dragAlso(mLoadLine[0]);
    }

    addToDrawListOnly(newNode);
    for (int i = 0; i < mLoadLine.length; i++) {
      addToDrawListOnly(mLoadLine[i]);
    }
  }

  public static final int REPORT_X_START = 60;
  public static final int REPORT_Y_START = 480;
  public static final int REPORT_LINE_SPACE = 17;
  public static final int REPORT_COLUMN_SPACE = 110;

  private void makeReport() {
/*    int x = REPORT_X_START;
    int y = REPORT_Y_START;

    TText newText = new TText();
    newText.mSize = 18;
    newText.mText = "Member forces";
    newText.x = x;
    newText.y = y;
    addToDrawList(newText);
    y += REPORT_LINE_SPACE * 1.2;

    TTextLength newReport;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[0];
    newReport.mPrefix = "A1 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[1];
    newReport.mPrefix = "B1 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[2];
    newReport.mPrefix = "C1 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);*/
  }

  private void makeLinesOfAction() {

    mResultantStartNode = new TPoint();
    mResultantEndNode = new TPoint();

    mActionIntersect = new TPointIntersect(mRa, mRb);
    mActionIntersect.x = 20;
    mActionIntersect.y = 20;
    mActionIntersect.mConsiderExtents = false;
    addToDrawList(mActionIntersect);


    mRaLineOfAction = new TLine();
    mRaLineOfAction.mStartPoint = mRa.mEndPoint;
    mRaLineOfAction.mEndPoint = mActionIntersect;
    mRaLineOfAction.mSize = 2;
    mRaLineOfAction.mDashed = true;
    mRaLineOfAction.mConsiderExtents = false;
    mRaLineOfAction.mColor = g.mGreen;
    addToDrawList(mRaLineOfAction);

    mRbLineOfAction = new TLine();
    mRbLineOfAction.mStartPoint = mRb.mEndPoint;
    mRbLineOfAction.mEndPoint = mActionIntersect;
    mRbLineOfAction.mSize = 2;
    mRbLineOfAction.mDashed = true;
    mRbLineOfAction.mConsiderExtents = false;
    mRbLineOfAction.mColor = g.mGreen;
    addToDrawList(mRbLineOfAction);

    mLoadLineOfAction = new TArrow();
    mLoadLineOfAction.mStartPoint = mResultantStartNode;
    mLoadLineOfAction.mEndPoint = mResultantEndNode;
    mLoadLineOfAction.mArrowOffset = 0;
    mLoadLineOfAction.mSize = 2;
    mLoadLineOfAction.mDashed = true;
    mLoadLineOfAction.mConsiderExtents = false;
    mLoadLineOfAction.mColor = Color.darkGray;
    addToDrawList(mLoadLineOfAction);
  }

  public static final int BUTTON_START_X = 20;
  public static final int BUTTON_START_Y = 70;
  public static final int BUTTON_Y_OFFSET = 30;

  private void makeButtons() {
    int x = BUTTON_START_X;
    int y = BUTTON_START_Y;

    TButton moveButton = new TButton("Return To Starting Position");
    moveButton.x = x;
    moveButton.y = y;
    moveButton.mWidth = 170;
    moveButton.mHeight = 20;
    addToDrawList(moveButton);
    moveButton.mAction = new TAction() {
      public void run() {
        g.mTimer.clearJobs();

        JobMoveViewToOrigin originMove = new JobMoveViewToOrigin(g);
        originMove.mView = mUpdateCanvas;
        g.mTimer.addJob(originMove);

        /*while (g.mTimer.numJobs() > 0) {
          try {
            Thread.sleep(20);
          }
          catch(Exception e) {
          }
        }*/

        mSupportsHoriz = false;
        mHorizButton.mSelected = false;

        g.selectedEntity = mForceTails[0];

        JobMovePointToPoint nextJob;
        JobMovePointToPoint thisJob = null;
        for (int i = 0; i < 7; i++) {
          mForceTails[i].y = mForceTailStarts[i].y;
//          nextJob = new JobMovePointToPoint(thisJob, g, mForceTails[i], mForceTailStarts[i]);
//          g.mTimer.addJob(nextJob);
//          thisJob = nextJob;
        }

        JobMovePointToStart leftHomeJob = new JobMovePointToStart(null, g);
        leftHomeJob.mMovePoint = mCableNodes[0];
        g.mTimer.addJob(leftHomeJob);

        JobMovePointToStart rightHomeJob = new JobMovePointToStart(leftHomeJob, g);
        rightHomeJob.mMovePoint = mCableNodes[8];
        g.mTimer.addJob(rightHomeJob);

        JobMovePointToStart newJob = new JobMovePointToStart(rightHomeJob, g);
        newJob.mMovePoint = mLoadLine[0];
        g.mTimer.addJob(newJob);

        JobMovePointToStart polyHomeJob = new JobMovePointToStart(newJob, g);
        polyHomeJob.mMovePoint = mForcePolyNode;
        g.mTimer.addJob(polyHomeJob);
      }
    };

    y += BUTTON_Y_OFFSET;
    mHorizButton = new TButton("Keep supports level");
    mHorizButton.x = x;
    mHorizButton.y = y;
    mHorizButton.mWidth = 170;
    mHorizButton.mHeight = 20;
    mHorizButton.mIsToggle = true;
    addToDrawList(mHorizButton);
    mHorizButton.mAction = new TAction() {
      public void run() {
        g.selectedEntity = mCableNodes[8];

        if (mSupportsHoriz) {
          mSupportsHoriz = false;
          return;
        }

        JobMovePointToPoint rightHoriz = new JobMovePointToPoint(g, mForcePolyNode, mHorizO);
        g.mTimer.addJob(rightHoriz);
        g.mTimer.addJob(new TimerJob(rightHoriz, g) {
          public void step() {
            if (!afterJob.done)
              return;

            mSupportsHoriz = true;
            done = true;
          }
        });
      }
    };

    y += BUTTON_Y_OFFSET;
    TButton mEqualButton = new TButton("Equalize Loads");
    mEqualButton.x = x;
    mEqualButton.y = y;
    mEqualButton.mWidth = 170;
    mEqualButton.mHeight = 20;
    addToDrawList(mEqualButton);
    mEqualButton.mAction = new TAction() {
      public void run() {
        JobMovePointToPoint nextJob;
        JobMovePointToPoint thisJob = null;
        for (int i = 0; i < 7; i++) {
          nextJob = new JobMovePointToPoint(thisJob, g, mForceTails[i], mEqualTails[i]);
          g.mTimer.addJob(nextJob);
          thisJob = nextJob;
        }

        if (mSupportsHoriz) {
          JobMovePointToPoint rightHoriz = new JobMovePointToPoint(thisJob, g, mForcePolyNode, mHorizO);
          g.mTimer.addJob(rightHoriz);
        }

      }
    };
    y += BUTTON_Y_OFFSET;

    mLinesOfActionCheck = new TButton("Extend Lines of Action");
    mLinesOfActionCheck.x = x;
    mLinesOfActionCheck.y = y;
    mLinesOfActionCheck.mWidth = 170;
    mLinesOfActionCheck.mHeight = 20;
    mLinesOfActionCheck.mIsToggle = true;
    addToDrawList(mLinesOfActionCheck);
    mLinesOfActionCheck.mAction = new TAction() {
      public void run() {
        mLinesOfAction = mLinesOfActionCheck.mSelected;
        repaint();
      }
    };

  }
}