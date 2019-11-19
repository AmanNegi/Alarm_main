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
  bool customPath;
  String path = "Select a file to see Text here";
  final MethodChannel platform = MethodChannel("aster.flutter.app/alarm/aster");

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

  @override
  void dispose() {
    // Clean up the focus node when the Form is disposed.
    myFocusNode.dispose();
    super.dispose();
  }

  Widget build(BuildContext context) {
    return ScopedModelDescendant<AlarmModel>(
      builder: (cntxt, wgt, model) {
        return Scaffold(
          body: GestureDetector(
            onTap: () {
              FocusScope.of(context).requestFocus(FocusNode());
            },
            child: CustomScrollView(
              slivers: <Widget>[
                SliverAppBar(
                  floating: true,
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
                  expandedHeight: 60.0,
                  pinned: true,
                ),
                SliverList(
                  delegate: SliverChildListDelegate(
                    [
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
                                      borderRadius: BorderRadius.circular(10.0),
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
                            subtitle: Text(path),
                          trailing: IconButton(
                            icon: Icon(Icons.radio),
                            onPressed: () async {
                              String path =
                                  await platform.invokeMethod("getMusicPicker");
                              print(path + 'in Flutter ');
                              if (!(path.length <= 0)) {
                                setState(() {
                                  customPath = true;
                                  this.path = path;
                                });
                              }
                            },
                          ),
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
                              customPath: 0,
                              repeating: repeating ? 1 : 0,
                              message: messageText,
                              id: widget.alarm.id,
                              hour: widget.alarm.hour,
                              minute: widget.alarm.minute,
                              timeString: widget.alarm.timeString,
                            );

                            updateAlarm(alarm, model.updateProduct);
                            Navigator.pop(context);
                          }
                        },
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
