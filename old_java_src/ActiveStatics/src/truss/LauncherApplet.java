package truss;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import java.net.URL;

public class LauncherApplet extends Applet {
  public static final int APPLET_WIDTH = 700;
  public static final int APPLET_HEIGHT = 700;

  public GraphicEntity[] drawList = new GraphicEntity[0];
  public GraphicEntity[] updateList = new GraphicEntity[0];
  private Image mOffscreen;
  private Graphics mGOff;
  Dimension mOffSize;
  public int W;
  public int H;

  private Point tempPoint = new Point();
  private int paintedTwice = 0;

  public G g;

  TImageButton mSinglePanelButton;
  TImageButton mTrussButton;
  TImageButton mHangingButton;
  TImageButton mCantButton;
  TImageButton mCableButton;
  TImageButton mOverButton;
  TImageButton mMinWeightButton;
  TImageButton mBeamLoadingButton;

  private Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
  private Cursor arrow = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);

  Color mTextColor = new Color(225, 225, 205);

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
    g.mLengthDivisor = 1.0f;

    makeText();
    makeImages();

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
    //setBackground(g.mBackground);
    setBackground(new Color(0x66, 0x00, 0x33));
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
      public void mouseMoved(MouseEvent e) {
        tempPoint.x = e.getX();
        tempPoint.y = e.getY();
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

  public void globalUpdate() {    // Apply contstraints

  }


  public void update(Graphics graphics) {
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
  }

  public void start() {
    g.mTimer = new Timer(g);
    g.mTimer.start();
  }

  public void stop() {
    g.mTimer.stop();
  }

  //------------------------  SCENE CREATION

  private static final int IMAGE_X_START = 40;
  private static final int IMAGE_Y_START = 86;
  private static final int IMAGE_Y_SEP = 120;
  private static final int TEXT_START = 160;
  private static final int COL_OFFSET = 340;

  private void makeImages() {
    int x = IMAGE_X_START;
    int y = IMAGE_Y_START;
    TButton tbut;

    mSinglePanelButton = new TImageButton("images/SinglePanelSmall.gif", g);
    mSinglePanelButton.x = x;
    mSinglePanelButton.y = y;
    mSinglePanelButton.mAction = new TAction() {
      public void run () {
        SinglePanelApplet applet = new SinglePanelApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  1. Single Panel Truss", null);
      }
    };
    addToDrawList(mSinglePanelButton);

    tbut = new TButton("1. Single Panel Truss");
    tbut.mSize = 18;
    tbut.mColor = mTextColor;
    tbut.mHighlightColor = g.mGreen;
    tbut.mHeight = 24;
    tbut.x = TEXT_START;
    tbut.y = y + 74;
    tbut.mDrawOutline = false;
    tbut.mAction = new TAction() {
      public void run () {
        SinglePanelApplet applet = new SinglePanelApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  1. Single Panel Truss", null);
      }
    };
    addToDrawList(tbut);

    y +=  IMAGE_Y_SEP;

    mTrussButton = new TImageButton("images/SimpleTrussSmall.gif", g);
    mTrussButton.x = x;
    mTrussButton.y = y;
    mTrussButton.mAction = new TAction() {
      public void run () {
        TrussApplet applet = new TrussApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  2. Simple Truss", null);
      }
    };
    addToDrawList(mTrussButton);


    tbut = new TButton("2. Simple Truss");
    tbut.mSize = 18;
    tbut.mColor = mTextColor;
    tbut.mHighlightColor = g.mGreen;
    tbut.mHeight = 24;
    tbut.x = TEXT_START;
    tbut.y = y + 74;
    tbut.mDrawOutline = false;
    tbut.mAction = new TAction() {
      public void run () {
        TrussApplet applet = new TrussApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  2. Simple Truss", null);
      }
    };
    addToDrawList(tbut);

    y += IMAGE_Y_SEP + 35;


    mHangingButton = new TImageButton("images/HangingCableSmall.gif", g);
    mHangingButton.x = x;
    mHangingButton.y = y;
    mHangingButton.mAction = new TAction() {
      public void run () {
        HangingCableApplet applet = new HangingCableApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  3. Hanging Cable/Arch", null);
      }
    };
    addToDrawList(mHangingButton);

    tbut = new TButton("3. Hanging Cable/Arch");
    tbut.mSize = 18;
    tbut.mColor = mTextColor;
    tbut.mHighlightColor = g.mGreen;
    tbut.mHeight = 24;
    tbut.x = TEXT_START;
    tbut.y = y + 50;
    tbut.mDrawOutline = false;
    tbut.mAction = new TAction() {
      public void run () {
        HangingCableApplet applet = new HangingCableApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  3. Hanging Cable/Arch", null);
      }
    };
    addToDrawList(tbut);

    y += IMAGE_Y_SEP - 24;

    mCantButton = new TImageButton("images/CantileverSmall.gif", g);
    mCantButton.x = x;
    mCantButton.y = y;
    mCantButton.mAction = new TAction() {
      public void run () {
        CantileverApplet applet = new CantileverApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  4. Cantilever Truss", null);
      }
    };
    addToDrawList(mCantButton);

    tbut = new TButton("4. Cantilever Truss");
    tbut.mSize = 18;
    tbut.mColor = mTextColor;
    tbut.mHighlightColor = g.mGreen;
    tbut.mHeight = 24;
    tbut.x = TEXT_START;
    tbut.y = y + 48;
    tbut.mDrawOutline = false;
    tbut.mAction = new TAction() {
      public void run () {
        CantileverApplet applet = new CantileverApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  4. Cantilever Truss", null);
      }
    };
    addToDrawList(tbut);

//    y += IMAGE_Y_SEP - 24;
    x += COL_OFFSET;
    y = IMAGE_Y_START;

    mCableButton = new TImageButton("images/CableStaySmall.gif", g);
    mCableButton.x = x;
    mCableButton.y = y;
    mCableButton.mAction = new TAction() {
      public void run () {
        CableStayApplet applet = new CableStayApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  5. Fanlike Structure", null);
      }
    };

    addToDrawList(mCableButton);
    tbut = new TButton("5. Fanlike Structure");
    tbut.mSize = 18;
    tbut.mColor = mTextColor;
    tbut.mHighlightColor = g.mGreen;
    tbut.mHeight = 24;
    tbut.x = TEXT_START + COL_OFFSET;
    tbut.y = y + 74;
    tbut.mDrawOutline = false;
    tbut.mAction = new TAction() {
      public void run () {
        CableStayApplet applet = new CableStayApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  5. Fanlike Structure", null);
      }
    };
    addToDrawList(tbut);

    y += IMAGE_Y_SEP;

    mOverButton = new TImageButton("images/OverhangSmall.gif", g);
    mOverButton.x = x;
    mOverButton.y = y;
    mOverButton.mAction = new TAction() {
      public void run () {
        OverhangApplet applet = new OverhangApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  6. Overhanging Truss", null);
      }
    };
    addToDrawList(mOverButton);

    tbut = new TButton("6. Overhanging Truss");
    tbut.mSize = 18;
    tbut.mColor = mTextColor;
    tbut.mHighlightColor = g.mGreen;
    tbut.mHeight = 24;
    tbut.x = TEXT_START + COL_OFFSET;
    tbut.y = y + 76;
    tbut.mDrawOutline = false;
    tbut.mAction = new TAction() {
      public void run () {
        OverhangApplet applet = new OverhangApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  6. Overhanging Truss", null);
      }
    };
    addToDrawList(tbut);

    y += IMAGE_Y_SEP;

    mMinWeightButton = new TImageButton("images/MinWeightSmall.gif", g);
    mMinWeightButton.x = x;
    mMinWeightButton.y = y;
    mMinWeightButton.mAction = new TAction() {
      public void run () {
        MinWeightApplet applet = new MinWeightApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  7. Minimum Weight Truss", null);
      }
    };
    addToDrawList(mMinWeightButton);

    tbut = new TButton("7. Minimum Material Truss");
    tbut.mSize = 18;
    tbut.mColor = mTextColor;
    tbut.mHighlightColor = g.mGreen;
    tbut.mHeight = 24;
    tbut.x = TEXT_START + COL_OFFSET;
    tbut.y = y + 84;
    tbut.mDrawOutline = false;
    tbut.mAction = new TAction() {
      public void run () {
        MinWeightApplet applet = new MinWeightApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  7. Minimum Weight Truss", null);
      }
    };
    addToDrawList(tbut);

    y += IMAGE_Y_SEP + 10;

    mBeamLoadingButton = new TImageButton("images/BeamLoadSmall.gif", g);
    mBeamLoadingButton.x = x;
    mBeamLoadingButton.y = y;
    mBeamLoadingButton.mAction = new TAction() {
      public void run () {
        BeamLoadApplet applet = new BeamLoadApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  8. Beam Loading", null);
      }
    };
    addToDrawList(mBeamLoadingButton);

    tbut = new TButton("8. Beam Loading");
    tbut.mSize = 18;
    tbut.mColor = mTextColor;
    tbut.mHighlightColor = g.mGreen;
    tbut.mHeight = 24;
    tbut.x = TEXT_START + COL_OFFSET;
    tbut.y = y + 54;
    tbut.mDrawOutline = false;
    tbut.mAction = new TAction() {
      public void run () {
        BeamLoadApplet applet = new BeamLoadApplet();
        applet.isStandalone = true;
        openFrame(applet, applet.APPLET_WIDTH, applet.APPLET_HEIGHT, "  8. Beam Loading", null);
      }
    };
    addToDrawList(tbut);

    y += IMAGE_Y_SEP;
  }

  private void makeText() {
    TText title = new TText();
    title.mText = "Interactive Graphic Statics Demonstrations";
    title.mColor = Color.orange;
    title.mSize = 24;
    title.x = 40;
    title.y = 50;
    addToDrawList(title);

    title = new TText();
    title.mText = "Select any demo to begin.";
    title.mColor = Color.orange;
    title.mSize = 12;
    title.x = 40;
    title.y = 70;
    addToDrawList(title);

    TButton attribution = new TButton("Copyright 2002, Simon Greenwold, Aesthetics + Computation Group, MIT Media Lab");
    attribution.mSize = 10;
    attribution.mColor = Color.orange;
    attribution.mHighlightColor = g.mGreen;
    attribution.mHeight = 16;
    attribution.x = 34;
    attribution.y = 570;
    attribution.mDrawOutline = false;
    attribution.mAction = new TAction() {
      public void run () {
        try {
          getAppletContext().showDocument(new URL("http://acg.media.mit.edu/people/simong"), "_AboutSimong");
        }
        catch (java.net.MalformedURLException e) {
        }
      }
    };
    addToDrawList(attribution);

    attribution = new TButton("simong@media.mit.edu");
    attribution.mSize = 10;
    attribution.mColor = Color.orange;
    attribution.mHighlightColor = g.mGreen;
    attribution.mHeight = 16;
    attribution.x = 480;
    attribution.y = 570;
    attribution.mDrawOutline = false;
    attribution.mAction = new TAction() {
      public void run () {
        try {
          getAppletContext().showDocument(new URL("mailto:simong@media.mit.edu"), "_blank");
        }
        catch (java.net.MalformedURLException e) {
        }
      }
    };
    addToDrawList(attribution);

    attribution = new TButton("Manual by Edward Allen and Waclaw Zalewski");
    attribution.mSize = 10;
    attribution.mColor = Color.orange;
    attribution.mHighlightColor = g.mGreen;
    attribution.mHeight = 16;
    attribution.x = 34;
    attribution.y = 584;
    attribution.mDrawOutline = false;
    attribution.mAction = new TAction() {
      public void run () {
        try {
          getAppletContext().showDocument(new URL("http://www.shapingstructures.com"), "_blank");
        }
        catch (java.net.MalformedURLException e) {
        }
      }
    };
    addToDrawList(attribution);

    attribution = new TButton("Methods from \"Shaping Structures: Statics\"");
    attribution.mSize = 10;
    attribution.mColor = Color.orange;
    attribution.mHighlightColor = g.mGreen;
    attribution.mHeight = 16;
    attribution.x = 34;
    attribution.y = 598;
    attribution.mDrawOutline = false;
    attribution.mAction = new TAction() {
      public void run () {
        try {
          getAppletContext().showDocument(new URL("http://www.shapingstructures.com"), "_blank");
        }
        catch (java.net.MalformedURLException e) {
        }
      }
    };
    addToDrawList(attribution);
  }


  public void openFrame(Applet applet, int width, int height, String name, Image iconImage) {
    Frame frame;
    frame = new Frame() {
      protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
          dispose();
        }
      }
      public synchronized void setTitle(String title) {
        super.setTitle(title);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      }
    };
    frame.setTitle(name);
    frame.add(applet, BorderLayout.CENTER);
    applet.init();
    applet.start();
    frame.setSize(width, height);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    g.WINDOW_X_START_MAX = d.width - 400;
    g.WINDOW_Y_START_MAX = d.height - 400;

    frame.setLocation(g.windowX, g.windowY);
    frame.setIconImage(iconImage);
    g.windowX += g.WINDOW_X_INC;
    g.windowY += g.WINDOW_Y_INC;
    if (g.windowX > g.WINDOW_X_START_MAX || g.windowY > g.WINDOW_Y_START_MAX) {
      g.windowY = g.WINDOW_Y_START;
      g.windowStacks += 3;
      if (g.windowStacks * g.WINDOW_X_INC >= g.WINDOW_X_START_MAX)
        g.windowStacks = 0;
      g.windowX = g.WINDOW_X_START + (g.windowStacks * g.WINDOW_X_INC);
    }
    frame.setVisible(true);
  }

/*  public void runScript(String scriptCall) {
    URL url;
    Class jsObjectClass;

    // Rather than checking the browser vendor to determine what action
    // to take, we test for the presence of the JSObject class.
    try {
      jsObjectClass = Class.forName("netscape.javascript.JSObject");
    }
    catch(ClassNotFoundException e) {
      jsObjectClass = null;
    }

    if(jsObjectClass != null) {
      netscape.javascript.JSObject window;

      window = netscape.javascript.JSObject.getWindow(this);
      window = (netscape.javascript.JSObject)window.getMember("top");
      window.eval(scriptCall);
    }
    else {
      try {
        url = new URL("javascript:top." + scriptCall);
        getAppletContext().showDocument(url);
      }
      catch(java.net.MalformedURLException e) {
        System.out.println("JavaScript invocation is not supported");
      }
    }
  }*/

}