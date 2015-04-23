package week2.frp

class StackableVariable[T](init: T) {
  private var values: List[T] = List(init)

  def value: T = values.head

  // withValue:
  //  (1) Puts the newValue on the top of the Stack
  //  (2) Executes the op Operation
  //  (3) When it's done it pops the newValue off the Stack
  def withValue[R](newValue: T)(op: => R): R = {
    values = newValue :: values
    try op finally values = values.tail
  }
}