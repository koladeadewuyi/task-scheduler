package example

import java.util.concurrent.ConcurrentHashMap

import example.model._
import org.apache.logging.log4j.scala.Logging

import scala.annotation.tailrec
import scala.collection.parallel.ParSeq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

class TaskSchedulerApp extends Logging {

  private val WaitDuration = 2000
  private val executedTasks = new ConcurrentHashMap[Task, String]()
  private val EmptyTaskMessage = "All child task(s) in group executed"

  def executeTasks(tasks: Seq[Task]): ParSeq[String] = {
    val (rootTasks, childTasks) = tasks.partition(_.parents.isEmpty)
    val sequencedTaskGroups = sequenceTaskGroups(childTasks)

    executeRootTasks(rootTasks)
    sequencedTaskGroups.par.map(executeSequencedTasks)
  }

  def sequenceTaskGroups(tasks: Seq[Task]): Seq[Seq[Task]] = {
    tasks.map(childTask => getTaskSequence(Seq(childTask))).distinct
  }

  private def executeRootTasks(rootTasks: Seq[Task]): ParSeq[String] = rootTasks.par.map { rootTask =>
    logger.info("Executing root tasks")
    val future = Future {
      logger.info(s"Executing ${rootTask.name}")
      Thread.sleep(WaitDuration)
      val message = s"${rootTask.name} Done!"
      logger.info(message)
      message
    }

    val result = Await.result(future, WaitDuration * 2 millis)
    executedTasks.put(rootTask, result)
    result
  }

  @tailrec
  private def getTaskSequence(tasks: Seq[Task], taskSequence: Seq[Task] = Seq.empty[Task]): Seq[Task] = {
    tasks.headOption match {
      case Some(task) if task.parents.isEmpty =>
        getTaskSequence(tasks.tail, task +: taskSequence)
      case Some(task) =>
        getTaskSequence(task.parents.toSeq, task +: taskSequence)
      case None => taskSequence
    }
  }

  @tailrec
  private def executeSequencedTasks(tasks: Seq[Task]): String = {
    tasks.headOption match {
      case Some(task) =>
        Option(executedTasks.get(task)) match {
          case Some(executionResult) =>
            logger.info(s"Already executed ${task.name} with outcome $executionResult!")
            executeSequencedTasks(tasks.tail)
          case None =>
            if (task.parents.isEmpty) executeTask(task)
            else executeTask(task)
            executeSequencedTasks(tasks)
        }
      case None =>
        logger.info(EmptyTaskMessage)
        EmptyTaskMessage
    }
  }

  private def executeTask(childTask: Task): String = {
    val future = Future {
      logger.info(s"Executing ${childTask.name}")
      Thread.sleep(WaitDuration)
      val message = s"${childTask.name} Done!"
      logger.info(message)
      message
    }

    val result = Await.result(future, WaitDuration * 2 millis)
    executedTasks.put(childTask, result)
    result
  }

}