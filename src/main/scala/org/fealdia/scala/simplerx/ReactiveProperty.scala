package org.fealdia.scala.simplerx

class ReactiveProperty[T](var value: T) {
  val es = new EventSource[(T, T)]

  def subscribe(callback: (T, T) => Unit): Subscription[(T, T)] = es.subscribe((value: (T, T)) => callback(value._1, value._2))
  def subscribe(callback: T => Unit): Subscription[T] = es.map(_._2).subscribe(callback)

  def := (newValue: T) = {
    if (value != newValue) {
      val oldValue = value
      value = newValue
      trigger(oldValue, newValue)
    }
  }

  def trigger(): Unit = trigger(value, value)

  def trigger(oldValue: T, newValue: T) = es.emit((oldValue, newValue))
}
