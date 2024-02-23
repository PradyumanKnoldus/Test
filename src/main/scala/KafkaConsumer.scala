import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

object KafkaConsumer extends App {
  // Create SparkSession
  val spark = SparkSession.builder()
    .appName("KafkaConsumerToDataFrame")
    .master("local[*]")
    .getOrCreate()

  // Define the schema for your Kafka topic data
  private val schema = StructType(Seq(
    StructField("first_name", StringType, nullable = true),
    StructField("last_name", StringType, nullable = true),
    StructField("location", StringType, nullable = true),
    StructField("online", BooleanType, nullable = true),
    StructField("followers", IntegerType, nullable = true)
  ))

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

  // Convert Kafka message value to JSON string and then to DataFrame
  private val parsedDF = kafkaDF
    .selectExpr("CAST(value AS STRING)")
    .select(from_json(col("value"), schema).as("data"))
    .select("data.*")

  // Write the data to a Databricks Delta table
  val deltaTablePath = "SampleData"
  val checkpointPath = "Checkpointing"

  val query = parsedDF.writeStream
    .outputMode("append")
    .format("parquet")
    .option("checkpointLocation", checkpointPath)
    .start(deltaTablePath)

  query.awaitTermination()
}

