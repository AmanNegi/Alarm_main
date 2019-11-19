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
  AnimationController controller;

  bool isPlaying = false;

  @override
  void initState() {
    super.initState();
    print("In initState() [AlarmView.dart]");
    controller = new AnimationController(
        vsync: this, duration: Duration(milliseconds: 500));
  }

  void openTimePicker(Function addAlarm, Function preExists) {
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
              customPath: 0,
              repeating: 0,
              hour: hour,
              minute: minute,
              message: "New Alarm",
              timeString: value.format(context));

          preExists(alarm).then((booleanValue) {
            print("Value in alarmView of preExists == " +
                booleanValue.toString());
            if (!booleanValue) {
              addAlarm(alarm);
            } else {
              Scaffold.of(context).showSnackBar(
                SnackBar(
                  duration: Duration(seconds: 3),
                  elevation: 50.0,
                  content: Text(
                    "The alarm pre-exists",
                    style:
                        TextStyle(color: Theme.of(context).primaryColorLight),
                  ),
                  backgroundColor: Theme.of(context).backgroundColor,
                  behavior: SnackBarBehavior.floating,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.all(Radius.circular(10.0)),
                  ),
                ),
              );
            }
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

  void _handleOnPressed() {
    setState(() {
      isPlaying ? controller.forward() : controller.reverse();
    });
  }

  @override
  Widget build(BuildContext context) {
    return ScopedModelDescendant<AlarmModel>(
      builder: (BuildContext context, Widget child, AlarmModel model) {
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
              openTimePicker(model.addAlarm, model.preExists);
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
