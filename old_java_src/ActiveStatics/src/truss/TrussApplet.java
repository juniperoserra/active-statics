package truss;

import java.applet.*;

import java.awt.*;
import java.awt.event.*;

public class TrussApplet extends Applet {
  public static final int APPLET_WIDTH = 700;
  public static final int APPLET_HEIGHT = 740;

  public static final int PANEL_SIZE = 80;
  public static final int TRUSS_X_START = 230;
  public static final int TRUSS_Y_START = 460;
  public final int START_FORCE_LENGTH = 60;
  public static final int LOAD_LINE_START_X = 600;
  public static final int LOAD_LINE_START_Y = 60;

  public GraphicEntity[] drawList = new GraphicEntity[0];
  public GraphicEntity[] updateList = new GraphicEntity[0];
  private Image mOffscreen;
  private Graphics mGOff;
  Dimension mOffSize;
  public int W;
  public int H;

  private Point tempPoint = new Point();
  private int paintedTwice = 0;

  private NoScrollUpdateCanvas mUpdateCanvas;

  TButton loadsVertCheck;
  TButton vertsVertCheck;
  TButton topLevelCheck;
  TButton bottomLevelCheck;
  TButton mVertMirrorButton;
  TButton mHorizMirrorButton;
  TButton mPreservePanelSpacing;
  TButton mGable;
  private TButton mLinesOfActionCheck;

  private float forceDx[] = new float[5];
  boolean preservePanelSpacing = false;



  public TPoint[] mTrussNodes = new TPoint[12];
  public TPoint[] mGableNodes = new TPoint[12];
  public TPoint[] mForceTails = new TPoint[5];
  public TArrow[] mLoads = new TArrow[5];
  public TLineMember[] mMembers = new TLineMember[21];
  public TPoint mRbTail;
  public TReaction mRb;
  public TPoint mRaTail;
  public TArrow mRa;
  public TPoint[] mLoadLine = new TPoint[7];
  public TPointForcePoly mForcePolyNodes[] = new TPointForcePoly[11];
  public TLineForcePoly mForcePolyLines[] = new TLineForcePoly[21];

  public TPoint[] mDummyPoint = new TPoint[6];
  public TLine mRaLineOfAction;
  public TLine mRbLineOfAction;
  public TLine mLoadLineOfAction;
  public TPoint mResultantEndNode;
  public TPoint mResultantStartNode;

  public TPointIntersect mActionIntersect;

  public boolean mLoadsVertical = false;
  public boolean mVerticalsVertical = false;
  public boolean mBottomLevel = false;
  public boolean mTopLevel = false;
  public boolean mVertMirror = false;
  public boolean mHorizMirror = false;
  public boolean mLinesOfAction = false;

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

    makeButtons();

