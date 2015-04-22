package week2.pubsub

class BankAccount extends Publisher {
  private var balance = 0

  def currentBalance = balance

  def deposit(amount: Int): Unit = {
    if (amount > 0) balance = balance + amount
    publish()
  }

  def withdraw(amount: Int): Unit = {
    if (sufficientFundsForWithdrawing(amount)) {
      balance = balance - amount
      publish()
    } else throw new Error("insufficient funds")
  }

  private def sufficientFundsForWithdrawing(amount: Int): Boolean = {
    0 < amount && amount <= balance
  }

}