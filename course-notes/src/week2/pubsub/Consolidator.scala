package week2.pubsub

class Consolidator(observed: List[BankAccount]) extends Subscriber {
  private var total = 0

  def totalBalance = total

  // Initialize itself:
  //  - subscribing to the observed bank accounts
  //  - computing the total
  observed.foreach(_.subscribe(this))
  computeTotal()

  def handler(pub: Publisher) = computeTotal()

  private def computeTotal() =
    total = observed.map(_.currentBalance).sum
}

