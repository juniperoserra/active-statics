package truss;

/**
 * Title:        Truss
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Simon Greenwold
 * @version 1.0
 */

public class Types {

  public static Class GRAPHIC_ENTITY;
  public static Class TIMER_JOB;

  public Types() {
    GRAPHIC_ENTITY = new GraphicEntity().getClass();
    TIMER_JOB = new TimerJob().getClass();
  }
}