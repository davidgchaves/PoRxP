package week2.pubsub

trait Subscriber {
  def handler(pub: Publisher)
}

