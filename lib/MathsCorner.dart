import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';

import 'HelperMethods/platfomInvoker.dart';
import 'widgets/NumberPad.dart';
import 'HelperMethods/numberHelper.dart';
import 'dart:math';

class MathsCorner extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: MainMath(),
    );
  }
}

class MainMath extends StatefulWidget {
  @override
  _MainMathState createState() => _MainMathState();
}

class _MainMathState extends State<MainMath> {
  var color = Colors.purple[600];
  var rng = new Random();
  var height;
  int receivedId;
  int correctAnswers = 0;
  int firstNumber;
  int secondNumber;
  int currentNumber;

  @override
  void initState() {
    super.initState();
   // SystemChrome.setEnabledSystemUIOverlays([]);
    changeValues();
  }

  void changeValues() {
    setState(() {
      firstNumber = rng.nextInt(100);
      secondNumber = rng.nextInt(100);
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

  Widget createNoPad(String text) {
    return Container(
      width: 60,
      height: 60,
      child: Text(text),
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: Colors.grey,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    height = MediaQuery.of(context).size.height;

    return Scaffold(
      body: Container(
        height: height,
        decoration: BoxDecoration(
            gradient: LinearGradient(
                begin: Alignment.topCenter,
                end: Alignment.bottomCenter,
                colors: [color, Colors.black)),
        child: Column(
          children: <Widget>[
            SizedBox(
              height: 90.0,
            ),
            createTextView(
                firstNumber.toString() + "  +  " + secondNumber.toString()),
            SizedBox(
              height: 40,
            ),
            NumberPad(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: <Widget>[
                RaisedButton(
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20.0)),
                  child: Text("Clear"),
                  onPressed: () {
                    print(MediaQuery.of(context).size.height);
                    setState(() {
                      height = MediaQuery.of(context).size.height;
                    });
                    //  setState(() {
                    //   color = Colors.purple[600];
                    //   NumberHelper.stringNumber = "";
                    // });
                  },
                ),
                RaisedButton(
                  padding: EdgeInsets.all(20.0),
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10.0)),
                  child: Text(
                    " Check ",
                    style: TextStyle(fontSize: 20.0),
                  ),
                  color: color,
                  onPressed: () {
                    if (NumberHelper.numberSelected() &&
                        getSum() == NumberHelper.getIntNumber()) {
                      print(NumberHelper.getIntNumber().toString());
                      setState(() {
                        correctAnswers++;
                        NumberHelper.stringNumber = "";
                        color = Colors.green;
                      });
                      if (correctAnswers == 3) {
                        setState(() {
                          color = Colors.green;
                          NumberHelper.stringNumber = "";
                        });
                        PlatformInvoker.invokeTheMethod();
                        // SystemNavigator.pop();
                      }
                      changeValues();
                    } else if (!NumberHelper.numberSelected() ||
                        !(getSum() == NumberHelper.getIntNumber())) {
                      setState(() {
                        color = Colors.red;
                        NumberHelper.stringNumber = "";
                      });
                    }

                    print(
                        "value of correctAnswers " + correctAnswers.toString());
                  },
                ),
                RaisedButton(
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(20.0)),
                  child: Text("Change"),
                  onPressed: () {
                    setState(() {
                      changeValues();
                      color = Colors.purple[600];
                      NumberHelper.stringNumber = "";
                    });
                  },
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
