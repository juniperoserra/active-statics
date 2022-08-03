package truss;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class CableStayApplet extends Applet {
  public static final int APPLET_WIDTH = 750;
  public static final int APPLET_HEIGHT = 700;

  public static final int SEGMENT_SIZE = 40;
  public static final int TOWER_X_START = 300;
  public static final int TOWER_Y_START = 140;
  public static final int LOAD_LINE_START_X = 550;
  public static final int LOAD_LINE_START_Y = 520;

  public GraphicEntity[] drawList = new GraphicEntity[0];
  public GraphicEntity[] updateList = new GraphicEntity[0];
  private Image mOffscreen;
//  private Graphics mGOff;
//  Dimension mOffSize;
  public int W;
  public int H;

  private NoScrollUpdateCanvas mUpdateCanvas;

  public TPin mPin;
  public TRoller mRoller;

  private Point tempPoint = new Point();
  private int paintedTwice = 0;

  TButton mOriginalPosButton;
  TButton mDeckHorizCheck;
  TButton mTowerVertCheck;
  TButton mHarpButton;
  TButton mFanButton;


  public TPoint[] mNodes = new TPoint[26];
  public TPoint[] mForceTails = new TPoint[4];
  public TPoint[] mHarpPoints = new TPoint[2];
  public TArrow[] mLoads = new TLoad[6];
  public TLineMember[] mMembers = new TLineMember[18];

  public TPoint[] mGhostNodes = new TPoint[13];
  public TLineMember[] mGhostMembers = new TLineMember[8];
  public THalfPlane mGround;
  public TPoint mGroundRight;
  public TPoint mGroundBelow;

  public TPoint mRbTail;
  public TReaction mRb;
  public TPoint mRaHead;
  public TPoint mRaTail;
  public TReaction mRa;
  public TTextPointLength mRaMag;
  public TPoint[] mLoadLine = new TPoint[8];
  public TLine[] mLoadLineLines = new TLine[8];

  public TPointForcePoly mForcePolyNodes[] = new TPointForcePoly[6];
  public TLineForcePoly mForcePolyLines[] = new TLineForcePoly[15];

  public boolean mDeckHoriz = false;
  public boolean mTowerVert = false;

  public G g;


  boolean isStandalone = false;
  /**Get a parameter value*/
  public String getParameter(String key, String def) {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }


  private void makeHarpPoints() {
    mHarpPoints[0] = new TPoint() {
      public void update() {
        float dx = mNodes[7].x - mNodes[8].x;
        float dy = mNodes[7].y - mNodes[8].y;

        TLine.intersection(mNodes[0].x, mNodes[0].y,
                             mNodes[5].x, mNodes[5].y,
                             mNodes[8].x, mNodes[8].y,
                             mNodes[1].x - dx, mNodes[1].y - dy,
                              mHarpPoints[0]);
        //System.out.println("HarpPoint1: " +  mHarpPoints[0].x +", " + mHarpPoints[0].y);

      }
    };
    addToUpdateList(mHarpPoints[0]);

    mHarpPoints[1] = new TPoint() {
      public void update() {
        float dx = mNodes[7].x - mNodes[9].x;
        float dy = mNodes[7].y - mNodes[9].y;

        TLine.intersection(mNodes[0].x, mNodes[0].y,
                               mNodes[5].x, mNodes[5].y,
                               mNodes[9].x, mNodes[9].y,
                               mNodes[1].x - dx, mNodes[1].y - dy,
                                mHarpPoints[1]);
        //System.out.println("HarpPoint2: " +  mHarpPoints[1].x +", " + mHarpPoints[1].y);
      }
    };
    addToUpdateList(mHarpPoints[1]);
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
//    g.mLengthDivisor = 1.0f;

    makeButtons();

    makeNodes();
    makeGhostNodes();
    makeMembers();
    makeGhostMembers();

    makeLoads();
    addNodes();
    makeRa();
    makeRb();
    makeLoadLine();
    makeForcePolygon();
    makeText();
    makeTriangleLabels();
    makeReport();

    makeHarpPoints();
    mTowerVertCheck.run();
    mDeckHorizCheck.run();


    /*    makeSupports();
    addToUpdateList(mRaTail);
    addToUpdateList(mRbTail);*/

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    repaint();
  }
  /**Component initialization*/
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


  private void jbInit() throws Exception {
    setBackground(g.mBackground);
    W = getSize().width;
    H = getSize().height;

    mUpdateCanvas = new NoScrollUpdateCanvas(this, g) {
      public void globalUpdate() {    // Apply contstraints
        Rectangle r = getBounds();
        r.x -= (mUpdateCanvas.viewXOffset+10);
        r.y -= mUpdateCanvas.viewYOffset;
        r.width += 20;
        mGround.setBounds(r);
        adjustMastThicknesses();

        if (this.g.selectedEntity == mNodes[4] && mNodes[4].y < mNodes[0].y) {
          mNodes[0].y = mNodes[4].y;
        }

        if (mDeckHoriz && this.g.selectedEntity == mNodes[6]) {
          if (mNodes[6].y < mNodes[0].y) {
            mNodes[6].y = mNodes[0].y;
          }
          else if (mNodes[6].y > mNodes[5].y) {
            mNodes[6].y = mNodes[5].y;
          }
        }

        for (int i = 1; i < 5; i++) {
          if (this.g.selectedEntity == mNodes[i] || this.g.autoMove)
              mMembers[0].closestPointOnSeg(mNodes[i], mNodes[i]);
          else
              mMembers[0].paramToPoint(mNodes[i].mParamOnSeg, mNodes[i]);
        }

        // Deal with keeping deck horizontal
        if (mDeckHoriz) {
          if (this.g.selectedEntity == mNodes[6]) {
              TLine.intersection(mNodes[0].x, mNodes[0].y,
                                  mNodes[5].x, mNodes[5].y,
                                  mNodes[6].x, mNodes[6].y,
                                  mNodes[6].x - 100, mNodes[6].y,
                                  mNodes[4]);
              if (mNodes[4].y < mNodes[0].y) {
                mNodes[4].x = mNodes[0].x;
                mNodes[4].y = mNodes[0].y;
              }
              if (mNodes[4].y > mNodes[5].y) {
                mNodes[4].x = mNodes[5].x;
                mNodes[4].y = mNodes[5].y;
              }
          }
          else {
            mNodes[6].y = mNodes[4].y;
          }
        }

        // Deal with keeping tower vertical
        if (mTowerVert) {
          if (this.g.selectedEntity == mNodes[5]) {
            mNodes[0].x = mNodes[5].x;
          }
          else {
            mNodes[5].x = mNodes[0].x;
          }
        }

        // Remember the node positions along the tower
        setNodeParams();

        float dx = (mNodes[6].x - mNodes[4].x) / 6.0f;
        float dy = (mNodes[6].y - mNodes[4].y) / 6.0f;
        for (int i = 7; i < 13; i++) {
          mNodes[i].x = mNodes[4].x + (2 * i - 19) * dx;
          mNodes[i].y = mNodes[4].y + (2 * i - 19) * dy;
        }
        mNodes[13].x = mNodes[4].x - 6 * dx;
        mNodes[13].y = mNodes[4].y - 6 * dy;

        for (int i = 14; i < 20; i++) {
          mNodes[i].x = mNodes[i-7].x;
          mNodes[i].y = mNodes[i-7].y + 10;
        }

        for (int i = 20; i < 26; i++) {
          mNodes[i].x = mNodes[i-6].x;
          mNodes[i].y = mNodes[i-6].y + mMembers[7].length() / 6.0f;
        }

        for (int i = 0; i < 6; i++) {
          mGhostNodes[i].y = mNodes[i].y;
          mGhostNodes[i].x = mNodes[6].x + (mNodes[6].x - mNodes[i].x);
        }
        for (int i = 7; i < 14; i++) {
          mGhostNodes[i-1].y = mNodes[i].y;
          mGhostNodes[i-1].x = mNodes[6].x + (mNodes[6].x - mNodes[i].x);
        }

        for (int i = 0; i < 6; i++) {
          mLoads[i].mLabelYOff = (int)mLoads[i].length() + 20;
        }

        for (int i = 7; i < 14; i++) {
          mNodes[i].mLabelXOff = (int)(mMembers[7].length() / -12.0f);
        }
        mNodes[10].mLabelXOff /= 2;

        mGroundRight.x = mNodes[5].x + 100;
        mGroundRight.y =  mNodes[5].y;
        mGroundBelow.x = mNodes[5].x;
        mGroundBelow.y = mNodes[5].y + 100;
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

  private void setNodeParams() {
    for (int i = 1; i < 5; i++) {
      mNodes[i].mParamOnSeg = mMembers[0].pointToParam(mNodes[i]);
    }
  }

/*  public void globalUpdate() {    // Apply contstraints

  // Moving the deck

  for (int i = 1; i < 5; i++) {
    if (g.selectedEntity == mNodes[i] || g.autoMove)
        mMembers[0].closestPointOnSeg(mNodes[i], mNodes[i]);
    else
        mMembers[0].paramToPoint(mNodes[i].mParamOnSeg, mNodes[i]);
  }

  // Deal with keeping deck horizontal
  if (mDeckHoriz) {
    if (g.selectedEntity == mNodes[6]) {
        TLine.intersection(mNodes[0].x, mNodes[0].y,
                            mNodes[5].x, mNodes[5].y,
                            mNodes[6].x, mNodes[6].y,
                            mNodes[6].x - 100, mNodes[6].y,
                            mNodes[4]);
        if (mNodes[4].y < mNodes[0].y) {
          mNodes[4].x = mNodes[0].x;
          mNodes[4].y = mNodes[0].y;
        }
        if (mNodes[4].y > mNodes[5].y) {
          mNodes[4].x = mNodes[5].x;
          mNodes[4].y = mNodes[5].y;
        }
    }
    else {
      mNodes[6].y = mNodes[4].y;
    }
  }

  // Deal with keeping tower vertical
  if (mTowerVert) {
    if (g.selectedEntity == mNodes[5]) {
      mNodes[0].x = mNodes[5].x;
    }
    else {
      mNodes[5].x = mNodes[0].x;
    }
  }

  // Remember the node positions along the tower
  setNodeParams();

  float dx = (mNodes[6].x - mNodes[4].x) / 6.0f;
  float dy = (mNodes[6].y - mNodes[4].y) / 6.0f;
  for (int i = 7; i < 13; i++) {
    mNodes[i].x = mNodes[4].x + (2 * i - 19) * dx;
    mNodes[i].y = mNodes[4].y + (2 * i - 19) * dy;
  }
  mNodes[13].x = mNodes[4].x - 6 * dx;
  mNodes[13].y = mNodes[4].y - 6 * dy;

  for (int i = 14; i < 20; i++) {
    mNodes[i].x = mNodes[i-7].x;
    mNodes[i].y = mNodes[i-7].y + 10;
  }

  for (int i = 20; i < 26; i++) {
    mNodes[i].x = mNodes[i-6].x;
    mNodes[i].y = mNodes[i-6].y + mMembers[7].length() / 6.0f;
  }

  for (int i = 0; i < 6; i++) {
    mGhostNodes[i].y = mNodes[i].y;
    mGhostNodes[i].x = mNodes[6].x + (mNodes[6].x - mNodes[i].x);
  }
  for (int i = 7; i < 14; i++) {
    mGhostNodes[i-1].y = mNodes[i].y;
    mGhostNodes[i-1].x = mNodes[6].x + (mNodes[6].x - mNodes[i].x);
  }

  for (int i = 0; i < 6; i++) {
    mLoads[i].mLabelYOff = (int)mLoads[i].length() + 20;
  }

  for (int i = 7; i < 14; i++) {
    mNodes[i].mLabelXOff = (int)(mMembers[7].length() / -12.0f);
  }
  mNodes[10].mLabelXOff /= 2;

  mGroundRight.x = mNodes[5].x + 100;
  mGroundRight.y =  mNodes[5].y;
  mGroundBelow.x = mNodes[5].x;
  mGroundBelow.y = mNodes[5].y + 100;
}*/

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
    mGround.setBounds(getBounds());

    mGOff.clearRect(0, 0, W, H);
    for (int j = 0; j < 4; j++) {
      globalUpdate();
      for (int i = 0; i < updateList.length; i++) {   // We update twice to filter through the changes
        updateList[i].update();
      }
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
      for (int j = 0; j < 4; j++) {
        globalUpdate();
        for (int i = 0; i < updateList.length; i++) {   // We update twice to filter through the changes
          updateList[i].update();
        }
      }
      graphics.clearRect(0, 0, W, H);
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
    title.mText = "Fanlike Structure";
    title.mSize = 24;
    title.x = 20;
    title.y = 50;
    title.mPosRelativeTo = GraphicEntity.VIEW_RELATIVE;
    addToDrawList(title);

    TTextPoint forcePoly = new TTextPoint();
    forcePoly.mBasePoint = mLoadLine[0];
    forcePoly.mXOffset = -160;
    forcePoly.mYOffset = -60;
    forcePoly.mSize = 20;
    forcePoly.mText = "Force Polygon";
    addToDrawList(forcePoly);

    TTextPoint formDiag = new TTextPoint();
    formDiag.mBasePoint = mNodes[5];
    formDiag.mXOffset = -170;
    formDiag.mYOffset = 30;
    formDiag.mSize = 20;
    formDiag.mText = "Form Diagram";
    addToDrawList(formDiag);
  }
/*
  private void makeSupports() {
    mPin = new TPin(mTrussNodes[0]);
    mPin.mDir = 0.0f;
    addToDrawList(mPin);
    mRoller = new TRoller(mTrussNodes[1]);
    mRoller.mDir = 0.0f;
    addToDrawList(mRoller);
  }
*/
  private void makeGhostNodes() {
    for (int i = 0; i < 13; i++) {
      mGhostNodes[i] = new TPoint();
      mGhostNodes[i].mControlPoint = false;
      mGhostNodes[i].mSelectable = false;
    }

    mGround = new THalfPlane();
    mGround.mP1 = mNodes[5];
    mGround.mP2 = mGroundRight;
    mGround.mSidePoint = mGroundBelow;
    mGround.mColor = g.mYellow;
    addToDrawList(mGround);
  }

  private void makeNodes() {
    for (int i = 0; i < 2; i++) {
      mNodes[i] = new TPoint(TOWER_X_START, TOWER_Y_START  + i * SEGMENT_SIZE);
    }
    mNodes[2] = new TPoint(TOWER_X_START, 227);
    mNodes[3] = new TPoint(TOWER_X_START, 275);
    mNodes[4] = new TPoint(TOWER_X_START, TOWER_Y_START  + 4 * SEGMENT_SIZE);

    mNodes[1].mLabel = "A";
    mNodes[1].mLabelXOff = 15;
    mNodes[1].mLabelYOff = 0;

    mNodes[5] = new TPoint(TOWER_X_START, TOWER_Y_START  + (int)(7 * SEGMENT_SIZE));
    mGroundRight = new TPoint(mNodes[5].x + 100, mNodes[5].y);
    mGroundBelow = new TPoint(mNodes[5].x, mNodes[5].y + 100);

    mNodes[6] = new TPoint(TOWER_X_START + 3 * SEGMENT_SIZE, TOWER_Y_START  + 4 * SEGMENT_SIZE);
    mNodes[6].mLabel = "B";
    mNodes[6].mLabelXOff = 0;
    mNodes[6].mLabelYOff = 30;

    mNodes[4].dragAlso(mNodes[6]);
    mNodes[4].mLabel = "F";
    mNodes[4].mLabelXOff = -15;
    mNodes[4].mLabelYOff = 30;

    for (int i = 7; i < 14; i++) {
      mNodes[i] = new TPoint((int)(TOWER_X_START + (i - 9.5) * SEGMENT_SIZE), TOWER_Y_START  + 4 * SEGMENT_SIZE);
      mNodes[i].mControlPoint = false;
      mNodes[i].mSelectable = false;

      if (i > 9)
        mNodes[i].mLabel = String.valueOf((char)('C' + 12 - i));
      else
        mNodes[i].mLabel = String.valueOf((char)('C' + 13 - i));
      mNodes[i].mLabelXOff = -35;
      mNodes[i].mLabelYOff = 30;
    }
    mNodes[10].mLabelXOff = -15;
    mNodes[7].mLabel = "";

    for (int i = 14; i < 20; i++) {
      mNodes[i] = new TPoint((int)(TOWER_X_START + (i - 16.5) * SEGMENT_SIZE), TOWER_Y_START  + 4 * SEGMENT_SIZE + 10);
      mNodes[i].mControlPoint = false;
      mNodes[i].mSelectable = false;
    }

    for (int i = 20; i < 26; i++) {
      mNodes[i] = new TPoint((int)(TOWER_X_START + (i - 22.5) * SEGMENT_SIZE), TOWER_Y_START  + 5 * SEGMENT_SIZE + 10);
      mNodes[i].mControlPoint = false;
      mNodes[i].mSelectable = false;
    }

//Weird effect. Not exactly what we're after
//    mNodes[0].dragAlso(mNodes[6]);
  }

  private void addNodes() {
    addToDrawList(mNodes[5]);
    addToUpdateList(mGhostNodes[5]);
    for (int i = 0; i < 13; i++) {
      if (i != 5) {
        addToDrawList(mNodes[i]);
        addToUpdateList(mGhostNodes[i]);
      }
    }
  }

  // Deals with relative position of
  private void adjustMastThicknesses() {

    TPoint topNode;
    TPoint midNode;
    TPoint bottomNode;

    if (mNodes[1].y <= mNodes[2].y && mNodes[1].y <= mNodes[3].y) {
      topNode = mNodes[1];
      if (mNodes[2].y <= mNodes[3].y) {
        midNode = mNodes[2];
        bottomNode = mNodes[3];
      }
      else {
        midNode = mNodes[3];
        bottomNode = mNodes[2];
      }
    }
    else if (mNodes[2].y <= mNodes[3].y) {
      topNode = mNodes[2];
      if (mNodes[1].y <= mNodes[3].y) {
        midNode = mNodes[1];
        bottomNode = mNodes[3];
      }
      else {
        midNode = mNodes[3];
        bottomNode = mNodes[1];
      }
    }
    else {
      topNode = mNodes[3];
      if (mNodes[1].y <= mNodes[2].y) {
        midNode = mNodes[1];
        bottomNode = mNodes[2];
      }
      else {
        midNode = mNodes[2];
        bottomNode = mNodes[1];
      }
    }

    mMembers[13].mStartPoint = topNode;
    mMembers[13].mEndPoint = midNode;

    mMembers[14].mStartPoint = midNode;
    mMembers[14].mEndPoint = bottomNode;

    mMembers[15].mStartPoint = bottomNode;
    mMembers[15].mEndPoint = mNodes[5];
  }

  private void makeLoads() {
    for(int i = 0; i < 6; i++) {
      mLoads[i] = new TLoad(g);
      mLoads[i].mStartPoint = mNodes[i+14];
      mLoads[i].mEndPoint = mNodes[i+20];
      mLoads[i].mColor = Color.darkGray;

      addToDrawList(mLoads[i]);
    }
  }

  private void makeGhostMembers() {
    TLine ghostDeck = new TLine();
    ghostDeck.mStartPoint = mNodes[6];
    ghostDeck.mEndPoint = mGhostNodes[12];
    ghostDeck.mColor = Color.gray;
    ghostDeck.mSize = 3;
    ghostDeck.mDashed = true;
    ghostDeck.mDashLength = 7;
    ghostDeck.mGapLength = 5;

    TLine ghostTower = new TLine();
    ghostTower.mStartPoint = mGhostNodes[0];
    ghostTower.mEndPoint = mGhostNodes[5];
    ghostTower.mColor = Color.gray;
    ghostTower.mSize = 3;
    ghostTower.mDashed = true;
    ghostTower.mDashLength = 7;
    ghostTower.mGapLength = 5;

    TLine ghostCable;

    ghostCable = new TLine();
    ghostCable.mStartPoint = mGhostNodes[1];
    ghostCable.mEndPoint = mGhostNodes[6];
    ghostCable.mColor = Color.gray;
    ghostCable.mSize = 3;
    ghostCable.mDashed = true;
    ghostCable.mDashLength = 7;
    ghostCable.mGapLength = 5;
    addToDrawList(ghostCable);

    ghostCable = new TLine();
    ghostCable.mStartPoint = mGhostNodes[2];
    ghostCable.mEndPoint = mGhostNodes[7];
    ghostCable.mColor = Color.gray;
    ghostCable.mSize = 3;
    ghostCable.mDashed = true;
    ghostCable.mDashLength = 7;
    ghostCable.mGapLength = 5;
    addToDrawList(ghostCable);

    ghostCable = new TLine();
    ghostCable.mStartPoint = mGhostNodes[3];
    ghostCable.mEndPoint = mGhostNodes[8];
    ghostCable.mColor = Color.gray;
    ghostCable.mSize = 3;
    ghostCable.mDashed = true;
    ghostCable.mDashLength = 7;
    ghostCable.mGapLength = 5;
    addToDrawList(ghostCable);

    ghostCable = new TLine();
    ghostCable.mStartPoint = mGhostNodes[3];
    ghostCable.mEndPoint = mGhostNodes[9];
    ghostCable.mColor = Color.gray;
    ghostCable.mSize = 3;
    ghostCable.mDashed = true;
    ghostCable.mDashLength = 7;
    ghostCable.mGapLength = 5;
    addToDrawList(ghostCable);

    ghostCable = new TLine();
    ghostCable.mStartPoint = mGhostNodes[2];
    ghostCable.mEndPoint = mGhostNodes[10];
    ghostCable.mColor = Color.gray;
    ghostCable.mSize = 3;
    ghostCable.mDashed = true;
    ghostCable.mDashLength = 7;
    ghostCable.mGapLength = 5;
    addToDrawList(ghostCable);

    ghostCable = new TLine();
    ghostCable.mStartPoint = mGhostNodes[1];
    ghostCable.mEndPoint = mGhostNodes[11];
    ghostCable.mColor = Color.gray;
    ghostCable.mSize = 3;
    ghostCable.mDashed = true;
    ghostCable.mDashLength = 7;
    ghostCable.mGapLength = 5;
    addToDrawList(ghostCable);

    addToDrawList(ghostDeck);
    addToDrawList(ghostTower);
  }

  private void makeMembers() {
    mMembers[0] = new TLineMember(g);
    mMembers[0].mStartPoint = mNodes[0];
    mMembers[0].mEndPoint = mNodes[5];
    mMembers[0].mColor = g.mYellow;

    mMembers[1] = new TLineMember(g);
    mMembers[1].mStartPoint = mNodes[1];
    mMembers[1].mEndPoint = mNodes[7];

    mMembers[2] = new TLineMember(g);
    mMembers[2].mStartPoint = mNodes[2];
    mMembers[2].mEndPoint = mNodes[8];

    mMembers[3] = new TLineMember(g);
    mMembers[3].mStartPoint = mNodes[3];
    mMembers[3].mEndPoint = mNodes[9];

    mMembers[4] = new TLineMember(g);
    mMembers[4].mStartPoint = mNodes[1];
    mMembers[4].mEndPoint = mNodes[12];

    mMembers[5] = new TLineMember(g);
    mMembers[5].mStartPoint = mNodes[2];
    mMembers[5].mEndPoint = mNodes[11];

    mMembers[6] = new TLineMember(g);
    mMembers[6].mStartPoint = mNodes[3];
    mMembers[6].mEndPoint = mNodes[10];


    // 7 is deck as a whole.
    mMembers[7] = new TLineMember(g);
    mMembers[7].mStartPoint = mNodes[6];
    mMembers[7].mEndPoint = mNodes[13];
    mMembers[7].dragAlso(mNodes[6]);
    mMembers[7].dragAlso(mNodes[4]);
    mMembers[7].mColor = g.mYellow;

    for (int i = 8; i < 13; i++) {
      mMembers[i] = new TLineMember(g);
      mMembers[i].mStartPoint = mNodes[i-1];
      mMembers[i].mEndPoint = mNodes[i];
      mMembers[i].dragAlso(mNodes[6]);
      mMembers[i].dragAlso(mNodes[4]);
    }
    mMembers[8].mLabel = "1";
    mMembers[8].mLabelXOff = 8;
    mMembers[9].mLabel = "2";
    mMembers[9].mLabelXOff = 8;
    mMembers[11].mLabel = "5";
    mMembers[11].mLabelXOff = -16;
    mMembers[12].mLabel = "6";
    mMembers[12].mLabelXOff = -16;

    mMembers[13] = new TLineMember(g);
    mMembers[13].mStartPoint = mNodes[1];
    mMembers[13].mEndPoint = mNodes[2];

    mMembers[14] = new TLineMember(g);
    mMembers[14].mStartPoint = mNodes[2];
    mMembers[14].mEndPoint = mNodes[3];


    mMembers[15] = new TLineMember(g);
    mMembers[15].mStartPoint = mNodes[3];
    mMembers[15].mEndPoint = mNodes[5];

    mMembers[16] = new TLineMember(g);
/*    mMembers[15] = new TLineMember(g);
    mMembers[15].mStartPoint = mNodes[3];
    mMembers[15].mEndPoint = mNodes[4];

    mMembers[16] = new TLineMember(g);
    mMembers[16].mStartPoint = mNodes[4];
    mMembers[16].mEndPoint = mNodes[5];*/

    mMembers[17] = new TLineMember(g);
    mMembers[17].mStartPoint = mNodes[12];
    mMembers[17].mEndPoint = mNodes[6];

    for(int i = 0; i < 18; i++) {
      for (int j = 0; j < 26; j++) {
        mMembers[i].dragAlso(mNodes[j]);
      }
    }

    addToDrawList(mMembers[0]);
    for (int i = 13; i < 17; i++) {
      if (i != 16)
        addToDrawList(mMembers[i]);
    }
    for (int i = 1; i < 13; i++) {
      addToDrawList(mMembers[i]);
    }
    addToDrawList(mMembers[17]);
    setNodeParams();
  }

  private void makeRb() {
    mRbTail = new TPoint() {
      public void update() {
        x = mNodes[5].x - mRa.mArrowTail.x + mRa.mArrowHead.x;
        y = mNodes[5].y + + 15 + 6.0f * mLoads[0].length();
      }
    };
    addToUpdateList(mRbTail);
    mRb = new TReaction();
    mRb.mStartPoint = mRbTail;
    mRb.mEndPoint = mNodes[5];
    mRb.mColor = g.mGreen;
    mRb.mLabel = "";
    mRb.ARROW_OFFSET = 15;
    mRb.mArrowOffset = 15;
    mRb.mLabelXOff = 15;
    mRb.mLabelYOff = 0;
    addToDrawList(mRb);

    TTextPointLength RbMag = new TTextPointLength(g);
    RbMag.mBasePoint = mRb.mStartPoint;// mLabelPoint;
    RbMag.mXOffset = -20;
    RbMag.mYOffset = 20;
    RbMag.mLine = mRb;
    addToDrawList(RbMag);

    TPoint rbCorner = new TPoint() {
      public void update() {
        x = mNodes[5].x;
        y = mNodes[5].y + + 15 + 6.0f * mLoads[0].length();
      }
    };
    addToUpdateList(rbCorner);

    TLine rbVert = new TLine();
    rbVert.mStartPoint = mNodes[5];
    rbVert.mEndPoint = rbCorner;
    rbVert.mColor = g.mGreen;
    rbVert.mSize = 2;
    rbVert.mDashed = true;
    rbVert.mDashLength = 7;
    rbVert.mGapLength = 5;
    addToDrawList(rbVert);

    TLine rbHoriz = new TLine();
    rbHoriz.mStartPoint = rbCorner;
    rbHoriz.mEndPoint = mRbTail;
    rbHoriz.mColor = g.mGreen;
    rbHoriz.mSize = 2;
    rbHoriz.mDashed = true;
    rbHoriz.mDashLength = 7;
    rbHoriz.mGapLength = 5;
    addToDrawList(rbHoriz);
  }

  private void makeRa() {
    mRa = new TReaction();

    mRaHead = new TPoint() {
      public void update() {
        x = mNodes[6].x + mRa.ARROW_OFFSET;
        y = mNodes[6].y;
      }
    };
    addToUpdateList(mRaHead);

    mRaTail = new TPoint() {
      public void update() {
        float totalMoment = 0.0f;
        for (int i = 0; i < 6; i++) {
          totalMoment += mLoads[i].moment(mNodes[5]);
        }
        /*totalMoment *= TLine.CCW(mNodes[5].x, mNodes[5].y - 10,
                       mNodes[5].x, mNodes[5].y,
                       mNodes[0].x, mNodes[0].y);*/

        float pDist = TLine.perpDist(mNodes[6].x, mNodes[6].y,
                                  mNodes[6].x + 10, mNodes[6].y,
                                  mNodes[5].x, mNodes[5].y);
        if (Math.abs(pDist) < 0.1f)
          pDist = 1.0f;
        totalMoment /= pDist;
        x = mNodes[6].x + Math.abs(totalMoment) + 2 * mRa.ARROW_OFFSET;
        y = mNodes[6].y;

        mLoadLine[1].y = mLoadLine[0].y;
        mLoadLine[1].x = mLoadLine[0].x + totalMoment;

        if (totalMoment < 0) {
          mRa.mReverse = -1;
        }
        else {
          mRa.mReverse = 1;
        }
        if (Math.abs(totalMoment) < 1.0f) {
          mRa.mInvisible = true;
          mRaMag.mInvisible = true;
        }
        else {
          mRa.mInvisible = false;
          mRaMag.mInvisible = false;
        }
      }
    };
    addToUpdateList(mRaTail);

    mRa.mStartPoint = mRaHead;
    mRa.mEndPoint = mRaTail;
    mRa.mColor = g.mGreen;
    mRa.mLabel = "";
    mRa.ARROW_OFFSET = 15;
    mRa.mArrowOffset = 15;
    mRa.mLabelXOff = -30;
    mRa.mLabelYOff = 30;
    addToDrawList(mRa);


/*    mRaTail = new TRPoint();
    addToUpdateList(mRaTail);
    mRa = new TArrowOneSide();
    mRa.mStartPoint = new TPoint(mRaTail.x, mRaTail.y);
    mRa.mEndPoint = new TPoint(mTrussNodes[0].x, mTrussNodes[0].y);
    mRa.mAnchorPoint = new TPoint(mTrussNodes[0].x, mTrussNodes[0].y);
    mRa.mColor = g.mGreen;
    mRa.mLabel = "Ra";
    mRa.ARROW_OFFSET = 45;
    mRa.mArrowOffset = 45;
    mRa.mLabelXOff = -30;
    mRa.mLabelYOff = 0;
    addToDrawList(mRa);
*/
    mRaMag = new TTextPointLength(g);
    mRaMag.mBasePoint = mRaTail;
    mRaMag.mXOffset = -20;
    mRaMag.mYOffset = 20;
    mRaMag.mLine = mRa;
    addToDrawList(mRaMag);
  }

  private void makeLoadLine() {
    TPointTranslate newPoint;

    mLoadLine[0] = new TPoint(LOAD_LINE_START_X, LOAD_LINE_START_Y);
    TPoint prevPoint = mLoadLine[0];
    mLoadLine[0].mLabel = "a";
    mLoadLine[0].mLabelXOff = -14;
    mLoadLine[0].mLabelYOff = 0;
    mLoadLine[0].mSize = 6;

    mLoadLine[1] = new TPoint();
    mLoadLine[1].x = LOAD_LINE_START_X;
    mLoadLine[1].y = LOAD_LINE_START_Y;
    mLoadLine[1].mLabel = "b";
    mLoadLine[1].mLabelXOff = 8;
    mLoadLine[1].mLabelYOff = 0;
    mLoadLine[1].mSize = 6;
    mLoadLine[1].mControlPoint = false;
    mLoadLine[1].dragAlso(mLoadLine[0]);
    prevPoint = mLoadLine[1];

    // ForcePolyLine for A-B
    TLineForcePoly newLine = new TLineForcePoly() {
      public void update() {
        super.update();
        boolean sgn1 = mLoadLine[0].x >= mLoadLine[1].x;
        boolean sgn2 = mNodes[12].x >= mNodes[6].x;

        if (mLoadLine[0].x == mLoadLine[1].x)
          mMembers[17].mForcePolyMember.mCharacter = TLineForcePoly.NONE;
        else if (sgn1 == sgn2)
          mMembers[17].mForcePolyMember.mCharacter = TLineForcePoly.TENSILE;
        else
          mMembers[17].mForcePolyMember.mCharacter = TLineForcePoly.COMPRESSIVE;
      }
    };
    newLine.mStartPoint = mLoadLine[0];
    newLine.mEndPoint = mLoadLine[1];
    newLine.mMemberStart = mNodes[12];
    newLine.mMemberEnd = mNodes[6];
    newLine.dragAlso(mLoadLine[0]);
    mMembers[17].mForcePolyMember = newLine;
    addToUpdateList(newLine);


    for (int i = 2; i < 5; i++) {
      newPoint = new TPointTranslate();
      mLoadLine[i] = newPoint;
      newPoint.mBasePoint = prevPoint;
      newPoint.mVectorStart = mLoads[0].mStartPoint;
      newPoint.mVectorEnd = mLoads[0].mArrowHead;
      newPoint.mLabel = String.valueOf((char)('a' + i));
      newPoint.mLabelXOff = -14;
      newPoint.mLabelYOff = 0;
      newPoint.mSize = 7;
      newPoint.dragAlso(mLoadLine[0]);
      prevPoint = newPoint;
    }

    prevPoint = mLoadLine[0];
    for (int i = 5; i < 8; i++) {
      newPoint = new TPointTranslate();
      mLoadLine[i] = newPoint;
      newPoint.mBasePoint = prevPoint;
      newPoint.mVectorStart = mLoads[0].mArrowHead;
      newPoint.mVectorEnd = mLoads[0].mStartPoint;
      newPoint.mLabel = String.valueOf((char)('h' - (i-5)));
      newPoint.mLabelXOff = -14;
      newPoint.mLabelYOff = 0;
      newPoint.mSize = 7;
      newPoint.dragAlso(mLoadLine[0]);
      prevPoint = newPoint;
    }

    for (int i = 0; i < 4; i++) {
      mLoadLineLines[i] = new TLine();
      mLoadLineLines[i].mColor = Color.darkGray;
      mLoadLineLines[i].mSize = 4;
      mLoadLineLines[i].mStartPoint = mLoadLine[i];
      mLoadLineLines[i].mEndPoint = mLoadLine[(i + 1) % 8];
      mLoadLineLines[i].dragAlso(mLoadLine[0]);
      addToDrawList(mLoadLineLines[i]);
    }

    mLoadLineLines[4] = new TLine();
    mLoadLineLines[4].mColor = Color.darkGray;
    mLoadLineLines[4].mSize = 4;
    mLoadLineLines[4].mStartPoint = mLoadLine[4];
    mLoadLineLines[4].mEndPoint = mLoadLine[7];
    addToDrawList(mLoadLineLines[4]);

    for (int i = 5; i < 8; i++) {
      mLoadLineLines[i] = new TLine();
      mLoadLineLines[i].mColor = Color.darkGray;
      mLoadLineLines[i].mSize = 4;
      mLoadLineLines[i].mStartPoint = mLoadLine[i];
      mLoadLineLines[i].mEndPoint = mLoadLine[(i + 1) % 8];
      addToDrawList(mLoadLineLines[i]);
    }
    mLoadLineLines[4].mColor = g.mGreen;
    mLoadLineLines[0].mColor = g.mGreen;

    for (int i = 0; i < 8; i++) {
      addToDrawList(mLoadLine[i]);
    }


    // forcePolyLine for E-F
/*    newLine = new TLineForcePoly() {
      public void update() {
        super.update();
        boolean sgn1 = mLoadLine[4].y >= mLoadLine[7].y;
        boolean sgn2 = mNodes[4].y >= mNodes[5].y;

        if (mLoadLine[4].y == mLoadLine[7].y)
          mMembers[16].mForcePolyMember.mCharacter = TLineForcePoly.NONE;
        else if (sgn1 == sgn2)
          mMembers[16].mForcePolyMember.mCharacter = TLineForcePoly.TENSILE;
        else
          mMembers[16].mForcePolyMember.mCharacter = TLineForcePoly.COMPRESSIVE;
      }
    };
    newLine.mStartPoint = mLoadLine[4];
    newLine.mEndPoint = mLoadLine[7];
    newLine.mMemberStart = mNodes[4];
    newLine.mMemberEnd = mNodes[5];
    mMembers[16].mForcePolyMember = newLine;
    addToUpdateList(newLine);*/
  }

  private void makeForcePolygon() {
    TPointForcePoly newNode;

    // 1
    newNode = new TPointForcePoly();
    mForcePolyNodes[0] = newNode;
    newNode.mMember1 = mMembers[8];
    newNode.mMember1ForceBegin = mLoadLine[5];
    newNode.mMember2 = mMembers[1];
    newNode.mMember2ForceBegin = mLoadLine[0];
    newNode.mLabel = "1";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    addToDrawList(newNode);

    // 2
    newNode = new TPointForcePoly();
    mForcePolyNodes[1] = newNode;
    newNode.mMember1 = mMembers[9];
    newNode.mMember1ForceBegin = mLoadLine[6];
    newNode.mMember2 = mMembers[2];
    newNode.mMember2ForceBegin = mForcePolyNodes[0];
    newNode.mLabel = "2";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = 14;
    newNode.dragAlso(mLoadLine[0]);

    addToDrawList(newNode);

    // 3
    newNode = new TPointForcePoly();
    mForcePolyNodes[2] = newNode;
    newNode.mMember1 = mMembers[10];
    newNode.mMember1ForceBegin = mLoadLine[7];
    newNode.mMember2 = mMembers[3];
    newNode.mMember2ForceBegin = mForcePolyNodes[1];
    newNode.mLabel = "3";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    addToDrawList(newNode);

    // 6
    newNode = new TPointForcePoly();
    mForcePolyNodes[3] = newNode;
    newNode.mMember1 = mMembers[12];
    newNode.mMember1ForceBegin = mLoadLine[2];
    newNode.mMember2 = mMembers[4];
    newNode.mMember2ForceBegin = mLoadLine[0];
    newNode.mLabel = "6";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    addToDrawList(newNode);

    // 5
    newNode = new TPointForcePoly();
    mForcePolyNodes[4] = newNode;
    newNode.mMember1 = mMembers[11];
    newNode.mMember1ForceBegin = mLoadLine[3];
    newNode.mMember2 = mMembers[5];
    newNode.mMember2ForceBegin = mForcePolyNodes[3];
    newNode.mLabel = "5";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    addToDrawList(newNode);

    // 4
    newNode = new TPointForcePoly();
    mForcePolyNodes[5] = newNode;
    newNode.mMember1 = mMembers[10];
    newNode.mMember1ForceBegin = mLoadLine[4];
    newNode.mMember2 = mMembers[6];
    newNode.mMember2ForceBegin = mForcePolyNodes[4];
    newNode.mLabel = "4";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    addToDrawList(newNode);

/*
    for (int i = 0; i < 6; i++) {
      addToUpdateList(mLoadLine[i]);
    }
    for (int i = 1; i < 9; i++) {
      addToUpdateList(mForcePolyNodes[i]);
    }
*/
// Force poly lines

    TLineForcePoly newLine;

    for (int i = 0; i < 15; i++) {
      mForcePolyLines[i] = new TLineForcePoly();
      mForcePolyLines[i].dragAlso(mLoadLine[0]);
    }

    // a1
    newLine = mForcePolyLines[0];
    newLine.mStartPoint = mLoadLine[0];
    newLine.mEndPoint = mForcePolyNodes[0];
    newLine.mMemberStart = mNodes[7];
    newLine.mMemberEnd = mNodes[1];
    mMembers[1].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // h1
    newLine = mForcePolyLines[1];
    newLine.mStartPoint = mLoadLine[5];
    newLine.mEndPoint = mForcePolyNodes[0];
    newLine.mMemberStart = mNodes[8];
    newLine.mMemberEnd = mNodes[7];
    mMembers[8].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // g2
    newLine = mForcePolyLines[2];
    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[1];
    newLine.mMemberStart = mNodes[9];
    newLine.mMemberEnd = mNodes[8];
    mMembers[9].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // f3
    newLine = mForcePolyLines[3];
    newLine.mStartPoint = mLoadLine[7];
    newLine.mEndPoint = mForcePolyNodes[2];
    newLine.mMemberStart = mNodes[10];
    newLine.mMemberEnd = mNodes[9];
    mMembers[10].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // a6
    newLine = mForcePolyLines[4];
    newLine.mStartPoint = mLoadLine[0];
    newLine.mEndPoint = mForcePolyNodes[3];
    newLine.mMemberStart = mNodes[1];
    newLine.mMemberEnd = mNodes[12];
    mMembers[4].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // c6
    newLine = mForcePolyLines[5];
    newLine.mStartPoint = mLoadLine[2];
    newLine.mEndPoint = mForcePolyNodes[3];
    newLine.mMemberStart = mNodes[12];
    newLine.mMemberEnd = mNodes[11];
    mMembers[12].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // d5
    newLine = mForcePolyLines[6];
    newLine.mStartPoint = mLoadLine[3];
    newLine.mEndPoint = mForcePolyNodes[4];
    newLine.mMemberStart = mNodes[11];
    newLine.mMemberEnd = mNodes[10];
    mMembers[11].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // e4
    newLine = mForcePolyLines[7];
    newLine.mStartPoint = mLoadLine[4];
    newLine.mEndPoint = mForcePolyNodes[5];
    newLine.mMemberStart = mNodes[10];
    newLine.mMemberEnd = mNodes[9];
    //mMembers[11].mForcePolyMember = newLine;   // Must split middle member
    addToDrawList(newLine);


    // 1-6
    newLine = mForcePolyLines[8];
    newLine.mStartPoint = mForcePolyNodes[0];
    newLine.mEndPoint = mForcePolyNodes[3];
    newLine.mMemberStart = mNodes[5];
    newLine.mMemberEnd = mNodes[1];
    mMembers[13].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // 2-5
    newLine = mForcePolyLines[9];
    newLine.mStartPoint = mForcePolyNodes[1];
    newLine.mEndPoint = mForcePolyNodes[4];
    newLine.mMemberStart = mNodes[5];
    newLine.mMemberEnd = mNodes[2];
    mMembers[14].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // 3-4
    newLine = mForcePolyLines[10];
    newLine.mStartPoint = mForcePolyNodes[2];
    newLine.mEndPoint = mForcePolyNodes[5];
    newLine.mMemberStart = mNodes[5];
    newLine.mMemberEnd = mNodes[3];
    mMembers[15].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // 1-2
    newLine = mForcePolyLines[11];
    newLine.mStartPoint = mForcePolyNodes[0];
    newLine.mEndPoint = mForcePolyNodes[1];
    newLine.mMemberStart = mNodes[8];
    newLine.mMemberEnd = mNodes[2];
    mMembers[2].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // 2-3
    newLine = mForcePolyLines[12];
    newLine.mStartPoint = mForcePolyNodes[1];
    newLine.mEndPoint = mForcePolyNodes[2];
    newLine.mMemberStart = mNodes[9];
    newLine.mMemberEnd = mNodes[3];
    mMembers[3].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // 5-6
    newLine = mForcePolyLines[13];
    newLine.mStartPoint = mForcePolyNodes[4];
    newLine.mEndPoint = mForcePolyNodes[3];
    newLine.mMemberStart = mNodes[11];
    newLine.mMemberEnd = mNodes[2];
    mMembers[5].mForcePolyMember = newLine;
    addToDrawList(newLine);

    // 4-5
    newLine = mForcePolyLines[14];
    newLine.mStartPoint = mForcePolyNodes[5];
    newLine.mEndPoint = mForcePolyNodes[4];
    newLine.mMemberStart = mNodes[10];
    newLine.mMemberEnd = mNodes[3];
    mMembers[6].mForcePolyMember = newLine;
    addToDrawList(newLine);
  }

  private void makeTriangleLabels() {
    TTextTriangle text = new TTextTriangle();
    text.mText = "3";
    text.p1 = mNodes[3];
    text.p2 = mNodes[4];
    text.p3 = mNodes[9];
    text.xOff = -2;
    addToDrawList(text);

    text = new TTextTriangle();
    text.mText = "4";
    text.p1 = mNodes[3];
    text.p2 = mNodes[4];
    text.p3 = mNodes[10];
    text.xOff = -4;
    addToDrawList(text);
  }

  public static final int REPORT_X_START = 40;
  public static final int REPORT_Y_START = 500;
  public static final int REPORT_LINE_SPACE = 17;
  public static final int REPORT_COLUMN_SPACE = 110;

  private void makeReport() {
    int x = REPORT_X_START;
    int y = REPORT_Y_START;

    TText newText = new TText();
    newText.mSize = 18;
    newText.mText = "Cables";
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
    newReport.mForcePolyLine = mForcePolyLines[11];
    newReport.mPrefix = "1-2 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[12];
    newReport.mPrefix = "2-3 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[14];
    newReport.mPrefix = "4-5 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[13];
    newReport.mPrefix = "5-6 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;


    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[4];
    newReport.mPrefix = "A6 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;



//--- deck forces
    x += REPORT_COLUMN_SPACE;
    y = REPORT_Y_START;

    newText = new TText();
    newText.mSize = 18;
    newText.mText = "Deck";
    newText.x = x;
    newText.y = y;
    addToDrawList(newText);
    y += REPORT_LINE_SPACE * 1.2;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[1];
    newReport.mPrefix = "H1 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[2];
    newReport.mPrefix = "G2 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[3];
    newReport.mPrefix = "F3 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[7];
    newReport.mPrefix = "E4 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[6];
    newReport.mPrefix = "D5 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[5];
    newReport.mPrefix = "C6 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

//--- tower forces
    x = REPORT_X_START + REPORT_COLUMN_SPACE * 3;
    y = REPORT_Y_START;

    newText = new TText();
    newText.mSize = 18;
    newText.mText = "Tower";
    newText.x = x;
    newText.y = y;
    addToDrawList(newText);
    y += REPORT_LINE_SPACE * 1.2;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[8];
    newReport.mPrefix = "1-6 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[9];
    newReport.mPrefix = "2-5 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[10];
    newReport.mPrefix = "3-4 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;
  }

  public static final int BUTTON_START_X = 20;
  public static final int BUTTON_START_Y = 70;
  public static final int BUTTON_Y_OFFSET = 30;

  private void makeButtons() {
    int x = BUTTON_START_X;
    int y = BUTTON_START_Y;

    TButton mOriginalPosButton = new TButton("Return To Starting Position");
    mOriginalPosButton.x = x;
    mOriginalPosButton.y = y;
    mOriginalPosButton.mWidth = 170;
    mOriginalPosButton.mHeight = 20;
    addToDrawList(mOriginalPosButton);
    mOriginalPosButton.mAction = new TAction() {
      public void run() {
        JobMoveViewToOrigin moveView = new JobMoveViewToOrigin(g);
        moveView.mView = mUpdateCanvas;
        g.mTimer.addJob(moveView);

        JobMovePointToStart newJob;
        mDeckHorizCheck.mSelected = false;
        mDeckHoriz = false;
        mTowerVertCheck.mSelected = false;
        mTowerVert = false;

        newJob = new JobMovePointToStart(g);
        newJob.mMovePoint = mLoadLine[0];
        g.mTimer.addJob(newJob);

        newJob = new JobMovePointToStart(g);
        newJob.mMovePoint = mNodes[0];
        g.mTimer.addJob(newJob);

        newJob = new JobMovePointToStart(g);
        newJob.mMovePoint = mNodes[5];
        g.mTimer.addJob(newJob);

        JobMovePointToStart cascadedJob;
        JobMovePointToStart lastCascadedJob;

        cascadedJob = new JobMovePointToStart(null, g);
        cascadedJob.mMovePoint = mNodes[6];
        g.mTimer.addJob(cascadedJob);

        for (int i = 1; i < 5; i++) {
          lastCascadedJob = cascadedJob;
          cascadedJob = new JobMovePointToStart(lastCascadedJob, g);
          cascadedJob.mMovePoint = mNodes[i];
          cascadedJob.forceSelectMovingNode = true;
          g.mTimer.addJob(cascadedJob);
        }
      }
    };
    y += BUTTON_Y_OFFSET;

    mDeckHorizCheck = new TButton("Keep Deck Horizontal");
    mDeckHorizCheck.x = x;
    mDeckHorizCheck.y = y;
    mDeckHorizCheck.mWidth = 170;
    mDeckHorizCheck.mHeight = 20;
    mDeckHorizCheck.mIsToggle = true;
    addToDrawList(mDeckHorizCheck);
    mDeckHorizCheck.mAction = new TAction() {
      public void run() {
        mDeckHoriz = mDeckHorizCheck.mSelected;
        repaint();
      }
    };
    y += BUTTON_Y_OFFSET;

    mTowerVertCheck = new TButton("Keep Tower Vertical");
    mTowerVertCheck.x = x;
    mTowerVertCheck.y = y;
    mTowerVertCheck.mWidth = 170;
    mTowerVertCheck.mHeight = 20;
    mTowerVertCheck.mIsToggle = true;
    addToDrawList(mTowerVertCheck);
    mTowerVertCheck.mAction = new TAction() {
      public void run() {
        mTowerVert = mTowerVertCheck.mSelected;
        //repaint();
        if (mUpdateCanvas != null)
          mUpdateCanvas.globalUpdate();
        repaint();
      }
    };
    y += BUTTON_Y_OFFSET;

    mHarpButton = new TButton("Harp Configuration");
    mHarpButton.x = x;
    mHarpButton.y = y;
    mHarpButton.mWidth = 170;
    mHarpButton.mHeight = 20;
    addToDrawList(mHarpButton);
    mHarpButton.mAction = new TAction() {
      public void run() {
        JobMovePointTo newJob = new JobMovePointTo(null, g);
        newJob.forceSelectMovingNode = true;
        newJob.mMovePoint = mNodes[2];
        newJob.xDst = mHarpPoints[0].x;
        newJob.yDst = mHarpPoints[0].y;
        g.mTimer.addJob(newJob);

        JobMovePointTo newJob2 = new JobMovePointTo(newJob, g);
        newJob2.forceSelectMovingNode = true;
        newJob2.mMovePoint = mNodes[3];
        newJob2.xDst = mHarpPoints[1].x;
        newJob2.yDst = mHarpPoints[1].y;
        g.mTimer.addJob(newJob2);

        repaint();
      }
    };
    y += BUTTON_Y_OFFSET;

    mFanButton = new TButton("Fan Configuration");
    mFanButton.x = x;
    mFanButton.y = y;
    mFanButton.mWidth = 170;
    mFanButton.mHeight = 20;
    addToDrawList(mFanButton);
    mFanButton.mAction = new TAction() {
      public void run() {
        JobMovePointTo newJob = new JobMovePointTo(null, g);
        newJob.forceSelectMovingNode = true;
        newJob.mMovePoint = mNodes[2];
        newJob.xDst = mNodes[1].x;
        newJob.yDst = mNodes[1].y;
        g.mTimer.addJob(newJob);

        JobMovePointTo newJob2 = new JobMovePointTo(newJob, g);
        newJob2.forceSelectMovingNode = true;
        newJob2.mMovePoint = mNodes[3];
        newJob2.xDst = mNodes[1].x;
        newJob2.yDst = mNodes[1].y;
        g.mTimer.addJob(newJob2);

        repaint();
      }
    };
    y += BUTTON_Y_OFFSET;
  }
}