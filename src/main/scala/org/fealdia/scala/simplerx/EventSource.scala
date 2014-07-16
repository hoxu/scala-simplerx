package org.fealdia.scala.simplerx

import scala.collection.mutable.ListBuffer


class EventSource[A] { self =>
  val callbacks: ListBuffer[A => Unit] = ListBuffer.empty
  def subscribe(callback: A => Unit): Subscription[A] = {
    callbacks += callback
    new Subscription[A](this, callback)
  }
  def emit(a: A) = for (cb <- callbacks) cb(a)

  def filter(p: A => Boolean): EventSource[A] = {
    new EventSource[A] {
      override def subscribe(filteredCallback: (A) => Unit) = {
        self.subscribe(a => if (p(a)) filteredCallback(a))
      }
    }
  }

  def map[B](f: A => B): EventSource[B] = {
    new EventSource[B] {
      override def subscribe(mappedCallback: (B) => Unit) = {
        val actualSubscription = self.subscribe(a => mappedCallback(f(a)))
        new Subscription[B](this, mappedCallback) {
          override def unsubscribe() = {
            actualSubscription.unsubscribe()
          }
        }
      }
    }
  }
}
