package week2.pubsub

object accounts {
  println("Welcome to the Scala worksheet")

  val accountA = new BankAccount
  val accountB = new BankAccount
  val consolidator = new Consolidator(List(accountA, accountB))

  consolidator.totalBalance
  accountA deposit 50
  consolidator.totalBalance
  accountA withdraw 30
  consolidator.totalBalance
  accountB deposit 100
  consolidator.totalBalance

  accountA withdraw 40
}
