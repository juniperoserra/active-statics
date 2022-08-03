package truss;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class BeamLoadApplet extends Applet {
  public static final int APPLET_WIDTH = 820;
  public static final int APPLET_HEIGHT = 680;

  public final int START_FORCE_LENGTH = 165;

  public static final float AREA_LOAD_DIVISOR = 80.0f;
  public static final float AREA_INTEGRATE_DIVISOR = 80.0f;

  private static final Color mVColor = new Color(0x99, 0xCC, 0xFF);
  private static final Color mMColor = new Color(0xFF, 0x99, 0x99);


  public TPoint[] mPointLoadPoints = new TPoint[4];
  public TPoint[] mDistLoadPoints = new TPoint[4];
  public TPoint[] mSlopedLoadPoints = new TPoint[4];

  public TPolygon mPointBeam;
  public TPolygon mDistBeam;
  public TPolygon mSlopedBeam;

  public TPoint  mPaLoadHead;
  public TPoint  mPaLoadTail;
  public TPoint  mPbLoadHead;
  public TPoint  mPbLoadTail;

  public TPoint  mDistLoadTR;
  public TPoint  mDistLoadBR;
  public TPoint  mDistLoadBL;
  public TPoint  mDistLoadTL;

  public TPoint mSlopedLoadTR;
  public TPoint mSlopedLoadBR;
  public TPoint mSlopedLoadBL;

  public TPoint mPointRaHead;
  public TPoint mPointRbHead;
  public TPoint mPointRaTail;
  public TPoint mPointRbTail;
  public TArrow mPa;
  public TArrow mPb;

  public TPoint mDistRaHead;
  public TPoint mDistRbHead;
  public TPoint mDistRaTail;
  public TPoint mDistRbTail;
  public TLine  mDistForce;

  public TPoint mSlopedRaHead;
  public TPoint mSlopedRbHead;
  public TPoint mSlopedRaTail;
  public TPoint mSlopedRbTail;
  public TLine  mSlopedForce;


  public static final int SEGMENT_DIVISIONS = 8;
  private class Segment implements Comparable {
//    boolean exists;
    float startPointForce;
    float xStart;
    float dx;
    float[] yVals = new float[SEGMENT_DIVISIONS + 1];

    public boolean compare(Object obj1, Object obj2) {
      return ((Segment)obj1).xStart < ((Segment)obj2).xStart;
    }
  }
  private Segment[][] pointSegments = new Segment[3][5];
  private Segment[][] distSegments = new Segment[3][5];
  private Segment[][] slopedSegments = new Segment[3][5];

  public int W;
  public int H;

  private Point tempPoint = new Point();
  private int paintedTwice = 0;
  private TButton mCircleLoadButton;
  private TButton mLoadsVertCheck;

  private NoScrollUpdateCanvas mUpdateCanvas;

  public GraphicEntity[] drawList = new GraphicEntity[0];
  public GraphicEntity[] updateList = new GraphicEntity[0];

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

/*  private void makeSupports() {
    TPin pin = new TPin(mTrussNodes[0]);
    addToDrawList(pin);
    TRoller roller = new TRoller(mTrussNodes[2]);
    addToDrawList(roller);
  }*/

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

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 5; j++) {
        pointSegments[i][j] = new Segment();
        distSegments[i][j] = new Segment();
        slopedSegments[i][j] = new Segment();
      }
    }

    makeBeams();
    makeLoads();
    makeSupports();
    makeText();

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
      public void globalUpdate() {
        calcSegments();
      }
      public void predrawGlobalHook(Graphics graphics) {
        drawStuff(graphics);
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


  public void repaint() {
    mUpdateCanvas.repaint();
  }

  public void drawStuff(Graphics graphics) {

/*    System.out.println("Load: ");
    printSegments(slopedSegments[0]);
    System.out.println("V: ");
    printSegments(slopedSegments[1]);
    System.out.println("M: ");
    printSegments(slopedSegments[2]);*/

    drawSegments(graphics, mMColor, BEAM_X_START + BEAM_LENGTH + BEAM_SEPARATION, BEAM_Y_START + 400, distSegments[2]);
    drawSegments(graphics, mMColor, BEAM_X_START, BEAM_Y_START + 400, pointSegments[2]);
    drawSegments(graphics, mMColor, BEAM_X_START + 2 * (BEAM_LENGTH + BEAM_SEPARATION), BEAM_Y_START + 400, slopedSegments[2]);

    drawSegments(graphics, mVColor, BEAM_X_START, BEAM_Y_START + 200, pointSegments[1]);
    drawSegments(graphics, mVColor, BEAM_X_START + BEAM_LENGTH + BEAM_SEPARATION, BEAM_Y_START + 200, distSegments[1]);
    drawSegments(graphics, mVColor, BEAM_X_START + 2 * (BEAM_LENGTH + BEAM_SEPARATION), BEAM_Y_START + 200, slopedSegments[1]);
  }

  private void printSegments(Segment[] segments) {
    for (int i = 0; i < 5; i++) {
      System.out.println("Segment " + i + ":");
      System.out.println("  dx: " + segments[i].dx);
      System.out.println("  Start force: " + segments[i].startPointForce);
      System.out.print("  YVals: " + segments[i].yVals[0]);
      for (int j = 1; j <= SEGMENT_DIVISIONS; j++) {
        System.out.print(", " + segments[i].yVals[j]);
      }
      System.out.println("");
    }
    System.out.println("");
  }

  private void drawSegments(Graphics graphics, Color fillColor, int xStart, int yStart, Segment[] segments) {
    float x = xStart;
    float y = yStart;
    float dx;

    int[] xPoints = new int[4];
    int[] yPoints = new int[4];
    yPoints[0] = (int)yStart;
    yPoints[3] = (int)yStart;

    xPoints[0] = (int)x;
    xPoints[1] = (int)x;
    int skipX = (int)x;
    for (int i = 0; i < 5; i++) {
      dx = segments[i].dx;
      skipX = (int)x;
      for (int j = 0; j < SEGMENT_DIVISIONS; j++) {

        if (i > 0 && segments[i-1].yVals[SEGMENT_DIVISIONS] != segments[i].yVals[0]) {  // Discontinuity
          if (j == 0) {
            xPoints[0]++;
            xPoints[1]++;
            skipX = (int)x;
          }
          else if ((int)x == skipX) {
            xPoints[0]++;
            xPoints[1]++;
          }
        }

        xPoints[2] = (int)(x+dx);
        xPoints[3] = (int)(x+dx);

        if (xPoints[2] < xPoints[0]) xPoints[2] = xPoints[0];
        if (xPoints[3] < xPoints[0]) xPoints[3] = xPoints[0];

        if (i < 4 && j == SEGMENT_DIVISIONS - 1 && segments[i].yVals[SEGMENT_DIVISIONS] != segments[i+1].yVals[0]) {  // Discontinuity
          xPoints[2]++;
          xPoints[3]++;
        }

        yPoints[1] = (int)(y + segments[i].yVals[j]);
        yPoints[2] = (int)(y + segments[i].yVals[j+1]);

        graphics.setColor(fillColor);
        graphics.fillPolygon(xPoints, yPoints,4);

        graphics.setColor(Color.black);
        graphics.drawLine((int)x, yPoints[1], (int)(x+dx), yPoints[2]);
        x += dx;
        xPoints[0] = (int)x;
        xPoints[1] = (int)x;
      }
      if (i < 4)
        graphics.drawLine((int)x, (int)(y + segments[i].yVals[SEGMENT_DIVISIONS]), (int)x, (int)(y + segments[i+1].yVals[0]));
    }
    graphics.drawLine((int)x, (int)y, (int)x, (int)yStart);
    graphics.drawLine((int)xStart, (int)yStart, (int)xStart, (int)(yStart + segments[0].yVals[0]));

    graphics.setColor(Color.black);
    graphics.drawLine((int)xStart, (int)yStart - 1, (int)x, (int)yStart- 1);
    graphics.drawLine((int)xStart, (int)yStart, (int)x, (int)yStart);
    //graphics.drawLine((int)xStart, (int)yStart + 1, (int)x, (int)yStart + 1);
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

  private void integrateSegments(Segment[] source, Segment[] dest, float divisor) {
    float y = 0.0f;
    //float dy = 0.0f;
    for (int i = 0; i < 5; i++) {
      if (source[i].dx <= 0.0f || source[i].startPointForce != 0.0f) {    // Discontinuity.
        y += source[i].startPointForce / ((float)divisor / AREA_LOAD_DIVISOR);
//        for (int j = 0; j <= SEGMENT_DIVISIONS; j++) {
//          dest[i].yVals[j] = y;
//        }
      }
//      else {
        dest[i].yVals[0] = y; // Start where you left off.
        for (int j = 0; j < SEGMENT_DIVISIONS; j++) {
          //y += (source[i].yVals[j]+source[i].yVals[j+1]) / (2*divisor); //(source[i].dx * divisor); //(SEGMENT_DIVISIONS * divisor);
          y += (source[i].yVals[j]+source[i].yVals[j+1]) * source[i].dx / (2*divisor);
          dest[i].yVals[j+1] = y;
        }
//      }
    }
  }

  private void calcSegments() {
    //System.out.println("In calc segs.");

    pointSegments[0][0].xStart = mPointLoadPoints[0].x;
    pointSegments[0][0].startPointForce = 0;

    pointSegments[0][1].xStart = mPaLoadHead.x;
    pointSegments[0][1].startPointForce = mPaLoadHead.y - mPaLoadTail.y;

    pointSegments[0][2].xStart = mPbLoadHead.x;
    pointSegments[0][2].startPointForce = mPbLoadHead.y - mPbLoadTail.y;

    pointSegments[0][3].xStart = mPointRaHead.x;
    pointSegments[0][3].startPointForce = mPointRaHead.y - mPointRaTail.y;

    pointSegments[0][4].xStart = mPointRbHead.x;
    pointSegments[0][4].startPointForce = mPointRbHead.y - mPointRbTail.y;

    QuickSort.getInstance().sort(pointSegments[0], 0, 4, pointSegments[0][0]);
    // Set dx and copy vals to lower ones.
    for (int i = 0; i < 4; i++) {
      pointSegments[0][i].dx = (pointSegments[0][i+1].xStart - pointSegments[0][i].xStart) / SEGMENT_DIVISIONS;
      pointSegments[1][i].xStart = pointSegments[0][i].xStart;
      pointSegments[2][i].xStart = pointSegments[0][i].xStart;
      pointSegments[1][i].dx = pointSegments[0][i].dx;
      pointSegments[2][i].dx = pointSegments[0][i].dx;
    }
    pointSegments[0][4].dx = (mPointLoadPoints[2].x - pointSegments[0][4].xStart) / SEGMENT_DIVISIONS;
    pointSegments[1][4].dx = pointSegments[0][4].dx;
    pointSegments[2][4].dx = pointSegments[0][4].dx;

    // Set yVals for top beam
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < SEGMENT_DIVISIONS + 1; j++) {
        pointSegments[0][i].yVals[j] = 0.0f;
      }
    }
    integrateSegments(pointSegments[0], pointSegments[1], AREA_INTEGRATE_DIVISOR);
    integrateSegments(pointSegments[1], pointSegments[2], AREA_INTEGRATE_DIVISOR);

    // Setup dist segments

    distSegments[0][0].xStart = mDistLoadPoints[0].x;
    distSegments[0][0].startPointForce = 0;

    distSegments[0][1].xStart = mDistLoadBL.x;
    distSegments[0][1].startPointForce = 0;

    distSegments[0][2].xStart = mDistLoadBR.x;
    distSegments[0][2].startPointForce = 0;

    distSegments[0][3].xStart = mDistRaHead.x;
    distSegments[0][3].startPointForce = mDistRaHead.y - mDistRaTail.y;

    distSegments[0][4].xStart = mDistRbHead.x;
    distSegments[0][4].startPointForce = mDistRbHead.y - mDistRbTail.y;

    QuickSort.getInstance().sort(distSegments[0], 0, 4, distSegments[0][0]);

    // Set dx and copy vals to lower ones.
    for (int i = 0; i < 4; i++) {
      distSegments[0][i].dx = (distSegments[0][i+1].xStart - distSegments[0][i].xStart) / SEGMENT_DIVISIONS;
      distSegments[1][i].xStart = distSegments[0][i].xStart;
      distSegments[2][i].xStart = distSegments[0][i].xStart;
      distSegments[1][i].dx = distSegments[0][i].dx;
      distSegments[2][i].dx = distSegments[0][i].dx;
    }
    distSegments[0][4].dx = (mDistLoadPoints[2].x - distSegments[0][4].xStart) / SEGMENT_DIVISIONS;
    distSegments[1][4].dx = distSegments[0][4].dx;
    distSegments[2][4].dx = distSegments[0][4].dx;

    // Set yVals for top beam
    float y = 0.0f;
    float startX, stopX;
    startX = Math.min(mDistLoadBL.x, mDistLoadBR.x);
    stopX = Math.max(mDistLoadBL.x, mDistLoadBR.x);
    for (int i = 0; i < 5; i++) {
      if (distSegments[0][i].xStart >= startX && distSegments[0][i].xStart < stopX)
        y = mDistLoadBL.y - mDistLoadTL.y;
      else
        y = 0.0f;
      for (int j = 0; j < SEGMENT_DIVISIONS+1; j++) {
        distSegments[0][i].yVals[j] = y;
      }
    }
    integrateSegments(distSegments[0], distSegments[1], AREA_INTEGRATE_DIVISOR);
    integrateSegments(distSegments[1], distSegments[2], AREA_INTEGRATE_DIVISOR / 1.5f);


    // Setup sloped segments

    slopedSegments[0][0].xStart = mSlopedLoadPoints[0].x;
    slopedSegments[0][0].startPointForce = 0;

    slopedSegments[0][1].xStart = mSlopedLoadBL.x;
    slopedSegments[0][1].startPointForce = 0;

    slopedSegments[0][2].xStart = mSlopedLoadBR.x;
    slopedSegments[0][2].startPointForce = 0;

    slopedSegments[0][3].xStart = mSlopedRaHead.x;
    slopedSegments[0][3].startPointForce = mSlopedRaHead.y - mSlopedRaTail.y;

    slopedSegments[0][4].xStart = mSlopedRbHead.x;
    slopedSegments[0][4].startPointForce = mSlopedRbHead.y - mSlopedRbTail.y;

    QuickSort.getInstance().sort(slopedSegments[0], 0, 4, slopedSegments[0][0]);
    // Set dx and copy vals to lower ones.
    for (int i = 0; i < 4; i++) {
      slopedSegments[0][i].dx = (slopedSegments[0][i+1].xStart - slopedSegments[0][i].xStart) / SEGMENT_DIVISIONS;
      slopedSegments[1][i].xStart = slopedSegments[0][i].xStart;
      slopedSegments[2][i].xStart = slopedSegments[0][i].xStart;
      slopedSegments[1][i].dx = slopedSegments[0][i].dx;
      slopedSegments[2][i].dx = slopedSegments[0][i].dx;
    }
    slopedSegments[0][4].dx = (mSlopedLoadPoints[2].x - slopedSegments[0][4].xStart) / SEGMENT_DIVISIONS;
    slopedSegments[1][4].dx = slopedSegments[0][4].dx;
    slopedSegments[2][4].dx = slopedSegments[0][4].dx;

    // Set yVals for top beam
    y = 0.0f;
    startX = Math.min(mSlopedLoadBL.x, mSlopedLoadBR.x);
    stopX = Math.max(mSlopedLoadBL.x, mSlopedLoadBR.x);
    float dyTotal = mSlopedLoadBL.y - mSlopedLoadTR.y;
    float startY;
    float slope = 0.0f;
    if (stopX > startX)
      slope = dyTotal / (mSlopedLoadBR.x - mSlopedLoadBL.x);

    boolean flipped = mSlopedLoadBL.x > mSlopedLoadBR.x;

    for (int i = 0; i < 5; i++) {
      for (int j = 0; j <= SEGMENT_DIVISIONS; j++) {
        if (slopedSegments[0][i].xStart >= startX && slopedSegments[0][i].xStart < stopX) {
          if (flipped) {
            //y = (mSlopedLoadBL.y - mSlopedLoadTR.y) * (SEGMENT_DIVISIONS - j) / (float)SEGMENT_DIVISIONS;
            startY = dyTotal + slope * (slopedSegments[0][i].xStart - startX);
          }
          else {
            startY = slope * (slopedSegments[0][i].xStart - startX);
            //y = (mSlopedLoadBL.y - mSlopedLoadTR.y) * (j / (float)SEGMENT_DIVISIONS);
          }
          y = startY + slope * (slopedSegments[0][i].dx * j);
        }
        else
          y = 0.0f;
        slopedSegments[0][i].yVals[j] = y;
      }
    }
    integrateSegments(slopedSegments[0], slopedSegments[1], AREA_INTEGRATE_DIVISOR);
    integrateSegments(slopedSegments[1], slopedSegments[2], AREA_INTEGRATE_DIVISOR / 1.5f);

  }

  //------------------------  SCENE CREATION

