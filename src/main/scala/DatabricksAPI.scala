import org.apache.spark.sql.{DataFrame, SparkSession}
import scalaj.http.{Http, HttpResponse}

object DatabricksAPI extends App {
  // Create SparkSession
  val spark = SparkSession.builder()
    .appName("KafkaConsumerToDataFrame")
    .master("local[*]")
    .getOrCreate()

  // Subscribe to your Kafka topic
  val topic = "test_topic"
  val kafkaDF = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")  // Specify Kafka broker address
    .option("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")  // Specify key deserializer
    .option("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")  // Specify value deserializer
    .option("group.id", "dataframe-consumer")  // Specify consumer group ID
    .option("auto.offset.reset", "earliest")  // Specify offset reset
    .option("enable.auto.commit", "false")  // Disable auto commit
    .option("subscribe", topic)  // Subscribe to the Kafka topic
    .load()

  val jsonData: String =
    """
      |{
      |  "name": "John Doe",
      |  "age": 30,
      |  "city": "New York"
      |}
      |""".stripMargin

  // Databricks REST API settings
  private val databricksUrl = "https://adb-2373921131899949.9.azuredatabricks.net/"
  private val databricksToken = "dapi5eeedde9b33737656b1d82bca88add46-2"
  private val dbfsEndpoint = "dbfs:/user/hive/warehouse/Sample"

  // Send data to Databricks
  private val response: HttpResponse[String] = Http(databricksUrl + "dbfs/JsonString2")
    .postData(jsonData)
    .header("Content-Type", "application/json")
    .header("Authorization", s"Bearer $databricksToken")
    .asString

  println(response.body)
}
