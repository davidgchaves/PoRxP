/*
 * This is just a personal notes file to further digest week3 material
 *
 * If you want to compile, etc go to the original source
 * Combinators.scala which has all dependencies and such:
 * https://github.com/headinthebox/CourseraCodeSamplesReactiveProgramming
 */

import scala.concurrent.{Promise, ExecutionContext, Future}
import scala.async.Async._
import scala.util.{Success, Failure, Try}

package object combinators {

  /*
   * retry: retry successfully completing 'block' at most 'n' times
   */

  // 1st attempt - Using explicit recursion
  def retryI[T](n: Int)(block: =>Future[T]): Future[T] = {
    if (n == 0) { Future.failed(new Exception("Sorry")) }
    else { block fallbackTo { retryI(n-1) { block } } }
  }

  // 2nd attempt - Using foldLeft
  def retryII[T](n: Int)(block: =>Future[T]): Future[T] = {
    // In order to use fold we need a List -----> enter ns
    val ns: Iterator[Int]                 = (1 to n).iterator
    // Map the block over the List
    // NOTE: We wrap the block with ()=> because we don't want to execute the block just yet,
    // we want to execute them one by one, because each time we execute it might have side effects
    val attempts: Iterator[()=>Future[T]] = ns.map(_ => ()=>block)
    // The neutral element is failed (the first arg to fold)
    val failed: Future[T]                 = Future.failed(new Exception)

    // FOLD IT!!!
    attempts.foldLeft(failed)
                     ((a, block) => a fallbackTo { block() })
  }

  // 3rd attempt - Using foldRight
  //               Same as before but satisfying the foldRight types
  def retryIII[T](n: Int)(block: =>Future[T]): Future[T] = {
    val ns: Iterator[Int]                 = (1 to n).iterator
    val attempts: Iterator[()=>Future[T]] = ns.map(_ => ()=>block)
    val failed: Future[T]                 = Future.failed(new Exception)

    attempts.foldRight(()=>failed)
                      ((block, a) => ()=> { block() fallbackTo{ a() }}) ()
  }

  // 4rd attempt
  //  From the outside we still have Future[T]
  //  From the inside we code as we just have T
  //  REMEMBER: ASYNC AWAIT 'hides' Futures (or Monads in general)
  def retryIV[T](n: Int)(block: =>Future[T])(implicit executor: ExecutionContext): Future[T] = async {
    var i: Int         = 0
    var result: Try[T] = Failure(new Exception("Oops"))

    while (i < n) {
      result = await { block.withTry() }

      result match {
        case Success(s) => { i = i+1  }
        case Failure(f) => { i = n    }
      }
    }

    result.get
  }

  // Filter reimplemented with ASYNC AWAIT
  def filterI[T](future: Future[T], predicate: T => Boolean)(implicit executor: ExecutionContext): Future[T] = async {
    val x: T = await { future }

    if(!predicate(x)) { throw new NoSuchElementException("No such element") }
    else { x }
  }

  // FlatMap reimplemented with ASYNC AWAIT
  def flatMap[T,S](future: Future[T], selector: T => Future[S])(implicit executor: ExecutionContext): Future[S] = async {
    val x: T = await { future }
    await { selector(x) }: S
  }

  // Filter reimplemented with Promises
  def filterII[T](future: Future[T], predicate: T => Boolean)(implicit executor: ExecutionContext): Future[T] = {
    val p = Promise[T]()

    future.onComplete {
      case Success(s) => {
        if(!predicate(s)) { p.failure(new NoSuchElementException("No such element")) }
        else { p.success(s) }
      }
      case Failure(f) => { p.failure(f) }
    }

    p.future
  }

  // A Promise example
  def race[T](left: Future[T], right: Future[T])(implicit executor: ExecutionContext): Future[T] = {
    val p = Promise[T]()

    left  onComplete { p.tryComplete }
    right onComplete { p.tryComplete }

    p.future
  }

  def zipI[T, S, R](future: Future[T], other: Future[S], combine: (T, S) => R)
                   (implicit executor: ExecutionContext): Future[R] = async {
    combine(await { future }: T, await { other }: S)
  }

  def zipII[T, S, R](future: Future[T], other: Future[S], combine: (T, S) => R)
                    (implicit executor: ExecutionContext): Future[R] = {
    val p = Promise[R]()

    future onComplete {
      case Failure(f) => { p.failure(f) }
      case Success(t) => { other onComplete {
        case Failure(f) => { p.failure(f) }
        case Success(s) => p.success(combine(t,s))
      }}
    }

    p.future
  }

  def never[T]()(implicit executor: ExecutionContext): Future[T] = { Promise[T].future }
}
