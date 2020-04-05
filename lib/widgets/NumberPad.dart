import 'package:flutter/material.dart';

import '../HelperMethods/numberHelper.dart';

class NumberPad extends StatefulWidget {
  @override
  _NumberPadState createState() => _NumberPadState();
}

class _NumberPadState extends State<NumberPad> {
  Widget createTextField() {
    return Container(
      width: ((80 / 100) * MediaQuery.of(context).size.width),
      height: 60.0,
      margin: EdgeInsets.all(10.0),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.0),
        border: Border.all(
          color: Colors.grey[200],
        ),
      ),
      child: Center(
        child: Text(
          NumberHelper.stringNumber,
          style: TextStyle(fontSize: 30.0, color: Colors.white),
        ),
      ),
    );
  }

  Widget createNoPad(int text) {
    return Container(
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: Colors.grey,
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(30.0),
          splashColor: Colors.white,
          onTap: () {
            setState(() {
              if (NumberHelper.stringNumber.length < 5) {
                NumberHelper.addNumber(text);
              }
            });
          },
          child: Container(
            width: 60,
            height: 60,
            child: Center(
              child: Text(
                text.toString(),
                style: TextStyle(fontSize: 25.0),
              ),
            ),
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              color: Colors.transparent,
            ),
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    var height = MediaQuery.of(context).size.height;
    return Container(
     // height: ((60 / 100) * height),
      child: Padding(
        padding: EdgeInsets.symmetric(horizontal: 30.0),
        child: Column(
          children: <Widget>[
            createTextField(),
            SizedBox(
              height: 30.0,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                createNoPad(1),
                createNoPad(2),
                createNoPad(3)
              ],
            ),
            SizedBox(
              height: (0.025)*height,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                createNoPad(4),
                createNoPad(5),
                createNoPad(6)
              ],
            ),
            SizedBox(
              height: (0.025)*height,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                createNoPad(7),
                createNoPad(8),
                createNoPad(9)
              ],
            ),
            SizedBox(
              height: (0.025)*height,
            ),
            createNoPad(0),
          ],
        ),
      ),
    );
  }
}
