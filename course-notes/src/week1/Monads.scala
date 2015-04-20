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

  */

}

