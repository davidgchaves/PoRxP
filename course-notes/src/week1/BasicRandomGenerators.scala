package week1

object BasicRandomGenerators {
  println("Welcome to the Basic Random Generators' Notes")

  // The Generator 'type'
  trait Generator[+T] {
    self =>    // an alias for 'this'

    def generate: T

    //NOTE: The random boolean generator needs foreach to work properly
    def foreach[U](f: T => U) {
      f(generate)
    }

    def map[S](f:T => S): Generator[S] = new Generator[S] {
      def generate = f(self.generate)
    }

    def flatMap[S](f:T => Generator[S]): Generator[S] = new Generator[S] {
      def generate = f(self.generate).generate
    }
  }

  // A Random Generator for Integers
  val randomIntegers = new Generator[Int] {
    def generate = scala.util.Random.nextInt()
  }

  randomIntegers.generate

  // A Random Generator for Booleans
  val randomBooleans = for (x <- randomIntegers) yield x > 0

  randomBooleans.generate

  // A Random Generator for Pairs
  def randomPairs[T,U](t: Generator[T], u: Generator[U]) = for {
    x <- randomIntegers
    y <- randomIntegers
  } yield (x,y)

  randomPairs(randomIntegers,randomIntegers).generate

  // Some building blocks for Generators
  def unit[T](x: T): Generator[T] = new Generator[T] {
    def generate = x
  }

  unit(3).generate

  // A Random Generator for Lists of Integers: A list is either an empty list or a non-empty list
  def randomLists: Generator[List[Int]] = for {
    isEmpty <- randomBooleans
    list    <- if (isEmpty) emptyLists else nonEmptyLists
  } yield list

  def emptyLists = unit(Nil)

  def nonEmptyLists = for {
    head <- randomIntegers
    tail <- randomLists
  } yield head :: tail

  randomLists.generate

}
