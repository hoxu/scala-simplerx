# Introduction

scala-simplerx is a simple reactive library for scala I have used in my
personal projects.

It provides two classes: ReactiveProperty and EventSource.

# Usage

## EventSource

**EventSource** does not hold an actual value, but merely provides a stream of
values that can be subscribed to.

    case class Position(x: Int, y: Int)
    val positionChanges = new EventSource[Position]()
    positionChanges.subscribe(p => println(p))
    positionChanges.emit(Position(1, 3))

## ReactiveProperty

**ReactiveProperty** is a class holding an actual property that can be read at any
time - on top of allowing subscriptions that get notified of value changes.

    val prop = new ReactiveProperty[Int](3)
    println(prop.value) // prints 3
    prop.subscribe(v => println(v))
    prop := 7 // prints 7

You can also use the overloaded subscribe method to get old value as well:

    prop.subscribe((oldValue, newValue) => println(s"$oldValue -> $newValue"))

Internally ReactiveProperty actually uses EventSource.

## filter & map

EventSource supports both filter and map.

## Unsubscribing

All subscribe methods return a Subscription that has only one method -
unsubscribe. It should be changed when the subscriber is no longer interested
in following the ReactiveProperty/EventSource.

# License

[MIT](http://opensource.org/licenses/MIT)

