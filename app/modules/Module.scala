package modules
import actors.{ ActorTags, GithubOAuth, GoogleOAuth }
import akka.actor.ActorSystem
import com.google.inject.{ AbstractModule, Provides }
import configuration._
import loggers.{ AkkaSemanticLog, SemanticLog }
import models.JwtToken
import org.bson.codecs.Codec
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.bson.codecs.Macros
import org.mongodb.scala.{ MongoClient, MongoDatabase }
import play.api.libs.concurrent.AkkaGuiceSupport
import utils.SecureIdentifier
import utils.SecureIdentifier.SecureIdentifierCodec

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bindActor[GoogleOAuth](ActorTags.googleOAuth)
    bindActor[GithubOAuth](ActorTags.githubOAuth)

  }

  @Provides
  def provideMongoDatabase(configFactory: ConfigFactory): MongoDatabase = {
    val name = configFactory.mongoConfig.name
    val url = configFactory.mongoConfig.url
    // To directly connect to the default server localhost on port 27017
    val mongoClient: MongoClient = MongoClient(url)
    mongoClient.getDatabase(name)
  }

  @Provides
  def provideGoogleApiConfig(configFactory: ConfigFactory) : GoogleApiConfig = {
    configFactory.googleApiConfig()
  }

  @Provides
  def provideGithubApiConfig(configFactory: ConfigFactory) : GithubApiConfig = {
    configFactory.githubApiConfig()
  }

  @Provides
  def provideActorConfig(configFactory: ConfigFactory) : ActorConfig = {
    configFactory.actorConfig()
  }

  @Provides
  def provideSemanticLog(system : ActorSystem) : SemanticLog = new AkkaSemanticLog[String](system, "global")

  @Provides
  def mongoCodecs(secureIdentifierCodec: SecureIdentifierCodec) : Seq[Codec[_]] = Seq(secureIdentifierCodec)

  @Provides
  def mongoCodecProviders : Seq[CodecProvider] = Seq(Macros.createCodecProvider[JwtToken])

  @Provides
  def provideLocalAuthConfig(configFactory: ConfigFactory) : LocalAuthConfig =
    configFactory.localAuthConfig
}
