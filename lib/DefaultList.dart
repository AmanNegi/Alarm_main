import 'package:alarm_main/AlarmView.dart';
import 'package:alarm_main/HelperMethods/MyBlinkingImage.dart';
import 'package:alarm_main/widgets/BottomAppBarBuilder.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class DefaultList extends StatefulWidget {
  @override
  _DefaultListState createState() => _DefaultListState();
}

class _DefaultListState extends State<DefaultList> {
  bool value = false;
  Widget mainWidget;
  Color color = Colors.white30;
  Function onPressed;
  MethodChannel channel = MethodChannel("aster.flutter.app/alarm/aster");

  @override
  void initState() {
    mainWidget = Builder(
      builder: (context) {
        var width = MediaQuery.of(context).size.width;
        return ListView(
          children: <Widget>[
            Padding(
              padding: EdgeInsets.all(10.0),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(15.0),
                child: Container(
                  color: Theme.of(context).cardColor,
                  height: 100.0,
                  child: Material(
                    color: Colors.transparent,
                    child: InkWell(
                      splashColor: Colors.black,
                      onTap: () {},
                      child: Dismissible(
                        key: new ObjectKey(365),
                        direction: DismissDirection.startToEnd,
                        background: Container(
                          decoration: BoxDecoration(
                            gradient: LinearGradient(
                              colors: [Colors.red[700], Colors.black26],
                            ),
                          ),
                          child: Align(
                            alignment: Alignment.centerLeft,
                            child: Icon(
                              Icons.delete,
                              color: Colors.white30,
                            ),
                          ),
                        ),
                        onDismissed: ((direction) {
                          if (direction == DismissDirection.startToEnd) {
                            showSnackBar(
                                context, "You dismissed an alarm successfully");
                            changeWidget();
                          }
                        }),
                        child: Center(
                          child: Row(
                            crossAxisAlignment: CrossAxisAlignment.center,
                            children: <Widget>[
                              SizedBox(
                                width: (5/100)*width,
                              ),
                              Image.asset(
                                "assets/Images/mainAlarm.png",
                                color: Colors.white30,
                                height: 40,
                                width: 40,
                              ),
                              SizedBox(
                                width: (5/100)*width,
                              ),
                              Text(
                                "12:00 PM",
                                style: TextStyle(
                                    fontSize: 35.0,
                                    fontWeight: FontWeight.w500,
                                    color: Colors.white30),
                              ),
                              SizedBox(
                                width: ((18 / 100) * width),
                              ),
                              MyBlinkingImage(
                                assetString: "assets/Images/forward.png",
                                width: 45,
                                height: 45,
                              ),
                            ],
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ),
            ),
            Center(
              child: Text(
                "Swipe Right to dismiss an alarm",
                style: TextStyle(color: Colors.white),
              ),
            ),
          ],
        );
      },
    );
    super.initState();
  }

  void showSnackBar(BuildContext modernContext, String text) {
    Scaffold.of(modernContext).showSnackBar(SnackBar(
      duration: Duration(seconds: 1),
      elevation: 50.0,
      backgroundColor: Theme.of(modernContext).primaryColor,
      action: SnackBarAction(
        label: "Ok",
        textColor: Colors.blue,
        onPressed: () {},
      ),
      content: Text(
        text,
        style: TextStyle(color: Theme.of(modernContext).primaryColorLight),
      ),
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.all(Radius.circular(10.0)),
      ),
    ));
  }

  @override
  Widget build(BuildContext mainContext) {
    return Scaffold(
        bottomNavigationBar: BottomAppBarBuilder(
          color: Colors.white30,
          doAnything: false,
        ),
        floatingActionButtonLocation: FloatingActionButtonLocation.centerDocked,
        floatingActionButton: FloatingActionButton(
          backgroundColor: color,
          child: Icon(Icons.add),
          onPressed: onPressed,
        ),
        body: mainWidget);
  }

  void changeWidget() {
    setState(() {
      mainWidget = Builder(
        builder: (BuildContext newContext) {
          return Container(
            width: MediaQuery.of(context).size.width,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisSize: MainAxisSize.max,
              mainAxisAlignment: MainAxisAlignment.end,
              children: <Widget>[
                Text(
                  "Add alarms using this button",
                  style: TextStyle(color: Colors.white),
                ),
                SizedBox(
                  height: 40,
                ),
                MyBlinkingImage(
                  assetString: "assets/Images/down-arrow.png",
                  height: 80,
                  width: 80,
                ),
                SizedBox(
                  height: 40,
                )
              ],
            ),
          );
        },
      );
      color = Colors.lightBlueAccent;
      onPressed = () {
        channel.invokeMethod("showToast", {"string": "Great! Intro completed"});
        Navigator.of(context).pushReplacement(
          MaterialPageRoute(
            builder: (buildContext) {
              return AlarmView();
            },
            fullscreenDialog: true,
          ),
        );
      };
    });
  }
}
