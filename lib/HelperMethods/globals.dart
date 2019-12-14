library alarm_main.globals;

import 'package:shared_preferences/shared_preferences.dart';

bool firstTime = true;

void saveToSharedPrefs(bool value) async {
  SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
  sharedPreferences.setBool("firstTime", value);
  firstTime = false;
}

void getValuesFromSharedPrefs() async {
  SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
  if (sharedPreferences.containsKey("firstTime")) {
    bool a = sharedPreferences.getBool("firstTime");
    firstTime = a;
  }else{
    firstTime = true;
  }
}
