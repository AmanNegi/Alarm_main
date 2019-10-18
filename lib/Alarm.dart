import "package:flutter/foundation.dart";

class Alarm {
  List<bool> isSelected = [false, false, false, false, false, false, false];
  List<int> listInt = [0,0,0,0,0,0,0];
  int id;
  int hour;
  int minute;
  String message = "Random predefined Text";
  String timeString;

// **Named Constructor

  Alarm.withId({
    this.id,
    @required this.hour,
    @required this.minute,
    @required this.timeString,
    this.isSelected,
    this.message,
  });

  Alarm.withInt({
    this.id,
    @required this.hour,
    @required this.minute,
    @required this.timeString,
    @required this.listInt,
    this.message,
  });
// default Constructor
  Alarm({
    @required this.hour,
    @required this.minute,
    this.isSelected,
    @required this.timeString,
    this.message,
  });

  Alarm.fromMapObject(Map<String, dynamic> map) {
    this.id = map['id'];
    this.hour = map['hour'];
    this.minute = map['minute'];
    this.message = map['message'];
    this.timeString = map['timeString'];
    this.listInt = map['listInt'];
  }

  Map<String, dynamic> toMap() {
    var map = Map<String, dynamic>();
    if (id != null) {
      map['id'] = id;
    }
    map['hour'] = hour;
    map['minute'] = minute;
    map['message'] = message;
    map['timeString'] = timeString;
    map['listInt'] = listInt;
    return map;
  }
}
