package quickcheck

import common._

import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  // Write a generator of heaps, of abstract type H,
  // so that you can write properties on any random heap, generated by your procedure
  lazy val genHeap: Gen[H] = for {
    elem <- arbitrary[A]
    heap <- oneOf(const(empty), genHeap)
  } yield insert(elem, heap)

  // Generates an arbitrary heap using the above generator of heaps
  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  // As an example of what you should do, here is a property that ensures that:
  //  1 - if you insert an element into an empty heap,
  //      val h = insert(a, empty)
  //  2 - then find the minimum of the resulting heap,
  //      findMin(h)
  //  3 - you get the element back
  //      findMin(h) == a
  property("minimum of 1 inserted element") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  // Here are some possible properties we suggest you write.
  //
  //  1: If you insert any two elements into an empty heap,
  //     finding the minimum of the resulting heap
  //     should get the smallest of the two elements back.
  property("mimimum of 2 inserted elements") = forAll { (a: Int, b: Int) =>
    val h1 = insert(a, empty)
    val h2 = insert(b, h1)
    findMin(h2) == Math.min(a,b)
  }

  //  2: If you insert an element into an empty heap,
  //     then delete the minimum,
  //     the resulting heap should be empty.
  property("insert and delete minimum") = forAll { a: Int =>
    val h1 = insert(a, empty)
    deleteMin(h1) == empty
  }

  //  3: Given any heap, you should get a sorted sequence of elements
  //     when continually finding and deleting minima.
  //     Hint: recursion and helper functions are your friends.
  property("sequence of deleted elements") = forAll { h: H =>
    val deletedElems = seqOfDeleteMinElems(h, Nil)
    deletedElems == deletedElems.sorted
  }

  def seqOfDeleteMinElems(heap: H, delElemsAcc: List[A]): List[A] = isEmpty(heap) match {
    case true  => delElemsAcc
    case false => findMin(heap) :: seqOfDeleteMinElems(deleteMin(heap), delElemsAcc)
  }

  //  4: Finding a minimum of the melding of any two heaps
  //     should return a minimum of one or the other.

}
