import 'dart:io';
import 'dart:async';

import 'package:sqflite/sqflite.dart';
import 'package:sqflite/sqlite_api.dart';
import 'Alarm.dart';
import 'package:path_provider/path_provider.dart';

class DbHelper {
  static Database database;

  static String tableName = 'table_name';
  static String colId = "id";
  static String colHour = "hour";
  static String colMinute = "minute";
  static String colMessage = "message";
  static String colCustomPath = "customPath";
  static String colPath = "path";
  static String colDefaultMethod ="defaultMethod";
  static String colTimeString = "timeString";
  static String colRepeating = "repeating";

  Future<Database> get getDatabase async {
    if (database == null) {
      initializeDb();
    }
    return database;
  }

  Future<void> initializeDb() async {
    Directory directory = await getApplicationDocumentsDirectory();
    String path = directory.path + '/alarm.db';
    database = await openDatabase(path, version: 1, onCreate: createDb);
  }

  static Future<void> createDb(Database db, int newVersion) async {
    print('creating db');
    await db.execute(
        'CREATE TABLE $tableName($colId INTEGER PRIMARY KEY AUTOINCREMENT, $colHour INTEGER, $colMinute INTEGER, $colMessage TEXT NOT NULL, $colTimeString TEXT NOT NULL, $colRepeating INTEGER, $colCustomPath INTEGER, $colPath TEXT, $colDefaultMethod INTEGER)');
  }

  // ! adding alarm here...
  Future<int> insertAlarm(Alarm alarm) async {
    // ** the return value is the Unique id here
    var result = await database.insert(
      tableName,
      alarm.toMap(),
      conflictAlgorithm: ConflictAlgorithm.replace,
    );
    return result;
  }

  // ! fetching alarm list here...
  Future<List<Map<String, dynamic>>> getAlarmMapList() async {
    var result = await database.query(tableName, orderBy: '$colId ASC');
    return result;
  }

  // ! deleting alarm here...
  Future<int> deleteAlarm(int id) async {
    return await database
        .delete(tableName, where: "$colId = ? ", whereArgs: [id]);
  }

  // ! updating alarm here...
  Future<int> updateAlarm(Alarm alarm) async {
    return await database.update(tableName, alarm.toMap(),
        where: '$colId = ?',
        whereArgs: [alarm.id],
        conflictAlgorithm: ConflictAlgorithm.replace);
  }

  List<Alarm> fromListOfMapToAlarmList(List<Map<String, dynamic>> list) {
    List<Alarm> alarmList = [];

    if (list != null) {
      int totalItems = list.length;

      for (int i = 0; i < totalItems; i++) {
        Alarm alarm = Alarm.fromMapObject(list[i]);
        alarmList.add(alarm);
      }
    }
    return alarmList;
  }

  Future<int> getNoOfItems() async {
    List<Map<String, dynamic>> a = await getAlarmMapList();
    print(a.length);
    return a.length;
  }
}
