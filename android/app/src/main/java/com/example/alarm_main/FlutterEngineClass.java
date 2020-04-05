package com.example.alarm_main;

import android.content.Context;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.view.FlutterMain;

class FlutterEngineClass {
    static void createFlutterEngine(Context context) {
        FlutterMain.startInitialization(context);
        FlutterMain.ensureInitializationComplete(context, null);

        FlutterEngine engine = new FlutterEngine(context);
        engine.getNavigationChannel().setInitialRoute("/startMathsCorner");
        engine.getDartExecutor()
                .executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault());
        FlutterEngineCache.getInstance()
                .put("mathsEngine", engine);
    }

    static void destroyEngine() {
        FlutterEngineCache cache = FlutterEngineCache.getInstance();
        FlutterEngine enginePrivate = cache.get("mathsEngine");
        if (enginePrivate != null)
            enginePrivate.destroy();
    }

    static FlutterEngine getFlutterEngine() {
        FlutterEngineCache cache = FlutterEngineCache.getInstance();
        return cache.get("mathsEngine");
    }
}
