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
  static const platform = const MethodChannel('samples.flutter.dev/alarm');
  final _formKey = GlobalKey<FormState>();
  String messageText;
  List<int> intList = [0, 0, 0, 0, 0, 0, 0];
  List<String> days = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
  List<bool> isSelected = [false, false, false, false, false, false, false];
  List<String> daysFull = [
    'Sunday',
    'Monday',
    'Tuesday',
    'Wednesday',
    'Thursday',
    'Friday',
    'Saturday'
  ];
  List<Map<String, bool>> daysWithBool = [
    {"Sunday": false},
    {"Monday": false},
    {"Tuesday": false},
    {"Wednesday": false},
    {"Thursday": false},
    {"Friday": false},
    {"Saturday": false},
  ];

  void updateAlarm(Alarm alarm, Function update) async {
    print("index in alarm Update : ${widget.index}");
    update(alarm, widget.index, daysWithBool);
  }

  @override
  void initState() {
    print(" init state() " + widget.alarm.listInt.toString());
    setIntList();
    super.initState();
    myFocusNode = FocusNode();
  }

  void setIntList() {
    setState(() {
      this.messageText = widget.alarm.message;
      this.intList = widget.alarm.listInt;
    });
  }

  List<int> getIntListFromMapList() {
    List<int> getIntList = [];
    for (int a = 0; a < isSelected.length; a++) {
      getIntList.add(isSelected[a] ? 1 : 0);
    }
    return getIntList;
  }

  List<bool> fromIntListToBoolList(List<int> intList) {
    List<bool> boolList = [];
    for (int i = 0; i < intList.length; i++) {
      boolList.add(intList[i] == 1 ? true : false);
    }
    return boolList;
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
                        height: 70.0,
                        child: ListView.builder(
                          scrollDirection: Axis.horizontal,
                          itemCount: 7,
                          itemBuilder: (BuildContext context, int index) {
                            return Padding(
                              padding: EdgeInsets.all(5.0),
                              child: ChoiceChip(
                                selectedColor: Colors.tealAccent,
                                padding: EdgeInsets.all(10.0),
                                elevation: 10.0,
                                onSelected: (bool val) {
                                  setState(() {
                                    intList[index] = val == true ? 1 : 0;
                                    isSelected[index] = val;
                                    daysWithBool
                                        .insert(index, {daysFull[index]: val});
                                    print(" Selected terms  $isSelected");
                                  });
                                  print(isSelected.toString());
                                },
                                selected: fromIntListToBoolList(intList)[index],
                                label: Text(days[index]),
                              ),
                            );
                          },
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

                            Alarm alarm = Alarm.withInt(
                                message: messageText,
                                id: widget.alarm.id,
                                hour: widget.alarm.hour,
                                minute: widget.alarm.minute,
                                timeString: widget.alarm.timeString,
                                listInt: intList);
                            updateAlarm(alarm, model.updateProduct);
                            print("The text required here " +
                                alarm.listInt.toString());
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
