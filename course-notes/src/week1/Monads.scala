/*
  Instructions to convert this file into a Worksheet file:
    1: Change file extension from .scala to .sc
    2: Change object name to something else
       (like MonadsWorksheet)
*/
package week1

object Monads {
  println("Welcome to the Monads' Notes")

  /*
  MONADS

    A Monad M is a parametric type M[T] with
      - 2 operations
      - 3 laws


  MONAD OPERATIONS

    In Haskell:
      - return :: a -> M a
      - >>=    :: M a -> (a -> M b) -> M b

    In Scala:
      - unit[T]   (x: T):        M[T]
      - flatMap[U](f:T => M[U]): M[U]

    In Scala flatMap is a function wrapped in a Monad trait:
      trait M[T] {
        flatMap[U](f:T => M[U]): M[U]
      }
    whereas unit is different for each Monad.

    NOTE: Usually flatMap is called bind


  EXAMPLES OF MONADS IN SCALA

    - List      is a Monad with unit(x) = List(x)
    - Set       is a Monad with unit(x) = Set(x)
    - Option    is a Monad with unit(x) = Some(x)
    - Generator is a Monad with unit(x) = single(x)


  MONADS AND MAP

    map can be defined in terms of flatMap and unit:
      m map f == m flatMap (x => unit(f(x)))

    or using the andThen combinator (allows function composition in Scala):
      m map f == m flatMap (f andThen unit)


  MONAD LAWS

    Associativity:
      m flatMap f flatMap g == m flatMap (x => f(x) flatMap g)

    Left unit:
      unit(x) flatMap f == f(x)

    Right unit:
      m flatMap unit == m


  SCALA'S FOR-COMPREHENSIONS vs HASKELL'S DO-NOTATION

    Scala's 'for comprehensions' are equivalent to Haskell's 'do' notation,
    and it is nothing more than a syntactic sugar for composition of multiple monadic operations.

    In Haskell we have do-notation for Monads:
      do x <- [1, 2, 3]
         y <- [4, 5, 6]
         let z = (x + y)
         return z

    In Scala we have for-comprehensions for Monads:
      for { x <- List(1, 2, 3)
            y <- List(4, 5, 6)
            z = x + y
      } yield z


  MONADS WITH ZERO

    Monads that also define a withFilter function
  */

}

