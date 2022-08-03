package truss;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;

public class MinWeightApplet extends Applet {
  public static final int APPLET_WIDTH = 880;
  public static final int APPLET_HEIGHT = 700;

  public static final int PANEL_SIZE = 80;
  public static final int TRUSS_X_START = 150;
  public static final int TRUSS_Y_START = 400;
  public final int START_FORCE_LENGTH = 60;
  public static final int LOAD_LINE_START_X = 560;
  public static final int LOAD_LINE_START_Y = 30;

  public GraphicEntity[] drawList = new GraphicEntity[0];
  public GraphicEntity[] updateList = new GraphicEntity[0];
  private Image mOffscreen;
  private Graphics mGOff;
  Dimension mOffSize;
  public int W;
  public int H;

  private Point tempPoint = new Point();
  private int paintedTwice = 0;

  TButton loadsVertCheck;
  TButton vertsVertCheck;
  TButton topLevelCheck;
  TButton bottomLevelCheck;
  TButton mVertMirrorButton;
  TButton mHorizMirrorButton;
  TButton mFairnessCheck;
  TButton equalizeButton;


  public TPoint[] mTrussNodes = new TPoint[12];
  public TPoint[] mForceTails = new TPoint[5];

  public TPoint[] mLowestTrussNodes = new TPoint[12];
  public TPoint[] mLowestForceTails = new TPoint[5];

  public TArrow[] mLoads = new TArrow[5];
  public TLineMember[] mMembers = new TLineMember[21];
  public TPoint mRbTail;
  public TReaction mRb;
  public TPoint mRaTail;
  public TArrow mRa;
  public TPoint[] mLoadLine = new TPoint[7];
  public TPointForcePoly mForcePolyNodes[] = new TPointForcePoly[11];
  public TLineForcePoly mForcePolyLines[] = new TLineForcePoly[21];

  public boolean mLoadsVertical = false;
  public boolean mVerticalsVertical = false;
  public boolean mBottomLevel = false;
  public boolean mTopLevel = false;
  public boolean mVertMirror = false;
  public boolean mHorizMirror = false;
  public boolean mFairness = true;

  public boolean mFairLowest = true;

  public TPolygon mGraphPoly;
  public float mTotalWeight;

  public float mHighest;
  public float mLowest;
  public TButton mLowestButton;
  public TButton mClearMinButton;
  public TText  mHowLow;

  private NoScrollUpdateCanvas mUpdateCanvas;

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

  /**Initialize the applet*/
  public void init() {
    g = new G(this);
    new Types();
    setLayout(null);
    g.mFrame = this;
    g.selectedEntity = null;
    g.mLengthDivisor = 10.0f;

    mGraphPoly = new TPolygon();
    addToDrawListOnly(mGraphPoly);

    mLowest = 9999999;

    makeButtons();
    makeBarGraph();

    makeNodes();
    makeMembers();
    makeLoads();
    addNodes();
    makeRb();
    makeLoadLine();
    makeRa();
    makeForcePolygon();
    makeTriangleLabels();
    makeText();
    makeSupports();
    makeReport();
    saveLowest();



    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    mUpdateCanvas.globalUpdate();
    repaint();
  }

