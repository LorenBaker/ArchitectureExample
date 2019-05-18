package com.lbconsulting.architectureexample.ui.activities;

import android.app.Application;

import com.lbconsulting.architectureexample.BuildConfig;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

/**
 * This class initializes application wide resources
 */
public class NoteApp extends Application {

    public NoteApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            // initiate Timber
            Timber.plant(new Timber.DebugTree() {
                             @Override
                             protected String createStackElementTag(@NotNull StackTraceElement element) {
                                 return "Timber: " + super.createStackElementTag(element) + ":" + element.getLineNumber();
                             }
                         }
            );
        }

        Timber.i("onCreate() complete.");
    }
}