/*
 * Copyright (c) 2016-2017. Vijai Chandra Prasad R.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */

package com.orpheusdroid.screenrecorder.encoder;

/**
 * Created by vijai on 31-08-2017.
 */

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.IOException;
import java.io.OutputStream;

public class GifEncoder {

    protected int width; /*// image size*/

    protected int height;

    protected int x = 0;

    protected int y = 0;

    protected int transparent = -1; /*// transparent color if given*/

    protected int transIndex; /*// transparent index in color table*/

    protected int repeat = -1; /*// no repeat*/

    protected int delay = 0; /*// frame delay (hundredths)*/

    protected boolean started = false; /*// ready to output frames*/

    protected OutputStream out;

    protected Bitmap image; /*// current frame*/

    protected byte[] pixels; /*// BGR byte array from frame*/

    protected byte[] indexedPixels; /*// converted frame indexed to palette*/

    protected int colorDepth; /*// number of bit planes*/

    protected byte[] colorTab; /*// RGB palette*/

    protected boolean[] usedEntry = new boolean[256]; /*// active palette entries*/

    protected int palSize = 7; /*// color table size (bits-1)*/

    protected int dispose = -1; /*// disposal code (-1 = use default)*/

    protected boolean closeStream = false; /*// close stream when finished*/

    protected boolean firstFrame = true;

    protected boolean sizeSet = false; /*// if false, get size from first frame*/

    protected int sample = 10; /*// default sample interval for quantizer*/

