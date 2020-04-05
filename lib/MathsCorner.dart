import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

import 'HelperMethods/platfomInvoker.dart';
import 'widgets/NumberPad.dart';
import 'HelperMethods/numberHelper.dart';
import 'dart:math';

class MathsCorner extends StatefulWidget {
  @override
  State<StatefulWidget> createState() {
    return MathsCornerState();
  }
}

class MathsCornerState extends State<MathsCorner> {
  var height;
  var width;
  var color = Colors.purple[600];
  var randomNumber = new Random();
  int correctAnswers = 0;
  int firstNumber;
  int secondNumber;

  @override
  Widget build(BuildContext context) {
    height = MediaQuery.of(context).size.height;
    width = MediaQuery.of(context).size.width;
    return Scaffold(
      body: Container(
        height: height,
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [color, Colors.black, Colors.black],
          ),
        ),
        child: Column(
          children: <Widget>[
            SizedBox(
              height: (0.3 / 4) * height,
            ),
            createTextView(
                firstNumber.toString() + "  +  " + secondNumber.toString()),
            SizedBox(
              height: (0.2 / 4) * height,
            ),
            NumberPad(),
            SizedBox(
              height: (0.15/4)*height,
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[

                createButton(Icons.backspace, Colors.red[900], Colors.orange,
                    onPressedClear),

                createButton(Icons.arrow_forward, Colors.green,
                    Colors.lightGreenAccent, onPressedOk),

                createButton(Icons.refresh, Colors.blue, Colors.lightBlueAccent,
                    onPressedRefresh),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget createButton(
      IconData icon, Color mainColor, Color accentColor, Function onPressed) {
    return Container(
      decoration: BoxDecoration(
        color: mainColor,
        shape: BoxShape.circle,
      ),
      width: 75.0,
      height: 75.0,
      child: new RawMaterialButton(
        splashColor: accentColor,
        shape: new CircleBorder(),
        elevation: 1.0,
        child: Icon(
          icon,
          color: Colors.white,
        ),
        onPressed: onPressed,
      ),
    );
  }

  void onPressedClear() {
    setState(() {
      color = Colors.purple[600];
      NumberHelper.stringNumber = "";
    });
  }

  void onPressedOk() {
    if (NumberHelper.numberSelected() &&
        getSum() == NumberHelper.getIntNumber()) {
      print(NumberHelper.getIntNumber().toString());
      setState(() {
        correctAnswers++;
        NumberHelper.stringNumber = "";
        color = Colors.green;
      });
      if (correctAnswers == 3) {
        PlatformInvoker.invokeCloseWindowMath();
      } else if (correctAnswers < 3) {
        changeValues();
      }
    } else if (!NumberHelper.numberSelected() ||
        !(getSum() == NumberHelper.getIntNumber())) {
      setState(() {
        color = Colors.red[900];
        NumberHelper.stringNumber = "";
      });
    }
    print("value of correctAnswers " + correctAnswers.toString());
  }

  void onPressedRefresh() {
    setState(() {
      color = Colors.purple[600];
      NumberHelper.stringNumber = "";
      changeValues();
    });
  }

  @override
  void initState() {
    super.initState();
    print(" in MathsCorner.dart ");
    SystemChrome.setEnabledSystemUIOverlays([]);
    changeValues();
  }

  void changeValues() {
    setState(() {
      firstNumber = randomNumber.nextInt(100);
      secondNumber = randomNumber.nextInt(100);
    });
  }

  int getSum() {
    return firstNumber + secondNumber;
  }

  Widget createTextView(String text) {
    return Text(
      text,
      style: TextStyle(fontSize: 60.0),
    );
  }
}
