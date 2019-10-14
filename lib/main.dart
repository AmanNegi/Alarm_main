import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:dynamic_theme/dynamic_theme.dart';
import 'dbHelper.dart';

import 'AlarmView.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return DynamicTheme(
      defaultBrightness: Brightness.dark,
      data: (brightness) => ThemeData(
        primarySwatch: Colors.indigo,
        brightness: brightness,
      ),
      themedWidgetBuilder: (context, theme) {
        return MaterialApp(
          title: 'Flutter Demo',
          theme: theme,
          home: RandomApp(),
        );
      },
    );
  }
}

class RandomApp extends StatefulWidget {
  RandomApp({Key key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _RandomApp();
}

class _RandomApp extends State<RandomApp> {
  @override
  void initState() {
    DbHelper dbHelper = new DbHelper();
    dbHelper.initalizeDb();
    print('initiating db');
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Scaffold(
        body: AlarmView(),
      ),
    );
  }
}
