package com.willbiddy.counter;


import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.github.stkent.amplify.logging.AndroidLogger;
import com.github.stkent.amplify.tracking.Amplify;
import com.github.stkent.amplify.tracking.PromptViewEvent;
import com.github.stkent.amplify.tracking.rules.GooglePlayStoreRule;
import com.github.stkent.amplify.tracking.rules.MaximumCountRule;

import io.fabric.sdk.android.Fabric;

public class AmplifyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        Amplify.setLogger(new AndroidLogger());

        Amplify.initSharedInstance(this)
                .setFeedbackEmailAddress("willbiddy@gmail.com")
                .addEnvironmentBasedRule(new GooglePlayStoreRule())
                .setInstallTimeCooldownDays(2)
                .setLastUpdateTimeCooldownDays(4)
                .setLastCrashTimeCooldownDays(14)
                .addTotalEventCountRule(PromptViewEvent.PROMPT_SHOWN, new MaximumCountRule(1));
    }
}