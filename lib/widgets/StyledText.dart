import 'package:flutter/material.dart';

class StyledTextClass extends StatelessWidget {
  final String text;
  final double fontSize;
  StyledTextClass({this.text, this.fontSize});

  @override
  Widget build(BuildContext context) {
    return Text(
      text,
      style: TextStyle(
          fontFamily: "Raleway", fontSize: fontSize == null ? 20.0 : fontSize),
    );
  }
}
