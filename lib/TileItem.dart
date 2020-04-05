import 'dart:async';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:scoped_model/scoped_model.dart';

import 'scoped-model/Alarms.dart';
import 'Imported/EnsureVisibleWhenFocused.dart';
import 'Alarm.dart';

class TileItem extends StatefulWidget {
  final Alarm alarm;
  final int index;

  TileItem({this.alarm, this.index});

  @override
  _TileItemState createState() => _TileItemState();
}

class _TileItemState extends State<TileItem> {
  // to get focus from the text editor
  var maxHeight, maxWidth;
  var myFocusNode = new FocusNode();
  final _formKey = GlobalKey<FormState>();
  String messageText;
  bool repeating;
  bool defaultMethod = true;
  bool customPath = false;
  StreamSubscription _timerSubscription;

  // the type of alarm
  List<Map<String, bool>> _list = [];
  String path = "";
  final MethodChannel platform = MethodChannel("aster.flutter.app/alarm/aster");
  static const stream =
      const EventChannel('com.aster.eventchannelsample/stream1');

  Widget build(BuildContext context) {
    return ScopedModelDescendant<AlarmModel>(
      builder: (context, wgt, model) {
        maxWidth = MediaQuery.of(context).size.width;
        maxHeight = MediaQuery.of(context).size.height;
        return Scaffold(
          floatingActionButton: FloatingActionButton.extended(
            tooltip: "Save",
            onPressed: () {
              checkIfDefault();
              if (_formKey.currentState.validate()) {
                _formKey.currentState.save();
                Alarm alarm = Alarm.withId(
                  customPath: customPath ? 1 : 0,
                  path: path,
                  defaultMethod: defaultMethod ? 1 : 0,
                  repeating: repeating ? 1 : 0,
                  message: messageText,
                  id: widget.alarm.id,
                  hour: widget.alarm.hour,
                  minute: widget.alarm.minute,
                  timeString: widget.alarm.timeString,
                );

                updateAlarm(alarm, model.updateAlarm);
                Navigator.pop(context);
              }
            },
            label: Text("Save"),
            icon: Icon(Icons.save),
          ),
          resizeToAvoidBottomInset: false,
          appBar: AppBar(
            leading: IconButton(
              icon: Icon(Icons.arrow_back),
              onPressed: () {
                Navigator.pop(context);
              },
            ),
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(20.0)),
            backgroundColor: Theme.of(context).bottomAppBarColor,
            centerTitle: true,
            titleSpacing: 10.0,
            title: Text(
              widget.alarm.message,
              style: TextStyle(fontSize: 25.0),
            ),
            automaticallyImplyLeading: false,
          ),
          body: GestureDetector(
            onTap: () {
              FocusScope.of(context).requestFocus(FocusNode());
            },
            child: Column(
              children: <Widget>[
                SizedBox(
                  height: 10.0,
                ),
                Padding(
                  padding: EdgeInsets.all(8.0),
                  child: EnsureVisibleWhenFocused(
                    focusNode: myFocusNode,
                    child: Material(
                      child: Form(
                        key: _formKey,
                        child: TextFormField(
                          validator: (value) {
                            if (value.length > 15) {
                              return 'Enter a small message for the alarm...';
                            }
                            return null;
                          },
                          autocorrect: false,
                          onSaved: ((value) {
                            setState(() {
                              this.messageText = value;
                            });
                          }),
                          initialValue: messageText,
                          decoration: InputDecoration(
                            prefixIcon: Icon(Icons.message),
                            border: OutlineInputBorder(
                                gapPadding: 10.0,
                                borderRadius: BorderRadius.circular(10.0),
                                borderSide: BorderSide()),
                          ),
                          focusNode: myFocusNode,
                        ),
                      ),
                    ),
                  ),
                ),
                Container(
                  child: ListTile(
                    leading: Text("Repeating"),
                    trailing: Checkbox(
                      value: repeating,
                      onChanged: (value) {
                        repeating = value;
                      },
                    ),
                  ),
                ),
                Container(
                  child: ListTile(
                    leading: Text("Select custom music"),
                    subtitle: Text(path != null ? path : "String variable"),
                    trailing: IconButton(
                      icon: Icon(Icons.radio),
                      onPressed: () async {
                        await platform.invokeMethod("getMusicPicker");
                        initiateStream();
                      },
                    ),
                  ),
                ),
                Container(
                  height: (40 / 100) * maxHeight,
                  width: maxWidth,
                  child: ListView.builder(
                    scrollDirection: Axis.horizontal,
                    itemCount: 2,
                    itemBuilder: (context, index) {
                      return createCard(index);
                    },
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  void updateAlarm(Alarm alarm, Function update) async {
    print("index in alarm Update : ${widget.index}");
    update(alarm, widget.index);
  }

  void createList() {
    print("default Method " + widget.alarm.defaultMethod.toString());
    bool value = widget.alarm.defaultMethod == 1 ? true : false;
    if (value) {
      setState(() {
        _list = [
          {"Default": true},
          {"Math": false}
        ];
      });
    } else {
      setState(() {
        _list = [
          {"Default": false},
          {"Math": true}
        ];
      });
    }
  }

  @override
  void initState() {
    super.initState();
    createList();
    setState(() {
      messageText = widget.alarm.message;
      repeating = widget.alarm.repeating == 1 ? true : false;
    });
    myFocusNode = FocusNode();
  }

  @override
  void dispose() {
    // Clean up the focus node when the Form is disposed.
    //  _timerSubscription.cancel();
    cancelStream();
    myFocusNode.dispose();
    super.dispose();
  }

  void initiateStream() {
    if (_timerSubscription == null) {
      _timerSubscription =
          stream.receiveBroadcastStream().listen(receiveFromStream);
    }
  }

  void cancelStream() {
    if (_timerSubscription != null) {
      _timerSubscription.cancel();
    }
  }

  void receiveFromStream(dynamic value) {
    print(value);
    Map<String, dynamic> a = value.cast<String, dynamic>();
    var _list = a.values.toList();
    var receivedCustomPath = (_list[0]);
    String receivedPath = (_list[1]);
    bool customPathVar = false;
    setState(() {
      path = receivedPath;
      if (receivedCustomPath == "true" || receivedCustomPath == true) {
        customPathVar = true;
      } else {
        customPathVar = false;
      }
      customPath = customPathVar;
    });
  }

  bool isActivated(int index) {
    if (_list[index].containsKey(true)) {
      return true;
    } else {
      return false;
    }
  }

  void alterPositions(int index) {
    for (int i = 0; i < 2; i++) {
      Map<String, bool> val = _list[i];
      List keys = val.keys.toList();
      setState(() {
        if (i == index) {
          _list[i] = {keys[0]: true};
        } else {
          _list[i] = {keys[0]: false};
        }
      });
    }
  }

  void checkIfDefault() {
    bool value;
    if (_list[0].containsValue(true)) {
      value = true;
    } else {
      value = false;
    }
    setState(() {
      defaultMethod = value;
    });
  }

  Widget createCard(int index) {
    List key = _list[index].keys.toList();
    bool val = _list[index].containsValue(true);
    Color color = val ? Colors.blueGrey[700] : Theme.of(context).bottomAppBarColor;
    Widget icon = _list[index].containsKey("Default")
        ? Image.asset("assets/Images/default.png")
        : Image.asset("assets/Images/calculator.png");

    return Container(
      decoration: BoxDecoration(
        color: color,
        borderRadius: BorderRadius.circular(10.0),
      ),
      margin: EdgeInsets.all(20),
      height: 250,
      width: (60 / 100) * maxWidth,
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(10.0),
          onTap: () {
            print(index);
            setState(() {
              bool activated = isActivated(index);
              if (activated) {
                print("Alredy selected!!");
              } else {
                alterPositions(index);
              }
            });
          },
          child: Container(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                Container(
                  child: icon,
                  height: 40,
                  width: 40,
                ),
                SizedBox(
                  height: 10,
                ),
                Text(
                  key[0],
                  style: TextStyle(color: Colors.white),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
