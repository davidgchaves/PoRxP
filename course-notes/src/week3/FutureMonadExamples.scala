/*
 * This is just a personal notes file to further digest week3 material
 *
 * If you want to compile, etc go to the original source
 * Socket.scala which has all dependencies and such:
 * https://github.com/headinthebox/CourseraCodeSamplesReactiveProgramming
 */

import scala.util.{Failure, Success}
import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global
import scala.collection.immutable.Queue
import akka.serialization._

object Socket {
  def apply(): Socket = new Socket(){}
}

trait Socket {

  val serialization: Serialization = ???

  val memory = Queue[EmailMessage](
    EmailMessage(from = "Erik", to = "Roland"),
    EmailMessage(from = "Martin", to = "Erik"),
    EmailMessage(from = "Roland", to = "Martin")
  )

  def readFromMemory(): Future[Array[Byte]] = {
    Future {
      val email = memory.dequeue
      val serializer = serialization.findSerializerFor(email)
      serializer.toBinary(email)
    }
  }

  /*
   * I - SENDING PACKAGES USING FUTURES
   */

  // 1a - Dealing with Latency Explicitly (pattern matching)
  def sendPacketToEuropeAndBackI(): Unit = {
    val socket = Socket()

    val packet: Future[Array[Byte]] = socket.readFromMemory()

    // We have a problem here:
    //  - We expect to return Unit, but
    //  - we are returning Future[Array[Byte]]
    // SOLUTION: 1b
    val confirmation: Unit /* Future[Array[Byte]] */ =
      packet onComplete {
        case Success(p) => socket.sendToEurope(p)
        case Failure(t) => ???
      }

  }

  // 1b - Dealing with Latency Explicitly (pattern matching with a tiny refactor)
  def sendPacketToEuropeAndBackII(): Unit = {
    val socket = Socket()

    val packet: Future[Array[Byte]] = socket.readFromMemory()

    packet onComplete {
      case Success(p) => {
        val confirmation: Future[Array[Byte]] = socket.sendToEurope(p)
        ???
      }
      case Failure(t) => ???
    }
  }

  // 2 - Reducing noise with Monads (flatMap)
  def sendPacketToEuropeAndBackIII(): Unit = {
    val socket = Socket()

    val packet: Future[Array[Byte]]       = socket.readFromMemory()

    val confirmation: Future[Array[Byte]] = packet.flatMap(socket.sendToEurope(_))
    //val confirmation: Future[Array[Byte]] = packet.flatMap(p => socket.sendToEurope(p))
  }

  // 3 - Reducing even more noise with Monads (for-comprehensions)
  def sendPacketToEuropeAndBackIV(): Unit = {
    val socket = Socket()
    val confirmation: Future[Array[Byte]] = for {
      packet       <- socket.readFromMemory()
      confirmation <- socket.sendToSafeII(packet)
    } yield confirmation
  }

  /*
   * II - SENDING PACKAGES RESILIENT TO FAILURE USING FUTURES
   */

  // Given an URL and a request, we get back a Future response
  //object Http {
  // def apply(url: String, request: Request): Future[Response] = ???
  //}

  // 1st attempt - This is not safe
  def sendToEurope(packet: Array[Byte]): Future[Array[Byte]] = {
    Http("mail.server.eu", Request(packet)).filter(_.isOK).map(_.body)
    //Http("mail.server.eu", Request(packet))
    //  .filter(response => response.isOK)
    //  .map(response => response.body)
  }

  // A more generic sendTo
  def sendTo(url: String, packet: Array[Byte]): Future[Array[Byte]] =
    Http(url, Request(packet)).filter(_.isOK).map(_.body)

  // 2nd attempt - Let's try to improve sendToEurope by send it TWICE
  //
  // PROBLEM: when zipping if something is the empty list, then the empty list is returned
  //          ie.: the zip will fail when Europe or USA fails
  def sendToAndBackUp(packet: Array[Byte]):Future[(Array[Byte], Array[Byte])] = {
    val europeConfirm = sendTo("...europe...", packet)
    val usaConfirm    = sendTo("...usa...", packet)
    europeConfirm.zip(usaConfirm)
  }

  // 3rd attempt - Improving thanks to recover and recoverWith
  //               (the map and the flatMap for the error case)
  //
  // We send it to Europe first,
  //  when that fails we use recoverWith to send it to the USA,
  //    when that fails we use recover to return the error.
  // On the other hand if we succeed, we are done.
  //
  // PROBLEM: We are trying to send the package to Europe,
  //          but when it fails we get the error for sending it to the USA
  def sendToSafeI(packet: Array[Byte]): Future[Array[Byte]] =
    sendTo("...europe...", packet) recoverWith {
      case europeError => sendTo("...usa...", packet) recover {
        case usaError  => usaError.getMessage.getBytes
      }
    }

  // 4th attempt - Returning the right error with fallbackTo
  //
  // We send it to Europe first:
  // If Europe succeeds ---> WE ARE DONE
  // If Europe fails, send it to the USA:
  //    If USA succeeds ---> WE ARE DONE
  //    If USA fails, get the Europe error
  def sendToSafeII(packet: Array[Byte]): Future[Array[Byte]] =
    sendTo("...europe...", packet) fallbackTo { sendTo("...usa...", packet) } recover {
      case europeError => europeError.getMessage.getBytes
    }

}

