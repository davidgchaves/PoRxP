package week2.frp

/*
 * BankAccount example but with signals:
 *  - Add a 'balance' signal
 *  - Define a 'consolidated' function (check the worksheet)
 */
class BankAccount {
  val balance = Var(0)

  def deposit(amount: Int): Unit = {
    if (amount > 0) {
      val currentBalance = balance()
      balance() = currentBalance + amount
    }
  }

  def withdraw(amount: Int): Unit = {
    if (sufficientFundsForWithdrawing(amount)) {
      val currentBalance = balance()
      balance() = currentBalance - amount
    } else throw new Error("insufficient funds")
  }

  private def sufficientFundsForWithdrawing(amount: Int): Boolean = {
    0 < amount && amount <= balance()
  }

}
