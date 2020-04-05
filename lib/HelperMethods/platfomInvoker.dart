import 'package:flutter/services.dart';

class PlatformInvoker {
  static final closingWindowMathWindow =
      MethodChannel("aster.plugins.dev/mathChannel");

  static void invokeCloseWindowMath() {
    closingWindowMathWindow.invokeMethod("CloseAllWindows");
  }
}