  /**Component initialization*/
  private void jbInit() throws Exception {
    setBackground(g.mBackground);
    W = getSize().width;
    H = getSize().height;

    mUpdateCanvas = new NoScrollUpdateCanvas(this, g) {
      public void globalUpdate() {    // Apply contstraints
        mFairnessCheck.mInvisible = true;

        for (int i = 0; i < 12; i++) {
          mTrussNodes[i].mControlPoint = true;
          mTrussNodes[i].mSelectable = true;
          mTrussNodes[i].mSize = TPoint.DEFAULT_SIZE;
        }
        for (int i = 0; i < 5; i++) {
          mForceTails[i].mControlPoint = true;
          mForceTails[i].mSelectable = true;
          mForceTails[i].mSize = TPoint.DEFAULT_SIZE;
        }

        if (mFairness) {
          if (mTrussNodes[11].x - mTrussNodes[0].x != mTrussNodes[11].xStart - mTrussNodes[0].xStart) {
            if (!mHorizMirror && this.g.mTimer.numJobs() == 0) {
              mHorizMirrorButton.mSelected = true;
              mHorizMirror = true;
            }
          }


          loadsVertCheck.mInvisible = true;
          equalizeButton.mInvisible = true;
          mGraphPoly.mColor = G.mYellow;
          mHowLow.mText = "How low can you go?";

          mTrussNodes[0].mControlPoint = false;
          mTrussNodes[0].mSelectable = false;
          mTrussNodes[0].mSize = 8;

          mTrussNodes[11].mControlPoint = false;
          mTrussNodes[11].mSelectable = false;
          mTrussNodes[11].mSize = 8;

          for (int i = 0; i < 5; i++) {
            mForceTails[i].mControlPoint = false;
            mForceTails[i].mSelectable = false;
            mForceTails[i].mSize = 8;

            mTrussNodes[i*2 + 1].x = mTrussNodes[0].x + (i+1) * PANEL_SIZE;
            mForceTails[i].x = mTrussNodes[0].x + (i+1) * PANEL_SIZE;
            mForceTails[i].y = mTrussNodes[i*2 + 1].y - START_FORCE_LENGTH;
          }
        }
        else {
          mTrussNodes[0].mControlPoint = false;
          mTrussNodes[0].mSelectable = false;
          mTrussNodes[0].mSize = 8;

          mTrussNodes[11].mControlPoint = false;
          mTrussNodes[11].mSelectable = false;
          mTrussNodes[11].mSize = 8;

          mHorizMirrorButton.mInvisible = false;
          loadsVertCheck.mInvisible = false;
          vertsVertCheck.mInvisible = false;
          mHorizMirrorButton.mInvisible = false;
          equalizeButton.mInvisible = false;
          mGraphPoly.mColor = G.mRed;
          mHowLow.mText = "Cheating makes it easy.";
        }




        if (mLoadsVertical) {
          for (int i = 0; i < 5; i++) {
            mForceTails[i].x = mLoads[i].mEndPoint.x;
          }
        }

    /*    if (g.selectedEntity ==  //mVerticalsVertical) {
          mTrussNodes[2].x = mTrussNodes[1].x;
          mTrussNodes[4].x = mTrussNodes[3].x;
          mTrussNodes[6].x = mTrussNodes[5].x;
          mTrussNodes[8].x = mTrussNodes[7].x;
          mTrussNodes[10].x = mTrussNodes[9].x;
        }*/
        if (mVerticalsVertical) {
          mTrussNodes[2].x = mTrussNodes[1].x;
          mTrussNodes[4].x = mTrussNodes[3].x;
          mTrussNodes[6].x = mTrussNodes[5].x;
          mTrussNodes[8].x = mTrussNodes[7].x;
          mTrussNodes[10].x = mTrussNodes[9].x;
        }

        if (mBottomLevel) {
          mTrussNodes[2].y = mTrussNodes[0].y;
          mTrussNodes[4].y = mTrussNodes[0].y;
          mTrussNodes[6].y = mTrussNodes[0].y;
          mTrussNodes[8].y = mTrussNodes[0].y;
          mTrussNodes[10].y = mTrussNodes[0].y;
          mTrussNodes[11].y = mTrussNodes[0].y;
        }

        if (mTopLevel) {
          float oldY;
          oldY = mTrussNodes[3].y;
          mTrussNodes[3].y = mTrussNodes[1].y;
          mForceTails[1].y += mTrussNodes[3].y - oldY;

          oldY = mTrussNodes[5].y;
          mTrussNodes[5].y = mTrussNodes[1].y;
          mForceTails[2].y += mTrussNodes[5].y - oldY;

          oldY = mTrussNodes[7].y;
          mTrussNodes[7].y = mTrussNodes[1].y;
          mForceTails[3].y += mTrussNodes[7].y - oldY;

          oldY = mTrussNodes[9].y;
          mTrussNodes[9].y = mTrussNodes[1].y;
          mForceTails[4].y += mTrussNodes[9].y - oldY;
        }

        if (mHorizMirror) {
          for (int i = 7; i < 12; i++) {
            mTrussNodes[i].mControlPoint = false;
            mTrussNodes[i].mSelectable = false;
            mTrussNodes[i].mSize = 8;
          }

          float centerX = mTrussNodes[5].x;
          float oldX, oldY;

          mTrussNodes[6].x = mTrussNodes[5].x;

          oldX = mTrussNodes[7].x;
          oldY = mTrussNodes[7].y;
          mTrussNodes[7].x = centerX + (centerX - mTrussNodes[3].x);
          mTrussNodes[7].y = mTrussNodes[3].y;
          mForceTails[3].x += mTrussNodes[7].x - oldX;
          mForceTails[3].y += mTrussNodes[7].y - oldY;

          oldX = mTrussNodes[9].x;
          oldY = mTrussNodes[9].y;
          mTrussNodes[9].x = centerX + (centerX - mTrussNodes[1].x);
          mTrussNodes[9].y = mTrussNodes[1].y;
          mForceTails[4].x += mTrussNodes[9].x - oldX;
          mForceTails[4].y += mTrussNodes[9].y - oldY;

          mTrussNodes[8].x = centerX + (centerX - mTrussNodes[4].x);
          mTrussNodes[8].y = mTrussNodes[4].y;
          mTrussNodes[10].x = centerX + (centerX - mTrussNodes[2].x);
          mTrussNodes[10].y = mTrussNodes[2].y;
          mTrussNodes[11].x = centerX + (centerX - mTrussNodes[0].x);
          mTrussNodes[11].y = mTrussNodes[0].y;
        }

        if (mVertMirror) {
          mTrussNodes[2].mControlPoint = false;
          mTrussNodes[2].mSelectable = false;
          mTrussNodes[2].mSize = 8;
          mTrussNodes[4].mControlPoint = false;
          mTrussNodes[4].mSelectable = false;
          mTrussNodes[4].mSize = 8;
          mTrussNodes[6].mControlPoint = false;
          mTrussNodes[6].mSelectable = false;
          mTrussNodes[6].mSize = 8;
          mTrussNodes[8].mControlPoint = false;
          mTrussNodes[8].mSelectable = false;
          mTrussNodes[8].mSize = 8;
          mTrussNodes[10].mControlPoint = false;
          mTrussNodes[10].mSelectable = false;
          mTrussNodes[10].mSize = 8;

          float centerY = mTrussNodes[0].y;

          mTrussNodes[11].y = mTrussNodes[0].y;

          mTrussNodes[2].y = centerY + (centerY - mTrussNodes[1].y);
          mTrussNodes[2].x = mTrussNodes[1].x;
          mTrussNodes[4].y = centerY + (centerY - mTrussNodes[3].y);
          mTrussNodes[4].x = mTrussNodes[3].x;
          mTrussNodes[6].y = centerY + (centerY - mTrussNodes[5].y);
          mTrussNodes[6].x = mTrussNodes[5].x;
          mTrussNodes[8].y = centerY + (centerY - mTrussNodes[7].y);
          mTrussNodes[8].x = mTrussNodes[7].x;
          mTrussNodes[10].y = centerY + (centerY - mTrussNodes[9].y);
          mTrussNodes[10].x = mTrussNodes[9].x;
        }
      }
    };
    mUpdateCanvas.drawList = drawList;
    mUpdateCanvas.updateList = updateList;
    mUpdateCanvas.mGlobalUpdateEveryTime = true;

    add(mUpdateCanvas);

    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        mUpdateCanvas.appletResized();
      }
    });
  }


  public void repaint() {
    if (mUpdateCanvas != null)
      mUpdateCanvas.repaint();
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

/*  public void globalUpdate() {    // Apply contstraints

  }
*/
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
    for (int i = 0; i < updateList.length; i++) {
      updateList[i].update();
    }
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
    title.mText = "Minimum Material Truss";
    title.mSize = 24;
    title.x = 20;
    title.y = 50;
    title.mPosRelativeTo = GraphicEntity.VIEW_RELATIVE;
    addToDrawList(title);

    TTextPoint forcePoly = new TTextPoint();
    forcePoly.mBasePoint = mLoadLine[0];
    forcePoly.mXOffset = -160;
    forcePoly.mYOffset = 10;
    forcePoly.mSize = 20;
    forcePoly.mText = "Force Polygon";
    addToDrawList(forcePoly);

    TTextPoint formDiag = new TTextPoint();
    formDiag.mBasePoint = mTrussNodes[1];
    formDiag.mXOffset = -124;
    formDiag.mYOffset = -16;
    formDiag.mSize = 20;
    formDiag.mText = "Form";
    addToDrawList(formDiag);

    formDiag = new TTextPoint();
    formDiag.mBasePoint = mTrussNodes[1];
    formDiag.mXOffset = -124;
    formDiag.mYOffset = 12;
    formDiag.mSize = 20;
    formDiag.mText = "Diagram";
    addToDrawList(formDiag);
  }

  private void makeNodes() {
    int x = TRUSS_X_START - PANEL_SIZE;
    for (int i = 0; i < 12; i++) {

      if (i % 2 == 0) {
        mTrussNodes[i] = new TPoint(x, TRUSS_Y_START);
        x += PANEL_SIZE;
      }
      else {
        mTrussNodes[i] = new TPoint(x, TRUSS_Y_START - PANEL_SIZE);
      }
    }
    mTrussNodes[11].y = TRUSS_Y_START;
    mTrussNodes[11].yStart = TRUSS_Y_START;
    mTrussNodes[6].mLabel = "G";
    mTrussNodes[6].mLabelXOff = -5;
    mTrussNodes[6].mLabelYOff = 30;

    x = TRUSS_X_START;
    for (int i = 0; i < 5; i++) {
      mForceTails[i] = new TPoint(x, TRUSS_Y_START - PANEL_SIZE - START_FORCE_LENGTH);
      x += PANEL_SIZE;
    }

    // Add dragging relationships
    mTrussNodes[1].dragAlso(mForceTails[0]);
    mTrussNodes[3].dragAlso(mForceTails[1]);
    mTrussNodes[5].dragAlso(mForceTails[2]);
    mTrussNodes[7].dragAlso(mForceTails[3]);
    mTrussNodes[9].dragAlso(mForceTails[4]);
  }

  private void addNodes() {
    for (int i = 0; i < 12; i++) {
      addToDrawList(mTrussNodes[i]);
    }
    for (int i = 0; i < 5; i++) {
      addToDrawList(mForceTails[i]);
    }

  }

  private void makeLoads() {
    for(int i = 0; i < 5; i++) {
      mLoads[i] = new TLoad(g);
      addToDrawList(mLoads[i]);
    }
    mLoads[0].mStartPoint = mForceTails[0];
    mLoads[0].mEndPoint = mTrussNodes[1];
    mLoads[1].mStartPoint = mForceTails[1];
    mLoads[1].mEndPoint = mTrussNodes[3];
    mLoads[2].mStartPoint = mForceTails[2];
    mLoads[2].mEndPoint = mTrussNodes[5];
    mLoads[3].mStartPoint = mForceTails[3];
    mLoads[3].mEndPoint = mTrussNodes[7];
    mLoads[4].mStartPoint = mForceTails[4];
    mLoads[4].mEndPoint = mTrussNodes[9];
  }

  private void makeMembers() {
    for(int i = 0; i < 21; i++) {
      mMembers[i] = new TLineMember(g);
      mMembers[i].mIsWeightMember = true;
      for (int j = 0; j < 12; j++) {
        mMembers[i].dragAlso(mTrussNodes[j]);
      }

      addToDrawList(mMembers[i]);
    }
    mMembers[0].mStartPoint = mTrussNodes[0];
    mMembers[0].mEndPoint = mTrussNodes[1];
    mMembers[0].mLabelXOff = -14;
    mMembers[0].mLabelYOff = -14;
    mMembers[0].mLabel = "A";

    mMembers[1].mStartPoint = mTrussNodes[0];
    mMembers[1].mEndPoint = mTrussNodes[2];
    mMembers[2].mStartPoint = mTrussNodes[1];
    mMembers[2].mEndPoint = mTrussNodes[2];
    mMembers[3].mStartPoint = mTrussNodes[1];
    mMembers[3].mEndPoint = mTrussNodes[3];
    mMembers[3].mLabel = "B";

    mMembers[4].mStartPoint = mTrussNodes[1];
    mMembers[4].mEndPoint = mTrussNodes[4];
    mMembers[5].mStartPoint = mTrussNodes[2];
    mMembers[5].mEndPoint = mTrussNodes[4];
    mMembers[6].mStartPoint = mTrussNodes[3];
    mMembers[6].mEndPoint = mTrussNodes[4];
    mMembers[7].mStartPoint = mTrussNodes[3];
    mMembers[7].mEndPoint = mTrussNodes[5];
    mMembers[7].mLabel = "C";

    mMembers[8].mStartPoint = mTrussNodes[4];
    mMembers[8].mEndPoint = mTrussNodes[5];
    mMembers[9].mStartPoint = mTrussNodes[4];
    mMembers[9].mEndPoint = mTrussNodes[6];
    mMembers[10].mStartPoint = mTrussNodes[5];
    mMembers[10].mEndPoint = mTrussNodes[6];
    mMembers[11].mStartPoint = mTrussNodes[5];
    mMembers[11].mEndPoint = mTrussNodes[7];
    mMembers[11].mLabel = "D";

    mMembers[12].mStartPoint = mTrussNodes[5];
    mMembers[12].mEndPoint = mTrussNodes[8];
    mMembers[13].mStartPoint = mTrussNodes[6];
    mMembers[13].mEndPoint = mTrussNodes[8];
    mMembers[14].mStartPoint = mTrussNodes[7];
    mMembers[14].mEndPoint = mTrussNodes[8];
    mMembers[15].mStartPoint = mTrussNodes[7];
    mMembers[15].mLabel = "E";

    mMembers[15].mEndPoint = mTrussNodes[9];
    mMembers[16].mStartPoint = mTrussNodes[8];
    mMembers[16].mEndPoint = mTrussNodes[9];
    mMembers[17].mStartPoint = mTrussNodes[8];
    mMembers[17].mEndPoint = mTrussNodes[10];
    mMembers[18].mStartPoint = mTrussNodes[9];
    mMembers[18].mEndPoint = mTrussNodes[10];
    mMembers[19].mStartPoint = mTrussNodes[9];
    mMembers[19].mEndPoint = mTrussNodes[11];
    mMembers[19].mLabelXOff = 14;
    mMembers[19].mLabelYOff = -14;
    mMembers[19].mLabel = "F";

    mMembers[20].mStartPoint = mTrussNodes[10];
    mMembers[20].mEndPoint = mTrussNodes[11];
  }

  private void makeRb() {
    mRbTail = new TPoint() {
      float totalMoment;
      float pDist;
      public void update() {
        totalMoment = 0.0f;
        for (int i = 0; i < 5; i++) {
          totalMoment += mLoads[i].moment(mTrussNodes[0]);
        }
        totalMoment *= TLine.CCW(mTrussNodes[11].x, mTrussNodes[11].y - 10,
                           mTrussNodes[11].x, mTrussNodes[11].y,
                           mTrussNodes[0].x, mTrussNodes[0].y);

        float pDist = TLine.perpDist(mTrussNodes[11].x, mTrussNodes[11].y,
                                      mTrussNodes[11].x, mTrussNodes[11].y + 10,
                                      mTrussNodes[0].x, mTrussNodes[0].y);
        if (Math.abs(pDist) < 0.1f)
          pDist = 1.0f;
        totalMoment /= pDist;
        x = mTrussNodes[11].x;
        y = mTrussNodes[11].y + Math.abs(totalMoment) + mRb.ARROW_OFFSET;
        if (totalMoment < 0) {
          mRb.mReverse = -1;
        }
        else {
          mRb.mReverse = 1;
        }
    //    Util.tr("Rb: " + mLocation.x + ", " + mLocation.y);

      }

      public boolean hit(Point p) {
        return false;
      }
    };
    addToUpdateList(mRbTail);
    mRb = new TReaction();
    mRb.ARROW_OFFSET = 45;
    mRb.mArrowOffset = 45;
    mRb.mStartPoint = mRbTail;
    mRb.mEndPoint = mTrussNodes[11];
    mRb.mColor = g.mGreen;
    mRb.mLabel = "Rb";
    mRb.mLabelXOff = 15;
    mRb.mLabelYOff = 0;
    addToDrawList(mRb);

    TTextPointLength RbMag = new TTextPointLength(g);
    RbMag.mBasePoint = mRbTail;
    RbMag.mXOffset = -20;
    RbMag.mYOffset = 20;
    RbMag.mLine = mRb;
    addToDrawList(RbMag);
  }

  private void makeRa() {
    mRaTail = new TPoint() {
      private float mDir;
      private float mDist;

      public void update() {
        mDir = Util.direction(mLoadLine[6].x, mLoadLine[6].y,
                              mLoadLine[0].x, mLoadLine[0].y );
        mDist = Util.distance(mLoadLine[6].x, mLoadLine[6].y,
                              mLoadLine[0].x, mLoadLine[0].y ) + mRa.ARROW_OFFSET;
        x = mTrussNodes[0].x - mDist * (float)Math.cos(mDir);
        y = mTrussNodes[0].y - mDist * (float)Math.sin(mDir);
      }

      public boolean hit(Point p) {
        return false;
      }
    };
    addToUpdateList(mRaTail);
    mRa = new TArrow();
    mRa.ARROW_OFFSET = 45;
    mRa.mArrowOffset = 45;
    mRa.mStartPoint = mRaTail;
    mRa.mEndPoint = mTrussNodes[0];
    mRa.mColor = g.mGreen;
    mRa.mLabel = "Ra";
    mRa.mLabelXOff = -30;
    mRa.mLabelYOff = 0;
    addToDrawList(mRa);

    TTextPointLength RaMag = new TTextPointLength(g);
    RaMag.mBasePoint = mRaTail;
    RaMag.mXOffset = -20;
    RaMag.mYOffset = 20;
    RaMag.mLine = mRa;
    addToDrawList(RaMag);

  }

  private TLine mLoadLineLines[] = new TLine[7];
  private void makeLoadLine() {
    TPointTranslate newPoint;

    mLoadLine[0] = new TPoint(LOAD_LINE_START_X, LOAD_LINE_START_Y);
    TPoint prevPoint = mLoadLine[0];
    mLoadLine[0].mLabel = "a";
    mLoadLine[0].mLabelXOff = 14;
    mLoadLine[0].mLabelYOff = 0;
    mLoadLine[0].mSize = 6;

    for (int i = 1; i < 6; i++) {
      newPoint = new TPointTranslate();
      mLoadLine[i] = newPoint;
      newPoint.mBasePoint = prevPoint;
      newPoint.mVectorStart = mForceTails[i-1];
      newPoint.mVectorEnd = mLoads[i-1].mArrowHead;
      newPoint.mLabel = String.valueOf((char)('a' + i));
      newPoint.mLabelXOff = 14;
      newPoint.mLabelYOff = 0;
      newPoint.mSize = 7;
      newPoint.dragAlso(mLoadLine[0]);
      prevPoint = newPoint;
    }
    newPoint = new TPointTranslate();
    mLoadLine[6] = newPoint;
    newPoint.mBasePoint = prevPoint;
    newPoint.mVectorStart = mRb.mArrowTail;
    newPoint.mVectorEnd = mRb.mArrowHead;
    newPoint.mLabel = "g";
    newPoint.mLabelXOff = 14;
    newPoint.mLabelYOff = 0;
    newPoint.mSize = 7;
    newPoint.dragAlso(mLoadLine[0]);

    for (int i = 0; i < 7; i++) {
      mLoadLineLines[i] = new TLine();
      mLoadLineLines[i].mColor = Color.darkGray;
      mLoadLineLines[i].mSize = 4;
      mLoadLineLines[i].mStartPoint = mLoadLine[i];
      mLoadLineLines[i].mEndPoint = mLoadLine[(i + 1) % 7];
      mLoadLineLines[i].dragAlso(mLoadLine[0]);
      addToDrawList(mLoadLineLines[i]);
    }
    mLoadLineLines[5].mColor = g.mGreen;
    mLoadLineLines[6].mColor = g.mGreen;
  }

  private void makeForcePolygon() {
    TPointForcePoly newNode;

    newNode = new TPointForcePoly();
    mForcePolyNodes[1] = newNode;
    newNode.mMember1 = mMembers[0];
    newNode.mMember1ForceBegin = mLoadLine[0];
    newNode.mMember2 = mMembers[1];
    newNode.mMember2ForceBegin = mLoadLine[6];
    newNode.mLabel = "1";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[2] = newNode;
    newNode.mMember1 = mMembers[2];
    newNode.mMember1ForceBegin = mForcePolyNodes[1];
    newNode.mMember2 = mMembers[5];
    newNode.mMember2ForceBegin = mLoadLine[6];
    newNode.mLabel = "2";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = 14;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[3] = newNode;
    newNode.mMember1 = mMembers[4];
    newNode.mMember1ForceBegin = mForcePolyNodes[2];
    newNode.mMember2 = mMembers[3];
    newNode.mMember2ForceBegin = mLoadLine[1];
    newNode.mLabel = "3";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[4] = newNode;
    newNode.mMember1 = mMembers[6];
    newNode.mMember1ForceBegin = mForcePolyNodes[3];
    newNode.mMember2 = mMembers[7];
    newNode.mMember2ForceBegin = mLoadLine[2];
    newNode.mLabel = "4";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);
    addToDrawList(newNode);

    newNode = new TPointForcePoly();
    mForcePolyNodes[5] = newNode;
    newNode.mMember1 = mMembers[8];
    newNode.mMember1ForceBegin = mForcePolyNodes[4];
    newNode.mMember2 = mMembers[9];
    newNode.mMember2ForceBegin = mLoadLine[6];
    newNode.mLabel = "5";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[6] = newNode;
    newNode.mMember1 = mMembers[10];
    newNode.mMember1ForceBegin = mForcePolyNodes[5];
    newNode.mMember2 = mMembers[13];
    newNode.mMember2ForceBegin = mLoadLine[6];
    newNode.mLabel = "6";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = 14;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[7] = newNode;
    newNode.mMember1 = mMembers[12];
    newNode.mMember1ForceBegin = mForcePolyNodes[6];
    newNode.mMember2 = mMembers[11];
    newNode.mMember2ForceBegin = mLoadLine[3];
    newNode.mLabel = "7";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = 14;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[8] = newNode;
    newNode.mMember1 = mMembers[14];
    newNode.mMember1ForceBegin = mForcePolyNodes[7];
    newNode.mMember2 = mMembers[15];
    newNode.mMember2ForceBegin = mLoadLine[4];
    newNode.mLabel = "8";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = 14;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[9] = newNode;
    newNode.mMember1 = mMembers[16];
    newNode.mMember1ForceBegin = mForcePolyNodes[8];
    newNode.mMember2 = mMembers[17];
    newNode.mMember2ForceBegin = mLoadLine[6];
    newNode.mLabel = "9";
    newNode.mLabelXOff = 0;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[10] = newNode;
    newNode.mMember1 = mMembers[18];
    newNode.mMember1ForceBegin = mForcePolyNodes[9];
    newNode.mMember2 = mMembers[20];
    newNode.mMember2ForceBegin = mLoadLine[6];
    newNode.mLabel = "10";
    newNode.mLabelXOff = 0;
    newNode.mLabelYOff = 14;
    newNode.dragAlso(mLoadLine[0]);

    for (int i = 0; i < 7; i++) {
      addToUpdateList(mLoadLine[i]);
    }
    for (int i = 1; i < 11; i++) {
      addToUpdateList(mForcePolyNodes[i]);
    }

