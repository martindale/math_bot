package controllers

import java.net.URLDecoder

import actors.LevelGenerationActor
import actors.LevelGenerationActor.{GetLevel, GetStep}
import actors.messages.{ActorFailed, PreparedStepData, RawLevelData}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import javax.inject.Singleton
import loggers.MathBotLogger
import daos.PlayerTokenDAO
import play.api.Environment
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import types.{LevelName, TokenId}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class LevelController @Inject()(system: ActorSystem,
                                playerTokenDAO: PlayerTokenDAO,
                                logger: MathBotLogger,
                                environment: Environment)
    extends Controller {

  implicit val timeout: Timeout = 5000.minutes

  val levelActor = system.actorOf(LevelGenerationActor.props(playerTokenDAO, logger, environment), "level-actor")

  def getStep(level: LevelName, step: LevelName, encodedTokenId: Option[TokenId]) = Action.async { implicit request =>
    (levelActor ? GetStep(level, step, encodedTokenId.map(URLDecoder.decode(_, "UTF-8"))))
      .mapTo[Either[PreparedStepData, ActorFailed]]
      .map {
        case Left(preparedStepData) => Ok(Json.toJson(preparedStepData))
        case Right(invalidJson) => BadRequest(invalidJson.msg)
      }
  }

  def getLevel(level: LevelName, encodedTokenId: Option[TokenId]) = Action.async { implicit request =>
    (levelActor ? GetLevel(level, encodedTokenId.map(URLDecoder.decode(_, "UTF-8"))))
      .mapTo[Either[RawLevelData, ActorFailed]]
      .map {
        case Left(rawLevelData) => Ok(Json.toJson(rawLevelData))
        case Right(invalidJson) => BadRequest(invalidJson.msg)
      }
  }
}
