/**
    Copyright 2004-2008 Ricard Marxer  <email@ricardmarxer.com>

    This file is part of Geomerative.

    Geomerative is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Geomerative is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with Geomerative.  If not, see <http://www.gnu.org/licenses/>.
*/

package geomerative;
import processing.core.*;

/**
 * R is a static class containing all the states, modes, etc..
 */
public class RG implements PConstants{
  private static boolean initialized = false;
  private static PApplet parent;

  /**
   * @invisible
   */  
  public static boolean ignoreStyles = false;

  /**
   * @invisible
   */  
  public static boolean useFastClip = true;

  /**
   * @invisible
   */
  public final static int BYPOINT = 0;
  
  /**
   * @invisible
   */
  public final static int BYELEMENTPOSITION = 1;
  
  /**
   * @invisible
   */
  public final static int BYELEMENTINDEX = 2;
  
  static int adaptorType = BYELEMENTPOSITION;
  static float adaptorScale = 1F;
  static float adaptorLengthOffset = 0F;



  public static class LibraryNotInitializedException extends NullPointerException{
    private static final long serialVersionUID = -3710605630786298671L;

    LibraryNotInitializedException(){
      super("Must call RG.init(this); before using this library.");
    }
  }

  public static class FontNotLoadedException extends NullPointerException{
    private static final long serialVersionUID = -3710605630786298672L;

    FontNotLoadedException(){
      super("Must load a font using RG.loadFont(filename, size) first.");
    }
  }

  public static class NoPathInitializedException extends NullPointerException{
    private static final long serialVersionUID = -3710605630786298673L;

    NoPathInitializedException(){
      super("Must initialize a path by calling RG.beginGroup(), RG.beginShape() or RG.beginPath() first.");
    }
  }


  static RShape shape;
  static RGroup group;
  static RPath path;

  static RFont fntLoader;

  static boolean shapeBegin = false;

  public static void loadFont(String font, int size){
    fntLoader = new RFont(font, size);
  }

  public static RGroup loadText(String font, String text, int size){
    RFont tempFntLoader = new RFont(font, size);
    return tempFntLoader.toGroup(text);
  }

  public static RGroup loadText(String text){
    if(fntLoader == null){
      throw new FontNotLoadedException();      
    }
    
    return fntLoader.toGroup(text);
  }

  public static RGroup loadSVG(String filename){
    RSVG svgLoader = new RSVG();
    return svgLoader.toGroup(filename);    
  }

  // Methods to create shapes
  public static void beginShape(){
    shape = new RShape();
  }

  public static void breakShape(){
    shape.addPath();
    shapeBegin = true;
  }

  public static void breakShape(int endMode){
    if (endMode == CLOSE) {
      shape.addClose();
    }
    breakShape();
  }

  public static void vertex(float x, float y){
    if(path == null){
      if (shape.countPaths() == 0){
        shape.addMoveTo(x, y);
      }else{
        if (shapeBegin){
          shape.addMoveTo(x, y);
          shapeBegin = false;
        }
        shape.addLineTo(x, y);
      }
    }else{
      path.addLineTo(x, y);
    }
  }

  public static void bezierVertex(float cx1, float cy1, float cx2, float cy2, float x, float y){
    if(path == null){
      if (shape.countPaths() == 0){
        throw new NoPathInitializedException();
      }else{
        shape.addBezierTo(cx1, cy1, cx2, cy2, x, y);
      }
    }else{
      path.addBezierTo(cx1, cy1, cx2, cy2, x, y);
    }
  }


  public static void endShape(PGraphics g){
    if(group == null){
      // We are not inside a beginGroup
      shape.draw(g);
    }else{
      // We are inside a beginGroup
      group.addElement(shape);
      
      shape = null;
    }
    
    shape = null;
  }

  public static void endShape(){
    shape.draw();
    shape = null;
  }


  public static RGroup getShape(){
    RGroup returningGroup = new RGroup();
    returningGroup.addElement(shape);

    shape = null;

    return returningGroup;    
  }

  


  public static void init(PApplet _parent){
    parent = _parent;
    initialized = true;
  }
  
  public static boolean initialized() {
    return initialized;
  }

  protected static PApplet parent(){
    if(parent == null){
      throw new LibraryNotInitializedException();
    }
    
    return parent;
  }

  public static void ignoreStyles(){
    ignoreStyles = true;
  }

  public static void ignoreStyles(boolean _value){
    ignoreStyles = _value;
  }

  /**
   * Use this to set the adaptor type.  RGroup.BYPOINT adaptor adapts the group to a particular shape by adapting each of the groups points.  This can cause deformations of the individual elements in the group.  RGroup.BYELEMENT adaptor adapts the group to a particular shape by adapting each of the groups elements.  This mantains the proportions of the shapes.
   * @eexample RGroup_setAdaptor
   * @param int adptorType, it can take the values RGroup.BYPOINT and RGroup.BYELEMENT
   * */
  public static void setAdaptor(int adptorType){
    adaptorType = adptorType;
  }
  
  /**
   * Use this to set the adaptor scaling.  This scales the transformation of the adaptor.
   * @eexample RGroup_setAdaptor
   * @param float adptorScale, the scaling coefficient
   * */
  public static void setAdaptorScale(float adptorScale){
    adaptorScale = adptorScale;
  }
  
  /**
   * Use this to set the adaptor length offset.  This specifies where to start adapting the group to the shape.
   * @eexample RGroup_setAdaptorLengthOffset
   * @param float adptorLengthOffst, the offset along the curve of the shape. Must be a value between 0 and 1;
   * */
  public static void setAdaptorLengthOffset(float adptorLengthOffset) throws RuntimeException{
    if(adptorLengthOffset>=0F && adptorLengthOffset<=1F)
      adaptorLengthOffset = adptorLengthOffset;
    else
      throw new RuntimeException("The adaptor length offset must take a value between 0 and 1.");
  }
}