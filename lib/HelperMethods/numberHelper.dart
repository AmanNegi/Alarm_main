class NumberHelper {
  static String stringNumber = "";
  static int intNumber;

  static addNumber(int val) {
    stringNumber = stringNumber + val.toString();
  }

  static bool numberSelected(){
    if(stringNumber.length >0){
      return true;
    }else{
      return false;
    }
  }
  static int getIntNumber() {
    int number = int.parse(stringNumber);
    intNumber = number;
    return intNumber;
  }
}
