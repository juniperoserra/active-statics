/**
 * Created by simong on 2/20/17.
 */

import AppBase from './AppBase';

export default class HangingCableApp extends AppBase {

    static APPLET_WIDTH = 820;
    static APPLET_HEIGHT = 620;
    static PANEL_SIZE = 45;
    static CABLE_X_START = 160;
    static CABLE_Y_START = 350;
    static START_FORCE_LENGTH = 60;
    static LOAD_LINE_START_X = 710;
    static LOAD_LINE_START_Y = 40;
    static MAX_WIDTH = 60.0;
    static MIN_WIDTH = 2.0;
    static WIDTH_MULT = 0.1;
    static REPORT_X_START = 60;
    static REPORT_Y_START = 480;
    static REPORT_LINE_SPACE = 17;
    static REPORT_COLUMN_SPACE = 110;
    static BUTTON_START_X = 20;
    static BUTTON_START_Y = 70;
    static BUTTON_Y_OFFSET = 30;

    constructor() {
        super();
        /*
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
        public TTextPointLength RbMag;
        public TTextPointLength RaMag;
        public TTextPointLength mRaXMag;
        public TTextPointLength mRaYMag;
        public TTextPointLength mRbXMag;
        public TTextPointLength mRbYMag;
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
        public TLine[] mForcePolyLines = new TLine[8];
        public TPoint mOPrime;
        public TPoint mHorizO;
        public boolean mLoadsVertical = false;
        public G g;
        boolean isStandalone = false;
        private TLine[] mLoadLineLines = new TLine[3];
        */
        //this.g = new G(this);
        //new Types();

        //this.g = {};
        //this.setLayout(null);
        //this.g.mFrame = this;
        //this.g.selectedEntity = null;
        //this.g.mLengthDivisor = 1.0f;

        this.makeNodes();
        this.makeRb();
        this.makeLoadLine();
        this.makeRa();
        this.makeForcePolygon();
        this.makeMembers();
        this.addNodes();
        this.makeText();
        this.makeSupports();
        this.makeReport();
        this.makeLinesOfAction();
        this.makeButtons();
    }
};




