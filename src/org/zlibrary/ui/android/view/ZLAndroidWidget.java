package org.zlibrary.ui.android.view;

import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.view.*;
import android.util.AttributeSet;

import org.zlibrary.core.view.ZLView;

public class ZLAndroidWidget extends View {
	private final ZLAndroidPaintContext myPaintContext = new ZLAndroidPaintContext();
	private ZLAndroidViewWidget myViewWidget;

	private int myWidth;
	private int myHeight;

	public static long Time;

	public ZLAndroidWidget(Context context, AttributeSet attrs, Map inflateParams, int defStyle) {
		super(context, attrs, inflateParams, defStyle);
	}

	public ZLAndroidWidget(Context context, AttributeSet attrs, Map inflateParams) {
		super(context, attrs, inflateParams);
	}

	public ZLAndroidPaintContext getPaintContext() {
		return myPaintContext;
	}

	void setViewWidget(ZLAndroidViewWidget viewWidget) {
		myViewWidget = viewWidget;
	}

	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		myWidth = w;
		myHeight = h;
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (myViewWidget == null) {
			return;
		}
		ZLView view = myViewWidget.getView();
		if (view == null) {
			return;
		}

		myPaintContext.beginPaint(canvas);
		//long start = System.currentTimeMillis();
		switch (myViewWidget.getRotation()) {
			case DEGREES0:
				myPaintContext.setSize(myWidth, myHeight);
				break;
			case DEGREES90:
				myPaintContext.setSize(myHeight, myWidth);
				canvas.rotate(270, myHeight / 2, myHeight / 2);
				break;
			case DEGREES180:
				myPaintContext.setSize(myWidth, myHeight);
				canvas.rotate(180, myWidth / 2, myHeight / 2);
				break;
			case DEGREES270:
				myPaintContext.setSize(myHeight, myWidth);
				canvas.rotate(90, myWidth / 2, myWidth / 2);
				break;
		}
		view.paint();
		//Time = System.currentTimeMillis() - start;
		//String sTime = "" + Time;
		//myPaintContext.drawString(240, 165, sTime.toCharArray(), 0, sTime.length());
		myPaintContext.endPaint();
	}

	public boolean onMotionEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		switch (myViewWidget.getRotation()) {
			case DEGREES0:
				break;
			case DEGREES90:
			{
				int swap = x;
				x = myHeight - y - 1;
				y = swap;
				break;
			}
			case DEGREES180:
			{
				x = myWidth - x - 1;
				y = myHeight - y - 1;
				break;
			}
			case DEGREES270:
			{
				int swap = myWidth - x - 1;
				x = y;
				y = swap;
				break;
			}
		}

		ZLView view = myViewWidget.getView();
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				view.onStylusRelease(x, y);
				break;
			case MotionEvent.ACTION_DOWN:
				view.onStylusPress(x, y);
				break;
			case MotionEvent.ACTION_MOVE:
				view.onStylusMovePressed(x, y);
				break;
		}

		return true;
	}
}
