package org.fealdia.scala.simplerx

import scala.collection.mutable.ListBuffer


class EventSource[A] { self =>
  val callbacks: ListBuffer[A => Unit] = ListBuffer.empty
  def subscribe(callback: A => Unit): Subscription[A] = {
    callbacks += callback
    new Subscription[A](this, callback)
  }
  def emit(a: A) = for (cb <- callbacks) cb(a)

  /**
   * target.chainedSubscription(target => target.health.subscribe(...)))
   */
  def chainedSubscription(chainedCallback: A => Subscription[_]): Subscription[A] = {
    // TODO ReactiveProperty would have initial value that we could call original callback with
    var chainedSubscription: Option[Subscription[_]] = None

    // Update Subscription every time called
    def callback(a: A): Unit = {
      chainedSubscription.foreach(_.unsubscribe())
      chainedSubscription = Some(chainedCallback(a))
    }
    val outerSubscription = subscribe(callback)

    // Return Subscription that unsubscribes from this and the chained Subscription
    new Subscription[A](this, callback) {
      override def unsubscribe() = {
        outerSubscription.unsubscribe()
        chainedSubscription.foreach(_.unsubscribe())
      }
    }
  }

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