/*
 public static final int ;
 public static final int
 public static final int

 public GraphicEntity[] drawList = new GraphicEntity[0];
 public GraphicEntity[]
 private Image mOffscreen;
 private Graphics mGOff;
 Dimension mOffSize;




 public String getParameter(String key, String def) {
 return this.isStandalone ? System.getProperty(key, def) : (this.getParameter(key) != null ? this.getParameter(key) : def);
 }

 public void addToDrawList(GraphicEntity entity) {
 this.drawList = (GraphicEntity[])Util.append(this.drawList, Types.GRAPHIC_ENTITY, entity);
 this.updateList = (GraphicEntity[])Util.append(this.updateList, Types.GRAPHIC_ENTITY, entity);
 }

 public void addToDrawListOnly(GraphicEntity entity) {
 this.drawList = (GraphicEntity[])Util.append(this.drawList, Types.GRAPHIC_ENTITY, entity);
 }

 public void addToUpdateList(GraphicEntity entity) {
 this.updateList = (GraphicEntity[])Util.append(this.updateList, Types.GRAPHIC_ENTITY, entity);
 }

 public void repaint() {
 if (this.mUpdateCanvas != null) {
 this.mUpdateCanvas.repaint();
 }
 }

 public void init() {
 this.g = new G(this);
 new Types();
 this.setLayout(null);
 this.g.mFrame = this;
 this.g.selectedEntity = null;
 this.g.mLengthDivisor = 1.0f;
 this.makeNodes();
 this.makeRb();
 this.makeLoadLine();
 this.makeRa();
 this.makeForcePolygon();
 this.makeMembers();
 this.addNodes();
 this.makeText();
 this.makeSupports();
 this.makeReport();
 this.makeLinesOfAction();
 this.makeButtons();
 try {
 this.jbInit();
 }
 catch (Exception e) {
 e.printStackTrace();
 }
 this.repaint();
 }

 private void jbInit() throws Exception {
 this.setBackground(G.mBackground);
 this.W = this.getSize().width;
 this.H = this.getSize().height;
 this.mUpdateCanvas = new NoScrollUpdateCanvas(this, this.g){

 public void mouseUpHook() {
 if (!HangingCableApplet.this.mSupportsHoriz) {
 return;
 }
 for (int i = 0; i < 7; ++i) {
 if (this.g.selectedEntity != HangingCableApplet.this.mForceTails[i]) continue;
 this.g.mTimer.addJob(new JobMovePointToPoint(this.g, HangingCableApplet.this.mForcePolyNode, HangingCableApplet.this.mHorizO));
 return;
 }
 }

 public void globalUpdate() {
 int i;
 if (HangingCableApplet.this.mForcePolyNode.x == HangingCableApplet.this.mLoadLine[0].x) {
 return;
 }
 if (HangingCableApplet.this.mLinesOfAction) {
 HangingCableApplet.this.mRaLineOfAction.mInvisible = false;
 HangingCableApplet.this.mRbLineOfAction.mInvisible = false;
 HangingCableApplet.this.mLoadLineOfAction.mInvisible = false;
 HangingCableApplet.this.mActionIntersect.mInvisible = false;
 } else {
 HangingCableApplet.this.mRaLineOfAction.mInvisible = true;
 HangingCableApplet.this.mRbLineOfAction.mInvisible = true;
 HangingCableApplet.this.mLoadLineOfAction.mInvisible = true;
 HangingCableApplet.this.mActionIntersect.mInvisible = true;
 }
 if (HangingCableApplet.this.mSupportsHoriz && this.g.mTimer.numJobs() == 0) {
 if (this.g.selectedEntity == HangingCableApplet.this.mCableNodes[0]) {
 HangingCableApplet.this.mCableNodes[8].y = HangingCableApplet.this.mCableNodes[0].y;
 } else if (this.g.selectedEntity == HangingCableApplet.this.mCableNodes[8]) {
 HangingCableApplet.this.mCableNodes[0].y = HangingCableApplet.this.mCableNodes[8].y;
 }
 if (this.g.selectedEntity == HangingCableApplet.this.mForcePolyNode) {
 HangingCableApplet.this.mForcePolyNode.y = HangingCableApplet.this.mOPrime.y;
 }
 }
 HangingCableApplet.this.isArch();
 if (HangingCableApplet.this.mIsArch) {
 HangingCableApplet.this.RbMag.mXOffset = -20;
 HangingCableApplet.this.RbMag.mYOffset = 20;
 HangingCableApplet.this.RaMag.mXOffset = -20;
 HangingCableApplet.this.RaMag.mYOffset = 20;
 HangingCableApplet.this.mRaYMag.mXOffset = -50;
 HangingCableApplet.this.mRbYMag.mXOffset = 10;
 for (i = 0; i < 8; ++i) {
 HangingCableApplet.this.mMembers[i].mColor = G.mRed;
 HangingCableApplet.this.mForcePolyLines[i].mColor = G.mRed;
 }
 } else {
 HangingCableApplet.this.RbMag.mXOffset = 10;
 HangingCableApplet.this.RbMag.mYOffset = -10;
 HangingCableApplet.this.RaMag.mXOffset = -40;
 HangingCableApplet.this.RaMag.mYOffset = -10;
 HangingCableApplet.this.mRaYMag.mXOffset = 10;
 HangingCableApplet.this.mRbYMag.mXOffset = -50;
 for (i = 0; i < 8; ++i) {
 HangingCableApplet.this.mMembers[i].mColor = G.mBlue;
 HangingCableApplet.this.mForcePolyLines[i].mColor = G.mBlue;
 }
 }
 if (HangingCableApplet.this.mUpdatedOnce) {
 for (i = 0; i < 7; ++i) {
 if (HangingCableApplet.this.mForceTails[i].y <= HangingCableApplet.this.mLoads[i].mArrowHead.y) continue;
 HangingCableApplet.this.mForceTails[i].y = HangingCableApplet.this.mLoads[i].mArrowHead.y;
 }
 } else {
 HangingCableApplet.this.mUpdatedOnce = true;
 }
 if (HangingCableApplet.this.mCableNodes[8].x < HangingCableApplet.this.mCableNodes[0].x + (float)8) {
 HangingCableApplet.this.mCableNodes[8].x = HangingCableApplet.this.mCableNodes[0].x + (float)8;
 }
 if (this.g.selectedEntity == HangingCableApplet.this.mCableNodes[0] || this.g.selectedEntity == HangingCableApplet.this.mCableNodes[8]) {
 HangingCableApplet.this.findO();
 } else {
 HangingCableApplet.this.findOPrime();
 }
 if (HangingCableApplet.this.mSupportsHoriz && this.g.mTimer.numJobs() == 0 && this.g.selectedEntity == HangingCableApplet.this.mForcePolyNode) {
 HangingCableApplet.this.mForcePolyNode.y = HangingCableApplet.this.mOPrime.y;
 }
 HangingCableApplet.this.distributeCableNodes();
 for (i = 0; i < 7; ++i) {
 HangingCableApplet.this.mForceTails[i].x = HangingCableApplet.this.mCableNodes[i + 1].x;
 HangingCableApplet.access$7((HangingCableApplet)HangingCableApplet.this)[i] = HangingCableApplet.this.mForceTails[i].y - HangingCableApplet.this.mCableNodes[i + 1].y;
 }
 HangingCableApplet.this.findCableYs();
 HangingCableApplet.this.findReactions();
 for (i = 0; i < 7; ++i) {
 HangingCableApplet.this.mForceTails[i].y = HangingCableApplet.this.mForceDy[i] + HangingCableApplet.this.mCableNodes[i + 1].y;
 }
 for (i = 0; i < 8; ++i) {
 HangingCableApplet.this.mMembers[i].mSize = (int)Util.bound(HangingCableApplet.this.mForcePolyLines[i].length() * 0.1f, 2.0f, 60.0f);
 }
 for (i = 0; i < 7; ++i) {
 HangingCableApplet.this.mForceTailStarts[i].x = HangingCableApplet.this.mCableNodes[i + 1].x;
 HangingCableApplet.this.mForceTailStarts[i].y = HangingCableApplet.this.mCableNodes[i + 1].y - (float)60;
 HangingCableApplet.this.mEqualTails[i].x = HangingCableApplet.this.mCableNodes[i + 1].x;
 HangingCableApplet.this.mEqualTails[i].y = HangingCableApplet.this.mCableNodes[i + 1].y - (HangingCableApplet.this.mCableNodes[1].y - HangingCableApplet.this.mForceTails[0].y);
 }
 HangingCableApplet.this.mHorizO.x = HangingCableApplet.this.mForcePolyNode.x;
 HangingCableApplet.this.mHorizO.y = HangingCableApplet.this.mOPrime.y;
 HangingCableApplet.this.mResultantStartNode.x = HangingCableApplet.this.mActionIntersect.x;
 HangingCableApplet.this.mResultantStartNode.y = HangingCableApplet.this.mActionIntersect.y;
 HangingCableApplet.this.mResultantEndNode.x = HangingCableApplet.this.mActionIntersect.x;
 HangingCableApplet.this.mResultantEndNode.y = HangingCableApplet.this.mActionIntersect.y + (HangingCableApplet.this.mLoadLine[7].y - HangingCableApplet.this.mLoadLine[0].y);
 }
 };
 this.mUpdateCanvas.drawList = this.drawList;
 this.mUpdateCanvas.updateList = this.updateList;
 this.mUpdateCanvas.mUpdateTimes = 3;
 this.mUpdateCanvas.mGlobalUpdateEveryTime = true;
 this.add(this.mUpdateCanvas);
 this.addComponentListener(new ComponentAdapter(){

 public void componentResized(ComponentEvent e) {
 HangingCableApplet.this.mUpdateCanvas.appletResized();
 }
 });
 }

 public String getAppletInfo() {
 return "Applet Information";
 }

 public String[][] getParameterInfo() {
 return null;
 }

 public static void main(String[] args) {
 TrussApplet applet = new TrussApplet();
 applet.isStandalone = true;
 Frame frame = new Frame(){

 protected void processWindowEvent(WindowEvent e) {
 super.processWindowEvent(e);
 if (e.getID() == 201) {
 System.exit(0);
 }
 }

 public synchronized void setTitle(String title) {
 super.setTitle(title);
 this.enableEvents(64);
 }
 };
 frame.setTitle("Applet Frame");
 frame.add((Component)applet, "Center");
 applet.init();
 applet.start();
 frame.setSize(820, 640);
 Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
 frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
 frame.setVisible(true);
 }

 private void findO() {
 float len = Util.distance(this.mForcePolyNode.x, this.mForcePolyNode.y, this.mOPrime.x, this.mOPrime.y);
 float dir = Util.direction(this.mCableNodes[8].x, this.mCableNodes[8].y, this.mCableNodes[0].x, this.mCableNodes[0].y);
 if (!this.mIsArch) {
 len = - len;
 }
 this.mForcePolyNode.x = this.mOPrime.x + len * (float)Math.cos(dir);
 this.mForcePolyNode.y = this.mOPrime.y + len * (float)Math.sin(dir);
 }

 private void findOPrime() {
 float slope = Util.slope(this.mCableNodes[0].x, this.mCableNodes[0].y, this.mCableNodes[8].x, this.mCableNodes[8].y);
 this.mOPrime.x = this.mLoadLine[0].x;
 this.mOPrime.y = this.mForcePolyNode.y - slope * (this.mForcePolyNode.x - this.mOPrime.x);
 }

 private void isArch() {
 this.mIsArch = this.mForcePolyNode.x < this.mLoadLine[0].x;
 }

 private void findReactions() {
 float len = this.mForcePolyLines[0].length() + (float)this.mRa.ARROW_OFFSET;
 float dir = this.mForcePolyLines[0].direction();
 if (!this.mIsArch) {
 len = - len;
 this.mRa.mReverse = -1;
 } else {
 this.mRa.mReverse = 1;
 }
 this.mRaTail.x = this.mCableNodes[0].x + len * (float)Math.cos(dir);
 this.mRaTail.y = this.mCableNodes[0].y + len * (float)Math.sin(dir);
 if (this.mUpdatedOnce) {
 this.mRaX.x = this.mRa.mArrowHead.x;
 this.mRaX.y = this.mRa.mArrowTail.y;
 this.mRaY.x = this.mRa.mArrowTail.x;
 this.mRaY.y = this.mRa.mArrowHead.y;
 }
 len = this.mForcePolyLines[7].length() + (float)this.mRb.ARROW_OFFSET;
 dir = this.mForcePolyLines[7].direction();
 if (!this.mIsArch) {
 len = - len;
 this.mRb.mReverse = -1;
 } else {
 this.mRb.mReverse = 1;
 }
 this.mRbTail.x = this.mCableNodes[8].x - len * (float)Math.cos(dir);
 this.mRbTail.y = this.mCableNodes[8].y - len * (float)Math.sin(dir);
 if (this.mUpdatedOnce) {
 this.mRbX.x = this.mRb.mArrowHead.x;
 this.mRbX.y = this.mRb.mArrowTail.y;
 this.mRbY.x = this.mRb.mArrowTail.x;
 this.mRbY.y = this.mRb.mArrowHead.y;
 }
 }

 private void findCableYs() {
 float y = this.mCableNodes[0].y;
 float xOver = (this.mCableNodes[8].x - this.mCableNodes[0].x) / 8.0f;
 for (int i = 0; i < this.mLoadLine.length - 1; ++i) {
 this.mCableNodes[i + 1].y = y += Util.slope(this.mForcePolyNode.x, this.mForcePolyNode.y, this.mLoadLine[i].x, this.mLoadLine[i].y) * xOver;
 }
 this.mCableNodes[8].y = y += Util.slope(this.mForcePolyNode.x, this.mForcePolyNode.y, this.mLoadLine[this.mLoadLine.length - 1].x, this.mLoadLine[this.mLoadLine.length - 1].y) * xOver;
 }

 private void distributeCableNodes() {
 float increment = (this.mCableNodes[8].x - this.mCableNodes[0].x) / 8.0f;
 float x = this.mCableNodes[0].x + increment;
 for (int i = 1; i < 8; ++i) {
 this.mCableNodes[i].x = x;
 x += increment;
 }
 }

 public void start() {
 this.g.mTimer = new Timer(this.g);
 this.g.mTimer.start();
 }

 public void stop() {
 this.g.mTimer.stop();
 }

 private void makeText() {
 TText title = new TText();
 title.mText = "Hanging Cable/Arch";
 title.mSize = 24;
 title.x = 20.0f;
 title.y = 50.0f;
 title.mPosRelativeTo = 0;
 this.addToDrawList(title);
 TTextPoint forcePoly = new TTextPoint();
 forcePoly.mBasePoint = this.mLoadLine[0];
 forcePoly.mXOffset = -160;
 forcePoly.mYOffset = 0;
 forcePoly.mSize = 20;
 forcePoly.mText = "Force Polygon";
 this.addToDrawList(forcePoly);
 TTextPoint formDiag = new TTextPoint();
 formDiag.mBasePoint = this.mCableNodes[1];
 formDiag.mXOffset = -180;
 formDiag.mYOffset = 0;
 formDiag.mSize = 20;
 formDiag.mText = "Form Diagram";
 this.addToDrawList(formDiag);
 }

 private void makeSupports() {
 TPin pin = new TPin(this.mCableNodes[0]);
 this.addToDrawList(pin);
 TPin pin2 = new TPin(this.mCableNodes[8]);
 this.addToDrawList(pin2);
 }

 private void makeNodes() {
 int x = 160;
 this.mCableNodes[0] = new TPoint(x, 350.0f);
 this.mCableNodes[8] = new TPoint(x + 360, 350.0f);
 for (int i = 0; i < 7; ++i) {
 this.mCableNodes[i + 1] = new TPoint(x + (i + 1) * 45, 350.0f);
 this.mCableNodes[i + 1].mSelectable = false;
 this.mCableNodes[i + 1].mControlPoint = false;
 this.mForceTails[i] = new TPoint(x + (i + 1) * 45, 290.0f);
 this.mForceTailStarts[i] = new TPoint(x + (i + 1) * 45, 290.0f);
 this.mEqualTails[i] = new TPoint(x + (i + 1) * 45, 290.0f);
 this.addToUpdateList(this.mForceTails[i]);
 this.mLoads[i] = new TLoad(this.g);
 this.addToUpdateList(this.mLoads[i]);
 this.mLoads[i].mStartPoint = this.mForceTails[i];
 this.mLoads[i].mEndPoint = this.mCableNodes[i + 1];
 }
 this.mCableNodes[4].mLabelXOff = 0;
 this.mCableNodes[4].mLabelYOff = 24;
 this.mCableNodes[4].mLabel = "O";
 this.mHorizO = new TPoint();
 }

 private void addNodes() {
 int i;
 this.addToDrawList(this.mCableNodes[0]);
 this.addToDrawList(this.mCableNodes[8]);
 for (i = 1; i < 8; ++i) {
 this.addToDrawList(this.mCableNodes[i]);
 }
 for (i = 0; i < 7; ++i) {
 this.addToDrawList(this.mForceTails[i]);
 }
 }

 private void makeMembers() {
 int i;
 TLine groundLine = new TLine();
 groundLine.mStartPoint = this.mCableNodes[0];
 groundLine.mEndPoint = this.mCableNodes[8];
 groundLine.mColor = G.mYellow;
 groundLine.mSize = 3;
 groundLine.mDashed = true;
 groundLine.mDashLength = 7;
 groundLine.mGapLength = 5;
 this.addToDrawList(groundLine);
 for (i = 0; i < 8; ++i) {
 this.mMembers[i] = new TLine();
 this.mMembers[i].mStartPoint = this.mCableNodes[i];
 this.mMembers[i].mEndPoint = this.mCableNodes[i + 1];
 this.mMembers[i].dragAlso(this.mCableNodes[0]);
 this.mMembers[i].dragAlso(this.mCableNodes[8]);
 this.mMembers[i].mLabelXOff = 0;
 this.mMembers[i].mLabelYOff = -20;
 this.mMembers[i].mLabel = String.valueOf((char)(65 + i));
 this.addToDrawList(this.mMembers[i]);
 }
 for (i = 0; i < 7; ++i) {
 this.addToDrawListOnly(this.mLoads[i]);
 }
 }

 private void makeRb() {
 this.mRbTail = new TPoint();
 this.mRb = new TReaction();
 this.mRb.ARROW_OFFSET = 45;
 this.mRb.mArrowOffset = 45;
 this.mRb.mStartPoint = this.mRbTail;
 this.mRb.mEndPoint = this.mCableNodes[8];
 this.mRb.mColor = G.mGreen;
 this.mRb.mLabel = "Rb";
 this.mRb.mLabelXOff = 14;
 this.mRb.mLabelYOff = 0;
 this.addToDrawList(this.mRb);
 this.RbMag = new TTextPointLength(this.g);
 this.RbMag.mBasePoint = this.mRbTail;
 this.RbMag.mXOffset = -20;
 this.RbMag.mYOffset = 20;
 this.RbMag.mLine = this.mRb;
 this.addToDrawList(this.RbMag);
 this.mRbX = new TPoint();
 TLine newLine = new TLine();
 newLine.mStartPoint = this.mRb.mArrowHead;
 newLine.mEndPoint = this.mRbX;
 newLine.mColor = G.mGreen;
 newLine.mSize = 2;
 newLine.mDashed = true;
 newLine.mDashLength = 7;
 newLine.mGapLength = 5;
 newLine.mLabel = "Rbx";
 newLine.mLabelYOff = 0;
 newLine.mLabelXOff = 10;
 this.addToDrawList(newLine);
 this.mRbXMag = new TTextPointLength(this.g);
 this.mRbXMag.mBasePoint = this.mRbX;
 this.mRbXMag.mXOffset = 0;
 this.mRbXMag.mYOffset = 20;
 this.mRbXMag.mLine = newLine;
 this.addToDrawList(this.mRbXMag);
 this.mRbY = new TPoint();
 newLine = new TLine();
 newLine.mStartPoint = this.mRb.mArrowHead;
 newLine.mEndPoint = this.mRbY;
 newLine.mColor = G.mGreen;
 newLine.mSize = 2;
 newLine.mDashed = true;
 newLine.mDashLength = 7;
 newLine.mGapLength = 5;
 newLine.mLabel = "Rby";
 newLine.mLabelYOff = -10;
 newLine.mLabelXOff = 0;
 this.addToDrawList(newLine);
 this.mRbYMag = new TTextPointLength(this.g);
 this.mRbYMag.mBasePoint = this.mRbY;
 this.mRbYMag.mXOffset = -50;
 this.mRbYMag.mYOffset = 0;
 this.mRbYMag.mLine = newLine;
 this.addToDrawList(this.mRbYMag);
 }

 private void makeRa() {
 this.mRaTail = new TPoint();
 this.mRa = new TReaction();
 this.mRa.ARROW_OFFSET = 45;
 this.mRa.mArrowOffset = 45;
 this.mRa.mStartPoint = this.mRaTail;
 this.mRa.mEndPoint = this.mCableNodes[0];
 this.mRa.mColor = G.mGreen;
 this.mRa.mLabel = "Ra";
 this.mRa.mLabelXOff = -24;
 this.mRa.mLabelYOff = 0;
 this.addToDrawList(this.mRa);
 this.RaMag = new TTextPointLength(this.g);
 this.RaMag.mBasePoint = this.mRaTail;
 this.RaMag.mXOffset = -20;
 this.RaMag.mYOffset = 20;
 this.RaMag.mLine = this.mRa;
 this.addToDrawList(this.RaMag);
 this.mRaX = new TPoint();
 TLine newLine = new TLine();
 newLine.mStartPoint = this.mRa.mArrowHead;
 newLine.mEndPoint = this.mRaX;
 newLine.mColor = G.mGreen;
 newLine.mSize = 2;
 newLine.mDashed = true;
 newLine.mDashLength = 7;
 newLine.mGapLength = 5;
 newLine.mLabel = "Ray";
 newLine.mLabelYOff = 0;
 newLine.mLabelXOff = 10;
 this.addToDrawList(newLine);
 this.mRaXMag = new TTextPointLength(this.g);
 this.mRaXMag.mBasePoint = this.mRaX;
 this.mRaXMag.mXOffset = 0;
 this.mRaXMag.mYOffset = 20;
 this.mRaXMag.mLine = newLine;
 this.addToDrawList(this.mRaXMag);
 this.mRaY = new TPoint();
 newLine = new TLine();
 newLine.mStartPoint = this.mRa.mArrowHead;
 newLine.mEndPoint = this.mRaY;
 newLine.mColor = G.mGreen;
 newLine.mSize = 2;
 newLine.mDashed = true;
 newLine.mDashLength = 7;
 newLine.mGapLength = 5;
 newLine.mLabel = "Rax";
 newLine.mLabelYOff = -10;
 newLine.mLabelXOff = 0;
 this.addToDrawList(newLine);
 this.mRaYMag = new TTextPointLength(this.g);
 this.mRaYMag.mBasePoint = this.mRaY;
 this.mRaYMag.mXOffset = -50;
 this.mRaYMag.mYOffset = 0;
 this.mRaYMag.mLine = newLine;
 this.addToDrawList(this.mRaYMag);
 }

 private void makeLoadLine() {
 this.mLoadLine[0] = new TPoint(710.0f, 40.0f);
 this.mLoadLine[0].mLabel = "a";
 this.mLoadLine[0].mLabelXOff = 14;
 this.mLoadLine[0].mLabelYOff = 0;
 this.addToUpdateList(this.mLoadLine[0]);
 for (int i = 0; i < 7; ++i) {
 TPointTranslate newPoint;
 this.mLoadLine[i + 1] = newPoint = new TPointTranslate();
 newPoint.mBasePoint = this.mLoadLine[i];
 newPoint.mVectorStart = this.mForceTails[i];
 newPoint.mVectorEnd = this.mLoads[i].mArrowHead;
 newPoint.mLabel = String.valueOf((char)(98 + i));
 newPoint.mLabelXOff = 14;
 newPoint.mLabelYOff = 0;
 newPoint.mSize = 7;
 newPoint.dragAlso(this.mLoadLine[0]);
 this.addToUpdateList(newPoint);
 TLine newLine = new TLine();
 newLine.mStartPoint = this.mLoadLine[i];
 newLine.mEndPoint = this.mLoadLine[i + 1];
 newLine.mColor = this.mLoads[0].mColor;
 newLine.mSize = 4;
 newLine.dragAlso(this.mLoadLine[0]);
 this.addToDrawList(newLine);
 }
 this.mOPrime = new TPoint();
 this.mOPrime.mSize = 7;
 this.mOPrime.mControlPoint = false;
 this.mOPrime.mSelectable = false;
 this.addToDrawList(this.mOPrime);
 }

 private void makeForcePolygon() {
 TPoint newNode;
 int i;
 this.mForcePolyNode = newNode = new TPoint(610.0f, 197.0f);
 newNode.mLabel = "O";
 newNode.mLabelXOff = -14;
 newNode.mLabelYOff = -8;
 this.addToUpdateList(newNode);
 this.mLoadLine[0].dragAlso(newNode);
 TLine groundLine = new TLine();
 groundLine.mStartPoint = this.mForcePolyNode;
 groundLine.mEndPoint = this.mOPrime;
 groundLine.mColor = G.mYellow;
 groundLine.mSize = 3;
 groundLine.mDashed = true;
 groundLine.mDashLength = 7;
 groundLine.mGapLength = 5;
 groundLine.dragAlso(this.mLoadLine[0]);
 this.addToDrawList(groundLine);
 for (i = 0; i < this.mLoadLine.length; ++i) {
 this.mForcePolyLines[i] = new TLine();
 this.mForcePolyLines[i].mStartPoint = this.mLoadLine[i];
 this.mForcePolyLines[i].mEndPoint = newNode;
 this.addToDrawList(this.mForcePolyLines[i]);
 this.mForcePolyLines[i].mSize = 2;
 this.mForcePolyLines[i].dragAlso(this.mLoadLine[0]);
 }
 this.addToDrawListOnly(newNode);
 for (i = 0; i < this.mLoadLine.length; ++i) {
 this.addToDrawListOnly(this.mLoadLine[i]);
 }
 }

 private void makeReport() {
 }

 private void makeLinesOfAction() {
 this.mResultantStartNode = new TPoint();
 this.mResultantEndNode = new TPoint();
 this.mActionIntersect = new TPointIntersect(this.mRa, this.mRb);
 this.mActionIntersect.x = 20.0f;
 this.mActionIntersect.y = 20.0f;
 this.mActionIntersect.mConsiderExtents = false;
 this.addToDrawList(this.mActionIntersect);
 this.mRaLineOfAction = new TLine();
 this.mRaLineOfAction.mStartPoint = this.mRa.mEndPoint;
 this.mRaLineOfAction.mEndPoint = this.mActionIntersect;
 this.mRaLineOfAction.mSize = 2;
 this.mRaLineOfAction.mDashed = true;
 this.mRaLineOfAction.mConsiderExtents = false;
 this.mRaLineOfAction.mColor = G.mGreen;
 this.addToDrawList(this.mRaLineOfAction);
 this.mRbLineOfAction = new TLine();
 this.mRbLineOfAction.mStartPoint = this.mRb.mEndPoint;
 this.mRbLineOfAction.mEndPoint = this.mActionIntersect;
 this.mRbLineOfAction.mSize = 2;
 this.mRbLineOfAction.mDashed = true;
 this.mRbLineOfAction.mConsiderExtents = false;
 this.mRbLineOfAction.mColor = G.mGreen;
 this.addToDrawList(this.mRbLineOfAction);
 this.mLoadLineOfAction = new TArrow();
 this.mLoadLineOfAction.mStartPoint = this.mResultantStartNode;
 this.mLoadLineOfAction.mEndPoint = this.mResultantEndNode;
 this.mLoadLineOfAction.mArrowOffset = 0;
 this.mLoadLineOfAction.mSize = 2;
 this.mLoadLineOfAction.mDashed = true;
 this.mLoadLineOfAction.mConsiderExtents = false;
 this.mLoadLineOfAction.mColor = Color.darkGray;
 this.addToDrawList(this.mLoadLineOfAction);
 }

 private void makeButtons() {
 int x = 20;
 int y = 70;
 TButton moveButton = new TButton("Return To Starting Position");
 moveButton.x = x;
 moveButton.y = y;
 moveButton.mWidth = 170.0f;
 moveButton.mHeight = 20.0f;
 this.addToDrawList(moveButton);
 moveButton.mAction = new TAction(){

 public void run() {
 HangingCableApplet.this.g.mTimer.clearJobs();
 JobMoveViewToOrigin originMove = new JobMoveViewToOrigin(HangingCableApplet.this.g);
 originMove.mView = HangingCableApplet.this.mUpdateCanvas;
 HangingCableApplet.this.g.mTimer.addJob(originMove);
 HangingCableApplet.this.mSupportsHoriz = false;
 HangingCableApplet.access$11((HangingCableApplet)HangingCableApplet.this).mSelected = false;
 HangingCableApplet.this.g.selectedEntity = HangingCableApplet.this.mForceTails[0];
 Object thisJob = null;
 for (int i = 0; i < 7; ++i) {
 HangingCableApplet.this.mForceTails[i].y = HangingCableApplet.this.mForceTailStarts[i].y;
 }
 JobMovePointToStart leftHomeJob = new JobMovePointToStart(null, HangingCableApplet.this.g);
 leftHomeJob.mMovePoint = HangingCableApplet.this.mCableNodes[0];
 HangingCableApplet.this.g.mTimer.addJob(leftHomeJob);
 JobMovePointToStart rightHomeJob = new JobMovePointToStart(leftHomeJob, HangingCableApplet.this.g);
 rightHomeJob.mMovePoint = HangingCableApplet.this.mCableNodes[8];
 HangingCableApplet.this.g.mTimer.addJob(rightHomeJob);
 JobMovePointToStart newJob = new JobMovePointToStart(rightHomeJob, HangingCableApplet.this.g);
 newJob.mMovePoint = HangingCableApplet.this.mLoadLine[0];
 HangingCableApplet.this.g.mTimer.addJob(newJob);
 JobMovePointToStart polyHomeJob = new JobMovePointToStart(newJob, HangingCableApplet.this.g);
 polyHomeJob.mMovePoint = HangingCableApplet.this.mForcePolyNode;
 HangingCableApplet.this.g.mTimer.addJob(polyHomeJob);
 }
 };
 this.mHorizButton = new TButton("Keep supports level");
 this.mHorizButton.x = x;
 this.mHorizButton.y = y += 30;
 this.mHorizButton.mWidth = 170.0f;
 this.mHorizButton.mHeight = 20.0f;
 this.mHorizButton.mIsToggle = true;
 this.addToDrawList(this.mHorizButton);
 this.mHorizButton.mAction = new TAction(){

 public void run() {
 HangingCableApplet.this.g.selectedEntity = HangingCableApplet.this.mCableNodes[8];
 if (HangingCableApplet.this.mSupportsHoriz) {
 HangingCableApplet.this.mSupportsHoriz = false;
 return;
 }
 JobMovePointToPoint rightHoriz = new JobMovePointToPoint(HangingCableApplet.this.g, HangingCableApplet.this.mForcePolyNode, HangingCableApplet.this.mHorizO);
 HangingCableApplet.this.g.mTimer.addJob(rightHoriz);
 HangingCableApplet.this.g.mTimer.addJob(new TimerJob(this, rightHoriz, HangingCableApplet.this.g){
 private final  this$1;

public void step() {
    if (!this.afterJob.done) {
        return;
    }
.access$12(()this.this$1).mSupportsHoriz = true;
    this.done = true;
}
});
}

static HangingCableApplet access$12( x$0) {
    return x$0.HangingCableApplet.this;
}
};
TButton mEqualButton = new TButton("Equalize Loads");
mEqualButton.x = x;
mEqualButton.y = y += 30;
mEqualButton.mWidth = 170.0f;
mEqualButton.mHeight = 20.0f;
this.addToDrawList(mEqualButton);
mEqualButton.mAction = new TAction(){

    //
     // Enabled force condition propagation
     // Lifted jumps to return sites
     //
    public void run() {
        JobMovePointToPoint thisJob = null;
        for (int i = 0; i < 7; ++i) {
            JobMovePointToPoint nextJob = new JobMovePointToPoint(thisJob, HangingCableApplet.this.g, HangingCableApplet.this.mForceTails[i], HangingCableApplet.this.mEqualTails[i]);
            HangingCableApplet.this.g.mTimer.addJob(nextJob);
            thisJob = nextJob;
        }
        if (!HangingCableApplet.this.mSupportsHoriz) return;
        JobMovePointToPoint rightHoriz = new JobMovePointToPoint(thisJob, HangingCableApplet.this.g, HangingCableApplet.this.mForcePolyNode, HangingCableApplet.this.mHorizO);
        HangingCableApplet.this.g.mTimer.addJob(rightHoriz);
    }
};
this.mLinesOfActionCheck = new TButton("Extend Lines of Action");
this.mLinesOfActionCheck.x = x;
this.mLinesOfActionCheck.y = y += 30;
this.mLinesOfActionCheck.mWidth = 170.0f;
this.mLinesOfActionCheck.mHeight = 20.0f;
this.mLinesOfActionCheck.mIsToggle = true;
this.addToDrawList(this.mLinesOfActionCheck);
this.mLinesOfActionCheck.mAction = new TAction(){

    public void run() {
        HangingCableApplet.this.mLinesOfAction = HangingCableApplet.access$13((HangingCableApplet)HangingCableApplet.this).mSelected;
        HangingCableApplet.this.repaint();
    }
};
}

static {
    APPLET_WIDTH = 820;
    APPLET_HEIGHT = 620;
    PANEL_SIZE = 45;
    CABLE_X_START = 160;
    CABLE_Y_START = 350;
    LOAD_LINE_START_X = 710;
    LOAD_LINE_START_Y = 40;
    MAX_WIDTH = 60.0f;
    MIN_WIDTH = 2.0f;
    WIDTH_MULT = 0.1f;
    REPORT_X_START = 60;
    REPORT_Y_START = 480;
    REPORT_LINE_SPACE = 17;
    REPORT_COLUMN_SPACE = 110;
    BUTTON_START_X = 20;
    BUTTON_START_Y = 70;
    BUTTON_Y_OFFSET = 30;
}

static TButton access$11(HangingCableApplet x$0) {
    return x$0.mHorizButton;
}

static  TButton access$13(HangingCableApplet x$0) {
    return x$0.mLinesOfActionCheck;
}

}
 */