    makeGableNodes();
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
    makeLinesOfAction();

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
      public void globalUpdate() {    // Apply contstraints
        if (mLinesOfAction) {
          mRaLineOfAction.mInvisible = false;
          mRbLineOfAction.mInvisible = false;
          mLoadLineOfAction.mInvisible = false;
          mActionIntersect.mInvisible = false;

          //mResultantNode.x = mActionIntersect.x + (mLoadLine[5].x - mLoadLine[0].x);
          //mResultantNode.y = mActionIntersect.y + (mLoadLine[5].y - mLoadLine[0].y);

          mResultantStartNode.x = 0;
          mResultantStartNode.y = 0;
          mResultantEndNode.x = 0;
          mResultantEndNode.y = 0;
          for (int i = 0; i < 5; i++) {
            mResultantStartNode.x += mLoads[i].mStartPoint.x;
            mResultantStartNode.y += mLoads[i].mStartPoint.y;
            mResultantEndNode.x += mLoads[i].mEndPoint.x;
            mResultantEndNode.y += mLoads[i].mEndPoint.y;
          }
          mResultantStartNode.x /= 5;
          mResultantStartNode.y /= 5;
          mResultantEndNode.x /= 5;
          mResultantEndNode.y /= 5;

        }
        else {
          mRaLineOfAction.mInvisible = true;
          mRbLineOfAction.mInvisible = true;
          mLoadLineOfAction.mInvisible = true;
          mActionIntersect.mInvisible = true;
        }

        if (!mActionIntersect.mExists) {
          mRaLineOfAction.mStartPoint = mDummyPoint[0];
          mRaLineOfAction.mStartPoint.x = mRa.mEndPoint.x;
          mRaLineOfAction.mStartPoint.y = -5000;

          mRaLineOfAction.mEndPoint = mDummyPoint[1];
          mRaLineOfAction.mEndPoint.x = mRa.mEndPoint.x;
          mRaLineOfAction.mEndPoint.y = 5000;


          mRbLineOfAction.mStartPoint = mDummyPoint[2];
          mRbLineOfAction.mStartPoint.x = mRb.mEndPoint.x;
          mRbLineOfAction.mStartPoint.y = -5000;

          mRbLineOfAction.mEndPoint = mDummyPoint[3];
          mRbLineOfAction.mEndPoint.x = mRb.mEndPoint.x;
          mRbLineOfAction.mEndPoint.y = 5000;

          mLoadLineOfAction.mStartPoint = mDummyPoint[4];
          mLoadLineOfAction.mStartPoint.x = mTrussNodes[1].x;
          mLoadLineOfAction.mStartPoint.y = -5000;

          mLoadLineOfAction.mEndPoint = mDummyPoint[5];
          mLoadLineOfAction.mEndPoint.x = mTrussNodes[1].x;
          mLoadLineOfAction.mEndPoint.y = 5000;

          mActionIntersect.x = 5000;
          mActionIntersect.y = 5000;
        }
        else {
          mRaLineOfAction.mStartPoint = mRa.mEndPoint;
          mRaLineOfAction.mEndPoint = mActionIntersect;
          mRbLineOfAction.mStartPoint = mRb.mEndPoint;
          mRbLineOfAction.mEndPoint = mActionIntersect;
          mLoadLineOfAction.mStartPoint = mResultantStartNode;
          mLoadLineOfAction.mEndPoint = mResultantEndNode;
        }

        if (preservePanelSpacing) {
          for (int i = 1; i < 10; i += 2) {
            mTrussNodes[i].x = mTrussNodes[0].x + ((i+1) / 2) * PANEL_SIZE;
            if (this.g.selectedEntity == mForceTails[i/2])
              forceDx[i/2] = mForceTails[i/2].x - mTrussNodes[i].x;
            else
              mForceTails[i/2].x = mTrussNodes[i].x + forceDx[i/2];
          }
        }

        for (int i = 0; i < 12; i++) {
          mTrussNodes[i].mControlPoint = true;
          mTrussNodes[i].mSelectable = true;
          mTrussNodes[i].mSize = TPoint.DEFAULT_SIZE;
        }

        if (mLoadsVertical) {
          for (int i = 0; i < 5; i++) {
            mForceTails[i].x = mLoads[i].mEndPoint.x;
          }
        }

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
    title.mText = "Simple Truss";
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
    formDiag.mBasePoint = mTrussNodes[1];
    formDiag.mXOffset = -204;
    formDiag.mYOffset = 10;
    formDiag.mSize = 20;
    formDiag.mText = "Form Diagram";
    addToDrawList(formDiag);
  }

