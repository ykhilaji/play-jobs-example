package services.actors
import akka.actor._
import akka.cluster.sharding._


trait  AkkaClusterSharding {

  def shardName: String

  def props: Props

  def startRegion(actorSystem: ActorSystem): ActorRef = {
    //Based on Akka Docs, the number of shards should be a factor
    //ten greater than the planned maximum number of cluster nodes.
    val numberOfShards = 100 * 12

    ClusterSharding(actorSystem).start(
      typeName = shardName,
      entityProps = props,
      settings = ClusterShardingSettings(actorSystem),
      extractEntityId = extractEntityId,
      extractShardId = extractShardId(numberOfShards)
    )
  }

  def getRegion(system: ActorSystem): ActorRef = {
    ClusterSharding(system).shardRegion(shardName)
  }

  def extractEntityId: ShardRegion.ExtractEntityId = {
    case env@Envelope(id, _) => (id.toString, env)
  }

  def extractShardId(numberOfShards: Int): ShardRegion.ExtractShardId = {
    case Envelope(id, _) => (math.abs(id.hashCode) % numberOfShards).toString
  }
  
}
