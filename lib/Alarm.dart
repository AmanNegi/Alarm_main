import "package:flutter/foundation.dart";


class Alarm {
  int id;
  int hour;
  int minute;
  String message = "";
  String timeString;

// **Named Constructor
  Alarm.withId({
    this.id,
    @required this.hour,
    @required this.minute,
    @required this.timeString,
    this.message,
  });

// default Constructor
  Alarm({
    @required this.hour,
    @required this.minute,
    @required this.timeString,
    this.message,
  });

  Alarm.fromMapObject(Map<String, dynamic> map) {
    this.id = map['id'];
    this.hour = map['hour'];
    this.minute = map['minute'];
    this.message = map['message'];
    this.timeString = map['timeString'];
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

    return map;
  }
}
