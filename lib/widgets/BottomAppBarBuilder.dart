import 'package:flutter/material.dart';

class BottomAppBarBuilder extends StatelessWidget {
  final Color color;
  final bool doAnything;

  const BottomAppBarBuilder({this.color, this.doAnything});

  @override
  Widget build(BuildContext context) {
    return BottomAppBar(
      notchMargin: 10.0,
      shape: CircularNotchedRectangle(),
      child: new Row(
        mainAxisSize: MainAxisSize.max,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: <Widget>[
          IconButton(
            icon: Icon(Icons.menu,color: color,),
            onPressed: () {
              if (doAnything) {
                showModalBottomSheet(
                  context: context,
                  builder: (BuildContext context) {
                    return Container(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: <Widget>[
                          Text(" Give some idea what to put here?? ")
                          //     Image.asset("assets/Images/aj.png"),
                          //     Text("© AsterJoules"),
                          //    SizedBox(
                          //     height: 10.0,
                          //     ),
                        ],
                      ),
                    );
                  },
                );
              }
            },
          ),
        ],
      ),
    );
  }
}
