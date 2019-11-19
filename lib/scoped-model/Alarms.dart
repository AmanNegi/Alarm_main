import 'package:scoped_model/scoped_model.dart';
import 'package:flutter/services.dart';

import '../dbHelper.dart';
import '../Alarm.dart';

class AlarmModel extends Model {
  List<Alarm> _alarms = [];
  final MethodChannel platform = MethodChannel("aster.flutter.app/alarm/aster");

  DbHelper dbHelper = new DbHelper();

  List<Alarm> get alarms {
    return List.from(_alarms);
  }

  Future<bool> preExists(Alarm alarm) async {
    int count = 0;
    await dbHelper.initalizeDb();
    List<Map<String, dynamic>> map = await dbHelper.getAlarmMapList();
    map.forEach((Map<String, dynamic> value) {
      int hour = value["hour"];
      int minute = value['minute'];
      if (hour == alarm.hour && minute == alarm.minute) {
        count++;
      }
    });

    if (count == 0) {
      return false;
    } else {
      return true;
    }
  }

  Future<void> refreshData() async {
    dbHelper.initalizeDb().then((value) async {
      List<Map<String, dynamic>> map = await dbHelper.getAlarmMapList();
      List<Alarm> alarmList = dbHelper.fromListOfMapToAlarmList(map);
      _alarms = alarmList;
      notifyListeners();
    });
  }

  Future<void> addAlarm(Alarm alarm) async {
    dbHelper.initalizeDb().then((value) async {
      int id = await dbHelper.insertAlarm(alarm);

      platform.invokeListMethod("setOneShot", {
        'uniqueId': id,
        'hour': alarm.hour,
        'minute': alarm.minute,
        "timeString": alarm.timeString,
        "message": alarm.message,
        "repeating": 0,
      });

      Alarm alarmWithID = Alarm.withId(
          repeating: alarm.repeating,
          id: id,
          hour: alarm.hour,
          minute: alarm.minute,
          message: alarm.message,
          timeString: alarm.timeString);
      _alarms.add(alarmWithID);
      notifyListeners();
    });

    //adding files to SQFLITE database
  }

  Future<int> updateProduct(Alarm alarm, int index) async {
    _alarms[index] = alarm;
    //updating files in SQFLITE database
    platform.invokeMethod("updateAlarm", {
      'uniqueId': alarm.id,
      'repeating': alarm.repeating,
      'hour': alarm.hour,
      'timeString': alarm.timeString,
      'message': alarm.message,
      'minute': alarm.minute,
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
      platform.invokeMethod("deleteAlarm", {'uniqueId': id});
    });
    _alarms.removeAt(index);
    notifyListeners();
  }
}
