package truss;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class OverhangApplet extends Applet {
  public static final int APPLET_WIDTH = 760;
  public static final int APPLET_HEIGHT = 800;

  public static final int PANEL_SIZE = 65;
  public static final int TRUSS_X_START = 70;
  public static final int TRUSS_Y_START = 460;
  public final int START_FORCE_LENGTH = 45;
  public static final int LOAD_LINE_START_X = 620;
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

  private NoScrollUpdateCanvas mUpdateCanvas;


  private TPoint LLa;
  private TPoint LLb;
  private TPoint LLc;
  private TPoint LLd;
  private TPoint LLe;
  private TPoint LLf;
  private TPoint LLg;
  private TPoint LLh;
  private TPoint LLi;
  private TPoint LLj;
  private TPoint LLk;
  private TPoint LLl;


  TButton loadsVertCheck;
  TButton vertsVertCheck;
  TButton topLevelCheck;
  TButton bottomLevelCheck;
  TButton mVertMirrorButton;
  TButton mHorizMirrorButton;
  TButton mPreservePanelSpacing;

  TButton mRaLeftButton;
  TButton mRaRightButton;
  TButton mRbLeftButton;
  TButton mRbRightButton;

  public TTextLength[] mRaReports = new TTextLength[4];
  public TTextLength[] mRbReports = new TTextLength[4];

  public TPin mPin;
  public TRoller mRoller;

  public TPoint mRaNode;
  public TPoint mRbNode;
  public int    mRaPos;
  public int    mRbPos;

  public TPoint[] mTrussNodes = new TPoint[20];
  public TPoint[] mForceTails = new TPoint[10];
  public TArrow[] mLoads = new TArrow[10];
  public TLineMember[] mMembers = new TLineMember[37];
  public TPoint mRbTail;
  public TReaction mRb;
  public TPoint mRaTail;
  public TArrow mRa;
  public TPoint[] mLoadLine = new TPoint[12];
  public TPointForcePoly mForcePolyNodes[] = new TPointForcePoly[19];  // We don't allocate zero. That way the array index can correspond to the area's actual number
  public TLineForcePoly mForcePolyLines[] = new TLineForcePoly[37];

//  private Point dragPoint = new Point();
  private float forceDx[] = new float[10];

  public boolean mLoadsVertical = false;
  public boolean mVerticalsVertical = false;
  public boolean mBottomLevel = false;
  public boolean mTopLevel = false;
  public boolean mVertMirror = false;
  public boolean mHorizMirror = false;
  public boolean preservePanelSpacing = false;

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
    makeNodes();
    makeMembers();
    makeLoads();
    addNodes();
    makeRb();
    makeLoadLine();
    makeRa();
    makeForcePolygon();
    makeSupports();
    setRPos(0,0);
    makeTriangleLabels();
    makeText();
    makeReport();

    addToDrawListOnly(mRaLeftButton);
    addToDrawListOnly(mRaRightButton);
    addToDrawListOnly(mRbLeftButton);
    addToDrawListOnly(mRbRightButton);

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
        if (preservePanelSpacing) {
          for (int i = 2; i < 20; i += 2) {
            mTrussNodes[i].x = mTrussNodes[0].x + (i / 2) * PANEL_SIZE;
            if (this.g.selectedEntity == mForceTails[i/2])
              forceDx[i/2 - 1] = mForceTails[i/2].x - mTrussNodes[i].x;
            else
              mForceTails[i/2].x = mTrussNodes[i].x + forceDx[i/2 - 1];
          }
        }

        for (int i = 0; i < 20; i++) {
          mTrussNodes[i].mControlPoint = true;
          mTrussNodes[i].mSelectable = true;
          mTrussNodes[i].mSize = TPoint.DEFAULT_SIZE;
        }

        if (mLoadsVertical) {
          for (int i = 0; i < 10; i++) {
            mForceTails[i].x = mLoads[i].mEndPoint.x;
          }
        }

        if (mVerticalsVertical) {
          mTrussNodes[1].x = mTrussNodes[0].x;
          mTrussNodes[3].x = mTrussNodes[2].x;
          mTrussNodes[5].x = mTrussNodes[4].x;
          mTrussNodes[7].x = mTrussNodes[6].x;
          mTrussNodes[9].x = mTrussNodes[8].x;
          mTrussNodes[11].x = mTrussNodes[10].x;
          mTrussNodes[13].x = mTrussNodes[12].x;
          mTrussNodes[15].x = mTrussNodes[14].x;
          mTrussNodes[17].x = mTrussNodes[16].x;
          mTrussNodes[19].x = mTrussNodes[18].x;
        }

        if (mBottomLevel) {
          mTrussNodes[3].y = mTrussNodes[1].y;
          mTrussNodes[5].y = mTrussNodes[1].y;
          mTrussNodes[7].y = mTrussNodes[1].y;
          mTrussNodes[9].y = mTrussNodes[1].y;
          mTrussNodes[11].y = mTrussNodes[1].y;
          mTrussNodes[13].y = mTrussNodes[1].y;
          mTrussNodes[15].y = mTrussNodes[1].y;
          mTrussNodes[17].y = mTrussNodes[1].y;
          mTrussNodes[19].y = mTrussNodes[1].y;
        }

        if (mTopLevel) {
          float oldY;
          oldY = mTrussNodes[2].y;
          mTrussNodes[2].y = mTrussNodes[0].y;
          mForceTails[1].y += mTrussNodes[2].y - oldY;

          oldY = mTrussNodes[4].y;
          mTrussNodes[4].y = mTrussNodes[0].y;
          mForceTails[2].y += mTrussNodes[4].y - oldY;

          oldY = mTrussNodes[6].y;
          mTrussNodes[6].y = mTrussNodes[0].y;
          mForceTails[3].y += mTrussNodes[6].y - oldY;

          oldY = mTrussNodes[8].y;
          mTrussNodes[8].y = mTrussNodes[0].y;
          mForceTails[4].y += mTrussNodes[8].y - oldY;

         oldY = mTrussNodes[10].y;
          mTrussNodes[10].y = mTrussNodes[0].y;
          mForceTails[5].y += mTrussNodes[10].y - oldY;

          oldY = mTrussNodes[12].y;
          mTrussNodes[12].y = mTrussNodes[0].y;
          mForceTails[6].y += mTrussNodes[12].y - oldY;

          oldY = mTrussNodes[14].y;
          mTrussNodes[14].y = mTrussNodes[0].y;
          mForceTails[7].y += mTrussNodes[14].y - oldY;

          oldY = mTrussNodes[16].y;
          mTrussNodes[16].y = mTrussNodes[0].y;
          mForceTails[8].y += mTrussNodes[16].y - oldY;

          oldY = mTrussNodes[18].y;
          mTrussNodes[18].y = mTrussNodes[0].y;
          mForceTails[9].y += mTrussNodes[18].y - oldY;
        }

        if (mHorizMirror) {
          for (int i = 10; i < 20; i++) {
            mTrussNodes[i].mControlPoint = false;
            mTrussNodes[i].mSelectable = false;
            mTrussNodes[i].mSize = 8;
          }

          float centerX = (mTrussNodes[8].x + mTrussNodes[10].x) / 2;
          float oldX, oldY;

          oldX = mTrussNodes[10].x;
          oldY = mTrussNodes[10].y;
          mTrussNodes[10].x = centerX + (centerX - mTrussNodes[8].x);
          mTrussNodes[10].y = mTrussNodes[8].y;
          mForceTails[5].x += mTrussNodes[10].x - oldX;
          mForceTails[5].y += mTrussNodes[10].y - oldY;



          oldX = mTrussNodes[12].x;
          oldY = mTrussNodes[12].y;
          mTrussNodes[12].x = centerX + (centerX - mTrussNodes[6].x);
          mTrussNodes[12].y = mTrussNodes[6].y;
          mForceTails[6].x += mTrussNodes[12].x - oldX;
          mForceTails[6].y += mTrussNodes[12].y - oldY;

          oldX = mTrussNodes[14].x;
          oldY = mTrussNodes[14].y;
          mTrussNodes[14].x = centerX + (centerX - mTrussNodes[4].x);
          mTrussNodes[14].y = mTrussNodes[4].y;
          mForceTails[7].x += mTrussNodes[14].x - oldX;
          mForceTails[7].y += mTrussNodes[14].y - oldY;

          oldX = mTrussNodes[16].x;
          oldY = mTrussNodes[16].y;
          mTrussNodes[16].x = centerX + (centerX - mTrussNodes[2].x);
          mTrussNodes[16].y = mTrussNodes[2].y;
          mForceTails[8].x += mTrussNodes[16].x - oldX;
          mForceTails[8].y += mTrussNodes[16].y - oldY;

          oldX = mTrussNodes[18].x;
          oldY = mTrussNodes[18].y;
          mTrussNodes[18].x = centerX + (centerX - mTrussNodes[0].x);
          mTrussNodes[18].y = mTrussNodes[0].y;
          mForceTails[9].x += mTrussNodes[18].x - oldX;
          mForceTails[9].y += mTrussNodes[18].y - oldY;


          mTrussNodes[11].x = centerX + (centerX - mTrussNodes[9].x);
          mTrussNodes[11].y = mTrussNodes[9].y;
          mTrussNodes[13].x = centerX + (centerX - mTrussNodes[7].x);
          mTrussNodes[13].y = mTrussNodes[7].y;
          mTrussNodes[15].x = centerX + (centerX - mTrussNodes[5].x);
          mTrussNodes[15].y = mTrussNodes[5].y;
          mTrussNodes[17].x = centerX + (centerX - mTrussNodes[3].x);
          mTrussNodes[17].y = mTrussNodes[3].y;
          mTrussNodes[19].x = centerX + (centerX - mTrussNodes[1].x);
          mTrussNodes[19].y = mTrussNodes[1].y;
        }

        if (mVertMirror) {
    //      mTrussNodes[1].mControlPoint = false;
    //      mTrussNodes[1].mSelectable = false;
    //      mTrussNodes[1].mSize = 8;
          mTrussNodes[3].mControlPoint = false;
          mTrussNodes[3].mSelectable = false;
          mTrussNodes[3].mSize = 8;
          mTrussNodes[5].mControlPoint = false;
          mTrussNodes[5].mSelectable = false;
          mTrussNodes[5].mSize = 8;
          mTrussNodes[7].mControlPoint = false;
          mTrussNodes[7].mSelectable = false;
          mTrussNodes[7].mSize = 8;
          mTrussNodes[9].mControlPoint = false;
          mTrussNodes[9].mSelectable = false;
          mTrussNodes[9].mSize = 8;
          mTrussNodes[11].mControlPoint = false;
          mTrussNodes[11].mSelectable = false;
          mTrussNodes[11].mSize = 8;
          mTrussNodes[13].mControlPoint = false;
          mTrussNodes[13].mSelectable = false;
          mTrussNodes[13].mSize = 8;
          mTrussNodes[15].mControlPoint = false;
          mTrussNodes[15].mSelectable = false;
          mTrussNodes[15].mSize = 8;
          mTrussNodes[17].mControlPoint = false;
          mTrussNodes[17].mSelectable = false;
          mTrussNodes[17].mSize = 8;
          mTrussNodes[19].mControlPoint = false;
          mTrussNodes[19].mSelectable = false;
          mTrussNodes[19].mSize = 8;
          float centerY = (mTrussNodes[0].y + mTrussNodes[1].y) / 2;
          //float centerY = mTrussNodes[0].y;
    //      mTrussNodes[11].y = mTrussNodes[0].y;

    //      mTrussNodes[1].y = centerY + (centerY - mTrussNodes[0].y);
          mTrussNodes[1].x = mTrussNodes[0].x;
          mTrussNodes[3].y = centerY + (centerY - mTrussNodes[2].y);
          mTrussNodes[3].x = mTrussNodes[2].x;
          mTrussNodes[5].y = centerY + (centerY - mTrussNodes[4].y);
          mTrussNodes[5].x = mTrussNodes[4].x;
          mTrussNodes[7].y = centerY + (centerY - mTrussNodes[6].y);
          mTrussNodes[7].x = mTrussNodes[6].x;
          mTrussNodes[9].y = centerY + (centerY - mTrussNodes[8].y);
          mTrussNodes[9].x = mTrussNodes[8].x;
          mTrussNodes[11].y = centerY + (centerY - mTrussNodes[10].y);
          mTrussNodes[11].x = mTrussNodes[10].x;
          mTrussNodes[13].y = centerY + (centerY - mTrussNodes[12].y);
          mTrussNodes[13].x = mTrussNodes[12].x;
          mTrussNodes[15].y = centerY + (centerY - mTrussNodes[14].y);
          mTrussNodes[15].x = mTrussNodes[14].x;
          mTrussNodes[17].y = centerY + (centerY - mTrussNodes[16].y);
          mTrussNodes[17].x = mTrussNodes[16].x;
          mTrussNodes[19].y = centerY + (centerY - mTrussNodes[18].y);
          mTrussNodes[19].x = mTrussNodes[18].x;
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
    title.mText = "Overhanging Truss";
    title.mSize = 24;
    title.x = 20;
    title.y = 40;
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
    formDiag.mBasePoint = mTrussNodes[11];
    formDiag.mXOffset = -100;
    formDiag.mYOffset = 60;
    formDiag.mSize = 20;
    formDiag.mText = "Form Diagram";
    addToDrawList(formDiag);
  }

  private void makeNodes() {
    int x = TRUSS_X_START;
    for (int i = 0; i < 20; i++) {

      if (i % 2 == 1) {
        mTrussNodes[i] = new TPoint(x, TRUSS_Y_START);
        x += PANEL_SIZE;
      }
      else {
        mTrussNodes[i] = new TPoint(x, TRUSS_Y_START - PANEL_SIZE);
      }
    }

    x = TRUSS_X_START;
    for (int i = 0; i < 10; i++) {
      mForceTails[i] = new TPoint(x, TRUSS_Y_START - PANEL_SIZE - START_FORCE_LENGTH);
      x += PANEL_SIZE;
    }

    // Add dragging relationships
    mTrussNodes[0].dragAlso(mForceTails[0]);
    mTrussNodes[2].dragAlso(mForceTails[1]);
    mTrussNodes[4].dragAlso(mForceTails[2]);
    mTrussNodes[6].dragAlso(mForceTails[3]);
    mTrussNodes[8].dragAlso(mForceTails[4]);
    mTrussNodes[10].dragAlso(mForceTails[5]);
    mTrussNodes[12].dragAlso(mForceTails[6]);
    mTrussNodes[14].dragAlso(mForceTails[7]);
    mTrussNodes[16].dragAlso(mForceTails[8]);
    mTrussNodes[18].dragAlso(mForceTails[9]);
  }

  private void addNodes() {
    for (int i = 0; i < 20; i++) {
      addToDrawList(mTrussNodes[i]);
    }
    for (int i = 0; i < 10; i++) {
      addToDrawList(mForceTails[i]);
    }

  }

  private void makeLoads() {
    for(int i = 0; i < 10; i++) {
      mLoads[i] = new TLoad(g);
      addToDrawList(mLoads[i]);
    }
    mLoads[0].mStartPoint = mForceTails[0];
    mLoads[0].mEndPoint = mTrussNodes[0];
    mLoads[1].mStartPoint = mForceTails[1];
    mLoads[1].mEndPoint = mTrussNodes[2];
    mLoads[2].mStartPoint = mForceTails[2];
    mLoads[2].mEndPoint = mTrussNodes[4];
    mLoads[3].mStartPoint = mForceTails[3];
    mLoads[3].mEndPoint = mTrussNodes[6];
    mLoads[4].mStartPoint = mForceTails[4];
    mLoads[4].mEndPoint = mTrussNodes[8];
    mLoads[5].mStartPoint = mForceTails[5];
    mLoads[5].mEndPoint = mTrussNodes[10];
    mLoads[6].mStartPoint = mForceTails[6];
    mLoads[6].mEndPoint = mTrussNodes[12];
    mLoads[7].mStartPoint = mForceTails[7];
    mLoads[7].mEndPoint = mTrussNodes[14];
    mLoads[8].mStartPoint = mForceTails[8];
    mLoads[8].mEndPoint = mTrussNodes[16];
    mLoads[9].mStartPoint = mForceTails[9];
    mLoads[9].mEndPoint = mTrussNodes[18];
  }

  private void makeMembers() {
    for(int i = 0; i < 37; i++) {
      mMembers[i] = new TLineMember(g);
      for (int j = 0; j < 20; j++) {
        mMembers[i].dragAlso(mTrussNodes[j]);
      }

      addToDrawList(mMembers[i]);
    }
    mMembers[0].mStartPoint = mTrussNodes[0];
    mMembers[0].mEndPoint = mTrussNodes[1];
    mMembers[0].mLabelXOff = -20;
    mMembers[0].mLabelYOff = 0;
    mMembers[0].mLabel = "A";

    mMembers[1].mStartPoint = mTrussNodes[0];
    mMembers[1].mEndPoint = mTrussNodes[2];
    mMembers[1].mLabel = "B";

    mMembers[2].mStartPoint = mTrussNodes[0];
    mMembers[2].mEndPoint = mTrussNodes[3];
    mMembers[3].mStartPoint = mTrussNodes[1];
    mMembers[3].mEndPoint = mTrussNodes[3];
    mMembers[4].mStartPoint = mTrussNodes[2];
    mMembers[4].mEndPoint = mTrussNodes[3];
    mMembers[5].mStartPoint = mTrussNodes[2];
    mMembers[5].mEndPoint = mTrussNodes[4];
    mMembers[5].mLabel = "C";

    mMembers[6].mStartPoint = mTrussNodes[2];
    mMembers[6].mEndPoint = mTrussNodes[5];
    mMembers[7].mStartPoint = mTrussNodes[3];
    mMembers[7].mEndPoint = mTrussNodes[5];
    mMembers[8].mStartPoint = mTrussNodes[4];
    mMembers[8].mEndPoint = mTrussNodes[5];
    mMembers[9].mStartPoint = mTrussNodes[4];
    mMembers[9].mEndPoint = mTrussNodes[6];
    mMembers[9].mLabel = "D";

    mMembers[10].mStartPoint = mTrussNodes[4];
    mMembers[10].mEndPoint = mTrussNodes[7];
    mMembers[11].mStartPoint = mTrussNodes[5];
    mMembers[11].mEndPoint = mTrussNodes[7];
    mMembers[12].mStartPoint = mTrussNodes[6];
    mMembers[12].mEndPoint = mTrussNodes[7];
    mMembers[13].mStartPoint = mTrussNodes[6];
    mMembers[13].mEndPoint = mTrussNodes[8];
    mMembers[13].mLabel = "E";

    mMembers[14].mStartPoint = mTrussNodes[6];
    mMembers[14].mEndPoint = mTrussNodes[9];
    mMembers[15].mStartPoint = mTrussNodes[7];
    mMembers[15].mEndPoint = mTrussNodes[9];
    mMembers[16].mStartPoint = mTrussNodes[8];
    mMembers[16].mEndPoint = mTrussNodes[9];
    mMembers[17].mStartPoint = mTrussNodes[8];
    mMembers[17].mEndPoint = mTrussNodes[10];
    mMembers[17].mLabel = "F";

    mMembers[18].mStartPoint = mTrussNodes[8];
    mMembers[18].mEndPoint = mTrussNodes[11];
    mMembers[19].mStartPoint = mTrussNodes[9];
    mMembers[19].mEndPoint = mTrussNodes[11];
    mMembers[19].mLabelXOff = 0;
    mMembers[19].mLabelYOff = 30;
    mMembers[19].mLabel = "L";

    mMembers[20].mStartPoint = mTrussNodes[10];
    mMembers[20].mEndPoint = mTrussNodes[11];
    mMembers[21].mStartPoint = mTrussNodes[10];
    mMembers[21].mEndPoint = mTrussNodes[12];
    mMembers[21].mLabel = "G";

    mMembers[22].mStartPoint = mTrussNodes[10];
    mMembers[22].mEndPoint = mTrussNodes[13];
    mMembers[23].mStartPoint = mTrussNodes[11];
    mMembers[23].mEndPoint = mTrussNodes[13];
    mMembers[24].mStartPoint = mTrussNodes[12];
    mMembers[24].mEndPoint = mTrussNodes[13];
    mMembers[25].mStartPoint = mTrussNodes[12];
    mMembers[25].mEndPoint = mTrussNodes[14];
    mMembers[25].mLabel = "H";

    mMembers[26].mStartPoint = mTrussNodes[12];
    mMembers[26].mEndPoint = mTrussNodes[15];
    mMembers[27].mStartPoint = mTrussNodes[13];
    mMembers[27].mEndPoint = mTrussNodes[15];
    mMembers[28].mStartPoint = mTrussNodes[14];
    mMembers[28].mEndPoint = mTrussNodes[15];
    mMembers[29].mStartPoint = mTrussNodes[14];
    mMembers[29].mEndPoint = mTrussNodes[16];
    mMembers[29].mLabel = "I";

    mMembers[30].mStartPoint = mTrussNodes[14];
    mMembers[30].mEndPoint = mTrussNodes[17];
    mMembers[31].mStartPoint = mTrussNodes[15];
    mMembers[31].mEndPoint = mTrussNodes[17];
    mMembers[32].mStartPoint = mTrussNodes[16];
    mMembers[32].mEndPoint = mTrussNodes[17];
    mMembers[33].mStartPoint = mTrussNodes[16];
    mMembers[33].mEndPoint = mTrussNodes[18];
    mMembers[33].mLabel = "J";

    mMembers[34].mStartPoint = mTrussNodes[16];
    mMembers[34].mEndPoint = mTrussNodes[19];
    mMembers[35].mStartPoint = mTrussNodes[17];
    mMembers[35].mEndPoint = mTrussNodes[19];
    mMembers[36].mStartPoint = mTrussNodes[18];
    mMembers[36].mEndPoint = mTrussNodes[19];
    mMembers[36].mLabel = "K";
    mMembers[36].mLabelXOff = 20;
    mMembers[36].mLabelYOff = 0;

  }

  private void makeRb() {
    mRbTail = new TPoint() {
      float totalMoment;
      float pDist;

      public void update() {
        totalMoment = 0.0f;
        for (int i = 0; i < 10; i++) {
          totalMoment += mLoads[i].moment(mRaNode);
        }
        totalMoment *= TLine.CCW(mRbNode.x, mRbNode.y - 10,
                           mRbNode.x, mRbNode.y,
                           mRaNode.x, mRaNode.y);

        float pDist = TLine.perpDist(mRbNode.x, mRbNode.y,
                                      mRbNode.x, mRbNode.y + 10,
                                      mRaNode.x, mRaNode.y);
        if (Math.abs(pDist) < 0.1f)
          pDist = 1.0f;
        totalMoment /= pDist;
        x = mRbNode.x;
        y = mRbNode.y + Math.abs(totalMoment) + mRb.ARROW_OFFSET;
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
    mRb.mEndPoint = mRbNode;
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
        mDir = Util.direction(mLoadLine[11].x, mLoadLine[11].y,
                              mLoadLine[0].x, mLoadLine[0].y );
        mDist = Util.distance(mLoadLine[11].x, mLoadLine[11].y,
                              mLoadLine[0].x, mLoadLine[0].y ) + mRa.ARROW_OFFSET;
        x = mRaNode.x - mDist * (float)Math.cos(mDir);
        y = mRaNode.y - mDist * (float)Math.sin(mDir);
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
    mRa.mEndPoint = mRaNode;
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

  private void setRPos(int RaPos, int RbPos) {
    mRaPos = RaPos;
    mRbPos = RbPos;

    mRbNode = mTrussNodes[19 - (2 * RbPos)];
    mRb.mEndPoint = mRbNode;
    mRoller.mPoint = mRbNode;

    mRaNode = mTrussNodes[1 + (2 * RaPos)];
    mRa.mEndPoint = mRaNode;
    mPin.mPoint = mRaNode;

    if (RaPos == 0)
      mRaLeftButton.mInvisible = true;
    else
      mRaLeftButton.mInvisible = false;
    if (RaPos == 4)
      mRaRightButton.mInvisible = true;
    else
      mRaRightButton.mInvisible = false;
    if (RbPos == 0)
      mRbRightButton.mInvisible = true;
    else
      mRbRightButton.mInvisible = false;
    if (RbPos == 4)
      mRbLeftButton.mInvisible = true;
    else
      mRbLeftButton.mInvisible = false;
  }

  private TLine mLoadLineLines[] = new TLine[12];
  private void makeLoadLine() {
    TPointTranslate newPoint;

    mLoadLine[0] = new TPoint(LOAD_LINE_START_X, LOAD_LINE_START_Y);
    TPoint prevPoint = mLoadLine[0];
    mLoadLine[0].mLabel = "a";
    mLoadLine[0].mLabelXOff = 14;
    mLoadLine[0].mLabelYOff = 0;
    mLoadLine[0].mSize = 6;

    for (int i = 1; i < 11; i++) {
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
    mLoadLine[11] = newPoint;
    newPoint.mBasePoint = prevPoint;
    newPoint.mVectorStart = mRb.mArrowTail;
    newPoint.mVectorEnd = mRb.mArrowHead;
    newPoint.mLabel = "l";
    newPoint.mLabelXOff = 14;
    newPoint.mLabelYOff = 0;
    newPoint.mSize = 7;
    newPoint.dragAlso(mLoadLine[0]);

    for (int i = 0; i < 12; i++) {
      mLoadLineLines[i] = new TLine();
      mLoadLineLines[i].mColor = Color.darkGray;
      mLoadLineLines[i].mSize = 4;
      mLoadLineLines[i].mStartPoint = mLoadLine[i];
      mLoadLineLines[i].mEndPoint = mLoadLine[(i + 1) % 12];
      mLoadLineLines[i].dragAlso(mLoadLine[0]);
      addToDrawList(mLoadLineLines[i]);
    }
    mLoadLineLines[10].mColor = g.mGreen;
    mLoadLineLines[11].mColor = g.mGreen;

     for (int i = 0; i < 12; i++) {
      addToDrawList(mLoadLine[i]);
    }

    setLoadLineAliases();
  }

  private void setLoadLineAliases() {
    LLa = mLoadLine[0];
    LLb = mLoadLine[1];
    LLc = mLoadLine[2];
    LLd = mLoadLine[3];
    LLe = mLoadLine[4];
    LLf = mLoadLine[5];
    LLg = mLoadLine[6];
    LLh = mLoadLine[7];
    LLi = mLoadLine[8];
    LLj = mLoadLine[9];
    LLk = mLoadLine[10];
    LLl = mLoadLine[11];
  }

  private void changeForcePolygon() {
    if (mRaPos == 0) {
      mForcePolyNodes[1].mMember2ForceBegin = LLl;
      mForcePolyNodes[3].mMember2ForceBegin = LLl;
      mForcePolyNodes[5].mMember2ForceBegin = LLl;
      mForcePolyNodes[7].mMember2ForceBegin = LLl;

      mForcePolyLines[3].mStartPoint = LLl;
      mForcePolyLines[7].mStartPoint = LLl;
      mForcePolyLines[11].mStartPoint = LLl;
      mForcePolyLines[15].mStartPoint = LLl;

      if (mRaReports[0] != null) {
        mRaReports[0].mPrefix = "L1 = ";
        mRaReports[1].mPrefix = "L3 = ";
        mRaReports[2].mPrefix = "L5 = ";
        mRaReports[3].mPrefix = "L7 = ";
      }
    }
    else if (mRaPos == 1) {
      mForcePolyNodes[1].mMember2ForceBegin = LLa;
      mForcePolyNodes[3].mMember2ForceBegin = LLl;
      mForcePolyNodes[5].mMember2ForceBegin = LLl;
      mForcePolyNodes[7].mMember2ForceBegin = LLl;

      mForcePolyLines[3].mStartPoint = LLa;
      mForcePolyLines[7].mStartPoint = LLl;
      mForcePolyLines[11].mStartPoint = LLl;
      mForcePolyLines[15].mStartPoint = LLl;

      if (mRaReports[0] != null) {
        mRaReports[0].mPrefix = "A1 = ";
        mRaReports[1].mPrefix = "L3 = ";
        mRaReports[2].mPrefix = "L5 = ";
        mRaReports[3].mPrefix = "L7 = ";
      }
    }
    else if (mRaPos == 2) {
      mForcePolyNodes[1].mMember2ForceBegin = LLa;
      mForcePolyNodes[3].mMember2ForceBegin = LLa;
      mForcePolyNodes[5].mMember2ForceBegin = LLl;
      mForcePolyNodes[7].mMember2ForceBegin = LLl;

      mForcePolyLines[3].mStartPoint = LLa;
      mForcePolyLines[7].mStartPoint = LLa;
      mForcePolyLines[11].mStartPoint = LLl;
      mForcePolyLines[15].mStartPoint = LLl;

      if (mRaReports[0] != null) {
        mRaReports[0].mPrefix = "A1 = ";
        mRaReports[1].mPrefix = "A3 = ";
        mRaReports[2].mPrefix = "L5 = ";
        mRaReports[3].mPrefix = "L7 = ";
      }
    }
    else if (mRaPos == 3) {
      mForcePolyNodes[1].mMember2ForceBegin = LLa;
      mForcePolyNodes[3].mMember2ForceBegin = LLa;
      mForcePolyNodes[5].mMember2ForceBegin = LLa;
      mForcePolyNodes[7].mMember2ForceBegin = LLl;

      mForcePolyLines[3].mStartPoint = LLa;
      mForcePolyLines[7].mStartPoint = LLa;
      mForcePolyLines[11].mStartPoint = LLa;
      mForcePolyLines[15].mStartPoint = LLl;

      if (mRaReports[0] != null) {
        mRaReports[0].mPrefix = "A1 = ";
        mRaReports[1].mPrefix = "A3 = ";
        mRaReports[2].mPrefix = "A5 = ";
        mRaReports[3].mPrefix = "L7 = ";
      }
    }
    else {
      mForcePolyNodes[1].mMember2ForceBegin = LLa;
      mForcePolyNodes[3].mMember2ForceBegin = LLa;
      mForcePolyNodes[5].mMember2ForceBegin = LLa;
      mForcePolyNodes[7].mMember2ForceBegin = LLa;

      mForcePolyLines[3].mStartPoint = LLa;
      mForcePolyLines[7].mStartPoint = LLa;
      mForcePolyLines[11].mStartPoint = LLa;
      mForcePolyLines[15].mStartPoint = LLa;

      if (mRaReports[0] != null) {
        mRaReports[0].mPrefix = "A1 = ";
        mRaReports[1].mPrefix = "A3 = ";
        mRaReports[2].mPrefix = "A5 = ";
        mRaReports[3].mPrefix = "A7 = ";
      }
    }

    if (mRbPos == 0) {
      mForcePolyNodes[17].mMember2ForceBegin = LLl;
      mForcePolyNodes[15].mMember2ForceBegin = LLl;
      mForcePolyNodes[13].mMember2ForceBegin = LLl;
      mForcePolyNodes[11].mMember2ForceBegin = LLl;

      mForcePolyLines[35].mStartPoint = LLl;
      mForcePolyLines[31].mStartPoint = LLl;
      mForcePolyLines[27].mStartPoint = LLl;
      mForcePolyLines[23].mStartPoint = LLl;

      if (mRaReports[0] != null) {
        mRbReports[0].mPrefix = "L17 = ";
        mRbReports[1].mPrefix = "L15 = ";
        mRbReports[2].mPrefix = "L13 = ";
        mRbReports[3].mPrefix = "L11 = ";
      }
    }
    else if (mRbPos == 1) {
      mForcePolyNodes[17].mMember2ForceBegin = LLk;
      mForcePolyNodes[15].mMember2ForceBegin = LLl;
      mForcePolyNodes[13].mMember2ForceBegin = LLl;
      mForcePolyNodes[11].mMember2ForceBegin = LLl;

      mForcePolyLines[35].mStartPoint = LLk;
      mForcePolyLines[31].mStartPoint = LLl;
      mForcePolyLines[27].mStartPoint = LLl;
      mForcePolyLines[23].mStartPoint = LLl;

      if (mRbReports[0] != null) {
        mRbReports[0].mPrefix = "K17 = ";
        mRbReports[1].mPrefix = "L15 = ";
        mRbReports[2].mPrefix = "L13 = ";
        mRbReports[3].mPrefix = "L11 = ";
      }
    }
    else if (mRbPos == 2) {
      mForcePolyNodes[17].mMember2ForceBegin = LLk;
      mForcePolyNodes[15].mMember2ForceBegin = LLk;
      mForcePolyNodes[13].mMember2ForceBegin = LLl;
      mForcePolyNodes[11].mMember2ForceBegin = LLl;

      mForcePolyLines[35].mStartPoint = LLk;
      mForcePolyLines[31].mStartPoint = LLk;
      mForcePolyLines[27].mStartPoint = LLl;
      mForcePolyLines[23].mStartPoint = LLl;

      if (mRbReports[0] != null) {
        mRbReports[0].mPrefix = "K17 = ";
        mRbReports[1].mPrefix = "K15 = ";
        mRbReports[2].mPrefix = "L13 = ";
        mRbReports[3].mPrefix = "L11 = ";
      }
    }
    else if (mRbPos == 3) {
      mForcePolyNodes[17].mMember2ForceBegin = LLk;
      mForcePolyNodes[15].mMember2ForceBegin = LLk;
      mForcePolyNodes[13].mMember2ForceBegin = LLk;
      mForcePolyNodes[11].mMember2ForceBegin = LLl;

      mForcePolyLines[35].mStartPoint = LLk;
      mForcePolyLines[31].mStartPoint = LLk;
      mForcePolyLines[27].mStartPoint = LLk;
      mForcePolyLines[23].mStartPoint = LLl;

      if (mRbReports[0] != null) {
        mRbReports[0].mPrefix = "K17 = ";
        mRbReports[1].mPrefix = "K15 = ";
        mRbReports[2].mPrefix = "K13 = ";
        mRbReports[3].mPrefix = "L11 = ";
      }
    }
    else {
      mForcePolyNodes[17].mMember2ForceBegin = LLk;
      mForcePolyNodes[15].mMember2ForceBegin = LLk;
      mForcePolyNodes[13].mMember2ForceBegin = LLk;
      mForcePolyNodes[11].mMember2ForceBegin = LLk;

      mForcePolyLines[35].mStartPoint = LLk;
      mForcePolyLines[31].mStartPoint = LLk;
      mForcePolyLines[27].mStartPoint = LLk;
      mForcePolyLines[23].mStartPoint = LLk;

      if (mRbReports[0] != null) {
        mRbReports[0].mPrefix = "K17 = ";
        mRbReports[1].mPrefix = "K15 = ";
        mRbReports[2].mPrefix = "K13 = ";
        mRbReports[3].mPrefix = "K11 = ";
      }
    }
  }

  private void makeForcePolygon() {
    TPointForcePoly newNode;

    newNode = new TPointForcePoly();
    mForcePolyNodes[1] = newNode;
    newNode.mMember1 = mMembers[0];
    newNode.mMember1ForceBegin = LLa;
    newNode.mMember2 = mMembers[1];
    newNode.mLabel = "1";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[2] = newNode;
    newNode.mMember1 = mMembers[2];
    newNode.mMember1ForceBegin = mForcePolyNodes[1];
    newNode.mMember2 = mMembers[1];
    newNode.mMember2ForceBegin = LLb;
    newNode.mLabel = "2";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[3] = newNode;
    newNode.mMember1 = mMembers[4];
    newNode.mMember1ForceBegin = mForcePolyNodes[2];
    newNode.mMember2 = mMembers[7];
    newNode.mLabel = "3";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[4] = newNode;
    newNode.mMember1 = mMembers[6];
    newNode.mMember1ForceBegin = mForcePolyNodes[3];
    newNode.mMember2 = mMembers[5];
    newNode.mMember2ForceBegin = LLc;
    newNode.mLabel = "4";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[5] = newNode;
    newNode.mMember1 = mMembers[8];
    newNode.mMember1ForceBegin = mForcePolyNodes[4];
    newNode.mMember2 = mMembers[11];
    newNode.mLabel = "5";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[6] = newNode;
    newNode.mMember1 = mMembers[10];
    newNode.mMember1ForceBegin = mForcePolyNodes[5];
    newNode.mMember2 = mMembers[9];
    newNode.mMember2ForceBegin = LLd;
    newNode.mLabel = "6";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[7] = newNode;
    newNode.mMember1 = mMembers[12];
    newNode.mMember1ForceBegin = mForcePolyNodes[6];
    newNode.mMember2 = mMembers[15];
    newNode.mLabel = "7";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[8] = newNode;
    newNode.mMember1 = mMembers[14];
    newNode.mMember1ForceBegin = mForcePolyNodes[7];
    newNode.mMember2 = mMembers[13];
    newNode.mMember2ForceBegin = LLe;
    newNode.mLabel = "8";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[9] = newNode;
    newNode.mMember1 = mMembers[16];
    newNode.mMember1ForceBegin = mForcePolyNodes[8];
    newNode.mMember2 = mMembers[19];
    newNode.mMember2ForceBegin = LLl;
    newNode.mLabel = "9";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[10] = newNode;
    newNode.mMember1 = mMembers[18];
    newNode.mMember1ForceBegin = mForcePolyNodes[9];
    newNode.mMember2 = mMembers[17];
    newNode.mMember2ForceBegin = LLf;
    newNode.mLabel = "10";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[11] = newNode;
    newNode.mMember1 = mMembers[20];
    newNode.mMember1ForceBegin = mForcePolyNodes[10];
    newNode.mMember2 = mMembers[23];
    newNode.mLabel = "11";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[12] = newNode;
    newNode.mMember1 = mMembers[22];
    newNode.mMember1ForceBegin = mForcePolyNodes[11];
    newNode.mMember2 = mMembers[21];
    newNode.mMember2ForceBegin = LLg;
    newNode.mLabel = "12";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[13] = newNode;
    newNode.mMember1 = mMembers[24];
    newNode.mMember1ForceBegin = mForcePolyNodes[12];
    newNode.mMember2 = mMembers[27];
    newNode.mLabel = "13";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[14] = newNode;
    newNode.mMember1 = mMembers[26];
    newNode.mMember1ForceBegin = mForcePolyNodes[13];
    newNode.mMember2 = mMembers[25];
    newNode.mMember2ForceBegin = LLh;
    newNode.mLabel = "14";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[15] = newNode;
    newNode.mMember1 = mMembers[28];
    newNode.mMember1ForceBegin = mForcePolyNodes[14];
    newNode.mMember2 = mMembers[31];
    newNode.mLabel = "15";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[16] = newNode;
    newNode.mMember1 = mMembers[30];
    newNode.mMember1ForceBegin = mForcePolyNodes[15];
    newNode.mMember2 = mMembers[29];
    newNode.mMember2ForceBegin = LLi;
    newNode.mLabel = "16";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[17] = newNode;
    newNode.mMember1 = mMembers[32];
    newNode.mMember1ForceBegin = mForcePolyNodes[16];
    newNode.mMember2 = mMembers[35];
    newNode.mLabel = "17";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);

    newNode = new TPointForcePoly();
    mForcePolyNodes[18] = newNode;
    newNode.mMember1 = mMembers[34];
    newNode.mMember1ForceBegin = mForcePolyNodes[17];
    newNode.mMember2 = mMembers[33];
    newNode.mMember2ForceBegin = LLj;
    newNode.mLabel = "18";
    newNode.mLabelXOff = -14;
    newNode.mLabelYOff = -8;
    newNode.dragAlso(mLoadLine[0]);


    for (int i = 0; i < 12; i++) {
      addToUpdateList(mLoadLine[i]);
    }
    for (int i = 1; i < 19; i++) {
      addToUpdateList(mForcePolyNodes[i]);
    }

// Force poly lines

    TLineForcePoly newLine;

    for (int i = 0; i < 37; i++) {
      mForcePolyLines[i] = new TLineForcePoly();
      mForcePolyLines[i].dragAlso(mLoadLine[0]);
      mMembers[i].mForcePolyMember = mForcePolyLines[i];
    }

    // 1-2
    newLine = mForcePolyLines[2];
    newLine.mStartPoint = mForcePolyNodes[1];
    newLine.mEndPoint = mForcePolyNodes[2];
    newLine.mMemberStart = mTrussNodes[3];
    newLine.mMemberEnd = mTrussNodes[0];
    addToDrawList(newLine);

    // 2-3
    newLine = mForcePolyLines[4];
    newLine.mStartPoint = mForcePolyNodes[2];
    newLine.mEndPoint = mForcePolyNodes[3];
    newLine.mMemberStart = mTrussNodes[3];
    newLine.mMemberEnd = mTrussNodes[2];
    addToDrawList(newLine);

    // 3-4
    newLine = mForcePolyLines[6];
    newLine.mStartPoint = mForcePolyNodes[3];
    newLine.mEndPoint = mForcePolyNodes[4];
    newLine.mMemberStart = mTrussNodes[5];
    newLine.mMemberEnd = mTrussNodes[2];
    addToDrawList(newLine);

    // 4-5
    newLine = mForcePolyLines[8];
    newLine.mStartPoint = mForcePolyNodes[4];
    newLine.mEndPoint = mForcePolyNodes[5];
    newLine.mMemberStart = mTrussNodes[5];
    newLine.mMemberEnd = mTrussNodes[4];
    addToDrawList(newLine);

    // 5-6
    newLine = mForcePolyLines[10];
    newLine.mStartPoint = mForcePolyNodes[5];
    newLine.mEndPoint = mForcePolyNodes[6];
    newLine.mMemberStart = mTrussNodes[7];
    newLine.mMemberEnd = mTrussNodes[4];
    addToDrawList(newLine);

    // 6-7
    newLine = mForcePolyLines[12];
    newLine.mStartPoint = mForcePolyNodes[6];
    newLine.mEndPoint = mForcePolyNodes[7];
    newLine.mMemberStart = mTrussNodes[7];
    newLine.mMemberEnd = mTrussNodes[6];
    addToDrawList(newLine);

    // 7-8
    newLine = mForcePolyLines[14];
    newLine.mStartPoint = mForcePolyNodes[7];
    newLine.mEndPoint = mForcePolyNodes[8];
    newLine.mMemberStart = mTrussNodes[9];
    newLine.mMemberEnd = mTrussNodes[6];
    addToDrawList(newLine);

    // 8-9
    newLine = mForcePolyLines[16];
    newLine.mStartPoint = mForcePolyNodes[8];
    newLine.mEndPoint = mForcePolyNodes[9];
    newLine.mMemberStart = mTrussNodes[9];
    newLine.mMemberEnd = mTrussNodes[8];
    addToDrawList(newLine);

    // 9-10
    newLine = mForcePolyLines[18];
    newLine.mStartPoint = mForcePolyNodes[9];
    newLine.mEndPoint = mForcePolyNodes[10];
    newLine.mMemberStart = mTrussNodes[11];
    newLine.mMemberEnd = mTrussNodes[8];
    addToDrawList(newLine);

    // 10-11
    newLine = mForcePolyLines[20];
    newLine.mStartPoint = mForcePolyNodes[10];
    newLine.mEndPoint = mForcePolyNodes[11];
    newLine.mMemberStart = mTrussNodes[11];
    newLine.mMemberEnd = mTrussNodes[10];
    addToDrawList(newLine);

    // 11-12
    newLine = mForcePolyLines[22];
    newLine.mStartPoint = mForcePolyNodes[11];
    newLine.mEndPoint = mForcePolyNodes[12];
    newLine.mMemberStart = mTrussNodes[13];
    newLine.mMemberEnd = mTrussNodes[10];
    addToDrawList(newLine);

    // 12-13
    newLine = mForcePolyLines[24];
    newLine.mStartPoint = mForcePolyNodes[12];
    newLine.mEndPoint = mForcePolyNodes[13];
    newLine.mMemberStart = mTrussNodes[13];
    newLine.mMemberEnd = mTrussNodes[12];
    addToDrawList(newLine);

    // 13-14
    newLine = mForcePolyLines[26];
    newLine.mStartPoint = mForcePolyNodes[13];
    newLine.mEndPoint = mForcePolyNodes[14];
    newLine.mMemberStart = mTrussNodes[15];
    newLine.mMemberEnd = mTrussNodes[12];
    addToDrawList(newLine);

    // 14-15
    newLine = mForcePolyLines[28];
    newLine.mStartPoint = mForcePolyNodes[14];
    newLine.mEndPoint = mForcePolyNodes[15];
    newLine.mMemberStart = mTrussNodes[15];
    newLine.mMemberEnd = mTrussNodes[14];
    addToDrawList(newLine);

    // 15-16
    newLine = mForcePolyLines[30];
    newLine.mStartPoint = mForcePolyNodes[15];
    newLine.mEndPoint = mForcePolyNodes[16];
    newLine.mMemberStart = mTrussNodes[17];
    newLine.mMemberEnd = mTrussNodes[14];
    addToDrawList(newLine);

    // 16-17
    newLine = mForcePolyLines[32];
    newLine.mStartPoint = mForcePolyNodes[16];
    newLine.mEndPoint = mForcePolyNodes[17];
    newLine.mMemberStart = mTrussNodes[17];
    newLine.mMemberEnd = mTrussNodes[16];
    addToDrawList(newLine);

    // 17-18
    newLine = mForcePolyLines[34];
    newLine.mStartPoint = mForcePolyNodes[17];
    newLine.mEndPoint = mForcePolyNodes[18];
    newLine.mMemberStart = mTrussNodes[19];
    newLine.mMemberEnd = mTrussNodes[16];
    addToDrawList(newLine);

 //--
    // a1
    newLine = mForcePolyLines[0];
    newLine.mStartPoint = LLa;
    newLine.mEndPoint = mForcePolyNodes[1];
    newLine.mMemberStart = mTrussNodes[1];
    newLine.mMemberEnd = mTrussNodes[0];
    addToDrawList(newLine);

    // b2
    newLine = mForcePolyLines[1];
    newLine.mStartPoint = LLb;
    newLine.mEndPoint = mForcePolyNodes[2];
    newLine.mMemberStart = mTrussNodes[0];
    newLine.mMemberEnd = mTrussNodes[2];
    addToDrawList(newLine);

    // c4
    newLine = mForcePolyLines[5];
    newLine.mStartPoint = LLc;
    newLine.mEndPoint = mForcePolyNodes[4];
    newLine.mMemberStart = mTrussNodes[2];
    newLine.mMemberEnd = mTrussNodes[4];
    addToDrawList(newLine);

    // d6
    newLine = mForcePolyLines[9];
    newLine.mStartPoint = LLd;
    newLine.mEndPoint = mForcePolyNodes[6];
    newLine.mMemberStart = mTrussNodes[4];
    newLine.mMemberEnd = mTrussNodes[6];
    addToDrawList(newLine);

    // e8
    newLine = mForcePolyLines[13];
    newLine.mStartPoint = LLe;
    newLine.mEndPoint = mForcePolyNodes[8];
    newLine.mMemberStart = mTrussNodes[6];
    newLine.mMemberEnd = mTrussNodes[8];
    addToDrawList(newLine);

    // f10
    newLine = mForcePolyLines[17];
    newLine.mStartPoint = LLf;
    newLine.mEndPoint = mForcePolyNodes[10];
    newLine.mMemberStart = mTrussNodes[8];
    newLine.mMemberEnd = mTrussNodes[10];
    addToDrawList(newLine);

    // g12
    newLine = mForcePolyLines[21];
    newLine.mStartPoint = LLg;
    newLine.mEndPoint = mForcePolyNodes[12];
    newLine.mMemberStart = mTrussNodes[10];
    newLine.mMemberEnd = mTrussNodes[12];
    addToDrawList(newLine);

    // h14
    newLine = mForcePolyLines[25];
    newLine.mStartPoint = LLh;
    newLine.mEndPoint = mForcePolyNodes[14];
    newLine.mMemberStart = mTrussNodes[12];
    newLine.mMemberEnd = mTrussNodes[14];
    addToDrawList(newLine);

    // I16
    newLine = mForcePolyLines[29];
    newLine.mStartPoint = LLi;
    newLine.mEndPoint = mForcePolyNodes[16];
    newLine.mMemberStart = mTrussNodes[14];
    newLine.mMemberEnd = mTrussNodes[16];
    addToDrawList(newLine);

    // J18
    newLine = mForcePolyLines[33];
    newLine.mStartPoint = LLj;
    newLine.mEndPoint = mForcePolyNodes[18];
    newLine.mMemberStart = mTrussNodes[16];
    newLine.mMemberEnd = mTrussNodes[18];
    addToDrawList(newLine);

    // K18
    newLine = mForcePolyLines[36];
    newLine.mStartPoint = LLk;
    newLine.mEndPoint = mForcePolyNodes[18];
    newLine.mMemberStart = mTrussNodes[18];
    newLine.mMemberEnd = mTrussNodes[19];
    addToDrawList(newLine);

    // K/L 17
    newLine = mForcePolyLines[35];
//    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[17];
    newLine.mMemberStart = mTrussNodes[19];
    newLine.mMemberEnd = mTrussNodes[17];
    addToDrawList(newLine);

    // K/L 15
    newLine = mForcePolyLines[31];
//    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[15];
    newLine.mMemberStart = mTrussNodes[17];
    newLine.mMemberEnd = mTrussNodes[15];
    addToDrawList(newLine);

    // K/L 13
    newLine = mForcePolyLines[27];
//    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[13];
    newLine.mMemberStart = mTrussNodes[15];
    newLine.mMemberEnd = mTrussNodes[13];
    addToDrawList(newLine);

    // K/L 11
    newLine = mForcePolyLines[23];
//    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[11];
    newLine.mMemberStart = mTrussNodes[13];
    newLine.mMemberEnd = mTrussNodes[11];
    addToDrawList(newLine);

    // L 9
    newLine = mForcePolyLines[19];
    newLine.mStartPoint = LLl;
    newLine.mEndPoint = mForcePolyNodes[9];
    newLine.mMemberStart = mTrussNodes[11];
    newLine.mMemberEnd = mTrussNodes[9];
    addToDrawList(newLine);

    // A/L 7
    newLine = mForcePolyLines[15];
//    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[7];
    newLine.mMemberStart = mTrussNodes[9];
    newLine.mMemberEnd = mTrussNodes[7];
    addToDrawList(newLine);

    // A/L 5
    newLine = mForcePolyLines[11];
//    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[5];
    newLine.mMemberStart = mTrussNodes[7];
    newLine.mMemberEnd = mTrussNodes[5];
    addToDrawList(newLine);

    // A/L 3
    newLine = mForcePolyLines[7];
//    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[3];
    newLine.mMemberStart = mTrussNodes[5];
    newLine.mMemberEnd = mTrussNodes[3];
    addToDrawList(newLine);

    // A/L 1
    newLine = mForcePolyLines[3];
//    newLine.mStartPoint = mLoadLine[6];
    newLine.mEndPoint = mForcePolyNodes[1];
    newLine.mMemberStart = mTrussNodes[3];
    newLine.mMemberEnd = mTrussNodes[1];
    addToDrawList(newLine);

    changeForcePolygon();

    for (int i = 0; i < 12; i++) {
      addToDrawListOnly(mLoadLine[i]);
    }
    for (int i = 1; i < 19; i++) {
      addToDrawListOnly(mForcePolyNodes[i]);
    }
  }

  private void makeTriangleLabels() {
    TTextTriangle newLabel;

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[0];
    newLabel.p2 = mTrussNodes[1];
    newLabel.p3 = mTrussNodes[3];
    newLabel.mText = "1";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[0];
    newLabel.p2 = mTrussNodes[2];
    newLabel.p3 = mTrussNodes[3];
    newLabel.mText = "2";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[2];
    newLabel.p2 = mTrussNodes[3];
    newLabel.p3 = mTrussNodes[5];
    newLabel.mText = "3";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[2];
    newLabel.p2 = mTrussNodes[4];
    newLabel.p3 = mTrussNodes[5];
    newLabel.mText = "4";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[4];
    newLabel.p2 = mTrussNodes[5];
    newLabel.p3 = mTrussNodes[7];
    newLabel.mText = "5";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[4];
    newLabel.p2 = mTrussNodes[6];
    newLabel.p3 = mTrussNodes[7];
    newLabel.mText = "6";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[6];
    newLabel.p2 = mTrussNodes[7];
    newLabel.p3 = mTrussNodes[9];
    newLabel.mText = "7";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[6];
    newLabel.p2 = mTrussNodes[8];
    newLabel.p3 = mTrussNodes[9];
    newLabel.mText = "8";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[8];
    newLabel.p2 = mTrussNodes[9];
    newLabel.p3 = mTrussNodes[11];
    newLabel.mText = "9";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[8];
    newLabel.p2 = mTrussNodes[10];
    newLabel.p3 = mTrussNodes[11];
    newLabel.mText = "10";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[10];
    newLabel.p2 = mTrussNodes[11];
    newLabel.p3 = mTrussNodes[13];
    newLabel.mText = "11";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[10];
    newLabel.p2 = mTrussNodes[12];
    newLabel.p3 = mTrussNodes[13];
    newLabel.mText = "12";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[12];
    newLabel.p2 = mTrussNodes[13];
    newLabel.p3 = mTrussNodes[15];
    newLabel.mText = "13";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[12];
    newLabel.p2 = mTrussNodes[14];
    newLabel.p3 = mTrussNodes[15];
    newLabel.mText = "14";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[14];
    newLabel.p2 = mTrussNodes[15];
    newLabel.p3 = mTrussNodes[17];
    newLabel.mText = "15";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[14];
    newLabel.p2 = mTrussNodes[16];
    newLabel.p3 = mTrussNodes[17];
    newLabel.mText = "16";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[16];
    newLabel.p2 = mTrussNodes[17];
    newLabel.p3 = mTrussNodes[19];
    newLabel.mText = "17";
    addToDrawList(newLabel);

    newLabel = new TTextTriangle();
    newLabel.p1 = mTrussNodes[16];
    newLabel.p2 = mTrussNodes[18];
    newLabel.p3 = mTrussNodes[19];
    newLabel.mText = "18";
    addToDrawList(newLabel);
  }

  private void makeSupports() {
    mPin = new TPin(mRaNode);
    addToDrawList(mPin);
    mRoller = new TRoller(mRbNode);
    addToDrawList(mRoller);
  }

  public static final int REPORT_X_START = 100;
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
    newReport.mForcePolyLine = mForcePolyLines[1];
    newReport.mPrefix = "B2 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[5];
    newReport.mPrefix = "C4 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[9];
    newReport.mPrefix = "D6 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[13];
    newReport.mPrefix = "E8 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[17];
    newReport.mPrefix = "F10 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[21];
    newReport.mPrefix = "G12 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[25];
    newReport.mPrefix = "H14 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[29];
    newReport.mPrefix = "I16 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[33];
    newReport.mPrefix = "J18 = ";
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
    mRaReports[0] = newReport;
    newReport.mForcePolyLine = mForcePolyLines[3];
    newReport.mPrefix = "L1 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    mRaReports[1] = newReport;
    newReport.mForcePolyLine = mForcePolyLines[7];
    newReport.mPrefix = "L3 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    mRaReports[2] = newReport;
    newReport.mForcePolyLine = mForcePolyLines[11];
    newReport.mPrefix = "L5 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    mRaReports[3] = newReport;
    newReport.mForcePolyLine = mForcePolyLines[15];
    newReport.mPrefix = "L7 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[19];
    newReport.mPrefix = "L9 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    mRbReports[3] = newReport;
    newReport.mForcePolyLine = mForcePolyLines[23];
    newReport.mPrefix = "L11 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    mRbReports[2] = newReport;
    newReport.mForcePolyLine = mForcePolyLines[27];
    newReport.mPrefix = "L13 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    mRbReports[1] = newReport;
    newReport.mForcePolyLine = mForcePolyLines[31];
    newReport.mPrefix = "L15 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    mRbReports[0] = newReport;
    newReport.mForcePolyLine = mForcePolyLines[35];
    newReport.mPrefix = "L17 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;


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
    newReport.mForcePolyLine = mForcePolyLines[0];
    newReport.mPrefix = "A1 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

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
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[20];
    newReport.mPrefix = "10-11 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[24];
    newReport.mPrefix = "12-13 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[28];
    newReport.mPrefix = "14-15 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[32];
    newReport.mPrefix = "16-17 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[36];
    newReport.mPrefix = "K18 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);

//-------- Diagonals
    x += REPORT_COLUMN_SPACE;
    y = REPORT_Y_START;

    newText = new TText();
    newText.mSize = 18;
    newText.mText = "Diagonals";
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
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[22];
    newReport.mPrefix = "11-12 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[26];
    newReport.mPrefix = "13-14 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[30];
    newReport.mPrefix = "15-16 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
    y += REPORT_LINE_SPACE;

    newReport = new TTextLength(g);
    newReport.mForcePolyLine = mForcePolyLines[34];
    newReport.mPrefix = "17-18 = ";
    newReport.x = x;
    newReport.y = y;
    addToDrawList(newReport);
  }

  public static final int BUTTON_START_X = 20;
  public static final int BUTTON_START_Y = 60;
  public static final int BUTTON_Y_OFFSET = 28;

  private void makeButtons() {
    int x = BUTTON_START_X;
    int y = BUTTON_START_Y;



    mRaLeftButton = new TButton("<<") {
      public void update() {
        mRaLeftButton.x = mRaNode.x - 60;
        mRaLeftButton.y = mRaNode.y + 20;
        super.update();
      }
    };
    mRaLeftButton.x = x;
    mRaLeftButton.y = y;
    mRaLeftButton.mWidth = 30;
    mRaLeftButton.mHeight = 20;
    mRaLeftButton.mPosRelativeTo = GraphicEntity.GLOBAL_RELATIVE;
    addToUpdateList(mRaLeftButton);
    mRaLeftButton.mAction = new TAction() {
      public void run() {
        if (mRaPos > 0) {
          setRPos(mRaPos - 1, mRbPos);
          changeForcePolygon();
          repaint();
        }
      }
    };
    //y += BUTTON_Y_OFFSET;

    mRaRightButton = new TButton(">>") {
      public void update() {
        mRaRightButton.x = mRaNode.x + 30;
        mRaRightButton.y = mRaNode.y + 20;
        super.update();
      }
    };
    mRaRightButton.x = x;
    mRaRightButton.y = y;
    mRaRightButton.mWidth = 30;
    mRaRightButton.mHeight = 20;
    mRaRightButton.mPosRelativeTo = GraphicEntity.GLOBAL_RELATIVE;
    addToUpdateList(mRaRightButton);
    mRaRightButton.mAction = new TAction() {
      public void run() {
        if (mRaPos < 4) {
          setRPos(mRaPos + 1, mRbPos);
          changeForcePolygon();
          repaint();
        }
      }
    };
    //y += BUTTON_Y_OFFSET;

    mRbLeftButton = new TButton("<<") {
      public void update() {
        mRbLeftButton.x = mRbNode.x - 60;
        mRbLeftButton.y = mRbNode.y + 20;
        super.update();
      }
    };
    mRbLeftButton.x = x;
    mRbLeftButton.y = y;
    mRbLeftButton.mWidth = 30;
    mRbLeftButton.mHeight = 20;
    mRbLeftButton.mPosRelativeTo = GraphicEntity.GLOBAL_RELATIVE;
    addToUpdateList(mRbLeftButton);
    mRbLeftButton.mAction = new TAction() {
      public void run() {
        if (mRbPos < 4) {
          setRPos(mRaPos, mRbPos + 1);
          changeForcePolygon();
          repaint();
        }
      }
    };
    //y += BUTTON_Y_OFFSET;

    mRbRightButton = new TButton(">>") {
      public void update() {
        mRbRightButton.x = mRbNode.x + 30;
        mRbRightButton.y = mRbNode.y + 20;
        super.update();
      }
    };
    mRbRightButton.x = x;
    mRbRightButton.y = y;
    mRbRightButton.mWidth = 30;
    mRbRightButton.mHeight = 20;
    mRbRightButton.mPosRelativeTo = GraphicEntity.GLOBAL_RELATIVE;
    addToUpdateList(mRbRightButton);
    mRbRightButton.mAction = new TAction() {
      public void run() {
        if (mRbPos > 0) {
          setRPos(mRaPos, mRbPos - 1);
          changeForcePolygon();
          repaint();
        }
      }
    };
    //y += BUTTON_Y_OFFSET;

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

        setRPos(0, 0);
        changeForcePolygon();

        JobMovePointToStart newJob;
        mVertMirrorButton.mSelected = false;
        mVertMirror = false;
        mPreservePanelSpacing.mSelected = false;
        preservePanelSpacing = false;

        newJob = new JobMovePointToStart(g);
        newJob.mMovePoint = mLoadLine[0];
        g.mTimer.addJob(newJob);

        for (int i = 0; i < 20; i++) {
          newJob = new JobMovePointToStart(g);
          newJob.mMovePoint = mTrussNodes[i];
          g.mTimer.addJob(newJob);
        }
        for (int i = 0; i < 10; i++) {
          newJob = new JobMovePointToStart(g);
          newJob.mMovePoint = mForceTails[i];
          g.mTimer.addJob(newJob);
        }
//        setRPos(0,0);
//        changeForcePolygon();
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
        JobMovePointToStart newJob;

        float length = mLoads[0].length();
        float dir;
        JobMovePointTo moveToJob;

        for (int i = 1; i < 10; i++) {
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
        for (int i = 0; i < 9; i++) {
          forceDx[i] = mForceTails[i+1].x - mTrussNodes[(i+1)*2].x;
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
        for (int i = 0; i < 9; i++) {
          forceDx[i] = 0;
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
  }
}