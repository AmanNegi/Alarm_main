import 'package:flutter/material.dart';

import 'simpleListView.dart';
import 'package:scoped_model/scoped_model.dart';
import 'package:alarm_main/scoped-model/Alarms.dart';
import 'package:alarm_main/TileItem.dart';

class ShadesView extends StatelessWidget {
  Widget build(BuildContext context) {
    return ScopedModelDescendant<AlarmModel>(
      builder: (BuildContext context, Widget widget, AlarmModel model) {
        return ListView.builder(
          itemCount: model.alarms.length,
          itemBuilder: (BuildContext context, int index) {
            return Padding(
              padding: EdgeInsets.all(10.0),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(15.0),
                child: Container(
                  color: Theme.of(context).cardColor,
                  height: 100.0,
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
                                  alarm: model.alarms[index],
                                  index: index,
                                );
                              },
                              fullscreenDialog: true),
                        );
                      },
                      child: SimpleListViewClass(
                        alarm: model.alarms[index],
                        index: index,
                      ),
                    ),
                  ),
                ),
              ),
            );
          },
        );
      },
    );
  }
}
