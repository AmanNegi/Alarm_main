import 'package:flutter/material.dart';

import 'simpleListView.dart';

class ShadesView extends StatefulWidget {
  @override
  _ShadesViewState createState() => _ShadesViewState();
}

class _ShadesViewState extends State<ShadesView> {
  List<Color> colorList = [
    Colors.cyan[200],
    Colors.cyan[300],
    Colors.cyan[400],
    Colors.cyan[500],
    Colors.cyan[600],
    Colors.cyan[700],
    Colors.cyan[800],
    Colors.lightBlue[300],
    Colors.lightBlue[400],
    Colors.lightBlue[500],
    Colors.lightBlue[600],
    Colors.lightBlue[700],
    Colors.lightBlue[800],
  ];

  @override
  void initState() {
    super.initState();
  }

  Widget build(BuildContext context) {
    return ListView.builder(
        itemCount: colorList.length,
        itemBuilder: (BuildContext context, int index) {
          return Container(
            width: 500,
            height: 100,
            color: colorList[index],
            child: SimpleListViewClass(),
          );
        });
  }
}
