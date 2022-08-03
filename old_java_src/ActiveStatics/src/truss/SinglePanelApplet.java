package truss;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;

public class SinglePanelApplet extends Applet {

  public static final int APPLET_WIDTH = 620;
  public static final int APPLET_HEIGHT = 620;

  public static final int PANEL_SIZE = 180;
  public static final int TRUSS_X_START = 150;
  public static final int TRUSS_Y_START = 350;
  public final int START_FORCE_LENGTH = 165;
  public static final int LOAD_LINE_START_X = 480;
  public static final int LOAD_LINE_START_Y = 120;

  public int W;
  public int H;

  private Point tempPoint = new Point();
  private int paintedTwice = 0;
  private TButton mCircleLoadButton;
  private TButton mLoadsVertCheck;
  private TButton mLinesOfActionCheck;

  private NoScrollUpdateCanvas mUpdateCanvas;

  public GraphicEntity[] drawList = new GraphicEntity[0];
  public GraphicEntity[] updateList = new GraphicEntity[0];





  public TPoint[] mTrussNodes = new TPoint[3];
  public TPoint[] mDummyPoint = new TPoint[6];
  public TPoint mForceTail;
  public TArrow mLoad;
  public TLineMember[] mMembers = new TLineMember[3];
  public TPoint mRbTail;
  public TReaction mRb;
  public TPoint mRaTail;
  public TArrow mRa;
  public TPoint[] mLoadLine = new TPoint[3];
  public TPointForcePoly mForcePolyNode;
  public TLineForcePoly mForcePolyLines[] = new TLineForcePoly[3];

  public TLine mRaLineOfAction;
  public TLine mRbLineOfAction;
  public TLine mLoadLineOfAction;
  public TPointIntersect mActionIntersect;

  public boolean mLoadsVertical = false;
  public boolean mVerticalsVertical = false;
  public boolean mLinesOfAction = false;
  public JobCirclePoint mCircleLoad = null;




  public G g;

  public void setSize(int width, int height)
  {

   super.setSize(width,height);

   validate();
  }


