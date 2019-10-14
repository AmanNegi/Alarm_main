import "package:flutter/material.dart";
import 'package:flutter/services.dart';
import 'dbHelper.dart';

import 'listView.dart';
import 'Alarm.dart';
import 'DropDown.dart';

class AlarmView extends StatefulWidget {
  _AlarmViewState createState() => _AlarmViewState();
}

class _AlarmViewState extends State<AlarmView>
    with SingleTickerProviderStateMixin {
  MethodChannel platform = MethodChannel("aster.flutter.app/alarm");
  String dropDownVal;
  DbHelper dbHelper = new DbHelper();
  int _hour;
  int _minute;
  AnimationController controller;
  bool isPlaying = false;

  @override
  void initState() {
    print("In initState() [AlarmView.dart]");
    super.initState();
    controller = new AnimationController(
        vsync: this, duration: Duration(milliseconds: 500));
    dbHelper.initalizeDb().then((value) {});
  }

  void _setAlarm(int uniqueId) async {
    print("Current alarm list in setAlarm()+${dbHelper.getNoOfItems()}");
    print("in setAlarm() hour  " +
        _hour.toString() +
        "  minute  " +
        _minute.toString());
    try {
      platform.invokeListMethod("randomMethod",
          {'uniqueId': uniqueId, 'hour': _hour, 'minute': _minute});
      print("[main.dart] setting alarm");
    } on PlatformException catch (e) {
      print("Some error " + e.message);
    }
  }

  void openTimePicker() {
    showTimePicker(
      context: context,
      initialTime: TimeOfDay.now(),
    ).then(
      (value) {
        if (value != null) {
          int hour = value.hour;
          int minute = value.minute;
          print("Value recieved :_ " + value.toString());

          print(value.format(context));
          Alarm alarm = new Alarm(
              hour: hour,
              minute: minute,
              message: "randomMessage",
              timeString: value.format(context));

          getUniqueIdAndSaveToSQL(alarm).then((int id) {
            setState(() {
              _hour = hour;
              _minute = minute;
            });
            _setAlarm(id);
          });
        } else {
          print("no value selected by user");
        }
        setState(() {
          isPlaying = false;
          //variable for animation
          _handleOnPressed();
        });
      },
    );
  }

  Future<int> getUniqueIdAndSaveToSQL(Alarm alarm) async {
    int id = await dbHelper.insertAlarm(alarm);
    setState(() {});
    return id;
  }

  void _handleOnPressed() {
    setState(() {
      isPlaying ? controller.forward() : controller.reverse();
    });
  }

  @override
  Widget build(BuildContext context) {
    print("in build [AlarmView.dart]");
    return Scaffold(
      floatingActionButton: FloatingActionButton.extended(
        elevation: 4.0,
        icon: AnimatedIcon(
          icon: AnimatedIcons.add_event,
          progress: controller,
        ),
        label: const Text(
          'Add alarm',
          style: TextStyle(fontFamily: "Raleway-Bold"),
        ),
        onPressed: () {
          setState(() {
            isPlaying = true;
          });
          _handleOnPressed();
          openTimePicker();
        },
      ),
      floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
      bottomNavigationBar: BottomAppBar(
        child: new Row(
          mainAxisSize: MainAxisSize.max,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: <Widget>[
            IconButton(
              icon: Icon(Icons.menu),
              onPressed: () {
                showModalBottomSheet(
                    context: context,
                    builder: (BuildContext context) {
                      return Column(
                        children: <Widget>[
                          SwitchListTile(
                            value: true,
                            title: Text("Show network images"),
                            onChanged: (value) {},
                          ),
                          Divider(),
                          Row(
                            children: <Widget>[
                              Expanded(
                                child: Padding(
                                  padding: const EdgeInsets.all(16.0),
                                  child: Text("Select a theme"),
                                ),
                              ),
                              Expanded(
                                child: Padding(
                                  padding: const EdgeInsets.all(16.0),
                                  child: DropDown(),
                                ),
                              )
                            ],
                          )
                        ],
                      );
                    });
              },
            ),
          ],
        ),
      ),
      body: ListViewClass(),
    );
  }
}
