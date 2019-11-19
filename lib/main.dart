import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:dynamic_theme/dynamic_theme.dart';
import 'package:scoped_model/scoped_model.dart';
import 'package:alarm_main/scoped-model/Alarms.dart';

import 'AlarmView.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  SystemUiOverlayStyle mySystemTheme = SystemUiOverlayStyle.dark.copyWith(
    systemNavigationBarColor: Colors.black,
  );

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    SystemChrome.setSystemUIOverlayStyle(mySystemTheme);
    return ScopedModel<AlarmModel>(
      model: AlarmModel(),
      child: DynamicTheme(
        defaultBrightness: Brightness.dark,
        data: (brightness) {
          return ThemeData(
            fontFamily: "Raleway",
            primarySwatch: Colors.indigo,
            snackBarTheme: SnackBarThemeData(),
            brightness: brightness,
          );
        },
        themedWidgetBuilder: (context, theme) {
          return MaterialApp(
            title: 'Flutter Demo',
            theme: theme,
            home: Container(
              child: Scaffold(
                body: AlarmView(),
              ),
            ),
          );
        },
      ),
    );
  }
}
