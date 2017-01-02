
/**
  * Helper implicit methods for managing the Map[Int, Map[A, Int]] which records results,
  * along with their frequency
  *
  * (this may be a bit overkill, but I had fun with it)
  */
object CounterMapHelper {
  implicit class CounterMap[A](map: Map[A, Int]) {

    //return a copy of the map with the provided key A, and incremented counter value by c
    def updateCounterBy(k: A, c: Int): Map[A, Int] =
      map + (k → (map.getOrElse(k, 0) + c))

    //return a copy of the map with all the provided keys added, and counter values incremented by c
    def updateCountersBy(keys: TraversableOnce[A], c: Int): Map[A, Int] =
      (map /: keys)((m, k) ⇒ m.updateCounterBy(k, c))

    def updateCounterBy1(k: A): Map[A, Int] =
      updateCounterBy(k, 1)

    def updateCountersBy1(keys: TraversableOnce[A]): Map[A, Int] =
      (map /: keys)((m, k) ⇒ m.updateCounterBy1(k))

  }

  implicit class AllCounterMaps[A](map: Map[Int, Map[A, Int]]) {

    //combine the group of maps into 1 countermap with counts added for collissions
    def aggregateCounterMaps: Map[A, Int] =
      (Map.empty[A, Int] /: map.values.flatten)((a, e) ⇒ a.updateCounterBy(e._1, e._2))

  }
}
