package org.fealdia.scala.simplerx

import org.junit.runner.RunWith
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class ReactivePropertyTest extends FunSuite with BeforeAndAfter {
  var prop: ReactiveProperty[Int] = _

  before {
    prop = new ReactiveProperty[Int](3)
  }

  test("ReactiveProperty subscription is called with right values") {
    assert(prop.value == 3)

    var wasCalled = false
    prop.subscribe((oldValue, newValue) => {
      assert(oldValue == 3)
      assert(newValue == 7)
      wasCalled = true
    })
    prop := 7

    assert(prop.value == 7)
    assert(wasCalled)
  }

  test("ReactiveProperty unsubscribe removes callback") {
    assert(prop.es.callbacks.length == 0)

    val subscription = prop.subscribe(v => {})
    assert(prop.es.callbacks.length == 1)

    subscription.unsubscribe()
    assert(prop.es.callbacks.length == 0)
  }
}
