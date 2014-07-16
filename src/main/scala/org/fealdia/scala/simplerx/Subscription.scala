package org.fealdia.scala.simplerx

class Subscription[A](eventSource: EventSource[A], cb: A => Unit) {
  def unsubscribe() {
    eventSource.callbacks -= cb
  }
}