private void makeText() {
    TText title = new TText();
    title.mText = "Beam Loading";
    title.mSize = 24;
    title.x = 20;
    title.y = 50;
    title.mPosRelativeTo = GraphicEntity.VIEW_RELATIVE;
    addToDrawList(title);

    title = new TText();
    title.mText = "Point Loads";
    title.mSize = 18;
    title.x = 40;
    title.y = 86;
    addToDrawList(title);

    title = new TText();
    title.mText = "Uniformly Distributed Load";
    title.mSize = 18;
    title.x = 300;
    title.y = 86;
    addToDrawList(title);

    title = new TText();
    title.mText = "Linearly Distributed Load";
    title.mSize = 18;
    title.x = 560;
    title.y = 86;
    addToDrawList(title);

    TText v = new TText();
    v.x = 10;
    v.y = 380;
    v.mSize = 20;
    v.mText = "V";
    addToDrawList(v);

    TText m = new TText();
    m.x = 10;
    m.y = 580;
    m.mSize = 20;
    m.mText = "M";
    addToDrawList(m);
  }

  private static final int BEAM_LENGTH = 180;
  private static final int BEAM_WIDTH = 8;
  private static final int BEAM_X_START = 40;
  private static final int BEAM_Y_START = 220;
  private static final int BEAM_SEPARATION = 80;
  private static final int BEAM_MIN_LENGTH = 20;

  private void makeBeams() {
    int x = BEAM_X_START;
    int y = BEAM_Y_START;

    mPointLoadPoints[0] = new TPoint(x, y);
    mPointLoadPoints[1] = new TPoint(x, y + BEAM_WIDTH);
    mPointLoadPoints[2] = new TPoint(x + BEAM_LENGTH, y + BEAM_WIDTH);
    mPointLoadPoints[3] = new TPoint(x + BEAM_LENGTH, y) {
      public void update() {
        this.y = BEAM_Y_START;
        if (this.x < mPointLoadPoints[0].x + BEAM_MIN_LENGTH) {
          this.x = mPointLoadPoints[0].x + BEAM_MIN_LENGTH;
        }
        mPointLoadPoints[2].x = this.x;
      }
    };
    mPointBeam = new TPolygon();
    for (int i = 0; i < 4; i++) {
      mPointBeam.addPoint(mPointLoadPoints[i]);
    }
    mPointBeam.mOutline = true;

    mPointLoadPoints[3].mSize = 3;
    mPointLoadPoints[3].mControlPoint = true;
    mPointBeam.mColor = Color.lightGray;
    addToDrawList(mPointBeam);
    addToDrawList(mPointLoadPoints[3]);



    x += BEAM_LENGTH + BEAM_SEPARATION;

    mDistLoadPoints[0] = new TPoint(x, y);
    mDistLoadPoints[1] = new TPoint(x, y + BEAM_WIDTH);
    mDistLoadPoints[2] = new TPoint(x + BEAM_LENGTH, y + BEAM_WIDTH);
    mDistLoadPoints[3] = new TPoint(x + BEAM_LENGTH, y) {
    public void update() {
        this.y = BEAM_Y_START;
        if (this.x < mDistLoadPoints[0].x + BEAM_MIN_LENGTH) {
          this.x = mDistLoadPoints[0].x + BEAM_MIN_LENGTH;
        }
        mDistLoadPoints[2].x = this.x;
      }
    };
    mDistBeam = new TPolygon();
    for (int i = 0; i < 4; i++) {
      mDistBeam.addPoint(mDistLoadPoints[i]);
    }
    mDistBeam.mOutline = true;

    mDistLoadPoints[3].mSize = 3;
    mDistLoadPoints[3].mControlPoint = true;
    mDistBeam.mColor = Color.lightGray;
    addToDrawList(mDistBeam);
    addToDrawList(mDistLoadPoints[3]);


    x += BEAM_LENGTH + BEAM_SEPARATION;

    mSlopedLoadPoints[0] = new TPoint(x, y);
    mSlopedLoadPoints[1] = new TPoint(x, y + BEAM_WIDTH);
    mSlopedLoadPoints[2] = new TPoint(x + BEAM_LENGTH, y + BEAM_WIDTH);
    mSlopedLoadPoints[3] = new TPoint(x + BEAM_LENGTH, y) {
      public void update() {
        this.y = BEAM_Y_START;
        if (this.x < mDistLoadPoints[0].x + BEAM_MIN_LENGTH) {
          this.x = mDistLoadPoints[0].x + BEAM_MIN_LENGTH;
        }
        mSlopedLoadPoints[2].x = this.x;
      }
    };
    mSlopedBeam = new TPolygon();
    for (int i = 0; i < 4; i++) {
      mSlopedBeam.addPoint(mSlopedLoadPoints[i]);
    }
    mSlopedBeam.mOutline = true;

    mSlopedLoadPoints[3].mSize = 3;
    mSlopedLoadPoints[3].mControlPoint = true;
    mSlopedBeam.mColor = Color.lightGray;
    addToDrawList(mSlopedBeam);
    addToDrawList(mSlopedLoadPoints[3]);

  }

  private final static int LOAD_TOP = 120;
  private final static int L_OFF     = 40;
  private final static int R_OFF     = 120;

  private void makeLoads() {
    mPaLoadHead = new TPoint(BEAM_X_START + L_OFF, BEAM_Y_START) {
      public void update() {
        y = mPointLoadPoints[0].y;
        if (x < mPointLoadPoints[0].x)
          x = mPointLoadPoints[0].x;
        if (x > mPointLoadPoints[2].x)
          x = mPointLoadPoints[2].x;
      }
    };
    mPaLoadTail = new TPoint(BEAM_X_START + L_OFF, LOAD_TOP) {
      public void update() {
        x = mPaLoadHead.x;
      }
    };
    mPbLoadHead = new TPoint(BEAM_X_START + R_OFF, BEAM_Y_START) {
      public void update() {
        y = mPointLoadPoints[0].y;
        if (x < mPointLoadPoints[0].x)
          x = mPointLoadPoints[0].x;
        if (x > mPointLoadPoints[2].x)
          x = mPointLoadPoints[2].x;
      }
    };
    mPbLoadTail = new TPoint(BEAM_X_START + R_OFF, LOAD_TOP) {
      public void update() {
        x = mPbLoadHead.x;
      }
    };

    mPa = new TArrow();
    mPa.mColor = Color.black;
    mPa.mEndPoint = mPaLoadHead;
    mPa.mStartPoint = mPaLoadTail;
    addToDrawList(mPa);

    mPb = new TArrow();
    mPb.mColor = Color.black;
    mPb.mEndPoint = mPbLoadHead;
    mPb.mStartPoint = mPbLoadTail;
    addToDrawList(mPb);

    addToDrawList(mPaLoadHead);
    addToDrawList(mPaLoadTail);
    addToDrawList(mPbLoadHead);
    addToDrawList(mPbLoadTail);


    mDistLoadTR = new TPoint(BEAM_X_START + BEAM_LENGTH + BEAM_SEPARATION + R_OFF, LOAD_TOP) {
      public void update() {
        x = mDistLoadBR.x;
        if (y > mDistLoadPoints[0].y)
          y = mDistLoadPoints[0].y;
      }
    };
    mDistLoadBR = new TPoint(BEAM_X_START + BEAM_LENGTH + BEAM_SEPARATION + R_OFF, BEAM_Y_START) {
      public void update() {
        y = mDistLoadPoints[0].y;
        if (x < mDistLoadPoints[0].x)
          x = mDistLoadPoints[0].x;
        if (x > mDistLoadPoints[2].x)
          x = mDistLoadPoints[2].x;
      }
    };
    mDistLoadBL = new TPoint(BEAM_X_START + BEAM_LENGTH + BEAM_SEPARATION + L_OFF, BEAM_Y_START) {
      public void update() {
        y = mDistLoadPoints[0].y;
        if (x < mDistLoadPoints[0].x)
          x = mDistLoadPoints[0].x;
        if (x > mDistLoadPoints[2].x)
          x = mDistLoadPoints[2].x;
      }
    };
    mDistLoadTL = new TPoint(BEAM_X_START + BEAM_LENGTH + BEAM_SEPARATION + L_OFF, LOAD_TOP) {
      public void update() {
        y = mDistLoadTR.y;
        x = mDistLoadBL.x;
      }
    };

    TPoint distForceTail = new TPoint() {
      public void update() {
        x = (mDistLoadBL.x + mDistLoadBR.x) / 2;
        y = mDistLoadBL.y;
      }
    };

    TPoint distForceHead = new TPoint() {
      public void update() {
        x = (mDistLoadBL.x + mDistLoadBR.x) / 2;
        y = mDistLoadBL.y + Math.abs(mDistLoadBL.x - mDistLoadBR.x) * (mDistLoadBL.y - mDistLoadTL.y) / AREA_LOAD_DIVISOR;
      }
    };
    mDistForce = new TLine();
    mDistForce.mStartPoint = distForceTail;
    mDistForce.mEndPoint = distForceHead;
    addToUpdateList(distForceTail);
    addToUpdateList(distForceHead);
    addToUpdateList(mDistForce);

    TPolygon distPoly = new TPolygon();
    distPoly.addPoint(mDistLoadTL);
    distPoly.addPoint(mDistLoadBL);
    distPoly.addPoint(mDistLoadBR);
    distPoly.addPoint(mDistLoadTR);
    distPoly.mColor = Color.black;
    //distPoly.mOutline = true;
    addToDrawList(distPoly);


    addToDrawList(mDistLoadBR);
    addToDrawList(mDistLoadTR);
    addToDrawList(mDistLoadBL);
    addToUpdateList(mDistLoadTL);


    mSlopedLoadTR = new TPoint(BEAM_X_START + 2 * (BEAM_LENGTH + BEAM_SEPARATION) + R_OFF, LOAD_TOP) {
      public void update() {
        x = mSlopedLoadBR.x;
        if (y > mSlopedLoadPoints[0].y)
          y = mSlopedLoadPoints[0].y;
      }
    };
    mSlopedLoadBR = new TPoint(BEAM_X_START + 2 * (BEAM_LENGTH + BEAM_SEPARATION) + R_OFF, BEAM_Y_START) {
      public void update() {
        y = mSlopedLoadPoints[0].y;
        if (x < mSlopedLoadPoints[0].x)
          x = mSlopedLoadPoints[0].x;
        if (x > mSlopedLoadPoints[2].x)
          x = mSlopedLoadPoints[2].x;
      }
    };
    mSlopedLoadBL = new TPoint(BEAM_X_START + 2 * (BEAM_LENGTH + BEAM_SEPARATION) + L_OFF, BEAM_Y_START) {
      public void update() {
        y = mSlopedLoadPoints[0].y;
        if (x < mSlopedLoadPoints[0].x)
          x = mSlopedLoadPoints[0].x;
        if (x > mSlopedLoadPoints[2].x)
          x = mSlopedLoadPoints[2].x;
      }
    };

    TPoint slopedForceTail = new TPoint() {
      public void update() {
        x = mSlopedLoadBR.x - (mSlopedLoadBR.x - mSlopedLoadBL.x) / 3;
        y = mSlopedLoadBL.y;
      }
    };

    TPoint slopedForceHead = new TPoint() {
      public void update() {
        x = mSlopedLoadBR.x - (mSlopedLoadBR.x - mSlopedLoadBL.x) / 3;
        y = mSlopedLoadBL.y + Math.abs(mSlopedLoadBL.x - mSlopedLoadBR.x) * (mSlopedLoadBL.y - mSlopedLoadTR.y) / (2 *AREA_LOAD_DIVISOR);
      }
    };
    mSlopedForce = new TLine();
    mSlopedForce.mStartPoint = slopedForceTail;
    mSlopedForce.mEndPoint = slopedForceHead;
    addToUpdateList(slopedForceTail);
    addToUpdateList(slopedForceHead);
    addToUpdateList(mSlopedForce);

    TPolygon slopePoly = new TPolygon();
    slopePoly.addPoint(mSlopedLoadBL);
    slopePoly.addPoint(mSlopedLoadBR);
    slopePoly.addPoint(mSlopedLoadTR);
    slopePoly.mColor = Color.black;
    //slopePoly.mOutline = true;
    addToDrawList(slopePoly);

    addToDrawList(mSlopedLoadBR);
    addToDrawList(mSlopedLoadTR);
    addToDrawList(mSlopedLoadBL);

  }

  private final static int RA_OFF     = 20;
  private final static int RB_OFF     = 200;

  private void makeSupports() {
    mPointRaHead = new TPoint(BEAM_X_START + RA_OFF, BEAM_Y_START + BEAM_WIDTH) {
      public void update() {
        y = mPointLoadPoints[1].y;
        if (x < mPointLoadPoints[0].x)
          x = mPointLoadPoints[0].x;
        if (x > mPointLoadPoints[2].x)
          x = mPointLoadPoints[2].x;
      }
    };
    mPointRbHead = new TPoint(BEAM_X_START + RB_OFF, BEAM_Y_START + BEAM_WIDTH) {
      public void update() {
        y = mPointLoadPoints[1].y;
        if (x < mPointLoadPoints[0].x)
          x = mPointLoadPoints[0].x;
        if (x > mPointLoadPoints[2].x)
          x = mPointLoadPoints[2].x;
      }
    };



    mPointRbTail = new TPoint() {
      float moment;
      public void update() {
        x = mPointRbHead.x;
        //moment = mPa.moment(mPointRaHead);
        if (x == mPointRaHead.x) {
          y = mPointRbHead.y;
          return;
        }
        moment = 0;
        if (mPaLoadHead.x != mPointRaHead.x)
          moment += (mPaLoadHead.y - mPaLoadTail.y) * (mPaLoadHead.x - mPointRaHead.x);
        if (mPbLoadHead.x != mPointRaHead.x)
          moment += (mPbLoadHead.y - mPbLoadTail.y) * (mPbLoadHead.x - mPointRaHead.x);

        moment /= (x - mPointRaHead.x);
        y = mPointRbHead.y + moment;
      }
    };

    mPointRaTail = new TPoint() {
      float dy;
      public void update() {
        x = mPointRaHead.x;
        dy = mPaLoadHead.y - mPaLoadTail.y;
        dy += mPbLoadHead.y - mPbLoadTail.y;
        dy += mPointRbHead.y - mPointRbTail.y;
        y = mPointRaHead.y + dy;
      }
    };

    TArrow ra = new TArrow();
    ra.mEndPoint = mPointRaHead;
    ra.mStartPoint = mPointRaTail;
    addToDrawList(ra);

    TArrow rb = new TArrow();
    rb.mEndPoint = mPointRbHead;
    rb.mStartPoint = mPointRbTail;
    addToDrawList(rb);

    addToDrawList(mPointRaHead);
    addToDrawList(mPointRbHead);
    addToUpdateList(mPointRbTail);
    addToUpdateList(mPointRaTail);

    mDistRaHead = new TPoint(BEAM_X_START + BEAM_LENGTH + BEAM_SEPARATION + RA_OFF, BEAM_Y_START + BEAM_WIDTH) {
      public void update() {
        y = mDistLoadPoints[1].y;
        if (x < mDistLoadPoints[0].x)
          x = mDistLoadPoints[0].x;
        if (x > mDistLoadPoints[2].x)
          x = mDistLoadPoints[2].x;
      }
    };
    mDistRbHead = new TPoint(BEAM_X_START + BEAM_LENGTH + BEAM_SEPARATION + RB_OFF, BEAM_Y_START + BEAM_WIDTH) {
      public void update() {
        y = mDistLoadPoints[1].y;
        if (x < mDistLoadPoints[0].x)
          x = mDistLoadPoints[0].x;
        if (x > mDistLoadPoints[2].x)
          x = mDistLoadPoints[2].x;
      }
    };

    mDistRbTail = new TPoint() {
      float moment;
      public void update() {
        x = mDistRbHead.x;
        if (x == mDistRaHead.x) {
          y = mDistRbHead.y;
          return;
        }
        moment = 0;
        if (mDistForce.mStartPoint.x != mDistRaHead.x)
          moment += (mDistForce.mEndPoint.y - mDistForce.mStartPoint.y) * (mDistForce.mStartPoint.x - mDistRaHead.x);
        moment /= (x - mDistRaHead.x);
        y = mDistRbHead.y + moment;
      }
    };

    mDistRaTail = new TPoint() {
      float dy;
      public void update() {
        x = mDistRaHead.x;
        dy = mDistForce.mEndPoint.y - mDistForce.mStartPoint.y;
        dy += mDistRbHead.y - mDistRbTail.y;
        y = mDistRaHead.y + dy;
      }
    };

    ra = new TArrow();
    ra.mEndPoint = mDistRaHead;
    ra.mStartPoint = mDistRaTail;
    addToDrawList(ra);

    rb = new TArrow();
    rb.mEndPoint = mDistRbHead;
    rb.mStartPoint = mDistRbTail;
    addToDrawList(rb);

    addToDrawList(mDistRaHead);
    addToDrawList(mDistRbHead);
    addToUpdateList(mDistRbTail);
    addToUpdateList(mDistRaTail);


    mSlopedRaHead = new TPoint(BEAM_X_START + 2 * (BEAM_LENGTH + BEAM_SEPARATION) + RA_OFF, BEAM_Y_START + BEAM_WIDTH) {
      public void update() {
        y = mSlopedLoadPoints[1].y;
        if (x < mSlopedLoadPoints[0].x)
          x = mSlopedLoadPoints[0].x;
        if (x > mSlopedLoadPoints[2].x)
          x = mSlopedLoadPoints[2].x;
      }
    };
    mSlopedRbHead = new TPoint(BEAM_X_START + 2 * (BEAM_LENGTH + BEAM_SEPARATION) + RB_OFF, BEAM_Y_START + BEAM_WIDTH) {
      public void update() {
        y = mSlopedLoadPoints[1].y;
        if (x < mSlopedLoadPoints[0].x)
          x = mSlopedLoadPoints[0].x;
        if (x > mSlopedLoadPoints[2].x)
          x = mSlopedLoadPoints[2].x;
      }
    };

    mSlopedRbTail = new TPoint() {
      float moment;
      public void update() {
        x = mSlopedRbHead.x;
        if (x == mSlopedRaHead.x) {
          y = mSlopedRbHead.y;
          return;
        }
        moment = 0;
        if (mSlopedForce.mStartPoint.x != mSlopedRaHead.x)
          moment += (mSlopedForce.mEndPoint.y - mSlopedForce.mStartPoint.y) * (mSlopedForce.mStartPoint.x - mSlopedRaHead.x);
        moment /= (x - mSlopedRaHead.x);
        y = mSlopedRbHead.y + moment;
      }
    };

    mSlopedRaTail = new TPoint() {
      float dy;
      public void update() {
        x = mSlopedRaHead.x;
        dy = mSlopedForce.mEndPoint.y - mSlopedForce.mStartPoint.y;
        dy += mSlopedRbHead.y - mSlopedRbTail.y;
        y = mSlopedRaHead.y + dy;
      }
    };

    ra = new TArrow();
    ra.mEndPoint = mSlopedRaHead;
    ra.mStartPoint = mSlopedRaTail;
    addToDrawList(ra);

    rb = new TArrow();
    rb.mEndPoint = mSlopedRbHead;
    rb.mStartPoint = mSlopedRbTail;
    addToDrawList(rb);

    addToDrawList(mSlopedRaHead);
    addToDrawList(mSlopedRbHead);
    addToUpdateList(mSlopedRbTail);
    addToUpdateList(mSlopedRaTail);
  }
}