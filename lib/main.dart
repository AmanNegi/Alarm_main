import 'package:alarm_main/AlarmView.dart';
import 'package:alarm_main/Permit.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:scoped_model/scoped_model.dart';
import 'package:alarm_main/scoped-model/Alarms.dart';

import 'Permit.dart';
import 'MathsCorner.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  ThemeData themeData =
      ThemeData(brightness: Brightness.dark, fontFamily: "Raleway");

  Widget mainWidget = Scaffold(
    body: Container(
      color: Colors.grey[900],
    ),
  );

  @override
  void initState() {
    super.initState();
    sharedPrefsSetup();
  }

  @override
  Widget build(BuildContext context) {
    return ScopedModel<AlarmModel>(
      model: AlarmModel(),
      child: MaterialApp(
        debugShowCheckedModeBanner: false,
        theme: ThemeData(brightness: Brightness.dark, fontFamily: "Raleway"),
        routes: {
          "/startMathsCorner": (context) => MathsCorner(),
        },
        home: mainWidget,
      ),
    );
  }

  void sharedPrefsSetup() async {
    SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
    bool isFirstTime = !sharedPreferences.containsKey("isFirstTime");
    setState(() {
      mainWidget = isFirstTime ? Permit() : AlarmView();
    });
  }
}
