import "package:flutter/material.dart";
import 'package:alarm_main/widgets/BottomAppBarBuilder.dart';
import 'package:scoped_model/scoped_model.dart';
import 'scoped-model/Alarms.dart';

import 'Alarm.dart';
import 'widgets/ShadesView.dart';

class AlarmView extends StatefulWidget {
  _AlarmViewState createState() => _AlarmViewState();
}

class _AlarmViewState extends State<AlarmView>
    with SingleTickerProviderStateMixin {
  GlobalKey<ScaffoldState> scaffoldState;
  AnimationController controller;
  bool isPlayingAnimation = false;

  @override
  void initState() {
    super.initState();
    controller = new AnimationController(
        vsync: this, duration: Duration(milliseconds: 500));
  }

  @override
  Widget build(BuildContext context) {
    return ScopedModelDescendant<AlarmModel>(
      builder: (BuildContext context, Widget child, AlarmModel model) {
        model.refreshData();
        return Scaffold(
          key: scaffoldState,
          floatingActionButton: Builder(
            builder: (cntxt) {
              return FloatingActionButton(
                elevation: 40.0,
                child: AnimatedIcon(
                  icon: AnimatedIcons.add_event,
                  progress: controller,
                ),
                onPressed: () {
                  setState(() {
                    isPlayingAnimation = true;
                  });
                  _handleOnPressed();
                  openTimePicker(cntxt, model.addAlarm, model.preExists);
                },
              );
            },
          ),
          floatingActionButtonLocation:
              FloatingActionButtonLocation.centerDocked,
          bottomNavigationBar: BottomAppBarBuilder(
            color: Colors.white,
            doAnything: true,
          ),
          body: ShadesView(),
        );
      },
    );
  }

  void openTimePicker(
      BuildContext contextNew, Function addAlarm, Function preExists) {
    showTimePicker(context: contextNew, initialTime: TimeOfDay.now()).then(
      (value) {
        if (value != null) {
          int hour = value.hour;
          int minute = value.minute;

          print("Value recieved :- " + value.toString());

          Alarm alarm = new Alarm(
              customPath: 0,
              repeating: 0,
              hour: hour,
              minute: minute,
              defaultMethod: 1,
              message: "New Alarm",
              timeString: value.format(contextNew));

          preExists(alarm).then((booleanValue) {
            print("Does the alarm preExists? == " + booleanValue.toString());
            if (!booleanValue) {
              addAlarm(alarm);
            } else {
              Scaffold.of(contextNew).showSnackBar(
                SnackBar(
                  duration: Duration(milliseconds: 1500),
                  elevation: 50.0,
                  backgroundColor: Theme.of(contextNew).primaryColor,
                  action: SnackBarAction(
                    label: "Ok",
                    textColor: Colors.blue,
                    onPressed: () {},
                  ),
                  content: Text(
                    "Alarm pre-exists",
                    style: TextStyle(
                        color: Theme.of(contextNew).primaryColorLight),
                  ),
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
          isPlayingAnimation = false;
          //variable for animation
          _handleOnPressed();
        });
      },
    );
  }

  void _handleOnPressed() {
    setState(() {
      isPlayingAnimation ? controller.forward() : controller.reverse();
    });
  }
}
