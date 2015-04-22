package week2.pubsub

trait Publisher {
  private var subscribers: Set[Subscriber] = Set()

  def subscribe(sub: Subscriber): Unit =
    subscribers += sub

  def unsubscribe(sub: Subscriber): Unit =
    subscribers -= sub

  def publish(): Unit =
    subscribers.foreach(_.handler(this))
}

