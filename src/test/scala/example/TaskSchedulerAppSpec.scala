package example

import example.model._
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.collection.parallel.ParSeq

class TaskSchedulerAppSpec extends FunSpec with Matchers with TableDrivenPropertyChecks {

  val root1 = Task("root_1")
  val root2 = Task("root_2")

  val child1 = Task("child_1", Set(root1))
  val child2 = Task("child_2", Set(root2, child1))
  val child3 = Task("child_3", Set(root1, root2))

  val scenarios = Table(
    ("tasks", "sequencedTasks"),
    (Seq(child1), Seq(Seq(root1, child1))),
    (Seq(root1, root2), Seq(Seq(root1, root2))),
    (Seq(child2), Seq(Seq(root1, child1, root2, child2))),
    (Seq(root1, root2, child1, child2), Seq(Seq(root1), Seq(root2), Seq(root1, child1), Seq(root1, child1, root2, child2)))
  )

  forAll(scenarios) { (tasks, sequencedTasks) =>
    ignore(s"should return $sequencedTasks as the sequenced result of $tasks") {
      new TaskSchedulerApp().sequenceTaskGroups(tasks) should contain theSameElementsInOrderAs sequencedTasks
    }
  }

  forAll(scenarios) { (tasks, _) =>
    it(s"should execute $tasks in right sequence") {
      new TaskSchedulerApp().executeTasks(tasks) shouldBe ParSeq("")
    }
  }
}
