import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'Imported/EnsureVisibleWhenFocused.dart';
import 'Alarm.dart';

class TileItem extends StatefulWidget {
  final List<Alarm> alarmList;
  final int index;
  TileItem({this.alarmList, this.index});

  @override
  _TileItemState createState() => _TileItemState();
}

class _TileItemState extends State<TileItem> {
  var myFocusNode = new FocusNode();

  static const platform = const MethodChannel('samples.flutter.dev/alarm');

  List<String> days = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  List<bool> isSelected = [false, false, false, false, false, false, false];
  int _hour;
  int _minute;

  void getHourMinuteForDismissing() {
    int hour = widget.alarmList[widget.index].hour;
    if (hour >= 12) {
      hour = hour - 12;
    }
    setState(() {
      _hour = hour;
      _minute = widget.alarmList[widget.index].minute;
    });
  }

  void _dismissAlarm() async {
    platform.invokeMethod('dismissAlarm', {'hour': _hour, 'minute': _minute});
  }

  @override
  void initState() {
    super.initState();
    getHourMinuteForDismissing();
    myFocusNode = FocusNode();
  }

  List<int> getList() {
    List<int> newList = [];
    for (int i = 0; i < isSelected.length; i++) {
      if (isSelected[i] == true) {
        newList.add(i + 1);
      }
    }
    return newList;
  }

  @override
  void dispose() {
    // Clean up the focus node when the Form is disposed.
    myFocusNode.dispose();
    super.dispose();
  }

  Widget build(BuildContext context) {
    return Scaffold(
      body: GestureDetector(
        onTap: () {
          FocusScope.of(context).requestFocus(FocusNode());
        },
        child: CustomScrollView(
          slivers: <Widget>[
            SliverAppBar(
              expandedHeight: 256.0,
              pinned: true,
              flexibleSpace: FlexibleSpaceBar(
                background: Hero(
                  child: FadeInImage(
                    placeholder: AssetImage("assets/Images/coffee.jpg"),
                    image: NetworkImage(
                        "https://picsum.photos/485/384?image=${widget.index + 100}"),fit: BoxFit.cover,
                  ),
                  tag: widget.index,
                ),
                title: Text(widget.alarmList[widget.index].message),
              ),
            ),
            SliverList(
              delegate: SliverChildListDelegate(
                [
                  // Image.network("https://picsum.photos/485/384?image=$num"),
                  //Image.asset("assets/Images/coffee.jpg"),
                  Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: EnsureVisibleWhenFocused(
                      focusNode: myFocusNode,
                      child: Material(
                        child: TextField(
                          decoration: InputDecoration(
                            hintText: 'Enter message',
                            icon: Icon(Icons.message),
                            border: OutlineInputBorder(
                                gapPadding: 10.0,
                                borderRadius: BorderRadius.circular(10.0),
                                borderSide: BorderSide()),
                          ),
                          focusNode: myFocusNode,
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
                          child: FilterChip(
                            selectedColor: Theme.of(context).accentColor,
                            padding: EdgeInsets.all(10.0),
                            elevation: 10.0,
                            onSelected: (bool val) {
                              setState(() {
                                isSelected[index] = val;
                                print(getList().toString());
                              });
                              print(isSelected.toString());
                            },
                            selected: isSelected[index],
                            label: Text(days[index]),
                          ),
                        );
                      },
                    ),
                  ),
                  RaisedButton(
                    child: Text("Dismiss alarm"),
                    onPressed: () async {
                      _dismissAlarm();
                    },
                  )
                ],
              ),
            )
          ],
        ),
      ),
    );
  }
}
