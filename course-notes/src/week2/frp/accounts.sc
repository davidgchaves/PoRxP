package week2.frp

object accounts {
  println("Welcome to the Scala worksheet")

  def consolidated(accts: List[BankAccount]): Signal[Int] =
    Signal(accts.map(_.balance()).sum)

  val accountA = new BankAccount
  val accountB = new BankAccount
  val consolidator = consolidated(List(accountA, accountB))

  consolidator()
  accountA deposit 50
  consolidator()
  accountA withdraw 30
  consolidator()
  accountB deposit 100
  consolidator()

  val exchangeRate = Signal(246.00)
  val inDollars = Signal(consolidator() * exchangeRate())

  inDollars()
  accountB withdraw 10
  inDollars()
}
