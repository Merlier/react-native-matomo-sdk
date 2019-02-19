
package com.terveystalo.react_native.matomo_sdk;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

import org.matomo.sdk.Matomo;
import org.matomo.sdk.TrackerBuilder;
import org.matomo.sdk.Tracker;
import org.matomo.sdk.extra.TrackHelper;

import java.util.LinkedList;
import java.util.List;



public class RNMatomoSdkModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private Tracker tracker;

  public RNMatomoSdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNMatomoSdk";
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void initialize(String apiUrl, int siteId, Promise promise) {
    try {
      if (tracker == null) {
        tracker = TrackerBuilder
                .createDefault(apiUrl, siteId)
                .build(Matomo.getInstance(this.reactContext));

        // Disable default automatic user ID generation for consistency with iOS
        tracker.setUserId(null);
      }
      promise.resolve(null);
    } catch(Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void trackView(ReadableArray route, Promise promise) {
    try {
      List<String> routeList = new LinkedList<>();
      for (int i = 0 ; i < route.size() ; i++) {
        routeList.add(route.getString(i));
      }
      String path = String.join("/", routeList);
      TrackHelper.track().screen(path).title(path).with(tracker);
      promise.resolve(null);
    } catch(Exception e) {
      promise.reject(e);
    }
  }

  @ReactMethod
  @SuppressWarnings("unused")
  public void trackEvent(String category, String action, ReadableMap optionalParameters, Promise promise) {
    try {
      TrackHelper.EventBuilder event = TrackHelper.track().event(category, action);
      if (optionalParameters.hasKey("name")) {
        event.name(optionalParameters.getString("name"));
      }
      if (optionalParameters.hasKey("value")) {
        event.value((float) optionalParameters.getDouble("value"));
      }
      event.with(tracker);

      promise.resolve(null);
    } catch(Exception e) {
      promise.reject(e);
    }
  }
}