// Force poly lines

    TLineForcePoly newLine;

    for (int i = 0; i < 21; i++) {
      mForcePolyLines[i] = new TLineForcePoly();
      mForcePolyLines[i].dragAlso(mLoadLine[0]);
      mMembers[i].mForcePolyMember = mForcePolyLines[i];
    }

    // 1-2
    newLine = mForcePolyLines[2];
    newLine.mStartPoint = mForcePolyNodes[1];
    newLine.mEndPoint = mForcePolyNodes[2];
    newLine.mMemberStart = mTrussNodes[2];
    newLine.mMemberEnd = mTrussNodes[1];
    addToDrawList(newLine);

    // 2-3
    newLine = mForcePolyLines[4];
    newLine.mStartPoint = mForcePolyNodes[2];
    newLine.mEndPoint = mForcePolyNodes[3];
    newLine.mMemberStart = mTrussNodes[4];
    newLine.mMemberEnd = mTrussNodes[1];
    addToDrawList(newLine);

    // 3-4
    newLine = mForcePolyLines[6];
    newLine.mStartPoint = mForcePolyNodes[3];
    newLine.mEndPoint = mForcePolyNodes[4];
    newLine.mMemberStart = mTrussNodes[4];
    newLine.mMemberEnd = mTrussNodes[3];
    addToDrawList(newLine);

    // 4-5
    newLine = mForcePolyLines[8];
    newLine.mStartPoint = mForcePolyNodes[4];
    newLine.mEndPoint = mForcePolyNodes[5];
    newLine.mMemberStart = mTrussNodes[4];
    newLine.mMemberEnd = mTrussNodes[5];
    addToDrawList(newLine);

    // 5-6
    newLine = mForcePolyLines[10];
    newLine.mStartPoint = mForcePolyNodes[5];
    newLine.mEndPoint = mForcePolyNodes[6];
    newLine.mMemberStart = mTrussNodes[6];
    newLine.mMemberEnd = mTrussNodes[5];
    addToDrawList(newLine);

    // 6-7
    newLine = mForcePolyLines[12];
    newLine.mStartPoint = mForcePolyNodes[6];
    newLine.mEndPoint = mForcePolyNodes[7];
    newLine.mMemberStart = mTrussNodes[8];
    newLine.mMemberEnd = mTrussNodes[5];
    addToDrawList(newLine);

    // 7-8
    newLine = mForcePolyLines[14];
    newLine.mStartPoint = mForcePolyNodes[7];
    newLine.mEndPoint = mForcePolyNodes[8];
    newLine.mMemberStart = mTrussNodes[8];
    newLine.mMemberEnd = mTrussNodes[7];
    addToDrawList(newLine);

    // 8-9
    newLine = mForcePolyLines[16];
    newLine.mStartPoint = mForcePolyNodes[8];
    newLine.mEndPoint = mForcePolyNodes[9];
    newLine.mMemberStart = mTrussNodes[8];
    newLine.mMemberEnd = mTrussNodes[9];
    addToDrawList(newLine);

    // 9-10
    newLine = mForcePolyLines[18];
    newLine.mStartPoint = mForcePolyNodes[9];
    newLine.mEndPoint = mForcePolyNodes[10];
    newLine.mMemberStart = mTrussNodes[10];
    newLine.mMemberEnd = mTrussNodes[9];
    addToDrawList(newLine);

 //--
    // a1
    newLine = mForcePolyLines[0];
    newLine.mStartPoint = mLoadLine[0];
    newLine.mEndPoint = mForcePolyNodes[1];
    newLine.mMemberStart = mTrussNodes[0];
    newLine.mMemberEnd = mTrussNodes[1];
    addToDrawList(newLine);

    // b3
    newLine = mForcePolyLines[3];
    newLine.mStartPoint = mLoadLine[1];
    newLine.mEndPoint = mForcePolyNodes[3];
    newLine.mMemberStart = mTrussNodes[1];
    newLine.mMemberEnd = mTrussNodes[3];
    addToDrawList(newLine);

    // c4
    newLine = mForcePolyLines[7];
    newLine.mStartPoint = mLoadLine[2];
    newLine.mEndPoint = mForcePolyNodes[4];
    newLine.mMemberStart = mTrussNodes[3];
    newLine.mMemberEnd = mTrussNodes[5];
    addToDrawList(newLine);

    // d7
    newLine = mForcePolyLines[11];
    newLine.mStartPoint = mLoadLine[3];
    newLine.mEndPoint = mForcePolyNodes[7];
    newLine.mMemberStart = mTrussNodes[5];
    newLine.mMemberEnd = mTrussNodes[7];
    addToDrawList(newLine);

    // e8
    newLine = mForcePolyLines[15];
    newLine.mStartPoint = mLoadLine[4];
    newLine.mEndPoint = mForcePolyNodes[8];
    newLine.mMemberStart = mTrussNodes[7];
    newLine.mMemberEnd = mTrussNodes[9];
    addToDrawList(newLine);

    // f10
    newLine = mForcePolyLines[19];
    newLine.mStartPoint = mLoadLine[5];
    newLine.mEndPoint = mForcePolyNodes[10];
    newLine.mMemberStart = mTrussNodes[9];
    newLine.mMemberEnd = mTrussNodes[11];
    addToDrawList(newLine);

    // g1
    newLine = mForcePolyLines[1];
    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[1];
    newLine.mMemberStart = mTrussNodes[2];
    newLine.mMemberEnd = mTrussNodes[0];
    addToDrawList(newLine);

    // g2
    newLine = mForcePolyLines[5];
    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[2];
    newLine.mMemberStart = mTrussNodes[4];
    newLine.mMemberEnd = mTrussNodes[2];
    addToDrawList(newLine);

    // g5
    newLine = mForcePolyLines[9];
    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[5];
    newLine.mMemberStart = mTrussNodes[6];
    newLine.mMemberEnd = mTrussNodes[4];
    addToDrawList(newLine);

    // g6
    newLine = mForcePolyLines[13];
    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[6];
    newLine.mMemberStart = mTrussNodes[8];
    newLine.mMemberEnd = mTrussNodes[6];
    addToDrawList(newLine);

    // g9
    newLine = mForcePolyLines[17];
    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[9];
    newLine.mMemberStart = mTrussNodes[10];
    newLine.mMemberEnd = mTrussNodes[8];
    addToDrawList(newLine);

    // g10
    newLine = mForcePolyLines[20];
    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[10];
    newLine.mMemberStart = mTrussNodes[11];
    newLine.mMemberEnd = mTrussNodes[10];
    addToDrawList(newLine);

    for (int i = 0; i < 7; i++) {
      addToDrawListOnly(mLoadLine[i]);
    }
    for (int i = 1; i < 11; i++) {
      addToDrawListOnly(mForcePolyNodes[i]);
    }
  }

  private void makeTriangleLabels() {
    TTextTriangle newLabel;

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[0];
    newLabel.p2 = mTrussNodes[1];
    newLabel.p3 = mTrussNodes[2];
    newLabel.mText = "1";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[4];
    newLabel.p2 = mTrussNodes[1];
    newLabel.p3 = mTrussNodes[2];
    newLabel.mText = "2";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[3];
    newLabel.p2 = mTrussNodes[1];
    newLabel.p3 = mTrussNodes[4];
    newLabel.mText = "3";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[3];
    newLabel.p2 = mTrussNodes[5];
    newLabel.p3 = mTrussNodes[4];
    newLabel.mText = "4";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[4];
    newLabel.p2 = mTrussNodes[5];
    newLabel.p3 = mTrussNodes[6];
    newLabel.mText = "5";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[5];
    newLabel.p2 = mTrussNodes[6];
    newLabel.p3 = mTrussNodes[8];
    newLabel.mText = "6";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[5];
    newLabel.p2 = mTrussNodes[7];
    newLabel.p3 = mTrussNodes[8];
    newLabel.mText = "7";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[7];
    newLabel.p2 = mTrussNodes[8];
    newLabel.p3 = mTrussNodes[9];
    newLabel.mText = "8";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[8];
    newLabel.p2 = mTrussNodes[9];
    newLabel.p3 = mTrussNodes[10];
    newLabel.mText = "9";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[9];
    newLabel.p2 = mTrussNodes[10];
    newLabel.p3 = mTrussNodes[11];
    newLabel.mText = "10";
    addToDrawList(newLabel);
  }

  private void makeSupports() {
    TPin pin = new TPin(mTrussNodes[0]);
    addToDrawList(pin);
    TRoller roller = new TRoller(mTrussNodes[11]);
    addToDrawList(roller);
  }


  private void saveLowest() {
    mFairLowest = mFairness;

    for (int i = 0; i < 12; i++) {
      if (mLowestTrussNodes[i] == null)
        mLowestTrussNodes[i] = new TPoint();
      mLowestTrussNodes[i].x = mTrussNodes[i].x;
      mLowestTrussNodes[i].y = mTrussNodes[i].y;
    }

    for (int i = 0; i < 5; i++) {
      if (mLowestForceTails[i] == null)
        mLowestForceTails[i] = new TPoint();
      mLowestForceTails[i].x = mForceTails[i].x;
      mLowestForceTails[i].y = mForceTails[i].y;
    }
  }



  public static final int GRAPH_X_START = 300;
  public static final int GRAPH_Y_START = 640;
  public static final int GRAPH_WIDTH = 210;
  public static final int GRAPH_Y_DIVISOR = 10;

  public static final int YMIN = -500;

  private void makeBarGraph() {
    TPoint bl = new TPoint(GRAPH_X_START, GRAPH_Y_START);
    TPoint br = new TPoint(GRAPH_X_START + GRAPH_WIDTH, GRAPH_Y_START);

    TPoint tl = new TPoint() {
      public void update() {
        x = GRAPH_X_START;
        y = Math.max(YMIN, GRAPH_Y_START - (mTotalWeight / GRAPH_Y_DIVISOR));
      }
    };
    TPoint tr = new TPoint() {
      public void update() {
        x = GRAPH_X_START + GRAPH_WIDTH;
        y = Math.max(YMIN, GRAPH_Y_START - (mTotalWeight / GRAPH_Y_DIVISOR));
      }
    };
    addToUpdateList(tl);
    addToUpdateList(tr);

    TPoint llowest = new TPoint() {
      public void update() {
        x = GRAPH_X_START - 150;

        if (mTotalWeight > 0.0f && mTotalWeight < mLowest) {
          if (g.mTimer == null || g.mTimer.numJobs() == 0) {                    // Do not record lowest during auto-movement
            mLowest = mTotalWeight;
            saveLowest();
          }
        }
        y = GRAPH_Y_START - (mLowest / GRAPH_Y_DIVISOR);
      }
    };
    addToUpdateList(llowest);

    TPoint rlowest = new TPoint() {
      public void update() {
        x = GRAPH_X_START + GRAPH_WIDTH;
        y = GRAPH_Y_START - (mLowest / GRAPH_Y_DIVISOR);
      }
    };
    addToUpdateList(rlowest);

/*    TPoint lhighest = new TPoint() {
      public void update() {
        x = GRAPH_X_START - 10;

        if (mTotalWeight > mHighest) {
          mHighest = mTotalWeight;
        }
        y = GRAPH_Y_START - (mHighest / GRAPH_Y_DIVISOR);
      }
    };
    addToUpdateList(lhighest);

    TPoint rhighest = new TPoint() {
      public void update() {
        x = GRAPH_X_START + GRAPH_WIDTH;
        y = GRAPH_Y_START - (mHighest / GRAPH_Y_DIVISOR);
      }
    };
    addToUpdateList(rhighest);*/

    mGraphPoly.addPoint(br);
    mGraphPoly.addPoint(tr);
    mGraphPoly.addPoint(tl);
    mGraphPoly.addPoint(bl);
    mGraphPoly.mColor = g.mYellow;

    addToDrawList(mGraphPoly);


    TLine outline = new TLine();
    outline.mColor = Color.black;
    outline.mSize = 2;
    outline.mStartPoint = bl;
    outline.mEndPoint = tl;
    addToDrawList(outline);

    outline = new TLine();
    outline.mColor = Color.black;
    outline.mSize = 2;
    outline.mStartPoint = bl;
    outline.mEndPoint = br;
    addToDrawList(outline);

    outline = new TLine();
    outline.mColor = Color.black;
    outline.mSize = 2;
    outline.mStartPoint = tl;
    outline.mEndPoint = tr;
    addToDrawList(outline);

    outline = new TLine();
    outline.mColor = Color.black;
    outline.mSize = 2;
    outline.mStartPoint = tr;
    outline.mEndPoint = br;
    addToDrawList(outline);



/*    outline = new TLine();
    outline.mColor = g.mRed;
    outline.mSize = 2;
    outline.mStartPoint = lhighest;
    outline.mEndPoint = rhighest;
    addToDrawList(outline);*/

    outline = new TLine();
    outline.mColor = g.mGreen;
    outline.mSize = 2;
    outline.mStartPoint = llowest;
    outline.mEndPoint = rlowest;
    addToDrawList(outline);

/*    TText highestText = new TText() {
      public void update() {
        mText = "Highest: " + Util.round(mHighest, 1) + G.LENGTH_UNIT;
        mText.mExponent = "3";
        x = GRAPH_X_START + 5;
        y = GRAPH_Y_START - (mHighest / GRAPH_Y_DIVISOR) + 12;
        super.update();
      }
    };
    highestText.mColor = Color.darkGray;
    highestText.mSize = 10;
    addToDrawList(highestText);*/

    TText lowestText = new TText() {
      public void update() {
        mText = "Lowest: " + Util.round(mLowest, 1) + G.LENGTH_UNIT;
        x = GRAPH_X_START - 150;
        y = GRAPH_Y_START - (mLowest / GRAPH_Y_DIVISOR) + 12;
        if (mFairLowest) {
          mColor = Color.darkGray;
        }
        else {
          mColor = g.mRed;
        }
        super.update();
      }
    };
    lowestText.mExponent = "3";
    //lowestText.mColor = Color.darkGray;
    lowestText.mSize = 10;
    addToDrawList(lowestText);

    mClearMinButton = new TButton("Clear") {
      public void update() {
        mClearMinButton.y = GRAPH_Y_START - (mLowest / GRAPH_Y_DIVISOR) + 32;
        super.update();
      }
    };
    mClearMinButton.mColor = Color.darkGray;
    mClearMinButton.x = GRAPH_X_START - 150;
    mClearMinButton.mWidth = 50;
    mClearMinButton.mHeight = 14;
    mClearMinButton.mPosRelativeTo = GraphicEntity.GLOBAL_RELATIVE;
    addToDrawList(mClearMinButton);
    mClearMinButton.mAction = new TAction() {
      public void run() {
        mLowest = mTotalWeight;
        mFairLowest = mFairness;
      }
    };

    mLowestButton = new TButton("Return to best form found") {
      public void update() {
        mLowestButton.y = GRAPH_Y_START - (mLowest / GRAPH_Y_DIVISOR) + 15;
        super.update();
      }
    };
    mLowestButton.mColor = Color.darkGray;
    mLowestButton.x = GRAPH_X_START - 150;
    mLowestButton.mWidth = 140;
    mLowestButton.mHeight = 14;
    mLowestButton.mPosRelativeTo = GraphicEntity.GLOBAL_RELATIVE;
    addToDrawList(mLowestButton);
    mLowestButton.mAction = new TAction() {
      public void run() {
        JobMovePointTo newJob;
        mVertMirrorButton.mSelected = false;
        mVertMirror = false;
        mHorizMirrorButton.mSelected = false;
        mHorizMirror = false;
        loadsVertCheck.mSelected = false;
        mLoadsVertical = false;
        vertsVertCheck.mSelected = false;
        mVerticalsVertical = false;
        topLevelCheck.mSelected = false;
        mTopLevel = false;
        bottomLevelCheck.mSelected = false;
        mBottomLevel = false;

        if (mFairness) {
          if (!mFairLowest) {
            mFairnessCheck.mSelected = true;
            mFairness = false;
          }
        }

        for (int i = 0; i < 12; i++) {
          newJob = new JobMovePointTo(g);
          newJob.xDst = mLowestTrussNodes[i].x;
          newJob.yDst = mLowestTrussNodes[i].y;
          newJob.mMovePoint = mTrussNodes[i];
          g.mTimer.addJob(newJob);
        }
        for (int i = 0; i < 5; i++) {
          newJob = new JobMovePointTo(g);
          newJob.xDst = mLowestForceTails[i].x;
          newJob.yDst = mLowestForceTails[i].y;
          newJob.mMovePoint = mForceTails[i];
          g.mTimer.addJob(newJob);
        }
      }
    };


    TText totalText = new TText() {
      public void update() {
        mText = "Total: " + Util.round(mTotalWeight, 1) + G.LENGTH_UNIT;
        y = Math.max(YMIN, GRAPH_Y_START - (mTotalWeight / GRAPH_Y_DIVISOR)) + 24;
        super.update();
      }
    };
    totalText.mExponent = "3";
    totalText.mColor = Color.darkGray;
    totalText.x = GRAPH_X_START + 10;
    //totalText.y = GRAPH_Y_START - 30;
    totalText.mSize = 18;
    addToDrawList(totalText);

    mHowLow = new TText();
    mHowLow.mSize = 18;
    mHowLow.mText = "How low can you go?";
    mHowLow.mColor = Color.darkGray;
    mHowLow.x = GRAPH_X_START + 10;
    mHowLow.y = GRAPH_Y_START - 10;
    addToDrawList(mHowLow);
  }









  public static final int REPORT_X_START = 600;
  public static final int REPORT_Y_START = 60;
  public static final int REPORT_LINE_SPACE = 17;
  public static final int REPORT_COLUMN_SPACE = 100;
  public static final int REPORT_LENGTH_COLUMN_SPACE = 65;

  private void makeReport() {
    int x = REPORT_X_START;
    int y = REPORT_Y_START;

    TText newText = new TText();
    newText.mSize = 18;
    newText.mText = "Top Chord";
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
    newReport.mForcePolyLine = mForcePolyLines[3];
    newReport.mPrefix = "B3 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[7];
    newReport.mPrefix = "C4 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[11];
    newReport.mPrefix = "D7 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[15];
    newReport.mPrefix = "E8 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[19];
    newReport.mPrefix = "F10 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);

 //--- top chord lengths
    TTextPointLength lengthReport;

    x += REPORT_COLUMN_SPACE;
    y = (int)(REPORT_Y_START + REPORT_LINE_SPACE * 1.2);

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[0];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[3];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[7];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[11];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[15];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[19];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;



 //--- top chord weights
    TText weightReport;

    x += REPORT_LENGTH_COLUMN_SPACE - 20;
    y = REPORT_Y_START;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round((mMembers[0].length() *  mForcePolyLines[0].length() +
                                 mMembers[3].length() *  mForcePolyLines[3].length() +
                                 mMembers[7].length() *  mForcePolyLines[7].length() +
                                 mMembers[11].length() *  mForcePolyLines[11].length() +
                                 mMembers[15].length() *  mForcePolyLines[15].length() +
                                 mMembers[19].length() *  mForcePolyLines[19].length()) /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.mSize = 18;
    weightReport.mText = "Top Chord";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE * 1.2;
    x += 20;


    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[0].length() *  mForcePolyLines[0].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[3].length() *  mForcePolyLines[3].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[7].length() *  mForcePolyLines[7].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[11].length() *  mForcePolyLines[11].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[15].length() *  mForcePolyLines[15].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[19].length() *  mForcePolyLines[19].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;


//--- bottom chord
    int bottomChordY;

    x = REPORT_X_START;
    //y = REPORT_Y_START;
    y += REPORT_LINE_SPACE * 1.2;
    bottomChordY = y;

    newText = new TText();
    newText.mSize = 18;
    newText.mText = "Bottom Chord";
    newText.x = x;
    newText.y = y;
    addToDrawList(newText);
    y += REPORT_LINE_SPACE * 1.2;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[1];
    newReport.mPrefix = "G1 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[5];
    newReport.mPrefix = "G2 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[9];
    newReport.mPrefix = "G5 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[13];
    newReport.mPrefix = "G6 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[17];
    newReport.mPrefix = "G9 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[20];
    newReport.mPrefix = "G10 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);

 //--- bottom chord lengths
    x += REPORT_COLUMN_SPACE;
    y = bottomChordY + (int)(REPORT_LINE_SPACE * 1.2); //(int)(REPORT_Y_START + REPORT_LINE_SPACE * 1.2);

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[1];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[5];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[9];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[13];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[17];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[20];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;



 //--- bottom chord weights

    x += REPORT_LENGTH_COLUMN_SPACE - 20;
    //y = REPORT_Y_START;
    y = bottomChordY;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round((mMembers[1].length() *  mForcePolyLines[1].length() +
                                 mMembers[5].length() *  mForcePolyLines[5].length() +
                                 mMembers[9].length() *  mForcePolyLines[9].length() +
                                 mMembers[13].length() *  mForcePolyLines[13].length() +
                                 mMembers[17].length() *  mForcePolyLines[17].length() +
                                 mMembers[20].length() *  mForcePolyLines[20].length()) /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.mSize = 18;
    weightReport.mText = "Top Chord";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE * 1.2;
    x += 20;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[1].length() *  mForcePolyLines[1].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[5].length() *  mForcePolyLines[5].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[9].length() *  mForcePolyLines[9].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[13].length() *  mForcePolyLines[13].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[17].length() *  mForcePolyLines[17].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[20].length() *  mForcePolyLines[20].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;



//-------- VERICALS
    x = REPORT_X_START;
    //y = REPORT_Y_START;
    y += REPORT_LINE_SPACE * 1.2;
    int verticalsY = y;

    newText = new TText();
    newText.mSize = 18;
    newText.mText = "Verticals";
    newText.x = x;
    newText.y = y;
    addToDrawList(newText);
    y += REPORT_LINE_SPACE * 1.2;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[2];
    newReport.mPrefix = "1-2 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[6];
    newReport.mPrefix = "3-4 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[10];
    newReport.mPrefix = "5-6 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[14];
    newReport.mPrefix = "7-8 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[18];
    newReport.mPrefix = "9-10 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);


//--- verticals lengths
    x += REPORT_COLUMN_SPACE;
    y = verticalsY + (int)(REPORT_LINE_SPACE * 1.2);//(int)(REPORT_Y_START + REPORT_LINE_SPACE * 1.2);

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[2];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[6];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[10];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[14];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[18];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;


 //--- verticals weights

    x += REPORT_LENGTH_COLUMN_SPACE - 20;
    y = verticalsY;//REPORT_Y_START;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round((mMembers[2].length() *  mForcePolyLines[2].length() +
                                 mMembers[6].length() *  mForcePolyLines[6].length() +
                                 mMembers[10].length() *  mForcePolyLines[10].length() +
                                 mMembers[14].length() *  mForcePolyLines[14].length() +
                                 mMembers[18].length() *  mForcePolyLines[18].length()) /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.mSize = 18;
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE * 1.2;
    x += 20;


    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[2].length() *  mForcePolyLines[2].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[6].length() *  mForcePolyLines[6].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[10].length() *  mForcePolyLines[10].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[14].length() *  mForcePolyLines[14].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[18].length() *  mForcePolyLines[18].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;


//-------- Diagonals
    x = REPORT_X_START;
    //y = REPORT_Y_START;
    y += REPORT_LINE_SPACE * 1.2;
    int diagonalsY = y;

    newText = new TText();
    newText.mSize = 18;
    newText.mText = "Diagonals";
    newText.x = x;
    newText.y = y;
    addToDrawList(newText);
    y += REPORT_LINE_SPACE * 1.2;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[4];
    newReport.mPrefix = "2-3 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[8];
    newReport.mPrefix = "4-5 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[12];
    newReport.mPrefix = "6-7 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[16];
    newReport.mPrefix = "8-9 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);



//--- diagonals lengths
    x += REPORT_COLUMN_SPACE;
    y = diagonalsY + (int)(REPORT_LINE_SPACE * 1.2);//(int)(REPORT_Y_START + REPORT_LINE_SPACE * 1.2);

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[4];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[8];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[12];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;

    lengthReport = new TTextPointLength(g);
    lengthReport.mLine = mMembers[16];
    lengthReport.mPrefix = "";
    lengthReport.mPostfix = G.LENGTH_UNIT;
    lengthReport.mBasePoint = new TPoint(x, y);
    addToDrawList(lengthReport);
    y += REPORT_LINE_SPACE;



 //--- diagonals weights


    x += REPORT_LENGTH_COLUMN_SPACE - 20;
    y = diagonalsY;//REPORT_Y_START;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round((mMembers[4].length() *  mForcePolyLines[4].length() +
                                 mMembers[8].length() *  mForcePolyLines[8].length() +
                                 mMembers[12].length() *  mForcePolyLines[12].length() +
                                 mMembers[16].length() *  mForcePolyLines[16].length()) /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.mSize = 18;
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE * 1.2;
    x += 20;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[4].length() *  mForcePolyLines[4].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[8].length() *  mForcePolyLines[8].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[12].length() *  mForcePolyLines[12].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;

    weightReport = new TText() {
      public void update() {
        mText = "" + Util.round(mMembers[16].length() *  mForcePolyLines[16].length() /
                                (g.mLengthDivisor * g.mLengthDivisor), 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    y += REPORT_LINE_SPACE;


//--- TOTAL WEIGHT
    y += REPORT_LINE_SPACE * 1.2;
    x = REPORT_X_START;

    weightReport = new TText();
    weightReport.mText = "Total Material:";
    weightReport.mSize = 18;
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);
    x += REPORT_COLUMN_SPACE + REPORT_LENGTH_COLUMN_SPACE - 20;

    weightReport = new TText() {
      public void update() {
        mTotalWeight = 0.0f;
        for (int i = 0; i < 21; i++) {
          mTotalWeight += mMembers[i].length() *  mForcePolyLines[i].length();
        }
        mTotalWeight /= (g.mLengthDivisor * g.mLengthDivisor);
        mText = "" + Util.round(mTotalWeight, 1) + G.LENGTH_UNIT;
        super.update();
      }
    };
    weightReport.mExponent = "3";
    weightReport.mSize = 18;
    weightReport.x = x;
    weightReport.y = y;
    addToDrawList(weightReport);

    x -= REPORT_COLUMN_SPACE + REPORT_LENGTH_COLUMN_SPACE - 20;
    y += 24;

    TText span = new TText();
    span.mText = "Span:";
    span.mSize = 18;
    span.x = x;
    span.y = y;
    addToDrawList(span);
    x += REPORT_COLUMN_SPACE + REPORT_LENGTH_COLUMN_SPACE - 20;

    span = new TText() {
      public void update() {
        mText = "" + (Math.abs((mTrussNodes[11].x -  mTrussNodes[0].x)) / g.mLengthDivisor) + G.LENGTH_UNIT;
        super.update();
      }
    };
    span.mSize = 18;
    span.x = x;
    span.y = y;
    addToDrawList(span);
  }

  public static final int BUTTON_START_X = 20;
  public static final int BUTTON_START_Y = 70;
  public static final int BUTTON_Y_OFFSET = 28;

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

        JobMovePointToStart newJob;

        newJob = new JobMovePointToStart(g);
        newJob.mMovePoint = mLoadLine[0];
        g.mTimer.addJob(newJob);

        mVertMirrorButton.mSelected = false;
        mVertMirror = false;
        mHorizMirrorButton.mSelected = false;
        mHorizMirror = false;
        for (int i = 0; i < 12; i++) {
          newJob = new JobMovePointToStart(g);
          newJob.mMovePoint = mTrussNodes[i];
          g.mTimer.addJob(newJob);
        }
        for (int i = 0; i < 5; i++) {
          newJob = new JobMovePointToStart(g);
          newJob.mMovePoint = mForceTails[i];
          g.mTimer.addJob(newJob);
        }
      }
    };
    y += BUTTON_Y_OFFSET;

    bottomLevelCheck = new TButton("Keep Bottom Chord Level");
    bottomLevelCheck.x = x;
    bottomLevelCheck.y = y;
    bottomLevelCheck.mWidth = 170;
    bottomLevelCheck.mHeight = 20;
    bottomLevelCheck.mIsToggle = true;
    addToDrawList(bottomLevelCheck);
    bottomLevelCheck.mAction = new TAction() {
      public void run() {
        mBottomLevel = bottomLevelCheck.mSelected;
        if (mBottomLevel) {
          mVertMirrorButton.mSelected = false;
          mVertMirror = false;
        }
        repaint();
      }
    };
    y += BUTTON_Y_OFFSET;

    topLevelCheck = new TButton("Keep Top Chord Level");
    topLevelCheck.x = x;
    topLevelCheck.y = y;
    topLevelCheck.mWidth = 170;
    topLevelCheck.mHeight = 20;
    topLevelCheck.mIsToggle = true;
    addToDrawList(topLevelCheck);
    topLevelCheck.mAction = new TAction() {
      public void run() {
        mTopLevel = topLevelCheck.mSelected;
        repaint();
      }
    };

    y += BUTTON_Y_OFFSET;
    mVertMirrorButton = new TButton("Top-Bottom Mirror");
    mVertMirrorButton.x = x;
    mVertMirrorButton.y = y;
    mVertMirrorButton.mWidth = 170;
    mVertMirrorButton.mHeight = 20;
    mVertMirrorButton.mIsToggle = true;
    addToDrawList(mVertMirrorButton);
    mVertMirrorButton.mAction = new TAction() {
      public void run() {
        mVertMirror = mVertMirrorButton.mSelected;
        if (mVertMirror) {
          bottomLevelCheck.mSelected = false;
          mBottomLevel = false;
        }
        repaint();
      }
    };
    y += BUTTON_Y_OFFSET;

    vertsVertCheck = new TButton("Keep Verticals Vertical");
    vertsVertCheck.x = x;
    vertsVertCheck.y = y;
    vertsVertCheck.mWidth = 170;
    vertsVertCheck.mHeight = 20;
    vertsVertCheck.mIsToggle = true;
    addToDrawList(vertsVertCheck);
    vertsVertCheck.mAction = new TAction() {
      public void run() {
        mVerticalsVertical = vertsVertCheck.mSelected;
        repaint();
      }
    };
    y += BUTTON_Y_OFFSET;

    mHorizMirrorButton = new TButton("Left-Right Mirror");
    mHorizMirrorButton.x = x;
    mHorizMirrorButton.y = y;
    mHorizMirrorButton.mWidth = 170;
    mHorizMirrorButton.mHeight = 20;
    mHorizMirrorButton.mIsToggle = true;
    addToDrawList(mHorizMirrorButton);
    mHorizMirrorButton.mAction = new TAction() {
      public void run() {
        mHorizMirror = mHorizMirrorButton.mSelected;
        repaint();
      }
    };
    y += BUTTON_Y_OFFSET;

    mFairnessCheck = new TButton("Allow cheating");
    mFairnessCheck.x = x;
    mFairnessCheck.y = y;
    mFairnessCheck.mWidth = 170;
    mFairnessCheck.mHeight = 20;
    mFairnessCheck.mIsToggle = true;
    addToDrawList(mFairnessCheck);
    mFairnessCheck.mAction = new TAction() {
      public void run() {
        mFairness = !mFairnessCheck.mSelected;
        repaint();
      }
    };
    mFairness = true;
    y += BUTTON_Y_OFFSET;

    equalizeButton = new TButton("Equalize Loads");
    equalizeButton.x = x;
    equalizeButton.y = y;
    equalizeButton.mWidth = 170;
    equalizeButton.mHeight = 20;
    equalizeButton.mAction = new TAction() {
      public void run() {
        JobMovePointToStart newJob;

        float length = mLoads[0].length();
        float dir;
        JobMovePointTo moveToJob;

        for (int i = 1; i < 5; i++) {
          dir = mLoads[i].direction();
          moveToJob = new JobMovePointTo(g);
          moveToJob.xDst = mLoads[i].mArrowHead.x - length * (float)Math.cos(dir);
          moveToJob.yDst = mLoads[i].mArrowHead.y - length * (float)Math.sin(dir);
          moveToJob.mMovePoint = mForceTails[i];
          g.mTimer.addJob(moveToJob);
        }
      }
    };
    addToDrawList(equalizeButton);
    y += BUTTON_Y_OFFSET;

    loadsVertCheck = new TButton("Keep Loads Vertical");
    loadsVertCheck.x = x;
    loadsVertCheck.y = y;
    loadsVertCheck.mWidth = 170;
    loadsVertCheck.mHeight = 20;
    loadsVertCheck.mIsToggle = true;
    addToDrawList(loadsVertCheck);
    loadsVertCheck.mAction = new TAction() {
      public void run() {
        mLoadsVertical = loadsVertCheck.mSelected;
        repaint();
      }
    };
    y += BUTTON_Y_OFFSET;

  }
}