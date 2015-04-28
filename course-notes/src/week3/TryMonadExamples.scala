/*
 * This is just a personal notes file to further digest week3 material
 *
 * If you want to compile, etc go to the original source
 * SafeAdventure.scala which has all dependencies and such:
 * https://github.com/headinthebox/CourseraCodeSamplesReactiveProgramming
 */
import scala.util.{Failure, Success, Try}

object Adventure {
  def apply(): Adventure = new Adventure(){
    var eatenByMonster: Boolean = true
    val treasureCost: Int = 42
  }
}

trait Adventure {

  var eatenByMonster: Boolean
  val treasureCost: Int

  def collectCoins(): Try[List[Coin]] = ???

  def buyTreasure(coins: List[Coin]): Try[Treasure] = ???

  // 1 - Dealing with Failure Explicitly (pattern matching)
  def PlayI(): Unit = {
    val adventure = Adventure()

    val coins: Try[List[Coin]]  = adventure.collectCoins()

    // Since collectCoins returns a Try, now we need to pattern match
    // in treasure to access the coins
    val treasure: Try[Treasure] = coins match {
      case Success(cs)          => adventure.buyTreasure(cs)
      case Failure(t)           => Failure(t)
    }
  }

  // 2 - Reducing noise with Monads (flatMap)
  def PlayII(): Unit = {
    val adventure = Adventure()

    val coins: Try[List[Coin]]  = adventure.collectCoins()

    // flatMap unwraps the Try[T]
    // Compare this treasure with the noisy treasure from PlayI,
    // and realize that now we only need to deal with the happy path...
    // ... no more pattern matching or Failure
    // "FlatMap is the plumber of the Happy Path" Erik Meijer
    val treasure: Try[Treasure] = coins.flatMap(cs => adventure.buyTreasure(cs))
  }

  // 3 - Reducing even more noise with Monads (for-comprehensions)
  def PlayIII(): Unit = {
    val adventure = Adventure()

    val treasure: Try[Treasure] = for {
      coins    <- adventure.collectCoins()
      treasure <- buyTreasure(coins)
    } yield treasure
  }
}
