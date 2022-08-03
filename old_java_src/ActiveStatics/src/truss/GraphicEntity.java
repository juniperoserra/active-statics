package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */
import java.awt.*;

public class GraphicEntity {

  public static final int VIEW_RELATIVE = 0;
  public static final int GLOBAL_RELATIVE = 1;

  public int mPosRelativeTo = GLOBAL_RELATIVE;

  public String mLabel = "";
  public int mLabelXOff = 10;
  public int mLabelYOff = 10;
  public int mSize;
  public boolean mSelectable = true;
  public boolean mInvisible = false;
  public boolean mConsiderExtents = true;

  public Color mColor;
  public GraphicEntity mDragAlso[];

  private Point tmpPoint = new Point();

  public GraphicEntity() {
    mColor = Color.black;
    mDragAlso = new GraphicEntity[0];
  }

  public void dragAlso(GraphicEntity entity) {
    mDragAlso = (GraphicEntity[])Util.append(mDragAlso, Types.GRAPHIC_ENTITY, entity);
  }

  public void noDragAlso(GraphicEntity entity) {
    mDragAlso = (GraphicEntity[])Util.remove(mDragAlso, Types.GRAPHIC_ENTITY, entity);
  }

  public void draw(Graphics g) {}

  public void update() {
  }

  public void getExtents(Rectangle extents) {
    extents.x = 0;
    extents.y = 0;
    extents.width = 0;
    extents.height = 0;
  }

  public boolean hit(Point p, int viewXOffset, int viewYOffset) {
    if (mPosRelativeTo == GLOBAL_RELATIVE) {
      tmpPoint.x = p.x - viewXOffset;
      tmpPoint.y = p.y - viewYOffset;
      return hit(tmpPoint);
    }
    return hit(p);
  }

  public boolean hit(Point p) {return false;}

  public boolean isButton() {return false;}

  public void prepareDrag(Point p) {
    for (int i = 0; i < mDragAlso.length; i++) {
      mDragAlso[i].prepareDrag(p);
    }
  }

  public void dragged(Point p, int viewXOffset, int viewYOffset) {
    if (mPosRelativeTo == GLOBAL_RELATIVE) {
      tmpPoint.x = p.x - viewXOffset;
      tmpPoint.y = p.y - viewYOffset;
      dragged(tmpPoint);
    }
    else {
      dragged(p);
    }

    for (int i = 0; i < mDragAlso.length; i++) {
      mDragAlso[i].dragged(p, viewXOffset, viewYOffset);
    }
  }

  public void dragged(Point p) {
//    for (int i = 0; i < mDragAlso.length; i++) {
//      mDragAlso[i].dragged(p);
//    }
  }
}