import 'package:flutter/material.dart';

class ShadesView extends StatelessWidget {
  Widget build(BuildContext context) {
    List<MaterialColor> color = Colors.primaries;
    return ListView.builder(
      itemCount: color.length,
      itemBuilder: (BuildContext context, int index) {
        Container(
          width: 500,
          height: 500,
          color: color[index],
        );
      },
    );
  }
}
