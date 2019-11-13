import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'package:dynamic_theme/dynamic_theme.dart';

class DropDown extends StatefulWidget {
  DropDown({Key key}) : super(key: key);

  _DropDownState createState() => _DropDownState();
}

class _DropDownState extends State<DropDown> {
  List<String> themes = ['Dark', 'Light'];
  String _selectedItem;

  SystemUiOverlayStyle mySystemTheme = SystemUiOverlayStyle.dark
      .copyWith(systemNavigationBarColor: Colors.black);

  @override
  Widget build(BuildContext context) {
    SystemChrome.setSystemUIOverlayStyle(mySystemTheme);

    return Container(
      child: DropdownButtonFormField<String>(
        items: themes.map((value) {
          return DropdownMenuItem<String>(
            value: value,
            child: Text(value),
          );
        }).toList(),
        onChanged: (value) {
          setState(() {
            _selectedItem = value;
          });
          if (_selectedItem == "Dark") {
            DynamicTheme.of(context).setBrightness(Brightness.dark);
            mySystemTheme = SystemUiOverlayStyle.dark
                .copyWith(systemNavigationBarColor: Colors.black);
          } else {
            setState(() {
              DynamicTheme.of(context).setBrightness(Brightness.light);
              mySystemTheme = SystemUiOverlayStyle.light
                  .copyWith(systemNavigationBarColor: Colors.white);
            });
          }
        },
        hint: Text("Choose"),
        value: _selectedItem,
      ),
    );
  }
}
