import 'package:flutter/material.dart';

class SnackBarBuilder extends StatelessWidget {

  @override
  SnackBar build(BuildContext context) {
    return SnackBar(
      duration: Duration(milliseconds: 1500),
      elevation: 50.0,
      backgroundColor: Theme.of(context).primaryColor,
      action: SnackBarAction(
        label: "Ok",
        textColor: Colors.blue,
        onPressed: () {},
      ),
      content: Text(
        "Alarm pre-exists",
        style: TextStyle(color: Theme.of(context).primaryColorLight),
      ),
      behavior: SnackBarBehavior.floating,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.all(Radius.circular(10.0)),
      ),
    );
  }
}
