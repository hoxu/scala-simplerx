package org.fealdia.scala.simplerx

class ReactiveProperty[T](var value: T) {
  val es = new EventSource[(T, T)]

  def subscribe(callback: (T, T) => Unit): Subscription[(T, T)] = es.subscribe((value: (T, T)) => callback(value._1, value._2))
  def subscribe(callback: T => Unit): Subscription[T] = es.map(_._2).subscribe(callback)

  def chainedSubscription(chainedCallback: T => Subscription[_]): Subscription[T] = es.map(_._2).chainedSubscription(chainedCallback, initial = Some(value))

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