  boolean isStandalone = false;
  /**Get a parameter value*/
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }

  private void makeSupports() {
    TPin pin = new TPin(mTrussNodes[0]);
    addToDrawList(pin);
    TRoller roller = new TRoller(mTrussNodes[2]);
    addToDrawList(roller);
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
    g.mLengthDivisor = 1.0f;

    makeButtons();

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
          mLoadLineOfAction.mStartPoint = mTrussNodes[1];
          mLoadLineOfAction.mEndPoint = mActionIntersect;
        }


        if (mLoadsVertical) {
          mForceTail.x = mLoad.mEndPoint.x;
        }

        if (mVerticalsVertical) {
          mTrussNodes[2].x = mTrussNodes[1].x;
          mTrussNodes[4].x = mTrussNodes[3].x;
          mTrussNodes[6].x = mTrussNodes[5].x;
          mTrussNodes[8].x = mTrussNodes[7].x;
          mTrussNodes[10].x = mTrussNodes[9].x;
        }
      }
    };
    mUpdateCanvas.mGlobalUpdateEveryTime = true;
    mUpdateCanvas.drawList = drawList;
    mUpdateCanvas.updateList = updateList;

    add(mUpdateCanvas);

    addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        mUpdateCanvas.appletResized();
      }
    });
  }


  public void repaint() {
    mUpdateCanvas.repaint();
  }


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
    title.mText = "Single Panel Truss";
    title.mSize = 24;
    title.x = 20;
    title.y = 50;
    title.mPosRelativeTo = GraphicEntity.VIEW_RELATIVE;    addToDrawList(title);

    TTextPoint forcePoly = new TTextPoint();
    forcePoly.mBasePoint = mLoadLine[0];
    forcePoly.mXOffset = -100;
    forcePoly.mYOffset = -20;
    forcePoly.mSize = 20;
    forcePoly.mText = "Force Polygon";
    addToDrawList(forcePoly);

    TTextPoint formDiag = new TTextPoint();
    formDiag.mBasePoint = mTrussNodes[1];
    formDiag.mXOffset = -220;
    formDiag.mYOffset = 0;
    formDiag.mSize = 20;
    formDiag.mText = "Form Diagram";
    addToDrawList(formDiag);
  }

  private void makeNodes() {
    int x = TRUSS_X_START;
    float height = PANEL_SIZE / 2.5f; //(float)(Math.sqrt(3.0)/2.0) * PANEL_SIZE;

    mTrussNodes[0] = new TPoint(x, TRUSS_Y_START);
    mTrussNodes[1] = new TPoint(x + PANEL_SIZE / 2.0f, TRUSS_Y_START - height);
    mTrussNodes[2] = new TPoint(x + PANEL_SIZE, TRUSS_Y_START);

    x = TRUSS_X_START + PANEL_SIZE / 2;
    mForceTail = new TPoint(x, TRUSS_Y_START - height - START_FORCE_LENGTH);

    // Add dragging relationships
    mTrussNodes[1].dragAlso(mForceTail);
  }

  private void addNodes() {
    for (int i = 0; i < 3; i++) {
      addToDrawList(mTrussNodes[i]);
    }
    addToDrawList(mForceTail);
  }

  private void makeLinesOfAction() {
    for (int i = 0; i < 6; i++) {
      mDummyPoint[i] = new TPoint();
    }


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
    mLoadLineOfAction.mStartPoint = mTrussNodes[1];
    mLoadLineOfAction.mEndPoint = mActionIntersect;
    mLoadLineOfAction.mSize = 2;
    mLoadLineOfAction.mDashed = true;
    mLoadLineOfAction.mConsiderExtents = false;
    mLoadLineOfAction.mColor = Color.darkGray;
    addToDrawList(mLoadLineOfAction);
  }

  private void makeLoads() {
    mLoad = new TLoad(g);
    addToDrawList(mLoad);
    mLoad.mStartPoint = mForceTail;
    mLoad.mEndPoint = mTrussNodes[1];
  }

  private void makeMembers() {
    for(int i = 0; i < 3; i++) {
      mMembers[i] = new TLineMember(g);
      addToDrawList(mMembers[i]);
    }
    mMembers[0].mStartPoint = mTrussNodes[0];
    mMembers[0].mEndPoint = mTrussNodes[1];
    mMembers[0].mLabelXOff = -14;
    mMembers[0].mLabelYOff = -14;
    mMembers[0].mLabel = "A";
    mMembers[0].dragAlso(mTrussNodes[0]);
    mMembers[0].dragAlso(mTrussNodes[1]);
    mMembers[0].dragAlso(mTrussNodes[2]);

    mMembers[1].mStartPoint = mTrussNodes[1];
    mMembers[1].mEndPoint = mTrussNodes[2];
    mMembers[1].mLabelXOff = 14;
    mMembers[1].mLabelYOff = -14;
    mMembers[1].mLabel = "B";
    mMembers[1].dragAlso(mTrussNodes[0]);
    mMembers[1].dragAlso(mTrussNodes[1]);
    mMembers[1].dragAlso(mTrussNodes[2]);

    mMembers[2].mStartPoint = mTrussNodes[2];
    mMembers[2].mEndPoint = mTrussNodes[0];
    mMembers[2].mLabelXOff = 0;
    mMembers[2].mLabelYOff = 20;
    mMembers[2].mLabel = "C";
    mMembers[2].dragAlso(mTrussNodes[0]);
    mMembers[2].dragAlso(mTrussNodes[1]);
    mMembers[2].dragAlso(mTrussNodes[2]);
  }

  private void makeRb() {
    mRbTail = new TPoint() {
      float moment;
      float pDist;

      public void update() {
        moment = mLoad.moment(mTrussNodes[0]);
        moment *= TLine.CCW(mTrussNodes[2].x, mTrussNodes[2].y - 10,
                            mTrussNodes[2].x, mTrussNodes[2].y,
                            mTrussNodes[0].x, mTrussNodes[0].y);
        pDist = TLine.perpDist(mTrussNodes[2].x, mTrussNodes[2].y,
                               mTrussNodes[2].x, mTrussNodes[2].y + 10,
                               mTrussNodes[0].x, mTrussNodes[0].y);
        if (Math.abs(pDist) < 0.1f)
          pDist = 1.0f;
        moment /= pDist;
        x = mTrussNodes[2].x;
        y = mTrussNodes[2].y + Math.abs(moment) + mRb.ARROW_OFFSET;
        if (moment < 0) {
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
    mRb.mEndPoint = mTrussNodes[2];
    mRb.mColor = g.mGreen;
    mRb.mLabel = "Rb";
    mRb.mLabelXOff = 20;
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
        mDir = Util.direction(mLoadLine[2].x, mLoadLine[2].y,
                              mLoadLine[0].x, mLoadLine[0].y );
        mDist = Util.distance(mLoadLine[2].x, mLoadLine[2].y,
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
    mRa.mLabelXOff = -24;
    mRa.mLabelYOff = 0;
    addToDrawList(mRa);

    TTextPointLength RaMag = new TTextPointLength(g);
    RaMag.mBasePoint = mRaTail;
    RaMag.mXOffset = -20;
    RaMag.mYOffset = 20;
    RaMag.mLine = mRa;
    addToDrawList(RaMag);
  }

  private TLine mLoadLineLines[] = new TLine[3];
  private void makeLoadLine() {
    TPointTranslate newPoint;

    mLoadLine[0] = new TPoint(LOAD_LINE_START_X, LOAD_LINE_START_Y);
    mLoadLine[0].mLabel = "a";
    mLoadLine[0].mLabelXOff = 14;
    mLoadLine[0].mLabelYOff = 0;
    mLoadLine[0].mSize = 6;
    addToUpdateList(mLoadLine[0]);

    newPoint = new TPointTranslate();
    mLoadLine[1] = newPoint;
    newPoint.mBasePoint = mLoadLine[0];
    newPoint.mVectorStart = mForceTail;
    newPoint.mVectorEnd = mLoad.mArrowHead;
    newPoint.mLabel = "b";
    newPoint.mLabelXOff = 14;
    newPoint.mLabelYOff = 0;
    newPoint.mSize = 7;
    newPoint.dragAlso(mLoadLine[0]);
    addToUpdateList(newPoint);

    newPoint = new TPointTranslate();
    mLoadLine[2] = newPoint;
    newPoint.mBasePoint = mLoadLine[1];
    newPoint.mVectorStart = mRb.mArrowTail;
    newPoint.mVectorEnd = mRb.mArrowHead;
    newPoint.mLabel = "c";
    newPoint.mLabelXOff = 14;
    newPoint.mLabelYOff = 0;
    newPoint.mSize = 7;
    newPoint.dragAlso(mLoadLine[0]);
    addToUpdateList(newPoint);

    for (int i = 0; i < 3; i++) {
      mLoadLineLines[i] = new TLine();
      mLoadLineLines[i].mColor = Color.black;
      mLoadLineLines[i].mSize = 2;
      mLoadLineLines[i].mStartPoint = mLoadLine[i];
      mLoadLineLines[i].mEndPoint = mLoadLine[(i + 1) % 3];
      mLoadLineLines[i].mSize = 4;
      mLoadLineLines[i].dragAlso(mLoadLine[0]);
      addToDrawList(mLoadLineLines[i]);
    }
    mLoadLineLines[0].mColor = mLoad.mColor;
    mLoadLineLines[1].mColor = g.mGreen;
    mLoadLineLines[2].mColor = g.mGreen;
  }

  private void makeForcePolygon() {
    TPointForcePoly newNode;

    newNode = new TPointForcePoly();
    mForcePolyNode = newNode;
    newNode.mMember1 = mMembers[0];
    newNode.mMember1ForceBegin = mLoadLine[0];
    newNode.mMember2 = mMembers[1];
    newNode.mMember2ForceBegin = mLoadLine[1];
    newNode.mLabel = "1";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);
    addToUpdateList(newNode);

// Force poly lines

    TLineForcePoly newLine;

    for (int i = 0; i < 3; i++) {
      mForcePolyLines[i] = new TLineForcePoly();
      mMembers[i].mForcePolyMember = mForcePolyLines[i];
      mForcePolyLines[i].dragAlso(mLoadLine[0]);
    }

 //--
    // a1
    newLine = mForcePolyLines[0];
    newLine.mStartPoint = mLoadLine[0];
    newLine.mEndPoint = mForcePolyNode;
    newLine.mMemberStart = mTrussNodes[0];
    newLine.mMemberEnd = mTrussNodes[1];
    addToDrawList(newLine);

    // b1
    newLine = mForcePolyLines[1];
    newLine.mStartPoint = mLoadLine[1];
    newLine.mEndPoint = mForcePolyNode;
    newLine.mMemberStart = mTrussNodes[1];
    newLine.mMemberEnd = mTrussNodes[2];
    addToDrawList(newLine);

    // c1
    newLine = mForcePolyLines[2];
    newLine.mStartPoint = mLoadLine[2];
    newLine.mEndPoint = mForcePolyNode;
    newLine.mMemberStart = mTrussNodes[2];
    newLine.mMemberEnd = mTrussNodes[0];
    addToDrawList(newLine);

    for (int i = 0; i < mLoadLine.length; i++) {
      addToDrawListOnly(mLoadLine[i]);
    }
    addToDrawListOnly(mForcePolyNode);
  }

  private void makeTriangleLabels() {
    TTextTriangle newLabel;

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[0];
    newLabel.p2 = mTrussNodes[1];
    newLabel.p3 = mTrussNodes[2];
    newLabel.mText = "1";
    addToDrawList(newLabel);
  }

  public static final int REPORT_X_START = 400;
  public static final int REPORT_Y_START = 440;
  public static final int REPORT_LINE_SPACE = 17;
  public static final int REPORT_COLUMN_SPACE = 110;

  private void makeReport() {
    int x = REPORT_X_START;
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
//        mUpdateCanvas.dump();

        if (mCircleLoad != null) {
          g.mTimer.removeJob(mCircleLoad);
          mCircleLoad = null;
          mCircleLoadButton.mSelected = false;
        }

        JobMoveViewToOrigin originMove = new JobMoveViewToOrigin(g);
        originMove.mView = mUpdateCanvas;
        g.mTimer.addJob(originMove);

        JobMovePointToStart newJob;

        newJob = new JobMovePointToStart(g);
        newJob.mMovePoint = mLoadLine[0];
        g.mTimer.addJob(newJob);

        for (int i = 0; i < 3; i++) {
          newJob = new JobMovePointToStart(g);
          newJob.mMovePoint = mTrussNodes[i];
          g.mTimer.addJob(newJob);
        }
        newJob = new JobMovePointToStart(g);
        newJob.mMovePoint = mForceTail;
        g.mTimer.addJob(newJob);
      }
    };
    y += BUTTON_Y_OFFSET;


    mCircleLoadButton = new TButton("Circle Load");
    mCircleLoadButton.x = x;
    mCircleLoadButton.y = y;
    mCircleLoadButton.mWidth = 170;
    mCircleLoadButton.mHeight = 20;
    mCircleLoadButton.mIsToggle = true;
    addToDrawList(mCircleLoadButton);
    mCircleLoadButton.mAction = new TAction() {
      public void run() {
        if (mCircleLoad == null) {
          mCircleLoad = new JobCirclePoint(g);
          mCircleLoad.mMovePoint = mForceTail;
          mCircleLoad.mPivot = mTrussNodes[1];
          g.mTimer.addJob(mCircleLoad);
        }
        else {
          g.mTimer.removeJob(mCircleLoad);
          mCircleLoad = null;
        }
      }
    };
    y += BUTTON_Y_OFFSET;

    mLoadsVertCheck = new TButton("Keep Load Vertical");
    mLoadsVertCheck.x = x;
    mLoadsVertCheck.y = y;
    mLoadsVertCheck.mWidth = 170;
    mLoadsVertCheck.mHeight = 20;
    mLoadsVertCheck.mIsToggle = true;
    addToDrawList(mLoadsVertCheck);
    mLoadsVertCheck.mAction = new TAction() {
      public void run() {
        mLoadsVertical = mLoadsVertCheck.mSelected;
        repaint();
        mUpdateCanvas.globalUpdate();
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
    addToDrawList(mLinesOfActionCheck);
    mLinesOfActionCheck.mAction = new TAction() {
      public void run() {
        mLinesOfAction = mLinesOfActionCheck.mSelected;
        repaint();
      }
    };
  }
}