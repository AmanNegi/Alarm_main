import 'dart:async';

import 'package:alarm_main/DefaultList.dart';
import 'package:flutter/material.dart';

import 'package:flutter/services.dart';
import 'package:alarm_main/AlarmView.dart';
import 'package:shared_preferences/shared_preferences.dart';

class Permit extends StatefulWidget {
  @override
  _PermitState createState() => _PermitState();
}

class _PermitState extends State<Permit> {
  final MethodChannel platform = MethodChannel("aster.flutter.app/alarm/aster");
  static const stream =
      const EventChannel('com.aster.eventPermitChannel/stream2');

  String buttonText = "Proceed";
  String instructions = "";
  StreamSubscription _timerSubscription;

  var colorsList = [
    Colors.black,
    Colors.black12,
    Colors.black12,
    Colors.purple,
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        alignment: Alignment.bottomCenter,
        decoration: BoxDecoration(
          gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: colorsList),
        ),
        child: Center(
          child: Padding(
            padding: EdgeInsets.all(20.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Text(
                  "Kindly grant the following permissions to make your experience a better one.",
                  style: TextStyle(color: Colors.white, fontSize: 30.0),
                ),
                SizedBox(
                  height: 50.0,
                ),
                RaisedButton(
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(5.0)),
                  child: Text(buttonText),
                  color: Colors.purple[700],
                  onPressed: () {
                    //request all permit
                    platform.invokeMethod("requestAllPermit");
                    initiateStream();
                  },
                ),
                SizedBox(
                  height: 20.0,
                ),
                Text(instructions),
              ],
            ),
          ),
        ),
      ),
    );
  }

  void initiateStream() {
    if (_timerSubscription == null) {
      _timerSubscription =
          stream.receiveBroadcastStream().listen(receiveFromStream);
    }
  }

  void cancelStream() {
    if (_timerSubscription != null) {
      _timerSubscription.cancel();
    }
  }

  void receiveFromStream(dynamic value) async {
    print(value);
    Map<String, dynamic> a = value.cast<String, dynamic>();
    var _list = a.values.toList();
    var receivedStoragePermit = (_list[0]);
    var receivedNotificationPermit = (_list[1]);

    if (receivedNotificationPermit == true && receivedStoragePermit == true) {
      setState(() {
        buttonText = "Proceed";
        instructions = "";
      });
      cancelStream();

      var sharedPrefs = await SharedPreferences.getInstance();
      sharedPrefs.setBool("isFirstTime", false);

      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) {
            return DefaultList();
          },
        ),
      );
    } else if (receivedStoragePermit == false ||
        receivedNotificationPermit == false) {
      setState(() {
        buttonText = "Grant Permission";
        instructions = "Grant all permissions to continue";
      });
    }
  }
}
