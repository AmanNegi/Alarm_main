import "package:flutter/material.dart";

import 'package:flutter/services.dart';
import 'package:scoped_model/scoped_model.dart';
import 'scoped-model/Alarms.dart';

import 'Alarm.dart';
import 'DropDown.dart';
import 'widgets/ShadesView.dart';

class AlarmView extends StatefulWidget {
  _AlarmViewState createState() => _AlarmViewState();
}

class _AlarmViewState extends State<AlarmView>
    with SingleTickerProviderStateMixin {
  String dropDownVal;
  bool permit = false;
  AnimationController controller;
  final MethodChannel platform = MethodChannel("aster.flutter.app/alarm");

  bool isPlaying = false;

  @override
  void initState() {
    super.initState();
    print("In initState() [AlarmView.dart]");
    getPermissions();
    controller = new AnimationController(
        vsync: this, duration: Duration(milliseconds: 500));
  }

  void openTimePicker(Function addAlarm) {
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
              message: "New Alarm",
              timeString: value.format(context));

          addAlarm(alarm);
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

  void getPermissions() {
    print("inGetPermissions");
    platform.invokeMethod("getPermission").then((value) {
      if (value == true || value == "true") {
        setState(() {
          permit = true;
        });
      }
    });
  }

  void _handleOnPressed() {
    setState(() {
      isPlaying ? controller.forward() : controller.reverse();
    });
  }

  @override
  Widget build(BuildContext context) {
    return ScopedModelDescendant<AlarmModel>(
      builder: (BuildContext context, Widget child, AlarmModel model) {
        model.setPermit(permit);
        model.refreshData();
        return Scaffold(
          floatingActionButton: FloatingActionButton.extended(
            backgroundColor: Theme.of(context).accentColor,
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
              openTimePicker(model.addProduct);
            },
          ),
          floatingActionButtonLocation:
              FloatingActionButtonLocation.centerDocked,
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
          body: ShadesView(),
        );
      },
    );
  }
}
