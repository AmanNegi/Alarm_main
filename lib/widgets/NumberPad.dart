import 'package:flutter/material.dart';

import '../HelperMethods/numberHelper.dart';

class NumberPad extends StatefulWidget {
  @override
  _NumberPadState createState() => _NumberPadState();
}

class _NumberPadState extends State<NumberPad> {
  @override
  initState() {
    super.initState();
  }

  Widget createTextField() {
    return Container(
      width: 450,
      height: 60.0,
      margin: EdgeInsets.all(15.0),
      padding: EdgeInsets.only(left: 10.0, top: 10.0, bottom: 10.0),
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(10.0),
        border: Border.all(
          color: Colors.grey,
        ),
      ),
      child: Text(
        NumberHelper.stringNumber,
        style: TextStyle(fontSize: 30.0, fontFamily: "Raleway-Bold"),
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
          splashColor: Colors.black,
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
                style: TextStyle(fontSize: 25.0, fontFamily: "Raleway-Bold"),
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
    return Container(
      child: Padding(
        padding: EdgeInsets.symmetric(horizontal: 30.0),
        child: Column(
          children: <Widget>[
            createTextField(),
            SizedBox(
              height: 40.0,
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
              height: 20.0,
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
              height: 20.0,
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
              height: 20.0,
            ),
            createNoPad(0),
            SizedBox(
              height: 20.0,
            ),
          ],
        ),
      ),
    );
  }
}
