import 'package:scoped_model/scoped_model.dart';
import 'package:flutter/services.dart';

import '../dbHelper.dart';
import '../Alarm.dart';

class AlarmModel extends Model {
  List<Alarm> _alarms = [];
  bool permit = false;
  final MethodChannel platform = MethodChannel("aster.flutter.app/alarm");

  DbHelper dbHelper = new DbHelper();

  List<Alarm> get alarms {
    return List.from(_alarms);
  }

  Future<void> refreshData() async {
    dbHelper.initalizeDb().then((value) async {
      List<Map<String, dynamic>> map = await dbHelper.getAlarmMapList();
      List<Alarm> alarmList = dbHelper.fromListOfMapToAlarmList(map);
      _alarms = alarmList;
      notifyListeners();
    });
  }

  void setPermit(bool value) {
    permit = value;
  }
  Future<void> addProduct(Alarm alarm) async {
    if (permit) {
      dbHelper.initalizeDb().then((value) async {
        int id = await dbHelper.insertAlarm(alarm);
        platform.invokeListMethod("addAlarm", {
          'uniqueId': id,
          'hour': alarm.hour,
          'minute': alarm.minute,
          "message": alarm.message
        });
        Alarm alarmWithID = Alarm.withId(
            id: id,
            hour: alarm.hour,
            minute: alarm.minute,
            message: alarm.message,
            timeString: alarm.timeString);
        _alarms.add(alarmWithID);
        notifyListeners();
      });
    } else {
      
    }

    //adding files to SQFLITE database
  }

  Future<int> updateProduct(
      Alarm alarm, int index, List<Map<String, bool>> week) async {
    _alarms[index] = alarm;
    //updating files in SQFLITE database
    platform.invokeListMethod("updateAlarm", {
      'uniqueId': alarm.id,
      'hour': alarm.hour,
      'message': alarm.message,
      'minute': alarm.minute,
      "listWeek": week
    });
    int success = await dbHelper.updateAlarm(alarm);
    print("success in update " + success.toString());
    notifyListeners();
    return success;
  }

  Future<void> deleteProduct(int id, int index) async {
    print("id received in deleteProduct $id");
    //removing from sqlite
    dbHelper.initalizeDb().then((value) async {
      int success = await dbHelper.deleteAlarm(id);
      print("success $success delete method");
      platform.invokeListMethod("deleteAlarm", {'uniqueId': id});
    });
    _alarms.removeAt(index);
    notifyListeners();
  }
}
