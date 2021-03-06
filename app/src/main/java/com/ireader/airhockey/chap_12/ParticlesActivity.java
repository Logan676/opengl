package com.ireader.airhockey.chap_12;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by guohongbin on 2018/12/18.
 */
public class ParticlesActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;
    private ParticlesRenderer particlesRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        ActivityManager activityManager =
                (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        /*
         * final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;
         */
        // Even though the latest emulator supports OpenGL ES 2.0,
        // it has a bug where it doesn't set the reqGlEsVersion so
        // the above check doesn't work. The below will detect if the
        // app is running on an emulator, and assume that it supports
        // OpenGL ES 2.0.
        final boolean supportsEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                        && (Build.FINGERPRINT.startsWith("generic")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || Build.MODEL.contains("google_sdk")
                        || Build.MODEL.contains("Emulator")
                        || Build.MODEL.contains("Android SDK built for x86")));

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            glSurfaceView.setEGLContextClientVersion(2);

            particlesRender = new ParticlesRenderer(this);
            // Assign our renderer.
            glSurfaceView.setRenderer(particlesRender);
            rendererSet = true;
        } else {
            /*
             * This is where you could create an OpenGL ES 1.x compatible
             * renderer if you wanted to support both ES 1 and ES 2. Since we're
             * not doing anything, the app will crash if the device doesn't
             * support OpenGL ES 2.0. If we publish on the market, we should
             * also add the following to AndroidManifest.xml:
             *
             * <uses-feature android:glEsVersion="0x00020000"
             * android:required="true" />
             *
             * This hides our app from those devices which don't support OpenGL
             * ES 2.0.
             */
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            float previousX;
            float previousY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            previousX = event.getX();
                            previousY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            final float deltaX = event.getX() - previousX;
                            final float deltaY = event.getY() - previousY;
                            previousX = event.getX();
                            previousY = event.getY();
                            glSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    particlesRender.handleTouchDrag(deltaX, deltaY);
                                }
                            });
                            break;
                    }
                    return true;
                }
                return false;
            }
        });

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

}