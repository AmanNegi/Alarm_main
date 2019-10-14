import 'package:scoped_model/scoped_model.dart';
import 'package:flutter/services.dart';

import '../dbHelper.dart';
import '../Alarm.dart';

class AlarmModel extends Model {
  List<Alarm> _alarms = [];
  int _selectedProductIndex;
  DbHelper dbHelper = new DbHelper();

  void refreshData() async {
    dbHelper.initalizeDb().then((value) async {
      List<Map<String, dynamic>> map = await dbHelper.getAlarmMapList();
      List<Alarm> alarmList = dbHelper.fromListOfMapToAlarmList(map);
      _alarms = alarmList;
      notifyListeners();
    });
  }

  final MethodChannel platform = MethodChannel("aster.flutter.app/alarm");
  List<Alarm> get alarms {
    return List.from(_alarms);
  }

  int get selectedProductIndex {
    return _selectedProductIndex;
  }

  Alarm get selectedProduct {
    if (_selectedProductIndex == null) {
      return null;
    }
    return _alarms[_selectedProductIndex];
  }

  Future<void> addProduct(Alarm alarm) async {
    dbHelper.initalizeDb().then((value) async {
      int id = await dbHelper.insertAlarm(alarm);
      platform.invokeListMethod("randomMethod",
          {'uniqueId': id, 'hour': alarm.hour, 'minute': alarm.minute});
      Alarm alarmWithID = Alarm.withId(
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

  Future<int> updateProduct(Alarm alarm) async {
    _alarms[_selectedProductIndex] = alarm;
    //updating files in SQFLITE database
    int success = await dbHelper.updateAlarm(alarm);
    _selectedProductIndex = null;
    return success;
  }

  Future<void> deleteProduct(int id, int index) async {
    // ! get android deleting support
    print("id received in deleteProduct $id");
    //removing from sqlite
    dbHelper.initalizeDb().then((value) async {
      int success = await dbHelper.deleteAlarm(id);
      print("success $success delete method");
    });
    _alarms.removeAt(index);
    notifyListeners();
  }

  void selectProductIndex(int index) {
    _selectedProductIndex = index;
  }
}
