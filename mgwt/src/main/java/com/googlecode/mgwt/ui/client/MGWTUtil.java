package com.googlecode.mgwt.ui.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeEvent.ORIENTATION;
import com.googlecode.mgwt.dom.client.event.orientation.OrientationChangeHandler;
import com.googlecode.mgwt.ui.client.theme.base.UtilCss;

public class MGWTUtil {
	private static final FeatureDetection FEATURE_DETECTION = GWT.create(FeatureDetection.class);

	private final static EventBus manager = new SimpleEventBus();

	public static FeatureDetection getFeatureDetection() {
		return FEATURE_DETECTION;
	}

	static {
		setupOrientation();
	}

	private static Timer timer;

	private static boolean scrollingDisabled;

	public static void setScrollingDisabled(boolean disabled) {
		scrollingDisabled = disabled;
	}

	public static void fixIOSScrollIssueBlur() {
		if (!scrollingDisabled) {
			return;
		}
		if (timer != null) {
			timer.cancel();

		}

		timer = new Timer() {

			@Override
			public void run() {
				Window.scrollTo(0, 0);

			}

		};

		timer.schedule(100);
	}

	public static void fixIOSScrollIssueFocus() {
		if (!scrollingDisabled) {
			return;
		}
		if (timer != null) {
			timer.cancel();

		}
		timer = null;
	}

	public static ORIENTATION getOrientation() {
		int orientation = getOrientation0();

		ORIENTATION o;
		switch (orientation) {
		case 0:
		case 180:

			o = ORIENTATION.PORTRAIT;
			break;

		case 90:
		case -90:
			o = ORIENTATION.LANDSCAPE;

			break;

		default:
			//TODO is default a good idea?
			o = ORIENTATION.PORTRAIT;
			break;
		}

		return o;
	}

	private static native int getOrientation0()/*-{
		if (typeof ($wnd.orientation) == 'undefined') {
			return 0;
		}

		return $wnd.orientation;
	}-*/;

	private static void onorientationChange(int orientation) {
		UtilCss utilCss = MGWTStyle.getDefaultClientBundle().getUtilCss();
		ORIENTATION o;
		switch (orientation) {
		case 0:
		case 180:
			Document.get().getBody().addClassName(utilCss.portrait());
			Document.get().getBody().removeClassName(utilCss.landscape());
			o = ORIENTATION.PORTRAIT;
			break;

		case 90:
		case -90:
			o = ORIENTATION.LANDSCAPE;
			Document.get().getBody().addClassName(utilCss.landscape());
			Document.get().getBody().removeClassName(utilCss.portrait());

			break;

		default:
			//TODO is default a good idea?
			o = ORIENTATION.PORTRAIT;
			break;
		}

		manager.fireEvent(new OrientationChangeEvent(o));

	}

	private static native void setupOrientation()/*-{

		var func = $entry(function() {
			@com.googlecode.mgwt.ui.client.MGWTUtil::onorientationChange(I)($wnd.orientation);
		});
		$doc.body.onorientationchange = func;
		$doc.addEventListener("orientationChanged", func);
	}-*/;

	public static HandlerRegistration addOrientationChangeHandler(OrientationChangeHandler handler) {
		return manager.addHandler(OrientationChangeEvent.getType(), handler);
	}
}