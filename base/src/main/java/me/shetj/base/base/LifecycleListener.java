package me.shetj.base.base;

public interface LifecycleListener {

    /**
     * Callback for when {@link android.support.v4.app.Fragment#onStart()}} or {@link
     * android.app.Activity#onStart()} is called.
     */
    void onStart();

    /**
     * Callback for when {@link android.support.v4.app.Fragment#onStop()}} or {@link
     * android.app.Activity#onStop()}} is called.
     */
    void onStop();
    /**
     * Callback for when {@link android.support.v4.app.Fragment#onResume()}} or {@link
     * android.app.Activity#onResume()} is called.
     */
    void onResume();

    /**
     * Callback for when {@link android.support.v4.app.Fragment#onPause()}} or {@link
     * android.app.Activity#onPause()}} is called.
     */
    void onPause();

    /**
     * Callback for when {@link android.support.v4.app.Fragment#onDestroy()}} or {@link
     * android.app.Activity#onDestroy()} is called.
     */
    void onDestroy();
}