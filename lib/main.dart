import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:dynamic_theme/dynamic_theme.dart';
import 'package:scoped_model/scoped_model.dart';
import 'package:alarm_main/scoped-model/Alarms.dart';


import 'AlarmView.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ScopedModel<AlarmModel>(
      model: AlarmModel(),
      child: DynamicTheme(
        defaultBrightness: Brightness.dark,
        data: (brightness) => ThemeData(
          primarySwatch: Colors.indigo,
          brightness: brightness,
        ),
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
