package org.fealdia.scala.simplerx

import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EventSourceTest extends FunSuite with BeforeAndAfter {
  var source: EventSource[(Int, Int)] = _

  before {
    source = new EventSource[(Int, Int)]()
  }

  test("emit notifies subscribers") {
    var notified = false
    source.subscribe(value => {
      notified = true
    })
    source.emit((1, 2))
    assert(notified)
  }

  test("emit no longer notifies unsubscribed") {
    var notified = false
    val subscription = source.subscribe(value => {
      notified = true
    })
    subscription.unsubscribe()
    source.emit((1, 2))
    assert(!notified)
  }

  test("filtered EventSource contains only matching events") {
    val biggerBs = source.filter(p => p._2 > p._1)

    biggerBs.subscribe({ case (a, b) =>
        assert(b > a)
    })
    source.emit((1, 2))
    source.emit((2, 2))
    source.emit((2, 1))
  }

  test("EventSource map works") {
    val differences: EventSource[Int] = source.map({ case (a, b) => a - b })

    differences.subscribe(value => assert(value == 5))

    source.emit((10, 5))
    source.emit((5, 0))
  }
}
