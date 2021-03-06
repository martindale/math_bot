package actors

import actors.messages.{AssignedFunction, RawLevelData, RawStepData}
import play.api.Environment
import play.api.libs.json.Json
import types.LevelName

import scala.io.Source

class LevelGenerator(environment: Environment) {

  private val dirName = "assets"

  def getListOfLevels: List[String] = {
    environment.getExistingFile(dirName).map { dir =>
      dir.listFiles.filter(_.isFile).map(_.getAbsolutePath).toList
    } orElse {
      environment.getExistingFile(s"conf/$dirName").map { dir =>
        dir.listFiles.filter(_.isFile).map(_.getAbsolutePath).toList
      }
    }
  }.getOrElse(List.empty[String])

  def getAllLevels: Map[String, RawLevelData] = {
    val levels = getListOfLevels
      .map { level =>
        getJsonFromFile(level) match {
          case Some(rawLevelData) =>
            if (rawLevelData.show) {
              (rawLevelData.level, Some(rawLevelData))
            } else ("nothing", None)
          case None => ("nothing", None)
        }
      }
      .toMap
      .filterNot(rld => rld._2.isEmpty)
      .map(rld => (rld._1, rld._2.get))

    levels.map { rld =>
      levels.get(rld._2.nextLevel) match {
        case Some(_) => rld
        case None => rld._1 -> rld._2.copy(nextLevel = "None")
      }
    }
  }

  def getJsonFromFile(filePath: String): Option[RawLevelData] =
    Json.parse(Source.fromFile(filePath).getLines().mkString("")).validate[RawLevelData].asOpt

  def getRawStepData(level: LevelName, step: LevelName): Option[RawStepData] =
    getAllLevels.get(level) match {
      case Some(levelData) => levelData.steps.get(step)
      case None => None
    }

  def getAssignedFunctions: List[AssignedFunction] = {
    getAllLevels.values
      .flatMap(_.steps.values)
      .flatMap(rsd => rsd.assignedStaged ++ rsd.preBuiltActive)
      .toList
  }
}
