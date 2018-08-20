package example

import example.model._
import org.scalatest._
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.collection.parallel.immutable.ParVector

class TaskSchedulerAppSpec extends FunSpec with Matchers with TableDrivenPropertyChecks {

  private val root1 = Task("root_1")
  private val root2 = Task("root_2")

  private val child1 = Task("child_1", Set(root1))
  private val child2 = Task("child_2", Set(root2, child1))
  private val child3 = Task("child_3", Set(root1, root2))

  private val ChildExecutionMessage = s"All child task(s) in group %d executed"

  val scenarios = Table(
    ("tasks", "sequencedTasks"),
    (Seq(child1), Seq(Seq(root1, child1))),
    (Seq(root1, root2), Seq(Seq(root1), Seq(root2))),
    (Seq(child2), Seq(Seq(root1, child1, root2, child2))),
    (Seq(child3), Seq(Seq(root2, root1, child3))),
    (Seq(root1, root2, child1, child2), Seq(Seq(root1), Seq(root2), Seq(root1, child1), Seq(root1, child1, root2, child2)))
  )

  forAll(scenarios) { (tasks, sequencedTasks) =>
    it(s"should return $sequencedTasks as the sequenced result of $tasks") {
      new TaskSchedulerApp().sequenceTaskGroups(tasks) should contain theSameElementsInOrderAs sequencedTasks
    }
  }

  val scenarios1 = Table(
    ("tasks", "executionMessage"),
    (Seq(child1), ParVector(ChildExecutionMessage.format(0))),
    (Seq(root1, root2), ParVector.empty),
    (Seq(child2), ParVector(ChildExecutionMessage.format(0))),
    (Seq(child3), ParVector(ChildExecutionMessage.format(0))),
    (Seq(root1, root2, child1, child2), ParVector(ChildExecutionMessage.format(0), ChildExecutionMessage.format(1)))
  )

  forAll(scenarios1) { (tasks, result) =>
    it(s"should execute $tasks in right sequence") {
      new TaskSchedulerApp().executeTasks(tasks) shouldBe result
    }
  }
}
