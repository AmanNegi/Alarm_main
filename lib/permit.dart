import 'dart:async';

import 'package:flutter/material.dart';

import 'package:async/async.dart';
import 'package:flutter/services.dart';
import 'globals.dart' as globals;
import 'package:alarm_main/AlarmView.dart';

class Permit extends StatefulWidget {
  @override
  _PermitState createState() => _PermitState();
}

class _PermitState extends State<Permit> {
  @override
  void dispose() {
    _timerSubscription.cancel();
    super.dispose();
  }

  final MethodChannel platform = MethodChannel("aster.flutter.app/alarm/aster");
  String buttonText = "Proceed";
  String instructions = "";
  static const stream =
      const EventChannel('com.aster.eventPermitChannel/stream2');
  StreamSubscription _timerSubscription;

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

  void receiveFromStream(dynamic value) {
    print(value);
    Map<String, dynamic> a = value.cast<String, dynamic>();
    var _list = a.values.toList();
    var receivedStoragePermit = (_list[0]);
    var receivedNotificationPermit = (_list[1]);

    setState(() {
      if (receivedNotificationPermit == true && receivedStoragePermit == true) {
        setState(() {
          buttonText = "Proceed";
          instructions = "";
        });

        cancelStream();
        globals.firstTime = false;
        globals.saveToSharedPrefs();
        Navigator.pushReplacement(context,
            MaterialPageRoute(builder: (context) {
          return Container(
            child: AlarmView(),
          );
        }));
      } else if (receivedStoragePermit == true &&
          receivedNotificationPermit == false) {
        setState(() {
          buttonText = "Grant Permission";
          instructions = "Grant all permissions to continue";
        });
      } else if (receivedStoragePermit == false &&
          receivedNotificationPermit == true) {
        setState(() {
          buttonText = "Grant Permission";
          instructions = "Grant all permissions to continue";
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        alignment: Alignment.bottomCenter,
        decoration: BoxDecoration(
          gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: [
                Colors.black,
                Colors.black12,
                Colors.black12,
                Colors.purple,
              ]),
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
}
