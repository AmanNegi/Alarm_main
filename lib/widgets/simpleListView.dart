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
            color: Colors.red[400],
            child: Align(
              alignment: Alignment.centerLeft,
              child: Icon(Icons.delete),
            ),
          ),
          onDismissed: ((direction) {
            if (direction == DismissDirection.startToEnd) {
              model.deleteProduct(alarmNew.id, index);
              Scaffold.of(context).showSnackBar(SnackBar(
                duration: Duration(seconds: 3),
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
          child: ListTile(
            dense: false,
            leading: Icon(Icons.alarm),
            contentPadding: EdgeInsets.all(20.0),
            title: Text(
              alarmNew.timeString,
              style: TextStyle(fontSize: 30.0),
            ),
          ),
        );
      },
    );
  }
}
