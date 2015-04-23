package week2.frp

/*
 * A Simple FRP Implementation
 *
 * NOTE:
 *  If objects have an apply method then they enable an special Contruction Syntax like this:
 *    Signal(expr) or Var(expr)
 */



/*
 * The Signal API
 *
 *  Each signal mantains:
 *    - Its current value
 *    - The current expression that defines the signal value
 *    - The other signals that depend on its value (A set of Observers)
 *  If the signal changes, all observers (signals) need to be re-evaluated
 */

class Signal[T](expr: => T) {
  import Signal._
  private var currentExpr: () => T = _
  private var currentValue: T = _
  private var observers: Set[Signal[_]] = Set()
  update(expr)

  protected def update(expr: => T): Unit = {
    currentExpr = () => expr
    computeValue()
  }

  protected def computeValue(): Unit = {
    var newValue = caller.withValue(this)(currentExpr())
    // If currentValueIsNotUpdatedTo newValue
    if (currentValue != newValue) {
      // update currentValue
      currentValue = newValue
      // propagate the changes to the observant signal
      val obs = observers
      observers = Set()
      obs.foreach(_.computeValue())
    }
  }

  def apply(): T = {
    observers += caller.value
    assert(!caller.value.observers.contains(this), "cyclic signal definition")
    currentValue
  }
}

// Initially there's no caller.
// We use a sentinel object called NoSignal
object NoSignal extends Signal[Nothing](???) {
  override def computeValue() = ()
}

// Signal[_] -> It can take signals of any value type
// NoSignal  -> The initial value
object Signal {
  // WARNING: GLOBAL STATE!!!
  //  Solution 1: Use Thread-Local State (avoiding race conditions)
  //    private val caller = new DynamicVariable[Signal[_]](NoSignal)
  //  Solution 2: Use Implicit Parameters
  //    future versions of Scala might solve this problem
  private val caller = new StackableVariable[Signal[_]](NoSignal)

  def apply[T](expr: => T) = new Signal(expr)
}


/* The Var API */
class Var[T](expr: => T) extends Signal[T](expr) {
  override def update(expr: => T): Unit = super.update(expr)
}

object Var {
  def apply[T](expr: => T) = new Var(expr)
}

