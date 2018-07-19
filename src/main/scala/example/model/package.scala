package example

package object model {

  case class Task(name: String, parents: Set[Task] = Set.empty[Task])

}
