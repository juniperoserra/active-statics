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
import java.util.*;
import java.lang.reflect.*;

public class Util {

  public Util() {
  }

  public static Color copyColor(Color src) {
    return new Color(src.getRed(), src.getGreen(), src.getBlue());
  }

  public static void sleep(long millis) {
    try {
      Thread.sleep(millis);
    }
    catch (Exception e) {
    }
  }

  public static float distance(float x1, float y1, float x2, float y2) {
    return (float)Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
  }

  public static float slope(float x1, float y1, float x2, float y2) {
    if (x1 == x2)
      if (y2 >= y1)
        return (float)1e+5;   // close enough to infinity
      else
        return (float)-1e+5;   // close enough to -infinity
    return (y2 - y1) / (x2 - x1);
  }

//  public static float distance(Point p1, Point p2) {
//    return (float)Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
//  }

  public static float direction(float x1, float y1, float x2, float y2) {
/*    if (y1 == y2) {
      if (x1 > x2)
        return (float)Math.PI;
      else
        return -(float)Math.PI;
    }*/
    float dir = (float)Math.atan2(y2 - y1, x2 - x1);
    if (dir < 0)
      dir += (float)Math.PI * 2.0f;
    if (near(dir, Math.PI * 2.0, .0001)) {
      dir = 0.0f;
    }
    return dir;
  }

//  public static float direction(Point p1, Point p2) {
//    return (float)Math.atan2(p2.y - p1.y, p2.x - p1.x );
//  }

    private static final Util instance = new Util(); // Util is a singleton

    public static Util getInstance() {
        return instance;
    }

    public static double clamp(double value, double low, double high) {
      return Math.max(Math.min(value, high ), low);
    }

    public static String round(double d, int place)
    {
      if (place <= 0)
        return ""+(int)(d+((d > 0)? 0.5 : -0.5));
      String s = "";
      if (d < 0)
        {
  	s += "-";
  	d = -d;
        }
      d += 0.5*Math.pow(10,-place);
      if (d > 1)
        {
  	int i = (int)d;
  	s += i;
  	d -= i;
        }
      else
        s += "0";
      if (d > 0)
        {
  	d += 1.0;
  	String f = ""+(int)(d*Math.pow(10,place));
  	s += "."+f.substring(1);
        }
      return s;
    }

    public void shiftRight(Object[] array, int index) {
        for (int i = index; i > 0; i--)
            array[i] = array[i-1];
    }

    public void shiftLeft(Object[] array, int index) {
        for (int i = index; i < array.length - 1; i++)
            array[i] = array[i+1];
    }

    public static boolean near (double first, double second, double tolerance) {
        return (java.lang.Math.abs(first - second) <= tolerance);
    }

    public static int[] append(int[] array, int newInt) {
      int[] newArray = new int[array.length + 1];
      System.arraycopy(array, 0, newArray, 0, array.length);
      newArray[array.length] = newInt;
      return newArray;
    }

    public static double[] append(double[] array, double newDouble) {
      double[] newArray = new double[array.length + 1];
      System.arraycopy(array, 0, newArray, 0, array.length);
      newArray[array.length] = newDouble;
      return newArray;
    }

    public static boolean[] removeBoolean(boolean[] array, int index) {
      boolean[] newArray = new boolean[array.length - 1];
      System.arraycopy(array, 0, newArray, 0, index);
      System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
      return newArray;
    }

    public static double[] removeDouble(double[] array, int index) {
      double[] newArray = new double[array.length - 1];
      System.arraycopy(array, 0, newArray, 0, index);
      System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
      return newArray;
    }

    public static int[] removeIndex(int[] array, int index) {
      int[] newArray = new int[array.length - 1];
      System.arraycopy(array, 0, newArray, 0, index);
      System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
      return newArray;
    }

    public static int[] removeInt(int[] array, int oldItem) {
        int index = find(array, oldItem);
        if (index == -1)
            return array;
        return removeIndex(array, index);
    }

    public static int find(int[] array, int value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == value)
                return i;
        }
        return -1;
    }

    public static Object[] append(Object[] array, Class type, Object newItem) {
        Object[] newArray = (Object[])Array.newInstance(type, array.length + 1);
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = newItem;
        return newArray;
    }

    public static Object[] insert(Object[] array, Class type, Object newItem, int index) {
      Object[] newArray = (Object[])Array.newInstance(type, array.length + 1);
      System.arraycopy(array, 0, newArray, 0, index);
      newArray[index] = newItem;
      System.arraycopy(array, index, newArray, index + 1, array.length - index);
      return newArray;
    }

    public static boolean[] insertBoolean(boolean[] array, boolean newItem, int index) {
      boolean[] newArray = new boolean[array.length + 1];
      System.arraycopy(array, 0, newArray, 0, index);
      newArray[index] = newItem;
      System.arraycopy(array, index, newArray, index + 1, array.length - index);
      return newArray;
    }

    public static double[] insertDouble(double[] array, double newItem, int index) {
      double[] newArray = new double[array.length + 1];
      System.arraycopy(array, 0, newArray, 0, index);
      newArray[index] = newItem;
      System.arraycopy(array, index, newArray, index + 1, array.length - index);
      return newArray;
    }

    public static Object[] addFront(Object[] array, Class type, Object newItem) {
        Object[] newArray = (Object[])Array.newInstance(type, array.length + 1);
        System.arraycopy(array, 0, newArray, 1, array.length);
        newArray[0] = newItem;
        return newArray;
    }

    public static Object[] remove(Object[] array, Class type, int index) {
        Object[] newArray = (Object[])Array.newInstance(type, array.length - 1);
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, newArray.length - index);
        return newArray;
    }

    public static Object[] remove(Object[] array, Class type, Object oldItem) {
        int index = find(array, oldItem);
        if (index == -1)
            return array;
        return remove(array, type, index);
    }

    public static int find(Object[] array, Object item) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(item))
                return i;
        }
        return -1;
    }

    public static boolean contains(Object[] array, Object item) {
        return find(array, item) != -1;
    }

    public static void tr(String trace) {
        System.out.println(trace);
    }

    public static float bound (float value, float lower, float upper) {
        return (float)java.lang.Math.min(java.lang.Math.max(value, lower), upper);
    }
}