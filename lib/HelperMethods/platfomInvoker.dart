import 'package:flutter/services.dart';

class PlatformInvoker {
  static const platform = const MethodChannel("aster.plugins.dev/mathChannel");

  static void invokeTheMethod() {
    platform.invokeMethod("CloseAllWindows");
  }

}