  private void makeGableNodes() {
    int x = TRUSS_X_START - PANEL_SIZE;
    for (int i = 0; i < 12; i++) {
      if (i % 2 == 0) {
        mGableNodes[i] = new TPoint(x, TRUSS_Y_START);
        x += PANEL_SIZE;
      }
      else {
        mGableNodes[i] = new TPoint(x, TRUSS_Y_START - (PANEL_SIZE * (3 - Math.abs((i+1)/2 - 3))));
      }
    }
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

  private void makeLinesOfAction() {
    for (int i = 0; i < 6; i++) {
      mDummyPoint[i] = new TPoint();
    }
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

    mLoadLineOfAction = new TLine();
    mLoadLineOfAction.mStartPoint = mResultantStartNode;
    mLoadLineOfAction.mEndPoint = mResultantEndNode;
    mLoadLineOfAction.mSize = 2;
    mLoadLineOfAction.mDashed = true;
    mLoadLineOfAction.mConsiderExtents = false;
    mLoadLineOfAction.mColor = Color.darkGray;
    addToDrawList(mLoadLineOfAction);
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

  public static final int REPORT_X_START = 195;
  public static final int REPORT_Y_START = 560;
  public static final int REPORT_LINE_SPACE = 17;
  public static final int REPORT_COLUMN_SPACE = 110;

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

//--- bottom chord
    x += REPORT_COLUMN_SPACE;
    y = REPORT_Y_START;

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

//-------- VERICALS
    x += REPORT_COLUMN_SPACE + 15;
    y = REPORT_Y_START;

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

//-------- Diagonals
    x += REPORT_COLUMN_SPACE - 15;
    y = REPORT_Y_START;

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

        JobMovePointToStart newJob;
        mVertMirrorButton.mSelected = false;
        mVertMirror = false;
        mPreservePanelSpacing.mSelected = false;
        preservePanelSpacing = false;

        newJob = new JobMovePointToStart(g);
        newJob.mMovePoint = mLoadLine[0];
        g.mTimer.addJob(newJob);

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
    mGable = new TButton("Gable Form");
    mGable.x = x;
    mGable.y = y;
    mGable.mWidth = 170;
    mGable.mHeight = 20;
    mGable.mIsToggle = false;
    addToDrawList(mGable);
    mGable.mAction = new TAction() {
      public void run() {
        g.mTimer.clearJobs();

        JobMovePointToPoint newJob;
        mVertMirrorButton.mSelected = false;
        mVertMirror = false;
        mPreservePanelSpacing.mSelected = false;
        preservePanelSpacing = false;
        mTopLevel = false;
        topLevelCheck.mSelected = false;

        JobMovePointTo forceMoveJob;
        for (int i = 0; i < 5; i++) {
          forceMoveJob = new JobMovePointTo(g);
          forceMoveJob.mMovePoint = mForceTails[i];
          forceMoveJob.xDst = mGableNodes[(2*i)+1].x + (mForceTails[i].x - mTrussNodes[(2*i)+1].x);
          forceMoveJob.yDst = mGableNodes[(2*i)+1].y + (mForceTails[i].y - mTrussNodes[(2*i)+1].y);
          g.mTimer.addJob(forceMoveJob);
        }

        for (int i = 0; i < 12; i++) {
          newJob = new JobMovePointToPoint(g, mTrussNodes[i], mGableNodes[i]);
          g.mTimer.addJob(newJob);
        }


      }
    };
    y += BUTTON_Y_OFFSET;

    TButton equalizeButton = new TButton("Equalize Loads");
    equalizeButton.x = x;
    equalizeButton.y = y;
    equalizeButton.mWidth = 170;
    equalizeButton.mHeight = 20;
    equalizeButton.mAction = new TAction() {
      public void run() {
        g.mTimer.clearJobs();

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

    mPreservePanelSpacing = new TButton("Keep Uniform Panel Spacing");
    mPreservePanelSpacing.x = x;
    mPreservePanelSpacing.y = y;
    mPreservePanelSpacing.mWidth = 170;
    mPreservePanelSpacing.mHeight = 20;
    mPreservePanelSpacing.mIsToggle = true;
    addToDrawList(mPreservePanelSpacing);
    mPreservePanelSpacing.mAction = new TAction() {
      public void run() {
        preservePanelSpacing = mPreservePanelSpacing.mSelected;
        for (int i = 0; i < 5; i++) {
          forceDx[i] = mForceTails[i].x - mTrussNodes[i * 2 + 1].x;
        }
        repaint();
      }
    };
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

    mLinesOfActionCheck = new TButton("Extend Lines of Action");
    mLinesOfActionCheck.x = x;
    mLinesOfActionCheck.y = y;
    mLinesOfActionCheck.mWidth = 170;
    mLinesOfActionCheck.mHeight = 20;
    mLinesOfActionCheck.mIsToggle = true;
//    addToDrawList(mLinesOfActionCheck);
    mLinesOfActionCheck.mAction = new TAction() {
      public void run() {
        mLinesOfAction = mLinesOfActionCheck.mSelected;
        repaint();
      }
    };
  }
}