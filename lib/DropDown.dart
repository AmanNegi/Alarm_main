import 'package:flutter/material.dart';

import 'package:dynamic_theme/dynamic_theme.dart';

class DropDown extends StatefulWidget {
  DropDown({Key key}) : super(key: key);

  _DropDownState createState() => _DropDownState();
}

class _DropDownState extends State<DropDown> {
  List<String> themes = ['Dark', 'Light'];
  String _selectedItem ;

  @override
  Widget build(BuildContext context) {
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
          } else {
            DynamicTheme.of(context).setBrightness(Brightness.light);
          }
        },
        hint: Text("Choose"),
        value: _selectedItem,
      ),
    );
  }
}