    /**
     * Sets the delay time between each frame, or changes it for subsequent frames
     * (applies to last frame added).
     *
     * @param ms
     *          int delay time in milliseconds
     */
    public void setDelay(int ms) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setDelay(int)",this,ms);try{delay = ms / 10;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setDelay(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setDelay(int)",this,throwable);throw throwable;}
    }

    /**
     * Sets the GIF frame disposal code for the last added frame and any
     * subsequent frames. Default is 0 if no transparent color has been set,
     * otherwise 2.
     *
     * @param code
     *          int disposal code.
     */
    public void setDispose(int code) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setDispose(int)",this,code);try{if (code >= 0) {
            dispose = code;
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setDispose(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setDispose(int)",this,throwable);throw throwable;}
    }

    /**
     * Sets the number of times the set of GIF frames should be played. Default is
     * 1; 0 means play indefinitely. Must be invoked before the first image is
     * added.
     *
     * @param iter
     *          int number of iterations.
     * @return
     */
    public void setRepeat(int iter) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setRepeat(int)",this,iter);try{if (iter >= 0) {
            repeat = iter;
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setRepeat(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setRepeat(int)",this,throwable);throw throwable;}
    }

    /**
     * Sets the transparent color for the last added frame and any subsequent
     * frames. Since all colors are subject to modification in the quantization
     * process, the color in the final palette for each frame closest to the given
     * color becomes the transparent color for that frame. May be set to null to
     * indicate no transparent color.
     *
     * @param c
     *          Color to be treated as transparent on display.
     */
    public void setTransparent(int c) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setTransparent(int)",this,c);try{transparent = c;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setTransparent(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setTransparent(int)",this,throwable);throw throwable;}
    }

    /**
     * Adds next GIF frame. The frame is not written immediately, but is actually
     * deferred until the next frame is received so that timing data can be
     * inserted. Invoking <code>finish()</code> flushes all frames. If
     * <code>setSize</code> was not invoked, the size of the first image is used
     * for all subsequent frames.
     *
     * @param im
     *          BufferedImage containing frame to write.
     * @return true if successful.
     */
    public boolean addFrame(Bitmap im) {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.addFrame(android.graphics.Bitmap)",this,im);try{if ((im == null) || !started) {
            {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.addFrame(android.graphics.Bitmap)",this);return false;}
        }
        boolean ok = true;
        try {
            if (!sizeSet) {
                /*// use first frame's size*/
                setSize(im.getWidth(), im.getHeight());
            }
            image = im;
            getImagePixels(); /*// convert to correct format if necessary*/
            analyzePixels(); /*// build color table & map pixels*/
            if (firstFrame) {
                writeLSD(); /*// logical screen descriptior*/
                writePalette(); /*// global color table*/
                if (repeat >= 0) {
                    /*// use NS app extension to indicate reps*/
                    writeNetscapeExt();
                }
            }
            writeGraphicCtrlExt(); /*// write graphic control extension*/
            writeImageDesc(); /*// image descriptor*/
            if (!firstFrame) {
                writePalette(); /*// local color table*/
            }
            writePixels(); /*// encode and write pixel data*/
            firstFrame = false;
        } catch (IOException e) {
            ok = false;
        }

        {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.addFrame(android.graphics.Bitmap)",this);return ok;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.addFrame(android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    /**
     * Flushes any pending data and closes output file. If writing to an
     * OutputStream, the stream is not closed.
     */
    public boolean finish() {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.finish()",this);try{if (!started)
            {{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.finish()",this);return false;}}
        boolean ok = true;
        started = false;
        try {
            out.write(0x3b); /*// gif trailer*/
            out.flush();
            if (closeStream) {
                out.close();
            }
        } catch (IOException e) {
            ok = false;
        }

        /*// reset for subsequent use*/
        transIndex = 0;
        out = null;
        image = null;
        pixels = null;
        indexedPixels = null;
        colorTab = null;
        closeStream = false;
        firstFrame = true;

        {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.finish()",this);return ok;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.finish()",this,throwable);throw throwable;}
    }

    /**
     * Sets frame rate in frames per second. Equivalent to
     * <code>setDelay(1000/fps)</code>.
     *
     * @param fps
     *          float frame rate (frames per second)
     */
    public void setFrameRate(float fps) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setFrameRate(float)",this,fps);try{if (fps != 0f) {
            delay = (int)(100 / fps);
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setFrameRate(float)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setFrameRate(float)",this,throwable);throw throwable;}
    }

    /**
     * Sets quality of color quantization (conversion of images to the maximum 256
     * colors allowed by the GIF specification). Lower values (minimum = 1)
     * produce better colors, but slow processing significantly. 10 is the
     * default, and produces good color mapping at reasonable speeds. Values
     * greater than 20 do not yield significant improvements in speed.
     *
     * @param quality
     *          int greater than 0.
     * @return
     */
    public void setQuality(int quality) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setQuality(int)",this,quality);try{if (quality < 1)
            {quality = 1;}
        sample = quality;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setQuality(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setQuality(int)",this,throwable);throw throwable;}
    }

    /**
     * Sets the GIF frame size. The default size is the size of the first frame
     * added if this method is not invoked.
     *
     * @param w
     *          int frame width.
     * @param h
     *          int frame width.
     */
    public void setSize(int w, int h) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setSize(int,int)",this,w,h);try{width = w;
        height = h;
        if (width < 1)
            {width = 320;}
        if (height < 1)
            {height = 240;}
        sizeSet = true;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setSize(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setSize(int,int)",this,throwable);throw throwable;}
    }

    /**
     * Sets the GIF frame position. The position is 0,0 by default.
     * Useful for only updating a section of the image
     *
     * @param w
     *          int frame width.
     * @param h
     *          int frame width.
     */
    public void setPosition(int x, int y) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setPosition(int,int)",this,x,y);try{this.x = x;
        this.y = y;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setPosition(int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.setPosition(int,int)",this,throwable);throw throwable;}
    }

    /**
     * Initiates GIF file creation on the given stream. The stream is not closed
     * automatically.
     *
     * @param os
     *          OutputStream on which GIF images are written.
     * @return false if initial write failed.
     */
    public boolean start(OutputStream os) {
        com.mijack.Xlog.logMethodEnter("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.start(java.io.OutputStream)",this,os);try{if (os == null)
            {{com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.start(java.io.OutputStream)",this);return false;}}
        boolean ok = true;
        closeStream = false;
        out = os;
        try {
            writeString("GIF89a"); /*// header*/
        } catch (IOException e) {
            ok = false;
        }
        {com.mijack.Xlog.logMethodExit("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.start(java.io.OutputStream)",this);return started = ok;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("boolean com.orpheusdroid.screenrecorder.encoder.GifEncoder.start(java.io.OutputStream)",this,throwable);throw throwable;}
    }

    /**
     * Analyzes image colors and creates color map.
     */
    protected void analyzePixels() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.analyzePixels()",this);try{int len = pixels.length;
        int nPix = len / 3;
        indexedPixels = new byte[nPix];
        NeuQuant nq = new NeuQuant(pixels, len, sample);
        /*// initialize quantizer*/
        colorTab = nq.process(); /*// create reduced palette*/
        /*// convert map from BGR to RGB*/
        for (int i = 0; i < colorTab.length; i += 3) {
            byte temp = colorTab[i];
            colorTab[i] = colorTab[i + 2];
            colorTab[i + 2] = temp;
            usedEntry[i / 3] = false;
        }
        /*// map image pixels to new palette*/
        int k = 0;
        for (int i = 0; i < nPix; i++) {
            int index = nq.map(pixels[k++] & 0xff, pixels[k++] & 0xff, pixels[k++] & 0xff);
            usedEntry[index] = true;
            indexedPixels[i] = (byte) index;
        }
        pixels = null;
        colorDepth = 8;
        palSize = 7;
        /*// get closest match to transparent color if specified*/
        if (transparent != -1) {
            transIndex = findClosest(transparent);
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.analyzePixels()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.analyzePixels()",this,throwable);throw throwable;}
    }

    /**
     * Returns index of palette color closest to c
     *
     */
    protected int findClosest(int c) {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.encoder.GifEncoder.findClosest(int)",this,c);try{if (colorTab == null)
            {{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.encoder.GifEncoder.findClosest(int)",this);return -1;}}
        int r = (c >> 16) & 0xff;
        int g = (c >> 8) & 0xff;
        int b = (c >> 0) & 0xff;
        int minpos = 0;
        int dmin = 256 * 256 * 256;
        int len = colorTab.length;
        for (int i = 0; i < len;) {
            int dr = r - (colorTab[i++] & 0xff);
            int dg = g - (colorTab[i++] & 0xff);
            int db = b - (colorTab[i] & 0xff);
            int d = dr * dr + dg * dg + db * db;
            int index = i / 3;
            if (usedEntry[index] && (d < dmin)) {
                dmin = d;
                minpos = index;
            }
            i++;
        }
        {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.encoder.GifEncoder.findClosest(int)",this);return minpos;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.encoder.GifEncoder.findClosest(int)",this,throwable);throw throwable;}
    }

    /**
     * Extracts image pixels into byte array "pixels"
     */
    protected void getImagePixels() {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.getImagePixels()",this);try{int w = image.getWidth();
        int h = image.getHeight();
        if ((w != width) || (h != height)) {
            /*// create new image with right size/format*/
            Bitmap temp = Bitmap.createBitmap(width, height, Config.RGB_565);
            Canvas g = new Canvas(temp);
            g.drawBitmap(image, 0, 0, new Paint());
            image = temp;
        }
        int[] data = getImageData(image);
        pixels = new byte[data.length * 3];
        for (int i = 0; i < data.length; i++) {
            int td = data[i];
            int tind = i * 3;
            pixels[tind++] = (byte) ((td >> 0) & 0xFF);
            pixels[tind++] = (byte) ((td >> 8) & 0xFF);
            pixels[tind] = (byte) ((td >> 16) & 0xFF);
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.getImagePixels()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.getImagePixels()",this,throwable);throw throwable;}
    }
    protected int[] getImageData(Bitmap img) {
        com.mijack.Xlog.logMethodEnter("[int com.orpheusdroid.screenrecorder.encoder.GifEncoder.getImageData(android.graphics.Bitmap)",this,img);try{int w = img.getWidth();
        int h = img.getHeight();

        int[] data = new int[w * h];
        img.getPixels(data, 0, w, 0, 0, w, h);
        {com.mijack.Xlog.logMethodExit("[int com.orpheusdroid.screenrecorder.encoder.GifEncoder.getImageData(android.graphics.Bitmap)",this);return data;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[int com.orpheusdroid.screenrecorder.encoder.GifEncoder.getImageData(android.graphics.Bitmap)",this,throwable);throw throwable;}
    }

    /**
     * Writes Graphic Control Extension
     */
    protected void writeGraphicCtrlExt() throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeGraphicCtrlExt()",this);try{out.write(0x21); /*// extension introducer*/
        out.write(0xf9); /*// GCE label*/
        out.write(4); /*// data block size*/
        int transp, disp;
        if (transparent == -1) {
            transp = 0;
            disp = 0; /*// dispose = no action*/
        } else {
            transp = 1;
            disp = 2; /*// force clear if using transparent color*/
        }
        if (dispose >= 0) {
            disp = dispose & 7; /*// user override*/
        }
        disp <<= 2;

        /*// packed fields*/
        out.write(0 | /*// 1:3 reserved*/
                disp | /*// 4:6 disposal*/
                0 | /*// 7 user input - 0 = none*/
                transp); /*// 8 transparency flag*/

        writeShort(delay); /*// delay x 1/100 sec*/
        out.write(transIndex); /*// transparent color index*/
        out.write(0); /*// block terminator*/com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeGraphicCtrlExt()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeGraphicCtrlExt()",this,throwable);throw throwable;}
    }

    /**
     * Writes Image Descriptor
     */
    protected void writeImageDesc() throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeImageDesc()",this);try{out.write(0x2c); /*// image separator*/
        writeShort(x); /*// image position x,y = 0,0*/
        writeShort(y);
        writeShort(width); /*// image size*/
        writeShort(height);
        /*// packed fields*/
        if (firstFrame) {
            /*// no LCT - GCT is used for first (or only) frame*/
            out.write(0);
        } else {
            /*// specify normal LCT*/
            out.write(0x80 | /*// 1 local color table 1=yes*/
                    0 | /*// 2 interlace - 0=no*/
                    0 | /*// 3 sorted - 0=no*/
                    0 | /*// 4-5 reserved*/
                    palSize); /*// 6-8 size of color table*/
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeImageDesc()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeImageDesc()",this,throwable);throw throwable;}
    }

    /**
     * Writes Logical Screen Descriptor
     */
    protected void writeLSD() throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeLSD()",this);try{/*// logical screen size*/
        writeShort(width);
        writeShort(height);
        /*// packed fields*/
        out.write((0x80 | /*// 1 : global color table flag = 1 (gct used)*/
                0x70 | /*// 2-4 : color resolution = 7*/
                0x00 | /*// 5 : gct sort flag = 0*/
                palSize)); /*// 6-8 : gct size*/

        out.write(0); /*// background color index*/
        out.write(0); /*// pixel aspect ratio - assume 1:1*/com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeLSD()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeLSD()",this,throwable);throw throwable;}
    }

    /**
     * Writes Netscape application extension to define repeat count.
     */
    protected void writeNetscapeExt() throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeNetscapeExt()",this);try{out.write(0x21); /*// extension introducer*/
        out.write(0xff); /*// app extension label*/
        out.write(11); /*// block size*/
        writeString("NETSCAPE" + "2.0"); /*// app id + auth code*/
        out.write(3); /*// sub-block size*/
        out.write(1); /*// loop sub-block id*/
        writeShort(repeat); /*// loop count (extra iterations, 0=repeat forever)*/
        out.write(0); /*// block terminator*/com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeNetscapeExt()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeNetscapeExt()",this,throwable);throw throwable;}
    }

    /**
     * Writes color table
     */
    protected void writePalette() throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writePalette()",this);try{out.write(colorTab, 0, colorTab.length);
        int n = (3 * 256) - colorTab.length;
        for (int i = 0; i < n; i++) {
            out.write(0);
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writePalette()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writePalette()",this,throwable);throw throwable;}
    }

    /**
     * Encodes and writes pixel data
     */
    protected void writePixels() throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writePixels()",this);try{LZWEncoder encoder = new LZWEncoder(width, height, indexedPixels, colorDepth);
        encoder.encode(out);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writePixels()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writePixels()",this,throwable);throw throwable;}
    }

    /**
     * Write 16-bit value to output stream, LSB first
     */
    protected void writeShort(int value) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeShort(int)",this,value);try{out.write(value & 0xff);
        out.write((value >> 8) & 0xff);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeShort(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeShort(int)",this,throwable);throw throwable;}
    }

    /**
     * Writes string to output stream
     */
    protected void writeString(String s) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeString(java.lang.String)",this,s);try{for (int i = 0; i < s.length(); i++) {
            out.write((byte) s.charAt(i));
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeString(java.lang.String)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.GifEncoder.writeString(java.lang.String)",this,throwable);throw throwable;}
    }
}

/*//	 Ported to Java 12/00 K Weiner*/
class NeuQuant {

    protected static final int netsize = 256; /* number of colours used */

    /* four primes near 500 - assume no image has a length so large */
	  /* that it is divisible by all four primes */
    protected static final int prime1 = 499;

    protected static final int prime2 = 491;

    protected static final int prime3 = 487;

    protected static final int prime4 = 503;

    protected static final int minpicturebytes = (3 * prime4);

	  /* minimum size for input image */

	  /*
	   * Program Skeleton ---------------- [select samplefac in range 1..30] [read
	   * image from input file] pic = (unsigned char*) malloc(3*width*height);
	   * initnet(pic,3*width*height,samplefac); learn(); unbiasnet(); [write output
	   * image header, using writecolourmap(f)] inxbuild(); write output image using
	   * inxsearch(b,g,r)
	   */

	  /*
	   * Network Definitions -------------------
	   */

    protected static final int maxnetpos = (netsize - 1);

    protected static final int netbiasshift = 4; /* bias for colour values */

    protected static final int ncycles = 100; /* no. of learning cycles */

    /* defs for freq and bias */
    protected static final int intbiasshift = 16; /* bias for fractions */

    protected static final int intbias = (1 << intbiasshift);

    protected static final int gammashift = 10; /* gamma = 1024 */

    protected static final int gamma = (1 << gammashift);

    protected static final int betashift = 10;

    protected static final int beta = (intbias >> betashift); /* beta = 1/1024 */

    protected static final int betagamma = (intbias << (gammashift - betashift));

    /* defs for decreasing radius factor */
    protected static final int initrad = (netsize >> 3); /*
	                                                         * for 256 cols, radius
	                                                         * starts
	                                                         */

    protected static final int radiusbiasshift = 6; /* at 32.0 biased by 6 bits */

    protected static final int radiusbias = (1 << radiusbiasshift);

    protected static final int initradius = (initrad * radiusbias); /*
	                                                                   * and
	                                                                   * decreases
	                                                                   * by a
	                                                                   */

    protected static final int radiusdec = 30; /* factor of 1/30 each cycle */

    /* defs for decreasing alpha factor */
    protected static final int alphabiasshift = 10; /* alpha starts at 1.0 */

    protected static final int initalpha = (1 << alphabiasshift);
    /* radbias and alpharadbias used for radpower calculation */
    protected static final int radbiasshift = 8;
    protected static final int radbias = (1 << radbiasshift);
    protected static final int alpharadbshift = (alphabiasshift + radbiasshift);
    protected static final int alpharadbias = (1 << alpharadbshift);
    protected int alphadec; /* biased by 10 bits */

	  /*
	   * Types and Global Variables --------------------------
	   */
    protected byte[] thepicture; /* the input image itself */

    protected int lengthcount; /* lengthcount = H*W*3 */

    protected int samplefac; /* sampling factor 1..30 */

    /*// typedef int pixel[4]; /* BGRc */
    protected int[][] network; /* the network itself - [netsize][4] */

    protected int[] netindex = new int[256];

	  /* for network lookup - really 256 */

    protected int[] bias = new int[netsize];

    /* bias and freq arrays for learning */
    protected int[] freq = new int[netsize];

    protected int[] radpower = new int[initrad];

	  /* radpower for precomputation */

    /*
     * Initialise network in range (0,0,0) to (255,255,255) and set parameters
     * -----------------------------------------------------------------------
     */
    public NeuQuant(byte[] thepic, int len, int sample) {

        int i;
        int[] p;

        thepicture = thepic;
        lengthcount = len;
        samplefac = sample;

        network = new int[netsize][];
        for (i = 0; i < netsize; i++) {
            network[i] = new int[4];
            p = network[i];
            p[0] = p[1] = p[2] = (i << (netbiasshift + 8)) / netsize;
            freq[i] = intbias / netsize; /* 1/netsize */
            bias[i] = 0;
        }
    }

    public byte[] colorMap() {
        com.mijack.Xlog.logMethodEnter("[byte com.orpheusdroid.screenrecorder.encoder.NeuQuant.colorMap()",this);try{byte[] map = new byte[3 * netsize];
        int[] index = new int[netsize];
        for (int i = 0; i < netsize; i++)
            {index[network[i][3]] = i;}
        int k = 0;
        for (int i = 0; i < netsize; i++) {
            int j = index[i];
            map[k++] = (byte) (network[j][0]);
            map[k++] = (byte) (network[j][1]);
            map[k++] = (byte) (network[j][2]);
        }
        {com.mijack.Xlog.logMethodExit("[byte com.orpheusdroid.screenrecorder.encoder.NeuQuant.colorMap()",this);return map;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.orpheusdroid.screenrecorder.encoder.NeuQuant.colorMap()",this,throwable);throw throwable;}
    }

    /*
     * Insertion sort of network and building of netindex[0..255] (to do after
     * unbias)
     * -------------------------------------------------------------------------------
     */
    public void inxbuild() {

        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.inxbuild()",this);try{int i, j, smallpos, smallval;
        int[] p;
        int[] q;
        int previouscol, startpos;

        previouscol = 0;
        startpos = 0;
        for (i = 0; i < netsize; i++) {
            p = network[i];
            smallpos = i;
            smallval = p[1]; /* index on g */
	      /* find smallest in i..netsize-1 */
            for (j = i + 1; j < netsize; j++) {
                q = network[j];
                if (q[1] < smallval) { /* index on g */
                    smallpos = j;
                    smallval = q[1]; /* index on g */
                }
            }
            q = network[smallpos];
	      /* swap p (i) and q (smallpos) entries */
            if (i != smallpos) {
                j = q[0];
                q[0] = p[0];
                p[0] = j;
                j = q[1];
                q[1] = p[1];
                p[1] = j;
                j = q[2];
                q[2] = p[2];
                p[2] = j;
                j = q[3];
                q[3] = p[3];
                p[3] = j;
            }
	      /* smallval entry is now in position i */
            if (smallval != previouscol) {
                netindex[previouscol] = (startpos + i) >> 1;
                for (j = previouscol + 1; j < smallval; j++)
                    {netindex[j] = i;}
                previouscol = smallval;
                startpos = i;
            }
        }
        netindex[previouscol] = (startpos + maxnetpos) >> 1;
        for (j = previouscol + 1; j < 256; j++)
            {netindex[j] = maxnetpos;} /* really 256 */com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.inxbuild()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.inxbuild()",this,throwable);throw throwable;}
    }

    /*
     * Main Learning Loop ------------------
     */
    public void learn() {

        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.learn()",this);try{int i, j, b, g, r;
        int radius, rad, alpha, step, delta, samplepixels;
        byte[] p;
        int pix, lim;

        if (lengthcount < minpicturebytes)
            {samplefac = 1;}
        alphadec = 30 + ((samplefac - 1) / 3);
        p = thepicture;
        pix = 0;
        lim = lengthcount;
        samplepixels = lengthcount / (3 * samplefac);
        delta = samplepixels / ncycles;
        alpha = initalpha;
        radius = initradius;

        rad = radius >> radiusbiasshift;
        if (rad <= 1)
            {rad = 0;}
        for (i = 0; i < rad; i++)
            {radpower[i] = alpha * (((rad * rad - i * i) * radbias) / (rad * rad));}

        /*// fprintf(stderr,"beginning 1D learning: initial radius=%d\n", rad);*/

        if (lengthcount < minpicturebytes)
            {step = 3;}
        else if ((lengthcount % prime1) != 0)
            {step = 3 * prime1;}
        else {
            if ((lengthcount % prime2) != 0)
                {step = 3 * prime2;}
            else {
                if ((lengthcount % prime3) != 0)
                    {step = 3 * prime3;}
                else
                    {step = 3 * prime4;}
            }
        }

        i = 0;
        while (i < samplepixels) {
            b = (p[pix + 0] & 0xff) << netbiasshift;
            g = (p[pix + 1] & 0xff) << netbiasshift;
            r = (p[pix + 2] & 0xff) << netbiasshift;
            j = contest(b, g, r);

            altersingle(alpha, j, b, g, r);
            if (rad != 0)
                {alterneigh(rad, j, b, g, r);} /* alter neighbours */

            pix += step;
            if (pix >= lim)
                {pix -= lengthcount;}

            i++;
            if (delta == 0)
                {delta = 1;}
            if (i % delta == 0) {
                alpha -= alpha / alphadec;
                radius -= radius / radiusdec;
                rad = radius >> radiusbiasshift;
                if (rad <= 1)
                    {rad = 0;}
                for (j = 0; j < rad; j++)
                    {radpower[j] = alpha * (((rad * rad - j * j) * radbias) / (rad * rad));}
            }
        }
        /*// fprintf(stderr,"finished 1D learning: final alpha=%f*/
        /*// !\n",((float)alpha)/initalpha);*/com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.learn()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.learn()",this,throwable);throw throwable;}
    }

    /*
     * Search for BGR values 0..255 (after net is unbiased) and return colour
     * index
     * ----------------------------------------------------------------------------
     */
    public int map(int b, int g, int r) {

        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.encoder.NeuQuant.map(int,int,int)",this,b,g,r);try{int i, j, dist, a, bestd;
        int[] p;
        int best;

        bestd = 1000; /* biggest possible dist is 256*3 */
        best = -1;
        i = netindex[g]; /* index on g */
        j = i - 1; /* start at netindex[g] and work outwards */

        while ((i < netsize) || (j >= 0)) {
            if (i < netsize) {
                p = network[i];
                dist = p[1] - g; /* inx key */
                if (dist >= bestd)
                    {i = netsize;} /* stop iter */
                else {
                    i++;
                    if (dist < 0)
                        {dist = -dist;}
                    a = p[0] - b;
                    if (a < 0)
                        {a = -a;}
                    dist += a;
                    if (dist < bestd) {
                        a = p[2] - r;
                        if (a < 0)
                            {a = -a;}
                        dist += a;
                        if (dist < bestd) {
                            bestd = dist;
                            best = p[3];
                        }
                    }
                }
            }
            if (j >= 0) {
                p = network[j];
                dist = g - p[1]; /* inx key - reverse dif */
                if (dist >= bestd)
                    {j = -1;} /* stop iter */
                else {
                    j--;
                    if (dist < 0)
                        {dist = -dist;}
                    a = p[0] - b;
                    if (a < 0)
                        {a = -a;}
                    dist += a;
                    if (dist < bestd) {
                        a = p[2] - r;
                        if (a < 0)
                            {a = -a;}
                        dist += a;
                        if (dist < bestd) {
                            bestd = dist;
                            best = p[3];
                        }
                    }
                }
            }
        }
        {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.encoder.NeuQuant.map(int,int,int)",this);return (best);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.encoder.NeuQuant.map(int,int,int)",this,throwable);throw throwable;}
    }

    public byte[] process() {
        com.mijack.Xlog.logMethodEnter("[byte com.orpheusdroid.screenrecorder.encoder.NeuQuant.process()",this);try{learn();
        unbiasnet();
        inxbuild();
        {com.mijack.Xlog.logMethodExit("[byte com.orpheusdroid.screenrecorder.encoder.NeuQuant.process()",this);return colorMap();}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("[byte com.orpheusdroid.screenrecorder.encoder.NeuQuant.process()",this,throwable);throw throwable;}
    }

    /*
     * Unbias network to give byte values 0..255 and record position i to prepare
     * for sort
     * -----------------------------------------------------------------------------------
     */
    public void unbiasnet() {

        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.unbiasnet()",this);try{int i;

        for (i = 0; i < netsize; i++) {
            network[i][0] >>= netbiasshift;
            network[i][1] >>= netbiasshift;
            network[i][2] >>= netbiasshift;
            network[i][3] = i; /* record colour no */
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.unbiasnet()",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.unbiasnet()",this,throwable);throw throwable;}
    }

    /*
     * Move adjacent neurons by precomputed alpha*(1-((i-j)^2/[r]^2)) in
     * radpower[|i-j|]
     * ---------------------------------------------------------------------------------
     */
    protected void alterneigh(int rad, int i, int b, int g, int r) {

        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.alterneigh(int,int,int,int,int)",this,rad,i,b,g,r);try{int j, k, lo, hi, a, m;
        int[] p;

        lo = i - rad;
        if (lo < -1)
            {lo = -1;}
        hi = i + rad;
        if (hi > netsize)
            {hi = netsize;}

        j = i + 1;
        k = i - 1;
        m = 1;
        while ((j < hi) || (k > lo)) {
            a = radpower[m++];
            if (j < hi) {
                p = network[j++];
                try {
                    p[0] -= (a * (p[0] - b)) / alpharadbias;
                    p[1] -= (a * (p[1] - g)) / alpharadbias;
                    p[2] -= (a * (p[2] - r)) / alpharadbias;
                } catch (Exception e) {
                } /*// prevents 1.3 miscompilation*/
            }
            if (k > lo) {
                p = network[k--];
                try {
                    p[0] -= (a * (p[0] - b)) / alpharadbias;
                    p[1] -= (a * (p[1] - g)) / alpharadbias;
                    p[2] -= (a * (p[2] - r)) / alpharadbias;
                } catch (Exception e) {
                }
            }
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.alterneigh(int,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.alterneigh(int,int,int,int,int)",this,throwable);throw throwable;}
    }

    /*
     * Move neuron i towards biased (b,g,r) by factor alpha
     * ----------------------------------------------------
     */
    protected void altersingle(int alpha, int i, int b, int g, int r) {

	    com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.altersingle(int,int,int,int,int)",this,alpha,i,b,g,r);try{/* alter hit neuron */
        int[] n = network[i];
        n[0] -= (alpha * (n[0] - b)) / initalpha;
        n[1] -= (alpha * (n[1] - g)) / initalpha;
        n[2] -= (alpha * (n[2] - r)) / initalpha;com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.altersingle(int,int,int,int,int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.NeuQuant.altersingle(int,int,int,int,int)",this,throwable);throw throwable;}
    }

    /*
     * Search for biased BGR values ----------------------------
     */
    protected int contest(int b, int g, int r) {

	    com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.encoder.NeuQuant.contest(int,int,int)",this,b,g,r);try{/* finds closest neuron (min dist) and updates freq */
	    /* finds best neuron (min dist-bias) and returns position */
	    /* for frequently chosen neurons, freq[i] is high and bias[i] is negative */
	    /* bias[i] = gamma*((1/netsize)-freq[i]) */

        int i, dist, a, biasdist, betafreq;
        int bestpos, bestbiaspos, bestd, bestbiasd;
        int[] n;

        bestd = ~(1 << 31);
        bestbiasd = bestd;
        bestpos = -1;
        bestbiaspos = bestpos;

        for (i = 0; i < netsize; i++) {
            n = network[i];
            dist = n[0] - b;
            if (dist < 0)
                {dist = -dist;}
            a = n[1] - g;
            if (a < 0)
                {a = -a;}
            dist += a;
            a = n[2] - r;
            if (a < 0)
                {a = -a;}
            dist += a;
            if (dist < bestd) {
                bestd = dist;
                bestpos = i;
            }
            biasdist = dist - ((bias[i]) >> (intbiasshift - netbiasshift));
            if (biasdist < bestbiasd) {
                bestbiasd = biasdist;
                bestbiaspos = i;
            }
            betafreq = (freq[i] >> betashift);
            freq[i] -= betafreq;
            bias[i] += (betafreq << gammashift);
        }
        freq[bestpos] += beta;
        bias[bestpos] -= betagamma;
        {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.encoder.NeuQuant.contest(int,int,int)",this);return (bestbiaspos);}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.encoder.NeuQuant.contest(int,int,int)",this,throwable);throw throwable;}
    }
}

/*//	 ==============================================================================*/
/*//	 Adapted from Jef Poskanzer's Java port by way of J. M. G. Elliott.*/
/*//	 K Weiner 12/00*/

class LZWEncoder {

    static final int BITS = 12;
    static final int HSIZE = 5003; /*// 80% occupancy*/
    private static final int EOF = -1;
    int n_bits; /*// number of bits/code*/
    int maxbits = BITS; /*// user settable max # bits/code*/
    int maxcode; /*// maximum code, given n_bits*/

    /*// GIFCOMPR.C - GIF Image compression routines*/
    /*//*/
    /*// Lempel-Ziv compression based on 'compress'. GIF modifications by*/
    /*// David Rowley (mgardi@watdcsu.waterloo.edu)*/

    /*// General DEFINEs*/
    int maxmaxcode = 1 << BITS; /*// should NEVER generate this code*/
    int[] htab = new int[HSIZE];

    /*// GIF Image compression - modified 'compress'*/
    /*//*/
    /*// Based on: compress.c - File compression ala IEEE Computer, June 1984.*/
    /*//*/
    /*// By Authors: Spencer W. Thomas (decvax!harpo!utah-cs!utah-gr!thomas)*/
    /*// Jim McKie (decvax!mcvax!jim)*/
    /*// Steve Davies (decvax!vax135!petsd!peora!srd)*/
    /*// Ken Turkowski (decvax!decwrl!turtlevax!ken)*/
    /*// James A. Woods (decvax!ihnp4!ames!jaw)*/
    /*// Joe Orost (decvax!vax135!petsd!joe)*/
    int[] codetab = new int[HSIZE];
    int hsize = HSIZE; /*// for dynamic table sizing*/
    int free_ent = 0; /*// first unused entry*/
    /*// block compression parameters -- after all codes are used up,*/
    /*// and compression rate changes, start over.*/
    boolean clear_flg = false;
    int g_init_bits;
    int ClearCode;
    int EOFCode;
    int cur_accum = 0;
    int cur_bits = 0;

    /*// Algorithm: use open addressing double hashing (no chaining) on the*/
    /*// prefix code / next character combination. We do a variant of Knuth's*/
    /*// algorithm D (vol. 3, sec. 6.4) along with G. Knott's relatively-prime*/
    /*// secondary probe. Here, the modular division first probe is gives way*/
    /*// to a faster exclusive-or manipulation. Also do block compression with*/
    /*// an adaptive reset, whereby the code table is cleared when the compression*/
    /*// ratio decreases, but after the table fills. The variable-length output*/
    /*// codes are re-sized at this point, and a special CLEAR code is generated*/
    /*// for the decompressor. Late addition: construct the table according to*/
    /*// file size for noticeable speed improvement on small files. Please direct*/
    /*// questions about this implementation to ames!jaw.*/
    int masks[] = {0x0000, 0x0001, 0x0003, 0x0007, 0x000F, 0x001F, 0x003F, 0x007F, 0x00FF, 0x01FF,
            0x03FF, 0x07FF, 0x0FFF, 0x1FFF, 0x3FFF, 0x7FFF, 0xFFFF};
    /*// Number of characters so far in this 'packet'*/
    int a_count;
    /*// Define the storage for the packet accumulator*/
    byte[] accum = new byte[256];

    /*// output*/
    /*//*/
    /*// Output the given code.*/
    /*// Inputs:*/
    /*// code: A n_bits-bit integer. If == -1, then EOF. This assumes*/
    /*// that n_bits =< wordsize - 1.*/
    /*// Outputs:*/
    /*// Outputs code to the file.*/
    /*// Assumptions:*/
    /*// Chars are 8 bits long.*/
    /*// Algorithm:*/
    /*// Maintain a BITS character long buffer (so that 8 codes will*/
    /*// fit in it exactly). Use the VAX insv instruction to insert each*/
    /*// code in turn. When the buffer fills up empty it and start over.*/
    private int imgW, imgH;
    private byte[] pixAry;
    private int initCodeSize;
    private int remaining;
    private int curPixel;

    /*// ----------------------------------------------------------------------------*/
    LZWEncoder(int width, int height, byte[] pixels, int color_depth) {
        imgW = width;
        imgH = height;
        pixAry = pixels;
        initCodeSize = Math.max(2, color_depth);
    }

    /*// Add a character to the end of the current packet, and if it is 254*/
    /*// characters, flush the packet to disk.*/
    void char_out(byte c, OutputStream outs) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.char_out(byte,java.io.OutputStream)",this,c,outs);try{accum[a_count++] = c;
        if (a_count >= 254)
            {flush_char(outs);}com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.char_out(byte,java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.char_out(byte,java.io.OutputStream)",this,throwable);throw throwable;}
    }

    /*// Clear out the hash table*/

    /*// table clear for block compress*/
    void cl_block(OutputStream outs) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.cl_block(java.io.OutputStream)",this,outs);try{cl_hash(hsize);
        free_ent = ClearCode + 2;
        clear_flg = true;

        output(ClearCode, outs);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.cl_block(java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.cl_block(java.io.OutputStream)",this,throwable);throw throwable;}
    }

    /*// reset code table*/
    void cl_hash(int hsize) {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.cl_hash(int)",this,hsize);try{for (int i = 0; i < hsize; ++i)
            {htab[i] = -1;}com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.cl_hash(int)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.cl_hash(int)",this,throwable);throw throwable;}
    }

    void compress(int init_bits, OutputStream outs) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.compress(int,java.io.OutputStream)",this,init_bits,outs);try{int fcode;
        int i /* = 0 */;
        int c;
        int ent;
        int disp;
        int hsize_reg;
        int hshift;

        /*// Set up the globals: g_init_bits - initial number of bits*/
        g_init_bits = init_bits;

        /*// Set up the necessary values*/
        clear_flg = false;
        n_bits = g_init_bits;
        maxcode = MAXCODE(n_bits);

        ClearCode = 1 << (init_bits - 1);
        EOFCode = ClearCode + 1;
        free_ent = ClearCode + 2;

        a_count = 0; /*// clear packet*/

        ent = nextPixel();

        hshift = 0;
        for (fcode = hsize; fcode < 65536; fcode *= 2)
            {++hshift;}
        hshift = 8 - hshift; /*// set hash code range bound*/

        hsize_reg = hsize;
        cl_hash(hsize_reg); /*// clear hash table*/

        output(ClearCode, outs);

        outer_loop: while ((c = nextPixel()) != EOF) {
            fcode = (c << maxbits) + ent;
            i = (c << hshift) ^ ent; /*// xor hashing*/

            if (htab[i] == fcode) {
                ent = codetab[i];
                continue;
            } else if (htab[i] >= 0) /*// non-empty slot*/
            {
                disp = hsize_reg - i; /*// secondary hash (after G. Knott)*/
                if (i == 0)
                    {disp = 1;}
                do {
                    if ((i -= disp) < 0)
                        {i += hsize_reg;}

                    if (htab[i] == fcode) {
                        ent = codetab[i];
                        continue outer_loop;
                    }
                } while (htab[i] >= 0);
            }
            output(ent, outs);
            ent = c;
            if (free_ent < maxmaxcode) {
                codetab[i] = free_ent++; /*// code -> hashtable*/
                htab[i] = fcode;
            } else
                {cl_block(outs);}
        }
        /*// Put out the final code.*/
        output(ent, outs);
        output(EOFCode, outs);com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.compress(int,java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.compress(int,java.io.OutputStream)",this,throwable);throw throwable;}
    }

    /*// ----------------------------------------------------------------------------*/
    void encode(OutputStream os) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.encode(java.io.OutputStream)",this,os);try{os.write(initCodeSize); /*// write "initial code size" byte*/

        remaining = imgW * imgH; /*// reset navigation variables*/
        curPixel = 0;

        compress(initCodeSize + 1, os); /*// compress and write the pixel data*/

        os.write(0); /*// write block terminator*/com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.encode(java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.encode(java.io.OutputStream)",this,throwable);throw throwable;}
    }

    /*// Flush the packet to disk, and reset the accumulator*/
    void flush_char(OutputStream outs) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.flush_char(java.io.OutputStream)",this,outs);try{if (a_count > 0) {
            outs.write(a_count);
            outs.write(accum, 0, a_count);
            a_count = 0;
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.flush_char(java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.flush_char(java.io.OutputStream)",this,throwable);throw throwable;}
    }

    final int MAXCODE(int n_bits) {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.encoder.LZWEncoder.MAXCODE(int)",this,n_bits);try{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.encoder.LZWEncoder.MAXCODE(int)",this);return (1 << n_bits) - 1;}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.encoder.LZWEncoder.MAXCODE(int)",this,throwable);throw throwable;}
    }

    /*// ----------------------------------------------------------------------------*/
    /*// Return the next pixel from the image*/
    /*// ----------------------------------------------------------------------------*/
    private int nextPixel() {
        com.mijack.Xlog.logMethodEnter("int com.orpheusdroid.screenrecorder.encoder.LZWEncoder.nextPixel()",this);try{if (remaining == 0)
            {{com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.encoder.LZWEncoder.nextPixel()",this);return EOF;}}

        --remaining;

        byte pix = pixAry[curPixel++];

        {com.mijack.Xlog.logMethodExit("int com.orpheusdroid.screenrecorder.encoder.LZWEncoder.nextPixel()",this);return pix & 0xff;}}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("int com.orpheusdroid.screenrecorder.encoder.LZWEncoder.nextPixel()",this,throwable);throw throwable;}
    }

    void output(int code, OutputStream outs) throws IOException {
        com.mijack.Xlog.logMethodEnter("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.output(int,java.io.OutputStream)",this,code,outs);try{cur_accum &= masks[cur_bits];

        if (cur_bits > 0)
            {cur_accum |= (code << cur_bits);}
        else
            {cur_accum = code;}

        cur_bits += n_bits;

        while (cur_bits >= 8) {
            char_out((byte) (cur_accum & 0xff), outs);
            cur_accum >>= 8;
            cur_bits -= 8;
        }

        /*// If the next entry is going to be too big for the code size,*/
        /*// then increase it, if possible.*/
        if (free_ent > maxcode || clear_flg) {
            if (clear_flg) {
                maxcode = MAXCODE(n_bits = g_init_bits);
                clear_flg = false;
            } else {
                ++n_bits;
                if (n_bits == maxbits)
                    {maxcode = maxmaxcode;}
                else
                    {maxcode = MAXCODE(n_bits);}
            }
        }

        if (code == EOFCode) {
            /*// At EOF, write the rest of the buffer.*/
            while (cur_bits > 0) {
                char_out((byte) (cur_accum & 0xff), outs);
                cur_accum >>= 8;
                cur_bits -= 8;
            }

            flush_char(outs);
        }com.mijack.Xlog.logMethodExit("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.output(int,java.io.OutputStream)",this);}catch(Throwable throwable){com.mijack.Xlog.logMethodExitWithThrowable("void com.orpheusdroid.screenrecorder.encoder.LZWEncoder.output(int,java.io.OutputStream)",this,throwable);throw throwable;}
    }
}
