import 'package:flutter/material.dart';

import 'simpleListView.dart';
import 'package:scoped_model/scoped_model.dart';
import 'package:alarm_main/scoped-model/Alarms.dart';
import 'package:alarm_main/TileItem.dart';

class ShadesView extends StatefulWidget {
  @override
  _ShadesViewState createState() => _ShadesViewState();
}

class _ShadesViewState extends State<ShadesView> {
  List<Color> colorList = [
    Colors.cyan[200],
    Colors.cyan[300],
    Colors.cyan[400],
    Colors.cyan[500],
    Colors.cyan[600],
    Colors.cyan[700],
    Colors.cyan[800],
    Colors.lightBlue[300],
    Colors.lightBlue[400],
    Colors.lightBlue[500],
    Colors.lightBlue[600],
    Colors.lightBlue[700],
    Colors.lightBlue[800],
    Colors.blue[300],
    Colors.blue[400],
    Colors.blue[500],
    Colors.blue[600],
    Colors.blue[700],
    Colors.blue[800],
    Colors.cyan[200],
    Colors.cyan[300],
    Colors.cyan[400],
    Colors.cyan[500],
    Colors.cyan[600],
    Colors.cyan[700],
    Colors.cyan[800],
    Colors.lightBlue[300],
    Colors.lightBlue[400],
    Colors.lightBlue[500],
    Colors.lightBlue[600],
    Colors.lightBlue[700],
    Colors.lightBlue[800],
    Colors.blue[300],
    Colors.blue[400],
    Colors.blue[500],
    Colors.blue[600],
    Colors.blue[700],
    Colors.blue[800],
  ];

  @override
  void initState() {
    super.initState();
  }

  Widget build(BuildContext context) {
    return ScopedModelDescendant<AlarmModel>(
      builder: (BuildContext context, Widget widget, AlarmModel model) {
        return ListView.builder(
            itemCount: model.alarms.length,
            itemBuilder: (BuildContext context, int index) {
              return Container(
                color: colorList[index],
                child: Material(
                  color: Colors.transparent,
                  child: InkWell(
                    splashColor: Colors.black,
                    onTap: () {
                      Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (BuildContext context) {
                                return TileItem(
                                  color: colorList[index],
                                  alarm: model.alarms[index],
                                  index: index,
                                );
                              },
                              fullscreenDialog: true));
                    },
                    child: Container(
                      width: 500,
                      height: 100,
                      child: SimpleListViewClass(
                        alarm: model.alarms[index],
                        index: index,
                      ),
                    ),
                  ),
                ),
              );
            });
      },
    );
  }
}
