package io.jbp.depthimagetest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DepthImage
{
  private static final String TAG = "DepthImage";

  public double blurAtInfinity;
  public double focalDistance;
  public double focalPointX;
  public double focalPointY;

  public String depthFormat;
  public String depthMime;
  public double depthNear;
  public double depthFar;
  public byte[] depthImage;

  public String colourMime;
  public byte[] colourImage;

  public boolean isValid() //!!!!!!!!!!!!!!!
  {
    return (blurAtInfinity != Double.NaN &&
        focalDistance != Double.NaN &&
        focalPointX != Double.NaN &&
        focalPointY != Double.NaN &&
        depthFormat != null &&
        depthMime != null &&
        depthNear != Double.NaN &&
        depthFar != Double.NaN &&
        depthImage != null &&
        colourMime != null && colourImage != null);
  }

  private Bitmap readBitmap(byte[] which, String label)
  {
    BitmapFactory.Options opts = new BitmapFactory.Options();
    opts.inScaled = false;
    Bitmap bm = BitmapFactory.decodeByteArray(which, 0, which.length, opts);
    Log.v(TAG, String.format("%s bitmap is %dx%d pixels", label, opts.outWidth, opts.outHeight));
    return bm;
  }

  public Bitmap getColourBitmap()
  {
    return readBitmap(colourImage, "colour");
  } //!!!!!!!!!!!!

  public Bitmap getDepthBitmap()
  {
    return readBitmap(depthImage, "depth");
  } //!!!!!!!!!!!!!!!

  private static String readStringAttr(String img, String attr)
  {
    Pattern pat = Pattern.compile(attr + "=\"([^\"]*)\"", Pattern.CASE_INSENSITIVE);
    Matcher m = pat.matcher(img);

    if (!m.find())
      return null;
    return m.group(1);
  }

  private static double readFloatAttr(String img, String attr)
  {
    String s = readStringAttr(img, attr);
    if (s == null)
      return Double.NaN;
    return Double.parseDouble(s);
  }
  
  public static DepthImage loadImage(JPEG image) //!!!!!!!!!!!!!!
  {
    byte[] xmpBA = image.getStandardXMPBlockContent();
    String xmp = new String(xmpBA);

    DepthImage di = new DepthImage();

    di.colourMime = readStringAttr(xmp, "GImage:Mime");
    if (di.colourMime == null)
      return di; /* Early fail */

    di.blurAtInfinity = readFloatAttr(xmp, "GFocus:BlurAtInfinity");
    di.focalDistance = readFloatAttr(xmp, "GFocus:FocalDistance");
    di.focalPointX = readFloatAttr(xmp, "GFocus:FocalPointX");
    di.focalPointY = readFloatAttr(xmp, "GFocus:FocalPointY");

    di.depthFormat = readStringAttr(xmp, "GDepth:Format");
    di.depthMime = readStringAttr(xmp, "GDepth:Mime");
    di.depthNear = readFloatAttr(xmp, "GDepth:Near");
    di.depthFar = readFloatAttr(xmp, "GDepth:Far");

    di.depthImage = image.exportDepthMap(true);
    di.colourImage = image.exportSourceImage(true);

    return di;
  }
}
