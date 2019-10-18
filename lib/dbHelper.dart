import 'dart:io';
import 'dart:async';
import 'package:sqflite/sqflite.dart';
import 'package:sqflite/sqlite_api.dart';
import 'Alarm.dart';
import 'dart:convert';
import 'package:path_provider/path_provider.dart';

class DbHelper {
  static Database database;

  String tableName = 'table_name';

  String colId = "id";
  String colHour = "hour";
  String colMinute = "minute";
  String colMessage = "message";
  String colTimeString = "timeString";
  String colIntList = "listInt";

  Future<Database> get getDatabase async {
    //check if database is not null
    if (database == null) {
      await initalizeDb();
    }
    return database;
  }

  Future<void> initalizeDb() async {
    // * * some official work on here...
    Directory directory = await getApplicationDocumentsDirectory();
    String path = directory.path + 'alarm.db';

    // ** creating the table n columns for database
    database = await openDatabase(path, version: 1, onCreate: createDb);
  }

  void createDb(Database db, int newVersion) async {
    print('creating db');
    await db.execute(
        'CREATE TABLE $tableName($colId INTEGER PRIMARY KEY AUTOINCREMENT, $colHour INTEGER, $colMinute INTEGER, $colMessage TEXT NOT NULL, $colTimeString TEXT NOT NULL, $colIntList BLOB)');
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
    print("in update Alarm  ${alarm.listInt.toString()}");
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
