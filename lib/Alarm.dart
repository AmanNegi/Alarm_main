import "package:flutter/foundation.dart";

class Alarm {
  int id;
  int customPath;
  int hour;
  String path;
  int repeating;
  int minute;
  String message = "Random predefined Text";
  String timeString;

// **Named Constructor

  Alarm.withId({
    this.id,
    @required this.hour,
    @required this.customPath,
    @required this.repeating,
    @required this.minute,
    @required this.timeString,
    this.path,
    this.message,
  });

// default Constructor
  Alarm({
    @required this.hour,
    @required this.repeating,
    @required this.customPath,
    @required this.minute,
    @required this.timeString,
    this.path,
    this.message,
  });

  Alarm.fromMapObject(Map<String, dynamic> map) {
    this.id = map['id'];
    this.hour = map['hour'];
    this.minute = map['minute'];
    this.message = map['message'];
    this.customPath = map['customMusic'];
    this.timeString = map['timeString'];
    this.path = map['musicPath'];
    this.repeating = map['repeating'];
  }

  Map<String, dynamic> toMap() {
    var map = Map<String, dynamic>();
    if (id != null) {
      map['id'] = id;
    }
    map['hour'] = hour;
    map['minute'] = minute;
    map['message'] = message;
    map['musicPath'] = path;
    map['customMusic'] = customPath;
    map['timeString'] = timeString;
    map['repeating'] = repeating;
    return map;
  }
}
