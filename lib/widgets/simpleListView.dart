import 'package:flutter/material.dart';
import 'package:alarm_main/Alarm.dart';

import 'package:scoped_model/scoped_model.dart';
import 'package:alarm_main/scoped-model/Alarms.dart';

class SimpleListViewClass extends StatefulWidget {
  final Alarm alarm;
  final int index;

  SimpleListViewClass({this.alarm, this.index});

  @override
  _SimpleListViewClassState createState() => _SimpleListViewClassState();
}

class _SimpleListViewClassState extends State<SimpleListViewClass>
    with SingleTickerProviderStateMixin {
  AnimationController animationController;
  bool isPlaying = false;

  @override
  void initState() {
    animationController = new AnimationController(
        vsync: this, duration: Duration(milliseconds: 500));
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    Alarm alarmNew = widget.alarm;
    int index = widget.index;
    return ScopedModelDescendant<AlarmModel>(
      builder: (BuildContext context, Widget widget, AlarmModel model) {
        return Dismissible(
          key: new ObjectKey(alarmNew.id),
          direction: DismissDirection.startToEnd,
          background: Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                colors: [Colors.red[700], Colors.black26],
              ),
            ),
            child: Align(
              alignment: Alignment.centerLeft,
              child: Icon(Icons.delete),
            ),
          ),
          onDismissed: ((direction) {
            if (direction == DismissDirection.startToEnd) {
              model.deleteProduct(alarmNew.id, index: index);
              Scaffold.of(context).showSnackBar(SnackBar(
                duration: Duration(seconds: 1),
                elevation: 50.0,
                backgroundColor: Theme.of(context).primaryColor,
                action: SnackBarAction(
                  label: "Ok",
                  textColor: Colors.blue,
                  onPressed: () {},
                ),
                content: Text(
                  "Alarm dismissed",
                  style: TextStyle(color: Theme.of(context).primaryColorLight),
                ),
                behavior: SnackBarBehavior.floating,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.all(Radius.circular(10.0)),
                ),
              ));
            }
          }),
          child: Center(
            child: Padding(
              padding: EdgeInsets.all(20.0),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: <Widget>[
                  Image.asset(
                    "assets/Images/mainAlarm.png",
                    height: 40,
                    width: 40,
                  ),
                  SizedBox(
                    width: 20,
                  ),
                  Text(
                    alarmNew.timeString,
                    style: TextStyle(
                        fontSize: 35.0,
                        fontWeight: FontWeight.w500,
                        color: Colors.white),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}
