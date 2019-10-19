import 'package:flutter/material.dart';
import 'StyledText.dart';

class SimpleListViewClass extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Row(
      children: <Widget>[
        Padding(
          padding: const EdgeInsets.only(left: 20.0),
          child: Align(
              alignment: Alignment.centerLeft,
              child: Row(
                children: <Widget>[
                  StyledTextClass(
                    fontSize: 40.0,
                    text: "8:00",
                  ),
                  StyledTextClass(
                    fontSize: 20.0,
                    text: '  AM',
                  )
                ],
              )),
        ),
      ],
    );
  }
}
