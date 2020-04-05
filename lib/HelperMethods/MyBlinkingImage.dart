import 'package:flutter/material.dart';

class MyBlinkingImage extends StatefulWidget {
  final String assetString;
  final double height;
  final double width;

  MyBlinkingImage({this.assetString, this.width, this.height});

  @override
  _MyBlinkingImageState createState() => _MyBlinkingImageState();
}

class _MyBlinkingImageState extends State<MyBlinkingImage>
    with SingleTickerProviderStateMixin {
  AnimationController _animationController;

  @override
  void initState() {
    _animationController =
        new AnimationController(vsync: this, duration: Duration(seconds: 3));

    _animationController.repeat();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return FadeTransition(
      opacity: _animationController,
      child: Image.asset(
        widget.assetString,
        height: widget.height,
        width: widget.width,
      ),
    );
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }
}
