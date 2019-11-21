library alarm_main.globals;

import 'package:shared_preferences/shared_preferences.dart';

bool firstTime = true;

void saveToSharedPrefs() async {
  SharedPreferences sharedPreferences = await SharedPreferences.getInstance();
  sharedPreferences.setBool("firstTime", firstTime);
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
