package org.fealdia.scala.simplerx

import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ChainedSubscriptionTest extends FunSuite with BeforeAndAfter {
  class Duck {
    val quacks = new EventSource[String]
  }
  var ducks: EventSource[Duck] = _

  before {
    ducks = new EventSource[Duck]()
  }

  test("chained subscription manually") {
    var quacksSubscription: Option[Subscription[_]] = None
    var gotQuack = false

    ducks.subscribe(duck => {
      quacksSubscription.foreach(_.unsubscribe())
      quacksSubscription = Some(duck.quacks.subscribe(quack => gotQuack = true))
    })

    val duck = new Duck()
    ducks.emit(duck)
    duck.quacks.emit("quack")
    assert(gotQuack)
  }

  test("chained subscription") {
    var quacks = 0
    assert(ducks.callbacks.length == 0)
    val subscription = ducks.chainedSubscription(duck => duck.quacks.subscribe(quack => quacks += 1))
    assert(ducks.callbacks.length == 1)

    val duck = new Duck()
    assert(duck.quacks.callbacks.length == 0)
    ducks.emit(duck)
    assert(duck.quacks.callbacks.length == 1)
    duck.quacks.emit("quack")

    assert(quacks == 1)

    val duck2 = new Duck()
    ducks.emit(duck2)
    assert(duck.quacks.callbacks.length == 0)
    assert(duck2.quacks.callbacks.length == 1)
    duck2.quacks.emit("quack2")

    assert(quacks == 2)

    subscription.unsubscribe()
    assert(ducks.callbacks.length == 0)
    assert(duck2.quacks.callbacks.length == 0)
  }
}
