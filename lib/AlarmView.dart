import "package:flutter/material.dart";
import 'package:flutter/services.dart';

import 'package:scoped_model/scoped_model.dart';
import 'scoped-model/Alarms.dart';

import 'imagelistView.dart';
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
    print("In initState() [AlarmView.dart]");
    super.initState();
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
              message: "randomMessage",
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
            backgroundColor: Colors.lightGreenAccent,
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
          //   body: ListViewClass(),
          body: ShadesView(),
        );
      },
    );
  }
}
