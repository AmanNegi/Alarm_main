import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'scoped-model/Alarms.dart';
import 'Imported/EnsureVisibleWhenFocused.dart';
import 'package:scoped_model/scoped_model.dart';
import 'Alarm.dart';

class TileItem extends StatefulWidget {
  final Alarm alarm;
  final int index;
  final Color color;

  TileItem({this.alarm, this.index, this.color});

  @override
  _TileItemState createState() => _TileItemState();
}

class _TileItemState extends State<TileItem> {
  var myFocusNode = new FocusNode();
  final _formKey = GlobalKey<FormState>();
  String messageText;
  bool repeating;
  bool customPath = false;
  StreamSubscription _timerSubscription;

  String path = "";
  final MethodChannel platform = MethodChannel("aster.flutter.app/alarm/aster");
  static const stream =
      const EventChannel('com.aster.eventchannelsample/stream1');

  void updateAlarm(Alarm alarm, Function update) async {
    print("index in alarm Update : ${widget.index}");
    update(alarm, widget.index);
  }

  @override
  void initState() {
    super.initState();
    setState(() {
      messageText = widget.alarm.message;
      repeating = widget.alarm.repeating == 1 ? true : false;
    });
    myFocusNode = FocusNode();
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

  @override
  void dispose() {
    // Clean up the focus node when the Form is disposed.
    //  _timerSubscription.cancel();
    cancelStream();
    myFocusNode.dispose();
    super.dispose();
  }

  Widget build(BuildContext context) {
    return ScopedModelDescendant<AlarmModel>(
      builder: (cntxt, wgt, model) {
        return Scaffold(
          appBar: AppBar(
            leading: IconButton(
              icon: Icon(Icons.arrow_back),
              onPressed: () {
                Navigator.pop(context);
              },
            ),
            shape: StadiumBorder(),
            backgroundColor: widget.color,
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
                  height: 40.0,
                ),
                Padding(
                  padding: const EdgeInsets.all(8.0),
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
                                borderRadius:
                                BorderRadius.circular(10.0),
                                borderSide: BorderSide()),
                          ),
                          focusNode: myFocusNode,
                        ),
                      ),
                    ),
                  ),
                ), //filterChip

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
                    subtitle:
                    Text(path != null ? path : "String variable"),
                    trailing: IconButton(
                      icon: Icon(Icons.radio),
                      onPressed: () async {
                        await platform.invokeMethod("getMusicPicker");
                        initiateStream();
                      },
                    ),
                  ),
                ),
                Expanded(
                  child: ListView(
                    scrollDirection: Axis.horizontal,
                    children: <Widget>[
                      Card(
                        child: Text("ranom"),
                      ),
                      Card(
                        child: Text("randin"),
                      )
                    ],
                  ),
                ),
                SizedBox(
                  height: 20.0,
                ),
                RaisedButton(
                  child: Text("Update alarm"),
                  onPressed: () {
                    if (_formKey.currentState.validate()) {
                      _formKey.currentState.save();
                      Alarm alarm = Alarm.withId(
                        customPath: customPath ? 1 : 0,
                        path: path,
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
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
