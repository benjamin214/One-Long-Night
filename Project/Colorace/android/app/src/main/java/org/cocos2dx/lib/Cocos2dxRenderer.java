/****************************************************************************
Copyright (c) 2010-2011 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 ****************************************************************************/
package org.cocos2dx.lib;

import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.os.SystemClock;

public class Cocos2dxRenderer implements GLSurfaceView.Renderer {
	// ===========================================================
	// Constants
	// ===========================================================

	private final static long NANOSECONDSPERSECOND = 1000000000L;
	private final static long NANOSECONDSPERMICROSECOND = 1000000;
	private final static long FIXED_FPS = 16; //60 FPS

	private static long sAnimationInterval = (long) (1.0 / 60 * Cocos2dxRenderer.NANOSECONDSPERSECOND);

	// ===========================================================
	// Fields
	// ===========================================================

	private long mLastTickInNanoSeconds;
	private long mStartTime = 0;
	private long mEndTime = 0;
	private int mScreenWidth;
	private int mScreenHeight;

	// ===========================================================
	// Constructors
	// ===========================================================

	Cocos2dxRenderer() {
		mStartTime = SystemClock.elapsedRealtime();
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public static void setAnimationInterval(final double pAnimationInterval) {
		Cocos2dxRenderer.sAnimationInterval = (long) (pAnimationInterval * Cocos2dxRenderer.NANOSECONDSPERSECOND);
	}

	public void setScreenWidthAndHeight(final int pSurfaceWidth, final int pSurfaceHeight) {
		this.mScreenWidth = pSurfaceWidth;
		this.mScreenHeight = pSurfaceHeight;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onSurfaceCreated(final GL10 pGL10, final EGLConfig pEGLConfig) {
		Cocos2dxRenderer.nativeInit(this.mScreenWidth, this.mScreenHeight);

		this.mLastTickInNanoSeconds = System.nanoTime();
	}

	@Override
	public void onSurfaceChanged(final GL10 pGL10, final int pWidth, final int pHeight) {
		Log.d("RENDERER", "Surface changed: " + pWidth + " " + pHeight);
		Cocos2dxRenderer.nativeOnSurfaceChanged(pWidth, pHeight);
	}

	@Override
	public void onDrawFrame(final GL10 gl) {
		try {
			mEndTime = SystemClock.elapsedRealtime();
			long dt = mEndTime - mStartTime;
			if (dt < FIXED_FPS) {
				Thread.sleep(FIXED_FPS - dt);
			}
			mStartTime = SystemClock.elapsedRealtime();
			
			Cocos2dxRenderer.nativeRender();
		}
		catch (Exception ex) {
			//Empty here because we do not have to do anything on an exception
		}
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private static native void nativeTouchesBegin(final int pID, final float pX, final float pY);
	private static native void nativeTouchesEnd(final int pID, final float pX, final float pY);
	private static native void nativeTouchesMove(final int[] pIDs, final float[] pXs, final float[] pYs);
	private static native void nativeTouchesCancel(final int[] pIDs, final float[] pXs, final float[] pYs);
	private static native boolean nativeKeyDown(final int pKeyCode);
	private static native boolean nativeKeyUp(final int pKeyCode);
	private static native void nativeRender();
	private static native void nativeInit(final int pWidth, final int pHeight);
	private static native void nativeOnSurfaceChanged(final int width, final int height);
	private static native void nativeOnPause();
	private static native void nativeOnResume();

	public void handleActionDown(final int pID, final float pX, final float pY) {
		Cocos2dxRenderer.nativeTouchesBegin(pID, pX, pY);
	}

	public void handleActionUp(final int pID, final float pX, final float pY) {
		Cocos2dxRenderer.nativeTouchesEnd(pID, pX, pY);
	}

	public void handleActionCancel(final int[] pIDs, final float[] pXs, final float[] pYs) {
		Cocos2dxRenderer.nativeTouchesCancel(pIDs, pXs, pYs);
	}

	public void handleActionMove(final int[] pIDs, final float[] pXs, final float[] pYs) {
		Cocos2dxRenderer.nativeTouchesMove(pIDs, pXs, pYs);
	}

	public void handleKeyDown(final int pKeyCode) {
		Cocos2dxRenderer.nativeKeyDown(pKeyCode);
	}
	public void handleKeyUp(final int pKeyCode) {
		Cocos2dxRenderer.nativeKeyUp(pKeyCode);
	}

	public void handleOnPause() {
		Cocos2dxRenderer.nativeOnPause();
	}

	public void handleOnResume() {
		Cocos2dxRenderer.nativeOnResume();
	}

	private static native void nativeInsertText(final String pText);
	private static native void nativeDeleteBackward();
	private static native String nativeGetContentText();

	public void handleInsertText(final String pText) {
		Cocos2dxRenderer.nativeInsertText(pText);
	}

	public void handleDeleteBackward() {
		Cocos2dxRenderer.nativeDeleteBackward();
	}

	public String getContentText() {
		return Cocos2dxRenderer.nativeGetContentText();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
