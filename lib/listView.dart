import 'package:alarm_main/TileItem.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import 'scoped-model/Alarms.dart';
import 'package:scoped_model/scoped_model.dart';
import "Alarm.dart";
import 'TileItem.dart';

class ListViewClass extends StatefulWidget {
  @override
  _ListViewClassState createState() => _ListViewClassState();
}

class _ListViewClassState extends State<ListViewClass> {
 

  @override
  initState() {
    print("In initState() [ListView.dart]");
    super.initState();
  }

  void dismissAlarm(Function deleteProduct, int id , int index) {
    deleteProduct(id,index);
  }

  @override
  Widget build(BuildContext context) {
    return ScopedModelDescendant<AlarmModel>(
      builder: (BuildContext context, Widget widget, AlarmModel model) {
        model.refreshData();
        List<Alarm> alarmList = model.alarms;
        return ListView.builder(
          itemCount: alarmList.length,
          itemBuilder: (BuildContext context, int index) {
            return Dismissible(
              direction: DismissDirection.startToEnd,
              onDismissed: (direction) {
                if (direction == DismissDirection.startToEnd) {
                  Scaffold.of(context).showSnackBar(SnackBar(
                    backgroundColor: Theme.of(context).primaryColor,
                    action: SnackBarAction(
                      label: "Ok",
                      textColor: Colors.blue,
                      onPressed: () {},
                    ),
                    content: Text(
                      "Alarm dismissed",
                      style:
                          TextStyle(color: Theme.of(context).primaryColorLight),
                    ),
                    behavior: SnackBarBehavior.floating,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.all(Radius.circular(10.0)),
                    ),
                  ));
                  dismissAlarm(model.deleteProduct, alarmList[index].id , index);
                }
              },
              background: Container(
                color: Colors.red[400],
                child: Align(
                  alignment: Alignment.centerLeft,
                  child: Icon(Icons.delete),
                ),
              ),
              key: new ObjectKey(index),
              child: Stack(
                children: <Widget>[
                  Padding(
                    padding: const EdgeInsets.all(18.0),
                    child: InkWell(
                      onTap: () {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            fullscreenDialog: true,
                            builder: (BuildContext context) {
                              return TileItem(
                                  alarm: alarmList[index],index: index,);
                            },
                          ),
                        );
                      },
                      child: Card(
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.all(
                            Radius.circular(16.0),
                          ),
                        ),
                        child: Column(
                          children: <Widget>[
                            //Image.asset("assets/Images/coffee.jpg")),
                            Hero(
                              tag: index,
                              child: FadeInImage(
                                height: 300,
                                width: 485,
                                placeholder:
                                    AssetImage("assets/Images/loading.gif"),
                                image: NetworkImage(
                                    "https://picsum.photos/485/384?image=${alarmList[index].id + 100}"),
                                fit: BoxFit.cover,
                              ),
                            ),
                            //TODO remove the placeholder and place the image here
                            Text(
                              alarmList[index].timeString,
                              style: TextStyle(fontSize: 30.0),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                  Divider(),
                ],
              ),
            );
          },
        );
      },
    );
  }
}
